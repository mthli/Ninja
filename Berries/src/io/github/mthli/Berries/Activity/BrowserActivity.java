package io.github.mthli.Berries.Activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
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
import io.github.mthli.Berries.Unit.IntentUnit;
import io.github.mthli.Berries.Unit.ViewUnit;
import io.github.mthli.Berries.View.DialogAdapter;

import java.util.*;

public class BrowserActivity extends Activity implements BrowserController {
    private LinearLayout controlPanel;
    private HorizontalScrollView tabsScroll;
    private LinearLayout tabsContainer;
    private ImageButton addTabButton;
    private ImageButton bookmarkButton;
    private AutoCompleteTextView inputBox;
    private ImageButton refreshButton;
    private ImageButton overflowButton;
    private LinearLayout progressWrapper;
    private ProgressBar progressBar;

    private RelativeLayout searchPanel;
    private View searchSeparator;
    private EditText searchBox;
    private ImageButton searchUpButton;
    private ImageButton searchDownButton;
    private ImageButton searchCancelButton;

    private FrameLayout browserFrame;
    private BerryView currentView = null;

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

            if (currentView == null) {
                finish();
            } else {
                if (currentView.canGoBack()) {
                    currentView.goBack();
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
        tabsScroll = (HorizontalScrollView) findViewById(R.id.browser_tabs_scroll);
        tabsContainer = (LinearLayout) findViewById(R.id.browser_tabs_container);
        addTabButton = (ImageButton) findViewById(R.id.browser_add_tab_button);
        bookmarkButton = (ImageButton) findViewById(R.id.browser_bookmark_button);
        inputBox = (AutoCompleteTextView) findViewById(R.id.browser_input_box);
        refreshButton = (ImageButton) findViewById(R.id.browser_refresh_button);
        overflowButton = (ImageButton) findViewById(R.id.browser_overflow_button);
        progressWrapper = (LinearLayout) findViewById(R.id.browser_progress_wrapper);
        progressBar = (ProgressBar) findViewById(R.id.browser_progress_bar);

        searchPanel = (RelativeLayout) findViewById(R.id.browser_search_panel);
        searchSeparator = findViewById(R.id.browser_search_separator);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            searchPanel.setElevation(ViewUnit.dp2px(this, 2));
            searchSeparator.setVisibility(View.GONE);
        } else {
            searchSeparator.setVisibility(View.GONE);
        }
        searchBox = (EditText) findViewById(R.id.browser_search_box);
        searchUpButton = (ImageButton) findViewById(R.id.browser_search_up_button);
        searchDownButton = (ImageButton) findViewById(R.id.browser_search_down_button);
        searchCancelButton = (ImageButton) findViewById(R.id.browser_search_cancel_button);

        browserFrame = (FrameLayout) findViewById(R.id.browser_frame);
        newTab(R.string.browser_tab_home, false, true, null);

        addTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newTab(R.string.browser_tab_home, false, true, null);
            }
        });
        addTabButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                newTab(R.string.browser_tab_home, true, true, null);
                Toast.makeText(BrowserActivity.this, R.string.browser_toast_incognito, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!prepareRecord()) {
                    Toast.makeText(BrowserActivity.this, R.string.browser_toast_add_bookmark_failed, Toast.LENGTH_SHORT).show();
                    return;
                }

                RecordAction action = new RecordAction(BrowserActivity.this);
                action.open(false);
                if (action.checkBookmark(currentView.getUrl())) {
                    action.deleteBookmark(currentView.getUrl());
                    Toast.makeText(BrowserActivity.this, R.string.browser_toast_delete_bookmark_successful, Toast.LENGTH_SHORT).show();
                } else {
                    action.addBookmark(new Record(currentView.getTitle(), currentView.getUrl(), System.currentTimeMillis()));
                    Toast.makeText(BrowserActivity.this, R.string.browser_toast_add_bookmark_successful, Toast.LENGTH_SHORT).show();
                }
                action.close();
                updateBookmarkButton();
                updateAutoComplete();
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentView.isLoadFinish()) {
                    currentView.reload();
                } else {
                    currentView.stopLoading();
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
                currentView.loadUrl(BrowserUnit.queryWrapper(BrowserActivity.this, query));
                inputBox.clearFocus();
                hideSearchPanel();

                return false;
            }
        });
        updateAutoComplete();

        overflowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBrowserMenu();
            }
        });

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                /* Do nothing */
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                /* Do nothing */
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (currentView != null) {
                    currentView.findAllAsync(editable.toString());
                }
            }
        });
        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (!(actionId == EditorInfo.IME_ACTION_DONE)) {
                    return false;
                }

                if (searchBox.getText().toString().isEmpty()) {
                    Toast.makeText(BrowserActivity.this, R.string.browser_toast_input_empty, Toast.LENGTH_SHORT).show();
                    return true;
                }

                return false;
            }
        });

        searchUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = searchBox.getText().toString();
                if (query.isEmpty()) {
                    Toast.makeText(BrowserActivity.this, R.string.browser_toast_input_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                currentView.findNext(false);
            }
        });

        searchDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = searchBox.getText().toString();
                if (query.isEmpty()) {
                    Toast.makeText(BrowserActivity.this, R.string.browser_toast_input_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                currentView.findNext(true);
            }
        });

        searchCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSearchPanel();
            }
        });
    }

    private synchronized void newTab(int stringResId, boolean incognito, final boolean foreground, final Message resultMsg) {
        newTab(getString(stringResId), incognito, foreground, resultMsg);
    }

    private synchronized void newTab(String title, boolean incognito, final boolean foreground, final Message resultMsg) {
        inputBox.clearFocus();
        hideSearchPanel();

        final BerryView berryView = new BerryView(this, incognito);
        berryView.setController(this);

        final View tabView = berryView.getTab().getView();
        berryView.getTab().setTitle(title);
        tabView.setVisibility(View.INVISIBLE);

        if (currentView != null && resultMsg != null) {
            int index = BerryContainer.indexOf(currentView) + 1;
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

                if (currentView != null) {
                    currentView.deactivate();
                    browserFrame.removeView(currentView);
                }
                currentView = berryView;

                browserFrame.addView(currentView, 0);
                currentView.activate();
                tabsScroll.smoothScrollTo(tabView.getLeft(), 0);
                updateOnmiBox();

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
        if (berryView == null || berryView.equals(currentView)) {
            return;
        }

        inputBox.clearFocus();
        hideSearchPanel();

        if (currentView != null) {
            currentView.deactivate();
            browserFrame.removeView(currentView);
        }
        currentView = berryView;

        browserFrame.addView(currentView, 0);
        berryView.activate();
        tabsScroll.smoothScrollTo(currentView.getTab().getView().getLeft(), 0);
        updateOnmiBox();
    }

    @Override
    public synchronized void deleteTab() {
        if (currentView == null) {
            return;
        }

        if (BerryContainer.size() <= 1) {
            finish();
            return;
        }

        inputBox.clearFocus();
        hideSearchPanel();

        final View tabView = currentView.getTab().getView();
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

                currentView.deactivate();
                browserFrame.removeView(currentView);
                updateProgress(BrowserUnit.PROGRESS_MAX);

                int index = BerryContainer.indexOf(currentView);
                BerryContainer.remove(currentView);
                if (index >= BerryContainer.size()) {
                    index = BerryContainer.size() - 1;
                }

                currentView = BerryContainer.get(index);
                browserFrame.addView(currentView, 0);
                currentView.activate();
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        tabsScroll.smoothScrollTo(currentView.getTab().getView().getLeft(), 0);
                        updateOnmiBox();
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
        if (currentView == null) {
            bookmarkButton.setImageDrawable(getResources().getDrawable(R.drawable.browser_bookmark_outline_button_selector));
            return;
        }

        RecordAction action = new RecordAction(this);
        action.open(false);
        if (action.checkBookmark(currentView.getUrl())) {
            bookmarkButton.setImageDrawable(getResources().getDrawable(R.drawable.browser_bookmark_full_button_selector));
        } else {
            bookmarkButton.setImageDrawable(getResources().getDrawable(R.drawable.browser_bookmark_outline_button_selector));
        }
        action.close();
    }

    private void updateAutoComplete() {
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
                if (currentView != null) {
                    currentView.loadUrl(BrowserUnit.queryWrapper(BrowserActivity.this, url));
                }
                hideSoftInput(inputBox);
            }
        });
    }

    @Override
    public void updateInputBox(String query) {
        inputBox.setText(query);
        inputBox.clearFocus();
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

        if (currentView.isLoadFinish()) {
            // TODO: don't add homepage, don't use record,
            RecordAction action = new RecordAction(this);
            action.open(true);
            action.addHistory(new Record(currentView.getTitle(), currentView.getUrl(), System.currentTimeMillis()));
            action.close();
            updateAutoComplete();

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
            refreshButton.setImageDrawable(getResources().getDrawable(R.drawable.cl_button_selector));
        } else {
            refreshButton.setImageDrawable(getResources().getDrawable(R.drawable.browser_refresh_button_selector));
        }
    }

    private void updateOnmiBox() {
        if (currentView == null) {
            return;
        }

        updateProgress(currentView.getProgress());
        updateBookmarkButton();
        if (currentView.getUrl() == null) {
            updateInputBox(null);
        } else {
            updateInputBox(currentView.getUrl());
        }
    }

    @Override
    public void onCreateView(WebView view, boolean incognito, final Message resultMsg) {
        if (resultMsg == null) {
            return;
        }
        newTab(R.string.browser_tab_untitled, incognito, true, resultMsg);
    }

    @Override
    public void onLongPress(String url) {
        WebView.HitTestResult result = null;
        if (currentView != null) {
            result = currentView.getHitTestResult();
        }

        if (url != null) {
            // TODO
        }
    }

    private boolean prepareRecord() {
        if (currentView == null) {
            return false;
        }

        String title = currentView.getTitle();
        String url = currentView.getUrl();
        if (title == null
                || title.isEmpty()
                || url == null
                || url.isEmpty()
                || url.startsWith(BrowserUnit.URL_SCHEME_ABOUT)
                || url.startsWith(BrowserUnit.URL_SCHEME_MAIL_TO)
                || url.startsWith(BrowserUnit.URL_SCHEME_INTENT)) {
            return false;
        }

        return true;
    }

    private void showBrowserMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog, null, false);
        builder.setView(linearLayout);

        String[] strings = getResources().getStringArray(R.array.browser_menu);
        List<String> list = new ArrayList<String>();
        list.addAll(Arrays.asList(strings));

        ListView listView = (ListView) linearLayout.findViewById(R.id.dialog_listview);
        DialogAdapter adapter = new DialogAdapter(this, R.layout.dialog_item, list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        final AlertDialog dialog = builder.create();
        dialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // TODO
                        break;
                    case 1:
                        // TODO
                        break;
                    case 2:
                        if (searchPanel.getVisibility() == View.GONE) {
                            showSearchPanel();
                        }
                        break;
                    case 3:
                        if (prepareRecord()) {
                            IntentUnit.share(BrowserActivity.this, currentView.getTitle(), currentView.getUrl());
                        } else {
                            Toast.makeText(BrowserActivity.this, R.string.browser_toast_share_failed, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 4:
                        // TODO
                        break;
                    case 5:
                        finish();
                        break;
                    default:
                        break;
                }

                dialog.hide();
                dialog.dismiss();
            }
        });
    }

    private void showSoftInput(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void hideSoftInput(View view) {
        view.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showSearchPanel() {
        searchPanel.setVisibility(View.VISIBLE);
        showSoftInput(searchBox);
    }

    private void hideSearchPanel() {
        hideSoftInput(searchBox);
        searchPanel.setVisibility(View.GONE);
        searchBox.setText("");
    }
}
