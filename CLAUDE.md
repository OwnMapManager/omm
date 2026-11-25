# OMM – instrukcja dla asystenta AI (czytać na początku każdej sesji)

1. Przeczytaj `docs/ZASADY.md`, `docs/DECYZJE.md`, `docs/ARCHITEKTURA.md` i `docs/ZATWIERDZONE.md`. Repozytorium jest
   źródłem prawdy; Twoje notatki są tylko roboczym notesem.
2. Rozmawiaj z Pawłem po polsku. Uwagi najpierw na listę roboczą z diagnozą; wdrażaj dopiero po „działaj”; pytania po jednym.
3. Cały kod: `app/omm.html`. Po zmianie:
   - `node tests/omm/run.mjs` (wszystko musi przejść; nowe napisy `_t('…')` potrzebują tłumaczenia EN),
   - pomoc PL/EN, samouczek, karty startowe, słownik EN – jeśli zmiana je dotyczy,
   - podbij `APP_VER`, wypchnij na `main`, poczekaj na CI, sprawdź APK, podaj link do wydania.
4. Zmiana dotyka czegoś z `docs/ZATWIERDZONE.md` → najpierw zapytaj Pawła („to rusza zatwierdzone X”).
5. Zmiana liczenia śladów → podbij `TRK_ALG` i porównaj ze wzorcami (ARCHITEKTURA.md → Ślady GPS).
6. Zmiany natywne/uprawnienia → dopisz do `docs/natywne-vNN-do-audytu.md`.
7. Nowa decyzja → wiersz w `docs/DECYZJE.md` (data, decyzja, powód).
