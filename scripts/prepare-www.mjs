// Przygotowuje folder www/ – to, co trafia do środka aplikacji na Androida.
//  • app/omm.html → www/index.html
//  • Leaflet z CDN (internet) zamieniony na kopię wbudowaną w aplikację (działa bez zasięgu)
//  • dołączony rdzeń Capacitora (lib/capacitor.js) – dostęp do wtyczek: zapis plików, GPS w tle…
//  • numer wersji (np. APP_VER = 'v34') zapisany w www/version.txt – Gradle nadaje go plikowi APK
//  • skrypt aplikacji przeniesiony do osobnego pliku app.js i ścisła polityka CSP: przeglądarka
//    wykonuje tylko pliki z aplikacji (bez skryptów wpisanych w HTML), więc nawet gdyby jakiś
//    tekst z importowanego pliku trafił do strony jako HTML, nie uruchomi kodu (audyt v45, R01)
import {readFileSync, writeFileSync, mkdirSync, cpSync, rmSync} from 'node:fs';

const src = 'app/omm.html';
let html = readFileSync(src, 'utf8');

const cdn = 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/';
for (const f of ['leaflet.css', 'leaflet.js']) {
  if (!html.includes(cdn + f)) throw new Error(`W ${src} nie ma odnośnika do ${cdn + f}`);
  html = html.replaceAll(cdn + f, 'lib/leaflet/' + f);
}

// rdzeń Capacitora wczytujemy przed skryptem aplikacji
const leafletTag = '<script src="lib/leaflet/leaflet.js"></script>';
if (!html.includes(leafletTag)) throw new Error('Brak znacznika ' + leafletTag);
html = html.replace(leafletTag, '<script src="lib/capacitor.js"></script>\n' + leafletTag);

// skrypt aplikacji → app.js (dokładnie jeden wbudowany <script>; w HTML nie może być innych)
const inl = [...html.matchAll(/<script>([\s\S]*?)<\/script>/g)];
if (inl.length !== 1) throw new Error('Oczekiwano dokładnie jednego wbudowanego <script>, jest ' + inl.length);
const appJs = inl[0][1];
html = html.replace(inl[0][0], '<script src="app.js"></script>');
if (/\son[a-z]+\s*=\s*["']/i.test(html.replace(/<script[\s\S]*?<\/script>/g, '')))
  throw new Error('W HTML są atrybuty zdarzeń (on…=) – CSP bez unsafe-inline by je zablokowała');
// ścisła polityka treści dla aplikacji (wersja w app/omm.html jest łagodniejsza, bo ma skrypt w środku)
const TILES = [
  'https://mapy.geoportal.gov.pl',
  'https://integracja.gugik.gov.pl',
  'https://mapserver.bdl.lasy.gov.pl',
  'https://tile.openstreetmap.org',
  'https://tile.waymarkedtrails.org',
  'https://*.google.com',
  'https://server.arcgisonline.com'
];
const CSP = [
  "default-src 'self'",
  "script-src 'self'",
  "style-src 'self' 'unsafe-inline'",
  "img-src 'self' data: blob: https:",
  "media-src 'self' data: blob:",
  "connect-src 'self' data: blob: https://overpass-api.de https://nominatim.openstreetmap.org https://services.gugik.gov.pl https://routing.openstreetmap.de " +
    TILES.join(' '),
  "font-src 'self' data:",
  "worker-src 'self' blob:",
  "object-src 'none'",
  "frame-src 'none'",
  "child-src 'none'",
  "base-uri 'none'",
  "form-action 'none'"
].join('; ');
const cspRx = /<meta http-equiv="Content-Security-Policy" content="[^"]*">/;
if (!cspRx.test(html)) throw new Error('Brak znacznika CSP w ' + src);
html = html.replace(cspRx, '<meta http-equiv="Content-Security-Policy" content="' + CSP + '">');

const ver = appJs.match(/const APP_VER = 'v(\d+)'/);
if (!ver) throw new Error(`W ${src} nie ma stałej APP_VER = 'vNN'`);

rmSync('www', {recursive: true, force: true});
mkdirSync('www/lib', {recursive: true});
writeFileSync('www/index.html', html);
writeFileSync('www/app.js', appJs);
writeFileSync('www/version.txt', ver[1] + '\n');
cpSync('node_modules/leaflet/dist', 'www/lib/leaflet', {recursive: true});
cpSync('node_modules/@capacitor/core/dist/capacitor.js', 'www/lib/capacitor.js');
console.log(`www/ gotowe – OMM v${ver[1]}`);
