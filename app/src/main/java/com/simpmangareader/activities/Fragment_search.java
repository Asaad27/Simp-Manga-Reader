package com.simpmangareader.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.HandlerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simpmangareader.R;
import com.simpmangareader.provider.data.Manga;
import com.simpmangareader.provider.mangadex.Mangadex;
import com.simpmangareader.util.GridAutoFitLayoutManager;
import com.simpmangareader.util.ItemClickSupport;
import com.simpmangareader.util.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

public class Fragment_search extends Fragment {
    String query;
    private final ArrayList<Manga> mData = new ArrayList<>();
    protected Fragment_browse.LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView mRecyclerView;
    protected RecyclerViewAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int COLUMN_WIDTH = 130;

    int currentIndex= 0, currentLimit = 15;
    boolean is_loading = false;


    private final Handler myHandler = HandlerCompat.createAsync(Looper.myLooper());

    public Fragment_search(String query) {
        this.query = query;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_results, container, false);
        initRecyclerView(rootView, savedInstanceState);

        if (mData.size() == 0) {
            FetchMoreManga();
        }

        return rootView;
    }

    private void FetchMoreManga()
    {
        if (!is_loading) {
            is_loading = true;
            mData.add(null);
            mAdapter.notifyItemInserted(mData.size() - 1);
        }
        else{
            // it's a retry fetch
        }
        Mangadex.SearchMangaByName(currentIndex, currentLimit, query, result -> {
            //NOTE(Mouad): result is an array of Manga
            //UI UPDATED
            synchronized (mData) {
                mData.remove(mData.size() - 1);
            }
            synchronized (mData) {
                mData.addAll(Arrays.asList(result));
            }
            synchronized (mAdapter) {
                mAdapter.notifyDataSetChanged();
            }
            is_loading = false;
            currentIndex += currentLimit;
        }, e -> {
            //retry again
            FetchMoreManga();
        }, myHandler);
    }


    public void initRecyclerView(View rootView, Bundle savedInstanceState){
        //get recycler view from resource
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_browse_recycler_view);
        //initialize layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = Fragment_browse.LayoutManagerType.GRID_LAYOUT_MANAGER;
        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (Fragment_browse.LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
        //configure adapter
        mAdapter = new RecyclerViewAdapter(mData);
        mRecyclerView.setAdapter(mAdapter);
        //configure item decoration
        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), VERTICAL);
        mRecyclerView.addItemDecoration(itemDecor);
        //configure scroll listener
        initScrollListener();

        this.configureOnClickRecyclerView();
        this.configureOnLongClickRecyclerView();
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    public void setRecyclerViewLayoutManager(Fragment_browse.LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        if (layoutManagerType == Fragment_browse.LayoutManagerType.GRID_LAYOUT_MANAGER) {
            mLayoutManager = new GridAutoFitLayoutManager(getActivity(), COLUMN_WIDTH);
            mCurrentLayoutManagerType = Fragment_browse.LayoutManagerType.GRID_LAYOUT_MANAGER;
        } else {
            mLayoutManager = new LinearLayoutManager(getActivity());
            mCurrentLayoutManagerType = Fragment_browse.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);

    }

    private void initScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                final int visibleThreshold = 2;

                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                assert layoutManager != null;

                int lastItem = layoutManager.findLastCompletelyVisibleItemPosition();
                //int currentTotalCount = layoutManager.getItemCount();
                int currentTotalCount = mData.size();


                if (!is_loading && currentTotalCount <= lastItem + visibleThreshold )
                {
                    FetchMoreManga();
                }
            }
        });
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
                    Intent intent = new Intent(getContext(), MangaDetailActivity.class);
                    Bundle bundle = new Bundle();
                    synchronized (mData) {
                        if (mData.get(position) == null) return;
                        bundle.putParcelable("mangas", mData.get(position));
                    }
                    intent.putExtras(bundle);
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