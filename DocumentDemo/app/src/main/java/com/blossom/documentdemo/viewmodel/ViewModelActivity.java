package com.blossom.documentdemo.viewmodel;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.blossom.documentdemo.R;

import java.util.List;

public class ViewModelActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, ViewModelActivity.class);
        context.startActivity(intent);
    }

    private TextView content, activity;
    private FrameLayout containerOne, containerTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_model);
        activity = findViewById(R.id.activity);
        content = findViewById(R.id.content);
        containerOne = findViewById(R.id.container_one);
        containerTwo = findViewById(R.id.container_two);
        ArticleList articleList = ViewModelProviders.of(this).get(ArticleList.class);
        articleList.getArticleList().observe(this, new Observer<List<Article>>() {
            @Override
            public void onChanged(@Nullable List<Article> articles) {
                for (int i = 0; i < articles.size(); i++) {
                    Article article = articles.get(i);
                    String contents = article.getTitle() + "\n" + article.getContent();
                    if (article.isFllowed()) {
                        contents += "\n" + "我被关注了";
                    } else {
                        contents += "\n" + "我被取消关注了";
                    }
                    content.setText(contents);
                }
            }
        });
        initFragments();
        activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArticleActivity.start(ViewModelActivity.this);
            }
        });
    }

    private void initFragments() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container_one, new FragmentOne());
        fragmentTransaction.replace(R.id.container_two, new FragmentTwo());
        fragmentTransaction.commit();
    }
}
