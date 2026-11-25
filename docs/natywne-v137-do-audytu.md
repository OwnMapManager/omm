# Zmiany natywne v137 – do audytu przy następnej paczce

## OmmPhotos – automatyczne zdjęcia z wybranego folderu (pkt 467)
Plik: `android/app/src/main/java/io/github/ownmapmanager/omm/OmmPhotosPlugin.java`

Nowe metody:
- `pickFolder` – systemowe okno wyboru folderu (`ACTION_OPEN_DOCUMENT_TREE`, start w DCIM/Camera).
  Wybrany folder: `takePersistableUriPermission(..., FLAG_GRANT_READ_URI_PERMISSION)` – stały dostęp **tylko do odczytu**
  i **tylko do tego folderu**. Przed pierwszym wyborem – jak w `pick` – prośba o `ACCESS_MEDIA_LOCATION`.
- `scanFolder` – lista plików `image/*` w folderze (bez podfolderów, bez kopiowania): id dokumentu, nazwa, czas zmiany,
  rozmiar; tylko nowsze niż `since`, najwyżej 300 najnowszych. Odmowa, gdy dostęp do folderu wygasł / został cofnięty.
- `readFiles` – kopie wskazanych dokumentów (najwyżej 300 na wywołanie) do `cache/omm-foto` (jak `pick`: oryginał
  z lokalizacją przez `MediaStore.getMediaUri` + `setRequireOriginal`, limit 80 MB na plik) + współrzędne z EXIF.
- `forgetFolder` – `releasePersistableUriPermission` przy usunięciu źródła.

Bez nowych uprawnień w manifeście. Świadomie BEZ `READ_MEDIA_IMAGES` (dostęp do całej galerii) – zasady Google Play
dla uprawnień do zdjęć i mniejszy zakres danych. Adres folderu (`content://…`) zapisany w `omm-autofoto`
(localStorage, wykluczony z kopii zapasowej – `PREFS_SKIP`, bo dostęp dotyczy tylko tego telefonu).

Do sprawdzenia: id dokumentów z niezaufanego dostawcy (inna aplikacja „Pliki”) są używane tylko w
`DocumentsContract.buildDocumentUriUsingTree(tree, id)` w obrębie przyznanego drzewa; nazwy plików przechodzą przez
`safe()` przed zapisem w pamięci podręcznej.
