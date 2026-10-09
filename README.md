# OMM – Own Map Manager

**Twoje mapy, Twoje dane.** OMM to prywatny notes na mapie dla ludzi, którzy chodzą po lesie i górach: grzybiarzy, turystów, miłośników bushcraftu. Zaznaczasz miejsca, które chcesz zapamiętać (grzybowe miejscówki, wiaty, noclegi, źródła, ciekawe zakątki), rysujesz trasy i obszary, mierzysz odległości i wysokości. Wszystko zapisuje się w telefonie i działa także bez zasięgu.

© 2026 Paweł Jewuła · licencja: patrz [LICENSE](LICENSE) (kod GPL v3 z dodatkowymi warunkami, grafika CC BY-NC-ND 4.0, nazwa zastrzeżona).

---

## Bez reklam. Bez śledzenia. Na zawsze.

OMM jest i zawsze będzie wolny od reklam i śledzenia. Wszystkie funkcje są dostępne dla każdego – bez wersji „premium” i bez blokowania funkcji za opłatą.

## Bezpieczeństwo

- Dane z importowanych plików i kopii są sprawdzane przed użyciem: identyfikatory, kolory, teksty i zdjęcia mają ścisły format, a strona aplikacji ma politykę CSP, która nie pozwala uruchomić żadnego kodu spoza samej aplikacji.
- Przywracanie kopii niczego nie zmienia przed Twoją zgodą (także zdjęć); archiwa ZIP są sprawdzane sumami kontrolnymi i limitami rozmiaru.
- Połączenia wyłącznie przez https. Zdjęcia-linki z internetu wczytują się dopiero po Twojej zgodzie.
- Opcjonalne **szyfrowanie kopii hasłem** (AES-256-GCM, klucz z hasła PBKDF2-SHA-256). Utrata hasła = kopii nie da się odczytać.
- Zgłoszenia problemów bezpieczeństwa: onemanmanufacture@gmail.com. Historia napraw: [docs/audyt-v45-naprawy.md](docs/audyt-v45-naprawy.md).

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
9. **Ślady GPS** – przycisk **REC** po prawej rozpoczyna nagrywanie (w aplikacji także przy wygaszonym ekranie), a ponowne dotknięcie kończy je i zapisuje. W trakcie nagrywania pod REC jest pigułka: ⏸ pauza, 📷 zdjęcie, 📍 punkt z ikoną i opisem. Pasek u góry pokazuje czas, dystans i prędkość (dotknięcie – panel na żywo). Mapa podąża za Tobą; gdy ją przesuniesz, wraca po 6 s. Wszystkie ślady są w panelu warstw w zakładce **Ślady GPS** – z własnymi warstwami, okiem pokaż/ukryj, zaznaczaniem (przenieś, ukryj, GPX, usuń), statystykami i profilem wysokości.
10. **Zdjęcia** – przycisk 📷 na dolnym pasku: zdjęcie tutaj (pozycja z GPS), zdjęcie w wybranym miejscu (dotknij mapy albo punktu), z galerii z lokalizacją (jedno lub wiele) albo z galerii w wybrane miejsce. Zdjęcia z galerii wybieraj przez aplikację „Pliki” – galeria Androida potrafi usuwać lokalizację. Punkty i zdjęcia dodane w czasie nagrywania można później skopiować na dowolną mapę („📍 Punkty na mapę…” w okienku śladu).
11. **🧭 Nawiguj** – w okienku punktu: linia i odległość od Twojej pozycji do punktu, aktualizowane na bieżąco; Twoja pozycja to strzałka obracana kompasem telefonu.
12. **Udostępnij i ✂ Podziel** – w okienku śladu: udostępnienie jako GPX, obraz mapy, obraz statystyk, mapa + statystyki albo film; dzielenie śladu w wybranym miejscu – nożyczki ✂ przeciąga się po śladzie, części są oznaczone kolorami 1 i 2 (np. gdzie przesiadłeś się do auta – OMM sam to podpowie po zakończeniu nagrywania).
13. **Animacja śladu** – w okienku śladu „▶ Animacja”: ślad rysuje się stopniowo, przy zdjęciach animacja się zatrzymuje; „🎬 Film” zapisuje ją jako plik wideo do udostępnienia.
14. **Z Moich Map Google** – w Moich Mapach pobierz KML/KMZ, potem tutaj Import (zdjęcia i opisy też się przenoszą).

Dane są tylko Twoje: aplikacja nikomu ich nie wysyła, z internetu pobiera jedynie podkłady mapowe.

---

## Najważniejsze funkcje

