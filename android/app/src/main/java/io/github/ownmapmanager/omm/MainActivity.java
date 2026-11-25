package io.github.ownmapmanager.omm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebSettings;
import android.webkit.WebView;
import androidx.webkit.WebViewFeature;
import com.getcapacitor.BridgeActivity;
import com.getcapacitor.WebViewListener;

public class MainActivity extends BridgeActivity {

    // v139 (pkt 471): sygnał Androida „mało pamięci” → do strony (OMM zwalnia kafle i miniatury, wpis w dzienniku)
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        try {
            if (getBridge() != null) getBridge().triggerWindowJSEvent("ommtrim", "{\"level\":" + level + "}");
        } catch (Exception ignored) {}
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(OmmBackupPlugin.class); // kopia zapasowa w miejscu wybranym przez użytkownika (np. Dysk Google)
        registerPlugin(OmmLockPlugin.class); // blokada OMM odciskiem palca / twarzą
        registerPlugin(OmmPhotosPlugin.class); // zdjęcia z galerii razem z miejscem zrobienia (v79)
        registerPlugin(OmmGnssPlugin.class); // v94: satelity do mapy nieba w Informacjach GPS
        registerPlugin(OmmRecNotifPlugin.class); // v94: powiadomienie nagrywania z dystansem i przyciskami
        registerPlugin(OmmNoWebViewPlugin.class); // zamiast wbudowanej „WebView” – bez podmiany ścieżki aplikacji
        registerPlugin(OmmSysPlugin.class); // v133: głośność multimediów, informacja o zamknięciu silnika strony
        super.onCreate(savedInstanceState);
        // Tylko https: żadnych treści po HTTP na stronie aplikacji (audyt v45, R02).
        getBridge().getWebView().getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        // v133 (pkt 440): gdy Android zamknie silnik strony (brak pamięci albo awaria renderera), domyślnie ginie cała
        // aplikacja. Zamiast tego: znacznik dla dziennika OMM i odtworzenie okna – nagrywanie w tle (usługa) działa dalej,
        // a OMM po starcie wznawia nawigację (pkt 442).
        getBridge().addWebViewListener(new WebViewListener() {
            @Override
            public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
                try {
                    SharedPreferences p = getSharedPreferences(OmmSysPlugin.PREFS, Context.MODE_PRIVATE);
                    boolean crash = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && detail != null && detail.didCrash();
                    p.edit().putLong("goneT", System.currentTimeMillis()).putBoolean("goneCrash", crash)
                        .putInt("goneN", p.getInt("goneN", 0) + 1).apply();
                } catch (Exception ignored) {}
                try {
                    if (view != null) {
                        ViewGroup parent = (ViewGroup) view.getParent();
                        if (parent != null) parent.removeView(view);
                        view.destroy();
                    }
                } catch (Exception ignored) {}
                runOnUiThread(() -> recreate());
                return true;
            }
        });
        // Ścisła polityka CSP w OMM wymaga WebView, który wstrzykuje most Capacitora bez skryptu w HTML
        // (DOCUMENT_START_SCRIPT, Android System WebView/Chrome ok. 2021+). Starszy – prosimy o aktualizację.
        if (!WebViewFeature.isFeatureSupported(WebViewFeature.DOCUMENT_START_SCRIPT)) {
            new AlertDialog.Builder(this)
                .setTitle("Zaktualizuj Android System WebView")
                .setMessage(
                    "OMM wymaga aktualnego składnika „Android System WebView” (albo Chrome). " +
                    "Zaktualizuj go w Sklepie Play – bez tego aplikacja nie zadziała poprawnie, " +
                    "a stary WebView nie ma poprawek bezpieczeństwa."
                )
                .setPositiveButton("OK", null)
                .show();
        }
    }
}
