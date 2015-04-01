package io.github.mthli.Berries.Browser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import io.github.mthli.Berries.Database.Record;
import io.github.mthli.Berries.R;

public class Tab {
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

    public Tab(Context context, Record record, boolean incognito) {
        this.context = context;
        this.record = record;
        this.incognito = incognito;

        initUI();
    }

    private void initUI() {
        view = LayoutInflater.from(context).inflate(R.layout.tab, null, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });

        title = (TextView) view.findViewById(R.id.tab_title);
        title.setText(record.getTitle());

        closeButton = (ImageButton) view.findViewById(R.id.tab_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });

        incognitoLine = view.findViewById(R.id.tab_incognito_line);
        if (incognito) {
            incognitoLine.setVisibility(View.VISIBLE);
        } else {
            incognitoLine.setVisibility(View.GONE);
        }
    }

    public void activateTab() {
        if (view != null) {
            view.setBackgroundColor(context.getResources().getColor(R.color.gray_900));
            closeButton.setVisibility(View.VISIBLE);
        }
    }

    public void deactivateTab() {
        if (view != null) {
            view.setBackgroundColor(context.getResources().getColor(R.color.gray_800));
            closeButton.setVisibility(View.INVISIBLE);
        }
    }
}
