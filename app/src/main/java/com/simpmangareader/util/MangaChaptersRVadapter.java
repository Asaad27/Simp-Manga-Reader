package com.simpmangareader.util;

import android.graphics.Bitmap;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simpmangareader.R;
import com.simpmangareader.provider.data.Chapter;

import static android.content.ContentValues.TAG;

public class MangaChaptersRVadapter extends RecyclerView.Adapter<MangaChaptersRVadapter.MangaChapterViewHolder> {

    private Chapter[] chapters;
    private int recent;

    /** params : recent : o if rv in chapter_detail, 1 if rv in fragment recent **/
    public MangaChaptersRVadapter(Chapter[] chapters, int recent) {
        this.chapters = chapters;
        this.recent = recent;

    }



    @NonNull
    @Override
    public MangaChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (recent == 1)
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.manga_listview, parent, false);
        else
            view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.manga_detail_chapters, parent, false);

        return new MangaChapterViewHolder(view, recent);
    }

    public Spanned htmlFonts(String s, String tag, String closingTag){
        Spanned res = Html.fromHtml( tag + s +closingTag);
        return res;
    }
    @Override
    public void onBindViewHolder(@NonNull MangaChapterViewHolder holder, int position) {
        if (chapters[position] == null) {
            holder.getChapter_detail().setText("chapter loading");
            return;
        }
        String title = chapters[position].MangaTitle;
        String detail =  (title != null ? htmlFonts(title, "<b>", "</b>") + "\n" : "") +  htmlFonts("Chapter : ", "<b>", "</b>") + chapters[position].chapterNumber  + "\n" + chapters[position].title ;
        holder.getChapter_detail().setText(detail);

        if(recent == 1) {
            Bitmap bitmap = BitmapConverter.getBitmapFromString(chapters[position].CoverBitmapEncoded);
            if (bitmap != null)
                holder.getChapter_cover().setImageBitmap(bitmap);
        }
    }



    @Override
    public int getItemCount() {
        if (chapters == null) return 0;
        return chapters.length;
    }

    public static class MangaChapterViewHolder extends RecyclerView.ViewHolder{

        private TextView chapter_detail;
        private int recent;
        private ImageView chapter_cover;

        public MangaChapterViewHolder(@NonNull View itemView, int recent) {
            super(itemView);
            this.recent = recent;

            if (recent == 1) {
                chapter_cover = itemView.findViewById(R.id.manga_image_view_listview);
                chapter_detail = itemView.findViewById(R.id.manga_text_view_listview);
            }
            else {
                chapter_detail = itemView.findViewById(R.id.chapter_detail);
            }

        }

        public TextView getChapter_detail() {
            return chapter_detail;
        }

        public void setChapter_detail(TextView chapter_detail) {
            this.chapter_detail = chapter_detail;
        }

        public ImageView getChapter_cover() {
            return chapter_cover;
        }

        public void setChapter_cover(ImageView chapter_cover) {
            this.chapter_cover = chapter_cover;
        }
    }
    public void setChapters(Chapter[] chapters){
        this.chapters = chapters;
    }
}

