package com.example.xyzreader.ui;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.example.xyzreader.R;
import com.example.xyzreader.adapters.ArticleListAdapter;
import com.example.xyzreader.data.AddItems;
import com.example.xyzreader.data.ArticleLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, ArticleListAdapter.ArticleClickListener {

    public static final String ARTICLE_INTENT_KEY = "com.example.xyzreader.article_intent";

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.articleListCoordinatorLayout)
    CoordinatorLayout mArticlesListParentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            initLoader();
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    private boolean isThereInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            deployArticles(data);
        } else if (isThereInternetConnection()) {
            addArticlesToDatabase();
        } else {
            Snackbar.make(mArticlesListParentLayout, R.string.missing_connection_message, Snackbar.LENGTH_LONG).show();
        }
    }

    private void initLoader() {
        getSupportLoaderManager().initLoader(0, null, this);
    }

    private void addArticlesToDatabase() {
        startService(new Intent(this, AddItems.class));
    }

    private void deployArticles(Cursor data) {
        ArticleListAdapter adapter = new ArticleListAdapter(this, data, this);
        adapter.setHasStableIds(true);
        int columnsCount = getResources().getInteger(R.integer.columns_count_list);
        GridLayoutManager layoutManager =
                new GridLayoutManager(this, columnsCount);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
        runRecyclerViewAnimation();
    }

    private void runRecyclerViewAnimation() {
        int slideUpLayoutAnimation = R.anim.slide_up_layout_animation;
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(this, slideUpLayoutAnimation);
        mRecyclerView.setLayoutAnimation(animationController);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    @Override
    public void onArticleClickListener(long articleId, View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            makeSharedElementsTransitions(articleId, view);
        } else {
            startArticleDetailActivity(articleId);
        }
    }

    @SuppressLint("NewApi")
    private void makeSharedElementsTransitions(long articleId, View view) {
        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this, view, view.getTransitionName()).toBundle();
        Intent articleIntent = new Intent(this, ArticleDetailActivity.class);
        articleIntent.putExtra(ARTICLE_INTENT_KEY, articleId);
        ArticleListActivity.this.startActivity(articleIntent, bundle);
    }

    private void startArticleDetailActivity(long articleId) {
        Intent articleIntent = new Intent(this, ArticleDetailActivity.class);
        articleIntent.putExtra(ARTICLE_INTENT_KEY, articleId);
        startActivity(articleIntent);
    }
}
