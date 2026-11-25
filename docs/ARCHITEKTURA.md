# Architektura OMM – mechanizmy, których nie wolno zgubić

Cały kod aplikacji jest w `app/omm.html` (HTML + CSS + JS w jednym pliku). Natywne wtyczki Androida:
`android/app/src/main/java/io/github/ownmapmanager/omm/` (OmmBackup, OmmLock, OmmPhotos, OmmNoWebView, OmmRecNotif,
OmmGnss, OmmSys). Serwery: `docs/serwery.md`. CSP: `scripts/prepare-www.mjs`.

## Dane
- IndexedDB „omm” (mapy `DB.maps`, ślady `TRK` pod kluczem „slady”, kafle nakładek „ov:…”), ustawienia w localStorage
  `omm-*`. Kopia zapasowa: `settingsObj.prefs` = wszystkie `omm-…` poza `PREFS_SKIP` (ustawienia zależne od urządzenia
  dopisywać do PREFS_SKIP). Dane zostające przy „ustawieniach fabrycznych” – `RESET_KEEP`.
- Kosz: `trashPut` / `trashRestore` (30 dni), „Cofnij” w podpowiedzi (`toastAct`).

## Ślady GPS
- `trkProcess(rows)` – surowe pomiary (s, szer, dł, dokładność, prędkość, kierunek, wysokość) → punkty śladu.
  Kolejno: `trkDriftFix` (skoki przy złej dokładności), start od 3 dobrych pomiarów, `indoorFix`, `trkWalkMask` (mocne
  wygładzanie tylko marszu), filtr Kalmana (`kfAcc` z przyspieszeniem dośrodkowym, `kfVelR`), `stopFind` (postoje),
  upraszczanie (jazda DP 1 m, marsz 1,5 m w pasie ±3 m), ciasne zakręty jazdy – wierzchołek na przecięciu prostych.
- Każda zmiana liczenia = podbicie `TRK_ALG` (ślady przeliczane z surowych pomiarów, pytanie z „Nie pytaj więcej”).
  Test `tests/omm/testy/05-slady.test.mjs` pilnuje, żeby wynik nie zmienił się bez podbicia.
- Wzorce: marsz v106 (TRK_ALG 10), auto v121; algorytm v79 w gałęzi `slad-dobry-v79`. Samochodu i roweru nie prostować.
- Zawsze zostają: surowe pomiary + ślad OMM + ślad po edycji („Przywróć oryginał”).

## Wygląd „Mapa i krawędzie”
- Polecenia = krople (`sklCmds`, `sklChips`); sąsiednie krople łączą się w kapsułę, kropla na kropli = folder,
  schowek = dolny wiersz Panelu Dziś. Zapis `omm-uklad` = `{p|w: {v:2, s:[…], f:{…}, x:[…]}}`; `sklGet` porządkuje
  (każde polecenie dokładnie raz) i przenosi stare układy (`sklMigrate`; v134: Mapy + Ślady → „Moje”).
- Fabryczny układ (`sklFactory`): lewa kapsuła Punkt, Rysuj, Zmierz, Zdjęcie; prawa Moje, Podkład; nad nią Trening;
  u góry Szukaj i Moja pozycja; schowek: Ustawienia, Zdjęcia, Gry, Północ.
- Tło elementów: `rgba(var(--kfog), 0.88)` + `backdrop-filter: var(--kblur)` + linia `var(--line)`. Kapsuły: rysowany
  obrys (filtr SVG `SKL_INK`) nad szklanym tłem przyciętym tym samym kształtem (`.sklglass`, `clip-path: path()`).
  Kolory HUD-u liczone na elemencie (nie przez zmienną z `html`) – noc bez przyciemnionej mapy ma dzienne krople.
- Menedżer „Moje” (`menuOpen('moje')`, `mojeTabsUi`, `mojeTab`): pasek `#ktop` z zakładkami `#ktabs` (Mapy | Ślady | Zdjęcia) (przekładki rysowane filtrem `#kinkf` = `SKL_INK`), wiersz `#sh`: ‹ `#mname` `#mnew` ✕; podpowiedź pustej mapy `mHint`.
- Ikony „Tusz z plamką”: `scripts/ikony-zrodlo.mjs` → `scripts/ikony-tusz.mjs` generuje `IC`; rejestr `IC` w omm.html.

