package io.github.ownmapmanager.omm;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

/**
 * v94 (pkt 118, etap 2): satelity widziane przez telefon – do „mapy nieba” w Informacjach GPS.
 * Android podaje to tylko kodowi natywnemu (GnssStatus). Bez nowych uprawnień: wymaga zgody na dokładną
 * lokalizację, którą OMM już ma. Dane nie są nigdzie zapisywane ani wysyłane – tylko pokazywane na ekranie,
 * i tylko gdy ten ekran jest otwarty (start → stop).
 *
 * Autor: Paweł Jewuła · OMM – Own Map Manager
 */
@CapacitorPlugin(name = "OmmGnss")
public class OmmGnssPlugin extends Plugin {

    private GnssStatus.Callback cb;
    private long last = 0;

    @PluginMethod
    public void start(PluginCall call) {
        if (getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            call.reject("Brak zgody na dokładną lokalizację.");
            return;
        }
        stopCb();
        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        cb = new GnssStatus.Callback() {
            @Override
            public void onSatelliteStatusChanged(GnssStatus s) {
                long now = System.currentTimeMillis();
                if (now - last < 900) return; // najwyżej raz na sekundę
                last = now;
                JSArray a = new JSArray();
                int used = 0;
                for (int i = 0; i < s.getSatelliteCount(); i++) {
                    JSObject o = new JSObject();
                    o.put("id", s.getSvid(i));
                    o.put("c", s.getConstellationType(i)); // 1 GPS, 3 GLONASS, 4 QZSS, 5 BeiDou, 6 Galileo, 7 IRNSS, 2 SBAS
                    o.put("az", Math.round(s.getAzimuthDegrees(i)));
                    o.put("el", Math.round(s.getElevationDegrees(i)));
                    o.put("cn", Math.round(s.getCn0DbHz(i)));
                    o.put("u", s.usedInFix(i));
                    if (s.usedInFix(i)) used++;
                    a.put(o);
                }
                JSObject r = new JSObject();
                r.put("sats", a);
                r.put("used", used);
                notifyListeners("gnss", r);
            }
        };
        try {
            lm.registerGnssStatusCallback(cb, new Handler(Looper.getMainLooper()));
            call.resolve();
        } catch (SecurityException e) {
            cb = null;
            call.reject("Brak zgody na lokalizację.");
        }
    }

    @PluginMethod
    public void stop(PluginCall call) {
        stopCb();
        call.resolve();
    }

    private void stopCb() {
        if (cb == null) return;
        try {
            ((LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE)).unregisterGnssStatusCallback(cb);
        } catch (Exception e) {
            // nic
        }
        cb = null;
    }

    @Override
    protected void handleOnDestroy() {
        stopCb();
    }
}
