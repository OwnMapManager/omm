// Źródło ikon OMM (geometria w siatce 24×24). Z tego pliku scripts/ikony-tusz.mjs generuje
// bloki IC i ICON w app/omm.html w stylu „Tusz z plamką” (v115, pkt 317): kreska z lekkim drżeniem ręki
// (rough.js, stałe ziarno – za każdym razem ten sam wynik) i plamka koloru w znaczącym miejscu (AKC).
// Nowa ikona: dopisz ją tutaj (IC albo NOWE), uruchom „node scripts/ikony-tusz.mjs” i zatwierdź zmiany w omm.html.
const svgi = d => d;
const RULER = '<path d="M3 15L15 3l6 6L9 21z"/><path d="M7 13l2 2M10 10l2 2M13 7l2 2"/>';
export const IC = {
  must: '<path d="M6 21V4"/><path d="M6 4.5h11.5l-2.4 4.2 2.4 4.3H6"/>', // v93: punkt obowiązkowy (flaga)
  car: '<path d="M4 16v-4l2-5h12l2 5v4z"/><path d="M4 12h16"/><circle cx="7.5" cy="16.5" r="1.8"/><circle cx="16.5" cy="16.5" r="1.8"/>',
  bell: '<path d="M6 16V11a6 6 0 0 1 12 0v5l1.5 2h-15z"/><path d="M10 20.5a2 2 0 0 0 4 0"/>',
  clock: '<circle cx="12" cy="13" r="8"/><path d="M12 9v4l2.5 2"/><path d="M5 4L2.5 6.5M19 4l2.5 2.5"/>',
  fields: '<rect x="3.5" y="4" width="17" height="16" rx="2"/><path d="M3.5 9.5h17M3.5 15h17M9.5 4v16"/>',
  imgmap: '<path d="M3 6l6-2 6 2 6-2v14l-6 2-6-2-6 2z"/><path d="M9 4v14M15 6v14"/><circle cx="18" cy="17" r="3.5" fill="currentColor" stroke="none" opacity=".35"/>',
  flame: '<path d="M12 21c-4 0-6.5-2.6-6.5-6 0-3.6 3-5.5 3.5-9 2 1.5 3 3.5 3 5.5 1-.8 1.6-2 1.8-3.2C16.6 10 18.5 12.6 18.5 15c0 3.4-2.5 6-6.5 6z"/>',
  cluster: '<circle cx="8" cy="9" r="3"/><circle cx="16" cy="8" r="2.4"/><circle cx="14" cy="16" r="3.4"/>',
  moon: '<path d="M20 14.5A8.5 8.5 0 1 1 9.5 4a7 7 0 0 0 10.5 10.5z"/>',
  snd: '<path d="M4 9.5h3.5L12 5.5v13l-4.5-4H4z"/><path d="M15.5 9a4 4 0 0 1 0 6M18 6.5a7.5 7.5 0 0 1 0 11"/>',
  mute: '<path d="M4 9.5h3.5L12 5.5v13l-4.5-4H4z"/><path d="M16 9.5l5 5M21 9.5l-5 5"/>',
  share:
    '<circle cx="18" cy="5" r="2.6"/><circle cx="6" cy="12" r="2.6"/><circle cx="18" cy="19" r="2.6"/><path d="M8.3 10.8l7.4-4.3M8.3 13.2l7.4 4.3"/>',
  del: '<path d="M4 6.5h16M9.5 6.5V4.5h5v2M6.5 6.5l1 13.5h9l1-13.5"/><path d="M10 10v7M14 10v7"/>',
  edit: '<path d="M4 20l1-4.5L15.5 5l3.5 3.5L8.5 19z"/><path d="M13.5 7l3.5 3.5"/>',
  eye: '<path d="M2 12s3.6-6.5 10-6.5S22 12 22 12s-3.6 6.5-10 6.5S2 12 2 12z"/><circle cx="12" cy="12" r="3"/>',
  eyeOff:
    '<path d="M3 10c2.2 3 5.4 4.5 9 4.5s6.8-1.5 9-4.5"/><path d="M5.6 13.1L4 15.4M9.4 14.3l-.6 2.6M14.6 14.3l.6 2.6M18.4 13.1l1.6 2.3"/>',
  nav: '<path d="M12 3l7 17-7-4-7 4z"/>',
  split:
    '<circle cx="6" cy="6.5" r="2.6"/><circle cx="6" cy="17.5" r="2.6"/><path d="M8.2 8L20 18M8.2 16L20 6"/>',
  stats: '<path d="M4 20V11M10 20V5M16 20v-6M2.5 20h19"/>',
  dl: '<path d="M12 4v11M7.5 10.5L12 15l4.5-4.5"/><path d="M4 16v4h16v-4"/>',
  play: '<path d="M7 4.5v15l12-7.5z"/>',
  photo: '<path d="M3.5 8h3.8l1.8-2.6h5.8L16.7 8h3.8v11h-17z"/><circle cx="12" cy="13.3" r="3.5"/>', // jak przycisk 📷 na pasku
  move: '<path d="M8.5 12V5.8a1.4 1.4 0 0 1 2.8 0V11M11.3 10.5V4.4a1.4 1.4 0 0 1 2.8 0V11M14.1 10.8V6.2a1.4 1.4 0 0 1 2.8 0V12M16.9 11.2a1.4 1.4 0 0 1 2.8 0V15a6 6 0 0 1-6 6h-1.4a6 6 0 0 1-4.7-2.3l-3.1-3.9a1.4 1.4 0 0 1 2.1-1.8L8.5 15v-3"/>', // dłoń: chwyć i przesuń
  layer: '<path d="M12 3l9 5-9 5-9-5z"/><path d="M3 12.5l9 5 9-5"/>', // jak przycisk warstw
  pin: '<path d="M12 2.5a6 6 0 0 0-6 6c0 4.5 6 12 6 12s6-7.5 6-12a6 6 0 0 0-6-6z"/><circle cx="12" cy="8.5" r="2"/>',
  restore: '<path d="M4.5 12a7.5 7.5 0 1 0 2.2-5.3L4.5 9"/><path d="M4.5 4.5V9H9"/>',
  back: '<path d="M9 14L4 9l5-5"/><path d="M4 9h10.5a5.5 5.5 0 0 1 0 11H11"/>',
  pause: '<path d="M8.5 5v14M15.5 5v14"/>',
  tag: '<path d="M3.5 12.5V4h8.5l9 9-8.5 8.5z"/><circle cx="8" cy="8.5" r="1.5"/>',
  note: '<path d="M4 5h16v11H9l-5 4z"/>',
  save: '<path d="M5 4h11l3 3v13H5z"/><path d="M8 4v5h7V4M8 20v-6h8v6"/>', // jak „Kopia” w panelu map
  up: '<path d="M12 19V5M6 11l6-6 6 6"/>',
  link: '<path d="M10 14a4 4 0 0 0 5.7 0l3-3a4 4 0 0 0-5.7-5.7l-1.1 1.1"/><path d="M14 10a4 4 0 0 0-5.7 0l-3 3a4 4 0 0 0 5.7 5.7l1.1-1.1"/>', // v89: pokaż też na innej mapie (odnośnik)
  trip: '<path d="M12 21.5V3"/><path d="M12 4.5h6.5l2.2 2.3-2.2 2.3H12"/><path d="M12 11.5H5.5l-2.2 2.3 2.2 2.3H12"/><path d="M9 21.5h6"/>', // v89: wycieczka (drogowskaz)
  snap: '<path d="M7 3.5v6a5 5 0 0 0 10 0v-6"/><path d="M7 3.5h3.2v6a1.8 1.8 0 0 0 3.6 0v-6H17" /><path d="M7 6.2h3.2M13.8 6.2H17" stroke-width="3"/><path d="M3 21c3-2.5 6-3 9-3s6 .5 9 3" stroke-dasharray="2 2.3"/><path d="M12 15.5v2" />', // v89: magnes przyciąga ślad do ścieżki
  extend: '<path d="M3 18c3-1 4-5 7-6" stroke-dasharray="2.2 2.6"/><path d="M10 12c3-1 4-6 8-7"/><path d="M16 15h6M19 12v6"/>', // dorysuj brakujący kawałek śladu
  merge: '<path d="M5 4v5c0 3 3 4 7 6M19 4v5c0 3-3 4-7 6v5"/><path d="M9.5 18l2.5 2.5 2.5-2.5"/>', // scal ślady
  down: '<path d="M12 5v14M6 13l6 6 6-6"/>',
  color:
    '<path d="M12 3a9 9 0 1 0 0 18c1.2 0 1.8-.8 1.8-1.7 0-1-.8-1.4-.8-2.3 0-1 .8-1.7 1.8-1.7H17a4 4 0 0 0 4-4C21 6.7 17 3 12 3z"/><circle cx="7.5" cy="11" r="1"/><circle cx="10" cy="7" r="1"/><circle cx="14.5" cy="7" r="1"/>',
  // panel map i warstw (v50) – jeden zestaw ikon dla całej aplikacji
  gear: '<path d="M21.1 10.4 21.1 13.6 18.7 14.1 18.2 15.3 19.5 17.3 17.3 19.5 15.3 18.2 14.1 18.7 13.6 21.1 10.4 21.1 9.9 18.7 8.7 18.2 6.7 19.5 4.5 17.3 5.8 15.3 5.3 14.1 2.9 13.6 2.9 10.4 5.3 9.9 5.8 8.7 4.5 6.7 6.7 4.5 8.7 5.8 9.9 5.3 10.4 2.9 13.6 2.9 14.1 5.3 15.3 5.8 17.3 4.5 19.5 6.7 18.2 8.7 18.7 9.9z"/><circle cx="12" cy="12" r="3.2"/>', // ustawienia
  // v79: narzędzia poprawki śladu – ślad + symbol czynności (czerwone = usuwanie)
  tdRm: '<path d="M2 19c3-1 6-4 10-4s7 2 10 1"/><circle cx="12" cy="15" r="2.3" fill="currentColor"/><path d="M9 3.5l6 6M15 3.5l-6 6" stroke="#d93025" stroke-width="2.2"/>',
  tdCutout: '<path d="M2 20c1.5 0 2.5-.7 3.5-1.7M18.5 18.3c1 1 2 1.7 3.5 1.7"/><path d="M5.5 18.3c3.5-3 9.5-3 13 0" stroke-width="3" opacity=".45"/><circle cx="5.5" cy="18.3" r="1.5" fill="currentColor"/><circle cx="18.5" cy="18.3" r="1.5" fill="currentColor"/><path d="M8 5.5h8M10.5 5.5V4h3v1.5M9.3 5.5l.6 6.5h4.2l.6-6.5" stroke="#d93025"/>',
  tdCut: '<path d="M2 20c2.5-.5 4-2 5.5-3.5M13 11c2.5-2.5 5-5 9-6"/><circle cx="7.5" cy="6.5" r="2.3"/><circle cx="16.5" cy="17.5" r="2.3"/><path d="M9.2 8.2l10.3 4.8M14.8 15.8L9.5 5.5"/>',
  tdJoin: '<path d="M2 18c1.8 0 3-.6 4.5-1.2M22 18c-1.8 0-3-.6-4.5-1.2"/><circle cx="7.5" cy="16.5" r="1.9" fill="currentColor"/><circle cx="16.5" cy="16.5" r="1.9" fill="currentColor"/><path d="M10 16.5h4" stroke-dasharray="1.2 1.8"/><path d="M5 7.5h4.5M7.8 5.3l2.2 2.2-2.2 2.2M19 7.5h-4.5M16.2 5.3L14 7.5l2.2 2.2"/>',
  tdDraw: '<path d="M2 20.5c2-.5 3.5-1.6 5-2.8"/><circle cx="7.8" cy="17" r="1.9" fill="currentColor"/><path d="M10 15.3c1.8-1.2 3-2.6 4-4" stroke-dasharray="1.6 2.2"/><path d="M14.6 10.6l5.6-5.6 2.2 2.2-5.6 5.6-3 .8z"/>',
  tdNote: '<path d="M12 21.5s-6.5-5.7-6.5-10.5a6.5 6.5 0 0 1 13 0c0 4.8-6.5 10.5-6.5 10.5z"/><path d="M9.3 9.3h5.4M9.3 12.2h3.6"/>',
  info: '<circle cx="12" cy="12" r="9"/><path d="M12 11v6"/><circle cx="12" cy="7.6" r=".6" fill="currentColor"/>', // o aplikacji
  x: '<path d="M6.5 6.5l11 11M17.5 6.5l-11 11"/>', // zamknij
  paste: '<rect x="5.5" y="4.5" width="13" height="16" rx="2"/><path d="M9 4.5V3h6v1.5M9 10h6M9 14h4"/>', // wklej ze schowka
  star: '<path d="M12 3.5l2.6 5.3 5.9.9-4.3 4.1 1 5.8L12 16.9l-5.2 2.7 1-5.8-4.3-4.1 5.9-.9z"/>', // domyślny (np. podkład)
  chev: '<path d="M7 10l5 5 5-5"/>', // rozwiń listę
  check: '<path d="M5 12.5l4.5 4.5L19 7.5"/>', // gotowe / zatwierdź
  swap: '<path d="M4 8h13l-3.5-3.5M20 16H7l3.5 3.5"/>', // v67: przełącz (kolorowanie śladu)
  fok: '<path d="M5.5 12.8l4.2 4.2 8.8-9.6" stroke-width="2.8"/>', // v67: ✓ na pasku rysowania (pogrubiony)
  fno: '<path d="M7 7l10 10M17 7L7 17" stroke-width="2.5"/>', // v67: ✕ na pasku rysowania
  fundo: '<path d="M8.5 6.5L4.5 10.5l4 4" stroke-width="2.3"/><path d="M4.5 10.5h9.5a5 5 0 0 1 0 10H11" stroke-width="2.3"/>', // v67: ↶ cofnij
  plus: '<path d="M12 5v14M5 12h14"/>',
  home: '<path d="M3.5 11.5L12 4l8.5 7.5"/><path d="M6 9.5V20h12V9.5"/><path d="M10 20v-5.5h4V20"/>', // tryb prywatny
  work: '<rect x="3.5" y="7.5" width="17" height="12" rx="2"/><path d="M9 7.5V5.5h6v2M3.5 12.5h17"/>', // tryb praca
  addLayer: '<path d="M12 3l8 4.5-8 4.5-8-4.5z"/><path d="M4 12.5l8 4.5 3-1.7"/><path d="M19 14v6M16 17h6"/>', // dodaj warstwę
  imp: '<path d="M12 15V4M7.5 8.5L12 4l4.5 4.5"/><path d="M4 15v5h16v-5"/>', // import (wczytaj plik)
  sel: '<rect x="4" y="4" width="16" height="16" rx="2"/><path d="M8 12.5l3 3 5-6"/>', // zaznaczanie
  filter: '<path d="M4 5h16l-6 7.5V18l-4 2v-7.5z"/>',
  folder: '<path d="M3.5 6.5h6l2 2.5h9v10.5h-17z"/>',
  lock: '<rect x="5" y="10.5" width="14" height="10" rx="2"/><path d="M8 10.5V7.5a4 4 0 0 1 8 0v3"/>',
  rec: '<circle cx="4.5" cy="19.5" r="1.6"/><path d="M6 18.5c3-1.5 2-6 6-7s5-3 6.2-5.3"/><circle cx="18.5" cy="5.5" r="2.9" fill="currentColor"/>', // nagrywanie śladu (v68: nagrywany ślad z kropką na końcu)
  route: '<path d="M4 19c4 0 3.5-7 8-7s4-7 8-7"/><circle cx="4" cy="19" r="1.4"/><circle cx="20" cy="5" r="1.4"/>', // ślady na mapie
  globe: '<circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3c3.2 3.2 3.2 14.8 0 18M12 3c-3.2 3.2-3.2 14.8 0 18"/>',
  copy: '<rect x="8.5" y="8.5" width="11.5" height="11.5" rx="2"/><path d="M15.5 8.5V4H4v11.5h4.5"/>',
  phone: '<path d="M6.5 3.5h3l1.5 4-2 1.3a11 11 0 0 0 6.2 6.2l1.3-2 4 1.5v3a2 2 0 0 1-2 2A16.5 16.5 0 0 1 4.5 5.5a2 2 0 0 1 2-2z"/>',
  gps: '<circle cx="12" cy="12" r="7"/><circle cx="12" cy="12" r="2.4" fill="currentColor"/><path d="M12 2v3M12 19v3M2 12h3M19 12h3"/>', // moja pozycja
  finger: '<path d="M12 11.5v4.5M8.7 19.5c.9-2 1-4.5 1-7.5a2.3 2.3 0 0 1 4.6 0c0 2.2 0 3.8-.5 5.5M6.2 16c.4-1.5.5-3 .5-4.5a5.3 5.3 0 0 1 10.6 0c0 1.5 0 3-.3 4.5M5.2 8a8.3 8.3 0 0 1 13.6 0"/>', // odcisk palca
  book: '<path d="M3.5 5.5c3-1.4 5.8-1.4 8.5 0 2.7-1.4 5.5-1.4 8.5 0V19c-3-1.4-5.8-1.4-8.5 0-2.7-1.4-5.5-1.4-8.5 0z"/><path d="M12 5.5V19"/>', // samouczek
  profile: '<path d="M3 4v16h18"/><path d="M6 15l4-5 3 3 5-7"/>', // profil wysokości
  offline: '<path d="M7 18.5h10a4 4 0 0 0 .6-7.95A6 6 0 0 0 6.2 10 4.3 4.3 0 0 0 7 18.5z"/><path d="M3.5 3.5l17 17"/>', // mapy offline (bez sieci)
  refresh: '<path d="M19.5 11A7.5 7.5 0 0 0 6 6.6L4 8.5"/><path d="M4 4v4.5h4.5"/><path d="M4.5 13A7.5 7.5 0 0 0 18 17.4l2-1.9"/><path d="M20 20v-4.5h-4.5"/>', // odśwież mapę
  map: '<path d="M3.5 6.5l5.5-2 6 2 5.5-2v13l-5.5 2-6-2-5.5 2z"/><path d="M9 4.5v13M15 6.5v13"/>', // mapa
  more: '<circle cx="12" cy="5.5" r="1.7" fill="currentColor"/><circle cx="12" cy="12" r="1.7" fill="currentColor"/><circle cx="12" cy="18.5" r="1.7" fill="currentColor"/>', // więcej opcji
  // nawigacja (v64): kompas (orientacja mapy), cała trasa, strzałki manewrów
  // v96: przycisk kompasu mapy – obwódka w kolorze przycisków, igła jak w zwykłym kompasie (północ czerwona, południe niebieska)
  compn: '<circle cx="12" cy="12" r="9.2" stroke-width="1.6"/><path d="M12 3.6L15 12H9Z" fill="#d32f2f" stroke="none"/><path d="M12 20.4L9 12H15Z" fill="#1e6fd9" stroke="none"/><circle cx="12" cy="12" r="1.1" fill="#fff" stroke="none"/>',
  compass: '<circle cx="12" cy="12" r="9"/><path d="M12 4.5l2.6 7.5H9.4z" fill="currentColor"/><path d="M12 19.5L9.4 12h5.2z"/>',
  fit: '<path d="M4 9V4h5M20 9V4h-5M4 15v5h5M20 15v5h-5"/><path d="M8 15c2-1 2-5 4-6s3 2 4 0"/>',
  mv_s: '<path d="M12 21V4M6 10l6-6 6 6"/>',
  mv_r: '<path d="M7 21v-8a4 4 0 0 1 4-4h9M16 5l4 4-4 4"/>',
  mv_l: '<path d="M17 21v-8a4 4 0 0 0-4-4H4M8 5L4 9l4 4"/>',
  mv_sr: '<path d="M9 21v-7l8-8M11 6h6v6"/>',
  mv_sl: '<path d="M15 21v-7L7 6M13 6H7v6"/>',
  mv_shr: '<path d="M8 21V7a3 3 0 0 1 5.4-1.8L19 14M19 8v6h-6"/>',
  mv_shl: '<path d="M16 21V7a3 3 0 0 0-5.4-1.8L5 14M5 8v6h6"/>',
  mv_u: '<path d="M16 21V9a4 4 0 0 0-8 0v6M4 12l4 4 4-4"/>',
  mv_rb: '<circle cx="12" cy="10" r="4.5"/><path d="M12 21v-6.5M15.2 6.8L19 3M15 3h4v4"/>',
  mv_end: '<path d="M6 21V4M6 4h11l-2.5 4 2.5 4H6"/>',
  // dyscypliny Sportu (v62) – rysowane jak reszta ikon, bez kolorów
  sp_bike: '<circle cx="5.5" cy="16.5" r="3.6"/><circle cx="18.5" cy="16.5" r="3.6"/><path d="M5.5 16.5L9 9.5h6.5l3 7M9 9.5l3.5 7h-7M12.5 16.5l3-7M8 6.5h3M15.5 9.5l-1-3h2.5"/>',
  sp_run: '<circle cx="15" cy="4.2" r="2"/><path d="M13.6 8.2L10.8 13.3l3.3 2.9-1.2 5.3M10.8 13.3l-3.6 1.6-2.4 3.4M7.8 9.8l3.2-1.8 2.6.2 2.4 3 3.2.6"/>',
  sp_walk: '<circle cx="12.5" cy="4" r="2"/><path d="M12.3 7.6l-.8 6.2 2.6 3.2.6 4.4M11.5 13.8L9.4 21.3M9.2 11.6l3.1-4 3.2 3.4 1.9.6"/>',
  sp_trek: '<circle cx="10.5" cy="3.9" r="2"/><path d="M10.4 7.6l-1 6 2.8 3 .5 4.7M9.4 13.6l-2 7.7M10.4 7.6l2.8 3.4 3.6-1.5M7.6 8.4L6 13.2l3 .6M17.4 8.2l1.6 13.3"/>',
  sp_horse: '<path d="M5.6 20.5C3.8 16.8 3.4 12.6 4.6 9.2a7.8 7.8 0 0 1 14.8 0c1.2 3.4.8 7.6-1 11.3h-3.6c1.7-3.3 2.1-6.6 1.4-9.2a4.4 4.4 0 0 0-8.4 0c-.7 2.6-.3 5.9 1.4 9.2z"/>',
  sp_skate: '<path d="M6.5 3h5v6.2l4.6 2c1.8.8 2.9 2.2 2.9 3.8v.5H6.5z"/><path d="M5 17.6h15M11.5 6h-2M11.5 8.6h-2"/><circle cx="7.6" cy="20" r="1.7"/><circle cx="12.4" cy="20" r="1.7"/><circle cx="17.2" cy="20" r="1.7"/>',
  sport: '<circle cx="6" cy="16.5" r="3.5"/><circle cx="18" cy="16.5" r="3.5"/><path d="M6 16.5l4-7h5l3 7M10 9.5L8.5 6.5H6.5M15 9.5l-1.8-3H16"/>', // tryb Sport (rower)
  bulb: '<path d="M9 18h6M10 21h4"/><path d="M12 3a6 6 0 0 0-3.6 10.8c.7.6 1.1 1.3 1.1 2.2h5c0-.9.4-1.6 1.1-2.2A6 6 0 0 0 12 3z"/>', // ekran nie gaśnie
  // v95: tryby jasności w prowadzeniu – zapalona (stale jasny, z promieniami, żółta), zgaszona (zwykły tryb telefonu),
  // pół na pół po skosie (przyciemnianie między manewrami)
  bulb_on:
    '<g stroke="#f2b01e"><path d="M12 3a6 6 0 0 0-3.6 10.8c.7.6 1.1 1.3 1.1 2.2h5c0-.9.4-1.6 1.1-2.2A6 6 0 0 0 12 3z"/><path d="M12 0.4V1.4M4.2 3L5.1 3.9M19.8 3L18.9 3.9M1.2 9H2.6M21.4 9H22.8"/></g><path d="M9 18h6M10 21h4"/>',
  bulb_off: '<g opacity=".75"><path d="M9 18h6M10 21h4"/><path d="M12 3a6 6 0 0 0-3.6 10.8c.7.6 1.1 1.3 1.1 2.2h5c0-.9.4-1.6 1.1-2.2A6 6 0 0 0 12 3z"/></g>',
  bulb_half:
    '<defs><clipPath id="bhL"><path d="M-2 -2H26L-2 26Z"/></clipPath><clipPath id="bhR"><path d="M26 -2V26H-2Z"/></clipPath></defs><g clip-path="url(#bhL)" stroke="#f2b01e"><path d="M12 3a6 6 0 0 0-3.6 10.8c.7.6 1.1 1.3 1.1 2.2h5c0-.9.4-1.6 1.1-2.2A6 6 0 0 0 12 3z"/><path d="M12 0.4V1.4M4.2 3L5.1 3.9M1.2 9H2.6"/></g><g clip-path="url(#bhR)" opacity=".75"><path d="M12 3a6 6 0 0 0-3.6 10.8c.7.6 1.1 1.3 1.1 2.2h5c0-.9.4-1.6 1.1-2.2A6 6 0 0 0 12 3z"/></g><path d="M3.5 20.5L20.5 3.5"/><path d="M9 18h6M10 21h4"/>',
  roadnav: '<path d="M8 21l2.5-18M16 21L13.5 3"/><path d="M12 6v2M12 11v2M12 16v2"/>', // prowadzenie po drogach
  straight: '<circle cx="5" cy="19" r="2"/><circle cx="19" cy="5" r="2"/><path d="M6.6 17.4l10.8-10.8" stroke-dasharray="2.6 2.6"/>', // v69: odcinek / trasa prosto (bez dróg)
  redo: '<path d="M15.5 6.5l4 4-4 4" stroke-width="2.3"/><path d="M19.5 10.5H10a5 5 0 0 0 0 10h3" stroke-width="2.3"/>', // v69: ↷ ponów
  fstart: '<path d="M6 21V4M6 4h11l-2.5 4L17 12H6"/>', // v69: start trasy
  ffin: '<path d="M6 21V4M6 4h12v8H6"/><path d="M10 4v8M14 4v8M6 8h12" stroke-width="1.2"/>', // v69: cel / meta
  swapv: '<path d="M7.5 4v16M4 16.5L7.5 20l3.5-3.5M16.5 20V4M13 7.5L16.5 4 20 7.5"/>', // v69: odwróć kierunek trasy
  loop: '<path d="M20 12a8 8 0 1 1-2.3-5.7"/><path d="M20 4v4.5h-4.5"/>', // v69: pętla – powrót do startu
  grip: '<path d="M8 7h8M8 12h8M8 17h8"/>', // v69: uchwyt – zmiana kolejności
  optim: '<circle cx="5" cy="18" r="2"/><circle cx="19" cy="18" r="2"/><path d="M7 18h2.5c2 0 2-5 4-5s2 5 3.5 5"/><path d="M12 3.5l1 2.2 2.2 1-2.2 1-1 2.2-1-2.2-2.2-1 2.2-1z"/>', // v72: ułóż kolejność punktów
  join: '<path d="M4 20c4-1 6-5 8-9s4-7 8-7"/><path d="M4 9l5 2.5L6.5 16"/>', // v69: dołącz do trasy
  recalc: '<path d="M3 17l2.5-3 2 2.5 2.5-4 2 3 2.5-3.5" stroke-dasharray="0" opacity=".55"/><path d="M3 11c3-4 7-4 10 0s6 3 8 0"/><path d="M17.5 4.5l1 2 2 1-2 1-1 2-1-2-2-1 2-1z" fill="currentColor" stroke="none"/>', // v70: przelicz ślad (wygładź z surowych pomiarów)
  plan: '<circle cx="6" cy="18" r="2.2"/><path d="M8.2 18H14a3 3 0 0 0 0-6h-4a3 3 0 0 1 0-6h5"/><path d="M15 3.5l3 2.5-3 2.5"/>', // zaplanuj trasę
  tools: '<path d="M14.5 6.5a4 4 0 0 0-5.3 5.3L4 17l3 3 5.2-5.2a4 4 0 0 0 5.3-5.3l-2.5 2.5-2.3-.7-.7-2.3z"/>', // narzędzia
  visited: '<circle cx="12" cy="12" r="9"/><path d="M8 12.5l3 3 5.5-6"/>', // odwiedzone
  gallery: '<rect x="3.5" y="4.5" width="17" height="15" rx="2"/><circle cx="9" cy="9.5" r="1.7"/><path d="M4 17.5l5-4.5 3.5 3 3-2.5 4.5 4"/>', // v105 (pkt 260): zdjęcia – ramka z górką i słońcem
  menu: '<path d="M4.5 7h15M4.5 12h15M4.5 17h15"/>', // v105: menu (lewy dolny róg)
  coffee: '<path d="M5 9.5h11v4.5a5 5 0 0 1-5 5h-1a5 5 0 0 1-5-5z"/><path d="M16 11h1.5a2.5 2.5 0 0 1 0 5H16M8.5 3.5c-.8 1 .8 2 0 3M12 3.5c-.8 1 .8 2 0 3"/>',
  dlapk: '<path d="M12 4v10M7.5 9.5 12 14l4.5-4.5"/><path d="M4.5 15v3.5h15V15"/>', // v66: pobierz najnowszą wersję
  bank: '<path d="M4 9.5 12 5l8 4.5"/><path d="M5.5 10v7M10 10v7M14 10v7M18.5 10v7M4 19h16"/>', // v66: wpłata na konto
};
export const ICON = {
  draw: svgi('<path d="M4 20l1-4L16 5l3 3L8 19z"/><path d="M14 7l3 3"/>'),
  data: svgi(RULER),
  pin: svgi(
    '<path d="M12 2a6 6 0 0 0-6 6c0 4.5 6 12 6 12s6-7.5 6-12a6 6 0 0 0-6-6z"/><circle cx="12" cy="8" r="2"/>'
  ),
  line: svgi('<path d="M4 19l6-11 5 6 5-10"/>'),
  circle: svgi('<circle cx="12" cy="12" r="8"/>'),
  poly: svgi('<path d="M5 18L4 8l9-4 7 7-4 8z"/>'),
  dot: svgi('<circle cx="12" cy="12" r="4.5" fill="currentColor"/>'),
  num: svgi('<circle cx="12" cy="12" r="9"/><path d="M10.5 9l2-1.5V17"/>'),
  seg: svgi(
    '<path d="M5 19L19 5"/><circle cx="5" cy="19" r="1.6" fill="currentColor"/><circle cx="19" cy="5" r="1.6" fill="currentColor"/>'
  ),
  arrow: svgi('<path d="M5 19L18 6"/><path d="M10 5.5h8.5V14"/>'),
  text: svgi('<path d="M5 7V4.5h14V7M12 4.5V20M9 20h6"/>'),
  measure: svgi(RULER),
  marea: svgi('<path d="M5 18L4 8l9-4 7 7-4 8z"/><path d="M8 14l4-4M10 16l6-6M7 11l3-3" opacity=".7"/>'),
  azimuth: svgi('<circle cx="12" cy="12" r="9"/><path d="M12 12l3-7"/><path d="M12 3v2"/>'),
  dh: svgi('<path d="M7 4v16M4 7l3-3 3 3M4 17l3 3 3-3M14 6h7M14 12h7M14 18h7"/>'),
  profile: svgi('<path d="M3 4v16h18"/><path d="M6 15l4-5 3 3 5-7"/>'),
  height: svgi('<path d="M2 20l7-12 4 7 3-4 6 9z"/>'),
  coord: svgi('<circle cx="12" cy="12" r="3"/><path d="M12 2v5M12 17v5M2 12h5M17 12h5"/>'),
  plan: svgi('<circle cx="6" cy="18" r="2"/><circle cx="18" cy="6" r="2"/><path d="M8 18h6a3 3 0 0 0 0-6h-4a3 3 0 0 1 0-6h6"/>'),
  azgo: svgi('<circle cx="12" cy="12" r="9"/><path d="M14.5 9.5l-1.5 4-3.5 1 1.5-4z"/><path d="M12 3v1.5M12 19.5V21M3 12h1.5M19.5 12H21"/>'),
  print: svgi('<path d="M7 9V3.5h10V9"/><rect x="3.5" y="9" width="17" height="8" rx="2"/><path d="M7 14h10v6.5H7z"/>'), // v72: wydruk w menu narzędzi
  photo: svgi('<path d="M3.5 8h3.8l1.8-2.6h5.8L16.7 8h3.8v11h-17z"/><circle cx="12" cy="13.3" r="3.5"/>')
};
// v115 (pkt 317): nowe kształty najczęściej widocznych ikon – zastępują te z IC / ICON o tej samej nazwie
const C = (cx, cy, r) => `M${cx - r} ${cy}a${r} ${r} 0 1 0 ${2 * r} 0a${r} ${r} 0 1 0 ${-2 * r} 0`;
const R = (x, y, w, h) => `M${x} ${y}h${w}v${h}h${-w}z`;
const P = d => `<path d="${d}"/>`;
const PIN = 'M12 21s-6-6.5-6-11a6 6 0 0 1 12 0c0 4.5-6 11-6 11z',
  RULER2 = 'M3 16L16 3l5 5L8 21zM7 12l2 2M10 9l2 2M13 6l2 2',
  DRAW2 = 'M4 20l1-4L16 5l3 3L8 19zM14 7l3 3',
  CAM = 'M4 8h3l1.5-2.5h7L17 8h3v11H4z' + C(12, 13.5, 3.5);
