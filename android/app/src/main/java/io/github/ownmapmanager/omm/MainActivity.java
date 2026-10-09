package io.github.ownmapmanager.omm;

import android.app.AlertDialog;
import android.os.Bundle;
import android.webkit.WebSettings;
import androidx.webkit.WebViewFeature;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(OmmBackupPlugin.class); // kopia zapasowa w miejscu wybranym przez użytkownika (np. Dysk Google)
        registerPlugin(OmmLockPlugin.class); // blokada OMM odciskiem palca / twarzą
        super.onCreate(savedInstanceState);
        // Tylko https: żadnych treści po HTTP na stronie aplikacji (audyt v45, R02).
        getBridge().getWebView().getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
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
