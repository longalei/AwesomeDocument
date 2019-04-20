package com.blossom.documentdemo.viewmodel;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.blossom.documentdemo.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentOne extends Fragment {

    private Button button;
    private TextView content;
    private ArticleList articleList;

    public FragmentOne() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_fragment_one, container, false);
        button = root.findViewById(R.id.button);
        content = root.findViewById(R.id.content);
        articleList = ViewModelProviders.of(getActivity()).get(ArticleList.class);
        articleList.getArticleList().observe(getActivity(), new Observer<List<Article>>() {
            @Override
            public void onChanged(@Nullable List<Article> articles) {
                for (int i = 0; i < articles.size(); i++) {
                    Article article = articles.get(i);
                    String contents = "另一个Activity中的数据：" + article.getTitle() + "\n" + article.getContent();
                    if (article.isFllowed()) {
                        contents += "\n" + "我被关注了";
                    } else {
                        contents += "\n" + "我被取消关注了";
                    }
                    content.setText(contents);
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attention();
            }
        });
        return root;
    }


    public void attention() {
        articleList.attend();
    }

}
