// Poprawka bezpieczeństwa w @capacitor/android (audyt OMM v45, R07).
// CapacitorWebView.dispatchKeyEvent dla zdarzenia ACTION_MULTIPLE (np. z klawiatury ekranowej)
// sklejał wpisany tekst z kodem JavaScript bez kodowania znaków – tekst z apostrofem mógł stać się kodem.
// Zamieniamy to na bezpieczne przekazanie tekstu jako literału JSON (org.json.JSONObject.quote).
// Skrypt uruchamia się po każdym „npm install / npm ci” (postinstall) i jest idempotentny.
import {readFileSync, writeFileSync, existsSync} from 'node:fs';

const f = 'node_modules/@capacitor/android/capacitor/src/main/java/com/getcapacitor/CapacitorWebView.java';
if (!existsSync(f)) {
  console.log('patch-capacitor: brak ' + f + ' – pomijam');
  process.exit(0);
}
let s = readFileSync(f, 'utf8');
const MARK = '/* OMM-R07 */';
if (s.includes(MARK)) {
  console.log('patch-capacitor: poprawka już jest');
  process.exit(0);
}
const bad =
  `evaluateJavascript("document.activeElement.value = document.activeElement.value + '" + event.getCharacters() + "';", null);`;
if (!s.includes(bad)) throw new Error('patch-capacitor: nie znaleziono oczekiwanego kodu w ' + f + ' – sprawdź nową wersję Capacitora');
s = s.replace(
  bad,
  MARK +
    ` evaluateJavascript("(function(e,t){if(e&&typeof e.value==='string')e.value+=t;})(document.activeElement," + org.json.JSONObject.quote(String.valueOf(event.getCharacters())) + ");", null);`
);
writeFileSync(f, s);
console.log('patch-capacitor: poprawiono CapacitorWebView (R07)');
