package com.example.xyzreader.ui;


import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = ArticleDetailFragment.class.getSimpleName();
    @BindView(R.id.articleImageView)
    ImageView mArticleImageView;
    @BindView(R.id.articleDetailsTitleAuthorTextView)
    TextView mArticleTitleTextView;
    @BindView(R.id.articleDetailsBodyTextView)
    TextView mArticleBodyTextView;
    @BindView(R.id.articlePublishedDateTetView)
    TextView mArticleDateTextView;
    @BindView(R.id.collapsingToolbar)
    CollapsingToolbarLayout mArticleCollapsingToolbar;
    @BindView(R.id.toolbar)
    Toolbar mArticleToolbar;
    private Cursor mCursor;
    private long mItemId;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);
    private String mArticleBody;
    private String mArticleAuthor;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }

    public long getmItemId() {
        return mItemId;
    }

    public void setmItemId(long mItemId) {
        this.mItemId = mItemId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
        ButterKnife.bind(this, rootView);
        if (isAdded()) {
            setupActionBar();
        }
        getLoaderManager().initLoader(0, null, this);
        return rootView;
    }

    @OnClick(R.id.shareFab)
    public void shareArticle() {
        if (isAdded()) {
            ShareCompat.IntentBuilder.from(getActivity())
                    .setChooserTitle(R.string.share_article_text)
                    .setType("text/plain")
                    .setText("Check this article by " +
                            "\n" + mArticleAuthor +
                            "\n\n" + mArticleBody)
                    .startChooser();
        }
    }

    private void setupActionBar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mArticleToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private Date parsePublishedDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
            return new Date();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newInstanceForItemId(getActivity(), getmItemId());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (!isAdded()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }
        mCursor = cursor;
        if (mCursor != null && !mCursor.moveToFirst()) {
            mCursor.close();
            mCursor = null;
            return;
        }
        showArticleDetails();
    }

    private void showArticleDetails() {
        mCursor.moveToFirst();
        int imageUrlIndex = ArticleLoader.Query.PHOTO_URL;
        int bodyIndex = ArticleLoader.Query.BODY;
        int titleIndex = ArticleLoader.Query.TITLE;
        int authorIndex = ArticleLoader.Query.AUTHOR;
        int dateIndex = ArticleLoader.Query.PUBLISHED_DATE;

        String imageUrl = mCursor.getString(imageUrlIndex);
        mArticleBody = mCursor.getString(bodyIndex);
        String title = mCursor.getString(titleIndex);
        mArticleAuthor = mCursor.getString(authorIndex);
        String date = mCursor.getString(dateIndex);

        publishDate(date);
        Glide.with(this)
                .load(imageUrl)
                .into(mArticleImageView);

        mArticleBodyTextView.setText(Html.fromHtml(mArticleBody.replaceAll("(\r\n|\n)", "<br />")));
        mArticleTitleTextView.setText(title);
        mArticleTitleTextView.append("\n" + mArticleAuthor);
        mArticleCollapsingToolbar.setTitle(title);
    }

    private void publishDate(String date) {
        Date publishedDate = parsePublishedDate(date);
        if (!publishedDate.before(START_OF_EPOCH.getTime())) {
            mArticleDateTextView.setText(Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            publishedDate.getTime(),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
            ));

        } else {
            // If date is before 1902, just show the string
            mArticleDateTextView.setText(Html.fromHtml(
                    outputFormat.format(publishedDate)));

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
    }

}
