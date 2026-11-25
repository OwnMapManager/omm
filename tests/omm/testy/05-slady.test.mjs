// Liczenie śladów (trkProcess) na sztucznych pomiarach GPS z szumem: wynik nie może się zmienić bez podbicia TRK_ALG.
// Wzorzec: tests/omm/dane/slad-wzorzec.json (zapis przy --aktualizuj, razem z numerem TRK_ALG).
import fs from 'node:fs';
import path from 'node:path';
import {ROOT, sprawdz, AKTUALIZUJ} from '../srodowisko.mjs';

const czekaj = ms => new Promise(r => setTimeout(r, ms));
export const testy = {
  async 'ślad pieszy i rowerowy – zgodny ze wzorcem'({page, url}) {
    await page.addInitScript(() => localStorage.setItem('omm-tut', 'karty'));
    await page.goto(url);
    await czekaj(2500);
    const r = await page.evaluate(() => {
      // powtarzalny „losowy” szum (ten sam przy każdym uruchomieniu)
      let s = 12345;
      const rnd = () => ((s = (s * 1103515245 + 12345) % 2147483648) / 2147483648) * 2 - 1;
      const gen = (v, n, acc) => {
        const rows = [],
          la0 = 50.0597,
          lo0 = 19.9126;
        let x = 0,
          y = 0,
          hd = 0.3;
        for (let i = 0; i < n; i++) {
          hd += 0.02 * Math.sin(i / 40);
          x += v * Math.sin(hd);
          y += v * Math.cos(hd);
          const nx = x + rnd() * acc * 0.6,
            ny = y + rnd() * acc * 0.6;
          rows.push([i, la0 + ny / 111320, lo0 + nx / (111320 * Math.cos((la0 * Math.PI) / 180)), acc + Math.abs(rnd()) * 3, v + rnd() * 0.3, ((hd * 180) / Math.PI + 360) % 360, 210 + rnd()]);
        }
        return rows;
      };
      const len = pts => pts.reduce((a, p, i) => (i ? a + hav(pts[i - 1], p) : 0), 0);
      const w = trkProcess(gen(1.4, 900, 6), {act: 'pieszo'}),
        b = trkProcess(gen(5.5, 600, 5), {act: 'rower'});
      return {alg: TRK_ALG, pieszo: {n: w.length, m: Math.round(len(w))}, rower: {n: b.length, m: Math.round(len(b))}};
    });
    const plik = path.join(ROOT, 'tests/omm/dane/slad-wzorzec.json');
    if (AKTUALIZUJ || !fs.existsSync(plik)) return fs.writeFileSync(plik, JSON.stringify(r, null, 1) + '\n');
    const W = JSON.parse(fs.readFileSync(plik, 'utf8'));
    sprawdz(W.alg === r.alg, `zmieniono TRK_ALG (${W.alg} → ${r.alg}) – sprawdź ślady wzorcowe i zapisz nowy wzorzec (--aktualizuj)`);
    for (const k of ['pieszo', 'rower']) {
      const d = Math.abs(r[k].m - W[k].m) / W[k].m;
      sprawdz(d < 0.005, `ślad ${k}: długość ${r[k].m} m zamiast ${W[k].m} m – liczenie śladów zmieniło się bez podbicia TRK_ALG`);
    }
  }
};
