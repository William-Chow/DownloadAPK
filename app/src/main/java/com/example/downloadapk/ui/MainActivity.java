package com.example.downloadapk.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.downloadapk.R;
import com.example.downloadapk.util.DownloadController;
import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.rlMainLayout)
    RelativeLayout rlMainLayout;

    @BindView(R.id.btnDownload)
    Button btnDownload;

    public static final int PERMISSION_REQUEST_STORAGE = 0;
    String apkUrl = "https://androidwave.com/source/apk/app-pagination-recyclerview.apk";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        btnDownload.setOnClickListener(v -> checkStoragePermission());
    }

    private void checkStoragePermission() {
        if (permissionAlreadyGranted()) {
            DownloadController.enqueueDownload(this, apkUrl);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
        }
    }

    private boolean permissionAlreadyGranted() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    DownloadController.enqueueDownload(this, apkUrl);
                } else {
                    Snackbar.make(rlMainLayout, R.string.storage_permission_denied, Snackbar.LENGTH_SHORT).show();

                    boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (! showRationale) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, 101);
                    }
                }
                return;
            default:
                break;

        }
    }
}
