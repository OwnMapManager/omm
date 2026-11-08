// v115 (pkt 317): generuje ikony OMM w stylu „Tusz z plamką” z geometrii w scripts/ikony-zrodlo.mjs.
// Każdy kształt (path, circle, rect, line, polyline, ellipse) przechodzi przez rough.js (kreska z lekkim drżeniem ręki),
// ze stałym ziarnem z nazwy ikony – ten sam wynik przy każdym uruchomieniu. Wypełnione kształty dostają wypełnienie
// „z ręki”, a plamki z AKC – klasę koloru (.ac.acc / .red / .me / .ink, kolory w CSS).
// Uruchom: node scripts/ikony-tusz.mjs  → podmienia bloki między znacznikami <ikony-tusz:IC> i <ikony-tusz:ICON> w app/omm.html.
import fs from 'fs';
import {createRequire} from 'module';
import {fileURLToPath} from 'url';
import path from 'path';
const req = createRequire(import.meta.url);
const rough = req('roughjs/bundled/rough.cjs.js');
const gen = rough.generator();
const ROOT = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const SRC = await import('./ikony-zrodlo.mjs');
const OPT = {roughness: 0.6, bowing: 0.9, disableMultiStroke: true, strokeWidth: 2};
const hash = s => [...s].reduce((a, c) => (a * 31 + c.charCodeAt(0)) >>> 0, 7) % 2e9;
const r1 = d => d.replace(/-?\d*\.\d+(?:e-?\d+)?/g, n => String(Math.round(+n * 10) / 10)).replace(/\s+/g, ' ');
const attrs = s => {
  const o = {};
  s.replace(/([\w:-]+)="([^"]*)"/g, (_, k, v) => (o[k] = v));
  return o;
};
const num = v => +v || 0;
function toD(tag, a) {
  if (tag === 'path') return a.d;
  if (tag === 'circle') {
    const cx = num(a.cx), cy = num(a.cy), r = num(a.r);
    return `M${cx - r} ${cy}a${r} ${r} 0 1 0 ${2 * r} 0a${r} ${r} 0 1 0 ${-2 * r} 0`;
  }
  if (tag === 'ellipse') {
    const cx = num(a.cx), cy = num(a.cy), rx = num(a.rx), ry = num(a.ry);
    return `M${cx - rx} ${cy}a${rx} ${ry} 0 1 0 ${2 * rx} 0a${rx} ${ry} 0 1 0 ${-2 * rx} 0`;
  }
  if (tag === 'rect') {
    const x = num(a.x), y = num(a.y), w = num(a.width), h = num(a.height), r = Math.min(num(a.rx || a.ry), w / 2, h / 2);
    if (!r) return `M${x} ${y}h${w}v${h}h${-w}z`;
    return `M${x + r} ${y}h${w - 2 * r}a${r} ${r} 0 0 1 ${r} ${r}v${h - 2 * r}a${r} ${r} 0 0 1 ${-r} ${r}h${-(w - 2 * r)}a${r} ${r} 0 0 1 ${-r} ${-r}v${-(h - 2 * r)}a${r} ${r} 0 0 1 ${r} ${-r}z`;
  }
  if (tag === 'line') return `M${num(a.x1)} ${num(a.y1)}L${num(a.x2)} ${num(a.y2)}`;
  if (tag === 'polyline' || tag === 'polygon') {
    const p = (a.points || '').trim().split(/[\s,]+/).map(Number);
    let d = '';
    for (let i = 0; i + 1 < p.length; i += 2) d += (i ? 'L' : 'M') + p[i] + ' ' + p[i + 1];
    return d + (tag === 'polygon' ? 'z' : '');
  }
  return null;
}
const KEEP = ['opacity', 'stroke-width', 'stroke-dasharray', 'stroke', 'class', 'transform'];
function conv(frag, key) {
  let seed = hash(key) + 1,
    out = '';
  frag.replace(/<(path|circle|rect|line|polyline|polygon|ellipse)\b([^>]*?)\/?>(?:<\/\1>)?/g, (all, tag, at) => {
    const a = attrs(at),
      d = toD(tag, a);
    // kropki (odcinki zerowej długości) i kształty z przekształceniem – bez zmian
    // małe kółka (kropki, oczka) zostają gładkie – drżenie zrobiłoby z nich kreski
    if (!d || /h\.0\d|v\.0\d/.test(d) || a.transform || ((tag === 'circle' || tag === 'ellipse') && num(a.r || a.rx) <= 1.8)) return (out += all);
    const ex = KEEP.filter(k => a[k] != null && k !== 'stroke').map(k => ` ${k}="${a[k]}"`).join('');
    const filled = a.fill && a.fill !== 'none';
    try {
      if (filled) {
        const fp = gen.toPaths(gen.path(d, {...OPT, fill: 'x', fillStyle: 'solid', stroke: 'none', seed: seed++})).filter(p => p.fill);
        fp.forEach(p => (out += `<path d="${r1(p.d)}" fill="${a.fill}" stroke="none"${ex}/>`));
      }
      if (!filled || (a.stroke && a.stroke !== 'none')) {
        const sp = gen.toPaths(gen.path(d, {...OPT, seed: seed++}));
        sp.forEach(p => (out += `<path d="${r1(p.d)}"${a.stroke && a.stroke !== 'none' ? ` stroke="${a.stroke}"` : ''}${ex}/>`));
      }
    } catch (e) {
      out += all;
    }
    if (/NaN/.test(out)) throw new Error('NaN w ' + key);
  });
  return out;
}
function akc(list, key) {
  let seed = hash(key + 'a') + 1;
  return (list || [])
    .map(([d, c]) =>
      gen
        .toPaths(gen.path(d, {roughness: 0.6, bowing: 0.9, fill: 'x', fillStyle: 'solid', stroke: 'none', seed: seed++}))
        .filter(p => p.fill)
        .map(p => `<path class="ac ${c}" d="${r1(p.d)}" stroke="none"/>`)
        .join('')
    )
    .join('');
}
const q = s => "'" + s.replace(/\\/g, '\\\\').replace(/'/g, "\\'") + "'";
const IC = {...SRC.IC, ...SRC.NOWE},
  ICON = {...SRC.ICON, ...SRC.NOWE_ICON};
let ic = '// <ikony-tusz:IC> – wygenerowane przez scripts/ikony-tusz.mjs ze scripts/ikony-zrodlo.mjs (nie edytuj ręcznie)\nconst IC = {\n';
for (const k of Object.keys(IC)) ic += `  ${k}: ${q(conv(IC[k], k) + akc(SRC.AKC[k], k))},\n`;
ic += '};\n// </ikony-tusz:IC>';
let icon = '// <ikony-tusz:ICON>\nconst ICON = {\n';
for (const k of Object.keys(ICON)) icon += `  ${k}: svgi(${q(conv(ICON[k], 'i_' + k) + akc(SRC.AKC_ICON[k], 'i_' + k))}),\n`;
icon += '};\n// </ikony-tusz:ICON>';
const F = path.join(ROOT, 'app/omm.html');
let h = fs.readFileSync(F, 'utf8');
const swap = (name, block, first) => {
  const re = new RegExp('// <ikony-tusz:' + name + '>[\\s\\S]*?// </ikony-tusz:' + name + '>');
  if (re.test(h)) h = h.replace(re, () => block);
  else {
    const i = h.indexOf(first);
    if (i < 0) throw new Error('brak bloku ' + name);
    const j = h.indexOf('\n};', i);
    h = h.slice(0, i) + block + h.slice(j + 3);
  }
};
swap('IC', ic, 'const IC = {\n');
swap('ICON', icon, 'const ICON = {\n');
fs.writeFileSync(F, h);
console.log('IC', Object.keys(IC).length, 'ICON', Object.keys(ICON).length, 'bajtów', ic.length + icon.length);
