package hci.com.weirdcam.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import hci.com.weirdcam.R;

public class GalleryAdapter extends BaseAdapter{

    private List<String> items = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;

    public GalleryAdapter(Context context, List<String> items) {
        this.context = context;
        inflater= LayoutInflater.from(context);
        this.items = items;
    }


    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            row = inflater.inflate(R.layout.gallery_item, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.item_image);
            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        Uri uri = Uri.fromFile(new File(items.get(position)));

        Picasso.with(context).load(uri)
                .resize(150, 150).centerCrop().into(holder.image);

        return row;
    }

    static class ViewHolder {
        ImageView image;
    }
}
