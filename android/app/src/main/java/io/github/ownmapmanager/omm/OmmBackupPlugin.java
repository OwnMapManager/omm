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
 * release  – „Odłącz” / zmiana miejsca: oddaje Androidowi trwałe uprawnienie do pliku.
 *
 * Bezpieczeństwo (audyt v45, F05/R08): OMM trzyma tylko prawo ZAPISU do wybranego pliku,
 * writeTo zapisuje wyłącznie do plików, do których użytkownik dał trwały dostęp, a docelowy
 * plik jest otwierany do nadpisania dopiero, gdy kopia źródłowa jest gotowa do odczytu.
 *
 * Autor: Paweł Jewuła · OMM – Own Map Manager
 */
@CapacitorPlugin(name = "OmmBackup")
public class OmmBackupPlugin extends Plugin {

    private static final int W = Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

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
            getContext().getContentResolver().takePersistableUriPermission(uri, W); // tylko zapis – odczyt niepotrzebny
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
                    if (!f.isFile() || f.length() == 0) throw new Exception("Kopia źródłowa jest pusta albo jej brak.");
                    Uri u = Uri.parse(target);
                    // zapis tylko do pliku wybranego wcześniej przez użytkownika (trwały dostęp w Androidzie)
                    if (!persisted(u)) throw new SecurityException("Brak dostępu do wybranego pliku kopii.");
                    long n = 0;
                    InputStream in = new FileInputStream(f); // źródło otwarte PRZED nadpisaniem celu
                    OutputStream out;
                    try {
                        try {
                            out = getContext().getContentResolver().openOutputStream(u, "wt"); // nadpisanie od zera
                        } catch (Exception e) {
                            out = getContext().getContentResolver().openOutputStream(u, "w"); // dostawca bez trybu „wt”
                        }
                    } catch (Exception e) {
                        in.close();
                        throw e;
                    }
                    if (out == null) {
                        in.close();
                        throw new Exception("Nie można otworzyć pliku do zapisu.");
                    }
                    try (InputStream i = in; OutputStream o = out) {
                        byte[] buf = new byte[64 * 1024];
                        int k;
                        while ((k = i.read(buf)) > 0) {
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

    @PluginMethod
    public void release(PluginCall call) {
        String target = call.getString("uri");
        JSObject r = new JSObject();
        if (target == null) {
            call.reject("Brak parametru uri.");
            return;
        }
        Uri u = Uri.parse(target);
        int flags = 0;
        for (android.content.UriPermission p : getContext().getContentResolver().getPersistedUriPermissions()) if (p.getUri().equals(u)) {
            if (p.isReadPermission()) flags |= Intent.FLAG_GRANT_READ_URI_PERMISSION;
            if (p.isWritePermission()) flags |= Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
        }
        try {
            if (flags != 0) getContext().getContentResolver().releasePersistableUriPermission(u, flags);
            r.put("released", flags != 0);
            r.put("ok", !persisted(u));
            call.resolve(r);
        } catch (Exception e) {
            call.reject(e.getMessage() != null ? e.getMessage() : e.toString());
        }
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
