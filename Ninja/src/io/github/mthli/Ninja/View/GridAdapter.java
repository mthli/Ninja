package io.github.mthli.Ninja.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import io.github.mthli.Ninja.R;
import org.askerov.dynamicgrid.BaseDynamicGridAdapter;

import java.util.List;

public class GridAdapter extends BaseDynamicGridAdapter {
    private static class Holder {
        TextView title;
        ImageView cover;
    }

    private Context context;
    private List<GridItem> list;

    public GridAdapter(Context context, List<GridItem> list, int columnCount) {
        super(context, list, columnCount);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
            holder = new Holder();
            holder.title = (TextView) view.findViewById(R.id.grid_item_title);
            holder.cover = (ImageView) view.findViewById(R.id.grid_item_cover);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        GridItem item = list.get(position);
        holder.title.setText(item.getTitle());

        // TODO: file to bitmap

        return view;
    }
}
