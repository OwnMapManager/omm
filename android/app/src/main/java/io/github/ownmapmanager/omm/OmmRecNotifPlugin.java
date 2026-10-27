package io.github.ownmapmanager.omm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

/**
 * v94: powiadomienie nagrywania śladu z dystansem, czasem i przyciskami Zakończ / Zdjęcie / Punkt.
 *
 * Nagrywanie w tle prowadzi usługa wtyczki background-geolocation, która pokazuje własne stałe powiadomienie
 * (id 28351, kanał = nazwa pakietu wtyczki). OMM podmienia jego treść (to samo id i kanał – Android aktualizuje
 * powiadomienie usługi pierwszoplanowej; nic nowego się nie pojawia). Nowych uprawnień brak.
 *
 * Przyciski:
 *  – Zakończ i Punkt: rozgłoszenie (broadcast) do OmmRecActionReceiver w tej samej aplikacji (exported=false),
 *    bez otwierania OMM; zdarzenie „recAction” trafia do JavaScriptu (zapis śladu / punkt w bieżącym miejscu),
 *  – Zdjęcie: otwiera OMM (aparat potrzebuje ekranu), zdarzenie przychodzi przez handleOnNewIntent.
 * Wszystkie PendingIntent są IMMUTABLE i wskazują jawnie klasy OMM.
 *
 * Autor: Paweł Jewuła · OMM – Own Map Manager
 */
@CapacitorPlugin(name = "OmmRecNotif")
public class OmmRecNotifPlugin extends Plugin {

    static final int NOTIF_ID = 28351; // jak BackgroundGeolocationService.NOTIFICATION_ID
    static final String CHANNEL = "com.equimaps.capacitor_background_geolocation"; // kanał wtyczki
    static final String EXTRA = "omm_rec_action";
    private static OmmRecNotifPlugin inst;

    @Override
    public void load() {
        inst = this;
        // OMM uruchomiony od zera przyciskiem „Zdjęcie” (rzadkie – zwykle aplikacja już działa)
        Intent i = getActivity() != null ? getActivity().getIntent() : null;
        if (i != null && i.hasExtra(EXTRA)) {
            fire(i.getStringExtra(EXTRA));
            i.removeExtra(EXTRA);
        }
    }

    @Override
    protected void handleOnNewIntent(Intent intent) {
        super.handleOnNewIntent(intent);
        if (intent != null && intent.hasExtra(EXTRA)) {
            fire(intent.getStringExtra(EXTRA));
            intent.removeExtra(EXTRA);
        }
    }

    /** zdarzenie z powiadomienia → JavaScript (zachowane, gdyby nikt jeszcze nie słuchał) */
    static void fire(String a) {
        if (a == null || inst == null) return;
        if (!a.equals("stop") && !a.equals("pkt") && !a.equals("foto")) return; // tylko znane akcje
        JSObject r = new JSObject();
        r.put("a", a);
        inst.notifyListeners("recAction", r, true);
    }

    @PluginMethod
    public void update(PluginCall call) {
        try {
            Context c = getContext();
            String title = call.getString("title", "OMM"), text = call.getString("text", "");
            Notification.Builder b = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? new Notification.Builder(c, CHANNEL) : new Notification.Builder(c);
            b.setContentTitle(title)
                .setContentText(text)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .setSmallIcon(icon(c));
            Intent launch = c.getPackageManager().getLaunchIntentForPackage(c.getPackageName());
            if (launch != null) {
                launch.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                b.setContentIntent(PendingIntent.getActivity(c, 0, launch, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
            }
            if (call.getBoolean("buttons", true)) {
                b.addAction(action(c, 1, call.getString("stop", "Zakończ"), "stop", false));
                b.addAction(action(c, 2, call.getString("foto", "Zdjęcie"), "foto", true));
                b.addAction(action(c, 3, call.getString("pkt", "Punkt"), "pkt", false));
            }
            NotificationManager m = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            m.notify(NOTIF_ID, b.build());
            call.resolve();
        } catch (Exception e) {
            call.reject(e.getMessage() != null ? e.getMessage() : e.toString());
        }
    }

    /** po zakończeniu nagrywania – gdyby zostało nieaktualne powiadomienie (usługi pierwszoplanowej Android i tak nie da zdjąć) */
    @PluginMethod
    public void clear(PluginCall call) {
        try {
            NotificationManager m = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            m.cancel(NOTIF_ID);
        } catch (Exception e) {
            // nic
        }
        call.resolve();
    }

    private Notification.Action action(Context c, int req, String label, String a, boolean openApp) {
        PendingIntent pi;
        if (openApp) {
            Intent i = new Intent(c, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            i.putExtra(EXTRA, a);
            pi = PendingIntent.getActivity(c, 100 + req, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            Intent i = new Intent(c, OmmRecActionReceiver.class);
            i.putExtra(EXTRA, a);
            pi = PendingIntent.getBroadcast(c, 200 + req, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        }
        return new Notification.Action.Builder(null, label, pi).build();
    }

    private int icon(Context c) {
        // ta sama ikona co w powiadomieniu wtyczki (zasób capacitor_background_geolocation_notification_icon), inaczej ikona aplikacji
        try {
            int sid = c.getResources().getIdentifier("capacitor_background_geolocation_notification_icon", "string", c.getPackageName());
            if (sid != 0) {
                String[] p = c.getString(sid).split("/");
                int id = c.getResources().getIdentifier(p[1], p[0], c.getPackageName());
                if (id != 0) return id;
            }
        } catch (Exception e) {
            // brak – ikona aplikacji
        }
        return c.getApplicationInfo().icon;
    }
}
