package io.github.mthli.Ninja.Unit;

import android.app.DownloadManager;
import android.content.*;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.webkit.*;
import io.github.mthli.Ninja.Database.Record;
import io.github.mthli.Ninja.Database.RecordAction;
import io.github.mthli.Ninja.R;
import io.github.mthli.Ninja.View.NinjaToast;
import org.json.JSONObject;

import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Pattern;

public class BrowserUnit {
    public static final int PROGRESS_MAX = 100;
    public static final int PROGRESS_MIN = 0;
    public static final String SUFFIX_PNG = ".png";
    public static final String SUFFIX_TXT = ".txt";

    public static final int FLAG_BOOKMARKS = 0x100;
    public static final int FLAG_HISTORY = 0x101;
    public static final int FLAG_HOME = 0x102;
    public static final int FLAG_NINJA = 0x103;

    public static final String MIME_TYPE_TEXT_PLAIN = "text/plain";
    public static final String MIME_TYPE_IMAGE = "image/*";

    public static final String SEARCH_ENGINE_GOOGLE = "https://www.google.com/search?q=";
    public static final String SEARCH_ENGINE_DUCKDUCKGO = "https://duckduckgo.com/?q=";
    public static final String SEARCH_ENGINE_STARTPAGE = "https://startpage.com/do/search?query=";
    public static final String SEARCH_ENGINE_BING = "http://www.bing.com/search?q=";
    public static final String SEARCH_ENGINE_BAIDU = "http://www.baidu.com/s?wd=";

    public static final String URL_ENCODING = "UTF-8";
    public static final String URL_ABOUT_BLANK = "about:blank";
    public static final String URL_SCHEME_ABOUT = "about:";
    public static final String URL_SCHEME_MAIL_TO = "mailto:";
    public static final String URL_SCHEME_FILE = "file://";
    public static final String URL_SCHEME_FTP = "ftp://";
    public static final String URL_SCHEME_HTTP = "http://";
    public static final String URL_SCHEME_HTTPS = "https://";
    public static final String URL_SCHEME_INTENT = "intent://";

