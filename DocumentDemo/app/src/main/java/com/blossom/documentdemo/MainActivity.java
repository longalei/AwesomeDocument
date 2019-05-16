package com.blossom.documentdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.blossom.documentdemo.glide.GlideActivity;
import com.blossom.documentdemo.viewmodel.ViewModelActivity;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.launch);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        launchMedia();
                    } else {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                    }
                }
            }
        });
    }

    public void launchViewModel(View view) {
        ViewModelActivity.start(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {

        }
    }

    private String mTempFilePath;

    private void launchMedia() {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        if (makeDirs(getSaveFilePath())) {
            mTempFilePath =
                    getSaveFilePath() + File.separator + UUID.randomUUID().toString() + ".jpeg";
            Uri imageUri = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".provider",
                    new File(mTempFilePath));
            // 部分手机在7.0版本以下也做了私有目录权限控制，必须使用content://协议。比如一加 ONE E1001这里就无需版本判断
            // 低版本Provider进行授权
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
            }
            intent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }
        startActivityForResult(intent, 100);
    }

    private static boolean makeDirs(String dirPath) {
        File file = new File(dirPath);
        boolean success = file.mkdirs();
        return success || (file.exists() && file.isDirectory());
    }

    private String getSaveFilePath() {
        File fileDir = getCacheDir();
        return fileDir.getPath();
    }

    public void launchGlide(View view) {
        GlideActivity.start(this);
    }
}