## Mapa i podkłady
- Leaflet 1.9.4 + leaflet-rotate 0.2.8. Własne panele mają „bliźniaka” na znaczniki (obracana mapa, stojące ikony).
- Podkłady `BASES`; `baseWatch` przełącza na OSM, gdy podkład nie odpowiada 8 s.
- Ortofoto Geoportalu (`GeoOrto`): z < 15 kafle WMTS StandardResolution (EPSG:3857), z ≥ 15 WMS HighResolution
  (`geoHiUrl`, 512 px na kafel 256), zapas – kafel Standard. Certyfikaty wystawców Geoportalu pobiera CI
  (`scripts/geoportal-ca.sh` → `network_security_config`).
- Noc (`html.mnight`): filtr na każdym kaflu `.leaflet-tile`.
- Nakładki WMS z pamięcią (`CachedWMS`, IndexedDB 30 dni); GDOŚ z własnym SLD.

## Zdjęcia automatyczne (v137)
- Źródła w `omm-autofoto` (`afList`): folder (`uri` z `OmmPhotos.pickFolder`), mapa, warstwa, tryb `pytaj`/`auto`, `bez` (`slad`/`pomin`), `strefy`, `od`.
  Sprawdzone pliki: IndexedDB `autofoto:<id>` (`afSeen`). `afCheck` po starcie (6 s) i po powrocie do aplikacji; czeka, gdy `afBusy()`
  (nagrywanie, nawigacja, trening) albo otwarte okno. `afAsk` – pasek `.afbar`, `afPick` – podgląd miniatur, `afImport` – pinezki `o:foto`.
- v138: zakładka Zdjęcia (`fotoSheet`): pasek `.fsa`, foldery `afSettings` w `.ffold`, skrzynka „Bez miejsca” (`omm-foto-bez`, `fotoBez*`,
  liczona w `fotoUsed` – pliki nie są sprzątane), `fotoLocPick` / `fotoRelocate` / `fotoSaveMany` (Pictures/OMM).

## Nawigacja
- `navStart` (bez trasy → ekran „Trasa” `routeScreen`), `navPos` (każdy pomiar), `navCam` (kadr i zoom: najwyżej ok.
  pół poziomu na sekundę, `navZoomSteps` – kroki zwykłej animacji Leafleta), `navRotLoop` (obrót), `navStop`.
- Stan zapisywany co chwilę (`omm-nav-stan`) – powrót po zamknięciu przez system (`navResume`).
- Głos: kolejka `SAYQ` (12 s limit, zapas speechSynthesis), wyciszenie `MUTE`; ostrzeżenie o wyciszonej głośności
  multimediów (`OmmSys.audio()`).
- Ekran: przygaszanie `#navdim`, rozjaśnienie przed manewrem (`omm-nav-przed`) i po nim (`omm-nav-po`).

## Odporność
- Dziennik `diag()`; „Wyślij raport”. Zamknięcie silnika strony przez Androida: `MainActivity.onRenderProcessGone`
  odtwarza okno, wpis w dzienniku (`OmmSys.lastGone()`); nagrywanie i nawigacja wracają same (`omm-zywy`, `omm-nav-stan`).
- Pamięć (v139): **bez `backdrop-filter`** (każda warstwa rozmycia to osobna tekstura GPU przeliczana co klatkę).
  `MainActivity.onTrimMemory` → zdarzenie `ommtrim` → `memTrim()` (keepBuffer 0, przycięcie kafli; poziom ≥ 20 – wszystkie);
  w tle (`App.pause`) też `memTrim`, po powrocie `memBack()`. Po starcie `OmmSys.exits()` (ApplicationExitInfo, Android 11+)
  → wpis „Android zamknął OMM: powód” w Diagnostyce (`omm-exit-t` – ostatni zgłoszony).

## Testy (`tests/omm/`)
- `node tests/omm/run.mjs` – wszystkie; CI uruchamia je przed budowaniem APK (zadanie `testy` w `.github/workflows/android.yml`).
- `--aktualizuj` zapisuje nowe wzorce (zrzuty zatwierdzonych widoków, wzorzec śladu, lista braków EN) – tylko świadomie.
- Bez sieci (kafle i serwery zablokowane) – wynik powtarzalny.
