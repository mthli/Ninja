package io.github.mthli.Berries.Dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import io.github.mthli.Berries.R;

import java.util.List;

public class PreferenceDialogAdapter extends ArrayAdapter<PreferenceDialogItem> {
    private Context context;
    private int layoutResId;
    private List<PreferenceDialogItem> list;

    public PreferenceDialogAdapter(Context context, int layoutResId, List<PreferenceDialogItem> list) {
        super(context, layoutResId, list);

        this.context = context;
        this.layoutResId = layoutResId;
        this.list = list;
    }

    class Holder {
        TextView title;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        Holder holder;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(layoutResId, viewGroup, false);

            holder = new Holder();
            holder.title = (TextView) view.findViewById(R.id.preference_dialog_item);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        PreferenceDialogItem item = list.get(position);
        holder.title.setText(item.getTitle());

        return view;
    }
}
