package com.simpmangareader.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.HandlerCompat;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.simpmangareader.R;
import com.simpmangareader.provider.data.Chapter;
import com.simpmangareader.provider.mangadex.Mangadex;

public class ReaderFragment extends DialogFragment  implements SeekBar.OnSeekBarChangeListener{

    private Chapter chapter;
    private Bitmap[] pagesBitmap;
    private boolean[] isPageLoading;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView lblCount, lblTitle, lblDate;
    private LinearLayout lnBottom,lnTop;
    private int selectedPosition = 0;
    SeekBar seekBar;
    private final Handler myHandler = HandlerCompat.createAsync(Looper.myLooper());

    //singleton pattern
    static ReaderFragment newInstance() {
        ReaderFragment f = new ReaderFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.image_slider, container, false);
        viewPager =  v.findViewById(R.id.viewpager);
        lblCount =  v.findViewById(R.id.lbl_count);
        lblTitle =  v.findViewById(R.id.title);
        lblDate =  v.findViewById(R.id.date);
        lnBottom = v.findViewById(R.id.lnBottom);
        lnTop = v.findViewById(R.id.lnTop);
        seekBar = v.findViewById(R.id.seekBar);

        //get data from prev
        assert getArguments() != null;
        chapter = (Chapter) getArguments().getSerializable("manga");
        selectedPosition = getArguments().getInt("position");


        pagesBitmap = new Bitmap[chapter.getPageCount()];
        isPageLoading = new boolean[chapter.getPageCount()];

        seekBar.setMax(pagesBitmap.length - 1);
        seekBar.setOnSeekBarChangeListener(this);
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPosition);
        for (int i =0; i < isPageLoading.length; ++i) isPageLoading[i] = true;
        Mangadex.FetchAllChapterPictures(chapter, (pageNumber, pageImage) -> {
            synchronized (pagesBitmap) {
                pagesBitmap[pageNumber] = pageImage;
            }
            synchronized (isPageLoading)
            {
                isPageLoading[pageNumber] = false;
            }
            synchronized (viewPager) {
                viewPager.post(()->{viewPager.setAdapter(myViewPagerAdapter);});
            }
        }, (e, pageNumber) -> {
            synchronized (isPageLoading)
            {
                isPageLoading[pageNumber] = false;
            }
        }, myHandler);


        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(position);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
            seekBar.setProgress(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void displayMetaInfo(int position) {
        lblCount.setText((position + 1) + " of " + chapter.getPageCount());
        lblTitle.setText(chapter.title);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    //may use this method to load the next bitmap directly instead of waiting for the thread to randomly do it
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @NonNull
        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.activity_reader, container, false);

            final SubsamplingScaleImageView imageViewPreview = view.findViewById(R.id.image_preview);

            if (pagesBitmap[position] == null) {
                if (!isPageLoading[position])
                {
                    isPageLoading[position] = true;
                    // page didn't load wit hall the pages, so try reloading it individually
                    Mangadex.FetchChapterPicture(chapter, position, pageImage -> {
                        synchronized (imageViewPreview) {
                            imageViewPreview.post(() -> {
                                imageViewPreview.setImage(ImageSource.bitmap(pageImage));
                            });
                        }
                        synchronized (pagesBitmap) {
                            pagesBitmap[position] = pageImage;
                        }
                        synchronized (isPageLoading)
                        {
                            isPageLoading[position] = false;
                        }
                    }, e -> {
                        //TODO: report failure
                        synchronized (isPageLoading)
                        {
                            isPageLoading[position] = false;
                        }
                    }, myHandler);
                }
            }
            else
            {
                imageViewPreview.setImage(ImageSource.bitmap(pagesBitmap[position]));
            }

            container.addView(view);
            imageViewPreview.setOnClickListener(v -> {
                if(lnBottom.getVisibility() == View.INVISIBLE){
                    lnBottom.setVisibility(View.VISIBLE);
                    lnTop.setVisibility(View.VISIBLE);
                }

                else{
                    lnBottom.setVisibility(View.INVISIBLE);
                    lnTop.setVisibility(View.INVISIBLE);
                }

            });

            return view;
        }

        @Override
        public int getCount() {
            return pagesBitmap.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}