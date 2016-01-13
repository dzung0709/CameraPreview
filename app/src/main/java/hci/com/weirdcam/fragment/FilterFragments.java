package hci.com.weirdcam.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import hci.com.weirdcam.R;
import hci.com.weirdcam.adapter.FilterListAdapter;
import hci.com.weirdcam.instances.Filter;

public class FilterFragments extends Fragment implements FilterListAdapter.FilterListCallback{

    public static final String TAG = "filters_fragment";

    private boolean isInitialized;
    private List<Filter> filters;
    private FilterListAdapter adapter;
    private FilterFragmentCallback callback;

    private View rootView;
    private RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_filters, container, false);

            recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        } else {
            ViewGroup viewGroup = (ViewGroup)rootView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(rootView);
            }
        }
        return  rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!isInitialized) {
            isInitialized = true;
            init();
        }
    }

    private void init() {
        initData();
        adapter = new FilterListAdapter(filters);
        adapter.setFilterListCallback(this);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void initData() {
        filters = new ArrayList<>();
        filters.add(new Filter("test 1", R.mipmap.dummy_camera));
        filters.add(new Filter("test 2", R.mipmap.dummy_camera));
        filters.add(new Filter("test 3", R.mipmap.dummy_camera));
        filters.add(new Filter("test 4", R.mipmap.dummy_camera));
        filters.add(new Filter("test 5", R.mipmap.dummy_camera));
        filters.add(new Filter("test 6", R.mipmap.dummy_camera));
        filters.add(new Filter("test 7", R.mipmap.dummy_camera));
        filters.add(new Filter("test 8", R.mipmap.dummy_camera));
        filters.add(new Filter("test 9", R.mipmap.dummy_camera));
        filters.add(new Filter("test 10", R.mipmap.dummy_camera));
    }

    @Override
    public void onFilterClick(int position) {
        callback.onFilterItemClick(position);
    }

    /**
     * Callback
     */

    public interface FilterFragmentCallback {
        void onFilterItemClick(int position);
    }

    public void setFilterFragmentCallback(FilterFragmentCallback callback) {
        this.callback = callback;
    }
}
