package io.github.mthli.Ninja.Activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.*;
import io.github.mthli.Ninja.Browser.AlbumController;
import io.github.mthli.Ninja.Browser.BrowserContainer;
import io.github.mthli.Ninja.Browser.BrowserController;
import io.github.mthli.Ninja.Task.ScreenshotTask;
import io.github.mthli.Ninja.Database.Record;
import io.github.mthli.Ninja.Database.RecordAction;
import io.github.mthli.Ninja.R;
import io.github.mthli.Ninja.Service.HolderService;
import io.github.mthli.Ninja.Unit.BrowserUnit;
import io.github.mthli.Ninja.Unit.IntentUnit;
import io.github.mthli.Ninja.Unit.ViewUnit;
import io.github.mthli.Ninja.View.*;

import java.util.*;

public class BrowserActivity extends Activity implements BrowserController {
    private static final int DOUBLE_TAPS_QUIT_DEFAULT = 512;

    private SwitcherPanel switcherPanel;
    private float dimen144dp;
    private float dimen108dp;
    private float dimen48dp;

    private HorizontalScrollView switcherScroller;
    private LinearLayout switcherContainer;
    private ImageButton switcherBookmarks;
    private ImageButton switcherHistory;
    private ImageButton switcherAdd;

    private RelativeLayout omnibox;
    private AutoCompleteTextView inputBox;
    private ImageButton omniboxBookmark;
    private ImageButton omniboxRefresh;
    private ImageButton omniboxOverflow;
    private ProgressBar progressBar;
    private FrameLayout contentFrame;

    private RelativeLayout searchPanel;
    private EditText searchBox;
    private ImageButton searchUp;
    private ImageButton searchDown;
    private ImageButton searchCancel;

    private static boolean quit = false;
    private boolean create = true;
    private int shortAnimTime = 0;
    private int longAnimTime = 0;
    private AlbumController currentAlbumController = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String anchor = sp.getString(getString(R.string.sp_anchor), getString(R.string.setting_summary_tab_position_top));
        if (anchor.equals(getString(R.string.setting_summary_tab_position_top))) {
            setContentView(R.layout.main_top);
        } else {
            setContentView(R.layout.main_bottom);
        }

        create = true;
        shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        longAnimTime = getResources().getInteger(android.R.integer.config_longAnimTime);
        switcherPanel = (SwitcherPanel) findViewById(R.id.switcher_panel);
        switcherPanel.setStatusListener(new SwitcherPanel.StatusListener() {
            @Override
            public void onFling() {}

            @Override
            public void onExpanded() {}

            @Override
            public void onCollapsed() {
                inputBox.clearFocus();
            }
        });

        dimen144dp = getResources().getDimensionPixelSize(R.dimen.layout_width_144dp);
        dimen108dp = getResources().getDimensionPixelSize(R.dimen.layout_height_108dp);
        dimen48dp = getResources().getDimensionPixelOffset(R.dimen.layout_height_48dp);

        initSwitcherView();
        initOmnibox();
        initSearchPanel();

        contentFrame = (FrameLayout) findViewById(R.id.main_content);
        dispatchIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (create) {
            return;
        }
        dispatchIntent(getIntent());

        if (IntentUnit.isDBChange()) {
            updateBookmarks();
            updateAutoComplete();
            IntentUnit.setDBChange(false);
        }

