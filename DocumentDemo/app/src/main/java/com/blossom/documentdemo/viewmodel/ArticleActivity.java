package com.blossom.documentdemo.viewmodel;

import android.arch.lifecycle.ViewModelProviders;
import android.arch.lifecycle.ViewModelStore;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.blossom.documentdemo.R;

public class ArticleActivity extends AppCompatActivity {

    private static Context mContext;

    public static void start(Context context) {
        Intent intent = new Intent();
        mContext = context;
        intent.setClass(context, ArticleActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
    }

    public void cancelAttention(View view) {
        //Todo 这里需要优雅的获取ViewModelActivity的对象，比如通过AMS？栈？
        ArticleList articleList = ViewModelProviders.of((FragmentActivity) mContext).get(ArticleList.class);
        articleList.cancelAttend();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return super.onRetainCustomNonConfigurationInstance();
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return super.getViewModelStore();
    }
}
