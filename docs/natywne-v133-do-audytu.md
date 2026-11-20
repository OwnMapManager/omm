# Zmiany natywne v133 – do audytu przy następnej paczce

Paweł 19.11.2026: zmiany natywne wchodzą od razu, audyt później. Lista do sprawdzenia:

1. **`OmmSysPlugin.java` (nowa wtyczka „OmmSys”)** – bez uprawnień.
   - `audio()` – odczyt głośności `STREAM_MUSIC`, jej maksimum i trybu dzwonka (`AudioManager`); OMM ostrzega na starcie
     nawigacji, gdy głośność multimediów = 0 (pkt 454). Tylko odczyt, nic nie zmienia.
   - `lastGone()` – zwraca i kasuje znacznik z `SharedPreferences("omm-sys")` o zamknięciu silnika strony.
2. **`MainActivity.java` – `WebViewListener.onRenderProcessGone`** (pkt 440): gdy Android zamknie proces renderera WebView
   (brak pamięci / awaria), zamiast zamknięcia całej aplikacji: zapis znacznika (czas, awaria tak/nie, licznik),
   usunięcie martwego WebView i `recreate()` aktywności. Ryzyko do oceny: pętla odtwarzania przy powtarzającej się awarii
   renderera (licznik `goneN` w dzienniku OMM).
3. **`res/xml/network_security_config.xml` + `android:networkSecurityConfig` w manifeście** (pkt 424): bazowo tylko
   https i systemowe certyfikaty (jak wcześniej `usesCleartextTraffic=false`). W CI `scripts/geoportal-ca.sh` pobiera
   łańcuch `mapy.geoportal.gov.pl` (z serwera + „CA Issuers”) i dopisuje certyfikaty WYSTAWCÓW (bez certyfikatu serwera)
   jako dodatkowe zaufane **tylko dla tej jednej domeny** (`res/raw/geoportal_ca.pem`, `domain-config` bez subdomen).
   Do oceny: pobieranie certyfikatów przy budowaniu (TOFU w CI) – log kroku wypisuje podmioty i daty ważności.
