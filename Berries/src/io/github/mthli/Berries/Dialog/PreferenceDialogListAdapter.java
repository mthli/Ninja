package io.github.mthli.Berries.Dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import io.github.mthli.Berries.R;

import java.util.List;

public class PreferenceDialogListAdapter extends ArrayAdapter<PreferenceDialogListItem> {
    private Context context;
    private int layoutResId;
    private List<PreferenceDialogListItem> list;

    public PreferenceDialogListAdapter(Context context, int layoutResId, List<PreferenceDialogListItem> list) {
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
            holder.title = (TextView) view.findViewById(R.id.preference_dialog_list_textview);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        PreferenceDialogListItem item = list.get(position);
        holder.title.setText(item.getTitle());

        return view;
    }
}
