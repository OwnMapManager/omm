// Tłumaczenia EN: każdy nowy napis _t('…') musi mieć wpis w słowniku EN. Znane braki z dawnych wersji są na liście
// tests/omm/dane/en-braki.json – ta lista może tylko maleć.
import fs from 'node:fs';
import path from 'node:path';
import {ROOT, sprawdz, AKTUALIZUJ} from '../srodowisko.mjs';

export function brakiEN() {
  const s = fs.readFileSync(path.join(ROOT, 'app/omm.html'), 'utf8');
  const klucze = new Set([...s.matchAll(/^ {2}"((?:[^"\\]|\\.)*)": "/gm)].map(m => JSON.parse('"' + m[1] + '"')));
  const napisy = new Set(
    [...s.matchAll(/_t\('((?:[^'\\]|\\.)*)'\)/g)].map(m => {
      try {
        return JSON.parse('"' + m[1].replace(/\\'/g, "'").replace(/"/g, '\\"') + '"');
      } catch (e) {
        return m[1];
      }
    })
  );
  return [...napisy].filter(n => !klucze.has(n)).sort();
}
export const testy = {
  async 'nowe napisy mają tłumaczenie EN'() {
    const plik = path.join(ROOT, 'tests/omm/dane/en-braki.json');
    const teraz = brakiEN();
    if (AKTUALIZUJ) fs.writeFileSync(plik, JSON.stringify(teraz, null, 1) + '\n');
    const znane = new Set(fs.existsSync(plik) ? JSON.parse(fs.readFileSync(plik, 'utf8')) : []);
    const nowe = teraz.filter(n => !znane.has(n));
    sprawdz(!nowe.length, 'brak tłumaczenia EN dla ' + nowe.length + ' napisów:\n  ' + nowe.slice(0, 30).join('\n  '));
  }
};
