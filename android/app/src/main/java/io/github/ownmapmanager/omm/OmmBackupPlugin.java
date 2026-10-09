package io.github.ownmapmanager.omm;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import androidx.activity.result.ActivityResult;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Kopia zapasowa OMM w miejscu wskazanym przez użytkownika (np. na Dysku Google).
 *
 * pickFile – systemowe okno „Zapisz w…” (ACTION_CREATE_DOCUMENT). Użytkownik sam wybiera miejsce
 *            i nazwę pliku; aplikacja dostaje stały dostęp tylko do TEGO jednego pliku.
 * writeTo  – nadpisuje ten plik gotową kopią ZIP z pamięci podręcznej aplikacji. Wysyłką na Dysk
 *            zajmuje się aplikacja Dysk Google (bez logowania i kluczy API w OMM).
 * check    – czy dostęp do pliku nadal jest (użytkownik mógł go cofnąć albo usunąć plik).
 *
 * Autor: Paweł Jewuła · OMM – Own Map Manager
 */
@CapacitorPlugin(name = "OmmBackup")
public class OmmBackupPlugin extends Plugin {

    private static final int RW = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

    @PluginMethod
    public void pickFile(PluginCall call) {
        Intent i = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("application/zip");
        i.putExtra(Intent.EXTRA_TITLE, call.getString("name", "OMM-kopia.zip"));
        startActivityForResult(call, i, "picked");
    }

    @ActivityCallback
    private void picked(PluginCall call, ActivityResult result) {
        if (call == null) return;
        Intent data = result.getData();
        if (result.getResultCode() != Activity.RESULT_OK || data == null || data.getData() == null) {
            call.reject("Anulowano wybór pliku.", "CANCELLED");
            return;
        }
        Uri uri = data.getData();
        try {
            getContext().getContentResolver().takePersistableUriPermission(uri, RW);
        } catch (SecurityException e) {
            // dostawca nie daje stałego dostępu – zapis zadziała tylko teraz
        }
        JSObject r = new JSObject();
        r.put("uri", uri.toString());
        r.put("name", displayName(uri));
        r.put("persisted", persisted(uri));
        call.resolve(r);
    }

    @PluginMethod
    public void writeTo(PluginCall call) {
        String target = call.getString("uri"), src = call.getString("src");
        if (target == null || src == null) {
            call.reject("Brak parametrów uri / src.");
            return;
        }
        getBridge()
            .execute(() -> {
                try {
                    // źródło musi leżeć w pamięci podręcznej OMM (tam JS zapisuje gotową kopię)
                    File f = new File(Uri.parse(src).getPath()).getCanonicalFile();
                    File cache = getContext().getCacheDir().getCanonicalFile();
                    if (!f.getPath().startsWith(cache.getPath() + File.separator)) throw new SecurityException("Plik spoza pamięci podręcznej OMM.");
                    long n = 0;
                    Uri u = Uri.parse(target);
                    OutputStream out;
                    try {
                        out = getContext().getContentResolver().openOutputStream(u, "wt"); // nadpisanie od zera
                    } catch (Exception e) {
                        out = getContext().getContentResolver().openOutputStream(u, "w"); // dostawca bez trybu „wt”
                    }
                    if (out == null) throw new Exception("Nie można otworzyć pliku do zapisu.");
                    try (InputStream in = new FileInputStream(f); OutputStream o = out) {
                        byte[] buf = new byte[64 * 1024];
                        int k;
                        while ((k = in.read(buf)) > 0) {
                            o.write(buf, 0, k);
                            n += k;
                        }
                    }
                    JSObject r = new JSObject();
                    r.put("bytes", n);
                    call.resolve(r);
                } catch (Exception e) {
                    call.reject(e.getMessage() != null ? e.getMessage() : e.toString());
                }
            });
    }

    @PluginMethod
    public void check(PluginCall call) {
        String target = call.getString("uri");
        JSObject r = new JSObject();
        r.put("ok", target != null && persisted(Uri.parse(target)));
        call.resolve(r);
    }

    private boolean persisted(Uri uri) {
        List<android.content.UriPermission> list = getContext().getContentResolver().getPersistedUriPermissions();
        for (android.content.UriPermission p : list) if (p.getUri().equals(uri) && p.isWritePermission()) return true;
        return false;
    }

    private String displayName(Uri uri) {
        try (Cursor c = getContext().getContentResolver().query(uri, new String[] { OpenableColumns.DISPLAY_NAME }, null, null, null)) {
            if (c != null && c.moveToFirst()) return c.getString(0);
        } catch (Exception e) {
            // brak nazwy – nieistotne
        }
        return "OMM-kopia.zip";
    }
}
