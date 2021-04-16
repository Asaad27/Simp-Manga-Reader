package com.simpmangareader.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.simpmangareader.R;
import com.simpmangareader.provider.data.ChapterDetail;
import com.simpmangareader.provider.data.MangaDetail;
import com.simpmangareader.util.GridAutoFitLayoutManager;
import com.simpmangareader.util.ItemClickSupport;
import com.simpmangareader.util.MangaChaptersRVadapter;
import com.simpmangareader.util.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.Inflater;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

public class MangaDetailActivity extends AppCompatActivity {

    protected RecyclerView mRecyclerView;
    protected Fragment_recent.LayoutManagerType mCurrentLayoutManagerType;
    protected MangaChaptersRVadapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int COLUMN_WIDTH = 130;

    private List<ChapterDetail> mData;




    public enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_detail);

        initDataset();
        //get data from previous activity

        List<ChapterDetail> mData =  this.getIntent().getExtras().getParcelableArrayList("mangaChapters");




        //Recycler view
        mRecyclerView = findViewById(R.id.manga_detail_rv_chapters);
        mLayoutManager = new LinearLayoutManager(this);
        mCurrentLayoutManagerType = Fragment_recent.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (Fragment_recent.LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
        mAdapter = new MangaChaptersRVadapter(mData);
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, VERTICAL);
        mRecyclerView.addItemDecoration(itemDecor);

        this.configureOnClickRecyclerView();


    }


//toolbar back button onclick
    public void bt_back(MenuItem item) {
        super.onBackPressed(); // or super.finish();
    }



    private void configureOnClickRecyclerView()
    {
        ItemClickSupport.addTo(mRecyclerView, R.layout.manga_detail_chapters)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener()
                {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v)
                    {
                        Log.e("TAG", "Position : "+position);
                        Toast.makeText(getBaseContext(), "short clicked \"Position : \""+position, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */

    public void setRecyclerViewLayoutManager(Fragment_recent.LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        if (layoutManagerType == Fragment_recent.LayoutManagerType.GRID_LAYOUT_MANAGER) {
            mLayoutManager = new GridAutoFitLayoutManager(this, COLUMN_WIDTH);
            mCurrentLayoutManagerType = Fragment_recent.LayoutManagerType.GRID_LAYOUT_MANAGER;
        } else {
            mLayoutManager = new LinearLayoutManager(this);
            mCurrentLayoutManagerType = Fragment_recent.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }



    /**
     * Generates RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     */
    private void initDataset() {
        mData = new ArrayList<>();

        mData.add(new ChapterDetail("test the chapter",3    ));
        mData.add(new ChapterDetail("test the chapter",3    ));
        mData.add(new ChapterDetail("test the chapter",3    ));
        mData.add(new ChapterDetail("test the chapter",3    ));
        mData.add(new ChapterDetail("test the chapter",3    ));
        mData.add(new ChapterDetail("test the chapter",3    ));
        mData.add(new ChapterDetail("test the chapter",3    ));
    }


}


