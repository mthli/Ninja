package io.github.mthli.Berries.Activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import io.github.mthli.Berries.Browser.Tab;
import io.github.mthli.Berries.Database.Record;
import io.github.mthli.Berries.R;
import io.github.mthli.Berries.Unit.ViewUnit;

public class BrowserActivity extends Activity {
    private LinearLayout controlPanel;
    private ImageButton overflowButton;

    private HorizontalScrollView tabsScroll;
    private LinearLayout tabsContainer;
    private ImageButton addTabButton;

    private ImageButton bookmarkButton;
    private AutoCompleteTextView urlInputBox;
    private ImageButton refreshButton;

    private LinearLayout progressWrapper;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser);

        initUI();
    }

    private void initUI() {
        controlPanel = (LinearLayout) findViewById(R.id.browser_control_panel);
        ViewCompat.setElevation(controlPanel, ViewUnit.getElevation(this, 2));
        overflowButton = (ImageButton) findViewById(R.id.browser_overflow_button);

        tabsScroll = (HorizontalScrollView) findViewById(R.id.browser_tabs_scroll);
        tabsContainer = (LinearLayout) findViewById(R.id.browser_tabs_container);
        addTabButton = (ImageButton) findViewById(R.id.browser_add_tab_button);

        bookmarkButton = (ImageButton) findViewById(R.id.browser_bookmark_button);
        urlInputBox = (AutoCompleteTextView) findViewById(R.id.browser_url_input);
        refreshButton = (ImageButton) findViewById(R.id.browser_refresh_button);

        progressWrapper = (LinearLayout) findViewById(R.id.progress_wrapper);
        progressBar = (ProgressBar) findViewById(android.R.id.progress);

        // TODO
        addTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Record record = new Record();
                record.setTitle("Untitled");
                record.setURL("www.baidu.com");
                record.setTime(System.currentTimeMillis());
                Tab tab = new Tab(BrowserActivity.this, record, false);
                addTab(tab);
            }
        });
    }

    private synchronized void addTab(Tab tab) {
        tab.activateTab();
        final View view = tab.getView();

        view.setVisibility(View.INVISIBLE);
        tabsContainer.addView(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Handler handler = new Handler();
                handler.postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                tabsScroll.smoothScrollTo(view.getLeft(), 0);
                            }
                        },
                        BrowserActivity.this.getResources().getInteger(android.R.integer.config_shortAnimTime)
                );
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                /* Do nothing here */
            }
        });
        view.startAnimation(animation);
    }
}
