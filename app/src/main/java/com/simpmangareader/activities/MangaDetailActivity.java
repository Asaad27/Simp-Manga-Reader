package com.simpmangareader.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.os.HandlerCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.simpmangareader.R;
import com.simpmangareader.provider.data.Chapter;
import com.simpmangareader.provider.data.Manga;
import com.simpmangareader.provider.mangadex.Mangadex;
import com.simpmangareader.util.GridAutoFitLayoutManager;
import com.simpmangareader.util.ItemClickSupport;
import com.simpmangareader.util.MangaChaptersRVadapter;


import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

public class MangaDetailActivity extends AppCompatActivity {

    protected RecyclerView mRecyclerView;
    protected Fragment_recent.LayoutManagerType mCurrentLayoutManagerType;
    protected MangaChaptersRVadapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int COLUMN_WIDTH = 130;
    private ImageView coverImage;
    private TextView titleText;
    private TextView categoryText;
    private TextView statusText;
    private TextView descriptionText;
    Manga manga;
    Chapter[] chapters;




    public enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_detail);
        //TODO: maybe adjust the layout and change font sizes so that the texts are visible
        coverImage = findViewById(R.id.imageView);
        titleText = findViewById(R.id.manga_detail_title_tv);
        categoryText = findViewById(R.id.manga_category_title_tv);
        statusText = findViewById(R.id.manga_detail_status_tv);
        descriptionText = findViewById(R.id.manga_detail_description_tv);

        chapters = new Chapter[0];
        //get data from previous activity
        manga =  this.getIntent().getExtras().getParcelable("manga");

        coverImage.setImageBitmap(manga.cover);
        titleText.setText(manga.title);
        categoryText.setText(manga.publicationDemographic);
        statusText.setText(manga.status);
        descriptionText.setText(manga.description);

        //TODO: use manga to set the manga specific data
        Mangadex.FetchAllMangaEnglishChapter(manga.id, result -> {
            //TODO: display the fetched chapters
        }, e -> {
            //TODO: report failure
        }, HandlerCompat.createAsync(Looper.myLooper()));


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
        mAdapter = new MangaChaptersRVadapter(chapters);
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
                .setOnItemClickListener((recyclerView, position, v) -> {
                    Log.e("TAG", "Position : "+position);
                    Toast.makeText(getBaseContext(), "short clicked \"Position : \""+position, Toast.LENGTH_LONG).show();
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

}


