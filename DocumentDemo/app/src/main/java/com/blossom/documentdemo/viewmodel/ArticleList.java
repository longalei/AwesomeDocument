package com.blossom.documentdemo.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ArticleList extends ViewModel {

    private MutableLiveData<List<Article>> articleList;

    public LiveData<List<Article>> getArticleList() {
        if (articleList == null) {
            articleList = new MutableLiveData<List<Article>>();
            loadArticles();
            Log.e("tag", "getArticleList: wssssssssss" );
        }
        return articleList;
    }

    private void loadArticles() {
        List<Article> articles = new ArrayList<>();
        Article article = new Article();
        article.setTitle("这是一条标题");
        article.setContent("这是一条内容");
        articles.add(article);
        articleList.setValue(articles);
    }


    public void attend() {
        if (articleList != null) {
            List<Article> value = articleList.getValue();
            if (value != null && !value.isEmpty()) {
                value.get(0).setFllowed(true);
                articleList.postValue(value);
            }
        }

    }


    public void cancelAttend() {
        if (articleList != null) {
            List<Article> value = articleList.getValue();
            if (value != null && !value.isEmpty()) {
                value.get(0).setFllowed(false);
                articleList.setValue(value);
            }
        }
    }
}
