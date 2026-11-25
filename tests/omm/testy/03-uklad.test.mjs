// Układ poleceń (krople): fabryczny układ i przejście starych układów na jedno polecenie „Moje” (pkt 462).
import {sprawdz} from '../srodowisko.mjs';

const czekaj = ms => new Promise(r => setTimeout(r, ms));
export const testy = {
  async 'fabryczny układ kropli'({page, url}) {
    await page.addInitScript(() => localStorage.setItem('omm-tut', 'karty'));
    await page.goto(url);
    await czekaj(2500);
    const r = await page.evaluate(() => {
      const L = sklGet();
      return {s: L.s.map(i => i.id), x: L.x};
    });
    for (const id of ['punkt', 'rysuj', 'zmierz', 'foto', 'moje', 'podklad', 'trening', 'szuk', 'poz']) sprawdz(r.s.includes(id), 'w fabrycznym układzie brak kropli „' + id + '”');
    sprawdz(!r.s.includes('mapy') && !r.s.includes('slady'), 'stare krople Mapy/Ślady w układzie');
  },
  async 'stary układ: Mapy i Ślady → Moje w miejscu Map, bez dziury'({page, url}) {
    await page.addInitScript(() => localStorage.setItem('omm-tut', 'karty'));
    await page.goto(url);
    await czekaj(2500);
    const r = await page.evaluate(() => {
      const a = sklAll(),
        g = sklGrid(),
        c = g.cols - 1;
      a[sklKey()] = {v: 2, s: [{id: 'mapy', c, r: 5}, {id: 'slady', c, r: 6}, {id: 'podklad', c, r: 7}], f: {}, x: ['gry']};
      localStorage.setItem('omm-uklad', JSON.stringify(a));
      const L = sklGet(),
        m = L.s.find(i => i.id === 'moje'),
        p = L.s.find(i => i.id === 'podklad');
      return {ile: L.s.filter(i => i.id === 'moje').length, x: L.x.includes('moje'), sas: m && p && m.c === p.c && p.r - m.r === 1};
    });
    sprawdz(r.ile === 1 && !r.x, '„Moje” nie jest dokładnie raz na ekranie');
    sprawdz(r.sas, 'Podkład nie przylega do „Moje” (dziura w kapsule)');
  },
  async 'menedżer Moje: zakładki Mapy | Ślady i ostatnia zakładka'({page, url}) {
    await page.addInitScript(() => localStorage.setItem('omm-tut', 'karty'));
    await page.goto(url);
    await czekaj(2500);
    const r = await page.evaluate(async () => {
      const o = {};
      menuOpen('moje');
      await new Promise(r => setTimeout(r, 200));
      o.t1 = TAB;
      o.zak = [...document.querySelectorAll('#ktabs button')].map(b => b.dataset.k + (b.classList.contains('on') ? '*' : '')).join(',');
      document.querySelector('#ktabs [data-k=slady]').click();
      await new Promise(r => setTimeout(r, 200));
      $('sheet').style.display = 'none';
      menuOpen('moje');
      await new Promise(r => setTimeout(r, 200));
      o.t2 = TAB;
      o.nowy = $('mnew') && $('mnew').offsetParent !== null;
      return o;
    });
    sprawdz(r.t1 === 'mapy' && r.zak === 'mapy*,slady', 'zakładki: ' + r.zak);
    sprawdz(r.t2 === 'slady', '„Moje” nie otwiera ostatniej zakładki');
    sprawdz(r.nowy, 'brak przycisku nowej kategorii / mapy w wierszu nazwy');
  }
};
