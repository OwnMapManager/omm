// Start aplikacji: bez błędów JavaScript, także przy pierwszym uruchomieniu (karty startowe nad mapą) i z danymi.
import {sprawdz} from '../srodowisko.mjs';

const czekaj = ms => new Promise(r => setTimeout(r, ms));
export const testy = {
  async 'pierwsze uruchomienie – karty startowe'({page, url}) {
    await page.goto(url);
    await czekaj(3000);
    const r = await page.evaluate(() => ({v: typeof APP_VER === 'string' && APP_VER, karty: !!document.querySelector('.dbox.scards'), mapa: !!document.querySelector('.leaflet-container')}));
    sprawdz(r.v && /^v\d+$/.test(r.v), 'brak APP_VER');
    sprawdz(r.mapa, 'mapa się nie zbudowała');
    sprawdz(r.karty, 'przy pierwszym uruchomieniu nie ma kart startowych');
  },
  async 'kolejne uruchomienie – mapa i krople bez okien'({page, url}) {
    await page.addInitScript(() => localStorage.setItem('omm-tut', 'karty'));
    await page.goto(url);
    await czekaj(3000);
    const r = await page.evaluate(() => ({okna: document.querySelectorAll('.dback').length, krople: document.querySelectorAll('#sklchips .sklchip').length}));
    sprawdz(r.okna === 0, 'przy starcie otwarte okno (' + r.okna + ') – jednorazowych okien przy starcie nie robimy');
    sprawdz(r.krople >= 6, 'za mało kropli poleceń na mapie: ' + r.krople);
  }
};
