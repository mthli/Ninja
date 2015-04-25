package io.github.mthli.Ninja.Browser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import io.github.mthli.Ninja.R;

public class NinjaTab {
    private NinjaView ninjaView;
    private Context context;

    private View view;
    public View getView() {
        return view;
    }

    private TextView title;
    private ImageButton closeButton;

    public NinjaTab(NinjaView ninjaView) {
        this.ninjaView = ninjaView;
        this.context = ninjaView.getContext();
        initUI();
    }

    private void initUI() {
        view = LayoutInflater.from(context).inflate(R.layout.tab, null, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ninjaView.getController().showTab(ninjaView);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(context, title.getText().toString(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        title = (TextView) view.findViewById(R.id.tab_title);
        title.setText(ninjaView.getTitle());

        closeButton = (ImageButton) view.findViewById(R.id.tab_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ninjaView.getController().deleteTab();
            }
        });
    }

    public void activate() {
        view.setBackgroundColor(context.getResources().getColor(R.color.gray_900));
        closeButton.setVisibility(View.VISIBLE);
    }

    public void deactivate() {
        view.setBackgroundColor(context.getResources().getColor(R.color.gray_1000));
        closeButton.setVisibility(View.GONE);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }
}
