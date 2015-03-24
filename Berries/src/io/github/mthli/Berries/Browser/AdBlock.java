package io.github.mthli.Berries.Browser;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class AdBlock {
    private static final String FILE = "hosts.txt";

    private static final Set<String> domains = new HashSet<String>();

    private static final Locale locale = Locale.getDefault();

    private Context context;
    public Context getContext() {
        return context;
    }

    public AdBlock(Context context) {
        this.context = context;

        if (domains.isEmpty()) {
            loadDomains();
        }
    }

    private void loadDomains() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                AssetManager manager = context.getAssets();
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(manager.open(FILE)));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        domains.add(line.trim().toLowerCase(locale));
                    }
                } catch (IOException i) {}
            }
        });
        thread.start();
    }

    public boolean isAd(String url) {
        String domain;
        try {
            domain = getDomain(url);
        } catch (URISyntaxException u) {
            return false;
        }

        return domains.contains(domain.toLowerCase(locale));
    }

    private static String getDomain(String url) throws URISyntaxException {
        int index = url.indexOf('/', 8);
        if (index != -1) {
            url = url.substring(0, index);
        }

        URI uri = new URI(url);
        String domain = uri.getHost();
        if (domain == null) {
            return url;
        }

        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }
}
