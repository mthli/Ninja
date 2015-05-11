package io.github.mthli.Ninja.Activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
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
import io.github.mthli.Ninja.Unit.BrowserUnit;
import io.github.mthli.Ninja.Unit.IntentUnit;
import io.github.mthli.Ninja.Unit.ViewUnit;
import io.github.mthli.Ninja.View.*;

import java.util.*;

public class BrowserActivity extends Activity implements BrowserController {
    private static final int DOUBLE_TAPS_QUIT_DEFAULT = 512;

    private SwitcherPanel switcherPanel;

    private int windowWidth;
    private int windowHeight;
    private int statusBarHeight;
    private float dimen144dp;
    private float dimen108dp;
    private float dimen48dp;

    private HorizontalScrollView swictherScroller;
    private LinearLayout switcherContainer;
    private ImageButton swictherBookmarks;
    private ImageButton swictherHistory;
    private ImageButton switcherAdd;

    private LinearLayout mainView;
    private RelativeLayout omnibox;
    private AutoCompleteTextView inputBox;
    private ImageButton omniboxBookmark;
    private ImageButton omniboxRefresh;
    private ImageButton omniboxOverflow;
    private LinearLayout progressWrapper;
    private ProgressBar progressBar;
    private FrameLayout contentFrame;

    private RelativeLayout searchPanel;
    private EditText searchBox;
    private ImageButton searchUp;
    private ImageButton searchDown;
    private ImageButton searchCancel;

    private static final int BRIGHTNESS_BEGIN_DEFAULT = 130;
    private static boolean quit = false;
    private boolean create = true;
    private int animTime = 0;
    private AlbumController currentAlbumController = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int brightness = sp.getInt(getString(R.string.sp_brightness), -1);
        if (brightness < 0) {
            brightness = ViewUnit.getBrightness(this);  // 130
            sp.edit().putInt(getString(R.string.sp_brightness), brightness).commit();
        }
        ViewUnit.setBrightness(this, brightness);

        create = true;
        animTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        switcherPanel = (SwitcherPanel) findViewById(R.id.switcher_panel);
        switcherPanel.setStatusListener(new SwitcherPanel.StatusListener() {
            @Override
            public void onFling() {}

            @Override
            public void onExpanded() {}

            @Override
            public void onCollapsed() {
                hideSoftInput(inputBox);
            }
        });

