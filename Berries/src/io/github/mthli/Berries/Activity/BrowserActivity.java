package io.github.mthli.Berries.Activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.*;
import io.github.mthli.Berries.Browser.BerryContainer;
import io.github.mthli.Berries.Browser.BerryView;
import io.github.mthli.Berries.Browser.BrowserController;
import io.github.mthli.Berries.Database.Record;
import io.github.mthli.Berries.Database.RecordAction;
import io.github.mthli.Berries.R;
import io.github.mthli.Berries.Unit.BrowserUnit;
import io.github.mthli.Berries.Unit.RecordUnit;
import io.github.mthli.Berries.Unit.ViewUnit;

import java.util.*;

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
    private BerryView currentBerryView = null;

    private int animTime;

    @Override
    public void updateNotification() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser);

        initUI();
        animTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
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
            inputBox.clearFocus();

            if (currentBerryView == null) {
                finish();
            } else {
                if (currentBerryView.canGoBack()) {
                    currentBerryView.goBack();
                } else {
                    deleteTab();
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
                if (currentBerryView == null || currentBerryView.getRecord() == null) {
                    Toast.makeText(BrowserActivity.this, R.string.browser_toast_bookmark_error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!currentBerryView.isLoadFinish()) {
                    Toast.makeText(BrowserActivity.this, R.string.browser_toast_bookmark_wait, Toast.LENGTH_SHORT).show();
                    return;
                }

                Record record = currentBerryView.getRecord();
                if (record.getURL().startsWith(BrowserUnit.URL_SCHEME_ABOUT)
                        || record.getURL().startsWith(BrowserUnit.URL_SCHEME_FILE)
                        || record.getURL().startsWith(BrowserUnit.URL_SCHEME_INTENT)
                        || record.getURL().startsWith(BrowserUnit.URL_SCHEME_MAIL_TO)
                        || record.getURL() == null
                        || record.getURL().isEmpty()
                        ) {
                    Toast.makeText(BrowserActivity.this, R.string.browser_toast_bookmark_error, Toast.LENGTH_SHORT).show();
                    return;
                }

                RecordAction action = new RecordAction(BrowserActivity.this);
                action.open(false);
                if (action.checkBookmark(record)) {
                    action.deleteBookmark(record);
                    Toast.makeText(BrowserActivity.this, R.string.browser_toast_delete_bookmark_successful, Toast.LENGTH_SHORT).show();
                } else {
                    action.addBookmark(record);
                    Toast.makeText(BrowserActivity.this, R.string.browser_toast_add_bookmark_successful, Toast.LENGTH_SHORT).show();
                }
                action.close();
                updateBookmarkButton();
                updateInputBox();
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentBerryView.isLoadFinish()) {
                    currentBerryView.reload();
                } else {
                    currentBerryView.stopLoading();
                }
            }
        });

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
                currentBerryView.loadUrl(BrowserUnit.queryWrapper(BrowserActivity.this, query));
                hideSoftInput();

                return false;
            }
        });
        updateInputBox();
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
                    case R.id.browser_menu_search_in_page:
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
        final BerryView berryView = new BerryView(this, record, incognito);
        final View tabView = berryView.getTab();

        berryView.setController(this);
        tabView.setVisibility(View.INVISIBLE);

        if (currentBerryView != null && resultMsg != null) {
            int index = BerryContainer.indexOf(currentBerryView) + 1;
            BerryContainer.add(berryView, index);
            tabsContainer.addView(tabView, index, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            BerryContainer.add(berryView);
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
                    berryView.deactivate();
                    return;
                }

                if (currentBerryView != null) {
                    currentBerryView.getRecord().setURL(inputBox.getText().toString());
                    currentBerryView.deactivate();
                    browserFrame.removeView(currentBerryView);
                }
                currentBerryView = berryView;

                browserFrame.addView(berryView);
                berryView.activate();
                tabsScroll.smoothScrollTo(tabView.getLeft(), 0);
                updateProgress(berryView.getProgress());
                updateBookmarkButton();

                Record record = berryView.getRecord();
                if (record.getURL().equals(BrowserUnit.ABOUT_HOME)) {
                    updateInputBox(null);
                } else {
                    updateInputBox(record.getURL());
                }

                if (resultMsg != null) {
                    WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                    transport.setWebView(berryView);
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
    public synchronized void showTab(final BerryView berryView) {
        if (berryView == null || berryView.equals(currentBerryView)) {
            return;
        }

        if (currentBerryView != null) {
            currentBerryView.getRecord().setURL(inputBox.getText().toString());
            currentBerryView.deactivate();
            browserFrame.removeView(currentBerryView);
        }
        currentBerryView = berryView;

        browserFrame.addView(berryView);
        berryView.activate();
        tabsScroll.smoothScrollTo(berryView.getTab().getLeft(), 0);
        updateProgress(berryView.getProgress());
        updateBookmarkButton();

        Record record = berryView.getRecord();
        if (record.getURL().equals(BrowserUnit.ABOUT_HOME)) {
            updateInputBox(null);
        } else {
            updateInputBox(record.getURL());
        }
    }

    @Override
    public synchronized void deleteTab() {
        if (currentBerryView == null) {
            return;
        }

        if (BerryContainer.size() <= 1) {
            finish();
            return;
        }

        final View tabView = currentBerryView.getTab();
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

                currentBerryView.deactivate();
                updateProgress(BrowserUnit.PROGRESS_MAX);
                browserFrame.removeView(currentBerryView);

                int index = BerryContainer.indexOf(currentBerryView);
                BerryContainer.remove(currentBerryView);
                if (index >= BerryContainer.size()) {
                    index = BerryContainer.size() - 1;
                }

                currentBerryView = BerryContainer.get(index);
                browserFrame.addView(currentBerryView);
                currentBerryView.activate();
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        tabsScroll.smoothScrollTo(currentBerryView.getTab().getLeft(), 0);
                        updateProgress(currentBerryView.getProgress());
                        updateBookmarkButton();

                        Record record = currentBerryView.getRecord();
                        if (record.getURL().equals(BrowserUnit.ABOUT_HOME)) {
                            updateInputBox(null);
                        } else {
                            updateInputBox(record.getURL());
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
    public void updateBookmarkButton() {
        if (currentBerryView == null || currentBerryView.getRecord() == null) {
            bookmarkButton.setImageDrawable(getResources().getDrawable(R.drawable.browser_bookmark_outline_button_selector));
            return;
        }

        RecordAction action = new RecordAction(this);
        action.open(false);
        if (action.checkBookmark(currentBerryView.getRecord())) {
            bookmarkButton.setImageDrawable(getResources().getDrawable(R.drawable.browser_bookmark_full_button_selector));
        } else {
            bookmarkButton.setImageDrawable(getResources().getDrawable(R.drawable.browser_bookmark_outline_button_selector));
        }
        action.close();
    }

    private void updateInputBox() {
        final List<Map<String, String>> list = new ArrayList<Map<String, String>>();

        RecordAction action = new RecordAction(this);
        action.open(false);
        for (Record record : action.listBookmarks()) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("title", record.getTitle());
            map.put("url", record.getURL());
            list.add(map);
        }
        for (Record record : action.listHistory()) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("title", record.getTitle());
            map.put("url", record.getURL());
            list.add(map);
        }
        action.close();

        Set<Map<String, String>> set = new HashSet<Map<String, String>>(list);
        list.clear();
        list.addAll(set);

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                list,
                R.layout.complete_item,
                new String[] {"title", "url"},
                new int[] {R.id.complete_item_title, R.id.complete_item_url}
        );
        inputBox.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        inputBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String url = ((TextView) view.findViewById(R.id.complete_item_url)).getText().toString();
                inputBox.setText(url);
                inputBox.setSelection(url.length());
                if (currentBerryView != null) {
                    currentBerryView.loadUrl(BrowserUnit.queryWrapper(BrowserActivity.this, url));
                }
                hideSoftInput();
            }
        });
    }

    @Override
    public void updateInputBox(String query) {
        inputBox.setText(query);
        if (query != null) {
            inputBox.setSelection(query.length());
        }
    }

    private void hideSoftInput() {
        inputBox.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(inputBox.getWindowToken(), 0);
    }

    @Override
    public void updateProgress(int progress) {
        if (progress > progressBar.getProgress()) {
            ObjectAnimator animator = ObjectAnimator.ofInt(progressBar, "progress", progress);
            animator.setDuration(animTime);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.start();
        } else if (progress < progressBar.getProgress()) {
            ObjectAnimator animator = ObjectAnimator.ofInt(progressBar, "progress", 0, progress);
            animator.setDuration(animTime);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.start();
        }

        if (currentBerryView.isLoadFinish()) {
            RecordAction action = new RecordAction(this);
            action.open(true);
            action.addHistory(currentBerryView.getRecord());
            action.close();
            updateInputBox();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateRefreshButton(false);
                    progressWrapper.setVisibility(View.GONE);
                }
            }, animTime);
        } else {
            updateRefreshButton(true);
            progressWrapper.setVisibility(View.VISIBLE);
        }
    }

    private void updateRefreshButton(boolean running) {
        if (running) {
            refreshButton.setImageDrawable(getResources().getDrawable(R.drawable.browser_cl_button_selector));
        } else {
            refreshButton.setImageDrawable(getResources().getDrawable(R.drawable.browser_refresh_button_selector));
        }
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

    @Override
    public void onLongPress(String url) {
        WebView.HitTestResult result = null;
        if (currentBerryView != null) {
            result = currentBerryView.getHitTestResult();
        }

        if (url != null) {
            // TODO
        }
    }
}
