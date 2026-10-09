package io.github.ownmapmanager.omm;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.security.KeyStore;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Blokada OMM odciskiem palca lub twarzą (systemowe okno biometrii Androida).
 *
 * available    – czy telefon ma silną biometrię (klasa 3) i zapisany odcisk / twarz,
 * enroll       – tworzy klucz w sprzętowym magazynie Androida (AndroidKeyStore), używalny tylko po biometrii;
 *                OMM woła to wyłącznie po podaniu PIN-u OMM albo przy włączaniu blokady,
 * authenticate – systemowe okno; odblokowanie = biometria odblokowała klucz i klucz zaszyfrował próbkę.
 *                Dodanie nowego odcisku / twarzy w telefonie unieważnia klucz → „INVALIDATED” (wtedy PIN OMM),
 *                brak klucza → „NOKEY”,
 * remove       – usuwa klucz (wyłączenie blokady),
 * recents      – Android 13+: ukrywa podgląd OMM w „ostatnich aplikacjach” (zrzuty ekranu dalej działają).
 *
 * OMM nie dostaje żadnych danych biometrycznych – tylko informację „rozpoznano / nie”.
 *
 * Autor: Paweł Jewuła · OMM – Own Map Manager
 */
@CapacitorPlugin(name = "OmmLock")
public class OmmLockPlugin extends Plugin {

    private static final int AUTH = BiometricManager.Authenticators.BIOMETRIC_STRONG;
    private static final String KEY = "omm-lock-bio";
    private static final String TR = "AES/GCM/NoPadding";

    private KeyStore ks() throws Exception {
        KeyStore k = KeyStore.getInstance("AndroidKeyStore");
        k.load(null);
        return k;
    }

    @PluginMethod
    public void available(PluginCall call) {
        int r = BiometricManager.from(getContext()).canAuthenticate(AUTH);
        JSObject o = new JSObject();
        o.put("bio", r == BiometricManager.BIOMETRIC_SUCCESS);
        o.put("code", r);
        call.resolve(o);
    }

    @PluginMethod
    public void enroll(PluginCall call) {
        try {
            KeyStore k = ks();
            if (k.containsAlias(KEY)) k.deleteEntry(KEY);
            KeyGenParameterSpec.Builder b = new KeyGenParameterSpec.Builder(
                KEY,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setUserAuthenticationRequired(true)
                .setInvalidatedByBiometricEnrollment(true);
            if (Build.VERSION.SDK_INT >= 30) b.setUserAuthenticationParameters(0, KeyProperties.AUTH_BIOMETRIC_STRONG);
            KeyGenerator g = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            g.init(b.build());
            g.generateKey();
            call.resolve();
        } catch (Exception e) {
            call.reject("Nie udało się utworzyć klucza biometrii: " + e, "ENROLL");
        }
    }

    @PluginMethod
    public void remove(PluginCall call) {
        try {
            KeyStore k = ks();
            if (k.containsAlias(KEY)) k.deleteEntry(KEY);
        } catch (Exception e) {}
        call.resolve();
    }

    @PluginMethod
    public void recents(PluginCall call) {
        final boolean hide = Boolean.TRUE.equals(call.getBoolean("hide", false));
        if (Build.VERSION.SDK_INT >= 33) getActivity().runOnUiThread(() -> getActivity().setRecentsScreenshotEnabled(!hide));
        call.resolve();
    }

    @PluginMethod
    public void authenticate(PluginCall call) {
        final String title = call.getString("title", "Odblokuj OMM");
        final String sub = call.getString("subtitle", "Odcisk palca lub twarz");
        final Cipher cipher;
        try {
            KeyStore k = ks();
            if (!k.containsAlias(KEY)) {
                call.reject("Brak klucza biometrii", "NOKEY");
                return;
            }
            cipher = Cipher.getInstance(TR);
            cipher.init(Cipher.ENCRYPT_MODE, (SecretKey) k.getKey(KEY, null));
        } catch (KeyPermanentlyInvalidatedException e) {
            try {
                ks().deleteEntry(KEY);
            } catch (Exception x) {}
            call.reject("Zmieniono biometrię w telefonie", "INVALIDATED");
            return;
        } catch (Exception e) {
            call.reject(e.getMessage() != null ? e.getMessage() : e.toString());
            return;
        }
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
                                try {
                                    // dowód: klucz odblokowany biometrią naprawdę zadziałał
                                    BiometricPrompt.CryptoObject co = result.getCryptoObject();
                                    if (co == null || co.getCipher() == null) throw new IllegalStateException("brak klucza");
                                    co.getCipher().doFinal(new byte[16]);
                                    JSObject o = new JSObject();
                                    o.put("ok", true);
                                    call.resolve(o);
                                } catch (Exception e) {
                                    call.reject("Biometria nie odblokowała klucza", "CRYPTO");
                                }
                            }

                            @Override
                            public void onAuthenticationError(int code, CharSequence msg) {
                                call.reject(String.valueOf(msg), String.valueOf(code));
                            }
                        }
                    );
                    p.authenticate(info, new BiometricPrompt.CryptoObject(cipher));
                } catch (Exception e) {
                    call.reject(e.getMessage() != null ? e.getMessage() : e.toString());
                }
            });
    }
}
