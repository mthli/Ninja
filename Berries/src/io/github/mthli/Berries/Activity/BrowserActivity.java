package io.github.mthli.Berries.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.*;
import io.github.mthli.Berries.Browser.BerryContainer;
import io.github.mthli.Berries.Browser.Berry;
import io.github.mthli.Berries.Browser.BrowserController;
import io.github.mthli.Berries.Database.Record;
import io.github.mthli.Berries.R;
import io.github.mthli.Berries.Unit.BrowserUnit;
import io.github.mthli.Berries.Unit.RecordUnit;
import io.github.mthli.Berries.Unit.ViewUnit;

public class BrowserActivity extends Activity implements BrowserController {
    private LinearLayout controlPanel;
    private ImageButton overflowButton;

    private HorizontalScrollView tabsScroll;
    private LinearLayout tabsContainer;
    private ImageButton addTabButton;

    private ImageButton bookmarkButton;
    private AutoCompleteTextView inputBox;
    private ImageButton refreshButton;

    private LinearLayout progressWrapper;
    private ProgressBar progressBar;

    private FrameLayout browserFrame;
    private Berry currentBerry = null;

    @Override
    public void updateProgress(int progress) {}

    @Override
    public void updateNotification() {}

    @Override
    public void showControlPanel() {}

    @Override
    public void hideControlPanel() {}

    @Override
    public boolean isControlPanelShowing() {
        return false;
    }

