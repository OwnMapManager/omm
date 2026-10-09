# kopie: zwykła i zaszyfrowana (tam i z powrotem), anulowanie bez skutków, zdjęcia z internetu
import tempfile; T=tempfile.mkdtemp()+'/'
from playwright.sync_api import sync_playwright
import base64, json
PNG=base64.b64decode('iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==')
R={}
with sync_playwright() as p:
    br=p.chromium.launch()
    pg=br.new_page(viewport={'width':400,'height':820}); errs=[]; pg.on('pageerror',lambda e:errs.append(str(e)))
    net=[]
    def route(r):
        if r.request.url.startswith('http://localhost'): return r.continue_()
        net.append(r.request.url); r.fulfill(body=PNG,content_type='image/png')
    pg.route('**/*', route)
    pg.goto('http://localhost:8765/index.html'); pg.evaluate("localStorage.setItem('omm-tut','nie')"); pg.wait_for_timeout(1500)
    # dane testowe: punkt ze zdjęciem lokalnym i zdalnym, ślad
    pg.evaluate("""async()=>{await IDB.set('foto/a.jpg', new Blob(['PHOTO-A'],{type:'image/jpeg'}));
      P.items.push({id:uid(),t:'pin',p:[[52,21]],l:P.groups[0].layers[0].id,nm:'Punkt',ph:['omm:a.jpg','http://evil.example/track.jpg?id=123']});save();render();
      TRK.items.push({id:uid(),l:TRK.layers[0].id,nm:'Ślad',st:Date.now(),pts:[[52,21,100,0],[52.01,21.01,110,60]],sg:[]});trkChanged&&trkChanged();}""")
    pg.wait_for_timeout(500)
    # zwykła kopia
    plain=pg.evaluate("async()=>{const b=await zipBackup(false);const u=new Uint8Array(await b.arrayBuffer());return {n:zipOpen(u.buffer).names(),b64:btoa(String.fromCharCode(...u))}}")
    R['plain_names']=plain['n']
    # szyfrowanie
    pg.evaluate("encSetPassword('tajne-haslo-1')")
    enc=pg.evaluate("async()=>{const b=await zipBackup(false);const u=new Uint8Array(await b.arrayBuffer());return {n:zipOpen(u.buffer).names(),b64:btoa(String.fromCharCode(...u))}}")
    R['enc_names']=enc['n']
    R['enc_contains_plain_text']= b'Punkt' in base64.b64decode(enc['b64']) or b'PHOTO-A' in base64.b64decode(enc['b64'])
    open(T+'enc.zip','wb').write(base64.b64decode(enc['b64']))
    # nowy telefon: brak klucza; zmieniamy dane, żeby zobaczyć przywrócenie
    pg.evaluate("IDB.del('szyfr-kopii')"); pg.evaluate("P.items.length=0;save();render()")
    pg.set_input_files('#pfile',T+'enc.zip'); pg.wait_for_timeout(800)
    R['ask_pass_prompt']=pg.evaluate("document.querySelector('.dback')?.innerText")
    R['pass_input_type']=pg.evaluate("document.querySelector('.dback input')?.type")
    pg.fill('.dback input','zle-haslo'); pg.click('.dback button.okb'); pg.wait_for_timeout(2500)
    R['after_wrong']=pg.evaluate("document.querySelector('.dback')?.innerText")
    pg.fill('.dback input','tajne-haslo-1'); pg.click('.dback button.okb'); pg.wait_for_timeout(2500)
    R['confirm_dialog']=pg.evaluate("document.querySelector('.dback')?.innerText")
    pg.locator('.dback button',has_text='Przywróć').click(); pg.wait_for_timeout(1000)
    R['restored_items']=pg.evaluate("DB.maps.flatMap(m=>m.items).map(i=>[i.nm,i.ph])")
    R['restored_tracks']=pg.evaluate("TRK.items.map(t=>t.nm)")
    # podmieniony bajt w dane.bin -> odmowa
    d=bytearray(open(T+'enc.zip','rb').read()); 
    pg.evaluate("encSetPassword('tajne-haslo-1')")
    R['tamper']=pg.evaluate("""async b=>{const u=Uint8Array.from(atob(b),c=>c.charCodeAt(0));const z=zipOpen(u.buffer);const n=await z.read('dane.bin');const h=JSON.parse(new TextDecoder().decode(await z.read('szyfr.json')));n[100]^=1;const E=await IDB.get('szyfr-kopii');
      const k=await encDerive('tajne-haslo-1',Uint8Array.from(atob(h.salt),c=>c.charCodeAt(0)),h.iter);try{await crypto.subtle.decrypt({name:'AES-GCM',iv:Uint8Array.from(atob(h.iv),c=>c.charCodeAt(0))},k,n);return 'ACCEPTED'}catch(e){return 'REJECTED'}}""", enc['b64'])
    # anulowanie przywracania: nic się nie zmienia (zdjęcia też)
    pg.evaluate("IDB.del('szyfr-kopii')")
    pg.evaluate("IDB.set('foto/a.jpg', new Blob(['CHANGED-LOCALLY']))")
    before=pg.evaluate("localStorage.getItem('mapa-v2')")
    open(T+'plain.zip','wb').write(base64.b64decode(plain['b64']))
    pg.set_input_files('#pfile',T+'plain.zip'); pg.wait_for_timeout(1200)
    pg.locator('.dback button',has_text='Anuluj').click(); pg.wait_for_timeout(500)
    R['cancel_db_same']= before==pg.evaluate("localStorage.getItem('mapa-v2')")
    R['cancel_photo']=pg.evaluate("(async()=>await (await IDB.get('foto/a.jpg')).text())()")
    # restore with confirm: different photo content same name -> renamed, existing kept
    pg.set_input_files('#pfile',T+'plain.zip'); pg.wait_for_timeout(1200)
    pg.locator('.dback button',has_text='Przywróć').click(); pg.wait_for_timeout(1000)
    R['collision_existing']=pg.evaluate("(async()=>await (await IDB.get('foto/a.jpg')).text())()")
    R['collision_refs']=pg.evaluate("DB.maps.flatMap(m=>m.items).map(i=>i.ph)")
    R['collision_keys']=pg.evaluate("IDB.keys('foto/')")
    # zdalne zdjęcie: brak połączenia przed zgodą
    net.clear()
    pg.evaluate("const it=DB.maps.flatMap(m=>m.items).find(i=>i.ph);document.body.append(fotoImg(it.ph[1],'tst'))")
    pg.wait_for_timeout(800)
    R['remote_before']=[u for u in net if 'evil' in u]
    pg.evaluate("document.querySelector('img.tst').click()"); pg.wait_for_timeout(400)
    R['remote_prompt']=pg.evaluate("document.querySelector('.dback')?.innerText")
    pg.locator('.dback button',has_text='Tylko to').click(); pg.wait_for_timeout(800)
    R['remote_after']=[u for u in net if 'evil' in u]
    R['errs']=errs
    print(json.dumps(R,indent=1,ensure_ascii=False))
