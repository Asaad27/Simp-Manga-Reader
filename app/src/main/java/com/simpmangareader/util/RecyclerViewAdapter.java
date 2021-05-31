package com.simpmangareader.util;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.os.HandlerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.simpmangareader.R;
import com.simpmangareader.provider.data.Manga;
import com.simpmangareader.provider.mangadex.Mangadex;

import java.util.ArrayList;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>  {

    private ArrayList<Manga> mData;

    public RecyclerViewAdapter(ArrayList<Manga> mData) {
        this.mData = mData;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.manga_cardview, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Manga manga = mData.get(position);
        ImageView img = holder.getImg_book_thumbnail();
        if (manga.cover != null)
        {
            img.setImageBitmap(manga.cover);
        }
        else {
            Mangadex.GetMangaCover(manga.id, cover -> {
                synchronized (img) {
                    img.post(()->{
                        img.setImageBitmap(cover);
                    });
                }
                manga.cover = cover;
            }, e -> {
                //TODO: report failure
            }, HandlerCompat.createAsync(Looper.getMainLooper()));
        }
        holder.getTv_manga_title().setText(mData.get(position).title);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_manga_title;
        private ImageView img_book_thumbnail;


        public MyViewHolder(View itemView) {
            super(itemView);

            tv_manga_title= (TextView) itemView.findViewById(R.id.manga_text_view_cardview) ;
            img_book_thumbnail = (ImageView) itemView.findViewById(R.id.manga_image_view_cardview);

        }

        public TextView getTv_manga_title() {
            return tv_manga_title;
        }

        public void setTv_manga_title(TextView tv_manga_title) {
            this.tv_manga_title = tv_manga_title;
        }

        public ImageView getImg_book_thumbnail() {
            return img_book_thumbnail;
        }

        public void setImg_book_thumbnail(ImageView img_book_thumbnail) {
            this.img_book_thumbnail = img_book_thumbnail;
        }


    }

}
