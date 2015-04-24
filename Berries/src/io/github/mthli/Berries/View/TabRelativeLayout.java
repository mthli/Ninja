package io.github.mthli.Berries.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import io.github.mthli.Berries.Browser.BrowserController;
import io.github.mthli.Berries.Browser.TabController;
import io.github.mthli.Berries.R;

public class TabRelativeLayout extends RelativeLayout implements TabController {
    private Context context;
    private int flag = 0;

    private BrowserController controller;
    public void setController(BrowserController controller) {
        this.controller = controller;
    }

    private View tabView;
    private TextView title;
    private ImageButton closeButton;

    public TabRelativeLayout(Context context) {
        super(context);
        this.context = context;
        initUI();
    }

    public TabRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initUI();
    }

    public TabRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initUI();
    }

    private void initUI() {
        tabView = LayoutInflater.from(context).inflate(R.layout.tab, null, false);
        tabView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (controller != null) {
                    controller.showTab(TabRelativeLayout.this);
                }
            }
        });
        tabView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (title.getText().toString().isEmpty()) {
                    Toast.makeText(context, R.string.browser_tab_untitled, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, title.getText().toString(), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        title = (TextView) tabView.findViewById(R.id.tab_title);
        title.setText(R.string.browser_tab_untitled);

        closeButton = (ImageButton) tabView.findViewById(R.id.tab_close_button);
        closeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.deleteTab();
            }
        });
    }

    @Override
    public void activate() {
        tabView.setBackgroundColor(context.getResources().getColor(R.color.gray_900));
        closeButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void deactivate() {
        tabView.setBackgroundColor(context.getResources().getColor(R.color.gray_1000));
        closeButton.setVisibility(View.GONE);
    }

    @Override
    public int getFlag() {
        return flag;
    }

    @Override
    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public View getTabView() {
        return tabView;
    }

    @Override
    public void setTabTitle(String title) {
        this.title.setText(title);
    }
}
