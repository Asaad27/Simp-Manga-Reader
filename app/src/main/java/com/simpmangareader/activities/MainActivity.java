package com.simpmangareader.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.os.HandlerCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.simpmangareader.R;
import com.simpmangareader.provider.mangadex.Mangadex;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.*;

public class MainActivity extends AppCompatActivity {
    //create a thread pool
    public static ExecutorService executorService = Executors.newFixedThreadPool(4);
    private Handler myHandler = HandlerCompat.createAsync(Looper.myLooper());

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SetPermissions();

        Toolbar main_toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(main_toolbar);   //make the toolbar act like an actionbar
        try                                  //remove app name from toolbar
        {
            Objects.requireNonNull(getSupportActionBar()).setTitle("lol");
        }
        catch (NullPointerException ignored){}

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        //Allocate cache for Network
        try {
            File httpCacheDir = new File(this.getApplicationContext().getCacheDir(), "http");
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
            Log.i("TAG", "HTTP response cache installation failed:" + e);
        }
        Mangadex.init(this.getApplicationContext(), executorService);

        //start fragment
        Fragment fragment = new Fragment_recent();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
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
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //make navigationbar clickable, navigation between fragments
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
        item -> {
            Fragment selectedFragment = null;

            switch (item.getItemId()){
                case R.id.bt_recent:
                    selectedFragment = new Fragment_recent();
                    break;
                case R.id.bt_browse:
                    selectedFragment = new Fragment_browse();
                    break;
                case R.id.bt_library:
                    selectedFragment = new Fragment_library();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

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