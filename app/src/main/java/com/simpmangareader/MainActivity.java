package com.simpmangareader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar main_toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(main_toolbar);   //make the toolbar act like an actionbar
        try                                  //remove app name from toolbar
        {
            getSupportActionBar().setTitle("simpmanga shelf");
        }
        catch (NullPointerException ignored){}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }



}