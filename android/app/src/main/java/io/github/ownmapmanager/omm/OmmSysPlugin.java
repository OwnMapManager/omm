package io.github.ownmapmanager.omm;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
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
}
