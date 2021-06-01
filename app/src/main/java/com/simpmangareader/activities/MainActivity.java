package com.simpmangareader.activities;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.os.Bundle;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.http.HttpResponseCache;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.simpmangareader.R;
import com.simpmangareader.provider.mangadex.Mangadex;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private final Fragment_browse fragment_browse = new Fragment_browse();
    private final Fragment_recent fragment_recent = new Fragment_recent();
    private final Fragment_library fragment_library = new Fragment_library();
    Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mangadex.init();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Manga Reader");
        
        setSupportActionBar(toolbar);
        SetPermissions();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        //Allocate cache for Network
        try {
            File httpCacheDir = new File(this.getApplicationContext().getCacheDir(), "http");
            long httpCacheSize = 50 * 1024 * 1024; // 50 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException ignored) {}

        currentFragment = fragment_recent;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_toolbar, menu);

        MenuItem searchViewItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query)
                    {
                        Fragment_search fragment_search = new Fragment_search(query);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment_search).commit();
                        return false;
                    }
                    public boolean onQueryTextChange(String newText)
                    {

                        return false;
                    }
                });
        searchViewItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                searchView.setQueryHint("Search for Manga by name");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                searchView.clearFocus();
                searchView.setQuery("", false);
                searchView.setFocusable(false);
                //searchView.clearFocus();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    //make navigationbar clickable, navigation between fragments
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                switch (item.getItemId()){
                    case R.id.bt_recent:
                        currentFragment = fragment_recent;
                        break;
                    case R.id.bt_browse:
                        currentFragment = fragment_browse;
                        break;
                    case R.id.bt_library:
                        currentFragment = fragment_library;
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();
                return true;
            };


    //ask user for read and write and internet permissions
    private void SetPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
        }
    }
}