package io.github.ownmapmanager.omm;

import android.os.Bundle;
import android.webkit.WebSettings;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(OmmBackupPlugin.class); // kopia zapasowa w miejscu wybranym przez użytkownika (np. Dysk Google)
        super.onCreate(savedInstanceState);
        // Treści mieszane w trybie zgodności: obrazki po HTTP (zapasowe kafle Geoportalu) są dozwolone,
        // skrypty, ramki i zapytania po HTTP – blokowane.
        getBridge().getWebView().getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
    }
}
