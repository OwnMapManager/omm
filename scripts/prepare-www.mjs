// Przygotowuje folder www/ – to, co trafia do środka aplikacji na Androida.
//  • app/omm.html → www/index.html
//  • Leaflet z CDN (internet) zamieniony na kopię wbudowaną w aplikację (działa bez zasięgu)
//  • numer wersji (APP_VER = 'v33') zapisany w www/version.txt – Gradle nadaje go plikowi APK
import {readFileSync, writeFileSync, mkdirSync, cpSync, rmSync} from 'node:fs';

const src = 'app/omm.html';
let html = readFileSync(src, 'utf8');

const cdn = 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/';
for (const f of ['leaflet.css', 'leaflet.js']) {
  if (!html.includes(cdn + f)) throw new Error(`W ${src} nie ma odnośnika do ${cdn + f}`);
  html = html.replaceAll(cdn + f, 'lib/leaflet/' + f);
}

const ver = html.match(/const APP_VER = 'v(\d+)'/);
if (!ver) throw new Error(`W ${src} nie ma stałej APP_VER = 'vNN'`);

rmSync('www', {recursive: true, force: true});
mkdirSync('www/lib', {recursive: true});
writeFileSync('www/index.html', html);
writeFileSync('www/version.txt', ver[1] + '\n');
cpSync('node_modules/leaflet/dist', 'www/lib/leaflet', {recursive: true});
console.log(`www/ gotowe – OMM v${ver[1]}`);
