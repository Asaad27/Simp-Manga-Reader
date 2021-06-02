package com.simpmangareader.activities;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import static androidx.recyclerview.widget.DividerItemDecoration.HORIZONTAL;
import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;
import static com.simpmangareader.database.SharedPreferencesHelper.favPreference_file_key;
import static com.simpmangareader.database.SharedPreferencesHelper.recPreference_file_key;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simpmangareader.R;
import com.simpmangareader.database.SharedPreferencesHelper;
import com.simpmangareader.provider.data.Chapter;
import com.simpmangareader.provider.data.Manga;
import com.simpmangareader.util.GridAutoFitLayoutManager;
import com.simpmangareader.util.ItemClickSupport;
import com.simpmangareader.util.MangaChaptersRVadapter;
import com.simpmangareader.util.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Objects;


public class Fragment_recent extends Fragment {

    protected Chapter[] mData;

    protected LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView mRecyclerView;
    protected MangaChaptersRVadapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 4;
    private static final int COLUMN_WIDTH = 130;
    private static final int DATASET_COUNT = 60;
    protected Button bt_clear_hist;




    public enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //loading recents
        SharedPreferencesHelper.getInstance(getActivity()).setSharedPreferencesHelper(recPreference_file_key, Objects.requireNonNull(getActivity()));
        mData =  SharedPreferencesHelper.getInstance(getActivity()).getAllRecs();


        View rootView = inflater.inflate(R.layout.fragment_recent, container, false);

        //load button
        bt_clear_hist = rootView.findViewById(R.id.bt_clear_history);
        bt_clear_hist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(getActivity());
                sharedPreferencesHelper.setSharedPreferencesHelper(recPreference_file_key, Objects.requireNonNull(getActivity()));
                sharedPreferencesHelper.removeHistory();
                mData = sharedPreferencesHelper.getAllRecs();

                mAdapter.notifyDataSetChanged();
                synchronized (mRecyclerView) {
                    mRecyclerView.notifyAll();
                }
                reloadFragment();

            }
        });

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_recent_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        mAdapter = new MangaChaptersRVadapter(mData, 1);
        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration itemDecor = new DividerItemDecoration(Objects.requireNonNull(getContext()), HORIZONTAL);
        mRecyclerView.addItemDecoration(itemDecor);

        this.configureOnClickRecyclerView();
        this.configureOnLongClickRecyclerView();


        return rootView;
    }

    public void reloadFragment(){
        mAdapter = new MangaChaptersRVadapter(mData, 1);
        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
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
                   // Toast.makeText(getContext(), "long clicked \"Position : \""+position, Toast.LENGTH_LONG).show();

                    return true;
                });
    }

    private void configureOnClickRecyclerView()
    {
        ItemClickSupport.addTo(mRecyclerView, R.layout.activity_main)
                .setOnItemClickListener((recyclerView, position, v) -> {
                   // Toast.makeText(getContext(), "short clicked \"Position : \""+position, Toast.LENGTH_LONG).show();

                    startFragment(mData[position]);
                });
    }

    public void startFragment(Chapter chapter) {

        Bundle bundle = new Bundle();
        bundle.putSerializable("manga", chapter);
        bundle.putInt("position", 0);
        assert getFragmentManager() != null;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ReaderFragment newFragment = ReaderFragment.newInstance();
        newFragment.setArguments(bundle);
        newFragment.show(ft, "slideshow");
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }



}
