# Decyzje (z datą i powodem)

Najnowsze na górze. Szczegóły i dyskusja – w historii zmian (git log) i w opisach wydań.

| Data | Decyzja | Powód |
|---|---|---|
| 2026-11-27 | v140 (pkt 474–476): naprawa kamery nawigacji (od v134 nie przybliżała, nie jechała za Tobą i nie wracała po przesunięciu) + test w 04-nawigacja; przy wyborze trasy (ekran „Trasa”, karta wariantów) ukryte ślady, obiekty map i zdjęcia – po „Nawiguj” wracają (Paweł: tylko przy wyborze, nie w nawigacji); kompas pojawia się sam tylko przy obróconej mapie i w nawigacji (pod „Moja pozycja”), kropla „Północ u góry” usunięta ze schowka | zgłoszenia Pawła 27.11 z jazdy samochodem |
| 2026-11-25 | v139 (pkt 470, 471): zdjęcie podczas nagrywania od razu do śladu, bez okna; **bez rozmycia tła (backdrop-filter) w całej aplikacji** – samo półprzezroczyste tło; zwalnianie kafli w tle i przy sygnale Androida „mało pamięci”; w Diagnostyce powód poprzedniego zamknięcia OMM przez Androida | v138 przy starcie nagrywania zabrał pamięć całego telefonu (przeładował pulpit) – warstwy rozmycia przeliczane co klatkę |
| 2026-11-25 | v138 (pkt 468, 469): Zdjęcia jako trzecia zakładka „Moje” (kropla Zdjęcia usunięta); w niej Wczytaj, Foldery (auto), Zaznacz, Zapisz w telefonie, skrzynka „Bez miejsca” i „Miejsce” (wskaż na mapie / wg śladu / jak inne zdjęcie); ikona „Moje” = komoda z mapą, Podkład = ikona mapy | ustawienia folderów były za mocno ukryte; wybór Pawła spośród 3 rund ikon |
| 2026-11-25 | v137 (pkt 467): automatyczne zdjęcia z wybranych folderów telefonu – po otwarciu OMM pasek z pytaniem (Importuj / Pokaż / Nie teraz, „Zawsze automatycznie”), tryb automatyczny z informacją i „Cofnij”; dostęp tylko do wskazanego folderu (bez READ_MEDIA_IMAGES) | Google Photos od 03.2025 nie daje aplikacjom dostępu do całej biblioteki; pasek zamiast okna – zasada „bez okien przy starcie” |
| 2026-11-25 | v136 (pkt 465, 466): menedżer „Moje” – zakładki jako przekładki obrysowane tuszem (wariant 7b: ikona, nazwa, liczba przy prawej krawędzi), pod nimi jeden wiersz ‹ nazwa ▾ ＋ ✕, pasek narzędzi bez zieleni, podpowiedź przy pustej mapie; napis u góry mapy w jednym wierszu (tryb 10,5 px + nazwa 14 px) | v134/v135 wyglądały biednie; wybór Pawła spośród 12 wariantów |
| 2026-11-25 | v135 (pkt 464): przyciski wyraźnie prześwitują (tło ok. 60%, lekkie rozmycie), cieńszy i jaśniejszy obrys kapsuł, napisy bez ramek; zakładki niskie (32 px, 13,5 px), liczba zwykłą szarą cyfrą; nagłówek menedżera zwarty, nazwa mapy 16 px | v134 wyglądał na biały, gruby i napompowany |
| 2026-11-25 | v134: jeden przycisk **„Moje”** (ikona kartoteki) zamiast osobnych Mapy i Ślady; u góry zakładki kartoteki Mapy \| Ślady z liczbą; otwiera ostatnią zakładkę, po nagrywaniu/nawigacji – Ślady; przesunięcie palcem przełącza | mniej przycisków na mapie, jedno miejsce na „moje rzeczy”, ten sam układ obu list |
| 2026-11-25 | Przycisk „＋ Mapa” / „＋ Kategoria” w wierszu nazwy, dosunięty do prawej, w stylu przycisków paska (ikona z plusem + podpis), nie okrągły ＋ | spójność z paskiem „Warstwa” |
| 2026-11-25 | Bez poświat i bez mgły; tło minimalnie przezroczyste z rozmyciem i wyraźną krawędzią | gradienty przezroczystości „jak z lat 90.” |
| 2026-11-25 | Ortofoto Geoportalu od przybliżenia 15 z WMS HighResolution (podwójna gęstość), niżej kafle StandardResolution | Standard przy dużym zbliżeniu rozmyty; HighResolution nie ma kafli w EPSG:3857 |
| 2026-11-25 | Nocna mapa: filtr na każdym kaflu, nie na całej warstwie | przy przesuwaniu widać było przez chwilę mapę dzienną |
| 2026-11-25 | Zoom w nawigacji małymi krokami zwykłej animacji Leafleta zamiast flyTo | linie trasy/śladu rozjeżdżały się z podkładem |
| 2026-11-25 | Repo = źródło prawdy (ZASADY, DECYZJE, ARCHITEKTURA, ZATWIERDZONE, CLAUDE.md), testy w CI blokują wydanie; cotygodniowa kopia ZIP repozytorium na Dysk Google | ochrona ustaleń i sprawdzonych mechanizmów przed utratą |
| 2026-11-24 | To, co Paweł zatwierdzi („zostawiamy”), jest chronione testami i wpisem w ZATWIERDZONE.md | żeby zmiany nie psuły sprawdzonych rzeczy |
| 2026-11-20 | v133: fabryczny układ kropli i kafelków treningu wg Pawła; podkład fabryczny OpenStreetMap | wybór Pawła |
| 2026-11-19 | Jeden wygląd „Mapa i krawędzie” (stary „Obecny” usunięty); bez jednorazowych okien przy starcie | prostota, mniej kodu |
| 2026-11-19 | Zmiany natywne bez czekania na audyt – lista do audytu w docs/ | tempo prac |
| 2026-11-19 | Mapy offline i „Odśwież mapę” usunięte (warunki OSMF); offline wróci jako pakiet mapy Polski | zgodność z zasadami serwerów |
| 2026-11-01 | Wzorzec śladu pieszego: v106 (TRK_ALG 10); auto: v121 | najlepsza rejestracja wg porównań z innymi rejestratorami |
| 2026-10-21 | Wzorzec algorytmu śladu: v79 (gałąź `slad-dobry-v79`) | „naprawdę dobry” – punkt odniesienia |
