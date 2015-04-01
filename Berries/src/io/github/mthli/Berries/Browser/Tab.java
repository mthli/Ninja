package io.github.mthli.Berries.Browser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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

    private View view;
    public View getView() {
        return view;
    }

    // TODO
    private ImageView incognitoIcon;
    private TextView title;
    private ImageButton closeButton;
    private View line;

    public Tab(Context context, Record record) {
        this.context = context;
        this.record = record;

        initUI();
    }

    private void initUI() {
        view = LayoutInflater.from(context).inflate(R.layout.tab, null, false);

        title = (TextView) view.findViewById(R.id.tab_title);
        title.setText(record.getTitle());
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });

        closeButton = (ImageButton) view.findViewById(R.id.tab_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });

        line = view.findViewById(R.id.tab_line);
        line.setVisibility(View.GONE);
    }

    public void activateTab() {
        if (view != null) {
            view.setBackgroundColor(context.getResources().getColor(R.color.gray_900));
            closeButton.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
        }
    }

    public void deactivateTab() {
        if (view != null) {
            view.setBackgroundColor(context.getResources().getColor(R.color.gray_800));
            closeButton.setVisibility(View.INVISIBLE);
            line.setVisibility(View.GONE);
        }
    }
}
