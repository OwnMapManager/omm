# Testy bezpieczeństwa (regresja audytu v45)

Testy w prawdziwej przeglądarce Chromium (Playwright) na danych syntetycznych z katalogu `dane/`.
Uruchomienie: `npm run www`, serwer `cd www && python3 -m http.server 8765`, potem:

    python3 tests/bezpieczenstwo/test_import.py   # F01, F02, F03, F06, F07 (wstrzykiwanie przez kopie, CRC, nazwy warstw)
    python3 tests/bezpieczenstwo/test_kopie.py    # szyfrowanie kopii, anulowanie bez skutków, kolizje zdjęć, zdjęcia z internetu
    python3 tests/bezpieczenstwo/test_csp.py      # aplikacja działa pod ścisłą CSP, wstrzyknięty atrybut zdarzenia jest blokowany

To testy warstwy WWW w przeglądarce na komputerze – nie zastępują testów na izolowanym telefonie z Androidem.
Pliki w `dane/` zawierają celowo spreparowane wartości (ustawiają tylko znacznik w pamięci testu) – nie importuj ich do używanej aplikacji.
