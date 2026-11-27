// Nawigacja: start, jazda po trasie, zjazd z trasy, koniec – bez błędów; ekran nawigacji ma ■/Nagraj i wyciszenie.
import {sprawdz} from '../srodowisko.mjs';

const czekaj = ms => new Promise(r => setTimeout(r, ms));
export const testy = {
  async 'prowadzenie po trasie i zjazd z trasy'({page, url}) {
    await page.addInitScript(() => {
      localStorage.setItem('omm-tut', 'karty');
      const n = Date.now.bind(Date);
      window.__off = 0;
      Date.now = () => n() + window.__off;
    });
    await page.goto(url);
    await czekaj(2500);
    const r = await page.evaluate(async () => {
      const o = {};
      const P = [[50.06, 19.93], [50.06, 19.94], [50.0645, 19.94]];
      navStart(P[2], 'Cel', P, {prof: 'auto'});
      for (let k = 0; k < 5; k++) (window.__off += 1000), navPos({latitude: 50.06, longitude: 19.93 + k * 0.0003, accuracy: 4, speed: 14, heading: 90});
      o.on = document.body.classList.contains('navon');
      for (let k = 0; k < 14; k++) (window.__off += 1000), navPos({latitude: 50.058, longitude: 19.932 + k * 0.0002, accuracy: 4, speed: 14, heading: 90});
      await new Promise(r => setTimeout(r, 1500));
      navStop(true);
      o.off = !NAV && !document.body.classList.contains('navon');
      return o;
    });
    sprawdz(r.on, 'nawigacja się nie włączyła');
    sprawdz(r.off, 'nawigacja się nie wyłączyła');
  },
  // v140 (pkt 474): kamera jedzie za Tobą – przybliżenie po starcie, a po przesunięciu mapy palcem wraca sama po ok. 5 s
  async 'kamera nawigacji podąża i wraca po przesunięciu'({page, url}) {
    await page.context().grantPermissions(['geolocation']);
    await page.context().setGeolocation({latitude: 51.76, longitude: 19.45, accuracy: 4});
    await page.addInitScript(() => localStorage.setItem('omm-tut', 'karty'));
    await page.goto(url);
    await czekaj(2500);
    const r = await page.evaluate(async () => {
      const sleep = ms => new Promise(ok => setTimeout(ok, ms)),
        P = [];
      for (let k = 0; k <= 40; k++) P.push([51.76 + k * 0.0006, 19.45]);
      navStart(P[40], 'Cel', P, {prof: 'auto'});
      const jedz = async (a, b) => {
        for (let k = a; k < b; k++) navPos({latitude: P[k][0], longitude: P[k][1], accuracy: 4, speed: 12, heading: 0}), await sleep(1000);
      };
      const stan = () => {
        const c = map.getCenter(),
          pt = map.latLngToContainerPoint(NAV.pos),
          s = map.getSize();
        return {d: Math.round(hav([c.lat, c.lng], NAV.pos)), z: map.getZoom(), w: pt.x > 0 && pt.y > 0 && pt.x < s.x && pt.y < s.y};
      };
      await jedz(0, 9);
      const a = stan(),
        kompas = getComputedStyle($('compb')).display !== 'none';
      map.fire('dragstart');
      map.panBy([250, -350], {animate: false});
      const b = stan();
      await jedz(9, 17);
      const c = stan();
      navStop(true);
      return {a, b, c, kompas, polnoc: JSON.stringify(sklGet()).includes('polnoc')};
    });
    sprawdz(r.a.w && r.a.d < 250 && r.a.z >= 15, `kamera nie jedzie za Tobą po starcie: ${JSON.stringify(r.a)}`);
    sprawdz(r.b.d > 250, `test nie przesunął mapy: ${JSON.stringify(r.b)}`);
    sprawdz(r.c.w && r.c.d < 250, `mapa nie wróciła sama po przesunięciu: ${JSON.stringify(r.c)}`);
    sprawdz(r.kompas, 'w nawigacji nie widać kompasu');
    sprawdz(!r.polnoc, 'kropla „Północ u góry” wróciła do układu');
  }
};
