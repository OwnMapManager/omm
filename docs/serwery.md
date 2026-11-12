# Serwery, z którymi łączy się OMM (stan v120)

Lista CSP `connect-src` (scripts/prepare-www.mjs) obejmuje tylko zapytania z WebView (`fetch`). Część zapytań w aplikacji na Androida
idzie natywnie (CapacitorHttp) i CSP ich nie widzi – dlatego pełny spis jest tutaj.

## Podkłady i nakładki mapy (obrazki kafli – `img-src`)

| Serwer | Po co | Co wysyła |
|---|---|---|
| tile.openstreetmap.org | podkład OSM | numery kafli (obszar oglądanej mapy) |
| mt0–3.google.com | satelita i hybryda Google | numery kafli |
| tile.openmaps.fr (OpenTopoMap), *.tile-cyclosm.openstreetmap.fr | podkłady turystyczne | numery kafli |
| mapy.geoportal.gov.pl, integracja.gugik.gov.pl, services.gugik.gov.pl | ortofotomapy, mapy topograficzne, NMT, działki (Geoportal) | prostokąt oglądanej mapy |
| server.arcgisonline.com | Esri – satelita | numery kafli |
| tiles.maps.eox.at | Sentinel-2 bez chmur | prostokąt oglądanej mapy |
| basemap.nationalmap.gov | USGS (USA) | numery kafli |
| tile.waymarkedtrails.org | szlaki piesze i rowerowe | numery kafli |
| mapserver.bdl.lasy.gov.pl | Bank Danych o Lasach (siedliska, drzewostany) | prostokąt mapy, punkt dotknięcia |

## Dane (`connect-src` i zapytania natywne)

| Serwer | Po co | Co wysyła | Droga |
|---|---|---|---|
| tiles.openfreemap.org | kafle wektorowe – zieleń do tras, rodzaje miejsc w Szukaj | numery kafli | fetch |
| overpass-api.de | zieleń – tylko zapasowo, gdy kafli wektorowych brak (od v119 bez luster kumi.systems i private.coffee) | prostokąt obszaru | natywnie (CapacitorHttp) albo fetch |
| valhalla1.openstreetmap.de | trasy (Nawiguj), dociąganie śladów do ścieżek | punkty trasy / punkty śladu | fetch |
| brouter.de | trasy przez zieleń | punkty trasy | natywnie albo fetch |
| routing.openstreetmap.de | trasy zapasowo (OSRM), „wzdłuż dróg” przy rysowaniu | punkty trasy | fetch |
| nominatim.openstreetmap.org | wyszukiwanie miejsc, nazwy miejsc śladów i pogody | tekst wyszukiwania / punkt | fetch |
| api.met.no | pogoda | punkt (zaokrąglony) | natywnie albo fetch |
| raw.githubusercontent.com | informacja o nowej wersji | — | fetch |

## Inne
- github.com – link do pobrania nowej wersji (otwiera przeglądarkę).
- Kopia na Dysk Google – przez systemowy wybór folderu (SAF), OMM nie łączy się z Google bezpośrednio.
- Poczta zgłoszeń – zgłoszenie wysyła sam użytkownik (mailto / Udostępnij); dziennik w zgłoszeniu ma wycięte współrzędne i numery kafli (v119).