        if (IntentUnit.isSPChange()) {
            for (AlbumController controller : BrowserContainer.list()) {
                if (controller instanceof NinjaWebView) {
                    ((NinjaWebView) controller).initPreferences();
                }
            }
            IntentUnit.setSPChange(false);
        }
    }

    private void dispatchIntent(Intent intent) {
        Intent toService = new Intent(this, HolderService.class);
        IntentUnit.setClear(false);
        stopService(toService);

        if (intent != null && intent.hasExtra(IntentUnit.OPEN)) { // From HolderActivity's menu
            pinAlbums(intent.getStringExtra(IntentUnit.OPEN));
        } else {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            if (sp.getBoolean(getString(R.string.sp_first), true)) {
                String lang;
                if (getResources().getConfiguration().locale.getLanguage().equals("zh")) {
                    lang = BrowserUnit.NINJA_INTRODUCTION_ZH;
                } else {
                    lang = BrowserUnit.NINJA_INTRODUCTION_EN;
                }
                pinAlbums(BrowserUnit.BASE_URL + lang);
                sp.edit().putBoolean(getString(R.string.sp_first), false).commit();
            } else {
                pinAlbums(null);
            }
        }
    }

    @Override
    public void onPause() {
        Intent toService = new Intent(this, HolderService.class);
        IntentUnit.setClear(false);
        stopService(toService);

        create = false;
        inputBox.clearFocus();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Intent toService = new Intent(this, HolderService.class);
        IntentUnit.setClear(true);
        stopService(toService);

        BrowserContainer.clear();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        hideSoftInput(inputBox);
        hideSearchPanel();
        if (switcherPanel.getStatus() != SwitcherPanel.Status.EXPANDED) {
            switcherPanel.expanded();
        }
        super.onConfigurationChanged(newConfig);

        float coverHeight = ViewUnit.getWindowHeight(this) - ViewUnit.getStatusBarHeight(this) - dimen108dp - dimen48dp;
        switcherPanel.setCoverHeight(coverHeight);
        switcherPanel.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                switcherPanel.fixKeyBoardShowing(switcherPanel.getHeight());
                switcherPanel.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        updateAutoComplete(); // For inputBox.setDropDownWidth()
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            hideSoftInput(inputBox);
            if (switcherPanel.getStatus() != SwitcherPanel.Status.EXPANDED) {
                switcherPanel.expanded();
            } else if (currentAlbumController == null) {
                finish();
            } else if (currentAlbumController instanceof NinjaWebView) {
                NinjaWebView ninjaWebView = (NinjaWebView) currentAlbumController;
                if (ninjaWebView.canGoBack()) {
                    ninjaWebView.goBack();
                } else {
                    updateAlbum();
                }
            } else if (currentAlbumController instanceof NinjaRelativeLayout) {
                switch (currentAlbumController.getFlag()) {
                    case BrowserUnit.FLAG_BOOKMARKS:
                        updateAlbum();
                        break;
                    case BrowserUnit.FLAG_HISTORY:
                        updateAlbum();
                        break;
                    case BrowserUnit.FLAG_HOME:
                        doubleTapsQuit();
                        break;
                    default:
                        finish();
                        break;
                }
            } else {
                finish();
            }
        }
        return true;
    }

    private void initSwitcherView() {
        switcherScroller = (HorizontalScrollView) findViewById(R.id.switcher_scroller);
        switcherContainer = (LinearLayout) findViewById(R.id.switcher_container);
        switcherBookmarks = (ImageButton) findViewById(R.id.switcher_bookmarks);
        switcherHistory = (ImageButton) findViewById(R.id.switcher_history);
        switcherAdd = (ImageButton) findViewById(R.id.switcher_add);

        switcherBookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAlbum(BrowserUnit.FLAG_BOOKMARKS);
            }
        });

        switcherHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAlbum(BrowserUnit.FLAG_HISTORY);
            }
        });

        switcherAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAlbum(BrowserUnit.FLAG_HOME);
            }
        });
    }

    private void initOmnibox() {
        omnibox = (RelativeLayout) findViewById(R.id.main_omnibox);
        inputBox = (AutoCompleteTextView) findViewById(R.id.main_omnibox_input);
        omniboxBookmark = (ImageButton) findViewById(R.id.main_omnibox_bookmark);
        omniboxRefresh = (ImageButton) findViewById(R.id.main_omnibox_refresh);
        omniboxOverflow = (ImageButton) findViewById(R.id.main_omnibox_overflow);
        progressBar = (ProgressBar) findViewById(R.id.main_progress_bar);

        inputBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (currentAlbumController == null || !(actionId == EditorInfo.IME_ACTION_DONE)) {
                    return false;
                }

                String query = inputBox.getText().toString().trim();
                if (query.isEmpty()) {
                    NinjaToast.show(BrowserActivity.this, R.string.toast_input_empty);
                    return true;
                }

                updateAlbum(query);
                hideSoftInput(inputBox);
                return false;
            }
        });
        updateBookmarks();
        updateAutoComplete();

        omniboxBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!prepareRecord()) {
                    NinjaToast.show(BrowserActivity.this, R.string.toast_add_bookmark_failed);
                    return;
                }

                NinjaWebView ninjaWebView = (NinjaWebView) currentAlbumController;
                RecordAction action = new RecordAction(BrowserActivity.this);
                action.open(true);
                String title = ninjaWebView.getTitle();
                String url = ninjaWebView.getUrl();
                if (action.checkBookmark(url)) {
                    action.deleteBookmark(url);
                    NinjaToast.show(BrowserActivity.this, R.string.toast_delete_bookmark_successful);
                } else {
                    action.addBookmark(new Record(title, url, System.currentTimeMillis()));
                    NinjaToast.show(BrowserActivity.this, R.string.toast_add_bookmark_successful);
                }
                action.close();
                updateBookmarks();
                updateAutoComplete();
            }
        });

        omniboxRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentAlbumController == null) {
                    NinjaToast.show(BrowserActivity.this, R.string.toast_refresh_failed);
                    return;
                }

                if (currentAlbumController instanceof NinjaWebView) {
                    NinjaWebView ninjaWebView = (NinjaWebView) currentAlbumController;
                    if (ninjaWebView.isLoadFinish()) {
                        ninjaWebView.reload();
                    } else {
                        ninjaWebView.stopLoading();
                    }
                } else if (currentAlbumController instanceof NinjaRelativeLayout) {
                    final NinjaRelativeLayout layout = (NinjaRelativeLayout) currentAlbumController;
                    if (layout.getFlag() == BrowserUnit.FLAG_HOME) {
                        return;
                    }
                    updateProgress(BrowserUnit.PROGRESS_MIN);

                    RecordAction action = new RecordAction(BrowserActivity.this);
                    action.open(false);
                    final List<Record> list;
                    if (layout.getFlag() == BrowserUnit.FLAG_BOOKMARKS) {
                        list = action.listBookmarks();
                        Collections.sort(list, new Comparator<Record>() {
                            @Override
                            public int compare(Record first, Record second) {
                                return first.getTitle().compareTo(second.getTitle());
                            }
                        });
                    } else if (layout.getFlag() == BrowserUnit.FLAG_HISTORY) {
                        list = action.listHistory();
                    } else {
                        list = new ArrayList<Record>();
                    }
                    action.close();

                    ListView listView = (ListView) layout.findViewById(R.id.record_list);
                    TextView textView = (TextView) layout.findViewById(R.id.record_list_empty);
                    listView.setEmptyView(textView);

                    final RecordAdapter adapter = new RecordAdapter(BrowserActivity.this, R.layout.record_item, list);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    /* Wait for adapter.notifyDataSetChanged() */
                    listView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            layout.setAlbumCover(ViewUnit.capture(layout, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
                        }
                    }, shortAnimTime);
                    updateProgress(BrowserUnit.PROGRESS_MAX);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            updateAlbum(list.get(position).getURL());
                        }
                    });

                    listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            showListMenu(adapter, list, position);
                            return true;
                        }
                    });
                } else {
                    NinjaToast.show(BrowserActivity.this, R.string.toast_refresh_failed);
                }
            }
        });

        omniboxOverflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOverflow();
            }
        });
    }

    private void initSearchPanel() {
        searchPanel = (RelativeLayout) findViewById(R.id.main_search_panel);
        searchBox = (EditText) findViewById(R.id.main_search_box);
        searchUp = (ImageButton) findViewById(R.id.main_search_up);
        searchDown = (ImageButton) findViewById(R.id.main_search_down);
        searchCancel = (ImageButton) findViewById(R.id.main_search_cancel);

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (currentAlbumController != null && currentAlbumController instanceof NinjaWebView) {
                    ((NinjaWebView) currentAlbumController).findAllAsync(s.toString());
                }
            }
        });

        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != EditorInfo.IME_ACTION_DONE) {
                    return false;
                }

                if (searchBox.getText().toString().isEmpty()) {
                    NinjaToast.show(BrowserActivity.this, R.string.toast_input_empty);
                    return true;
                }
                return false;
            }
        });

        searchUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchBox.getText().toString();
                if (query.isEmpty()) {
                    NinjaToast.show(BrowserActivity.this, R.string.toast_input_empty);
                    return;
                }

                hideSoftInput(searchBox);
                if (currentAlbumController instanceof NinjaWebView) {
                    ((NinjaWebView) currentAlbumController).findNext(false);
                }
            }
        });

        searchDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchBox.getText().toString();
                if (query.isEmpty()) {
                    NinjaToast.show(BrowserActivity.this, R.string.toast_input_empty);
                    return;
                }

                hideSoftInput(searchBox);
                if (currentAlbumController instanceof NinjaWebView) {
                    ((NinjaWebView) currentAlbumController).findNext(true);
                }
            }
        });

        searchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSearchPanel();
            }
        });
    }

    private synchronized void addAlbum(int flag) {
        final AlbumController holder;
        if (flag == BrowserUnit.FLAG_BOOKMARKS) {
            NinjaRelativeLayout layout = (NinjaRelativeLayout) getLayoutInflater().inflate(R.layout.record_list, null, false);
            layout.setBrowserController(this);
            layout.setFlag(BrowserUnit.FLAG_BOOKMARKS);
            layout.setAlbumCover(ViewUnit.capture(layout, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
            layout.setAlbumTitle(getString(R.string.album_title_bookmarks));
            holder = layout;

            RecordAction action = new RecordAction(this);
            action.open(false);
            final List<Record> list = action.listBookmarks();
            action.close();
            Collections.sort(list, new Comparator<Record>() {
                @Override
                public int compare(Record first, Record second) {
                    return first.getTitle().compareTo(second.getTitle());
                }
            });

            ListView listView = (ListView) layout.findViewById(R.id.record_list);
            TextView textView = (TextView) layout.findViewById(R.id.record_list_empty);
            listView.setEmptyView(textView);

            final RecordAdapter adapter = new RecordAdapter(this, R.layout.record_item, list);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    updateAlbum(list.get(position).getURL());
                }
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    showListMenu(adapter, list, position);
                    return true;
                }
            });
        } else if (flag == BrowserUnit.FLAG_HISTORY) {
            NinjaRelativeLayout layout = (NinjaRelativeLayout) getLayoutInflater().inflate(R.layout.record_list, null, false);
            layout.setBrowserController(this);
            layout.setFlag(BrowserUnit.FLAG_HISTORY);
            layout.setAlbumCover(ViewUnit.capture(layout, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
            layout.setAlbumTitle(getString(R.string.album_title_history));
            holder = layout;

            RecordAction action = new RecordAction(this);
            action.open(false);
            final List<Record> list = action.listHistory();
            action.close();

            ListView listView = (ListView) layout.findViewById(R.id.record_list);
            TextView textView = (TextView) layout.findViewById(R.id.record_list_empty);
            listView.setEmptyView(textView);

            final RecordAdapter adapter = new RecordAdapter(this, R.layout.record_item, list);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    updateAlbum(list.get(position).getURL());
                }
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    showListMenu(adapter, list, position);
                    return true;
                }
            });
        } else if (flag == BrowserUnit.FLAG_HOME) {
            NinjaRelativeLayout layout = (NinjaRelativeLayout) getLayoutInflater().inflate(R.layout.home, null, false);
            layout.setBrowserController(this);
            layout.setFlag(BrowserUnit.FLAG_HOME);
            layout.setAlbumCover(ViewUnit.capture(layout, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
            layout.setAlbumTitle(getString(R.string.album_title_home));
            holder = layout;
        } else {
            return;
        }

        final View albumView = holder.getAlbumView();
        albumView.setVisibility(View.INVISIBLE);

        BrowserContainer.add(holder);
        switcherContainer.addView(albumView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
                albumView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showAlbum(holder, true, true);
            }
        });
        albumView.startAnimation(animation);
    }

    private synchronized void addAlbum(String title, final String url, final boolean foreground, final Message resultMsg) {
        final NinjaWebView webView = new NinjaWebView(this);
        webView.setBrowserController(this);
        webView.setFlag(BrowserUnit.FLAG_NINJA);
        webView.setAlbumCover(ViewUnit.capture(webView, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
        webView.setAlbumTitle(title);
        ViewUnit.bound(this, webView);

        final View albumView = webView.getAlbumView();
        if (currentAlbumController != null && (currentAlbumController instanceof NinjaWebView) && resultMsg != null) {
            int index = BrowserContainer.indexOf(currentAlbumController) + 1;
            BrowserContainer.add(webView, index);
            switcherContainer.addView(albumView, index, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
        } else {
            BrowserContainer.add(webView);
            switcherContainer.addView(albumView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }

        if (!foreground) {
            ViewUnit.bound(this, webView);
            webView.loadUrl(url);
            webView.deactivate();

            albumView.setVisibility(View.VISIBLE);
            if (currentAlbumController != null) {
                switcherScroller.smoothScrollTo(currentAlbumController.getAlbumView().getLeft(), 0);
            }
            return;
        }

        albumView.setVisibility(View.INVISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
                albumView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showAlbum(webView, true, false);

                if (url != null && !url.isEmpty()) {
                    webView.loadUrl(url);
                } else if (resultMsg != null) {
                    WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                    transport.setWebView(webView);
                    resultMsg.sendToTarget();
                }
            }
        });
        albumView.startAnimation(animation);
    }

    private synchronized void pinAlbums(String url) {
        hideSoftInput(inputBox);
        hideSearchPanel();
        switcherContainer.removeAllViews();
        contentFrame.removeAllViews();

        for (AlbumController controller : BrowserContainer.list()) {
            if (controller instanceof NinjaWebView) {
                ((NinjaWebView) controller).setBrowserController(this);
            } else if (controller instanceof NinjaRelativeLayout) {
                ((NinjaRelativeLayout) controller).setBrowserController(this);
            }
            switcherContainer.addView(controller.getAlbumView(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            controller.getAlbumView().setVisibility(View.VISIBLE);
            controller.deactivate();
        }

        if (BrowserContainer.size() < 1 && url == null) {
            addAlbum(BrowserUnit.FLAG_HOME); // TODO
        } else if (BrowserContainer.size() >= 1 && url == null) {
            int index = BrowserContainer.size() - 1;
            if (currentAlbumController != null) {
                index = BrowserContainer.indexOf(currentAlbumController);
                currentAlbumController.deactivate();
            }
            currentAlbumController = BrowserContainer.get(index);
            contentFrame.addView((View) currentAlbumController);
            currentAlbumController.activate();

            updateOmnibox();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    switcherScroller.smoothScrollTo(currentAlbumController.getAlbumView().getLeft(), 0);
                    currentAlbumController.setAlbumCover(ViewUnit.capture(((View) currentAlbumController), dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
                }
            }, shortAnimTime);
        } else { // When url != null
            NinjaWebView webView = new NinjaWebView(this);
            webView.setBrowserController(this);
            webView.setFlag(BrowserUnit.FLAG_NINJA);
            webView.setAlbumCover(ViewUnit.capture(webView, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
            webView.setAlbumTitle(getString(R.string.album_untitled));
            ViewUnit.bound(this, webView);
            webView.loadUrl(url);

            BrowserContainer.add(webView);
            final View albumView = webView.getAlbumView();
            albumView.setVisibility(View.VISIBLE);
            switcherContainer.addView(albumView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            contentFrame.addView(webView);

            if (currentAlbumController != null) {
                currentAlbumController.deactivate();
            }
            currentAlbumController = webView;
            currentAlbumController.activate();

            updateOmnibox();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    switcherScroller.smoothScrollTo(currentAlbumController.getAlbumView().getLeft(), 0);
                    currentAlbumController.setAlbumCover(ViewUnit.capture(((View) currentAlbumController), dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
                }
            }, shortAnimTime);
        }
    }

    @Override
    public synchronized void showAlbum(AlbumController controller, final boolean expand, final boolean capture) {
        if (controller == null || controller == currentAlbumController) {
            switcherPanel.expanded();
            return;
        }

        if (currentAlbumController != null) {
            currentAlbumController.deactivate();
        }
        contentFrame.removeAllViews();
        contentFrame.addView((View) controller);

        currentAlbumController = controller;
        currentAlbumController.activate();
        switcherScroller.smoothScrollTo(currentAlbumController.getAlbumView().getLeft(), 0);
        updateOmnibox();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (expand) {
                    switcherPanel.expanded();
                }

                if (capture) {
                    currentAlbumController.setAlbumCover(ViewUnit.capture(((View) currentAlbumController), dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
                }
            }
        }, shortAnimTime);
    }

    private synchronized void updateAlbum() {
        if (currentAlbumController == null) {
            return;
        }

        NinjaRelativeLayout layout = (NinjaRelativeLayout) getLayoutInflater().inflate(R.layout.home, null, false);
        layout.setBrowserController(this);
        layout.setFlag(BrowserUnit.FLAG_HOME);
        layout.setAlbumCover(ViewUnit.capture(layout, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
        layout.setAlbumTitle(getString(R.string.album_title_home));

        int index = switcherContainer.indexOfChild(currentAlbumController.getAlbumView());
        currentAlbumController.deactivate();
        switcherContainer.removeView(currentAlbumController.getAlbumView());
        contentFrame.removeAllViews();

        switcherContainer.addView(layout.getAlbumView(), index, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        contentFrame.addView(layout);
        BrowserContainer.set(layout, index);
        currentAlbumController = layout;
        updateOmnibox();
    }

    private synchronized void updateAlbum(String url) {
        if (currentAlbumController == null) {
            return;
        }

        if (currentAlbumController instanceof NinjaWebView) {
            ((NinjaWebView) currentAlbumController).loadUrl(url);
            updateOmnibox();
        } else if (currentAlbumController instanceof NinjaRelativeLayout) {
            NinjaWebView webView = new NinjaWebView(this);
            webView.setBrowserController(this);
            webView.setFlag(BrowserUnit.FLAG_NINJA);
            webView.setAlbumCover(ViewUnit.capture(webView, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
            webView.setAlbumTitle(getString(R.string.album_untitled));
            ViewUnit.bound(this, webView);

            int index = switcherContainer.indexOfChild(currentAlbumController.getAlbumView());
            currentAlbumController.deactivate();
            switcherContainer.removeView(currentAlbumController.getAlbumView());
            contentFrame.removeAllViews();

            switcherContainer.addView(webView.getAlbumView(), index, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            contentFrame.addView(webView);
            BrowserContainer.set(webView, index);
            currentAlbumController = webView;
            webView.activate();

            webView.loadUrl(url);
            updateOmnibox();
        } else {
            NinjaToast.show(this, R.string.toast_load_error);
        }
    }

    @Override
    public synchronized void removeAlbum(AlbumController controller) {
        if (currentAlbumController == null || BrowserContainer.size() <= 1) {
            switcherContainer.removeView(controller.getAlbumView());
            BrowserContainer.remove(controller);
            addAlbum(BrowserUnit.FLAG_HOME);
            return;
        }

        if (controller != currentAlbumController) {
            switcherContainer.removeView(controller.getAlbumView());
            BrowserContainer.remove(controller);
        } else {
            switcherContainer.removeView(controller.getAlbumView());
            int index = BrowserContainer.indexOf(controller);
            BrowserContainer.remove(controller);
            if (index >= BrowserContainer.size()) {
                index = BrowserContainer.size() - 1;
            }
            showAlbum(BrowserContainer.get(index), false, false);
        }
    }

    @Override
    public void updateAutoComplete() {
        RecordAction action = new RecordAction(this);
        action.open(false);
        List<Record> list = action.listBookmarks();
        list.addAll(action.listHistory());
        action.close();

        final CompleteAdapter adapter = new CompleteAdapter(this, R.layout.complete_item, list);
        inputBox.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        inputBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = ((TextView) view.findViewById(R.id.complete_item_url)).getText().toString();
                inputBox.setText(url);
                inputBox.setSelection(url.length());
                updateAlbum(url);
                hideSoftInput(inputBox);
            }
        });
    }

    @Override
    public void updateBookmarks() {
        if (currentAlbumController == null || !(currentAlbumController instanceof NinjaWebView)) {
            omniboxBookmark.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bookmark_selector_dark));
            return;
        }

        RecordAction action = new RecordAction(this);
        action.open(false);
        String url = ((NinjaWebView) currentAlbumController).getUrl();
        if (action.checkBookmark(url)) {
            omniboxBookmark.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bookmark_selector_blue));
        } else {
            omniboxBookmark.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bookmark_selector_dark));
        }
        action.close();
    }

    @Override
    public void updateInputBox(String query) {
        inputBox.setText(query);
        inputBox.clearFocus();
    }

    private void updateOmnibox() {
        if (currentAlbumController == null) {
            return;
        }

        if (currentAlbumController instanceof NinjaRelativeLayout) {
            updateProgress(BrowserUnit.PROGRESS_MAX);
            updateBookmarks();
            updateInputBox(null);
        } else if (currentAlbumController instanceof NinjaWebView) {
            NinjaWebView ninjaWebView = (NinjaWebView) currentAlbumController;
            updateProgress(ninjaWebView.getProgress());
            updateBookmarks();
            if (ninjaWebView.getUrl() == null && ninjaWebView.getOriginalUrl() == null) {
                updateInputBox(null);
            } else if (ninjaWebView.getUrl() != null) {
                updateInputBox(ninjaWebView.getUrl());
            } else {
                updateInputBox(ninjaWebView.getOriginalUrl());
            }
        }
    }

    @Override
    public synchronized void updateProgress(int progress) {
        if (progress > progressBar.getProgress()) {
            ObjectAnimator animator = ObjectAnimator.ofInt(progressBar, "progress", progress);
            animator.setDuration(shortAnimTime);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.start();
        } else if (progress < progressBar.getProgress()) {
            ObjectAnimator animator = ObjectAnimator.ofInt(progressBar, "progress", 0, progress);
            animator.setDuration(shortAnimTime);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.start();
        }

        updateBookmarks();
        if (progress < BrowserUnit.PROGRESS_MAX) {
            updateRefresh(true);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            updateRefresh(false);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void updateRefresh(boolean running) {
        if (running) {
            omniboxRefresh.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.cl_selector_dark));
        } else {
            omniboxRefresh.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.refresh_selector));
        }
    }

    @Override
    public void onCreateView(WebView view, final Message resultMsg) {
        if (resultMsg == null) {
            return;
        }
        switcherPanel.collapsed();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addAlbum(getString(R.string.album_untitled), null, true, resultMsg);
            }
        }, shortAnimTime);
    }

    @Override
    public void onLongPress(String url) {
        WebView.HitTestResult result;
        if (!(currentAlbumController instanceof NinjaWebView)) {
            return;
        }
        result = ((NinjaWebView) currentAlbumController).getHitTestResult();

        final List<String> list = new ArrayList<>();
        list.add(getString(R.string.main_menu_new_tab));
        list.add(getString(R.string.main_menu_copy_link));
        if (result != null && (result.getType() == WebView.HitTestResult.IMAGE_TYPE || result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE)) {
            list.add(getString(R.string.main_menu_save));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog, null, false);
        builder.setView(layout);

        ListView listView = (ListView) layout.findViewById(R.id.dialog_list);
        DialogAdapter adapter = new DialogAdapter(this, R.layout.dialog_text_item, list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        final AlertDialog dialog = builder.create();
        if (url != null || (result != null && result.getExtra() != null)) {
            if (url == null) {
                url = result.getExtra();
            }
            dialog.show();
        }

        final String target = url;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = list.get(position);
                if (s.equals(getString(R.string.main_menu_new_tab))) { // New tab
                    addAlbum(getString(R.string.album_untitled), target, false, null);
                    NinjaToast.show(BrowserActivity.this, R.string.toast_new_tab_successful);
                } else if (s.equals(getString(R.string.main_menu_copy_link))) { // Copy link
                    BrowserUnit.copyURL(BrowserActivity.this, target);
                } else if (s.equals(getString(R.string.main_menu_save))) { // Save
                    BrowserUnit.download(BrowserActivity.this, target, target, BrowserUnit.MIME_TYPE_IMAGE);
                }
                dialog.hide();
                dialog.dismiss();
            }
        });
    }

    private void doubleTapsQuit() {
        final Timer timer = new Timer();
        if (!quit) {
            quit = true;
            NinjaToast.show(this, R.string.toast_double_taps_quit);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    quit = false;
                    timer.cancel();
                }
            }, DOUBLE_TAPS_QUIT_DEFAULT);
        } else {
            timer.cancel();
            finish();
        }
    }

    private void hideSoftInput(View view) {
        view.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showSoftInput(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void hideSearchPanel() {
        hideSoftInput(searchBox);
        searchBox.setText("");
        searchPanel.setVisibility(View.GONE);
        omnibox.setVisibility(View.VISIBLE);
    }

    private void showSearchPanel() {
        omnibox.setVisibility(View.GONE);
        searchPanel.setVisibility(View.VISIBLE);
        showSoftInput(searchBox);
    }

    private void showOverflow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog, null, false);
        builder.setView(layout);

        final String[] array = getResources().getStringArray(R.array.main_overflow);
        final List<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(array));
        if (currentAlbumController != null && currentAlbumController instanceof NinjaRelativeLayout) {
            list.remove(array[0]); // Go to top
            list.remove(array[1]); // Go forward
            list.remove(array[2]); // Find in page
            list.remove(array[3]); // Screenshot
            list.remove(array[4]); // Share
        }

        ListView listView = (ListView) layout.findViewById(R.id.dialog_list);
        DialogAdapter adapter = new DialogAdapter(this, R.layout.dialog_text_item, list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        final AlertDialog dialog = builder.create();
        dialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = list.get(position);
                if (s.equals(array[0])) { // Go to top
                    NinjaWebView ninjaWebView = (NinjaWebView) currentAlbumController;
                    ninjaWebView.scrollTo(0, 0); // Maybe use Handler().postDelay() would better.
                } else if (s.equals(array[1])) { // Go forward
                    NinjaWebView ninjaWebView = (NinjaWebView) currentAlbumController;
                    if (ninjaWebView.canGoForward()) {
                        ninjaWebView.goForward();
                    } else {
                        NinjaToast.show(BrowserActivity.this, R.string.toast_already_at_the_front);
                    }
                } else if (s.equals(array[2])) { // Find in page
                    hideSoftInput(inputBox);
                    showSearchPanel();
                } else if (s.equals(array[3])) { // Screenshot
                    NinjaWebView ninjaWebView = (NinjaWebView) currentAlbumController;
                    new ScreenshotTask(BrowserActivity.this, ninjaWebView).execute();
                } else if (s.equals(array[4])) { // Share
                    if (!prepareRecord()) {
                        NinjaToast.show(BrowserActivity.this, R.string.toast_share_failed);
                    } else {
                        NinjaWebView ninjaWebView = (NinjaWebView) currentAlbumController;
                        IntentUnit.share(BrowserActivity.this, ninjaWebView.getTitle(), ninjaWebView.getUrl());
                    }
                } else if (s.equals(array[5])) { // Setting
                    Intent intent = new Intent(BrowserActivity.this, SettingActivity.class);
                    startActivity(intent);
                } else if (s.equals(array[6])) { // Quit
                    finish();
                }
                dialog.hide();
                dialog.dismiss();
            }
        });
    }

    private void showListMenu(final RecordAdapter listAdapter, final List<Record> recordList, final int location) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog, null, false);
        builder.setView(layout);

        final String[] array = getResources().getStringArray(R.array.list_menu);
        final List<String> stringList = new ArrayList<>();
        stringList.addAll(Arrays.asList(array));
        if (currentAlbumController.getFlag() != BrowserUnit.FLAG_BOOKMARKS) {
            stringList.remove(array[3]);
        }

        ListView listView = (ListView) layout.findViewById(R.id.dialog_list);
        DialogAdapter dialogAdapter = new DialogAdapter(this, R.layout.dialog_text_item, stringList);
        listView.setAdapter(dialogAdapter);
        dialogAdapter.notifyDataSetChanged();

        final AlertDialog dialog = builder.create();
        dialog.show();

        final Record record = recordList.get(location);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = stringList.get(position);
                if (s.equals(array[0])) { // New tab
                    addAlbum(getString(R.string.album_untitled), record.getURL(), false, null);
                    NinjaToast.show(BrowserActivity.this, R.string.toast_new_tab_successful);
                } else if (s.equals(array[1])) { // Copy link
                    BrowserUnit.copyURL(BrowserActivity.this, record.getURL());
                } else if (s.equals(array[2])) { // Share
                    IntentUnit.share(BrowserActivity.this, record.getTitle(), record.getURL());
                } else if (s.equals(array[3])) { // Edit
                    showEditDialog(listAdapter, recordList, location);
                } else if (s.equals(array[4])) { // Delete
                    RecordAction action = new RecordAction(BrowserActivity.this);
                    action.open(true);
                    if (currentAlbumController.getFlag() == BrowserUnit.FLAG_BOOKMARKS) {
                        action.deleteBookmark(record);
                    } else if (currentAlbumController.getFlag() == BrowserUnit.FLAG_HISTORY) {
                        action.deleteHistory(record);
                    }
                    action.close();
                    recordList.remove(location);
                    listAdapter.notifyDataSetChanged();
                    updateBookmarks();
                    updateAutoComplete();
                    NinjaToast.show(BrowserActivity.this, R.string.toast_delete_successful);
                }
                dialog.hide();
                dialog.dismiss();
            }
        });
    }

    private void showEditDialog(final RecordAdapter listAdapter, List<Record> recordList, int location) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_edit, null, false);
        builder.setView(layout);

        final AlertDialog dialog = builder.create();
        dialog.show();

        final Record record = recordList.get(location);
        final EditText editText = (EditText) layout.findViewById(R.id.dialog_edit);
        editText.setText(record.getTitle());
        editText.setSelection(record.getTitle().length());
        hideSoftInput(inputBox);
        showSoftInput(editText);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != EditorInfo.IME_ACTION_DONE) {
                    return false;
                }

                String text = editText.getText().toString().trim();
                if (text.isEmpty()) {
                    NinjaToast.show(BrowserActivity.this, R.string.toast_input_empty);
                    return true;
                }

                RecordAction action = new RecordAction(BrowserActivity.this);
                action.open(true);
                record.setTitle(text);
                action.updateBookmark(record);
                action.close();

                listAdapter.notifyDataSetChanged();
                hideSoftInput(editText);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.hide();
                        dialog.dismiss();
                    }
                }, longAnimTime);
                return false;
            }
        });
    }

    private boolean prepareRecord() {
        if (currentAlbumController == null || !(currentAlbumController instanceof NinjaWebView)) {
            return false;
        }

        NinjaWebView webView = (NinjaWebView) currentAlbumController;
        String title = webView.getTitle();
        String url = webView.getUrl();
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
}
