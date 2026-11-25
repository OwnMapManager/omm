// Testy OMM uruchamiane w CI przed każdym wydaniem (pkt 457). Nieudany test = brak wydania.
//   node tests/omm/run.mjs              – wszystkie testy
//   node tests/omm/run.mjs --aktualizuj – zapis nowych wzorców (zrzuty, wzorzec śladu) – tylko świadomie, po zgodzie Pawła
import fs from 'node:fs';
import path from 'node:path';
import {chromium} from 'playwright-core';
import {ROOT, zbudujStrone, Blad} from './srodowisko.mjs';

const WYNIKI = path.join(ROOT, 'tests/omm/wyniki');
fs.mkdirSync(WYNIKI, {recursive: true});
const filtr = process.argv.slice(2).find(a => !a.startsWith('--'));
const pliki = fs
  .readdirSync(path.join(ROOT, 'tests/omm/testy'))
  .filter(f => f.endsWith('.test.mjs') && (!filtr || f.includes(filtr)))
  .sort();

async function przegladarka() {
  const opcje = [{channel: 'chrome'}, process.env.CHROME_PATH && {executablePath: process.env.CHROME_PATH}, {}].filter(Boolean);
  let ost;
  for (const o of opcje) {
    try {
      return await chromium.launch(o);
    } catch (e) {
      ost = e;
    }
  }
  throw ost;
}

const url = zbudujStrone();
const browser = await przegladarka();
let zle = 0;
for (const f of pliki) {
  const mod = await import(path.join(ROOT, 'tests/omm/testy', f));
  for (const [nazwa, fn] of Object.entries(mod.testy || {})) {
    const ctx = await browser.newContext({viewport: {width: 393, height: 873}, locale: 'pl-PL', timezoneId: 'Europe/Warsaw', deviceScaleFactor: 1});
    // bez sieci: kafle, serwery tras i pogody – testy nie zależą od cudzych serwerów
    await ctx.route(/^https?:/, r => r.abort());
    const page = await ctx.newPage();
    const bledy = [];
    page.on('pageerror', e => bledy.push(String(e && e.stack ? e.stack : e).slice(0, 400)));
    const t0 = Date.now();
    try {
      await fn({page, url, bledy, wyniki: WYNIKI});
      if (bledy.length) throw new Blad('błędy JavaScript na stronie:\n  ' + bledy.join('\n  '));
      console.log(`✓ ${f} › ${nazwa} (${Date.now() - t0} ms)`);
    } catch (e) {
      zle++;
      console.log(`✗ ${f} › ${nazwa}\n  ${e instanceof Blad ? e.message : e.stack}`);
      try {
        await page.screenshot({path: path.join(WYNIKI, (f + '-' + nazwa).replace(/[^\w.-]+/g, '_') + '.png')});
      } catch (e2) {}
    }
    await ctx.close();
  }
}
await browser.close();
console.log(zle ? `\n${zle} test(ów) nie przeszło – wydanie wstrzymane.` : '\nWszystkie testy przeszły.');
process.exit(zle ? 1 : 0);
