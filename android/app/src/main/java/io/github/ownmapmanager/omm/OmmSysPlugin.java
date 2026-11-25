package io.github.ownmapmanager.omm;

import android.app.ActivityManager;
import android.app.ApplicationExitInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

/**
 * v133: drobne informacje systemowe dla OMM (bez uprawnień).
 *
 * audio    – głośność multimediów (STREAM_MUSIC – tym kanałem mówi nawigacja) i jej maksimum, tryb dzwonka;
 *            OMM ostrzega na starcie nawigacji, gdy komunikaty nie będą słyszalne (pkt 454),
 * lastGone – czy Android zamknął silnik strony (WebView) od ostatniego startu – OMM odtwarza wtedy okno
 *            zamiast się wyłączać (MainActivity) i zapisuje to w dzienniku (pkt 440); odczyt kasuje znacznik.
 * exits    – v139 (pkt 471): powody ostatnich zamknięć procesu OMM podane przez Androida 11+
 *            (ApplicationExitInfo: brak pamięci, awaria, ANR, zamknięcie przez użytkownika…) z pamięcią w chwili śmierci;
 *            do dziennika OMM – pewna diagnoza zamiast domysłów. Bez uprawnień, tylko własny proces.
 * memory   – v139: pamięć telefonu (wolna / całkowita / czy system uważa, że jest mało) i pamięć OMM (PSS).
 *
 * Autor: Paweł Jewuła · OMM – Own Map Manager
 */
@CapacitorPlugin(name = "OmmSys")
public class OmmSysPlugin extends Plugin {

    static final String PREFS = "omm-sys";

    @PluginMethod
    public void audio(PluginCall call) {
        JSObject r = new JSObject();
        try {
            AudioManager am = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
            r.put("media", am.getStreamVolume(AudioManager.STREAM_MUSIC));
            r.put("max", am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            r.put("ringer", am.getRingerMode()); // 0 cisza, 1 wibracja, 2 normalny
        } catch (Exception e) {
            r.put("error", String.valueOf(e.getMessage()));
        }
        call.resolve(r);
    }

    @PluginMethod
    public void lastGone(PluginCall call) {
        SharedPreferences p = getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        JSObject r = new JSObject();
        r.put("t", p.getLong("goneT", 0));
        r.put("crash", p.getBoolean("goneCrash", false));
        r.put("n", p.getInt("goneN", 0));
        p.edit().remove("goneT").remove("goneCrash").remove("goneN").apply();
        call.resolve(r);
    }

    @PluginMethod
    public void exits(PluginCall call) {
        JSObject r = new JSObject();
        JSArray arr = new JSArray();
        try {
            if (Build.VERSION.SDK_INT >= 30) {
                ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
                for (ApplicationExitInfo e : am.getHistoricalProcessExitReasons(getContext().getPackageName(), 0, 8)) {
                    JSObject o = new JSObject();
                    o.put("t", e.getTimestamp());
                    o.put("reason", e.getReason());
                    o.put("status", e.getStatus());
                    o.put("importance", e.getImportance());
                    o.put("pssMb", Math.round(e.getPss() / 1024.0));
                    o.put("rssMb", Math.round(e.getRss() / 1024.0));
                    o.put("desc", String.valueOf(e.getDescription()));
                    o.put("proc", String.valueOf(e.getProcessName()));
                    arr.put(o);
                }
            }
        } catch (Exception ex) {
            r.put("error", String.valueOf(ex.getMessage()));
        }
        r.put("exits", arr);
        call.resolve(r);
    }

    @PluginMethod
    public void memory(PluginCall call) {
        JSObject r = new JSObject();
        try {
            ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            r.put("availMb", Math.round(mi.availMem / 1048576.0));
            r.put("totalMb", Math.round(mi.totalMem / 1048576.0));
            r.put("low", mi.lowMemory);
            android.os.Debug.MemoryInfo dm = new android.os.Debug.MemoryInfo();
            android.os.Debug.getMemoryInfo(dm);
            r.put("pssMb", Math.round(dm.getTotalPss() / 1024.0));
        } catch (Exception ex) {
            r.put("error", String.valueOf(ex.getMessage()));
        }
        call.resolve(r);
    }
}
