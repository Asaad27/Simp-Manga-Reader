package com.simpmangareader.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.HandlerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.simpmangareader.R;
import com.simpmangareader.provider.data.Manga;
import com.simpmangareader.provider.mangadex.Mangadex;
import com.simpmangareader.util.GridAutoFitLayoutManager;
import com.simpmangareader.util.ItemClickSupport;
import com.simpmangareader.util.RecyclerViewAdapter;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Collections;
import java.util.Comparator;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

public class Fragment_browse extends Fragment {
    private final ArrayList<Manga> mData = new ArrayList<>();
    protected Fragment_browse.LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView mRecyclerView;
    protected RecyclerViewAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 4;
    private static final int COLUMN_WIDTH = 130;
    private static final int DATASET_COUNT = 60;

    final int FETCH_NORMAL = 1;
    final int FETCH_LATEST_ASC = 2;
    final int FETCH_LATEST_DES = 3;
    int fetch_mode = FETCH_NORMAL;

    int sortClicked = 0;
    int currentIndex= 0, currentLimit = 15;
    boolean is_loading = false;
    boolean is_retrying = false;
    private BottomNavigationView bottomNavigationView;


    public enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    private final Handler myHandler = HandlerCompat.createAsync(Looper.myLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_browse, container, false);
        initRecyclerView(rootView, savedInstanceState);

        bottomNavigationView = rootView.findViewById(R.id.browse_toolbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
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
        else if (!is_retrying){
            return;
        }
        switch(fetch_mode)
        {
            case FETCH_NORMAL:
            {
                Mangadex.FetchManga(currentIndex, currentLimit, result -> {
                    //NOTE(Mouad): result is an array of Manga
                    //UI UPDATED
                    if (fetch_mode != FETCH_NORMAL)
                    {
                        //fetch mode changed.. this data is useless so just return
                        return;
                    }
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
                    is_retrying = false;
                    currentIndex += currentLimit;
                }, e -> {
                    //retry again
                    is_retrying = true;
                    FetchMoreManga();
                }, myHandler);
            }; break;
            case FETCH_LATEST_ASC:
            {
                Mangadex.FetchMangaLatestAsc(currentIndex, currentLimit, result -> {
                    //NOTE(Mouad): result is an array of Manga
                    //UI UPDATED
                    if (fetch_mode != FETCH_LATEST_ASC)
                    {
                        //fetch mode changed.. this data is useless so just return
                        return;
                    }
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
                    is_retrying = false;
                    currentIndex += currentLimit;
                }, e -> {
                    //retry again
                    is_retrying = true;
                    FetchMoreManga();
                }, myHandler);
            }; break;
            case FETCH_LATEST_DES:
            {
                Mangadex.FetchMangaLatestDes(currentIndex, currentLimit, result -> {
                    //NOTE(Mouad): result is an array of Manga
                    //UI UPDATED
                    if (fetch_mode != FETCH_LATEST_DES)
                    {
                        //fetch mode changed.. this data is useless so just return
                        return;
                    }
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
                    is_retrying = false;
                    currentIndex += currentLimit;
                }, e -> {
                    //retry again
                    is_retrying = true;
                    FetchMoreManga();
                }, myHandler);
            }break;
        }

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
                    //Toast.makeText(getContext(), "long clicked \"Position : \""+position, Toast.LENGTH_LONG).show();

                    return true;
                });
    }

    private void configureOnClickRecyclerView()
    {
        ItemClickSupport.addTo(mRecyclerView, R.layout.activity_main)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    //Toast.makeText(getContext(), "short clicked \"Position : \""+position, Toast.LENGTH_LONG).show();

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


    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {

                switch (item.getItemId()) {

                    case R.id.browse_sort:
                    {
                        Log.e(TAG, "bt_sort_alpha: ");
                        if (sortClicked == 0) {
                            Collections.sort(mData, new Comparator<Manga>() {
                                @Override
                                public int compare(Manga o1, Manga o2) {
                                    return o1.title.compareTo(o2.title);
                                }
                            });
                        } else {
                            Collections.sort(mData, new Comparator<Manga>() {
                                @Override
                                public int compare(Manga o1, Manga o2) {
                                    return o2.title.compareTo(o1.title);
                                }
                            });
                        }
                        mAdapter.notifyDataSetChanged();
                        sortClicked = 1 - sortClicked;
                    } break;
                    case R.id.latest:
                    {
                        // TODO: change the loading mode
                        switch(fetch_mode)
                        {
                            case FETCH_NORMAL: fetch_mode = FETCH_LATEST_ASC; break;
                            case FETCH_LATEST_ASC: fetch_mode = FETCH_LATEST_DES; break;
                            case FETCH_LATEST_DES: fetch_mode = FETCH_LATEST_ASC; break;
                        }
                        synchronized (mData)
                        {
                            mData.clear();
                        }
                        mAdapter.notifyDataSetChanged();
                        is_loading = false;
                        is_retrying = false;
                        FetchMoreManga();
                    } break;

                }
                return true;
            };



    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);

    }



}

