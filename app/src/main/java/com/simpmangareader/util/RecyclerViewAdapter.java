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
    private Context mContext ;
    private List<MangaDetail> mData;


    public RecyclerViewAdapter(Context mContext, List<MangaDetail> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.manga_cardview ,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.tv_manga_title.setText(mData.get(position).getTitle());
        holder.img_book_thumbnail.setImageResource(mData.get(position).getThumbnail());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // we will transfer everything to the reader activity to continue reading the chapter

            }
        });



    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_manga_title;
        TextView tv_chapter_title;
        ImageView img_book_thumbnail;
        CardView cardView ;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_manga_title= (TextView) itemView.findViewById(R.id.manga_image_view_title) ;
            img_book_thumbnail = (ImageView) itemView.findViewById(R.id.manga_image_view);
            cardView = (CardView) itemView.findViewById(R.id.cardview_id);


        }
    }

}
