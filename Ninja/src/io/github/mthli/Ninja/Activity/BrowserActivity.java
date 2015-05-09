package io.github.mthli.Ninja.Activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import io.github.mthli.Ninja.Database.Record;
import io.github.mthli.Ninja.Database.RecordAction;
import io.github.mthli.Ninja.R;
import io.github.mthli.Ninja.Unit.BrowserUnit;
import io.github.mthli.Ninja.Unit.ViewUnit;
import io.github.mthli.Ninja.View.NinjaRelativeLayout;
import io.github.mthli.Ninja.View.NinjaWebView;
import io.github.mthli.Ninja.View.SwipeToDismissListener;
import io.github.mthli.Ninja.View.SwitcherPanel;

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
    public void updateBookmarks() {}

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
            if (switcherPanel.getStatus() != SwitcherPanel.Status.EXPANDED) {
                switcherPanel.expanded();
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
                    Toast.makeText(BrowserActivity.this, R.string.toast_input_empty, Toast.LENGTH_SHORT).show();
                    return false;
                }

                updateAlbum(query);
                hideSoftInput(inputBox);
                return false;
            }
        });
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switcherPanel.expanded();
            }
        }, animTime);
    }

    private synchronized void updateAlbum(String url) {
        if (currentAlbumController == null) {
            return;
        }

        if (currentAlbumController instanceof NinjaWebView) {
            ((NinjaWebView) currentAlbumController).loadUrl(url);
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
        } else {
            Toast.makeText(this, R.string.toast_load_error, Toast.LENGTH_SHORT).show();
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
    public void updateInputBox(String query) {
        inputBox.setText(query);
        inputBox.clearFocus();
    }

    // TODO
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
            progressWrapper.setVisibility(View.GONE);
        } else if (currentAlbumController instanceof NinjaWebView) {
            final NinjaWebView ninjaWebView = (NinjaWebView) currentAlbumController;
            if (ninjaWebView.isLoadFinish()) {
                RecordAction action = new RecordAction(this);
                action.open(true);
                action.addHistory(new Record(ninjaWebView.getTitle(), ninjaWebView.getUrl(), System.currentTimeMillis()));
                action.close();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressWrapper.setVisibility(View.GONE);
                        ninjaWebView.setAlbumCover(ViewUnit.capture(ninjaWebView, dimen144dp, dimen108dp));
                    }
                }, animTime);
            } else {
                progressWrapper.setVisibility(View.VISIBLE);
            }
        } else {
            progressWrapper.setVisibility(View.GONE);
        }
    }
}
