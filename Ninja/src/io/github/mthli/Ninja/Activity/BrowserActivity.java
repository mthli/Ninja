package io.github.mthli.Ninja.Activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import io.github.mthli.Ninja.Browser.*;
import io.github.mthli.Ninja.Database.Record;
import io.github.mthli.Ninja.Database.RecordAction;
import io.github.mthli.Ninja.R;
import io.github.mthli.Ninja.Service.HolderService;
import io.github.mthli.Ninja.Unit.BrowserUnit;
import io.github.mthli.Ninja.Unit.IntentUnit;
import io.github.mthli.Ninja.Unit.NotificationUnit;
import io.github.mthli.Ninja.Unit.ViewUnit;
import io.github.mthli.Ninja.View.DialogAdapter;
import io.github.mthli.Ninja.View.ListAdapter;
import io.github.mthli.Ninja.View.TabRelativeLayout;

import java.util.*;

public class BrowserActivity extends Activity implements BrowserController {
    private LinearLayout controlPanel;
    private HorizontalScrollView tabScroll;
    private LinearLayout tabContainer;
    private RelativeLayout omniboxLayout;
    private ImageButton addTabButton;
    private ImageButton bookmarkButton;
    private AutoCompleteTextView inputBox;
    private ImageButton refreshButton;
    private ImageButton overflowButton;
    private LinearLayout progressWrapper;
    private ProgressBar progressBar;

    private RelativeLayout searchPanel;
    private EditText searchBox;
    private ImageButton searchUpButton;
    private ImageButton searchDownButton;
    private ImageButton searchCancelButton;

    private FrameLayout browserFrame;
    private TabController tabController = null;

