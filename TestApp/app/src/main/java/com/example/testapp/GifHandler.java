package com.example.testapp;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;


public class GifHandler {

    private Context context;

    public GifHandler(Context context) {
        this.context = context;
    }

    public void shareGif(String imageUrl) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/gif");
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imageUrl));
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Intent chooser = Intent.createChooser(shareIntent, "Share GIF with");
            if (shareIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(chooser);
            } else {
                Toast.makeText(context, "No app available to share GIF", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Error sharing GIF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void downloadGifToGallery(String imageUrl) {
        try {
            Uri downloadUri = Uri.parse(imageUrl);
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "GIFs/downloade.gif");
            request.setMimeType("image/gif");
            if (downloadManager != null) {
                downloadManager.enqueue(request);
                Toast.makeText(context, "Downloading GIF...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Download not available", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
