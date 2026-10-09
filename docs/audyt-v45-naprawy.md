# OMM v46 – naprawy po audycie bezpieczeństwa v45

Audyt: „OMM v45 – niezależny audyt bezpieczeństwa”, 9.10.2026. Badane APK: `f09afddf…cbb2b`, commit `15b6215`.
Naprawy: wersja **v46** (commit „v46: naprawy bezpieczeństwa…”), uzupełnienia w **v47** (klucz podpisu), **v48** (blokada aplikacji) i **v49** (ustalenia wewnętrznego audytu v48 – sekcja na końcu). Źródło `app/omm.html` przed zmianami było bajtowo
identyczne z plikiem z badanego commitu, więc ustalenia dotyczyły dokładnie tego kodu.

## Wydanie do ponownej oceny

- APK: https://github.com/OwnMapManager/omm/releases/download/v49/omm-v49.apk
- SHA-256 APK: w pliku `omm-v49.apk.sha256` dołączonym do wydania
- SHA-256 certyfikatu podpisu v2 (Android 7–8): `0329e7be9bc9226f7033f2fc9939e784ac540ce425d10cfe0ec9e05ddedb17ac` (ten sam co v45)
- SHA-256 certyfikatu podpisu v3 (Android 9+, rotacja od v49): `a5d63e2b35da55cd4c2ec6ce520a1a731227df05ecf8abc43523f3bdb21bc4e5`
- Commit: podany w wydaniu v49; `assets/public/app.js` w APK jest bajtowo zgodny z wynikiem `npm run www` z tego commitu.

## Weryfikacja ustaleń przed naprawą

Testy z audytu powtórzono w **prawdziwym Chromium** (Playwright, pełna aplikacja z Leaflet, IndexedDB i DOM), a nie tylko w QuickJS.
Na niezmienionym v45:

- **F01 potwierdzone z wykonaniem kodu**: stara kopia z kolorem `#000000"><svg onload=…>` – po wczytaniu mapy kod z pliku **wykonał się** (znacznik testowy ustawiony).
- **F02 potwierdzone z wykonaniem kodu**: identyfikator śladu z kopii – po otwarciu listy śladów kod z pliku **wykonał się**.
- F03 (zdjęcie nadpisane przed zgodą), F06 (zły CRC przyjęty), F07 (`__proto__`, `constructor`, `toString` przerywały import) – potwierdzone.

Ten sam zestaw testów na v46: żadnego wykonania, żadnych atrybutów zdarzeń w DOM, zdjęcie bez zmian, zły CRC odrzucony, nazwy zastrzeżone importowane poprawnie.

## Tabela napraw

