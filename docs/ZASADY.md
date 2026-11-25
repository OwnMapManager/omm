# Zasady pracy nad OMM

Stałe zasady ustalone przez Pawła (autora OMM). Repozytorium jest źródłem prawdy – zapiski asystenta AI są tylko roboczym
notesem. Zmiana zasady = zmiana tego pliku (z datą w DECYZJE.md).

## Jak wprowadzamy zmiany
- Uwagi, błędy i pomysły najpierw trafiają na listę roboczą z diagnozą. Wdrożenie dopiero po słowie **„działaj”**
  („ok”, „podoba mi się” = zgoda, ale jeszcze nie polecenie wdrożenia). Nowa wersja nigdy bez „działaj” – także przy
  oczywistych błędach.
- Po „działaj”: analiza, pytania **po jednym**, wdrożenie, testy, wydanie.
- Każde wydanie: podbity `APP_VER`, wypchnięte na `main`, poczekać na CI, sprawdzić APK, podać link
  `https://github.com/OwnMapManager/omm/releases/download/vNN/omm-vNN.apk`.
- Przy każdej większej zmianie: pomoc PL/EN, słownik EN, samouczek, „Jak zacząć” i karty startowe.
- Zmiana wyglądu w jednym miejscu = ta sama zmiana we wszystkich podobnych miejscach. Jedna funkcja = jedna ikona.
  Mapy i Ślady mają ten sam układ funkcji.
- Przy nowej funkcji najpierw rozpoznanie, jak robią to dobre aplikacje, i od razu kompletne rozwiązanie.
  Bierzemy zasady, nie kolorystykę 1:1; w aplikacji nie piszemy, czym się inspirowano.
- Robić porządnie i ładnie, nie po łebkach. Gdy trzeba czekać – aplikacja zawsze pokazuje, co robi.
- Propozycje wyglądu – opis słowami; makiety/strony tylko po zapytaniu Pawła.

## Zatwierdzone elementy (ochrona)
- Paweł przegląda aplikację etapami. To, co oznaczy „zostawiamy”, trafia do `docs/ZATWIERDZONE.md` (co, jak działa, data,
  etap) i do testów w `tests/omm/` (zachowanie + zrzut ekranu w rozmiarze telefonu).
- Testy uruchamia CI przed każdym wydaniem – nieudany test blokuje wydanie.
- Zmiana, która dotyka zatwierdzonego elementu: najpierw powiedzieć Pawłowi „to rusza zatwierdzone X” i czekać na zgodę.
  Dopiero wtedy wolno zaktualizować wzorzec (`node tests/omm/run.mjs --aktualizuj`).
- Rzeczy niesprawdzalne w symulatorze (GPS, okna Androida, dźwięk) – krótka lista do ręcznego sprawdzenia tylko przy
  zmianach w ich okolicy.

## Wygląd i zachowanie
- Jeden wygląd: „Mapa i krawędzie”. Mapa gra pierwsze skrzypce: jasny dzień, obowiązkowo noc, slim, niski kontrast.
- Bez poświat i bez „mgły” (przejść do przezroczystości). Jedyny efekt: równe tło minimalnie przezroczyste z rozmyciem
  tego, co pod spodem, i wyraźna krawędź (v134).
- Kolor tylko tam, gdzie niesie informację.
- Żadnych jednorazowych okien „Nowość…” przy starcie. W czasie jazdy, nawigacji i nagrywania żadnych okien z pytaniami
  ani podpowiedzi zasłaniających przyciski.
- W nawigacji ona jest najważniejsza; bez powiadomień systemowych o zejściu z trasy (samochód/rower – przeliczenie).
- Działam na mapie → widzę i zmieniam mapę; w Śladach → pracuję na śladach.
- Słownictwo: „Nawiguj/Nawigacja”, „Ślady”, „Wczytaj plik / Wczytaj GPX”, „Tryb pracy: Prywatny / Praca”, „Trening”.
- Nie ruszać: ikony aplikacji, ikon punktów (ICONS), ikon dyscyplin sportu.

## Bezpieczeństwo
- Klucze podpisu wyłącznie w sekretach GitHuba. SMS tylko przez wklejanie. Adres e-mail użytkownika prywatny.
- Zmiany natywne, uprawnienia i wtyczki wchodzą od razu (po „działaj”), a każda trafia do pliku `docs/natywne-vNN-do-audytu.md`
  na następny audyt.
- Kod uruchamiany przy starcie nie może używać `const` zadeklarowanych niżej (TDZ). Test startu zawsze z nakładkami.
