package io.github.mthli.Berries.Activity;

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
import io.github.mthli.Berries.Browser.*;
import io.github.mthli.Berries.Database.Record;
import io.github.mthli.Berries.Database.RecordAction;
import io.github.mthli.Berries.R;
import io.github.mthli.Berries.Service.HolderService;
import io.github.mthli.Berries.Unit.BrowserUnit;
import io.github.mthli.Berries.Unit.IntentUnit;
import io.github.mthli.Berries.Unit.ViewUnit;
import io.github.mthli.Berries.View.DialogAdapter;
import io.github.mthli.Berries.View.ListAdapter;
import io.github.mthli.Berries.View.TabRelativeLayout;

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
    private TabController tabController;
    private int animTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser);

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
        when(getIntent(), false);
    }

    private void when(Intent intent, boolean n) {
        Intent toService = new Intent(this, HolderService.class);
        IntentUnit.setClear(false);
        stopService(toService);

        if (intent != null && intent.hasExtra(IntentUnit.PIN)) {
            pinTabs();
        } else if (intent != null && intent.hasExtra(IntentUnit.OPEN)) {
            pinTabs();
            newTab(R.string.browser_tab_untitled, intent.getStringExtra(IntentUnit.OPEN), false, true, null);
        } else if (n) {
            newTab(R.string.browser_tab_home, BrowserUnit.ABOUT_HOME, false, true, null);
        } else {
            pinTabs();
        }
    }

    @Override
    public void onPause() {
        inputBox.clearFocus();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        BrowserContainer.clear();
        Intent toService = new Intent(this, HolderService.class);
        IntentUnit.setClear(true);
        stopService(toService);
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
                if (controller instanceof BerryView) {
                    ((BerryView) controller).initPreferences();
                }
            }
        }

        if (data.getStringExtra(IntentUnit.GITHUB) != null) {
            newTab(getString(R.string.browser_tab_untitled), data.getStringExtra(IntentUnit.GITHUB), false, true, null);
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
                } else if (tabController instanceof BerryView) {
                    BerryView berryView = (BerryView) tabController;
                    if (berryView.canGoBack()) {
                        berryView.goBack();
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
                newTab(R.string.browser_tab_home, BrowserUnit.ABOUT_HOME, false, true, null);
            }
        });
        addTabButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                newTab(R.string.browser_tab_home, BrowserUnit.ABOUT_HOME, true, true, null);
                Toast.makeText(BrowserActivity.this, R.string.toast_incognito, Toast.LENGTH_SHORT).show();
                return true;
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
                String title = ((BerryView) tabController).getTitle();
                String url = ((BerryView) tabController).getUrl();
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

                if (tabController instanceof BerryView) {
                    BerryView berryView = (BerryView) tabController;
                    if (berryView.isLoadFinish()) {
                        berryView.reload();
                    } else {
                        berryView.stopLoading();
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

                boolean incognito = false;
                if (tabController instanceof BerryView) {
                    incognito = ((BerryView) tabController).isIncognito();
                }
                updateTab(query, incognito);
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
                if (tabController != null && tabController instanceof BerryView) {
                    ((BerryView) tabController).findAllAsync(s.toString());
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
                if (tabController instanceof BerryView) {
                    ((BerryView) tabController).findNext(false);
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
                if (tabController instanceof BerryView) {
                    ((BerryView) tabController).findNext(true);
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
        if (tabController == null || !(tabController instanceof BerryView)) {
            return false;
        }

        String title = ((BerryView) tabController).getTitle();
        String url = ((BerryView) tabController).getUrl();
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
                        if (tabController == null || !(tabController instanceof BerryView)) {
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
                            BerryView berryView = (BerryView) tabController;
                            IntentUnit.share(BrowserActivity.this, berryView.getTitle(), berryView.getUrl());
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

    private synchronized void newTab(int stringResId, String url, boolean incognito, boolean foreground, Message resultMsg) {
        newTab(getString(stringResId), url, incognito, foreground, resultMsg);
    }

    private synchronized void newTab(String title, final String url, boolean incognito, final boolean foreground, final Message resultMsg) {
        hideSoftInput(inputBox);
        if (foreground) {
            hideSearchPanel();
        }

        final BerryView berryView = new BerryView(this, incognito);
        berryView.setController(this);
        berryView.setFlag(BrowserUnit.FLAG_BERRY);

        berryView.setTabTitle(title);
        final View tabView = berryView.getTabView();
        tabView.setVisibility(View.INVISIBLE);

        if (tabController != null && (tabController instanceof BerryView) && resultMsg != null) {
            int index = BrowserContainer.indexOf(tabController) + 1;
            BrowserContainer.add(berryView, index);
            tabContainer.addView(tabView, index, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            BrowserContainer.add(berryView);
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
                    berryView.loadUrl(url);
                    berryView.deactivate();
                    tabScroll.smoothScrollTo(tabController.getTabView().getLeft(), 0);
                    return;
                }

                if (tabController != null) {
                    tabController.deactivate();
                }
                browserFrame.removeAllViews();
                browserFrame.addView(berryView);
                berryView.activate();
                tabScroll.smoothScrollTo(tabView.getLeft(), 0);
                tabController = berryView;
                updateOmniBox();

                if (url != null) {
                    berryView.loadUrl(url); // TODO: about:home
                } else if (resultMsg != null) {
                    WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                    transport.setWebView(berryView);
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
                updateTab(list.get(position).getURL(), false);
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

    private synchronized void pinTabs() {
        hideSoftInput(inputBox);
        hideSearchPanel();
        tabContainer.removeAllViews();
        browserFrame.removeAllViews();

        for (TabController controller : BrowserContainer.list()) {
            if (controller instanceof BerryView) {
                ((BerryView) controller).setController(this);
            } else if (controller instanceof TabRelativeLayout) {
                ((TabRelativeLayout) controller).setController(this);
            }
            tabContainer.addView(controller.getTabView(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            controller.deactivate();
            controller.getTabView().setVisibility(View.VISIBLE);
        }

        if (tabContainer.getChildCount() < 1) {
            return;
        }
        tabController = BrowserContainer.get(tabContainer.getChildCount() - 1);
        if (tabController instanceof BerryView) {
            browserFrame.addView((BerryView) tabController);
        } else if (tabController instanceof TabRelativeLayout) {
            browserFrame.addView((TabRelativeLayout) tabController);
        }
        tabController.activate();
        tabScroll.smoothScrollTo(tabController.getTabView().getLeft(), 0); // TODO: how to pin right way?
        updateOmniBox();
    }

    @Override
    public synchronized void showTab(BerryView berryView) {
        if (berryView == null || berryView.equals(tabController)) {
            return;
        }

        hideSoftInput(inputBox);
        hideSearchPanel();

        if (tabController != null) {
            tabController.deactivate();
        }
        browserFrame.removeAllViews();
        browserFrame.addView(berryView);
        berryView.activate();
        tabScroll.smoothScrollTo(berryView.getTabView().getLeft(), 0);
        tabController = berryView;
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

    private synchronized void updateTab(String url, boolean incognito) {
        if (tabController == null) {
            return;
        }

        if (tabController instanceof BerryView) {
            ((BerryView) tabController).loadUrl(url);
        } else if (tabController instanceof TabRelativeLayout) {
            BerryView berryView = new BerryView(this, incognito);
            berryView.setController(this);
            berryView.setFlag(BrowserUnit.FLAG_BERRY);
            berryView.setTabTitle(getString(R.string.browser_tab_untitled));
            berryView.getTabView().setVisibility(View.VISIBLE);

            int index = tabContainer.indexOfChild(tabController.getTabView());
            tabController.deactivate();
            tabContainer.removeView(tabController.getTabView());
            browserFrame.removeAllViews();

            tabContainer.addView(berryView.getTabView(), index, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            browserFrame.addView(berryView);
            berryView.activate();
            BrowserContainer.set(berryView, index);
            tabController = berryView;
            updateOmniBox();

            berryView.loadUrl(url);
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
                        newTab(getString(R.string.browser_tab_untitled), record.getURL(), false, false, null);
                        Toast.makeText(BrowserActivity.this, R.string.toast_new_tab_successful, Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        newTab(getString(R.string.browser_tab_untitled), record.getURL(), true, false, null);
                        Toast.makeText(BrowserActivity.this, R.string.toast_incognito_tab_successful, Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        BrowserUnit.copy(BrowserActivity.this, record.getURL());
                        break;
                    case 3:
                        IntentUnit.share(BrowserActivity.this, record.getTitle(), record.getURL());
                        break;
                    case 4:
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
                updateTab(url, false);
                hideSoftInput(inputBox);
                hideSearchPanel();
            }
        });
    }

    @Override
    public synchronized void updateBookmarks() {
        if (tabController == null || !(tabController instanceof BerryView)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                bookmarkButton.setImageDrawable(getResources().getDrawable(R.drawable.browser_bookmark_outline_button_selector, null));
            } else {
                bookmarkButton.setImageDrawable(getResources().getDrawable(R.drawable.browser_bookmark_outline_button_selector));
            }
            return;
        }

        RecordAction action = new RecordAction(this);
        action.open(false);
        String url = ((BerryView) tabController).getUrl();
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
        } else if (tabController instanceof BerryView) {
            BerryView currentView = (BerryView) tabController;
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

        if (tabController instanceof BerryView) {
            BerryView berryView = (BerryView) tabController;
            updateProgress(berryView.getProgress());
            updateBookmarks();
            if (berryView.getUrl() == null && berryView.getOriginalUrl() == null) {
                updateInputBox(null);
            } else if (berryView.getUrl() != null) {
                updateInputBox(berryView.getUrl());
            } else {
                updateInputBox(berryView.getOriginalUrl());
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
    public void onCreateView(WebView view, boolean incognito, final Message resultMsg) {
        if (resultMsg == null) {
            return;
        }
        newTab(R.string.browser_tab_untitled, null, incognito, true, resultMsg);
    }

    @Override
    public void onLongPress(String url) {
        WebView.HitTestResult result;
        if (!(tabController instanceof BerryView)) {
            return;
        }
        result = ((BerryView) tabController).getHitTestResult();

        final List<String> list = new ArrayList<String>();
        list.add(getString(R.string.berry_menu_new_tab));
        list.add(getString(R.string.berry_menu_incognito_tab));
        list.add(getString(R.string.berry_menu_copy_url));
        if (result != null && (result.getType() == WebView.HitTestResult.IMAGE_TYPE || result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE)) {
            list.add(getString(R.string.berry_menu_save_picture));
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
                    newTab(getString(R.string.browser_tab_untitled), target, false, false, null);
                    Toast.makeText(BrowserActivity.this, R.string.toast_new_tab_successful, Toast.LENGTH_SHORT).show();
                } else if (string.equals(getString(R.string.berry_menu_incognito_tab))) {
                    newTab(getString(R.string.browser_tab_untitled), target, true, false, null);
                    Toast.makeText(BrowserActivity.this, R.string.toast_incognito_tab_successful, Toast.LENGTH_SHORT).show();
                } else if (string.equals(getString(R.string.berry_menu_copy_url))) {
                    BrowserUnit.copy(BrowserActivity.this, target);
                } else if (string.equals(getString(R.string.berry_menu_save_picture))) {
                    BrowserUnit.download(BrowserActivity.this, target, target, BrowserUnit.MIME_TYPE_IMAGE);
                }
                dialog.hide();
                dialog.dismiss();
            }
        });
    }
}