    private boolean create = true;
    private int animTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser);

        create = true;
        animTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        initControlPanel();
        initSearchPanel();
        browserFrame = (FrameLayout) findViewById(R.id.browser_frame);
        when(getIntent(), true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!create) {
            when(getIntent(), false);
        }
    }

    private void when(Intent intent, boolean n) {
        Intent toService = new Intent(this, HolderService.class);
        IntentUnit.setClear(false);
        stopService(toService);

        if (intent != null && intent.hasExtra(IntentUnit.PIN)) { // From Notification
            pinTabs(BrowserContainer.size() - 1, null);
        } else if (intent != null && intent.hasExtra(IntentUnit.OPEN)) { // From HolderActivity's menu
            String url = intent.getStringExtra(IntentUnit.OPEN);
            if (tabController != null) {
                pinTabs(BrowserContainer.indexOf(tabController), url);
            } else {
                pinTabs(BrowserContainer.size() - 1, url);
            }
        } else if (n) { // From this.onCreate()
            if (BrowserContainer.size() <= 0) {
                newTab(R.string.browser_tab_home, BrowserUnit.ABOUT_HOME, true, null);
            } else {
                pinTabs(BrowserContainer.size() - 1, null);
            }
        } else { // From onResume()
            if (tabController != null) {
                pinTabs(BrowserContainer.indexOf(tabController), null);
            } else {
                pinTabs(BrowserContainer.size() - 1, null);
            }
        }
    }

    @Override
    public void onPause() {
        Intent toService = new Intent(this, HolderService.class);
        IntentUnit.setClear(true);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != IntentUnit.REQUEST_SETTING && resultCode != IntentUnit.RESULT_SETTING || data == null) {
            return;
        }

        if (data.getBooleanExtra(IntentUnit.DATABASE_CHANGE, false)) {
            updateBookmarks();
            updateAutoComplete();
        }

        if (data.getBooleanExtra(IntentUnit.SHARED_PREFERENCE_CHANGE, false)) {
            for (TabController controller : BrowserContainer.list()) {
                if (controller instanceof NinjaView) {
                    ((NinjaView) controller).initPreferences();
                }
            }
        }

        if (data.getStringExtra(IntentUnit.GITHUB) != null) {
            newTab(getString(R.string.browser_tab_untitled), data.getStringExtra(IntentUnit.GITHUB), true, null);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation== Configuration.ORIENTATION_LANDSCAPE) {
        } else {}
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            hideSoftInput(inputBox);
            if (tabController == null) {
                finish();
            } else {
                if (tabController instanceof TabRelativeLayout) {
                    deleteTab();
                } else if (tabController instanceof NinjaView) {
                    NinjaView ninjaView = (NinjaView) tabController;
                    if (ninjaView.canGoBack()) {
                        ninjaView.goBack();
                    } else {
                        deleteTab();
                    }
                } else {
                    finish();
                }
            }
        }
        return true;
    }

    private void initControlPanel() {
        controlPanel = (LinearLayout) findViewById(R.id.browser_control_panel);
        ViewUnit.setElevation(this, controlPanel, 2);
        tabScroll = (HorizontalScrollView) findViewById(R.id.browser_tabs_scroll);
        tabContainer = (LinearLayout) findViewById(R.id.browser_tabs_container);
        omniboxLayout = (RelativeLayout) findViewById(R.id.browser_omnibox_layout);
        addTabButton = (ImageButton) findViewById(R.id.browser_add_tab_button);
        bookmarkButton = (ImageButton) findViewById(R.id.browser_bookmark_button);
        inputBox = (AutoCompleteTextView) findViewById(R.id.browser_input_box);
        refreshButton = (ImageButton) findViewById(R.id.browser_refresh_button);
        overflowButton = (ImageButton) findViewById(R.id.browser_overflow_button);
        progressWrapper = (LinearLayout) findViewById(R.id.browser_progress_wrapper);
        progressBar = (ProgressBar) findViewById(R.id.browser_progress_bar);

        addTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newTab(R.string.browser_tab_home, BrowserUnit.ABOUT_HOME, true, null);
            }
        });

        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!prepareRecord()) {
                    Toast.makeText(BrowserActivity.this, R.string.toast_add_bookmark_failed, Toast.LENGTH_SHORT).show();
                    return;
                }

                RecordAction action = new RecordAction(BrowserActivity.this);
                action.open(true);
                String title = ((NinjaView) tabController).getTitle();
                String url = ((NinjaView) tabController).getUrl();
                if (action.checkBookmark(url)) {
                    action.deleteBookmark(url);
                    Toast.makeText(BrowserActivity.this, R.string.toast_delete_bookmark_successful, Toast.LENGTH_SHORT).show();
                } else {
                    action.addBookmark(new Record(title, url, System.currentTimeMillis()));
                    Toast.makeText(BrowserActivity.this, R.string.toast_add_bookmark_successful, Toast.LENGTH_SHORT).show();
                }
                action.close();
                updateBookmarks();
                updateAutoComplete();
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tabController == null) {
                    Toast.makeText(BrowserActivity.this, R.string.toast_refresh_failed, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (tabController instanceof NinjaView) {
                    NinjaView ninjaView = (NinjaView) tabController;
                    if (ninjaView.isLoadFinish()) {
                        ninjaView.reload();
                    } else {
                        ninjaView.stopLoading();
                    }
                } else if (tabController instanceof TabRelativeLayout) {
                    updateProgress(BrowserUnit.PROGRESS_MIN);

                    RecordAction action = new RecordAction(BrowserActivity.this);
                    action.open(false);
                    List<Record> list = new ArrayList<Record>();
                    if (tabController.getFlag() == BrowserUnit.FLAG_BOOKMARKS) {
                        list = action.listBookmarks();
                    } else if (tabController.getFlag() == BrowserUnit.FLAG_HISTORY) {
                        list = action.listHistory();
                    }
                    action.close();

                    View view = (TabRelativeLayout) tabController;
                    ListView listView = (ListView) view.findViewById(R.id.list);
                    ListAdapter listAdapter = new ListAdapter(BrowserActivity.this, R.layout.list_item, list);
                    listView.setAdapter(listAdapter);
                    listAdapter.notifyDataSetChanged();

                    updateProgress(BrowserUnit.PROGRESS_MAX);
                } else {
                    Toast.makeText(BrowserActivity.this, R.string.toast_refresh_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });

        inputBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (tabController == null || !(actionId == EditorInfo.IME_ACTION_DONE)) {
                    return false;
                }

                String query = inputBox.getText().toString().trim();
                if (query.isEmpty()) {
                    Toast.makeText(BrowserActivity.this, R.string.toast_input_empty, Toast.LENGTH_SHORT).show();
                    return false;
                }

                updateTab(query);
                hideSoftInput(inputBox);
                hideSearchPanel();
                return false;
            }
        });
        updateBookmarks();
        updateAutoComplete();

        overflowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOverflow();
            }
        });
    }

    private void initSearchPanel() {
        searchPanel = (RelativeLayout) getLayoutInflater().inflate(R.layout.search_panel, null, false);
        searchBox = (EditText) searchPanel.findViewById(R.id.search_box);
        searchUpButton = (ImageButton) searchPanel.findViewById(R.id.search_panel_up_button);
        searchDownButton = (ImageButton) searchPanel.findViewById(R.id.search_panel_down_button);
        searchCancelButton = (ImageButton) searchPanel.findViewById(R.id.search_panel_cancel_button);

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (tabController != null && tabController instanceof NinjaView) {
                    ((NinjaView) tabController).findAllAsync(s.toString());
                }
            }
        });
        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (!(actionId == EditorInfo.IME_ACTION_DONE)) {
                    return false;
                }

                if (searchBox.getText().toString().isEmpty()) {
                    Toast.makeText(BrowserActivity.this, R.string.toast_input_empty, Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        searchUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchBox.getText().toString();
                if (query.isEmpty()) {
                    Toast.makeText(BrowserActivity.this, R.string.toast_input_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                hideSoftInput(searchBox);
                if (tabController instanceof NinjaView) {
                    ((NinjaView) tabController).findNext(false);
                }
            }
        });

        searchDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchBox.getText().toString();
                if (query.isEmpty()) {
                    Toast.makeText(BrowserActivity.this, R.string.toast_input_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                hideSoftInput(searchBox);
                if (tabController instanceof NinjaView) {
                    ((NinjaView) tabController).findNext(true);
                }
            }
        });

        searchCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSearchPanel();
            }
        });
    }

    private boolean prepareRecord() {
        if (tabController == null || !(tabController instanceof NinjaView)) {
            return false;
        }

        String title = ((NinjaView) tabController).getTitle();
        String url = ((NinjaView) tabController).getUrl();
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

    private void showOverflow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog, null, false);
        builder.setView(linearLayout);

        String[] strings = getResources().getStringArray(R.array.browser_overflow);
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
                        newTab(BrowserUnit.FLAG_BOOKMARKS);
                        break;
                    case 1:
                        newTab(BrowserUnit.FLAG_HISTORY);
                        break;
                    case 2:
                        if (tabController == null || !(tabController instanceof NinjaView)) {
                            Toast.makeText(BrowserActivity.this, R.string.toast_search_failed, Toast.LENGTH_SHORT).show();
                        } else {
                            hideSoftInput(inputBox);
                            showSearchPanel();
                        }
                        break;
                    case 3:
                        if (!prepareRecord()) {
                            Toast.makeText(BrowserActivity.this, R.string.toast_share_failed, Toast.LENGTH_SHORT).show();
                        } else {
                            NinjaView ninjaView = (NinjaView) tabController;
                            IntentUnit.share(BrowserActivity.this, ninjaView.getTitle(), ninjaView.getUrl());
                        }
                        break;
                    case 4:
                        Intent intent = new Intent(BrowserActivity.this, SettingActivity.class);
                        startActivityForResult(intent, IntentUnit.REQUEST_SETTING);
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

    private synchronized void newTab(int stringResId, String url, boolean foreground, Message resultMsg) {
        newTab(getString(stringResId), url, foreground, resultMsg);
    }

    private synchronized void newTab(String title, final String url, final boolean foreground, final Message resultMsg) {
        hideSoftInput(inputBox);
        if (foreground) {
            hideSearchPanel();
        }

        final NinjaView ninjaView = new NinjaView(this);
        ninjaView.setController(this);
        ninjaView.setFlag(BrowserUnit.FLAG_BERRY);

        ninjaView.setTabTitle(title);
        final View tabView = ninjaView.getTabView();
        tabView.setVisibility(View.INVISIBLE);

        if (tabController != null && (tabController instanceof NinjaView) && resultMsg != null) {
            int index = BrowserContainer.indexOf(tabController) + 1;
            BrowserContainer.add(ninjaView, index);
            tabContainer.addView(tabView, index, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            BrowserContainer.add(ninjaView);
            tabContainer.addView(tabView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        }

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
                tabView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!foreground) {
                    ninjaView.loadUrl(url);
                    ninjaView.deactivate();
                    tabScroll.smoothScrollTo(tabController.getTabView().getLeft(), 0);
                    return;
                }

                if (tabController != null) {
                    tabController.deactivate();
                }
                browserFrame.removeAllViews();
                browserFrame.addView(ninjaView);
                ninjaView.activate();
                tabScroll.smoothScrollTo(tabView.getLeft(), 0);
                tabController = ninjaView;
                updateOmniBox();

                if (url != null) {
                    ninjaView.loadUrl(url); // TODO: about:home
                } else if (resultMsg != null) {
                    WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                    transport.setWebView(ninjaView);
                    resultMsg.sendToTarget();
                }
            }
        });
        tabView.startAnimation(animation);
    }

    private synchronized void newTab(int tag) {
        hideSoftInput(inputBox);
        hideSearchPanel();

        for (TabController controller : BrowserContainer.list()) {
            if (controller.getFlag() == tag) {
                showTab((TabRelativeLayout) controller);
                return;
            }
        }

        final TabRelativeLayout listLayout = (TabRelativeLayout) getLayoutInflater().inflate(R.layout.list, null, false);
        listLayout.setController(this);
        if (tag == BrowserUnit.FLAG_BOOKMARKS) {
            listLayout.setFlag(BrowserUnit.FLAG_BOOKMARKS);
        } else if (tag == BrowserUnit.FLAG_HISTORY) {
            listLayout.setFlag(BrowserUnit.FLAG_HISTORY);
        }

        RecordAction action = new RecordAction(this);
        action.open(false);
        final List<Record> list;
        if (tag == BrowserUnit.FLAG_BOOKMARKS) {
            list = action.listBookmarks();
        } else if (tag == BrowserUnit.FLAG_HISTORY) {
            list = action.listHistory();
        } else {
            list = new ArrayList<Record>();
        }
        action.close();

        ListView listView = (ListView) listLayout.findViewById(R.id.list);
        TextView textView = (TextView) listLayout.findViewById(R.id.list_empty);
        listView.setEmptyView(textView);

        final ListAdapter adapter = new ListAdapter(this, R.layout.list_item, list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        final View tabView = listLayout.getTabView();
        if (tag == BrowserUnit.FLAG_BOOKMARKS) {
            listLayout.setTabTitle(getString(R.string.browser_tab_bookmarks));
        } else if (tag == BrowserUnit.FLAG_HISTORY) {
            listLayout.setTabTitle(getString(R.string.browser_tab_history));
        }
        tabView.setVisibility(View.INVISIBLE);

        browserFrame.removeAllViews();
        BrowserContainer.add(listLayout);
        tabContainer.addView(tabView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationStart(Animation animation) {
                tabView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (tabController != null) {
                    tabController.deactivate();
                }
                browserFrame.removeAllViews();
                browserFrame.addView(listLayout);
                listLayout.activate();
                tabScroll.smoothScrollTo(tabView.getLeft(), 0);
                tabController = listLayout;
                updateOmniBox();
            }
        });
        tabView.startAnimation(animation);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateTab(list.get(position).getURL());
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showListMenu(adapter, list, position);
                return true;
            }
        });
    }

    private synchronized void pinTabs(int index, final String url) {
        hideSoftInput(inputBox);
        hideSearchPanel();
        tabContainer.removeAllViews();
        browserFrame.removeAllViews();

        for (TabController controller : BrowserContainer.list()) {
            if (controller instanceof NinjaView) {
                ((NinjaView) controller).setController(this);
            } else if (controller instanceof TabRelativeLayout) {
                ((TabRelativeLayout) controller).setController(this);
            }
            tabContainer.addView(controller.getTabView(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            controller.deactivate();
            controller.getTabView().setVisibility(View.VISIBLE);
        }

        if (BrowserContainer.size() < 1 && url == null) {
            return;
        } else if (BrowserContainer.size() < 1 && url != null) {
            newTab(R.string.browser_tab_untitled, url, true, null);
        } else if (BrowserContainer.size() >= 1 && url == null) {
            tabController = BrowserContainer.get(index);
            if (tabController instanceof NinjaView) {
                browserFrame.addView((NinjaView) tabController);
            } else if (tabController instanceof TabRelativeLayout) {
                browserFrame.addView((TabRelativeLayout) tabController);
            }
            tabController.activate();
            tabScroll.post(new Runnable() {
                @Override
                public void run() {
                    tabScroll.scrollTo(tabController.getTabView().getLeft(), 0);
                    updateOmniBox();
                }
            });
        } else {
            tabController = BrowserContainer.get(index);
            if (tabController instanceof NinjaView) {
                browserFrame.addView((NinjaView) tabController);
            } else if (tabController instanceof TabRelativeLayout) {
                browserFrame.addView((TabRelativeLayout) tabController);
            }
            tabController.activate();
            tabScroll.post(new Runnable() {
                @Override
                public void run() {
                    tabScroll.scrollTo(tabController.getTabView().getLeft(), 0);
                    updateOmniBox();
                    newTab(R.string.browser_tab_untitled, url, true, null);
                }
            });
        }
    }

    @Override
    public synchronized void showTab(NinjaView ninjaView) {
        if (ninjaView == null || ninjaView.equals(tabController)) {
            return;
        }

        hideSoftInput(inputBox);
        hideSearchPanel();

        if (tabController != null) {
            tabController.deactivate();
        }
        browserFrame.removeAllViews();
        browserFrame.addView(ninjaView);
        ninjaView.activate();
        tabScroll.smoothScrollTo(ninjaView.getTabView().getLeft(), 0);
        tabController = ninjaView;
        updateOmniBox();
    }

    @Override
    public synchronized void showTab(TabRelativeLayout tabRelativeLayout) {
        if (tabRelativeLayout == null || tabRelativeLayout.equals(tabController)) {
            return;
        }

        hideSoftInput(inputBox);
        hideSearchPanel();

        if (tabController != null) {
            tabController.deactivate();
        }
        browserFrame.removeAllViews();
        browserFrame.addView(tabRelativeLayout);
        tabRelativeLayout.activate();
        tabScroll.smoothScrollTo(tabRelativeLayout.getTabView().getLeft(), 0);
        tabController = tabRelativeLayout;
        updateOmniBox();
    }

    private synchronized void updateTab(String url) {
        if (tabController == null) {
            return;
        }

        if (tabController instanceof NinjaView) {
            ((NinjaView) tabController).loadUrl(url);
        } else if (tabController instanceof TabRelativeLayout) {
            NinjaView ninjaView = new NinjaView(this);
            ninjaView.setController(this);
            ninjaView.setFlag(BrowserUnit.FLAG_BERRY);
            ninjaView.setTabTitle(getString(R.string.browser_tab_untitled));
            ninjaView.getTabView().setVisibility(View.VISIBLE);

            int index = tabContainer.indexOfChild(tabController.getTabView());
            tabController.deactivate();
            tabContainer.removeView(tabController.getTabView());
            browserFrame.removeAllViews();

            tabContainer.addView(ninjaView.getTabView(), index, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            browserFrame.addView(ninjaView);
            ninjaView.activate();
            BrowserContainer.set(ninjaView, index);
            tabController = ninjaView;
            updateOmniBox();

            ninjaView.loadUrl(url);
        } else {
            Toast.makeText(this, R.string.toast_load_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void deleteTab() {
        if (tabController == null || BrowserContainer.size() <= 1) {
            finish();
            return;
        }

        hideSoftInput(inputBox);
        hideSearchPanel();

        final View tabView = tabController.getTabView();
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_down);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tabView.setVisibility(View.GONE);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        tabContainer.removeView(tabView);
                    }
                });

                if (tabController != null) {
                    tabController.deactivate();
                }
                browserFrame.removeAllViews();
                updateProgress(BrowserUnit.PROGRESS_MAX);

                int index = BrowserContainer.indexOf(tabController);
                BrowserContainer.remove(tabController);
                if (index >= BrowserContainer.size()) {
                    index = BrowserContainer.size() - 1;
                }

                browserFrame.addView((View) BrowserContainer.get(index));
                tabController = BrowserContainer.get(index);
                tabController.activate();
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        tabScroll.smoothScrollTo(tabController.getTabView().getLeft(), 0);
                        updateOmniBox();
                    }
                });
            }
        });
        tabView.startAnimation(animation);
    }

    private void showListMenu(final ListAdapter listAdapter, final List<Record> recordList, final int location) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog, null, false);
        builder.setView(linearLayout);

        String[] strings = getResources().getStringArray(R.array.list_menu);
        List<String> list = new ArrayList<String>();
        list.addAll(Arrays.asList(strings));

        ListView listView = (ListView) linearLayout.findViewById(R.id.dialog_listview);
        DialogAdapter adapter = new DialogAdapter(this, R.layout.dialog_item, list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        final AlertDialog dialog = builder.create();
        dialog.show();

        final Record record = recordList.get(location);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        newTab(getString(R.string.browser_tab_untitled), record.getURL(), false, null);
                        Toast.makeText(BrowserActivity.this, R.string.toast_new_tab_successful, Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        BrowserUnit.copy(BrowserActivity.this, record.getURL());
                        break;
                    case 2:
                        IntentUnit.share(BrowserActivity.this, record.getTitle(), record.getURL());
                        break;
                    case 3:
                        if (tabController == null || !(tabController instanceof TabRelativeLayout)) {
                            break;
                        }
                        RecordAction action = new RecordAction(BrowserActivity.this);
                        action.open(true);
                        if (tabController.getFlag() == BrowserUnit.FLAG_BOOKMARKS) {
                            action.deleteBookmark(record);
                        } else if (tabController.getFlag() == BrowserUnit.FLAG_HISTORY) {
                            action.deleteHistory(record);
                        }
                        action.close();
                        recordList.remove(location);
                        listAdapter.notifyDataSetChanged();
                        updateBookmarks();
                        updateAutoComplete();
                        break;
                    default:
                        break;
                }
                dialog.hide();
                dialog.dismiss();
            }
        });
    }

    private synchronized void updateAutoComplete() {
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
                updateTab(url);
                hideSoftInput(inputBox);
                hideSearchPanel();
            }
        });
    }

    @Override
    public synchronized void updateBookmarks() {
        if (tabController == null || !(tabController instanceof NinjaView)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                bookmarkButton.setImageDrawable(getResources().getDrawable(R.drawable.browser_bookmark_outline_button_selector, null));
            } else {
                bookmarkButton.setImageDrawable(getResources().getDrawable(R.drawable.browser_bookmark_outline_button_selector));
            }
            return;
        }

        RecordAction action = new RecordAction(this);
        action.open(false);
        String url = ((NinjaView) tabController).getUrl();
        if (action.checkBookmark(url)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                bookmarkButton.setImageDrawable(getResources().getDrawable(R.drawable.browser_bookmark_full_button_selector, null));
            } else {
                bookmarkButton.setImageDrawable(getResources().getDrawable(R.drawable.browser_bookmark_full_button_selector));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                bookmarkButton.setImageDrawable(getResources().getDrawable(R.drawable.browser_bookmark_outline_button_selector, null));
            } else {
                bookmarkButton.setImageDrawable(getResources().getDrawable(R.drawable.browser_bookmark_outline_button_selector));
            }
        }
        action.close();
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

        if (tabController instanceof TabRelativeLayout) {
            if (progress < BrowserUnit.PROGRESS_MAX) {
                updateRefreshButton(true);
                progressWrapper.setVisibility(View.VISIBLE);
            } else {
                updateRefreshButton(false);
                progressWrapper.setVisibility(View.GONE);
                updateBookmarks();
                updateAutoComplete();
            }
        } else if (tabController instanceof NinjaView) {
            NinjaView currentView = (NinjaView) tabController;
            if (currentView.isLoadFinish()) {
                RecordAction action = new RecordAction(this);
                action.open(true);
                action.addHistory(new Record(currentView.getTitle(), currentView.getUrl(), System.currentTimeMillis()));
                action.close();
                updateBookmarks();
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
        } else {
            updateRefreshButton(false);
            progressWrapper.setVisibility(View.GONE);
        }
    }

    private void updateOmniBox() {
        if (tabController == null) {
            return;
        }

        if (tabController instanceof NinjaView) {
            NinjaView ninjaView = (NinjaView) tabController;
            updateProgress(ninjaView.getProgress());
            updateBookmarks();
            if (ninjaView.getUrl() == null && ninjaView.getOriginalUrl() == null) {
                updateInputBox(null);
            } else if (ninjaView.getUrl() != null) {
                updateInputBox(ninjaView.getUrl());
            } else {
                updateInputBox(ninjaView.getOriginalUrl());
            }
        } else if (tabController instanceof TabRelativeLayout) {
            updateProgress(BrowserUnit.PROGRESS_MAX);
            updateBookmarks();
            updateInputBox(null);
        }
    }

    private void updateRefreshButton(boolean running) {
        if (running) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                refreshButton.setImageDrawable(getResources().getDrawable(R.drawable.cl_button_dark_selector, null));
            } else {
                refreshButton.setImageDrawable(getResources().getDrawable(R.drawable.cl_button_dark_selector));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                refreshButton.setImageDrawable(getResources().getDrawable(R.drawable.browser_refresh_button_selector, null));
            } else {
                refreshButton.setImageDrawable(getResources().getDrawable(R.drawable.browser_refresh_button_selector));
            }
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

    private void showSearchPanel() {
        int index = controlPanel.indexOfChild(omniboxLayout);
        if (index < 0) {
            return;
        }

        controlPanel.removeView(omniboxLayout);
        controlPanel.addView(searchPanel, index, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        showSoftInput(searchBox);
    }

    private void hideSearchPanel() {
        int index = controlPanel.indexOfChild(searchPanel);
        if (index < 0) {
            return;
        }

        hideSoftInput(searchBox);
        searchBox.setText("");

        controlPanel.removeView(searchPanel);
        controlPanel.addView(omniboxLayout, index, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onCreateView(WebView view, final Message resultMsg) {
        if (resultMsg == null) {
            return;
        }
        newTab(R.string.browser_tab_untitled, null, true, resultMsg);
    }

    @Override
    public void onLongPress(String url) {
        WebView.HitTestResult result;
        if (!(tabController instanceof NinjaView)) {
            return;
        }
        result = ((NinjaView) tabController).getHitTestResult();

        final List<String> list = new ArrayList<String>();
        list.add(getString(R.string.berry_menu_new_tab));
        list.add(getString(R.string.berry_menu_copy_url));
        if (result != null && (result.getType() == WebView.HitTestResult.IMAGE_TYPE || result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE)) {
            list.add(getString(R.string.berry_menu_save));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog, null, false);
        builder.setView(linearLayout);

        ListView listView = (ListView) linearLayout.findViewById(R.id.dialog_listview);
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
                if (string.equals(getString(R.string.berry_menu_new_tab))) {
                    newTab(getString(R.string.browser_tab_untitled), target, false, null);
                    Toast.makeText(BrowserActivity.this, R.string.toast_new_tab_successful, Toast.LENGTH_SHORT).show();
                } else if (string.equals(getString(R.string.berry_menu_copy_url))) {
                    BrowserUnit.copy(BrowserActivity.this, target);
                } else if (string.equals(getString(R.string.berry_menu_save))) {
                    BrowserUnit.download(BrowserActivity.this, target, target, BrowserUnit.MIME_TYPE_IMAGE);
                }
                dialog.hide();
                dialog.dismiss();
            }
        });
    }
}
