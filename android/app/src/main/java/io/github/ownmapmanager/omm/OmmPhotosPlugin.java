package io.github.ownmapmanager.omm;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.content.UriPermission;
import androidx.activity.result.ActivityResult;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Zdjęcia z telefonu RAZEM z miejscem zrobienia (v79).
 *
 * Od Androida 10 system wycina współrzędne GPS z plików zdjęć wybieranych zwykłym oknem wyboru
 * (tak działa <input type="file"> w WebView). Oryginał z lokalizacją dostaje tylko aplikacja
 * z uprawnieniem ACCESS_MEDIA_LOCATION, która prosi o niego wprost (MediaStore.setRequireOriginal).
 *
 * pick    – systemowe okno wyboru plików (ACTION_OPEN_DOCUMENT, tylko obrazy, jeden albo wiele).
 *           Przed pierwszym wyborem prosi o zgodę „dostęp do lokalizacji w multimediach”; bez zgody
 *           działa dalej, tylko bez lokalizacji. Wybrane zdjęcia są kopiowane (oryginalne bajty)
 *           do pamięci podręcznej OMM; zwraca ścieżki kopii + odczytane współrzędne.
 * camera  – (v95) otwiera zwykłą aplikację aparatu; po powrocie OMM proponuje pick.
 * cleanup – usuwa te kopie (OMM zapisuje u siebie własną, zmniejszoną kopię zdjęcia).
 *
 * v137 (pkt 467) – automatyczne zdjęcia z wybranego folderu:
 * pickFolder   – systemowe okno wyboru FOLDERU (ACTION_OPEN_DOCUMENT_TREE); Android daje dostęp tylko do odczytu
 *                tego jednego folderu, zapamiętany na stałe (takePersistableUriPermission). Bez READ_MEDIA_IMAGES –
 *                OMM nie widzi reszty galerii.
 * scanFolder   – lista zdjęć w folderze (bez kopiowania): id, nazwa, czas zmiany, rozmiar; tylko nowsze niż „since”.
 * readFiles    – kopie wskazanych zdjęć z folderu (oryginał z lokalizacją, jak pick) do pamięci podręcznej.
 * forgetFolder – oddaje dostęp do folderu (usunięcie źródła w Ustawieniach).
 *
 * Bezpieczeństwo: OMM nie dostaje dostępu do całej galerii – tylko do zdjęć wskazanych przez
 * użytkownika w oknie systemowym (jednorazowo). Uprawnienie dotyczy wyłącznie odczytu lokalizacji
 * zapisanej w tych zdjęciach. cleanup usuwa tylko pliki z własnego folderu w pamięci podręcznej.
 *
 * Autor: Paweł Jewuła · OMM – Own Map Manager
 */
@CapacitorPlugin(
    name = "OmmPhotos",
    permissions = { @Permission(strings = { Manifest.permission.ACCESS_MEDIA_LOCATION }, alias = "mediaLocation") }
)
public class OmmPhotosPlugin extends Plugin {

    private static final String DIR = "omm-foto";

    @PluginMethod
    public void pick(PluginCall call) {
        if (Build.VERSION.SDK_INT >= 29 && getPermissionState("mediaLocation") != PermissionState.GRANTED) {
            requestPermissionForAlias("mediaLocation", call, "permDone");
            return;
        }
        launch(call);
    }

    /**
     * v95: zwykła aplikacja aparatu telefonu (pełne tryby, zdjęcie zapisuje się w galerii jak zawsze).
     * Bez uprawnień i bez dostępu do zdjęć – OMM tylko otwiera aparat; po powrocie użytkownik sam
     * wskazuje zrobione zdjęcia w systemowym oknie wyboru (pick).
     */
    @PluginMethod
    public void camera(PluginCall call) {
        try {
            Intent i = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
            startActivityForResult(call, i, "cameraBack");
        } catch (Exception e) {
            call.reject("brak aplikacji aparatu");
        }
    }

    @ActivityCallback
    private void cameraBack(PluginCall call, ActivityResult result) {
        if (call == null) return;
        call.resolve(new JSObject());
    }

