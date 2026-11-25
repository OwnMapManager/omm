// Automatyczne zdjęcia z folderów (pkt 467): pytanie u dołu, import, tryb automatyczny z „Cofnij”, bez duplikatów,
// wstrzymanie w treningu. Wtyczka OmmPhotos jest zastąpiona atrapą (folder z 3 zdjęciami).
import {sprawdz} from '../srodowisko.mjs';

const czekaj = ms => new Promise(r => setTimeout(r, ms));
export const testy = {
  async 'nowe zdjęcia z folderu – pytanie, import, automat, bez dubli'({page, url}) {
    await page.addInitScript(() => {
      localStorage.setItem('omm-tut', 'karty');
      window.__files = [1, 2, 3].map(k => ({id: 'a' + k, name: 'IMG_' + k + '.jpg', t: Date.now() - 1000 + k, size: 1}));
      let u = null;
      const img = async () => {
        if (u) return u;
        const c = document.createElement('canvas');
        c.width = 40;
        c.height = 30;
        c.getContext('2d').fillRect(0, 0, 40, 30);
        return (u = URL.createObjectURL(await new Promise(r => c.toBlob(r, 'image/jpeg'))));
      };
      const OP = {
        scanFolder: async o => ({files: window.__files.filter(f => f.t >= o.since)}),
        readFiles: async o => ({files: await Promise.all(o.ids.map(async id => ({id, path: await img(), name: id + '.jpg', mime: 'image/jpeg', lat: 50.06 + +id.slice(1) * 0.001, lng: 19.93})))}),
        cleanup: async () => ({}),
        forgetFolder: async () => ({})
      };
      window.Capacitor = {isNativePlatform: () => true, getPlatform: () => 'android', convertFileSrc: x => x.replace('file://', ''), isPluginAvailable: n => n === 'OmmPhotos', registerPlugin: () => OP, Plugins: {}};
    });
    await page.goto(url);
    await czekaj(2500);
    const r = await page.evaluate(async () => {
      const o = {},
        w = ms => new Promise(r => setTimeout(r, ms)),
        foto = () => P.items.filter(i => i.i === 'o:foto').length;
      afSave([{id: 's1', uri: 'content://t', name: 'DCIM/Camera', map: P.id, lid: P.active, tryb: 'pytaj', bez: 'slad', strefy: true, od: Date.now() - 5000}]);
      afCheck();
      await w(800);
      o.pasek = !!document.querySelector('.afbar') && /Nowe zdjęcia: 3/.test(document.querySelector('.afbar').textContent);
      [...document.querySelectorAll('.afbar button')].find(b => b.textContent === 'Importuj').click();
      await w(1500);
      o.po = foto();
      document.querySelectorAll('.afbar,.toast').forEach(x => x.remove());
      afCheck();
      await w(600);
      o.drugi = !!document.querySelector('.afbar');
      const L = afList();
      L[0].tryb = 'auto';
      afSave(L);
      window.__files.push({id: 'a4', name: 'IMG_4.jpg', t: Date.now(), size: 1});
      document.body.classList.add('trnon');
      afCheck();
      await w(500);
      o.trening = foto();
      document.body.classList.remove('trnon');
      afCheck();
      await w(1500);
      o.auto = foto();
      const c = document.querySelector('.afbar') && [...document.querySelectorAll('.afbar button')].find(b => b.textContent === 'Cofnij');
      if (c) c.click();
      await w(400);
      o.cofnij = foto();
      return o;
    });
    sprawdz(r.pasek, 'brak paska z pytaniem o 3 nowe zdjęcia');
    sprawdz(r.po === 3, 'po „Importuj” na mapie ' + r.po + ' zdjęć zamiast 3');
    sprawdz(!r.drugi, 'te same zdjęcia proponowane drugi raz');
    sprawdz(r.trening === 3, 'import w czasie treningu');
    sprawdz(r.auto === 4, 'tryb automatyczny nie dodał nowego zdjęcia');
    sprawdz(r.cofnij === 3, '„Cofnij” nie usunął dodanego zdjęcia');
  }
};
