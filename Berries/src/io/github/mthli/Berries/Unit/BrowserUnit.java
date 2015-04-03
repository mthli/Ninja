package io.github.mthli.Berries.Unit;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import io.github.mthli.Berries.R;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class BrowserUnit {
    public static final int LOAD_LIMIT = 8;
    public static final int PROGRESS_MAX = 100;
    public static final int PROGRESS_MIN = 0;

    public static final String URL_ENCODING = "UTF-8";
    public static final String URL_SCHEME_HTTP = "http";
    public static final String URL_SCHEME_HTTPS = "https";
    public static final String URL_SCHEME_FILE = "file";
    public static final String URL_SCHEME_FTP = "ftp";

    public static final String TAB_HOME = "about:home";
    public static final String TAB_UNTITLED = "about:untitled";

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

        try {
            new URL(url);
            return true;
        } catch (MalformedURLException m) {
            return false;
        }
    }

    public static String queryWrapper(Context context, String query) {
        try {
            query = URLEncoder.encode(query, URL_ENCODING);
        } catch (UnsupportedEncodingException u) {}

        if (isURL(query)) {
            return query;
        }

        if (isURL(URL_SCHEME_HTTP + "://" + query)) {
            return URL_SCHEME_HTTP + "://" + query;
        }

        SharedPreferences sp = context.getSharedPreferences(
                context.getString(R.string.sp_name),
                Context.MODE_PRIVATE
        );
        String searchEngine = sp.getString(
                context.getString(R.string.sp_search_engine),
                context.getString(R.string.sp_search_engine_google)
        );
        return searchEngine + query;
    }
}
