package com.simpmangareader.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simpmangareader.R;
import com.simpmangareader.provider.data.ChapterDetail;
import com.simpmangareader.provider.data.MangaDetail;

import java.util.List;

public class MangaChaptersRVadapter extends RecyclerView.Adapter<MangaChaptersRVadapter.MangaChapterViewHolder> {

    private List<ChapterDetail> mData;

    public MangaChaptersRVadapter(List<ChapterDetail> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public MangaChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.manga_detail_chapters, parent, false);

        return new MangaChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MangaChapterViewHolder holder, int position) {
        String detail = mData.get(position).getNumber() + " " +  mData.get(position).getTitle() ;
        holder.getChapter_detail().setText(detail);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MangaChapterViewHolder extends RecyclerView.ViewHolder{

        private TextView chapter_detail;

        public MangaChapterViewHolder(@NonNull View itemView) {
            super(itemView);

            chapter_detail = itemView.findViewById(R.id.chapter_detail);

        }

        public TextView getChapter_detail() {
            return chapter_detail;
        }

        public void setChapter_detail(TextView chapter_detail) {
            this.chapter_detail = chapter_detail;
        }
    }
}

