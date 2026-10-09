package io.github.ownmapmanager.omm;

import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

/**
 * Blokada OMM odciskiem palca lub twarzą (systemowe okno biometrii Androida).
 *
 * available    – czy telefon ma biometrię i czy jest w niej zapisany odcisk / twarz,
 * authenticate – pokazuje systemowe okno; sukces = rozwiązanie, „Użyj PIN-u” / anulowanie / błąd = odrzucenie.
 *
 * OMM nie dostaje żadnych danych biometrycznych – tylko informację „rozpoznano / nie”.
 * Zapasowy PIN OMM jest zawsze ustawiony (na wypadek mokrych palców, rękawiczek, awarii czytnika).
 *
 * Autor: Paweł Jewuła · OMM – Own Map Manager
 */
@CapacitorPlugin(name = "OmmLock")
public class OmmLockPlugin extends Plugin {

    private static final int AUTH = BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK;

    @PluginMethod
    public void available(PluginCall call) {
        int r = BiometricManager.from(getContext()).canAuthenticate(AUTH);
        JSObject o = new JSObject();
        o.put("bio", r == BiometricManager.BIOMETRIC_SUCCESS);
        o.put("code", r);
        call.resolve(o);
    }

    @PluginMethod
    public void authenticate(PluginCall call) {
        final String title = call.getString("title", "Odblokuj OMM");
        final String sub = call.getString("subtitle", "Odcisk palca lub twarz");
        getActivity()
            .runOnUiThread(() -> {
                try {
                    BiometricPrompt.PromptInfo info = new BiometricPrompt.PromptInfo.Builder()
                        .setTitle(title)
                        .setSubtitle(sub)
                        .setNegativeButtonText("Użyj PIN-u")
                        .setAllowedAuthenticators(AUTH)
                        .build();
                    BiometricPrompt p = new BiometricPrompt(
                        (FragmentActivity) getActivity(),
                        ContextCompat.getMainExecutor(getContext()),
                        new BiometricPrompt.AuthenticationCallback() {
                            @Override
                            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                                JSObject o = new JSObject();
                                o.put("ok", true);
                                call.resolve(o);
                            }

                            @Override
                            public void onAuthenticationError(int code, CharSequence msg) {
                                call.reject(String.valueOf(msg), String.valueOf(code));
                            }
                        }
                    );
                    p.authenticate(info);
                } catch (Exception e) {
                    call.reject(e.getMessage() != null ? e.getMessage() : e.toString());
                }
            });
    }
}
