# OMM v66 – naprawy po niezależnym audycie v2 (badana wersja v56)

Audyt: 12.10.2026, APK v56 SHA-256 `54b0bc5380a102f46a0ed8b61a46e97f5b968dbe7fda4af5bb3568d9aa132cfd`, commit `786164d`.
Wszystkie ustalenia sprawdzono w kodzie v65 – były nadal aktualne. Poprawki w v66 (plik `app/omm.html`, chyba że napisano inaczej).
Testy fragmentów wykonano w Chromium (Playwright) na całej aplikacji z syntetycznymi danymi, bez sieci zewnętrznej.
Testy urządzenia (Android, WebView, SAF, PCAP) nadal są do wykonania – patrz „Ograniczenia”.

## Ustalenia F01–F07

| ID | Zmiana | Gdzie | Test (wynik) | Ograniczenia |
| --- | --- | --- | --- | --- |
| F01 | Trzy stany szyfrowania: wyłączone / włączone i gotowe / nieznany. Znacznik `omm-szyfr-wl` (localStorage) zapisywany przy włączeniu i usuwany tylko przy świadomym wyłączeniu. `encWrap` używa `encGet(true)`: błąd IDB, brak klucza przy znaczniku albo uszkodzony wpis → wyjątek, żaden plik nie powstaje. Autokopia zapisuje błąd i pokazuje go (przy „Zrób kopię teraz” – okno z przyczyną); ręczny eksport też przerywa | `encGet`, `encWrap`, `encOff`, `autoBackup`, `projb` | Błąd IDB bez znacznika → wyjątek; znacznik bez klucza → wyjątek; poprawny klucz → kopia zaszyfrowana; świadomie wyłączone → zwykły ZIP; `zipBackup` z błędem IDB → wyjątek (wszystkie zgodne) | Nie badano zachowania WebCrypto na różnych WebView. Kopie sprzed v66 z kluczem bez znacznika – znacznik ustawia się przy pierwszym poprawnym odczycie |
| F02 | Jedna weryfikacja PIN-u naraz (`lockAttempt`) dla ekranu blokady i zmiany blokady; pole i przycisk wyłączone podczas sprawdzania; licznik czytany ponownie po `await` | `lockAttempt`, `lockShow.tryPin`, `lockVerify` | 10 równoczesnych wywołań: 1 sprawdzone, 9 odrzuconych jako „trwa sprawdzanie”, licznik 1; kolejne 5 prób → licznik 5 i opóźnienie | Nie mierzono czasu PBKDF2 na telefonie |
| F03 | Nowa nazwa zdjęcia przy kolizji zawsze ≤ 120 znaków i sprawdzona jako wolna w bazie oraz w bieżącej paczce; po 50 nieudanych losowaniach przywracanie przerywa się | `fotoCommit` | Nazwa 120 znaków → nowa nazwa 120 znaków, referencja przechodzi `okPh`, stare zdjęcie bez zmian; wymuszona druga kolizja → istniejący blob bez zmian, zdjęcie pod trzecią nazwą | – |
| F04 | **Nie w v66** (decyzja autora): docelowo szyfrowanie kopii porcjami (każdy element osobno, własny IV, znacznik kolejności, zaszyfrowany spis z sumami, odczyt starego formatu) | – | – | Do czasu wdrożenia zaszyfrowana kopia > ~128 MiB nie przejdzie własnego importera |
| F05 | `abRelease` sprawdza `ok`; błąd albo `ok:false` → komunikat, URI trafia na listę `omm-dysk-zwolnij` i jest ponawiane przy starcie | `abRelease` | Logika sprawdzona przeglądem kodu (w przeglądarce brak wtyczki natywnej) | Wymaga testu na Androidzie: lista `persistedUriPermissions` po sukcesie i błędzie |
| F06 | `parseCoords`: liczby muszą być całe (`(?!\d)`), minuty/sekundy < 60, stopnie z ułamkiem nie mogą mieć minut, minus razem z literą półkuli = odrzucenie. `findCoords`: granice liczb (`(?<![\d.,-])…(?!\d)`), zakresy DMS | `parseCoords`, `findCoords` | `151.123, 19.123` i `-151.123, 19.123` → odrzucone; `51°99'00"N…` → odrzucone; `51 75 00 N…` → odrzucone; poprawne zapisy (dziesiętne, z przecinkiem, DMS, minuty dziesiętne, linki OSM, geo:) → przyjęte | Przy kilku parach w tekście nadal bierzemy pierwszą |
| F07 | Tekst, w którym `parseCoords` lub `findCoords` znajduje pozycję, obsługiwany lokalnie – Enter i przycisk pokazują pozycję, do Nominatim nie idzie nic. Teksty dłuższe niż 150 znaków nie są wysyłane | `searchDlg.web` | Wiadomość z audytu (T16) + Enter: 0 żądań do Nominatim, okno zamknięte, mapa na 51.123, 19.123 | – |

