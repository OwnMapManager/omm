# Zatwierdzone przez Pawła („zostawiamy”)

Każdy wpis jest chroniony testem w `tests/omm/` (zachowanie i/lub zrzut ekranu w rozmiarze telefonu, wzorce w
`tests/omm/zrzuty/`). CI uruchamia testy przed każdym wydaniem – nieudany test wstrzymuje wydanie.

Zmiana dotykająca wpisu poniżej: najpierw „to rusza zatwierdzone X” do Pawła i czekać na zgodę; dopiero potem nowy
wzorzec (`node tests/omm/run.mjs --aktualizuj`) i data zmiany przy wpisie.

## Wpis – wzór
```
### <nazwa> – etap <N>, <data>
Co: …
Jak działa: …
Test: tests/omm/testy/<plik> › <nazwa testu>; zrzut: tests/omm/zrzuty/<nazwa>.png
Ręcznie (tylko przy zmianach w okolicy): …
```

## Lista
_(na razie pusta – Paweł zaczyna przegląd etapami)_
