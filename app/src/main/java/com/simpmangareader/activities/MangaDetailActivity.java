package com.simpmangareader.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Looper;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.simpmangareader.R;
import com.simpmangareader.database.SharedPreferencesHelper;
import com.simpmangareader.provider.data.Chapter;
import com.simpmangareader.provider.data.Manga;
import com.simpmangareader.provider.mangadex.Mangadex;
import com.simpmangareader.util.BitmapConverter;
import com.simpmangareader.util.GridAutoFitLayoutManager;
import com.simpmangareader.util.ItemClickSupport;
import com.simpmangareader.util.MangaChaptersRVadapter;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;
import static com.simpmangareader.database.SharedPreferencesHelper.favPreference_file_key;

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

    MenuItem bt_bookmark;
    Menu menu;
    BottomNavigationItemView item;
    Manga manga;
    Chapter[] chapters;

    boolean isPressedFav = false;
    private BottomNavigationView bottomNavigationView;


    public enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }


    @SuppressLint("RestrictedApi")
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

        manga = this.getIntent().getExtras().getParcelable("mangas");
        coverImage.setImageBitmap(manga.cover);
        titleText.setText(Html.fromHtml( "<b>" + manga.title +"</b>"));
        categoryText.append((Html.fromHtml("<em> " + manga.publicationDemographic + " </em>")));
        statusText.append((Html.fromHtml("<em> " + (manga.status)+ "</em>")));
        descriptionText.append((Html.fromHtml("<em> " +manga.description+ "</em>")));
        descriptionText.setMovementMethod(new ScrollingMovementMethod());

        //TODO : change bookmark_button icon onclick
        /*bottomNavigationView = findViewById(R.id.second_toolbar_manga_detail);
        menu = bottomNavigationView.getMenu();
        System.out.println("mangaisfav : " + manga.isFav);
        menu.findItem(R.id.bt_bookmark).setCheckable(true);
        menu.findItem(R.id.bt_bookmark).setEnabled(true);
        menu.findItem(R.id.bt_bookmark).setChecked(manga.isFav);*/

        isPressedFav = manga.isFav;

        Mangadex.FetchAllMangaEnglishChapter(manga.id,
                (result, offset, totalSize) -> {
                    if (chapters == null)
                    {
                        chapters = new Chapter[totalSize];
                        mAdapter.setChapters(chapters);
                    }
                    for (int i= offset; i < offset + result.length; ++i)
                    {
                        synchronized (chapters)
                        {
                            chapters[i] = result[i - offset];
                        }
                    }
                    synchronized (mAdapter) {
                        mAdapter.notifyDataSetChanged();
                    }
                    synchronized (mRecyclerView) {
                        mRecyclerView.notifyAll();
                    }
                },
                e -> {
                    //TODO: report failure
                },
                HandlerCompat.createAsync(Looper.myLooper()));

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
        mAdapter = new MangaChaptersRVadapter(chapters, 0);
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, VERTICAL);
        mRecyclerView.addItemDecoration(itemDecor);

        this.configureOnClickRecyclerView();

    }


    //toolbar back button onclick
    public void bt_back(MenuItem item) {
        super.onBackPressed(); // or super.finish();
    }
    public void bt_bookmark(MenuItem item) {


           SharedPreferencesHelper spHelper = SharedPreferencesHelper.getInstance(getApplicationContext());
           spHelper.setSharedPreferencesHelper(favPreference_file_key, getApplicationContext());
           spHelper.AddOrRemove(manga);
           isPressedFav = !isPressedFav;
           item.setChecked(isPressedFav);


    }


    private void configureOnClickRecyclerView()
    {
        ItemClickSupport.addTo(mRecyclerView, R.layout.manga_detail_chapters)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    Log.e("TAG", "Position : "+position);
                    //Toast.makeText(getBaseContext(), "short clicked \"Position : \""+position, Toast.LENGTH_LONG).show();

                    /*we save the chapter as recent
                    and add cover and title of the manga to Chapter*/

                    //first we change the shared preference data
                    SharedPreferencesHelper spHelper = SharedPreferencesHelper.getInstance(getApplicationContext());
                    spHelper.setSharedPreferencesHelper("simpmangareader.recPreference_file_key", getApplicationContext());
                    //we add root manga cover and title to chapter
                    chapters[position].MangaTitle = manga.title;
                    chapters[position].CoverBitmapEncoded = BitmapConverter.getStringFromBitmap(manga.cover);
                    Log.e("MangaDetailActivity", "configureOnClickRecyclerView: chapter ID "+ chapters[position].id );
                    spHelper.AddOrRemove(chapters[position]);
                    startFragment(chapters[position]);
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

    public void startFragment(Chapter chapter) {

        Bundle bundle = new Bundle();
        bundle.putSerializable("manga", chapter);
        bundle.putInt("position", 0);
        assert getFragmentManager() != null;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ReaderFragment newFragment = ReaderFragment.newInstance();
        newFragment.setArguments(bundle);
        newFragment.show(ft, "slideshow");
    }
}