        initData();
        initSwitcherView();
        initMainView();
        initSearchPanel();
        addAlbum(BrowserUnit.FLAG_HOME); // TODO
    }

    // TODO
    @Override
    public void onResume() {
        super.onResume();
    }

    // TODO
    @Override
    public void onPause() {
        super.onPause();
    }

    // TODO
    @Override
    public void onDestroy() {
        BrowserContainer.clear();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (switcherPanel.getStatus() != SwitcherPanel.Status.EXPANDED) {
            switcherPanel.expanded();
        }
        float coverHeight = windowHeight - statusBarHeight - dimen108dp - dimen48dp;
        switcherPanel.setCoverHeight(coverHeight);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
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

    private void initData() {
        windowWidth = ViewUnit.getWindowWidth(this);
        windowHeight = ViewUnit.getWindowHeight(this);
        statusBarHeight = ViewUnit.getStatusBarHeight(this);
        dimen144dp = getResources().getDimensionPixelSize(R.dimen.layout_width_144dp);
        dimen108dp = getResources().getDimensionPixelSize(R.dimen.layout_height_108dp);
        dimen48dp = getResources().getDimensionPixelOffset(R.dimen.layout_height_48dp);
    }

    private void initSwitcherView() {
        swictherScroller = (HorizontalScrollView) findViewById(R.id.switcher_scroller);
        switcherContainer = (LinearLayout) findViewById(R.id.switcher_container);
        swictherBookmarks = (ImageButton) findViewById(R.id.switcher_bookmarks);
        swictherHistory = (ImageButton) findViewById(R.id.switcher_history);
        switcherAdd = (ImageButton) findViewById(R.id.switcher_add);

        swictherBookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAlbum(BrowserUnit.FLAG_BOOKMARKS);
            }
        });

        swictherHistory.setOnClickListener(new View.OnClickListener() {
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

    private void initMainView() {
        mainView = (LinearLayout) findViewById(R.id.main_view);
        omnibox = (RelativeLayout) findViewById(R.id.main_omnibox);
        inputBox = (AutoCompleteTextView) findViewById(R.id.main_omnibox_input);
        omniboxBookmark = (ImageButton) findViewById(R.id.main_omnibox_bookmark);
        omniboxRefresh = (ImageButton) findViewById(R.id.main_omnibox_refresh);
        omniboxOverflow = (ImageButton) findViewById(R.id.main_omnibox_overflow);
        progressWrapper = (LinearLayout) findViewById(R.id.main_progress_wrapper);
        progressBar = (ProgressBar) findViewById(R.id.main_progress_bar);
        contentFrame = (FrameLayout) findViewById(R.id.main_content);

        inputBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (currentAlbumController == null || !(actionId == EditorInfo.IME_ACTION_DONE)) {
                    return false;
                }

                String query = inputBox.getText().toString().trim();
                if (query.isEmpty()) {
                    NinjaToast.show(BrowserActivity.this, R.string.toast_input_empty);
                    return false;
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
                    List<Record> list = new ArrayList<Record>();
                    if (layout.getFlag() == BrowserUnit.FLAG_BOOKMARKS) {
                        list = action.listBookmarks();
                    } else if (layout.getFlag() == BrowserUnit.FLAG_HISTORY) {
                        list = action.listHistory();
                    }
                    action.close();

                    ListView listView = (ListView) layout.findViewById(R.id.list);
                    TextView textView = (TextView) layout.findViewById(R.id.list_empty);
                    listView.setEmptyView(textView);

                    NinjaListAdapter adapter = new NinjaListAdapter(BrowserActivity.this, R.layout.list_item, list);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    /* Wait for adapter.notifyDataSetChanged() */
                    listView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            layout.setAlbumCover(ViewUnit.capture(layout, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
                        }
                    }, animTime);
                    updateProgress(BrowserUnit.PROGRESS_MAX);
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
        searchPanel = (RelativeLayout) getLayoutInflater().inflate(R.layout.search, null, false);
        searchBox = (EditText) searchPanel.findViewById(R.id.search_box);
        searchUp = (ImageButton) searchPanel.findViewById(R.id.search_up);
        searchDown = (ImageButton) searchPanel.findViewById(R.id.search_down);
        searchCancel = (ImageButton) searchPanel.findViewById(R.id.search_cancel);

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
            NinjaRelativeLayout bookmarksLayout = (NinjaRelativeLayout) getLayoutInflater().inflate(R.layout.list, null, false);
            bookmarksLayout.setBrowserController(this);
            bookmarksLayout.setFlag(BrowserUnit.FLAG_BOOKMARKS);
            bookmarksLayout.setAlbumCover(ViewUnit.capture(bookmarksLayout, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
            bookmarksLayout.setAlbumTitle(getString(R.string.album_title_bookmarks));
            holder = bookmarksLayout;

            RecordAction action = new RecordAction(this);
            action.open(false);
            List<Record> list = action.listBookmarks();
            action.close();

            ListView listView = (ListView) bookmarksLayout.findViewById(R.id.list);
            TextView textView = (TextView) bookmarksLayout.findViewById(R.id.list_empty);
            listView.setEmptyView(textView);

            NinjaListAdapter adapter = new NinjaListAdapter(this, R.layout.list_item, list);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO
                }
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO
                    return true;
                }
            });
        } else if (flag == BrowserUnit.FLAG_HISTORY) {
            NinjaRelativeLayout historyLayout = (NinjaRelativeLayout) getLayoutInflater().inflate(R.layout.list, null, false);
            historyLayout.setBrowserController(this);
            historyLayout.setFlag(BrowserUnit.FLAG_HISTORY);
            historyLayout.setAlbumCover(ViewUnit.capture(historyLayout, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
            historyLayout.setAlbumTitle(getString(R.string.album_title_history));
            holder = historyLayout;

            RecordAction action = new RecordAction(this);
            action.open(false);
            List<Record> list = action.listHistory();
            action.close();

            ListView listView = (ListView) historyLayout.findViewById(R.id.list);
            TextView textView = (TextView) historyLayout.findViewById(R.id.list_empty);
            listView.setEmptyView(textView);

            NinjaListAdapter adapter = new NinjaListAdapter(this, R.layout.list_item, list);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO
                }
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO
                    return true;
                }
            });
        } else if (flag == BrowserUnit.FLAG_HOME) {
            NinjaRelativeLayout homeLayout = (NinjaRelativeLayout) getLayoutInflater().inflate(R.layout.home, null, false);
            homeLayout.setBrowserController(this);
            homeLayout.setFlag(BrowserUnit.FLAG_HOME);
            homeLayout.setAlbumCover(ViewUnit.capture(homeLayout, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
            homeLayout.setAlbumTitle(getString(R.string.album_title_home));
            holder = homeLayout;
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
                showAlbum(holder, true);
            }
        });
        albumView.startAnimation(animation);
    }

    private synchronized void addAlbum(String title, final String url, final boolean foreground, final Message resultMsg) {
        final NinjaWebView ninjaWebView = new NinjaWebView(this);
        ninjaWebView.setBrowserController(this);
        ninjaWebView.setFlag(BrowserUnit.FLAG_NINJA);
        ninjaWebView.setAlbumCover(ViewUnit.capture(ninjaWebView, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
        ninjaWebView.setAlbumTitle(title);

        BrowserContainer.add(ninjaWebView);
        final View albumView = ninjaWebView.getAlbumView();
        switcherContainer.addView(albumView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        if (!foreground) {
            /* Very important for displaying webview's layout correctly */
            int specWidth = View.MeasureSpec.makeMeasureSpec(windowWidth, View.MeasureSpec.EXACTLY);
            int specHeight = View.MeasureSpec.makeMeasureSpec((int) (windowHeight - statusBarHeight - dimen48dp), View.MeasureSpec.EXACTLY);
            ninjaWebView.measure(specWidth, specHeight);
            ninjaWebView.layout(0, 0, ninjaWebView.getMeasuredWidth(), ninjaWebView.getMeasuredHeight());
            ninjaWebView.loadUrl(url);
            ninjaWebView.deactivate();

            albumView.setVisibility(View.VISIBLE);
            if (currentAlbumController != null) {
                swictherScroller.smoothScrollTo(currentAlbumController.getAlbumView().getLeft(), 0);
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
                showAlbum(ninjaWebView, false);

                if (url != null && !url.isEmpty()) {
                    ninjaWebView.loadUrl(url);
                } else if (resultMsg != null) {
                    WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                    transport.setWebView(ninjaWebView);
                    resultMsg.sendToTarget();
                }
            }
        });
        albumView.startAnimation(animation);
    }

    @Override
    public synchronized void showAlbum(AlbumController albumController, final boolean capture) {
        if (albumController == null || albumController == currentAlbumController) {
            switcherPanel.expanded();
            return;
        }

        if (currentAlbumController != null) {
            currentAlbumController.deactivate();
        }
        contentFrame.removeAllViews();
        contentFrame.addView((View) albumController, 0);

        currentAlbumController = albumController;
        currentAlbumController.activate();
        updateOmnibox();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swictherScroller.smoothScrollTo(currentAlbumController.getAlbumView().getLeft(), 0);
                switcherPanel.expanded();
                if (capture) {
                    currentAlbumController.setAlbumCover(ViewUnit.capture(((View) currentAlbumController), dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
                }
            }
        }, animTime);
    }

    private synchronized void updateAlbum() {
        if (currentAlbumController == null) {
            return;
        }

        NinjaRelativeLayout homeLayout = (NinjaRelativeLayout) getLayoutInflater().inflate(R.layout.home, null, false);
        homeLayout.setBrowserController(this);
        homeLayout.setFlag(BrowserUnit.FLAG_HOME);
        homeLayout.setAlbumCover(ViewUnit.capture(homeLayout, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
        homeLayout.setAlbumTitle(getString(R.string.album_title_home));

        int index = switcherContainer.indexOfChild(currentAlbumController.getAlbumView());
        currentAlbumController.deactivate();
        switcherContainer.removeView(currentAlbumController.getAlbumView());
        contentFrame.removeAllViews();

        switcherContainer.addView(homeLayout.getAlbumView(), index, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        contentFrame.addView(homeLayout, 0);
        BrowserContainer.set(homeLayout, index);
        currentAlbumController = homeLayout;
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
            NinjaWebView ninjaWebView = new NinjaWebView(this);
            ninjaWebView.setBrowserController(this);
            ninjaWebView.setFlag(BrowserUnit.FLAG_NINJA);
            ninjaWebView.setAlbumCover(ViewUnit.capture(ninjaWebView, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
            ninjaWebView.setAlbumTitle(getString(R.string.album_untitled));

            int index = switcherContainer.indexOfChild(currentAlbumController.getAlbumView());
            currentAlbumController.deactivate();
            switcherContainer.removeView(currentAlbumController.getAlbumView());
            contentFrame.removeAllViews();

            switcherContainer.addView(ninjaWebView.getAlbumView(), index, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            contentFrame.addView(ninjaWebView, 0);
            BrowserContainer.set(ninjaWebView, index);
            currentAlbumController = ninjaWebView;
            ninjaWebView.activate();

            ninjaWebView.loadUrl(url);
            updateOmnibox();
        } else {
            NinjaToast.show(this, R.string.toast_load_error);
        }
    }

    @Override
    public synchronized void removeAlbum(AlbumController albumController) {
        if (currentAlbumController == null || BrowserContainer.size() <= 1) {
            switcherContainer.removeView(albumController.getAlbumView());
            BrowserContainer.remove(albumController);
            addAlbum(BrowserUnit.FLAG_HOME);
            return;
        }

        if (albumController != currentAlbumController) {
            switcherContainer.removeView(albumController.getAlbumView());
            BrowserContainer.remove(albumController);
            if (BrowserContainer.size() <= 1) {
                switcherPanel.expanded();
            }
        } else {
            switcherContainer.removeView(albumController.getAlbumView());
            int index = BrowserContainer.indexOf(albumController);
            BrowserContainer.remove(albumController);
            if (index >= BrowserContainer.size()) {
                index = BrowserContainer.size() - 1;
            }
            showAlbum(BrowserContainer.get(index), false);
        }
    }

    @Override
    public void updateBookmarks() {
        if (currentAlbumController == null || !(currentAlbumController instanceof NinjaWebView)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                omniboxBookmark.setImageDrawable(getResources().getDrawable(R.drawable.bookmark_selector_dark, null));
            } else {
                omniboxBookmark.setImageDrawable(getResources().getDrawable(R.drawable.bookmark_selector_dark));
            }
            return;
        }

        RecordAction action = new RecordAction(this);
        action.open(false);
        String url = ((NinjaWebView) currentAlbumController).getUrl();
        if (action.checkBookmark(url)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                omniboxBookmark.setImageDrawable(getResources().getDrawable(R.drawable.bookmark_selector_blue, null));
            } else {
                omniboxBookmark.setImageDrawable(getResources().getDrawable(R.drawable.bookmark_selector_blue));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                omniboxBookmark.setImageDrawable(getResources().getDrawable(R.drawable.bookmark_selector_dark, null));
            } else {
                omniboxBookmark.setImageDrawable(getResources().getDrawable(R.drawable.bookmark_selector_dark));
            }
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
            animator.setDuration(animTime);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.start();
        } else if (progress < progressBar.getProgress()) {
            ObjectAnimator animator = ObjectAnimator.ofInt(progressBar, "progress", 0, progress);
            animator.setDuration(animTime);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.start();
        }

        updateBookmarks();
        if (progress < BrowserUnit.PROGRESS_MAX) {
            updateRefresh(true);
            progressWrapper.setVisibility(View.VISIBLE);
        } else {
            updateRefresh(false);
            progressWrapper.setVisibility(View.GONE);
        }
    }

    private void updateRefresh(boolean running) {
        if (running) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                omniboxRefresh.setImageDrawable(getResources().getDrawable(R.drawable.cl_selector, null));
            } else {
                omniboxRefresh.setImageDrawable(getResources().getDrawable(R.drawable.cl_selector));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                omniboxRefresh.setImageDrawable(getResources().getDrawable(R.drawable.refresh_selector, null));
            } else {
                omniboxRefresh.setImageDrawable(getResources().getDrawable(R.drawable.refresh_selector));
            }
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
        }, animTime);
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
        list.add(getString(R.string.main_menu_copy));
        if (result != null && (result.getType() == WebView.HitTestResult.IMAGE_TYPE || result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE)) {
            list.add(getString(R.string.main_menu_save));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog, null, false);
        builder.setView(layout);

        ListView listView = (ListView) layout.findViewById(R.id.dialog_list);
        DialogAdapter adapter = new DialogAdapter(this, R.layout.dialog_item, list);
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
                String string = list.get(position);
                if (string.equals(getString(R.string.main_menu_new_tab))) {
                    // TODO: addAlbum effect?
                    addAlbum(getString(R.string.album_untitled), target, false, null);
                    NinjaToast.show(BrowserActivity.this, R.string.toast_new_tab_successful);
                } else if (string.equals(getString(R.string.main_menu_copy))) {
                    BrowserUnit.copyURL(BrowserActivity.this, target);
                } else if (string.equals(getString(R.string.main_menu_save))) {
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
        int index = mainView.indexOfChild(searchPanel);
        if (index < 0) {
            return;
        }

        hideSoftInput(searchBox);
        searchBox.setText("");
        switcherPanel.setSearch(false);
        mainView.removeView(searchPanel);
        mainView.addView(omnibox, index, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    private void showSearchPanel() {
        int index = mainView.indexOfChild(omnibox);
        if (index < 0) {
            return;
        }

        switcherPanel.setSearch(true);
        mainView.removeView(omnibox);
        mainView.addView(searchPanel, index, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        showSoftInput(searchBox);
    }

    private void showOverflow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog, null, false);
        builder.setView(layout);

        final String[] strings = getResources().getStringArray(R.array.main_overflow);
        final List<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(strings));
        if (currentAlbumController != null && currentAlbumController instanceof NinjaRelativeLayout) {
            list.remove(strings[0]);
            list.remove(strings[1]);
            list.remove(strings[2]);
        }

        RelativeLayout dialogHeader = (RelativeLayout) getLayoutInflater().inflate(R.layout.dialog_header, null, false);
        SeekBar seekBar = (SeekBar) dialogHeader.findViewById(R.id.dialog_header_seek_bar);

        ListView listView = (ListView) layout.findViewById(R.id.dialog_list);
        listView.addHeaderView(dialogHeader);

        DialogAdapter adapter = new DialogAdapter(this, R.layout.dialog_item, list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        final AlertDialog dialog = builder.create();
        dialog.show();

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int brightness = sp.getInt(getString(R.string.sp_brightness), BRIGHTNESS_BEGIN_DEFAULT);
        seekBar.setProgress(brightness);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                ViewUnit.setBrightness(BrowserActivity.this, progress);
                sp.edit().putInt(getString(R.string.sp_brightness), progress).commit();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String string = list.get(position - 1);
                if (string.equals(strings[0])) {
                    hideSoftInput(inputBox);
                    showSearchPanel();
                } else if (string.equals(strings[1])) {
                    final NinjaWebView ninjaWebView = (NinjaWebView) currentAlbumController;
                    new ScreenshotTask(BrowserActivity.this, ninjaWebView).execute();
                } else if (string.equals(strings[2])) {
                    if (!prepareRecord()) {
                        NinjaToast.show(BrowserActivity.this, R.string.toast_share_failed);
                    } else {
                        NinjaWebView ninjaWebView = (NinjaWebView) currentAlbumController;
                        IntentUnit.share(BrowserActivity.this, ninjaWebView.getTitle(), ninjaWebView.getUrl());
                    }
                } else if (string.equals(strings[3])) {
                    // TODO: intent to SettingActivity
                } else if (string.equals(strings[4])) {
                    finish();
                }
                dialog.hide();
                dialog.dismiss();
            }
        });
    }

    private boolean prepareRecord() {
        if (currentAlbumController == null || !(currentAlbumController instanceof NinjaWebView)) {
            return false;
        }

        NinjaWebView ninjaWebView = (NinjaWebView) currentAlbumController;
        String title = ninjaWebView.getTitle();
        String url = ninjaWebView.getUrl();
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

    // TODO
    @Override
    public void updateAutoComplete() {}
}
