package com.simpmangareader.util;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.simpmangareader.R;
import com.simpmangareader.activities.Fragment_recent;
import com.simpmangareader.provider.data.MangaDetail;

import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>  {

    private List<MangaDetail> mData;

    public RecyclerViewAdapter(List<MangaDetail> mData) {
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

        holder.getImg_book_thumbnail().setImageResource(mData.get(position).getThumbnail());
        holder.getTv_manga_title().setText(mData.get(position).getTitle());

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
