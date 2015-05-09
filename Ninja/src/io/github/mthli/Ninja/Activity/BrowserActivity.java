package io.github.mthli.Ninja.Activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
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
import io.github.mthli.Ninja.Database.Record;
import io.github.mthli.Ninja.Database.RecordAction;
import io.github.mthli.Ninja.R;
import io.github.mthli.Ninja.Unit.BrowserUnit;
import io.github.mthli.Ninja.Unit.ViewUnit;
import io.github.mthli.Ninja.View.*;

import java.util.List;

public class BrowserActivity extends Activity implements BrowserController {
    private SwitcherPanel switcherPanel;

    private FrameLayout switcherHeader;
    private HorizontalScrollView swictherScroller;
    private LinearLayout switcherContainer;
    private ImageButton addButton;
    private float dimen144dp;
    private float dimen108dp;
    private float dimen48dp;

    private RelativeLayout ominibox;
    private AutoCompleteTextView inputBox;
    private ImageButton bookmarkButton;
    private ImageButton refreshButton;
    private ImageButton overflowButton;
    private LinearLayout progressWrapper;
    private ProgressBar progressBar;
    private FrameLayout contentFrame;

    private RelativeLayout searchPanel;
    private EditText searchBox;
    private ImageButton searchUp;
    private ImageButton searchDown;
    private ImageButton searchCancel;

    private AlbumController currentAlbumController = null;
    private AlbumController showAlbumController = null;
    private boolean show = false;

    private boolean create = true;
    private int animTime = 0;

    @Override
    public void onCreateView(WebView view, Message resultMsg) {}

