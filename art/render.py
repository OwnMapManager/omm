# Generuje ikony aplikacji i ekrany startowe z art/logo.svg (Playwright + Chromium)
import os,sys
from playwright.sync_api import sync_playwright
R=sys.argv[1]; svg=open(os.path.join(os.path.dirname(__file__),'logo.svg')).read()
BG='#f4f6f2'
def page_html(w,h,body): return f'<html><body style="margin:0;width:{w}px;height:{h}px;overflow:hidden">{body}</body></html>'
with sync_playwright() as p:
    b=p.chromium.launch(); pg=b.new_page()
    def shot(path,w,h,body,transparent=False):
        pg.set_viewport_size({'width':w,'height':h}); pg.set_content(page_html(w,h,body))
        os.makedirs(os.path.dirname(path),exist_ok=True); pg.screenshot(path=path,omit_background=transparent)
    img=lambda s:f'<div style="width:{s}px;height:{s}px">{svg.replace("<svg ",f"<svg width={s} height={s} ")}</div>'
    for d,k in [('mdpi',1),('hdpi',1.5),('xhdpi',2),('xxhdpi',3),('xxxhdpi',4)]:
        fg=int(108*k); lg=int(48*k)
        shot(f'{R}/mipmap-{d}/ic_launcher_foreground.png',fg,fg,img(fg),True)
        # starsze Androidy: zaokrąglony kwadrat i koło z tłem
        for name,rad in [('ic_launcher',str(int(lg*0.22))+'px'),('ic_launcher_round','50%')]:
            inner=int(lg*1.32)
            shot(f'{R}/mipmap-{d}/{name}.png',lg,lg,f'<div style="width:{lg}px;height:{lg}px;border-radius:{rad};background:{BG};overflow:hidden;position:relative"><div style="position:absolute;left:{(lg-inner)//2}px;top:{(lg-inner)//2}px">{img(inner)}</div></div>',True)
    import glob
    from PIL import Image
    for f in glob.glob(f'{R}/drawable*/splash.png'):
        w,h=Image.open(f).size; s=int(min(w,h)*0.5)
        shot(f,w,h,f'<div style="width:{w}px;height:{h}px;background:{BG};display:flex;flex-direction:column;align-items:center;justify-content:center;font-family:sans-serif;color:#3a2c22">{img(s)}<div style="font-size:{s*0.13}px;font-weight:700;margin-top:{-s*0.08}px">OMM</div><div style="font-size:{s*0.065}px;color:#7a7066">Own Map Manager</div></div>')
    b.close()
