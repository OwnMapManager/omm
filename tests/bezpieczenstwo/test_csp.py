# smoke test aplikacji pod ścisłą CSP: błędy JS, naruszenia CSP, podstawowe funkcje
from playwright.sync_api import sync_playwright
import base64
PNG=base64.b64decode('iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==')
with sync_playwright() as p:
    pg=p.chromium.launch().new_page(locale='pl-PL',viewport={'width':400,'height':820})
    errs=[];csp=[]
    pg.on('pageerror',lambda e:errs.append(str(e)))
    pg.on('console',lambda m: csp.append(m.text) if 'Content Security Policy' in m.text or 'Refused' in m.text else None)
    pg.route('**/*', lambda r: r.fulfill(body=PNG,content_type='image/png') if not r.request.url.startswith('http://localhost') else r.continue_())
    pg.goto('http://localhost:8765/index.html'); pg.wait_for_timeout(2500)
    # v119 (pkt 349 R7): nowa instalacja ma karty startowe – pomijamy je i przechodzimy cały samouczek
    print('APP_VER', pg.evaluate('APP_VER'), 'start cards:', pg.locator('.scards').count())
    pg.evaluate("localStorage.setItem('omm-tut','nie')"); pg.reload(); pg.wait_for_timeout(2500)
    pg.evaluate("tutStart()"); pg.wait_for_timeout(500)
    for i in range(40):
        if not pg.locator('.tut .okb').count(): break
        pg.click('.tut .okb'); pg.wait_for_timeout(250)
    # menu, ślady, ustawienia, trening
    pg.evaluate("$('lbtn').click()"); pg.wait_for_timeout(400)
    pg.evaluate("menuOpen('slady')"); pg.wait_for_timeout(400)
    pg.evaluate("$('sheet').style.display='none'; trnOpen()"); pg.wait_for_timeout(400)
    print('trening – kafelki:', pg.locator('#trn .trnt').count()); pg.evaluate("trnClose()")
    pg.evaluate("$('setb').click()"); pg.wait_for_timeout(400)
    pg.screenshot(path='t9.png')
    print('settings has zdj option:', pg.locator('text=Zdjęcia z internetu').count())
    # wstrzyknięcie poza walidacją – CSP ma zablokować wykonanie
    pg.evaluate("document.body.insertAdjacentHTML('beforeend','<img src=x onerror=\"globalThis.csp_marker=1\">')"); pg.wait_for_timeout(500)
    print('csp_marker (powinno być None):', pg.evaluate('globalThis.csp_marker'))
    print('errors:', errs); print('csp msgs:', csp[:5], len(csp))
