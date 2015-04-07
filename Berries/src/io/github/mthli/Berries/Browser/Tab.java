package io.github.mthli.Berries.Browser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import io.github.mthli.Berries.Database.Record;
import io.github.mthli.Berries.R;

public class Tab {
    private BerryView berryView;
    private Context context;

    private Record record;
    public Record getRecord() {
        return record;
    }

    private boolean incognito;

    private View view;
    public View getView() {
        return view;
    }

    private TextView title;
    private ImageButton closeButton;

    public Tab(BerryView berryView) {
        this.berryView = berryView;
        this.context = berryView.getContext();
        this.record = berryView.getRecord();
        this.incognito = berryView.isIncognito();

        initUI();
    }

    private void initUI() {
        view = LayoutInflater.from(context).inflate(R.layout.tab, null, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                berryView.getController().showTab(berryView);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(context, record.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        title = (TextView) view.findViewById(R.id.tab_title);
        title.setText(record.getTitle());

        closeButton = (ImageButton) view.findViewById(R.id.tab_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                berryView.getController().deleteTab();
            }
        });

        View incognitoLine = view.findViewById(R.id.tab_incognito_line);
        if (incognito) {
            incognitoLine.setVisibility(View.VISIBLE);
        } else {
            incognitoLine.setVisibility(View.GONE);
        }
    }

    public void activate() {
        view.setBackgroundColor(context.getResources().getColor(R.color.gray_900));
        closeButton.setVisibility(View.VISIBLE);
    }

    public void deactivate() {
        view.setBackgroundColor(context.getResources().getColor(R.color.gray_1000));
        closeButton.setVisibility(View.GONE);
    }

    public void update(String title, String url) {
        this.record.setTitle(title);
        this.record.setURL(url);
        this.title.setText(title);
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
