package com.simpmangareader.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simpmangareader.R;
import com.simpmangareader.provider.data.MangaDetail;
import com.simpmangareader.util.GridAutoFitLayoutManager;
import com.simpmangareader.util.ItemClickSupport;
import com.simpmangareader.util.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

public class Fragment_library extends Fragment {

    private List<MangaDetail> mData;

    protected Fragment_library.LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView mRecyclerView;
    protected RecyclerViewAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager; private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 4;
    private static final int COLUMN_WIDTH = 130;
    private static final int DATASET_COUNT = 60;


    public enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize dataset, this data would usually come from a local content provider or
        // remote server.
        initDataset();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_library, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_library_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (Fragment_library.LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        mAdapter = new RecyclerViewAdapter(mData);
        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);


        return rootView;
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    public void setRecyclerViewLayoutManager(Fragment_library.LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        if (layoutManagerType == Fragment_library.LayoutManagerType.GRID_LAYOUT_MANAGER) {
            mLayoutManager = new GridAutoFitLayoutManager(getActivity(), COLUMN_WIDTH);
            mCurrentLayoutManagerType = Fragment_library.LayoutManagerType.GRID_LAYOUT_MANAGER;
        } else {
            mLayoutManager = new LinearLayoutManager(getActivity());
            mCurrentLayoutManagerType = Fragment_library.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);

        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), VERTICAL);
        mRecyclerView.addItemDecoration(itemDecor);

        this.configureOnClickRecyclerView();
        this.configureOnLongClickRecyclerView();

    }

    private void configureOnLongClickRecyclerView() {
        ItemClickSupport.addTo(mRecyclerView, R.layout.activity_main)
                .setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener()
                {
                    @Override
                    public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                        Toast.makeText(getContext(), "long clicked \"Position : \""+position, Toast.LENGTH_LONG).show();

                        return true;
                    }
                });
    }

    private void configureOnClickRecyclerView()
    {
        ItemClickSupport.addTo(mRecyclerView, R.layout.activity_main)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener()
                {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v)
                    {
                        Toast.makeText(getContext(), "short clicked \"Position : \""+position, Toast.LENGTH_LONG).show();
                    }
                });
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Generates Strings for RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     */
    private void initDataset() {
        mData = new ArrayList<>();

        mData.add(new MangaDetail("test", R.drawable.covertest));
        mData.add(new MangaDetail("test", R.drawable.covertest));
        mData.add(new MangaDetail("test", R.drawable.covertest));
        mData.add(new MangaDetail("test", R.drawable.covertest));
        mData.add(new MangaDetail("test", R.drawable.covertest));
    }

}