export const NOWE = {
  menu: P('M4 7h16M4 12h16M4 17h16'),
  sliders: P('M4 7h3M11 7h9M4 12h9M17 12h3M4 17h2M10 17h10' + C(9, 7, 2) + C(15, 12, 2) + C(8, 17, 2)),
  search: P(C(10.5, 10.5, 6.5) + 'M15.5 15.5L21 21'),
  map: P('M3 6l6-2 6 2 6-2v14l-6 2-6-2-6 2zM9 4v14M15 6v14'),
  route: P('M5 19c3 0 3-5 7-5s4-6 7-8'),
  gallery: P(R(3, 5, 18, 14) + 'M3 17l5-5 4 4 3-3 6 6'),
  home: P('M4 11l8-7 8 7M6 9.5V20h12V9.5M10 20v-5h4v5'),
  gear: P(C(12, 12, 3) + C(12, 12, 6.5) + 'M12 2.5v3M12 18.5v3M2.5 12h3M18.5 12h3M5.3 5.3l2.1 2.1M16.6 16.6l2.1 2.1M5.3 18.7l2.1-2.1M16.6 7.4l2.1-2.1'),
  layer: P('M12 4l9 4.5-9 4.5-9-4.5zM3 13l9 4.5 9-4.5M3 16.5l9 4.5 9-4.5'),
  gps: P(C(12, 12, 6) + 'M12 2v4M12 18v4M2 12h4M18 12h4'),
  compn: P(C(12, 12, 9) + 'M12 5.5l3 6.5-3 6.5-3-6.5z'),
  photo: P(CAM),
  pin: P(PIN),
  ruler: P(RULER2),
  fundo: P('M9 13L4 8.5 9 4M4 8.5h10a5.5 5.5 0 0 1 0 11h-3'),
  chevl: P('M15 5l-7 7 7 7'),
  fno: P('M6 6l12 12M18 6L6 18'),
  x: P('M6 6l12 12M18 6L6 18'),
  fok: P('M4.5 12.5l5 5 10-11'),
  check: P('M4.5 12.5l5 5 10-11'),
  plus: P('M12 5v14M5 12h14'),
  del: P('M4 7h16M9 7V4h6v3M6 7l1 13h10l1-13M10 11v6M14 11v6'),
  edit: P('M4 20h4L19 9l-4-4L4 16zM13 7l4 4'),
  eye: P('M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12z' + C(12, 12, 3)),
  share: P(C(18, 5, 2.6) + C(6, 12, 2.6) + C(18, 19, 2.6) + 'M8.3 10.8l7.4-4.3M8.3 13.2l7.4 4.3'),
  nav: P('M12 3l7 18-7-4-7 4z'),
  rec: P(C(12, 12, 8.5)),
  stop: P(R(6.5, 6.5, 11, 11)),
  pause: P('M9 6v12M15 6v12'),
  moon: P('M20 14.5A8.5 8.5 0 1 1 9.5 4a7 7 0 0 0 10.5 10.5z'),
  must: P('M6 21V4M6 4.5h11.5l-2.4 4.2 2.4 4.3H6'),
  dl: P('M12 4v11M7 10l5 5 5-5M5 20h14'),
  info: P(C(12, 12, 9) + 'M12 11v6'),
  fields: P(R(3.5, 4, 17, 16) + 'M3.5 9.5h17M3.5 15h17M9.5 4v16'),
  // pogoda (wxIcon składa z nich symbole)
  wx_sun: P(C(12, 10, 4) + 'M12 2.5v2M12 15.5v2M4.5 10h2M17.5 10h2M6.7 4.7l1.4 1.4M15.9 13.9l1.4 1.4M6.7 15.3l1.4-1.4M15.9 6.1l1.4-1.4'),
  wx_moon: P('M15 4.5a6 6 0 1 0 4.5 9.5A6.5 6.5 0 0 1 15 4.5z'),
  wx_sunS: P(C(8.5, 7.5, 3) + 'M8.5 2.3v1.2M3.3 7.5h1.2M4.8 3.8l.8.8M12.2 3.8l-.8.8'),
  wx_moonS: P('M9 3.5a4 4 0 1 0 3.5 6A4.5 4.5 0 0 1 9 3.5z'),
  wx_cloud: P('M7 19h10a3.5 3.5 0 0 0 .4-7 5 5 0 0 0-9.6-1.2A3.8 3.8 0 0 0 7 19z'),
  wx_cloudL: P('M8 18h9a3.2 3.2 0 0 0 .4-6.4 4.6 4.6 0 0 0-8.8-1A3.5 3.5 0 0 0 8 18z'),
  wx_cloudH: P('M7 14h10a3.5 3.5 0 0 0 .4-7 5 5 0 0 0-9.6-1.2A3.8 3.8 0 0 0 7 14z'),
  wx_fog: P('M4 9h16M3 13h18M5 17h14'),
  wx_rain: P('M8 17l-1 3M12 17l-1 3M16 17l-1 3'),
  wx_bolt: P('M12 15l-2 4h3l-2 3'),
  wx_snow: '<path d="M8 18h.01M12 20h.01M16 18h.01M10 22h.01M14 22h.01" stroke-width="3"/>'
};
export const NOWE_ICON = {draw: P(DRAW2), data: P(RULER2), measure: P(RULER2), pin: P(PIN), photo: P(CAM)};
// plamki koloru: [kształt wypełniony, kolor] – acc (zieleń OMM), red (czerwień nagrywania), me (niebieski „Ty”), ink (kolor kreski)
export const AKC = {
  route: [[C(5, 19, 1.7), 'acc'], [C(19, 6, 1.7), 'acc']],
  gallery: [[C(15.5, 9.5, 1.6), 'acc']],
  gps: [[C(12, 12, 2), 'me']],
  compn: [['M12 5.5l3 6.5h-6z', 'red']],
  pin: [[C(12, 10, 2.2), 'acc']],
  rec: [[C(12, 12, 4.2), 'red']],
  info: [[C(12, 7.6, 1.1), 'ink']],
  stop: [[R(9.5, 9.5, 5, 5), 'red']]
};
export const AKC_ICON = {pin: [[C(12, 10, 2.2), 'acc']]};
