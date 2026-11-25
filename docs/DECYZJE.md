# Decyzje (z datą i powodem)

Najnowsze na górze. Szczegóły i dyskusja – w historii zmian (git log) i w opisach wydań.

| Data | Decyzja | Powód |
|---|---|---|
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
