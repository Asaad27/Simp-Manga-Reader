package com.simpmangareader.util;

import android.os.Looper;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.os.HandlerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.simpmangareader.R;
import com.simpmangareader.provider.data.Manga;
import com.simpmangareader.provider.mangadex.Mangadex;

import java.util.ArrayList;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private final int VIEW_TYPE_MANGA_ITEM = 0;
    private final int VIEW_TYPE_LOADING_ITEM = 1;
    private ArrayList<Manga> mData;

    public RecyclerViewAdapter(ArrayList<Manga> mData) {
        this.mData = mData;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == VIEW_TYPE_MANGA_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.manga_cardview, viewGroup, false);
            return new MangaViewHolder(view);
        }
        else if (viewType == VIEW_TYPE_LOADING_ITEM)
        {
            // LOADING VIEW
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_loading, viewGroup, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position) == null ? VIEW_TYPE_LOADING_ITEM : VIEW_TYPE_MANGA_ITEM;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MangaViewHolder)
        {
            onBindViewHolder((MangaViewHolder) holder, position);
        }
        else if (holder instanceof  LoadingViewHolder)
        {

        }
    }

    private void onBindViewHolder(MangaViewHolder holder, final int position) {
        Manga manga = mData.get(position);
        ImageView img = holder.getImg_book_thumbnail();
        if (manga.cover != null)
        {
            img.setImageBitmap(manga.cover);
            holder.isCoverLoading = false;
        }
        else {
            //clear the image view and wait for the data to arrive from the API
            if (!holder.isCoverLoading) {
                holder.isCoverLoading = true;
                img.setImageBitmap(null);
                Mangadex.GetMangaCover(manga.id, cover -> {
                    synchronized (img) {
                        img.post(() -> {
                            img.setImageBitmap(cover);
                            notifyDataSetChanged();
                        });
                    }
                    manga.cover = cover;
                }, e -> {
                    //TODO: report failure
                }, HandlerCompat.createAsync(Looper.getMainLooper()));
            }
        }
        holder.getTv_manga_title().setText(Html.fromHtml( "<b>"+mData.get(position).title+"</b>"));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MangaViewHolder extends RecyclerView.ViewHolder {
        public boolean isCoverLoading = false;
        private TextView tv_manga_title;
        private ImageView img_book_thumbnail;


        public MangaViewHolder(View itemView) {
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

    public static class LoadingViewHolder extends RecyclerView.ViewHolder{
        ProgressBar progress;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            this.progress = itemView.findViewById(R.id.progressBar);
        }
    }
}
