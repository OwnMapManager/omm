package io.github.ownmapmanager.omm;

import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

/**
 * Zastępuje wbudowaną wtyczkę Capacitora „WebView” (audyt wewnętrzny v48, pkt 4).
 * Oryginał pozwala skryptowi na stronie trwale podmienić katalog, z którego ładuje się aplikacja
 * (setServerBasePath / persistServerBasePath). OMM tego nie używa, więc wszystkie wywołania są odrzucane.
 * Rejestrowana w MainActivity po wtyczkach wbudowanych – ta sama nazwa nadpisuje oryginał.
 * Dodatkowo „DisableDeploy” w capacitor.config.json wyłącza wczytywanie zapisanej ścieżki przy starcie.
 */
@CapacitorPlugin(name = "WebView")
public class OmmNoWebViewPlugin extends Plugin {

    private void no(PluginCall call) {
        call.reject("Wyłączone w OMM", "DISABLED");
    }

    @PluginMethod
    public void setServerAssetPath(PluginCall call) {
        no(call);
    }

    @PluginMethod
    public void setServerBasePath(PluginCall call) {
        no(call);
    }

    @PluginMethod
    public void getServerBasePath(PluginCall call) {
        no(call);
    }

    @PluginMethod
    public void persistServerBasePath(PluginCall call) {
        no(call);
    }
}