| ID | Zmienione pliki | Sposób naprawy | Test przed → po | Ograniczenia |
| --- | --- | --- | --- | --- |
| F01 | app/omm.html | Jeden schemat walidacji dla wszystkich danych (`cleanItem`, `cleanMap`, `cleanDB`): kolor tylko `#rgb`/`#rrggbb`, ID tylko `[A-Za-z0-9_-]{1,64}` (z przepisaniem odwołań warstw/map przez jawne mapowanie), teksty o ograniczonej długości, liczby sprawdzone. Działa przy wczytaniu danych z pamięci (dane zapisane przez starsze wersje), przy **każdym zapisie**, przy przywracaniu każdej wersji kopii, przy imporcie i w `render()`. `esc()` koduje też apostrof. | test_import Q01, Q01b: wykonanie → brak | Test w Chromium na komputerze, nie w Android WebView |
| F02 | app/omm.html | `cleanTrk`/`cleanTrkDB` dla śladów (ID, warstwy, rodzaj aktywności tylko ze znanej listy, tryb koloru, punkty liczbowe, punkty na śladzie); `trkSwatch` dodatkowo filtruje ID i kolor. Ścieżki GeoJSON, ZIP/GPX, kosz i dane już zapisane w IndexedDB. | Q02, Q02b: wykonanie → brak | jw. |
| R01 | scripts/prepare-www.mjs, MainActivity.java | W aplikacji skrypt przeniesiony do `app.js`, CSP: `script-src 'self'` (bez `unsafe-inline`, bez CDN), `connect-src` tylko do używanych serwerów. Nawet gdyby tekst trafił do strony jako HTML, przeglądarka nie uruchomi atrybutu zdarzenia. Aplikacja prosi o aktualizację WebView, jeśli nie obsługuje wstrzykiwania mostu bez skryptu w HTML. | test_csp: wstrzyknięty `onerror` zablokowany przez CSP | `useLegacyBridge` zostaje (wymaga go nagrywanie śladu w tle); `style-src 'unsafe-inline'` zostaje |
| F03 | app/omm.html | Zdjęcia z kopii tylko wczytywane do pamięci; zapis (`fotoCommit`, jedna transakcja IndexedDB) dopiero po zgodzie. Istniejące zdjęcie nigdy nie jest nadpisywane – przy innej treści nowe dostaje nową nazwę i odwołania są przepisywane. | Q03 (błąd w kopii), test_kopie (Anuluj, kolizja nazw): zmiana → brak | Snapshot map + transakcja zdjęć; brak testu „brak miejsca” na telefonie |
| F04 | app/omm.html | Opcjonalne **szyfrowanie kopii hasłem** (WebCrypto: PBKDF2-SHA-256 600 000 iteracji, AES-256-GCM, losowa sól i IV; klucz w telefonie jako nieeksportowalny obiekt, hasło nie jest zapisywane). Zaszyfrowany ZIP zawiera tylko opis, parametry i szyfrogram. Usuwanie zdjęcia kasuje też jego kopię w folderach OMM/zdjecia. | test_kopie: kopia bez hasła nieczytelna, złe hasło odrzucone, podmiana bajtu odrzucona, przywrócenie z hasłem działa | Domyślnie wyłączone (decyzja autora); oryginały z aparatu w Obrazy/OMM zostają jak każde zdjęcie z galerii; stare jawne kopie nie są ruszane |
| F05 | OmmBackupPlugin.java, app/omm.html | Nowa metoda `release`: „Odłącz” i zmiana miejsca zwalniają `takePersistableUriPermission`. Trwałe uprawnienie tylko do zapisu. | – | Brak testu `getPersistedUriPermissions()` na urządzeniu |
| R08 | file_paths.xml, OmmBackupPlugin.java | FileProvider tylko `cache/share/` (zamiast całej pamięci wspólnej i całego cache). `writeTo` zapisuje wyłącznie do URI z trwałym przydziałem użytkownika i otwiera cel do nadpisania dopiero po otwarciu niepustego źródła. | – | Atomowość zależy od dostawcy (Dysk) |
| F06 | app/omm.html | Czytnik ZIP: CRC-32, rozmiar po rozpakowaniu, sygnatury nagłówków, powtórzone nazwy, wpisy szyfrowane – odrzucenie całego archiwum. KMZ używa tego samego czytnika (usunięty drugi parser). | Q06: przyjęty → odrzucony | CRC nie jest uwierzytelnieniem – do tego szyfrowanie z GCM |
| F07 | app/omm.html | Słowniki nazw (`Object.create(null)`) w imporcie, kopiach i statystykach; `own()` przy słownikach typów. | Q04: TypeError → poprawny import | – |
| R06 | app/omm.html | Limity: plik ≤ 1 GB, ≤ 50 000 wpisów, wpis ≤ 512 MB, archiwum ≤ 2 GB po rozpakowaniu; rozpakowanie strumieniowe przerywane po przekroczeniu zadeklarowanego rozmiaru. | – | Brak fuzzingu na telefonie |
| R02 | app/omm.html, MainActivity.java, AndroidManifest.xml | Usunięte zapasowe połączenia HTTP (Geoportal, NMT przez CapacitorHttp). `MIXED_CONTENT_NEVER_ALLOW`, `usesCleartextTraffic="false"`. | grep: brak adresów `http://` do usług | Gdyby na starym telefonie Geoportal nie działał przez https – warstwa się nie wczyta (zamiast przejścia na http) |
| R03 | app/omm.html | Zdjęcia-linki: tylko https; z nowego serwera wczytywane dopiero po dotknięciu i zgodzie („Tylko to” / „Zawsze z tego serwera”); ustawienie „Zdjęcia z internetu”. Film ze śladu pomija niezatwierdzone. | test_kopie: brak połączenia przed zgodą, po zgodzie tylko https | – |
| R07 | scripts/patch-capacitor.mjs, workflow | Poprawka `CapacitorWebView.dispatchKeyEvent`: tekst przekazywany jako literał JSON (`JSONObject.quote`), nie sklejany z kodem. Nakładana po instalacji zależności; CI sprawdza jej obecność. | – | Do zgłoszenia do Capacitora; brak testu z IME na urządzeniu |
| R04/R05 | .github/workflows/android.yml, .gitignore | Akcje przypięte do pełnych SHA; build tylko z prawem odczytu, publikacja w osobnym zadaniu bez kodu projektu; `npm ci --ignore-scripts`; klucz i hasło tylko w kroku Gradle. **v47:** plik `.p12` usunięty z repozytorium, klucz wyłącznie z sekretu `OMM_KEYSTORE_B64`; przed budowaniem sprawdzany odcisk certyfikatu (inny klucz = przerwanie builda). Suma SHA-256 APK publikowana z wydaniem. | v47: build z sekretu, podpis APK = `0329e7be…db17ac` (bez zmian, aktualizacje działają) | Plik .p12 pozostaje w historii repozytorium – ochronę daje losowe hasło; rotacja klucza (APK Signature Scheme v3) do rozważenia osobno. Brak `distributionSha256Sum` Gradle. |
| R09 | MainActivity.java | Ostrzeżenie i prośba o aktualizację przy starym WebView. | – | minSdk 24 bez zmian |
| R10 | OmmLockPlugin.java, app/omm.html | **v48:** opcjonalna blokada aplikacji (⚙️ → Prywatność): PIN/hasło (przechowywany tylko skrót PBKDF2-SHA-256, 150 000 iteracji, losowa sól; po 5 błędach rosnące opóźnienie) albo systemowa biometria Androida (BiometricPrompt, BIOMETRIC_STRONG/WEAK) z zapasowym PIN-em; ponowna blokada od razu / po 1 / po 5 min w tle; przy blokadzie „od razu” ekran jest zasłaniany już przy wyjściu z aplikacji. Szyfrowanie kopii i ustawienie zdjęć z internetu przeniesione do tej samej kategorii. | Chromium: blokada przy starcie, zły PIN odrzucony, poprawny odblokowuje, ponowna blokada po wyjściu | `FLAG_SECURE` – świadomie nie (decyzja autora: zrzuty ekranu mają działać). Blokada jest zasłoną interfejsu, nie szyfrowaniem danych; dane w telefonie chroni szyfrowanie Androida. Biometria nietestowana na urządzeniu. |
| uuid | – | Zależność tylko narzędzi budowania (nie w APK) – do aktualizacji przy najbliższej aktualizacji Capacitor CLI. | – | – |