## Ryzyka R01–R07

| ID | Zmiana | Gdzie | Test (wynik) | Ograniczenia |
| --- | --- | --- | --- | --- |
| R01 | Kopie automatyczne i plik awaryjny nagrania domyślnie w prywatnej pamięci OMM (`Directory.DATA`); folder publiczny tylko z wyboru. Jawne kopie zdjęć (OMM/zdjecia) i oryginały w galerii (Obrazy/OMM) – osobne opcje, domyślnie wyłączone. Przywracanie z listy kopii w pamięci OMM. Jednorazowa informacja dla dotychczasowych użytkowników (stare kopie zostają tam, gdzie były) | `abFolder`, `recFolder`, `fotoPut`, `fotoAdd`, `abRestoreDlg`, ustawienia „Foldery i pliki” | Ustawienia i ścieżki sprawdzone w przeglądarce; zapis do `DATA` – do testu na Androidzie | Kopia na Dysku Google nadal jawna bez szyfrowania (ostrzeżenie w ustawieniach). Pamięć prywatna znika przy odinstalowaniu (opisane w ustawieniach) |
| R02 | Przywracanie pełnej kopii: najpierw zapis map (`save()` sprawdzany), potem ślady (`trkReplace` awaitowane, zwraca wynik zapisu); każdy błąd → powrót do poprzednich map i śladów i komunikat; „Kopia przywrócona” tylko po sukcesie | `restoreBuf`, `trkReplace`, `trkSave` | Wymuszony błąd zapisu `mapa-v2`: dane bez zmian, komunikat o przerwaniu | Brak testu odcięcia zasilania |
| R03 | Błąd odczytu zdjęcia przerywa kopię; zdjęcie, którego nie ma w bazie, jest zapisywane w spisie (`brakZdjec`) i zgłaszane | `zipBackup` | Przegląd kodu + pełna kopia w teście F01 | – |
| R04 | Bez zmiany natywnej: poprzednia dobra generacja jest w 7 lokalnych autokopiach; zapis na Dysk zawsze po udanej kopii lokalnej | – | – | Atomowa podmiana pliku SAF wymaga zmiany w `OmmBackupPlugin` → do następnego audytu |
| R05 | Bez zmiany w v66 (`useLegacyBridge` zostaje) – zmiana mostu wymaga testów zgodności wtyczek na urządzeniu | – | – | Do następnego audytu |
| R06 | Bez zmiany – rotacja klucza jak w v49+ | – | – | – |
| R07 | CI: Gradle buduje bez dostępu do sekretów; osobny krok podpisu (apksigner). Brak któregokolwiek z czterech sekretów (stary i nowy klucz) przerywa wydanie. `gradle-wrapper.jar` sprawdzany akcją `gradle/actions/wrapper-validation` (przypięty SHA), dystrybucja Gradle – oficjalną sumą SHA-256 (`distributionSha256Sum`). `uuid` podniesione do 11.1.1 (override) – GHSA-w5hq-g745-h8pq | `.github/workflows/android.yml`, `package.json` | Suma `gradle-wrapper.jar` (`7d3a4ac4…6172`) zgodna z oficjalną listą dla 8.14.3 | Suma dystrybucji pobierana w CI z services.gradle.org – do przypięcia w repozytorium w następnej wersji |

## Pozostałe ustalenia z treści raportu

- `lockCfg`: uszkodzony wpis blokady zwraca `{broken:true}` – blokada zostaje, PIN nie przechodzi (wcześniej = blokada wyłączona). Test: uszkodzony JSON → `broken`, `lockCheck` → false.
- Kolejka nazw z miejscem (`nmWait`): przy wzorze nazwy bez `{miejsce}` oczekujące zapytania są kasowane przed wysłaniem.
- Aparat: `file_paths.xml` – dodany wąski wpis `external-files-path Pictures/` (tylko pliki tymczasowe zdjęcia z aparatu). Bez niego od v46 aparat się nie otwierał (FileProvider odmawiał). **Zmiana konfiguracji natywnej – do przejrzenia.**

## Nowe elementy do przejrzenia przy następnym audycie

- Tryb diagnostyczny nagrywania (domyślnie wyłączony): surowe pomiary GPS w IndexedDB (`recdiag/…`) i jako dodatkowy `<trk>` w eksporcie GPX (nie w kopii zapasowej).
- Wpłaty: numer konta i link buycoffee.to w „O OMM” (tylko tekst i link zewnętrzny).
- `leaflet-rotate` 0.2.8 (od v64) i prowadzenie w tle przez `BackgroundGeolocation` (od v65).
