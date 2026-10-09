# testy regresji audytu v45 w prawdziwym Chromium (DOM, Leaflet, IndexedDB)
from playwright.sync_api import sync_playwright
import base64, json, sys
import os; EV=os.path.join(os.path.dirname(__file__),'dane')+'/'
PNG=base64.b64decode('iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==')
res={}
def new(p, init=None):
    pg=p.chromium.launch().new_page(viewport={'width':400,'height':820})
    pg.errs=[]; pg.on('pageerror',lambda e:pg.errs.append(str(e)))
    pg.route('**/*', lambda r: r.fulfill(body=PNG,content_type='image/png') if not r.request.url.startswith('http://localhost') else r.continue_())
    pg.goto('http://localhost:8765/index.html')
    pg.evaluate("localStorage.setItem('omm-tut','nie')")
    if init: init(pg)
    pg.wait_for_timeout(1500)
    return pg
def confirm(pg):
    pg.wait_for_timeout(500)
    for t in ['Przywróć','Dodaj mapę','OK']:
        btn=pg.locator('.dback button',has_text=t)
        if btn.count(): btn.last.click(); pg.wait_for_timeout(1000); return True
    return False
def markers(pg):
    return pg.evaluate("[globalThis.audit_marker, globalThis.audit_track_marker, document.querySelectorAll('[onload],[onerror]').length]")
with sync_playwright() as p:
    # Q01: stara kopia z kolorem wstrzykującym HTML
    pg=new(p)
    pg.set_input_files('#pfile', EV+'stara-kopia-kolor.json'); confirm(pg)
    pg.wait_for_timeout(500)
    it=pg.evaluate("DB.maps.flatMap(m=>m.items).find(i=>i.tx==='Synthetic')")
    res['Q01']={'markers':markers(pg),'item':it,'saved_has_onload':'onload' in pg.evaluate("localStorage.getItem('mapa-v2')"),'errs':pg.errs}
    # Q01b: te same złośliwe dane już zapisane przez starszą wersję -> po wczytaniu
    pg.evaluate("""()=>{const d=JSON.parse(localStorage.getItem('mapa-v2'));d.maps[0].items.push({id:'bad"><svg onload=globalThis.audit_marker=2>',l:d.maps[0].groups[0].layers[0].id,t:'num',no:'1',p:[[52,21]],c:'red"><img src=x onerror=globalThis.audit_marker=3>'});d.maps[0].groups[0].layers[0].id_old=1;localStorage.setItem('mapa-v2',JSON.stringify(d))}""")
    pg.reload(); pg.wait_for_timeout(1500)
    res['Q01b']={'markers':markers(pg),'items':pg.evaluate("DB.maps.flatMap(m=>m.items).map(i=>[i.id,i.c,i.t])"),'errs':pg.errs}
    # Q02: kopia śladu z identyfikatorem wstrzykującym SVG
    pg.set_input_files('#pfile', EV+'slad-id.json'); confirm(pg)
    pg.wait_for_timeout(800)
    pg.evaluate("$('lbtn').click()"); pg.wait_for_timeout(300)
    pg.evaluate("document.querySelector('[data-k=slady]').click();TRK.layers.forEach(l=>l.open=true);trkSheet()"); pg.wait_for_timeout(800)
    res['Q02']={'markers':markers(pg),'ids':pg.evaluate("TRK.items.map(t=>t.id)"),'swatches':pg.evaluate("document.querySelectorAll('linearGradient').length"),'errs':pg.errs,'toast':pg.evaluate("document.querySelector('.dback')?.innerText"),'maps':pg.evaluate("DB.maps.map(m=>[m.id,m.groups[0].layers.map(l=>l.id)])"),'cur':pg.evaluate('DB.cur')}
    print(json.dumps(res,indent=1,ensure_ascii=False))
    # Q02b: złośliwy ślad już zapisany w IndexedDB przez starszą wersję
    pg.evaluate("""async()=>{const d=(await IDB.get('slady'))||JSON.parse(JSON.stringify(TRK));d.items.push({id:'y"><svg onload=globalThis.audit_track_marker=2>',l:d.layers[0].id,cm:'wysokosc',a:'constructor',st:Date.now(),pts:[[52,21,1,0],[52.01,21.01,2,10]],sg:[]});await IDB.set('slady',d)}""")
    pg.reload(); pg.wait_for_timeout(1500)
    pg.evaluate("$('lbtn').click()"); pg.wait_for_timeout(300)
    pg.evaluate("document.querySelector('[data-k=slady]').click();TRK.layers.forEach(l=>l.open=true);trkSheet()"); pg.wait_for_timeout(800)
    res['Q02b']={'markers':markers(pg),'ids':pg.evaluate("TRK.items.map(t=>[t.id,t.a])"),'errs':pg.errs}
    # Q03: zdjęcie nie może się zmienić przed zgodą ani po błędzie
    pg.evaluate("IDB.set('foto/existing.jpg', new Blob(['ORIGINAL']))")
    pg.set_input_files('#pfile', EV+'zdjecie-przed-zgoda.zip.testdata'); pg.wait_for_timeout(1200)
    msg=pg.evaluate("document.querySelector('.dback')?.innerText")
    confirm(pg)
    res['Q03']={'photo':pg.evaluate("(async()=>await (await IDB.get('foto/existing.jpg')).text())()"),'msg':msg}
    # Q06: zły CRC
    b64=base64.b64encode(open(EV+'zly-crc.zip.testdata','rb').read()).decode()
    res['Q06']=pg.evaluate("""async b=>{const u=Uint8Array.from(atob(b),c=>c.charCodeAt(0));try{const z=zipOpen(u.buffer);const r=await z.read('data.txt');return 'ACCEPTED '+new TextDecoder().decode(r)}catch(e){return 'REJECTED: '+e.message}}""", b64)
    # Q04/F07: zastrzeżone nazwy warstw
    res['Q04']=pg.evaluate("""()=>['__proto__','constructor','toString','normal'].map(n=>{try{const r=parseFile(JSON.stringify({type:'FeatureCollection',features:[{type:'Feature',geometry:{type:'Point',coordinates:[21,52]},properties:{warstwa:n,name:'a'}}]}),'x.geojson');return n+': OK '+r.layers.map(l=>l.name).join(',')}catch(e){return n+': ERR '+e.message}})""")
    print(json.dumps(res,indent=1,ensure_ascii=False))
