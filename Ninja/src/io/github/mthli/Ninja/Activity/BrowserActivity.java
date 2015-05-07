package io.github.mthli.Ninja.Activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import io.github.mthli.Ninja.R;
import io.github.mthli.Ninja.View.SwitcherPanel;

public class BrowserActivity extends Activity {
    private SwitcherPanel switcherPanel;

    private HorizontalScrollView swictherScroller;
    private LinearLayout switcherContainer;
    private ImageButton addButton;

    private RelativeLayout ominibox;
    private AutoCompleteTextView inputBox;
    private ImageButton bookmarkButton;
    private ImageButton refreshButton;
    private ImageButton switcherButton;
    private ImageButton overflowButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initSwitcherView();
        initMainView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (switcherPanel.getStatus() != SwitcherPanel.Status.EXPANDED) {
            switcherPanel.expanded();
        }

        if (newConfig.orientation== Configuration.ORIENTATION_LANDSCAPE) {
            switcherPanel.setCoverHeight(SwitcherPanel.COVER_HEIGHT_LANDSCAPE_DEFAULT);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            switcherPanel.setCoverHeight(SwitcherPanel.COVER_HEIGHT_PORTRAIT_DEFAULT);
        }
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
        swictherScroller = (HorizontalScrollView) findViewById(R.id.switcher_view_scoller);
        switcherContainer = (LinearLayout) findViewById(R.id.switcher_view_container);
        addButton = (ImageButton) findViewById(R.id.switcher_view_add);
    }

    private void initMainView() {
        switcherPanel = (SwitcherPanel) findViewById(R.id.switcher_panel);
        ominibox = (RelativeLayout) findViewById(R.id.main_view_omnibox);
        inputBox = (AutoCompleteTextView) findViewById(R.id.main_view_omnibox_input);
        bookmarkButton = (ImageButton) findViewById(R.id.main_view_omnibox_bookmark);
        refreshButton = (ImageButton) findViewById(R.id.main_view_omnibox_refresh);
        switcherButton = (ImageButton) findViewById(R.id.main_view_omnibox_switcher);
        overflowButton = (ImageButton) findViewById(R.id.main_view_omnibox_overflow);

        switcherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switcherPanel.collapsed();
            }
        });
    }
}
