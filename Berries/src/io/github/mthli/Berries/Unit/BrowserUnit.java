package io.github.mthli.Berries.Unit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.util.Patterns.GOOD_IRI_CHAR;

public class BrowserUnit {
    public static final int LOAD_LIMIT = 8;
    public static final int PROGRESS_MAX = 100;
    public static final int PROGRESS_MIN = 0;

    public static final String URL_ENCODING = "UTF-8";
    public static Pattern URL_PATTERN = Pattern.compile("(?:(http|https|file)\\:\\/\\/)?"           // scheme
            + "(?:([-A-Za-z0-9$_.+!*'(),;?&=]+(?:\\:[-A-Za-z0-9$_.+!*'(),;?&=]+)?)@)?"              // auth_info
            + "([" + GOOD_IRI_CHAR + "%_-][" + GOOD_IRI_CHAR + "%_\\.-]*|\\[[0-9a-fA-F:\\.]+\\])?"  // host
            + "(?:\\:([0-9]*))?"                                                                    // port
            + "(\\/?[^#]*)?"                                                                        // path
            + ".*", Pattern.CASE_INSENSITIVE);                                                      // anchor
    private static final int URL_PATTERN_GROUP_SCHEME = 1;
    private static final int URL_PATTERN_GROUP_AUTH_INFO = 2;
    private static final int URL_PATTERN_GROUP_HOST = 3;
    private static final int URL_PATTERN_GROUP_PORT = 4;
    private static final int URL_PATTERN_GROUP_PATH = 5;

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
        return (URL_PATTERN.matcher(url)).matches();
    }

    public static String getScheme(String url) {
        Matcher matcher = URL_PATTERN.matcher(url);
        String scheme = matcher.group(URL_PATTERN_GROUP_SCHEME);
        if (scheme != null) {
            scheme = scheme.toLowerCase(Locale.ROOT);
        }

        if (getPort(url) == 443 && (scheme == null ||scheme.isEmpty())) {
            scheme = "https";
        }
        if (scheme == null || scheme.isEmpty()) {
            scheme = "http";
        }

        return scheme;
    }

    public static String getAuthInfo(String url) {
        return (URL_PATTERN.matcher(url)).group(URL_PATTERN_GROUP_AUTH_INFO);
    }

    public static String getHost(String url) {
        Matcher matcher = URL_PATTERN.matcher(url);
        String host = matcher.group(URL_PATTERN_GROUP_HOST);
        if (host != null) {
            host = host.toLowerCase(Locale.ROOT);
        }

        return host;
    }

    public static int getPort(String url) {
        int port = -1;

        Matcher matcher = URL_PATTERN.matcher(url);
        String temp = matcher.group(URL_PATTERN_GROUP_PORT);
        if (temp != null && !temp.isEmpty()) {
            port = Integer.parseInt(temp);
        }

        if (port < 0 && url.startsWith("https://")) {
            port = 443;
        }
        if (port < 0) {
            port = 80;
        }

        return port;
    }

    public static String getPath(String url) {
        String path = "/";

        Matcher matcher = URL_PATTERN.matcher(url);
        String temp = matcher.group(URL_PATTERN_GROUP_PATH);
        if (temp != null && !temp.isEmpty()) {
            if (temp.charAt(0) == '/') {
                path = temp;
            } else {
                path += temp;
            }
        }

        return path;
    }

    public static String getEntireURL(String url) {
        String authInfo = getAuthInfo(url);
        if (authInfo == null || authInfo.isEmpty()) {
            authInfo = "";
        } else {
            authInfo += "@";
        }

        return getScheme(url) + "://" + authInfo + getHost(url) + ":" + getPort(url) + getPath(url);
    }
}
