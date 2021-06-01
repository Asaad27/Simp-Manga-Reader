package com.simpmangareader.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simpmangareader.R;
import com.simpmangareader.provider.data.Manga;
import com.simpmangareader.util.GridAutoFitLayoutManager;
import com.simpmangareader.util.ItemClickSupport;
import com.simpmangareader.util.RecyclerViewAdapter;

import java.util.ArrayList;


public class Fragment_recent extends Fragment {

    private ArrayList<Manga> mData;

    protected LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView mRecyclerView;
    protected RecyclerViewAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    private static final String TAG = "RecyclerViewFragment";
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

        mData = new ArrayList<>();
        //TODO: get data from local storage or db about the recent activities
        //TODO: we still need to figure out how to store the history of read manga
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_recent, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_recent_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        mAdapter = new RecyclerViewAdapter(mData);
        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), VERTICAL);
        mRecyclerView.addItemDecoration(itemDecor);

        this.configureOnClickRecyclerView();
        this.configureOnLongClickRecyclerView();


        return rootView;
    }



    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        if (layoutManagerType == LayoutManagerType.GRID_LAYOUT_MANAGER) {
            mLayoutManager = new GridAutoFitLayoutManager(getActivity(), COLUMN_WIDTH);
            mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
        } else {
            mLayoutManager = new LinearLayoutManager(getActivity());
            mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    private void configureOnLongClickRecyclerView() {
        ItemClickSupport.addTo(mRecyclerView, R.layout.activity_main)
                .setOnItemLongClickListener((recyclerView, position, v) -> {
                    Toast.makeText(getContext(), "long clicked \"Position : \""+position, Toast.LENGTH_LONG).show();

                    return true;
                });
    }

    private void configureOnClickRecyclerView()
    {
        ItemClickSupport.addTo(mRecyclerView, R.layout.activity_main)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    Toast.makeText(getContext(), "short clicked \"Position : \""+position, Toast.LENGTH_LONG).show();

                    //passing args and starting chapter detail activity
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    /*Bundle bundle = new Bundle();
                    synchronized (mData.get(position)) {
                        bundle.putParcelable("mangas", mData.get(position));
                    }
                    intent.putExtras(bundle);*/
                    startActivity(intent);
                });
    }




    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }



}