- **Zapomniany REC**: gdy OMM pokazuje Twoją pozycję, pamięta ostatnie 30 min ruchu i po włączeniu nagrywania proponuje dołączyć je do śladu; w okienku śladu ikona „dorysuj” pozwala zaznaczyć na mapie brakujący początek, koniec albo przerwę (opcjonalnie wzdłuż ścieżek), a w zaznaczaniu śladów „Scal” łączy kilka śladów w jeden. Dorysowane kawałki mają linię przerywaną.
- **Blokada aplikacji** (⚙️ → Prywatność): PIN / hasło albo odcisk palca / twarz (z zapasowym PIN-em); nagrywanie śladu działa dalej, gdy OMM jest zablokowany.
- **Samouczek** – krótki pokaz najważniejszych funkcji (⚙️ Ustawienia → Aplikacja → 📘 Samouczek); przy pierwszym uruchomieniu aplikacja pyta, czy go pokazać.

- Wiele map, każda z listą warstw (krycie, widoczność, scalanie, przenoszenie obiektów).
- Tryb **Prywatny** i **Praca** – osobne listy map; w trybie Praca narzędzia do szkiców: kropki, odcinki, strzałki, numeracja, napisy.
- Ikony w grupach: Ogólne, Nocleg i schronienie, Teren (bushcraft), Grzyby, Rośliny użytkowe.
- **Filtr** po kategoriach (ikonach) – z automatycznym włączaniem właściwej warstwy.
- **Ocena 1–5 gwiazdek** dla noclegów i restauracji; edytowalne szablony opisów.
- Pomiary: odległość, powierzchnia, azymut, różnica wysokości, profil NMT (GUGiK), wysokość punktu, współrzędne.
- Import: GPX, KML, **KMZ**, GeoJSON (ze zdjęciami z Moich Map). Eksport: GPX, KML, GeoJSON.
- Kopia zapasowa całości albo tylko tego, co widać; wydruk kadru do PNG w wysokiej rozdzielczości.
- **Ślady GPS**: nagrywanie (także w tle, z pauzą i auto-pauzą na postojach, z wygładzaniem pozycji), komunikat co 1 km (powiadomienie / głos), panel na żywo (tempo, odległość do startu, zachód słońca), osobne warstwy śladów, rodzaj aktywności, statystyki (dystans, czas, prędkości średnie i min/max, tempo, podejścia, wysokości), ślad w jednolitym kolorze albo w kolorach prędkości / wysokości, profil wysokości z GPS i z NMT, zdjęcia i punkty na trasie, animacja i film (kamera podąża za śladem, liczniki i profil wysokości, formaty 9:16 / 1:1 / 16:9, 720p / 1080p), kolor ustawiany osobno dla każdego śladu, automatyczna nazwa (data / rodzaj / miejsce z OpenStreetMap, np. „Rower · Bałuty → Widzew”), rozpoznawanie rodzaju aktywności i przesiadki do auta, dokładne dzielenie śladu, podsumowania tygodnia / miesiąca / roku, import GPX, KML/KMZ, TCX i GeoJSON, eksport GPX.
- **Mapy offline**: pobieranie podkładu (OpenStreetMap do zoomu 16, ortofoto Geoportalu) dla obszaru na ekranie; bez internetu mapa korzysta z pobranych kafli, a przy większym przybliżeniu je powiększa.
- **W terenie**: kropka GPS ze stożkiem kierunku (kompas), prowadzenie do punktu i po śladzie (powrót do startu), współrzędne z kopiowaniem, SMS-em i udostępnianiem (SOS), wschód i zachód słońca.
- **Wyszukiwarka** (obiekty, ślady, współrzędne; adresy przez OpenStreetMap Nominatim na żądanie) i **kosz** – usunięte obiekty, warstwy, mapy i ślady można przywrócić przez 30 dni.
- **Zdjęcia** w punktach i na śladach (zmniejszone kopie w aplikacji, oryginały w galerii); masowy import zdjęć z lokalizacją.
- **Automatyczna kopia** (przy zamknięciu: zawsze / raz dziennie / raz w tygodniu): lokalnie 7 ostatnich ZIP-ów i jeden nadpisywany plik na Dysku Google (wybrany raz w systemowym oknie „Zapisz w…”, wysyła aplikacja Dysk – bez logowania i kluczy API). Kopia Androida (allowBackup) wyłączona.
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
- Podpis: klucz podpisu nie jest w repozytorium – budowanie bierze go z sekretu `OMM_KEYSTORE_B64` (zaszyfrowany plik PKCS#12 zakodowany base64), hasło z sekretu `OMM_KEYSTORE_PASSWORD`; przed budowaniem sprawdzany jest odcisk certyfikatu (SHA-256 `0329e7be…db17ac`). Klucz musi być zawsze ten sam – bez niego nowej wersji nie da się zainstalować na starą. Kto buduje własną, zmienioną wersję, używa własnego klucza (i innej nazwy aplikacji – patrz LICENSE).
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
- Obracanie mapy zgodnie z kierunkiem patrzenia, samouczek.
- Menedżer ikon: wybór widocznych grup i ikon, „Ulubione”.
