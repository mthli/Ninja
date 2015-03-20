package io.github.mthli.Berries.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import io.github.mthli.Berries.Database.Record;
import io.github.mthli.Berries.R;

import java.util.List;

public class IndexItemAdapter extends ArrayAdapter<Record> {
    private Context context;
    private int layoutResId;
    private List<Record> list;

    public IndexItemAdapter(Context context, int layoutResId, List<Record> list) {
        super(context, layoutResId, list);

        this.context = context;
        this.layoutResId = layoutResId;
        this.list = list;
    }

    // TODO
    class Holder {
        TextView title;
        TextView url;
    }

    // TODO
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        Holder holder;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(layoutResId, viewGroup, false);

            holder = new Holder();
            holder.title = (TextView) view.findViewById(R.id.index_item_title);
            holder.url = (TextView) view.findViewById(R.id.index_item_url);

            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        Record record = list.get(position);
        holder.title.setText(record.getTitle());
        holder.url.setText(record.getURL());

        return view;
    }
}
