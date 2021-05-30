package com.simpmangareader.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.HandlerCompat;
import androidx.fragment.app.Fragment;

import com.simpmangareader.R;
import com.simpmangareader.provider.mangadex.Mangadex;

public class Fragment_browse extends Fragment {
    private Handler myHandler = HandlerCompat.createAsync(Looper.myLooper());
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //TODO: the data will not be immediately available, we need to display something until the data is ready...
        Mangadex.FetchManga(0, 10, result -> {
            //TODO: update UI
            //NOTE(Mouad): result is an array of Manga
            Toast.makeText(getContext(),"Manga size : " + result.length, Toast.LENGTH_LONG).show();
        }, e -> {
            //TODO: report failure
        }, myHandler);
        return inflater.inflate(R.layout.fragment_browse, container, false);
    }
}
