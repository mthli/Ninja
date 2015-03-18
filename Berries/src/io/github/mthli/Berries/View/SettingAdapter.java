package io.github.mthli.Berries.View;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import io.github.mthli.Berries.R;
import io.github.mthli.Berries.Unit.Flag;

import java.util.List;

public class SettingAdapter extends ArrayAdapter<SettingItem> {
    private Context context;
    private int layoutResId;
    private List<SettingItem> list;

    public SettingAdapter(Context context, int layoutResId, List<SettingItem> list) {
        super(context, layoutResId, list);

        this.context = context;
        this.layoutResId = layoutResId;
        this.list = list;
    }

    class Holder {
        TextView title;
        TextView content;
        SwitchCompat switchc;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        Holder holder;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(layoutResId, viewGroup, false);
            holder = new Holder();

            holder.title = (TextView) view.findViewById(R.id.setting_item_title);
            holder.content = (TextView) view.findViewById(R.id.setting_item_content);
            holder.switchc = (SwitchCompat) view.findViewById(R.id.setting_item_switch);

            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        SettingItem item = list.get(position);
        holder.title.setText(item.getTitle());
        holder.content.setText(item.getContent());
        switch (item.getStatus()) {
            case Flag.SWITCHC_HIDE:
                holder.switchc.setVisibility(View.GONE);
                break;
            case Flag.SWITCHC_OFF:
                holder.switchc.setChecked(false);
                holder.switchc.setVisibility(View.VISIBLE);
                break;
            case Flag.SWITCHC_ON:
                holder.switchc.setChecked(true);
                holder.switchc.setVisibility(View.VISIBLE);
                break;
            default:
                holder.switchc.setVisibility(View.GONE);
                break;
        }

        item.setView(view);
        return view;
    }
}