    @Override
    public void onLongPress() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser);

        initUI();
    }

    @Override
    public void onPause() {
        inputBox.clearFocus();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        BerryContainer.clear();
        super.onDestroy();
    }

    @Override
    public synchronized boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showControlPanel();

            if (currentBerry == null) {
                finish();
            } else {
                if (currentBerry.canGoBack()) {
                    currentBerry.goBack();
                } else {
                    deleteSelectedTab();
                }
            }
        }

        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation== Configuration.ORIENTATION_LANDSCAPE) {
            /* Do nothing */
        } else {
            /* Do nothing */
        }
    }

    private void initUI() {
        controlPanel = (LinearLayout) findViewById(R.id.browser_control_panel);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            controlPanel.setElevation(ViewUnit.dp2px(this, 2));
        }
        overflowButton = (ImageButton) findViewById(R.id.browser_overflow_button);

        tabsScroll = (HorizontalScrollView) findViewById(R.id.browser_tabs_scroll);
        tabsContainer = (LinearLayout) findViewById(R.id.browser_tabs_container);
        addTabButton = (ImageButton) findViewById(R.id.browser_add_tab_button);

        bookmarkButton = (ImageButton) findViewById(R.id.browser_bookmark_button);
        inputBox = (AutoCompleteTextView) findViewById(R.id.browser_input_box);
        refreshButton = (ImageButton) findViewById(R.id.browser_refresh_button);

        progressWrapper = (LinearLayout) findViewById(R.id.browser_progress_wrapper);
        progressBar = (ProgressBar) findViewById(R.id.browser_progress_bar);

        browserFrame = (FrameLayout) findViewById(R.id.browser_frame);
        newTab(RecordUnit.getHome(this), false, true, null);

        overflowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOverflow();
            }
        });
        overflowButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(BrowserActivity.this, R.string.browser_toast_more, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        addTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Record record = RecordUnit.getHome(BrowserActivity.this);
                newTab(record, false, true, null);
            }
        });
        addTabButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Record record = RecordUnit.getHome(BrowserActivity.this);
                newTab(record, true, true, null);
                Toast.makeText(BrowserActivity.this, R.string.browser_toast_incognito, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });
        bookmarkButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // TODO
                return true;
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });
        refreshButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // TODO
                return true;
            }
        });

        inputBox.setThreshold(3);
        inputBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (!(actionId == EditorInfo.IME_ACTION_DONE)) {
                    return false;
                }

                String query = inputBox.getText().toString().trim();
                if (query.isEmpty()) {
                    Toast.makeText(BrowserActivity.this, R.string.browser_toast_input_empty, Toast.LENGTH_SHORT).show();
                    return false;
                }
                query = BrowserUnit.queryWrapper(BrowserActivity.this, query);
                currentBerry.load(new Record(getString(R.string.browser_tab_untitled), query, System.currentTimeMillis()));

                return false;
            }
        });
    }

    private void showOverflow() {
        PopupMenu popupMenu = new PopupMenu(this, addTabButton);
        popupMenu.getMenuInflater().inflate(R.menu.broswer_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.browser_menu_bookmarks:
                        break;
                    case R.id.browser_menu_history:
                        break;
                    case R.id.browser_menu_search:
                        break;
                    case R.id.browser_menu_share:
                        break;
                    case R.id.browser_menu_setting:
                        break;
                    case R.id.browser_menu_quit:
                        finish();
                        break;
                    default:
                        break;
                }

                return true;
            }
        });
        popupMenu.show();
    }

    private synchronized void newTab(Record record, boolean incognito, final boolean foreground, final Message resultMsg) {
        final Berry berry = new Berry(this, record, incognito);
        final View tabView = berry.getTabView();

        berry.setController(this);
        tabView.setVisibility(View.INVISIBLE);

        if (currentBerry != null && resultMsg != null) {
            int index = BerryContainer.indexOf(currentBerry) + 1;
            BerryContainer.add(berry, index);
            tabsContainer.addView(tabView, index, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            BerryContainer.add(berry);
            tabsContainer.addView(tabView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        }

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                tabView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!foreground) {
                    berry.deactivate();
                    return;
                }

                if (currentBerry != null) {
                    currentBerry.getRecord().setURL(inputBox.getText().toString());
                    currentBerry.deactivate();
                    browserFrame.removeView(currentBerry.getWebView());
                }
                currentBerry = berry;

                browserFrame.addView(berry.getWebView());
                berry.activate();
                tabsScroll.smoothScrollTo(tabView.getLeft(), 0);

                Record record = berry.getRecord();
                if (record.getURL().equals(BrowserUnit.ABOUT_HOME)) {
                    inputBox.setText(null);
                } else {
                    inputBox.setText(record.getURL());
                }

                if (resultMsg != null) {
                    WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                    transport.setWebView(berry.getWebView());
                    resultMsg.sendToTarget();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                /* Do nothing here */
            }
        });
        tabView.startAnimation(animation);
    }

    @Override
    public synchronized void showSelectedTab(final Berry berry) {
        if (berry == null || berry.equals(currentBerry)) {
            return;
        }

        if (currentBerry != null) {
            currentBerry.getRecord().setURL(inputBox.getText().toString());
            currentBerry.deactivate();
            browserFrame.removeView(currentBerry.getWebView());
        }
        currentBerry = berry;

        browserFrame.addView(berry.getWebView());
        berry.activate();
        tabsScroll.smoothScrollTo(berry.getTabView().getLeft(), 0);

        Record record = berry.getRecord();
        if (record.getURL().equals(BrowserUnit.ABOUT_HOME)) {
            inputBox.setText(null);
        } else {
            inputBox.setText(record.getURL());
        }
    }

    @Override
    public synchronized void deleteSelectedTab() {
        if (currentBerry == null) {
            return;
        }

        if (BerryContainer.size() <= 1) {
            finish();
            return;
        }

        final View tabView = currentBerry.getTabView();
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_down);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                /* Do nothing here */
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tabView.setVisibility(View.GONE);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        tabsContainer.removeView(tabView);
                    }
                });

                currentBerry.deactivate();
                browserFrame.removeView(currentBerry.getWebView());

                int index = BerryContainer.indexOf(currentBerry);
                BerryContainer.remove(currentBerry);
                if (index >= BerryContainer.size()) {
                    index = BerryContainer.size() - 1;
                }

                currentBerry = BerryContainer.get(index);
                browserFrame.addView(currentBerry.getWebView());
                currentBerry.activate();
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        tabsScroll.smoothScrollTo(currentBerry.getTabView().getLeft(), 0);

                        Record record = currentBerry.getRecord();
                        if (record.getURL().equals(BrowserUnit.ABOUT_HOME)) {
                            inputBox.setText(null);
                        } else {
                            inputBox.setText(record.getURL());
                        }
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                /* Do nothing here */
            }
        });
        tabView.startAnimation(animation);
    }

    @Override
    public void updateInputBox(String query) {
        inputBox.setText(query);
    }

    @Override
    public void onCreateView(WebView view, boolean incognito, final Message resultMsg) {
        if (resultMsg == null) {
            return;
        }

        Record record = new Record();
        if (view.getTitle() == null || view.getTitle().isEmpty()) {
            record.setTitle(getString(R.string.browser_tab_untitled));
        } else {
            record.setTitle(view.getTitle());
        }
        record.setURL(view.getUrl());
        record.setTime(System.currentTimeMillis());
        newTab(record, incognito, true, resultMsg);
    }
}
