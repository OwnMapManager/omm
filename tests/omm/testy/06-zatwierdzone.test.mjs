// Zatwierdzone przez Pawła widoki (docs/ZATWIERDZONE.md): zrzut ekranu w rozmiarze telefonu porównany z wzorcem.
// Nowy zatwierdzony widok = nowy wpis tutaj + wzorzec zapisany przez --aktualizuj.
import {zrzut} from '../srodowisko.mjs';

const czekaj = ms => new Promise(r => setTimeout(r, ms));
const start = async (page, url) => {
  await page.addInitScript(() => {
    localStorage.setItem('omm-tut', 'karty');
    localStorage.setItem('mapa-view', JSON.stringify({c: [50.0597, 19.9126], z: 15}));
  });
  await page.goto(url);
  await czekaj(3000);
};
// Lista pusta, dopóki Paweł nic nie zatwierdzi – wpisy dopisujemy razem z docs/ZATWIERDZONE.md
export const WIDOKI = {};
export const testy = Object.fromEntries(
  Object.entries(WIDOKI).map(([nazwa, przygotuj]) => [
    nazwa,
    async ({page, url, wyniki}) => {
      await start(page, url);
      if (przygotuj) await page.evaluate(przygotuj);
      await czekaj(800);
      await zrzut(page, nazwa, wyniki);
    }
  ])
);
