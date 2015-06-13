package io.github.mthli.Ninja.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import io.github.mthli.Ninja.R;
import io.github.mthli.Ninja.Task.ReadabilityTask;
import io.github.mthli.Ninja.Unit.BrowserUnit;
import io.github.mthli.Ninja.Unit.IntentUnit;
import org.json.JSONObject;

public class ReadabilityActivity extends Activity {
    private static final String HEADER = "<link rel=\"stylesheet\" href=\"./typo.css\" />\n"
            + "<meta name=\"viewport\" content=\"width=device-width\">\n"
            + "<style>\n"
            + "    html {\n"
            + "        background: {background};\n"
            + "    }\n"
            + "    img {\n"
            + "        max-width: device-width;\n"
            + "        width: expression(this.width > device-width ? device-width : this.width);\n"
            + "        height: auto;\n"
            + "        display: block;\n"
            + "        margin-left: auto;\n"
            + "        margin-right: auto;\n"
            + "    }\n"
            + "    body {\n"
            + "        width: 90%;\n"
            + "        margin: 2em auto 0;\n"
            + "    }\n"
            + "</style>\n";
    private static final String HEADER_BACKGROUND = "{background}";
    private static final String DIV = "<div>";
    private static final String DIV_CLASS_TYPO = "<div class=\"typo typo-selection\">";
    private static final String COLOR_WHITE = "#FFFFFF";
    private static final String COLOR_YELLOW = "#F5F5DC";

    private static final String REQUEST = "https://www.readability.com/api/content/v1/parser?url={url}&token={token}";
    private static final String REQUEST_URL = "{url}";
    private static final String REQUEST_TOKEN = "{token}";
    private static final int RESULT_SUCCESSFUL = 0x100;
    private static final int RESULT_FAILED = 0x101;

    private static final String RESULT_CONTENT = "content";
    private static final String RESULT_DOMAIN = "domain";
    private static final String RESULT_AUTHOR = "author";
    private static final String RESULT_URL = "url";
    private static final String RESULT_SHORT_URL = "short_url";
    private static final String RESULT_TITLE = "title";
    private static final String RESULT_EXCERPT = "excerpt";
    private static final String RESULT_DIRECTION = "direction";
    private static final String RESULT_WORD_COUNT = "word_count";
    private static final String RESULT_TOTAL_PAGES = "total_pages";
    private static final String RESULT_DATE_PUBLISHED = "date_published";
    private static final String RESULT_DEK = "dek";
    private static final String RESULT_LEAD_IMAGE_URL = "lead_image_url";
    private static final String RESULT_NEXT_PAGE_ID = "next_page_id";
    private static final String RESULT_RENDERED_PAGES = "rendered_pages";

    private ProgressBar progressBar;
    private WebView webView;
    private TextView emptyView;

    private SharedPreferences sp;
    private String query = null;
    private JSONObject result = null;
    public void setResult(JSONObject result) {
        this.result = result;
    }

    public enum Status {
        RUNNING,
        IDLE
    }
    private Status status = Status.IDLE;
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.readability);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.readability_progress);
        webView = (WebView) findViewById(R.id.readability_webview);
        emptyView = (TextView) findViewById(R.id.readability_empty);
        showLoadStart();

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        int color = sp.getInt(getString(R.string.sp_readability_background), getResources().getColor(R.color.white));
        findViewById(R.id.readability_frame).setBackgroundColor(color);

        String token = sp.getString(getString(R.string.sp_readability_token), null);
        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(IntentUnit.URL) || token == null || token.trim().isEmpty()) {
            showLoadError();
        } else {
            String url = intent.getStringExtra(IntentUnit.URL);
            query = REQUEST.replace(REQUEST_URL, url).replace(REQUEST_TOKEN, token);
            new ReadabilityTask(this, query).execute();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.readability_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.readability_menu_background:
                int color = sp.getInt(getString(R.string.sp_readability_background), getResources().getColor(R.color.white));
                if (color == getResources().getColor(R.color.white)) {
                    sp.edit().putInt(getString(R.string.sp_readability_background), Color.parseColor(COLOR_YELLOW)).commit();
                    findViewById(R.id.readability_frame).setBackgroundColor(Color.parseColor(COLOR_YELLOW));
                } else {
                    sp.edit().putInt(getString(R.string.sp_readability_background), getResources().getColor(R.color.white)).commit();
                    findViewById(R.id.readability_frame).setBackgroundColor(getResources().getColor(R.color.white));
                }
                if (status == Status.IDLE && result != null) {
                    showLoadSuccessful();
                }
                break;
            default:
                break;
        }

        return true;
    }

    private String contentWrapper(String content) {
        int color = sp.getInt(getString(R.string.sp_readability_background), getResources().getColor(R.color.white));
        String header;
        if (color == getResources().getColor(R.color.white)) {
            header = HEADER.replace(HEADER_BACKGROUND, "#FFFFFF");
        } else {
            header = HEADER.replace(HEADER_BACKGROUND, "#F5F5DC");
        }

        return (header + content).replace(DIV, DIV_CLASS_TYPO);
    }

    private void initWebView() {
        webView.setAlwaysDrawnWithCacheEnabled(true);
        webView.setAnimationCacheEnabled(true);
        webView.setDrawingCacheBackgroundColor(0x00000000);
        webView.setDrawingCacheEnabled(true);
        webView.setWillNotCacheDrawing(false);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.setScrollbarFadingEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);
        webView.setVerticalScrollBarEnabled(true);

        webView.setBackground(null);
        webView.getRootView().setBackground(null);
        int color = sp.getInt(getString(R.string.sp_readability_background), getResources().getColor(R.color.white));
        webView.setBackgroundColor(color);

        WebSettings webSettings = webView.getSettings();
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCachePath(getCacheDir().toString());
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setDefaultTextEncodingName(BrowserUnit.URL_ENCODING);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setTextZoom(100);
        webSettings.setUseWideViewPort(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webSettings.setLoadsImagesAutomatically(true);
        } else {
            webSettings.setLoadsImagesAutomatically(false);
        }
    }

    public void showLoadStart() {
        webView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void showLoadError() {
        progressBar.setVisibility(View.GONE);
        webView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    public void showLoadSuccessful() {
        try {
            getActionBar().setTitle(result.getString(RESULT_TITLE));
            getActionBar().setSubtitle(result.getString(RESULT_URL));

            progressBar.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);

            initWebView();
            String content = contentWrapper(result.getString(RESULT_CONTENT));
            webView.loadDataWithBaseURL(BrowserUnit.BASE_URL, content, BrowserUnit.MIME_TYPE_TEXT_HTML, BrowserUnit.URL_ENCODING, null);
            webView.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            showLoadError();
        }
    }
}
