package com.simpmangareader.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.simpmangareader.R;

public class Fragment_browse extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //TODO: get the data from the web
        //TODO: the data will not be immediately available, we need to display something until the data is ready...
        return inflater.inflate(R.layout.fragment_browse, container, false);
    }
}
