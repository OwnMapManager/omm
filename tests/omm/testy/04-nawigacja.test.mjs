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
  }
};
