package com.sharelib;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.File;

import com.lyn.sharelib.R;
import com.sharelib.Store.SharedPreferencesCredentialStore;
import com.sharelib.Util.DownloadImagesTask;
import com.sharelib.Util.DownloadListener;

/**
 * This class provides static methods to easily use sharing options with Twitter.
 *
 * <p>No activity needs to be declared in AndroidManifest.xml</p>
 *
 * <p>Needs INTERNET permission.</p>
 */
public final class TwitterShare {
    private TwitterShare() {
    }

    /**
     * Make a tweet preview dialog.
     * If the user is unauthorized, also show an authorization dialog.
     *
     * @param context The context to use. Usually your android.app.Application or android.app.Activity object.
     * @param text    The text to show. Note that only first 140 characters will be shown.
     */
    public static void makeTweet(Context context, String text) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if (!TwitterUtils.tokensExist(prefs)) {
            new OAuthAccessTokenDialog(context, text).show();
        } else {
            TwitterDialog dialog = new TwitterDialog(context);
            dialog.setText(text);
            dialog.show();
        }
    }

    /**
     * Make a tweet preview dialog with images.
     * If the user is unauthorized, also show an authorization dialog.
     *
     * @param context The context to use. Usually your android.app.Application or android.app.Activity object.
     * @param text    The text to show.
     * @param images  Image(s) to upload.
     */
    public static void makeTweet(Context context, String text, File[] images) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if (!TwitterUtils.tokensExist(prefs)) {
            new OAuthAccessTokenDialog(context, text, images, false).show();
        } else {
            TwitterDialog dialog = new TwitterDialog(context);
            dialog.setText(text);
            dialog.setImages(images, false);
            dialog.show();
        }
    }

    /**
     * Make a tweet preview dialog with images from URLs.
     * @param context The context to use. Usually your android.app.Application or android.app.Activity object.
     * @param text The text to show.
     * @param url  URLs of images (images then will be removed if dialog finishes normally).
     */
    public static void makeTweet(final Context context, final String text, String[] url) {
        new DownloadImagesTask(context, url, new DownloadListener() {
            @Override
            protected void onComplete(File[] images) {
                if (!TwitterUtils.tokensExist(PreferenceManager.getDefaultSharedPreferences(context))) {
                    new OAuthAccessTokenDialog(context, text, images, true).show();
                } else {
                    TwitterDialog dialog = new TwitterDialog(context);
                    dialog.setText(text);
                    dialog.setImages(images, true);
                    dialog.show();
                }
            }

            @Override
            protected void onDownloadError(Exception e) {

            }
        }).execute();
    }

    /**
     * Logout from Twitter.
     * @param context The context to use. Usually your android.app.Application or android.app.Activity object.
     */
    public static void logout(Context context) {
    	new SharedPreferencesCredentialStore(PreferenceManager.getDefaultSharedPreferences(context)).clearCredentials();
    	Toast.makeText(context, R.string.twitter_logout_text, Toast.LENGTH_LONG).show();
    }
}