package io.github.ownmapmanager.omm;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
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
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
 * v94 (R04): writeTo po zapisie SPRAWDZA plik docelowy – suma SHA-256 odczytana z powrotem (gdy dostawca
 *            pozwala czytać) albo co najmniej rozmiar; niezgodność = błąd (JS zgłasza go, poprzednie kopie lokalne
 *            zostają). Dla pojedynczego pliku z „Zapisz w…” Android nie daje dostępu do folderu, więc pełnej
 *            podmiany przez plik tymczasowy się nie da – za to w folderze wybranym przez użytkownika (pickDir)
 *            zapis jest atomowy: plik „.part” → zmiana nazwy.
 * v94 (86):  pickDir / dirCheck / dirRelease / dirWrite – dowolny folder wybrany przez użytkownika
 *            (ACTION_OPEN_DOCUMENT_TREE, trwały dostęp tylko do tego drzewa) na pliki zapisywane ręcznie.
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
            // v94: zapis + odczyt – odczyt tylko do sprawdzenia zapisanej kopii (suma SHA-256)
            getContext().getContentResolver().takePersistableUriPermission(uri, W | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (SecurityException e) {
            try {
                getContext().getContentResolver().takePersistableUriPermission(uri, W); // sam zapis
            } catch (SecurityException e2) {
                // dostawca nie daje stałego dostępu – zapis zadziała tylko teraz
            }
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
                    MessageDigest md = MessageDigest.getInstance("SHA-256");
                    try (InputStream i = in; OutputStream o = out) {
                        byte[] buf = new byte[64 * 1024];
                        int k;
                        while ((k = i.read(buf)) > 0) {
                            o.write(buf, 0, k);
                            md.update(buf, 0, k);
                            n += k;
                        }
                    }
                    // v94 (R04): sprawdzenie zapisanego pliku
                    String ver = verify(u, md.digest(), n);
                    JSObject r = new JSObject();
                    r.put("bytes", n);
                    r.put("verified", ver);
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

    /** „sha” – suma zgodna, „size” – zgodny rozmiar (dostawca nie daje czytać), „none” – nie da się sprawdzić; błąd – niezgodność */
    private String verify(Uri u, byte[] want, long n) throws Exception {
        try (InputStream in = getContext().getContentResolver().openInputStream(u)) {
            if (in != null) {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] buf = new byte[64 * 1024];
                int k;
                long m = 0;
                while ((k = in.read(buf)) > 0) {
                    md.update(buf, 0, k);
                    m += k;
                }
                if (m != n || !MessageDigest.isEqual(md.digest(), want)) throw new Exception("Zapisana kopia różni się od źródła – zapis nieudany.");
                return "sha";
            }
        } catch (SecurityException e) {
            // brak prawa odczytu (plik wybrany przed v94) – sprawdzamy rozmiar
        }
        try (Cursor c = getContext().getContentResolver().query(u, new String[] { OpenableColumns.SIZE }, null, null, null)) {
            if (c != null && c.moveToFirst() && !c.isNull(0)) {
                long sz = c.getLong(0);
                if (sz > 0 && sz != n) throw new Exception("Zapisana kopia ma zły rozmiar – zapis nieudany.");
                if (sz == n) return "size";
            }
        } catch (SecurityException e) {
            // nic
        }
        return "none";
    }

    // ---------- v94 (86): folder wybrany przez użytkownika ----------
    @PluginMethod
    public void pickDir(PluginCall call) {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | W | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(call, i, "pickedDir");
    }

    @ActivityCallback
    private void pickedDir(PluginCall call, ActivityResult result) {
        if (call == null) return;
        Intent data = result.getData();
        if (result.getResultCode() != Activity.RESULT_OK || data == null || data.getData() == null) {
            call.reject("Anulowano wybór folderu.", "CANCELLED");
            return;
        }
        Uri tree = data.getData();
        try {
            getContext().getContentResolver().takePersistableUriPermission(tree, W | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (SecurityException e) {
            call.reject("Ten folder nie pozwala na stały dostęp – wybierz inny.");
            return;
        }
        JSObject r = new JSObject();
        r.put("uri", tree.toString());
        r.put("name", treeName(tree));
        call.resolve(r);
    }

    @PluginMethod
    public void dirCheck(PluginCall call) {
        String t = call.getString("uri");
        JSObject r = new JSObject();
        r.put("ok", t != null && persisted(Uri.parse(t)));
        call.resolve(r);
    }

    @PluginMethod
    public void dirRelease(PluginCall call) {
        release(call); // to samo – oddanie trwałego dostępu do drzewa
    }

    /**
     * dirWrite – zapis pliku (z pamięci podręcznej OMM) do podfolderu wybranego drzewa, bez nadpisywania:
     * „nazwa.ext”, „nazwa (2).ext”…; atomowo: najpierw „….part”, po zapisaniu i sprawdzeniu – zmiana nazwy.
     */
    @PluginMethod
    public void dirWrite(PluginCall call) {
        String t = call.getString("uri"), src = call.getString("src"), name = call.getString("name"), sub = call.getString("sub", "");
        String mime = call.getString("mime", "application/octet-stream");
        if (t == null || src == null || name == null || name.contains("/") || (sub != null && sub.contains(".."))) {
            call.reject("Złe parametry zapisu.");
            return;
        }
        getBridge()
            .execute(() -> {
                Uri part = null;
                try {
                    File f = new File(Uri.parse(src).getPath()).getCanonicalFile();
                    File cache = getContext().getCacheDir().getCanonicalFile();
                    if (!f.getPath().startsWith(cache.getPath() + File.separator)) throw new SecurityException("Plik spoza pamięci podręcznej OMM.");
                    if (!f.isFile()) throw new Exception("Brak pliku źródłowego.");
                    Uri tree = Uri.parse(t);
                    if (!persisted(tree)) throw new SecurityException("Brak dostępu do wybranego folderu – wybierz go ponownie w Ustawieniach.");
                    String dirId = DocumentsContract.getTreeDocumentId(tree);
                    String path = treeName(tree);
                    if (sub != null && !sub.isEmpty()) for (String s : sub.split("/")) if (!s.isEmpty()) {
                        dirId = childDir(tree, dirId, s);
                        path += "/" + s;
                    }
                    Set<String> names = childNames(tree, dirId);
                    int dot = name.lastIndexOf('.');
                    String stem = dot > 0 ? name.substring(0, dot) : name, ext = dot > 0 ? name.substring(dot) : "", fin = name;
                    for (int k = 2; names.contains(fin) && k < 1000; k++) fin = stem + " (" + k + ")" + ext;
                    Uri dir = DocumentsContract.buildDocumentUriUsingTree(tree, dirId);
                    part = DocumentsContract.createDocument(getContext().getContentResolver(), dir, "application/octet-stream", fin + ".part");
                    if (part == null) throw new Exception("Nie można utworzyć pliku w wybranym folderze.");
                    MessageDigest md = MessageDigest.getInstance("SHA-256");
                    long n = 0;
                    try (InputStream in = new FileInputStream(f); OutputStream out = getContext().getContentResolver().openOutputStream(part, "w")) {
                        if (out == null) throw new Exception("Nie można zapisać pliku.");
                        byte[] buf = new byte[64 * 1024];
                        int k;
                        while ((k = in.read(buf)) > 0) {
                            out.write(buf, 0, k);
                            md.update(buf, 0, k);
                            n += k;
                        }
                    }
                    verify(part, md.digest(), n);
                    Uri done = null;
                    try {
                        done = DocumentsContract.renameDocument(getContext().getContentResolver(), part, fin);
                    } catch (Exception e) {
                        // dostawca bez zmiany nazwy – kopia pod docelową nazwą
                    }
                    if (done == null) {
                        done = DocumentsContract.createDocument(getContext().getContentResolver(), dir, mime, fin);
                        if (done == null) throw new Exception("Nie można utworzyć pliku docelowego.");
                        try (InputStream in = new FileInputStream(f); OutputStream out = getContext().getContentResolver().openOutputStream(done, "w")) {
                            byte[] buf = new byte[64 * 1024];
                            int k;
                            while ((k = in.read(buf)) > 0) out.write(buf, 0, k);
                        }
                        DocumentsContract.deleteDocument(getContext().getContentResolver(), part);
                    }
                    part = null;
                    JSObject r = new JSObject();
                    r.put("name", fin);
                    r.put("path", path + "/" + fin);
                    r.put("bytes", n);
                    call.resolve(r);
                } catch (Exception e) {
                    if (part != null) try {
                        DocumentsContract.deleteDocument(getContext().getContentResolver(), part); // bez śmieci „.part”
                    } catch (Exception ignore) {}
                    call.reject(e.getMessage() != null ? e.getMessage() : e.toString());
                }
            });
    }

    private String childDir(Uri tree, String parentId, String name) throws Exception {
        Uri ch = DocumentsContract.buildChildDocumentsUriUsingTree(tree, parentId);
        try (Cursor c = getContext().getContentResolver().query(ch, new String[] { DocumentsContract.Document.COLUMN_DOCUMENT_ID, DocumentsContract.Document.COLUMN_DISPLAY_NAME, DocumentsContract.Document.COLUMN_MIME_TYPE }, null, null, null)) {
            while (c != null && c.moveToNext()) if (name.equals(c.getString(1)) && DocumentsContract.Document.MIME_TYPE_DIR.equals(c.getString(2))) return c.getString(0);
        }
        Uri d = DocumentsContract.createDocument(getContext().getContentResolver(), DocumentsContract.buildDocumentUriUsingTree(tree, parentId), DocumentsContract.Document.MIME_TYPE_DIR, name);
        if (d == null) throw new Exception("Nie można utworzyć folderu " + name + ".");
        return DocumentsContract.getDocumentId(d);
    }

    private Set<String> childNames(Uri tree, String parentId) {
        Set<String> out = new HashSet<>();
        Uri ch = DocumentsContract.buildChildDocumentsUriUsingTree(tree, parentId);
        try (Cursor c = getContext().getContentResolver().query(ch, new String[] { DocumentsContract.Document.COLUMN_DISPLAY_NAME }, null, null, null)) {
            while (c != null && c.moveToNext()) out.add(c.getString(0));
        } catch (Exception e) {
            // brak listy – najwyżej dostawca sam nada inną nazwę
        }
        return out;
    }

    private String treeName(Uri tree) {
        try {
            Uri d = DocumentsContract.buildDocumentUriUsingTree(tree, DocumentsContract.getTreeDocumentId(tree));
            try (Cursor c = getContext().getContentResolver().query(d, new String[] { DocumentsContract.Document.COLUMN_DISPLAY_NAME }, null, null, null)) {
                if (c != null && c.moveToFirst()) return c.getString(0);
            }
        } catch (Exception e) {
            // nic
        }
        return "Folder";
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