    @Override
    public void onLongPress(String url) {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

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

        initSwitcherView();
        initMainView();
        initSearchPanel();
        addAlbum(); // TODO
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
        int windowHeight = ViewUnit.getWindowHeight(this);
        int statusBarHeight = ViewUnit.getStatusBarHeight(this);
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
                    updateAlbum(BrowserUnit.FLAG_HOME);
                }
            } else if (currentAlbumController instanceof NinjaRelativeLayout) {
                switch (currentAlbumController.getFlag()) {
                    case BrowserUnit.FLAG_BOOKMARKS:
                        updateAlbum(BrowserUnit.FLAG_HOME);
                        break;
                    case BrowserUnit.FLAG_HISTORY:
                        updateAlbum(BrowserUnit.FLAG_HOME);
                        break;
                    case BrowserUnit.FLAG_HOME:
                        NinjaToast.show(this, R.string.toast_last_page);
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
        switcherHeader = (FrameLayout) findViewById(R.id.switcher_header);
        swictherScroller = (HorizontalScrollView) findViewById(R.id.switcher_scoller);
        switcherContainer = (LinearLayout) findViewById(R.id.switcher_container);
        addButton = (ImageButton) findViewById(R.id.switcher_add);
        dimen144dp = getResources().getDimensionPixelSize(R.dimen.layout_width_144dp);
        dimen108dp = getResources().getDimensionPixelSize(R.dimen.layout_height_108dp);
        dimen48dp = getResources().getDimensionPixelOffset(R.dimen.layout_height_48dp);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAlbum();
            }
        });
    }

    private void initMainView() {
        ominibox = (RelativeLayout) findViewById(R.id.main_omnibox);
        inputBox = (AutoCompleteTextView) findViewById(R.id.main_omnibox_input);
        bookmarkButton = (ImageButton) findViewById(R.id.main_omnibox_bookmark);
        refreshButton = (ImageButton) findViewById(R.id.main_omnibox_refresh);
        overflowButton = (ImageButton) findViewById(R.id.main_omnibox_overflow);
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
    }

    private void initSearchPanel() {
        searchPanel = (RelativeLayout) getLayoutInflater().inflate(R.layout.search, null, false);
        searchBox = (EditText) searchPanel.findViewById(R.id.search_box);
        searchUp = (ImageButton) searchPanel.findViewById(R.id.search_up);
        searchDown = (ImageButton) searchPanel.findViewById(R.id.search_down);
        searchCancel = (ImageButton) searchPanel.findViewById(R.id.search_cancel);
    }

    private synchronized void addAlbum() {
        final NinjaRelativeLayout homeLayout = (NinjaRelativeLayout) getLayoutInflater().inflate(R.layout.home, null, false);
        homeLayout.setBrowserController(this);
        homeLayout.setFlag(BrowserUnit.FLAG_HOME);
        homeLayout.setAlbumCover(ViewUnit.capture(homeLayout, dimen144dp, dimen108dp));
        homeLayout.setAlbumTitle(getString(R.string.album_title_home));

        final View albumView = homeLayout.getAlbumView();
        albumView.setVisibility(View.INVISIBLE);

        BrowserContainer.add(homeLayout);
        switcherContainer.addView(albumView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationStart(Animation animation) {
                albumView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showAlbum(homeLayout);
            }
        });
        albumView.startAnimation(animation);
    }

    private synchronized void addAlbum(String title, final String url, final boolean foreground, final Message resultMsg) {
        final NinjaWebView ninjaWebView = new NinjaWebView(this);
        ninjaWebView.setBrowserController(this);
        ninjaWebView.setFlag(BrowserUnit.FLAG_NINJA);
        ninjaWebView.setAlbumCover(ViewUnit.capture(ninjaWebView, dimen144dp, dimen108dp));
        ninjaWebView.setAlbumTitle(title);

        final View albumView = ninjaWebView.getAlbumView();
        albumView.setVisibility(View.INVISIBLE);

        BrowserContainer.add(ninjaWebView);
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
                if (!foreground) {
                    ninjaWebView.loadUrl(url);
                    ninjaWebView.deactivate();
                    if (currentAlbumController != null) {
                        swictherScroller.smoothScrollTo(currentAlbumController.getAlbumView().getLeft(), 0);
                    }
                    return;
                }

                showAlbum(ninjaWebView);

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
    public synchronized void showAlbum(AlbumController albumController) {
        if (albumController == null || albumController == currentAlbumController) {
            switcherPanel.expanded();
            return;
        }

        if (currentAlbumController != null) {
            currentAlbumController.deactivate();
        }
        contentFrame.removeAllViews();
        contentFrame.addView((View) albumController);
        currentAlbumController = albumController;
        currentAlbumController.activate();
        swictherScroller.smoothScrollTo(currentAlbumController.getAlbumView().getLeft(), 0);
        updateOmnibox();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switcherPanel.expanded();
            }
        }, animTime);
    }

    private synchronized void updateAlbum(int flag) {
        if (currentAlbumController == null) {
            return;
        }

        AlbumController holder;
        if (flag == BrowserUnit.FLAG_BOOKMARKS) {
            NinjaRelativeLayout bookmarksLayout = (NinjaRelativeLayout) getLayoutInflater().inflate(R.layout.list, null, false);
            bookmarksLayout.setBrowserController(this);
            bookmarksLayout.setFlag(BrowserUnit.FLAG_BOOKMARKS);
            bookmarksLayout.setAlbumCover(ViewUnit.capture(bookmarksLayout, dimen144dp, dimen108dp));
            bookmarksLayout.setAlbumTitle(getString(R.string.album_title_bookmarks));

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

            holder = bookmarksLayout;
            holder.setAlbumCover(ViewUnit.capture(bookmarksLayout, dimen144dp, dimen108dp));

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
            historyLayout.setAlbumCover(ViewUnit.capture(historyLayout, dimen144dp, dimen108dp));
            historyLayout.setAlbumTitle(getString(R.string.album_title_history));

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

            holder = historyLayout;
            holder.setAlbumCover(ViewUnit.capture(historyLayout, dimen144dp, dimen108dp));

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
            homeLayout.setAlbumCover(ViewUnit.capture(homeLayout, dimen144dp, dimen108dp));
            homeLayout.setAlbumTitle(getString(R.string.album_title_home));
            holder = homeLayout;
        } else {
            return;
        }

        int index = switcherContainer.indexOfChild(currentAlbumController.getAlbumView());
        currentAlbumController.deactivate();
        switcherContainer.removeView(currentAlbumController.getAlbumView());
        contentFrame.removeAllViews();

        switcherContainer.addView(holder.getAlbumView(), index, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        contentFrame.addView((View) holder);
        BrowserContainer.set(holder, index);
        currentAlbumController = holder;
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
            ninjaWebView.setAlbumCover(ViewUnit.capture(ninjaWebView, dimen144dp, dimen108dp));
            ninjaWebView.setAlbumTitle(getString(R.string.album_untitled));

            int index = switcherContainer.indexOfChild(currentAlbumController.getAlbumView());
            currentAlbumController.deactivate();
            switcherContainer.removeView(currentAlbumController.getAlbumView());
            contentFrame.removeAllViews();

            switcherContainer.addView(ninjaWebView.getAlbumView(), index, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            contentFrame.addView(ninjaWebView);
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
            addAlbum();
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
            showAlbum(BrowserContainer.get(index));
        }
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

    @Override
    public void updateBookmarks() {
        if (currentAlbumController == null || !(currentAlbumController instanceof NinjaWebView)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                bookmarkButton.setImageDrawable(getResources().getDrawable(R.drawable.bookmark_outline_selector, null));
            } else {
                bookmarkButton.setImageDrawable(getResources().getDrawable(R.drawable.bookmark_outline_selector));
            }
            return;
        }

        RecordAction action = new RecordAction(this);
        action.open(false);
        String url = ((NinjaWebView) currentAlbumController).getUrl();
        if (action.checkBookmark(url)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                bookmarkButton.setImageDrawable(getResources().getDrawable(R.drawable.bookmark_full_selector, null));
            } else {
                bookmarkButton.setImageDrawable(getResources().getDrawable(R.drawable.bookmark_full_selector));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                bookmarkButton.setImageDrawable(getResources().getDrawable(R.drawable.bookmark_outline_selector, null));
            } else {
                bookmarkButton.setImageDrawable(getResources().getDrawable(R.drawable.bookmark_outline_selector));
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

        if (currentAlbumController instanceof NinjaRelativeLayout) {
            if (progress < BrowserUnit.PROGRESS_MAX) {
                updateRefresh(true);
                progressWrapper.setVisibility(View.VISIBLE);
            } else {
                updateRefresh(false);
                progressWrapper.setVisibility(View.GONE);
                updateBookmarks();
                updateAutoComplete();
            }
        } else if (currentAlbumController instanceof NinjaWebView) {
            final NinjaWebView ninjaWebView = (NinjaWebView) currentAlbumController;
            if (ninjaWebView.isLoadFinish()) {
                RecordAction action = new RecordAction(this);
                action.open(true);
                action.addHistory(new Record(ninjaWebView.getTitle(), ninjaWebView.getUrl(), System.currentTimeMillis()));
                action.close();
                updateBookmarks();
                updateAutoComplete();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateRefresh(false);
                        progressWrapper.setVisibility(View.GONE);
                        ninjaWebView.setAlbumCover(ViewUnit.capture(ninjaWebView, dimen144dp, dimen108dp));
                    }
                }, animTime);
            } else {
                updateRefresh(true);
                progressWrapper.setVisibility(View.VISIBLE);
            }
        } else {
            updateRefresh(false);
            progressWrapper.setVisibility(View.GONE);
        }
    }

    private void updateRefresh(boolean running) {
        if (running) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                refreshButton.setImageDrawable(getResources().getDrawable(R.drawable.cl_selector, null));
            } else {
                refreshButton.setImageDrawable(getResources().getDrawable(R.drawable.cl_selector));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                refreshButton.setImageDrawable(getResources().getDrawable(R.drawable.refresh_selector, null));
            } else {
                refreshButton.setImageDrawable(getResources().getDrawable(R.drawable.refresh_selector));
            }
        }
    }

    // TODO
    private void updateAutoComplete() {}

}
