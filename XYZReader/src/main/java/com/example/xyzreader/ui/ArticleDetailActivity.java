package com.example.xyzreader.ui;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;

import static com.example.xyzreader.ui.ArticleListActivity.ARTICLE_INTENT_KEY;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity {

    private long mStartId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenWindow();
        setContentView(R.layout.activity_article_detail);
        if (getIntent() != null && getIntent().hasExtra(ARTICLE_INTENT_KEY)) {
            mStartId = getIntent().getLongExtra(ARTICLE_INTENT_KEY, ArticleLoader.Query.DEFAULT_VALUE);

        } else {
            errorUponLaunch();
        }
        setupArticleDetailsFragment();
    }

    private void errorUponLaunch() {
        Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void setupArticleDetailsFragment() {
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setmItemId(mStartId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.articleDetailContainer, fragment)
                .commit();
    }

    private void setFullScreenWindow() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