    @PermissionCallback
    private void permDone(PluginCall call) {
        launch(call); // także po odmowie – wtedy zdjęcia bez lokalizacji
    }

    private void launch(PluginCall call) {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, call.getBoolean("multi", false));
        startActivityForResult(call, i, "picked");
    }

    @ActivityCallback
    private void picked(PluginCall call, ActivityResult result) {
        if (call == null) return;
        Intent data = result.getData();
        if (result.getResultCode() != Activity.RESULT_OK || data == null) {
            JSObject r = new JSObject();
            r.put("files", new JSArray());
            call.resolve(r);
            return;
        }
        final List<Uri> uris = new ArrayList<>();
        ClipData cd = data.getClipData();
        if (cd != null) for (int k = 0; k < cd.getItemCount(); k++) uris.add(cd.getItemAt(k).getUri());
        else if (data.getData() != null) uris.add(data.getData());
        final boolean loc = Build.VERSION.SDK_INT < 29 || getPermissionState("mediaLocation") == PermissionState.GRANTED;
        getBridge()
            .execute(() -> {
                JSArray arr = new JSArray();
                File dir = new File(getContext().getCacheDir(), DIR);
                dir.mkdirs();
                int n = 0;
                for (Uri u : uris) {
                    if (u == null) continue;
                    try {
                        String name = displayName(u);
                        File out = new File(dir, System.currentTimeMillis() + "_" + (n++) + "_" + safe(name));
                        boolean orig = copy(original(u, loc), u, out);
                        JSObject o = new JSObject();
                        o.put("path", out.getAbsolutePath());
                        o.put("name", name);
                        o.put("mime", getContext().getContentResolver().getType(u));
                        o.put("original", orig);
                        try {
                            ExifInterface ex = new ExifInterface(out.getAbsolutePath());
                            float[] ll = new float[2];
                            if (ex.getLatLong(ll) && !(ll[0] == 0f && ll[1] == 0f)) {
                                o.put("lat", (double) ll[0]);
                                o.put("lng", (double) ll[1]);
                            }
                        } catch (Exception e) {
                            // brak EXIF (np. PNG) – bez lokalizacji
                        }
                        arr.put(o);
                    } catch (Exception e) {
                        // pojedyncze zdjęcie się nie skopiowało – pomijamy, reszta idzie dalej
                    }
                }
                JSObject r = new JSObject();
                r.put("files", arr);
                r.put("location", loc);
                call.resolve(r);
            });
    }

    /** Adres oryginału z lokalizacją (gdy wolno), inaczej null. */
    private Uri original(Uri u, boolean loc) {
        if (!loc || Build.VERSION.SDK_INT < 29) return null;
        try {
            Uri m = u;
            try {
                Uri x = MediaStore.getMediaUri(getContext(), u); // dokument z „Plików” → wpis w MediaStore
                if (x != null) m = x;
            } catch (Exception e) {
                // to nie jest zdjęcie z MediaStore (np. z chmury) – zostaje adres dokumentu
            }
            return MediaStore.setRequireOriginal(m);
        } catch (Exception e) {
            return null;
        }
    }

    /** Kopiuje bajty zdjęcia; najpierw oryginał z lokalizacją, w razie odmowy – zwykły odczyt. Zwraca true, gdy oryginał. */
    private boolean copy(Uri orig, Uri plain, File out) throws Exception {
        if (orig != null) {
            try (InputStream in = getContext().getContentResolver().openInputStream(orig)) {
                if (in != null) {
                    write(in, out);
                    return true;
                }
            } catch (Exception e) {
                // brak dostępu do oryginału – zwykły odczyt niżej
            }
        }
        try (InputStream in = getContext().getContentResolver().openInputStream(plain)) {
            if (in == null) throw new Exception("Nie można otworzyć zdjęcia.");
            write(in, out);
        }
        return false;
    }

    private void write(InputStream in, File out) throws Exception {
        try (OutputStream o = new FileOutputStream(out)) {
            byte[] buf = new byte[64 * 1024];
            int k;
            long n = 0;
            while ((k = in.read(buf)) > 0) {
                o.write(buf, 0, k);
                n += k;
                if (n > 80L * 1024 * 1024) throw new Exception("Zdjęcie jest za duże."); // ochrona pamięci telefonu
            }
        }
    }

    @PluginMethod
    public void cleanup(PluginCall call) {
        getBridge()
            .execute(() -> {
                int n = 0;
                try {
                    File dir = new File(getContext().getCacheDir(), DIR).getCanonicalFile();
                    File[] fs = dir.listFiles();
                    if (fs != null) for (File f : fs) if (f.getCanonicalFile().getParentFile().equals(dir) && f.delete()) n++;
                } catch (Exception e) {
                    // nic do sprzątania
                }
                JSObject r = new JSObject();
                r.put("deleted", n);
                call.resolve(r);
            });
    }

    // ---------- v137 (pkt 467): automatyczne zdjęcia z wybranego folderu ----------

    @PluginMethod
    public void pickFolder(PluginCall call) {
        if (Build.VERSION.SDK_INT >= 29 && getPermissionState("mediaLocation") != PermissionState.GRANTED) {
            requestPermissionForAlias("mediaLocation", call, "permFolder");
            return;
        }
        launchTree(call);
    }

    @PermissionCallback
    private void permFolder(PluginCall call) {
        launchTree(call); // także po odmowie – wtedy zdjęcia bez lokalizacji
    }

    private void launchTree(PluginCall call) {
        try {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            if (Build.VERSION.SDK_INT >= 26)
                try {
                    // zaczynamy od folderu aparatu (DCIM/Camera) – najczęstszy wybór
                    i.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse("content://com.android.externalstorage.documents/document/primary%3ADCIM%2FCamera"));
                } catch (Exception e) {
                    // bez podpowiedzi folderu
                }
            startActivityForResult(call, i, "folderPicked");
        } catch (Exception e) {
            call.reject("brak okna wyboru folderu");
        }
    }

    @ActivityCallback
    private void folderPicked(PluginCall call, ActivityResult result) {
        if (call == null) return;
        Intent data = result.getData();
        JSObject r = new JSObject();
        if (result.getResultCode() != Activity.RESULT_OK || data == null || data.getData() == null) {
            call.resolve(r);
            return;
        }
        Uri tree = data.getData();
        try {
            getContext().getContentResolver().takePersistableUriPermission(tree, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (Exception e) {
            call.reject("Android nie dał stałego dostępu do folderu");
            return;
        }
        r.put("uri", tree.toString());
        r.put("name", treeName(tree));
        r.put("location", Build.VERSION.SDK_INT < 29 || getPermissionState("mediaLocation") == PermissionState.GRANTED);
        call.resolve(r);
    }

    private String treeName(Uri tree) {
        try {
            String id = DocumentsContract.getTreeDocumentId(tree); // np. „primary:DCIM/Camera”
            int k = id.indexOf(':');
            String p = k >= 0 ? id.substring(k + 1) : id;
            return p.isEmpty() ? "/" : p;
        } catch (Exception e) {
            return "folder";
        }
    }

    private boolean granted(Uri tree) {
        for (UriPermission up : getContext().getContentResolver().getPersistedUriPermissions())
            if (up.isReadPermission() && up.getUri().equals(tree)) return true;
        return false;
    }

    @PluginMethod
    public void scanFolder(PluginCall call) {
        final String u = call.getString("uri", "");
        final long since = (long) (double) call.getDouble("since", 0.0);
        final int max = call.getInt("max", 300);
        getBridge()
            .execute(() -> {
                try {
                    Uri tree = Uri.parse(u);
                    if (!granted(tree)) {
                        call.reject("brak dostępu do folderu");
                        return;
                    }
                    Uri kids = DocumentsContract.buildChildDocumentsUriUsingTree(tree, DocumentsContract.getTreeDocumentId(tree));
                    String[] cols = {
                        DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                        DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                        DocumentsContract.Document.COLUMN_MIME_TYPE,
                        DocumentsContract.Document.COLUMN_LAST_MODIFIED,
                        DocumentsContract.Document.COLUMN_SIZE
                    };
                    List<JSObject> all = new ArrayList<>();
                    try (Cursor c = getContext().getContentResolver().query(kids, cols, null, null, null)) {
                        while (c != null && c.moveToNext()) {
                            String mime = c.getString(2);
                            long t = c.isNull(3) ? 0 : c.getLong(3);
                            if (mime == null || !mime.startsWith("image/") || t < since) continue;
                            JSObject o = new JSObject();
                            o.put("id", c.getString(0));
                            o.put("name", c.getString(1));
                            o.put("t", t);
                            o.put("size", c.isNull(4) ? 0 : c.getLong(4));
                            all.add(o);
                        }
                    }
                    all.sort((a, b) -> Long.compare(b.optLong("t"), a.optLong("t"))); // najnowsze najpierw
                    JSArray arr = new JSArray();
                    for (int k = 0; k < all.size() && k < max; k++) arr.put(all.get(k));
                    JSObject r = new JSObject();
                    r.put("files", arr);
                    r.put("total", all.size());
                    call.resolve(r);
                } catch (Exception e) {
                    call.reject("nie można odczytać folderu: " + e.getMessage());
                }
            });
    }

    @PluginMethod
    public void readFiles(PluginCall call) {
        final String u = call.getString("uri", "");
        final JSArray ids = call.getArray("ids", new JSArray());
        final boolean loc = Build.VERSION.SDK_INT < 29 || getPermissionState("mediaLocation") == PermissionState.GRANTED;
        getBridge()
            .execute(() -> {
                JSArray arr = new JSArray();
                try {
                    Uri tree = Uri.parse(u);
                    if (!granted(tree)) {
                        call.reject("brak dostępu do folderu");
                        return;
                    }
                    File dir = new File(getContext().getCacheDir(), DIR);
                    dir.mkdirs();
                    for (int k = 0; k < ids.length() && k < 300; k++) {
                        try {
                            String id = ids.getString(k);
                            Uri d = DocumentsContract.buildDocumentUriUsingTree(tree, id);
                            String name = displayName(d);
                            File out = new File(dir, System.currentTimeMillis() + "_" + k + "_" + safe(name));
                            boolean orig = copy(original(d, loc), d, out);
                            JSObject o = new JSObject();
                            o.put("id", id);
                            o.put("path", out.getAbsolutePath());
                            o.put("name", name);
                            o.put("mime", getContext().getContentResolver().getType(d));
                            o.put("original", orig);
                            try {
                                ExifInterface ex = new ExifInterface(out.getAbsolutePath());
                                float[] ll = new float[2];
                                if (ex.getLatLong(ll) && !(ll[0] == 0f && ll[1] == 0f)) {
                                    o.put("lat", (double) ll[0]);
                                    o.put("lng", (double) ll[1]);
                                }
                            } catch (Exception e) {
                                // brak EXIF – bez lokalizacji
                            }
                            arr.put(o);
                        } catch (Exception e) {
                            // pojedyncze zdjęcie się nie skopiowało – reszta idzie dalej
                        }
                    }
                } catch (Exception e) {
                    call.reject("nie można odczytać folderu: " + e.getMessage());
                    return;
                }
                JSObject r = new JSObject();
                r.put("files", arr);
                r.put("location", loc);
                call.resolve(r);
            });
    }

    @PluginMethod
    public void forgetFolder(PluginCall call) {
        try {
            getContext().getContentResolver().releasePersistableUriPermission(Uri.parse(call.getString("uri", "")), Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (Exception e) {
            // już nie było dostępu
        }
        call.resolve(new JSObject());
    }

    private String displayName(Uri uri) {
        try (Cursor c = getContext().getContentResolver().query(uri, new String[] { OpenableColumns.DISPLAY_NAME }, null, null, null)) {
            if (c != null && c.moveToFirst() && c.getString(0) != null) return c.getString(0);
        } catch (Exception e) {
            // brak nazwy – nieistotne
        }
        return "zdjecie.jpg";
    }

    private static String safe(String s) {
        String t = s.replaceAll("[^A-Za-z0-9._-]", "_");
        return t.length() > 80 ? t.substring(t.length() - 80) : t;
    }
}
