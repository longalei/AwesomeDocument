package com.blossom.documentdemo.glide;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.blossom.documentdemo.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.security.MessageDigest;

public class GlideActivity extends AppCompatActivity {

    private Button dialog;
    private String url = "https://avatars0.githubusercontent.com/u/13430267?s=460&v=4";

    private ImageView imageview1, imageview2;

    private static final String TAG = "GlideActivity";

    public static void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, GlideActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glide);
        dialog = findViewById(R.id.dialog);
        imageview1 = findViewById(R.id.imageview1);
        imageview2 = findViewById(R.id.imageview2);
        dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).transform(new WhiteBitmap() {
                    @Override
                    public void updateDiskCacheKey(MessageDigest messageDigest) {
                        super.updateDiskCacheKey(messageDigest);
                        Log.e(TAG, "updateDiskCacheKey:  requestOptions" + messageDigest);
                    }
                });

            }
        });

        RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).transform(new RoundedCorners(10));
        Glide.with(GlideActivity.this).load(url)
                .apply(requestOptions).into(imageview1);
        RequestOptions requestOptionsOne = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).transform(new RoundedCorners(10));
        Glide.with(GlideActivity.this).load(url)
                .apply(requestOptionsOne).into(imageview2);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e("tag", "onSaveInstanceState: ");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e("tag", "onRestoreInstanceState: ");
    }
}
