package hci.com.weirdcam.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import hci.com.weirdcam.R;
import hci.com.weirdcam.instances.Filter;

public class FilterListAdapter extends RecyclerView.Adapter<FilterListAdapter.FilterViewHolder>{

    List<Filter> filters;
    private FilterListCallback callback;

    public FilterListAdapter(List<Filter> filters) {
        this.filters = filters;
    }

    @Override
    public FilterListAdapter.FilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter_view, parent, false);
        FilterViewHolder filterViewHolder = new FilterViewHolder(v);
        return filterViewHolder;
    }

    @Override
    public void onBindViewHolder(FilterViewHolder holder, final int position) {
        holder.filterPhoto.setImageResource(filters.get(position).getPhotoId());
        holder.filterName.setText(filters.get(position).getFilterName());
        holder.filterPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onFilterClick(position);
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return filters.size();
    }

    /**
     * Callback
     */
    public interface FilterListCallback {
        void onFilterClick(int position);
    }

    public void setFilterListCallback(FilterListCallback callback) {
        this.callback = callback;
    }

    /**
     * Holder
     */

    public static class FilterViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView filterPhoto;
        TextView filterName;

        public FilterViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cv);
            filterPhoto = (ImageView) itemView.findViewById(R.id.filter_photo);
            filterName = (TextView) itemView.findViewById(R.id.filter_name);
        }
    }

}

