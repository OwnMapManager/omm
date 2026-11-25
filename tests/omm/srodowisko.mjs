// Wspólne narzędzia testów OMM (pkt 457): budowa strony testowej z app/omm.html, przeglądarka, porównanie zrzutów.
import fs from 'node:fs';
import os from 'node:os';
import path from 'node:path';
import {fileURLToPath} from 'node:url';

export const ROOT = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '../..');
export const AKTUALIZUJ = process.argv.includes('--aktualizuj');

// Strona testowa: biblioteki z node_modules zamiast CDN, bez CSP (plik lokalny). Mapa nie pobiera kafli (blokada sieci
// w run.mjs) – zrzuty ekranu są powtarzalne.
export function zbudujStrone() {
  let h = fs.readFileSync(path.join(ROOT, 'app/omm.html'), 'utf8');
  const nm = 'file://' + path.join(ROOT, 'node_modules');
  h = h.replace(/<link rel="stylesheet" href="https:\/\/cdnjs[^>]*>/, `<link rel="stylesheet" href="${nm}/leaflet/dist/leaflet.css">`);
  h = h.replace(/<script src="https:\/\/cdnjs[^>]*><\/script>/, `<script src="${nm}/leaflet/dist/leaflet.js"></script>`);
  h = h.replace(/<script src="https:\/\/cdn.jsdelivr[^>]*><\/script>/, `<script src="${nm}/leaflet-rotate/dist/leaflet-rotate.js"></script>`);
  h = h.replace(/<meta http-equiv="Content-Security-Policy"[^>]*>/, '');
  const dir = fs.mkdtempSync(path.join(os.tmpdir(), 'omm-testy-'));
  const f = path.join(dir, 'omm.html');
  fs.writeFileSync(f, h);
  return 'file://' + f;
}

export class Blad extends Error {}
export function sprawdz(warunek, opis) {
  if (!warunek) throw new Blad(opis);
}

// Zrzut ekranu porównany z wzorcem w tests/omm/zrzuty/<nazwa>.png (wzorzec powstaje przy --aktualizuj).
// Różnica ponad 0,5% pikseli = błąd; obraz różnic zapisywany obok w katalogu wyników.
export async function zrzut(page, nazwa, wynikiDir) {
  const wz = path.join(ROOT, 'tests/omm/zrzuty', nazwa + '.png');
  const buf = await page.screenshot();
  if (AKTUALIZUJ || !fs.existsSync(wz)) {
    if (AKTUALIZUJ) fs.writeFileSync(wz, buf);
    return {nowy: !AKTUALIZUJ};
  }
  const roz = await page.evaluate(
    async ([a, b]) => {
      const img = s =>
        new Promise((ok, zle) => {
          const i = new Image();
          i.onload = () => ok(i);
          i.onerror = zle;
          i.src = 'data:image/png;base64,' + s;
        });
      const [A, B] = await Promise.all([img(a), img(b)]);
      if (A.width !== B.width || A.height !== B.height) return {inny: 1, proc: 100};
      const c = document.createElement('canvas');
      c.width = A.width;
      c.height = A.height;
      const x = c.getContext('2d');
      x.drawImage(A, 0, 0);
      const da = x.getImageData(0, 0, c.width, c.height).data;
      x.drawImage(B, 0, 0);
      const db = x.getImageData(0, 0, c.width, c.height).data;
      let n = 0;
      for (let i = 0; i < da.length; i += 4) if (Math.abs(da[i] - db[i]) + Math.abs(da[i + 1] - db[i + 1]) + Math.abs(da[i + 2] - db[i + 2]) > 48) n++;
      return {proc: (100 * n) / (da.length / 4)};
    },
    [fs.readFileSync(wz).toString('base64'), buf.toString('base64')]
  );
  if (roz.proc > 0.5) {
    if (wynikiDir) fs.writeFileSync(path.join(wynikiDir, nazwa + '-teraz.png'), buf);
    throw new Blad(`zrzut „${nazwa}” różni się od zatwierdzonego wzorca (${roz.proc.toFixed(2)}% pikseli)`);
  }
  return {proc: roz.proc};
}