    public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return false;
        }

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isAvailable();
    }

    public static boolean isURL(String url) {
        if (url == null) {
            return false;
        }

        if (url.startsWith(URL_ABOUT_BLANK)
                || url.startsWith(URL_SCHEME_MAIL_TO)
                || url.startsWith(URL_SCHEME_FILE)) {
            return true;
        }

        url = url.toLowerCase();
        String regex = "^((ftp|http|https|intent)?://)"                      // support scheme
                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" // ftp的user@
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}"                            // IP形式的URL -> 199.194.52.184
                + "|"                                                        // 允许IP和DOMAIN（域名）
                + "([0-9a-z_!~*'()-]+\\.)*"                                  // 域名 -> www.
                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\."                    // 二级域名
                + "[a-z]{2,6})"                                              // first level domain -> .com or .museum
                + "(:[0-9]{1,4})?"                                           // 端口 -> :80
                + "((/?)|"                                                   // a slash isn't required if there is no file name
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(url).matches();
    }

    public static String queryWrapper(Context context, String query) {
        if (isURL(query)) {
            if (query.startsWith(URL_SCHEME_ABOUT) || query.startsWith(URL_SCHEME_MAIL_TO)) {
                return query;
            }

            if (!query.contains("://")) {
                query = URL_SCHEME_HTTP + query;
            }
            return query;
        }

        try {
            query = URLEncoder.encode(query, URL_ENCODING);
        } catch (UnsupportedEncodingException u) {}

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String searchEngine = sp.getString(
                context.getString(R.string.sp_search_engine),
                context.getString(R.string.setting_summary_search_engine_google)
        );
        if (searchEngine.equals(context.getString(R.string.setting_summary_search_engine_google))) {
            searchEngine = SEARCH_ENGINE_GOOGLE;
        } else if (searchEngine.equals(context.getString(R.string.setting_summary_search_engine_duckduckgo))) {
            searchEngine = SEARCH_ENGINE_DUCKDUCKGO;
        } else if (searchEngine.equals(context.getString(R.string.setting_summary_search_engine_startpage))) {
            searchEngine = SEARCH_ENGINE_STARTPAGE;
        } else if (searchEngine.equals(context.getString(R.string.setting_summary_search_engine_bing))) {
            searchEngine = SEARCH_ENGINE_BING;
        } else if (searchEngine.equals(context.getString(R.string.setting_summary_search_engine_baidu))) {
            searchEngine = SEARCH_ENGINE_BAIDU;
        } else {
            searchEngine = SEARCH_ENGINE_GOOGLE;
        }
        return searchEngine + query;
    }

    public static void copyURL(Context context, String url) {
        ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText(null, url);
        manager.setPrimaryClip(data);
        NinjaToast.show(context, R.string.toast_copy_successful);
    }

    public static void download(Context context, String url, String contentDisposition, String mimeType) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
        request.setMimeType(mimeType);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, context.getString(R.string.app_name));
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
        NinjaToast.show(context, R.string.toast_start_download);
    }

    public static String screenshot(Context context, Bitmap bitmap, String name) {
        if (bitmap == null) {
            return null;
        }

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (name == null || name.isEmpty()) {
            name = String.valueOf(System.currentTimeMillis());
        }

        int count = 0;
        File file = new File(dir, name + SUFFIX_PNG);
        while (file.exists()) {
            count++;
            file = new File(dir, name + "." + count + SUFFIX_PNG);
        }

        try {
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            return file.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }

    // TODO: used by AsyncTask
    public static void exportBookmarks(Context context) {
        RecordAction action = new RecordAction(context);
        action.open(false);
        List<Record> list = action.listBookmarks();
        action.close();

        String filename = context.getString(R.string.bookmarks_filename);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename + SUFFIX_TXT);
        int count = 0;
        while (file.exists()) {
            count++;
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename + "." + count + SUFFIX_TXT);
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
            for (Record record : list) {
                JSONObject object = new JSONObject();
                object.put(RecordUnit.COLUMN_TITLE, record.getTitle());
                object.put(RecordUnit.COLUMN_URL, record.getURL());
                object.put(RecordUnit.COLUMN_TIME, record.getTime());
                writer.write(object.toString());
                writer.newLine();
            }
            writer.close();
            NinjaToast.show(context, context.getString(R.string.toast_export_bookmarks_successful) + " " + file.getAbsolutePath());
        } catch (Exception e) {
            NinjaToast.show(context, R.string.toast_export_bookmarks_failed);
        }
    }

    // TODO: used by AsyncTask
    public static void importBookmarks(Context context, File file) {
        if (file == null) {
            return;
        }

        int count = 0;
        try {
            RecordAction action = new RecordAction(context);
            action.open(true);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                JSONObject object = new JSONObject(line);
                Record record = new Record();
                record.setTitle(object.getString(RecordUnit.COLUMN_TITLE));
                record.setURL(object.getString(RecordUnit.COLUMN_URL));
                record.setTime(object.getLong(RecordUnit.COLUMN_TIME));
                if (!action.checkBookmark(record)) {
                    action.addBookmark(record);
                    count++;
                }
            }
            reader.close();
            action.close();
            NinjaToast.show(context, context.getString(R.string.toast_import_bookmarks_successful) + " " + count);
        } catch (Exception e) {
            if (count <= 0) {
                NinjaToast.show(context, R.string.toast_import_bookmarks_failed);
            } else {
                NinjaToast.show(context, context.getString(R.string.toast_import_bookmarks_successful) + " " + count);
            }
        }
    }

    public static void clearBookmarks(Context context) {
        RecordAction action = new RecordAction(context);
        action.open(true);
        action.clearBookmarks();
        action.close();
        NinjaToast.show(context, R.string.toast_clear_bookmarks_successful);
    }

    public static void clearCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
            NinjaToast.show(context, R.string.toast_clear_cache_successful);
        } catch (Exception exception) {
            NinjaToast.show(context, R.string.toast_clear_cache_failed);
        }
    }

    public static void clearCookies(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.flush();
            cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
                @Override
                public void onReceiveValue(Boolean value) {}
            });
        } else {
            CookieManager cookieManager = CookieManager.getInstance();
            CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(context);
            cookieManager.removeAllCookie();
        }
        NinjaToast.show(context, R.string.toast_clear_cookie_successful);
    }

    public static void clearFromData(Context context) {
        WebViewDatabase.getInstance(context).clearFormData();
        NinjaToast.show(context, R.string.toast_clear_form_data_successful);
    }

    public static void clearHistory(Context context) {
        RecordAction action = new RecordAction(context);
        action.open(true);
        action.clearHistory();
        action.close();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            WebIconDatabase.getInstance().removeAllIcons();
        }
        NinjaToast.show(context, R.string.toast_clear_history_successful);
    }

    public static void clearPasswords(Context context) {
        WebViewDatabase.getInstance(context).clearHttpAuthUsernamePassword();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            WebViewDatabase.getInstance(context).clearUsernamePassword();
        }
        NinjaToast.show(context, R.string.toast_clear_passwords_successful);
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        return dir != null && dir.delete();
    }
}
