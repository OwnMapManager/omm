# Zmiany natywne v139 – do audytu przy następnej paczce

## OmmSys – powody zamknięcia i pamięć (pkt 471)
Plik: `android/app/src/main/java/io/github/ownmapmanager/omm/OmmSysPlugin.java`

- `exits` – `ActivityManager.getHistoricalProcessExitReasons(pakiet OMM, 0, 8)` (Android 11+, starsze: pusta lista).
  Zwraca czas, powód, status, ważność, PSS/RSS (MB), opis systemu i nazwę procesu. Tylko własny pakiet – bez uprawnień.
- `memory` – `ActivityManager.getMemoryInfo` (wolna/całkowita pamięć telefonu, flaga „mało pamięci”) + PSS procesu OMM.

## MainActivity – onTrimMemory
Plik: `android/app/src/main/java/io/github/ownmapmanager/omm/MainActivity.java`

- `onTrimMemory(level)` → `bridge.triggerWindowJSEvent("ommtrim", {level})`; strona zwalnia kafle mapy.
  W `try/catch` – brak mostu nie przerywa obsługi systemu.

Bez nowych uprawnień w manifeście. Do sprawdzenia: opis (`getDescription`) trafia tylko do lokalnego dziennika
Diagnostyki (wysyłany wyłącznie ręcznie przez „Wyślij raport”).
