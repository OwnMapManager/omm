# OMM – Own Map Manager

**Twoje mapy, Twoje dane.** OMM to prywatny notes na mapie dla ludzi, którzy chodzą po lesie i górach: grzybiarzy, turystów, miłośników bushcraftu. Zaznaczasz miejsca, które chcesz zapamiętać (grzybowe miejscówki, wiaty, noclegi, źródła, ciekawe zakątki), rysujesz trasy i obszary, mierzysz odległości i wysokości. Wszystko zapisuje się w telefonie i działa także bez zasięgu.

© 2026 Paweł Jewuła · licencja: patrz [LICENSE](LICENSE) (kod GPL v3 z dodatkowymi warunkami, grafika CC BY-NC-ND 4.0, nazwa zastrzeżona).

---

## Instalacja na telefonie z Androidem

1. Na telefonie otwórz **[Releases → najnowsze wydanie](https://github.com/OwnMapManager/omm/releases/latest)** i pobierz plik `omm-vXX.apk`.
2. Otwórz pobrany plik. Android zapyta o zgodę na instalację z tego źródła (np. z Chrome) – zezwól.
3. **Aktualizacja**: pobierz nowszy plik i zainstaluj go na starą wersję. Dane zostają (nie odinstalowuj wcześniej – odinstalowanie kasuje dane z telefonu).
4. Pliki zapisywane przez aplikację (kopie, eksporty, wydruki) trafiają do folderu **Dokumenty/OMM** albo **Pobrane/OMM** (wybór w ⚙️ Ustawieniach; wydruki mogą iść do **Obrazy/OMM**, widocznych w galerii). Z okna po zapisie można je też „Udostępnić…” (np. na Dysk Google).

Wersja w przeglądarce dalej działa: plik [`app/omm.html`](app/omm.html) otwiera się w każdej przeglądarce bez instalacji.

## Dla początkujących – jak zacząć

1. **Otwórz OMM** – aplikację na telefonie albo plik `omm.html` w przeglądarce (najlepiej Chrome).
2. **Mapa** – przesuwasz palcem, przybliżasz dwoma palcami. Przycisk ◎ pokazuje, gdzie jesteś.
3. **Podkład** – przycisk mapy w prawym górnym rogu: zwykła mapa, zdjęcia lotnicze, rzeźba terenu (LiDAR), nakładki (działki, szlaki, „Zanocuj w lesie”, wiaty).
4. **Punkt** – przycisk pinezki → wybierz grupę i ikonę (np. Grzyby → Kania) → dotykaj mapy → ✓ kończy.
5. **Kształty** (ołówek) – linie, wielokąty, okręgi. **Pomiary** (linijka) – odległość, powierzchnia, azymut, wysokość, profil terenu.
6. **Opis i ocena** – dotknij punktu: nazwa, ikona, komentarz, ocena w gwiazdkach, zdjęcia (linki), szablon „Dane noclegu”.
7. **Warstwy** – przycisk w lewym dolnym rogu: mapy, warstwy, filtr („pokaż tylko kanie”), import, kopia.
8. **Kopia zapasowa** – Kopia → „Zapisz pełną kopię”. Rób ją regularnie, zwłaszcza przed aktualizacją – to jeden plik ZIP ze wszystkim (mapy, ślady, zdjęcia, ustawienia).
9. **Ślady GPS** – czerwony przycisk ⏺ po prawej rozpoczyna nagrywanie (w aplikacji także przy wygaszonym ekranie). Pasek u góry pokazuje czas, dystans i prędkość; ⏸ pauza, ⏹ koniec i zapis. Wszystkie ślady są w panelu warstw w zakładce **Ślady GPS** – z własnymi warstwami, statystykami, profilem wysokości i eksportem GPX.
10. **Zdjęcia** – w okienku punktu „📷 Zdjęcie” (aparat albo galeria). Podczas nagrywania śladu przycisk 📷 na pasku dodaje samo zdjęcie albo punkt z opisem w bieżącym miejscu. Przycisk **📷 Zdjęcia** w panelu warstw zamienia zdjęcia z zapisaną lokalizacją w punkty 📷 (wybieraj je przez aplikację „Pliki” – galeria Androida potrafi usuwać lokalizację).
11. **🧭 Nawiguj** – w okienku punktu: linia i odległość od Twojej pozycji do punktu, aktualizowane na bieżąco; Twoja pozycja to strzałka obracana kompasem telefonu.
12. **Animacja śladu** – w okienku śladu „▶ Animacja”: ślad rysuje się stopniowo, przy zdjęciach animacja się zatrzymuje; „🎬 Film” zapisuje ją jako plik wideo do udostępnienia.
13. **Z Moich Map Google** – w Moich Mapach pobierz KML/KMZ, potem tutaj Import (zdjęcia i opisy też się przenoszą).

Dane są tylko Twoje: aplikacja nikomu ich nie wysyła, z internetu pobiera jedynie podkłady mapowe.

---

## Najważniejsze funkcje

- Wiele map, każda z listą warstw (krycie, widoczność, scalanie, przenoszenie obiektów).
- Tryb **Prywatny** i **Praca** – osobne listy map; w trybie Praca narzędzia do szkiców: kropki, odcinki, strzałki, numeracja, napisy.
- Ikony w grupach: Ogólne, Nocleg i schronienie, Teren (bushcraft), Grzyby, Rośliny użytkowe.
- **Filtr** po kategoriach (ikonach) – z automatycznym włączaniem właściwej warstwy.
- **Ocena 1–5 gwiazdek** dla noclegów i restauracji; edytowalne szablony opisów.
- Pomiary: odległość, powierzchnia, azymut, różnica wysokości, profil NMT (GUGiK), wysokość punktu, współrzędne.
- Import: GPX, KML, **KMZ**, GeoJSON (ze zdjęciami z Moich Map). Eksport: GPX, KML, GeoJSON.
- Kopia zapasowa całości albo tylko tego, co widać; wydruk kadru do PNG w wysokiej rozdzielczości.
- **Ślady GPS**: nagrywanie (także w tle, z pauzą, z wygładzaniem pozycji), panel na żywo, osobne warstwy śladów, statystyki (dystans, czas, prędkości, podejścia, wysokości), ślad w kolorach prędkości, profil wysokości z GPS i z NMT, zdjęcia i punkty na trasie, animacja i film, import/eksport GPX.
- **Zdjęcia** w punktach i na śladach (zmniejszone kopie w aplikacji, oryginały w galerii); masowy import zdjęć z lokalizacją.
- **Kopia zapasowa ZIP** z folderami `mapy/` (GeoJSON), `slady/` (GPX), `zdjecia/` i plikiem `ustawienia.json` – każdy plik otworzysz także innym programem.

---

## Dla programistów – jak zbudowany jest kod

- **Jeden plik HTML**: style (CSS) → układ przycisków → jeden skrypt JavaScript. Jedyna biblioteka: [Leaflet](https://leafletjs.com) 1.9.4.
- Sekcje skryptu zaczynają się od komentarza `// ---------- nazwa ----------` – wyszukaj go w pliku. Na początku skryptu jest spis sekcji i **opis modelu danych** (wszystkie pola obiektów).
- Dane są zapisywane w `localStorage` pod kluczem `mapa-v2` po każdej zmianie. Krótkie nazwy pól (`t`, `p`, `i`, `nm`, `n`, `ra`…) zostają bez zmian, bo w tym formacie zapisane są dane użytkowników – ich znaczenie opisuje nagłówek skryptu.
- **Zmiana struktury danych**: podnieś `SCHEMA` i dopisz krok w `MIGR` – stare dane przechodzą automatycznie, a przed migracją robiona jest kopia awaryjna.
- **Kopia zapasowa** (od v35) to ZIP: `omm.json` (spis), `mapy/*.geojson`, `slady/*.gpx` (dane OMM w `<extensions>`), `zdjecia/*.jpg`, `ustawienia.json`. Każda mapa w środku to standardowy GeoJSON (RFC 7946): obiekty w `features` z polskimi nazwami pól (`mapa`, `warstwa`, `typ`, `nazwa`, `ikona`, `komentarz`, `ocena`, `zdjecia`…), a układ map i ustawienia w sekcji `omm`. Plik otworzysz w QGIS, geojson.io i innych programach.

### Jak dodać własną ikonę

Ikony to rysunki SVG w siatce 32×32, rejestrowane funkcją `igroup(kod_grupy, nazwa, symbol, [[id, nazwa, svg, pełna_nazwa], …])` w sekcji „ikony punktów”. Nowe ikony rysuj w stylu z sekcji „Nowy styl ikon OMM” (obiekt `NOWY_STYL`): jeden kontur `#3a2c22` grubości 1,5, paleta z obiektu `PAL`, jeden cień z boku. Rysunek dodany do `NOWY_STYL` pod tym samym identyfikatorem automatycznie zastępuje starszą wersję ikony; starsze rysunki bez zamiennika są ujednolicane funkcją `restyleSvg` (grubszy kontur, stonowane kolory). Identyfikatora raz użytej ikony nie zmieniaj – jest zapisany w danych; ikonę wycofaną z menu zarejestruj przez `iadd(…, 1)`, żeby stare punkty dalej się wyświetlały.

### Aplikacja na Androida (Capacitor)

```
app/omm.html                aplikacja (ten sam plik działa w przeglądarce)
scripts/prepare-www.mjs     tworzy www/: wbudowany Leaflet zamiast CDN, numer wersji z APP_VER
android/                    projekt Android (Capacitor 8) – uprawnienia GPS, podpis, ikony
art/logo.svg, art/render.py logo i skrypt generujący z niego ikony aplikacji i ekran startowy
.github/workflows/android.yml  budowanie APK w chmurze i publikacja w Releases
```

- Każda zmiana w gałęzi `main` uruchamia akcję **Zbuduj APK**; po kilku minutach plik `omm-vXX.apk` pojawia się w Releases. Numer wersji APK = liczba z `APP_VER` w `app/omm.html`, więc **przy każdej nowej wersji podnieś `APP_VER`** (inaczej telefon nie potraktuje jej jako aktualizacji).
- Podpis: klucz `android/omm-release.p12` jest zaszyfrowany; hasło do niego jest tylko w sekrecie repozytorium `OMM_KEYSTORE_PASSWORD`. Klucz musi być zawsze ten sam – bez niego nowej wersji nie da się zainstalować na starą. Kto buduje własną, zmienioną wersję, używa własnego klucza (i innej nazwy aplikacji – patrz LICENSE).
- Różnice między przeglądarką a APK są w kodzie w jednym miejscu: stała `NATIVE` (zapis plików wtyczkami Filesystem/Share, przycisk „Wstecz”, podpowiedzi GPS).
- Budowanie u siebie: `npm ci && npm run sync`, potem `android/` otwórz w Android Studio.

### Testy

Zmiany sprawdzane są automatycznie w przeglądarce Chromium (Playwright) i w jsdom: wczytanie i migracja danych, rysowanie, filtr, import KML/KMZ, kopia i jej odtworzenie, wydruk.

---

## Kontakt

Dziękuję za zainteresowanie OMM! Uwagi, pomysły i zgłoszenia błędów: **[onemanmanufacture@gmail.com](mailto:onemanmanufacture@gmail.com?subject=OMM%20–%20uwagi)**.
W tej samej sprawie pisz, jeśli chcesz uzyskać zgodę opisaną w pliku LICENSE (np. wydanie w sklepie z aplikacjami).

## Plany

- Zdjęcia zapisywane w telefonie i w kopii zapasowej.
- Automatyczna kopia na Dysk Google, mapy offline.
- Menedżer ikon: wybór widocznych grup i ikon, „Ulubione”.
