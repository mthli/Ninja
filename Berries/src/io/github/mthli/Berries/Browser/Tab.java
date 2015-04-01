package io.github.mthli.Berries.Browser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import io.github.mthli.Berries.Database.Record;
import io.github.mthli.Berries.R;
import io.github.mthli.Berries.Unit.ViewUnit;

public class Tab {
    private Berry berry;
    private Context context;

    private Record record;
    public Record getRecord() {
        return record;
    }
    public void setRecord(Record record) {
        this.record = record;
        title.setText(record.getTitle());
    }

    private boolean incognito;

    private View view;
    public View getView() {
        return view;
    }

    private TextView title;
    private ImageButton closeButton;
    private View incognitoLine;

    private BrowserController controller;
    public void setController(BrowserController controller) {
        this.controller = controller;
    }

    public Tab(Berry berry) {
        this.berry = berry;
        this.context = berry.getContext();
        this.record = berry.getRecord();
        this.incognito = berry.isIncognito();
        this.controller = berry.getController();

        initUI();
    }

    private void initUI() {
        view = LayoutInflater.from(context).inflate(R.layout.tab, null, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.showSelectedTab(berry);
            }
        });

        title = (TextView) view.findViewById(R.id.tab_title);
        title.setText(record.getTitle());

        closeButton = (ImageButton) view.findViewById(R.id.tab_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.deleteSelectedTab();
            }
        });

        incognitoLine = view.findViewById(R.id.tab_incognito_line);
        if (incognito) {
            incognitoLine.setVisibility(View.VISIBLE);
        } else {
            incognitoLine.setVisibility(View.GONE);
        }
    }

    public void activate() {
        if (view != null) {
            view.setBackgroundColor(context.getResources().getColor(R.color.gray_900));
            title.setWidth((int) ViewUnit.dp2px(context, ViewUnit.WIDTH_MAX));
            closeButton.setVisibility(View.VISIBLE);
        }
    }

    public void deactivate() {
        if (view != null) {
            view.setBackgroundColor(context.getResources().getColor(R.color.gray_1000));
            title.setWidth((int) ViewUnit.dp2px(context, ViewUnit.WIDTH_MIN));
            closeButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }

        if (!(object instanceof Tab)) {
            return false;
        }

        return this.record.getTime() == ((Tab) object).getRecord().getTime();
    }

    @Override
    public int hashCode() {
        return (int) (this.record.getTime() * 31);
    }
}