## Nowe przepływy danych (v48)

- `routing.openstreetmap.de` – tylko gdy użytkownik przy dorysowywaniu brakującego kawałka śladu włączy „Po ścieżkach”: wysyłane są współrzędne zaznaczonych punktów. Domyślnie wyłączone; dodane do `connect-src` w CSP.
- Bufor pozycji sprzed nagrywania (do 30 min) jest wyłącznie w pamięci, nigdzie nie zapisywany ani wysyłany.

## Czego nie sprawdzono

Testy na izolowanym telefonie z Androidem (N01–N13 z audytu): wykonanie w Android WebView, uprawnienia SAF po odłączeniu,
zachowanie na Androidzie 7–16, ruch sieciowy urządzenia, klawiatura testowa. Wszystkie testy powyżej to testy warstwy WWW
w Chromium na komputerze oraz przegląd kodu natywnego. Naprawiona wersja wymaga ponownej, niezależnej oceny.

## v49 – ustalenia wewnętrznego audytu v48

Wewnętrzny audyt (trzy niezależne instancje, perspektywa prywatności użytkownika) – 16 ustaleń. Decyzje autora i stan:

| # | Ustalenie | Stan w v49 |
| --- | --- | --- |
| 1 | Domyślna jawna autokopia w Dokumenty/OMM, kopie zdjęć | **Bez zmian (decyzja autora).** Dodane wyraźne ostrzeżenie w ustawieniach kopii, gdy szyfrowanie jest wyłączone. |
| 2 | Klucz podpisu w historii gita | **Rotacja klucza:** podpis v2 dotychczasowym kluczem, v3 nowym kluczem z lineage (`--rotation-min-sdk-version 28`); nowy klucz tylko w sekretach, CI sprawdza odciski obu kluczy w gotowym APK. README poprawione (klucz jest w historii) i z odciskami. |
| 3 | CI: ręczne uruchomienie z dowolnej gałęzi | Oba zadania (`apk`, `wydanie`) tylko dla `refs/heads/main`. `environment` z zatwierdzeniem – nie (decyzja autora). |
| 4 | Wtyczki `WebView` (podmiana ścieżki aplikacji) | Atrapa `OmmNoWebViewPlugin` pod nazwą „WebView” (rejestrowana po wbudowanych – nadpisuje) odrzuca wszystkie metody; `DisableDeploy=true` (config.xml przez `capacitor.config.json`) – zapisana ścieżka nie jest wczytywana przy starcie. `CapacitorHttp` zostaje (używany przez nazwy miejsc i mapy offline). |
| 5 | Licznik prób PIN tylko w pamięci, krótki PIN | Trwały licznik `omm-lock-f` (przeżywa restart), opóźnienie 30 s → 1 h; minimum 6 znaków; ostrzeżenie przed PIN-em telefonu. |
| 6 | Kopie awaryjne bez terminu | Termin 30 dni (`mapa-snap-*`, `mapa-uszkodzone-*`, ślady sprzed przywrócenia) + przycisk „Usuń kopie awaryjne teraz” w Prywatności. |
| 7 | Biometria bez klucza, klasa WEAK | Tylko BIOMETRIC_STRONG; klucz AES w AndroidKeyStore (`setUserAuthenticationRequired`, `setInvalidatedByBiometricEnrollment`) jako CryptoObject; sukces dopiero po udanym szyfrowaniu próbki. Nowy odcisk w telefonie → klucz unieważniony → wymagany PIN OMM, dopiero po nim nowy klucz. |
| 8 | Miniatura w „ostatnich aplikacjach” | `setRecentsScreenshotEnabled(false)` (Android 13+) przy włączonej blokadzie; zrzuty ekranu działają. |
| 9 | Nazwa miejsca: współrzędne ~1 m do Nominatim | Zaokrąglenie do 3 miejsc (~100 m). |
| 10 | Zbędne uprawnienia | Usunięte `SCHEDULE_EXACT_ALARM`, `RECEIVE_BOOT_COMPLETED` i odbiornik restartu powiadomień (`tools:node="remove"`); powiadomienia jako niedokładne. `READ_EXTERNAL_STORAGE` (≤ Android 12) i `requestLegacyExternalStorage` zostają (zapis w Dokumenty/OMM na starszych Androidach). `taskAffinity=""`. |
| 11 | `__foto` w kopii JSON | Dane wewnętrzne ZIP w `WeakMap` (nie w obiekcie z pliku); zdjęcia: tylko Blob i nazwa `^[\w.-]{1,120}$`. |
| 12 | Import GPX przed walidacją; `constructor` jako kolor | `cleanTrkDB` przed zapisem; słowniki `Object.create(null)`. |
| 13 | Limity ZIP | 128 MB/wpis, 1 GB łącznie; zdjęcia z kopii czytane dopiero przy zapisie po zgodzie. |
| 14 | Gradle bez weryfikacji integralności | Nie zmieniono (brak dostępu do sum dystrybucji w środowisku budowania) – do zrobienia. |
| 15 | Leaflet z CDN bez SRI (wersja WWW) | Dodane `integrity` + `crossorigin`. |
| 16 | Informacyjne | `taskAffinity` – zrobione; reszta bez zmian. |

Czego nie sprawdzono: rotacja podpisu i biometria z kluczem wymagają testu na telefonie (aktualizacja v48 → v49 na Androidzie 8 i 9+; dodanie odcisku po włączeniu blokady).
