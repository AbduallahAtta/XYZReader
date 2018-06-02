package com.example.xyzreader.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by AbdullahAtta on 5/31/2018.
 */
public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ArticleListViewHolder> {
    private Context mContext;
    private Cursor mCursor;
    private ArticleClickListener mArticleClickListener;

    public ArticleListAdapter(Context mContext, Cursor mCursor, ArticleClickListener listener) {
        this.mContext = mContext;
        this.mCursor = mCursor;
        this.mArticleClickListener = listener;
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(ArticleLoader.Query._ID);
    }

    @NonNull
    @Override
    public ArticleListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View articleListView = LayoutInflater.from(mContext).inflate(R.layout.list_item_article, parent, false);
        final ArticleListViewHolder holder = new ArticleListViewHolder(articleListView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri articleUri = ItemsContract.Items.buildItemUri(getItemId(holder.getAdapterPosition()));
                mArticleClickListener.onArticleClickListener(ItemsContract.Items.getItemId(articleUri),
                        holder.mArticleThumbnailImageView);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleListViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        int titleIndex = ArticleLoader.Query.TITLE;
        int thumbnailIndex = ArticleLoader.Query.THUMB_URL;
        int authorIndex = ArticleLoader.Query.AUTHOR;

        String title = mCursor.getString(titleIndex);
        String thumbnailUrl = mCursor.getString(thumbnailIndex);
        String author = mCursor.getString(authorIndex);

        Glide.with(mContext)
                .load(thumbnailUrl)
                .into(holder.mArticleThumbnailImageView);
        holder.mArticleTitleTextView.setText(title);
        holder.mArticleSubtitleTextView.setText(author);
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }


    public interface ArticleClickListener {
        void onArticleClickListener(long articleId, View view);
    }

    public class ArticleListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.thumbnailImage)
        ImageView mArticleThumbnailImageView;
        @BindView(R.id.articleTitleTextView)
        TextView mArticleTitleTextView;
        @BindView(R.id.articleSubtitleTextView)
        TextView mArticleSubtitleTextView;

        public ArticleListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
