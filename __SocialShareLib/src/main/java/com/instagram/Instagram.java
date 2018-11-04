package com.instagram;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.File;

public class Instagram {

    public enum Type {
        IMAGE, VIDEO
    }

    /**
     * Create chooser dialog.
     * <p>Image requirements: jpeg, gif, png </p>
     * <p>Video requirements:
     * <ul>
     *     <li>Minimum duration: 3 seconds</li>
     *     <li>Maximum duration: 10 minutes</li>
     *     <li>Video format: mkv, mp4</li>
     *     <li>Min. dimensions: 640x640 px</li>
     * </ul></p>
     *
     * @param context The context to use.
     * @param type {@link com.instagram.Instagram.Type } specify share type (IMAGE or VIDEO)
     * @param mediaPath media path on device
     * @param caption caption (media description)
     *
     * <p>Example usage:</p>
     * <pre>{@code public void share(View v) {
         String type = "image/*";
         String filename = "/myPhoto.jpg";
         String mediaPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + filename;
         String captionText = ".__.";
         Instagram.createInstagramIntent(this, Instagram.Type.IMAGE, mediaPath, captionText);
     }

    }
     * </pre>
     */
    public static void createInstagramIntent(Context context, Type type, String mediaPath, String caption) {
        Intent share = new Intent(Intent.ACTION_SEND);
        switch (type) {
            case IMAGE:
                share.setType("image/*");
                break;
            case VIDEO:
                share.setType("video/*");
                break;
            default:
                Log.e("Instagram", "Invalid share type");
                return;
        }
        File media = new File(mediaPath);
        Uri uri = Uri.fromFile(media);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.putExtra(Intent.EXTRA_TEXT, caption);
        context.startActivity(Intent.createChooser(share, "Share to"));
    }
}
