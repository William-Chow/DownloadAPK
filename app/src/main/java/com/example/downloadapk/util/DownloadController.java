package com.example.downloadapk.util;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.downloadapk.BuildConfig;
import com.example.downloadapk.R;

import java.io.File;

public class DownloadController {

    private static String FILE_NAME = "SampleDownloadApp.apk";
    private static String FILE_BASE_PATH = "file://";
    private static String MIME_TYPE = "application/vnd.android.package-archive";
    private static String PROVIDER_PATH = ".provider";
    private static String APP_INSTALL_PATH = "\"application/vnd.android.package-archive\"";

    public static void enqueueDownload(Context context, String url) {
        String destination = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/";
        destination = destination + FILE_NAME;
        Uri uri = Uri.parse(FILE_BASE_PATH + destination);
        File file = new File(destination);
        if (file.exists()) {
            file.delete();
        }

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setMimeType(MIME_TYPE);
        request.setTitle(context.getString(R.string.title_file_download));
        request.setDescription(context.getString(R.string.downloading));

        request.setDestinationUri(uri);

        showInstallOption(destination, uri, context);
        assert downloadManager != null;
        downloadManager.enqueue(request);
        Toast.makeText(context, context.getString(R.string.downloading), Toast.LENGTH_LONG).show();
    }

    private static void showInstallOption(final String destination, final Uri uri, Context context){
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Build.VERSION.SDK_INT >= 24) {
                    Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + PROVIDER_PATH, new File(destination));
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                    install.setData(contentUri);
                    context.startActivity(install);
                    context.unregisterReceiver(this);
                } else {
                    Intent installx = new Intent(Intent.ACTION_VIEW);
                    installx.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    installx.setDataAndType(uri, APP_INSTALL_PATH);
                    context.startActivity(installx);
                    context.unregisterReceiver(this);
                }
            }
        };
        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
}
