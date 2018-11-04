package com.sharelib;

import java.io.File;

import com.sharelib.Store.SharedPreferencesCredentialStore;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.UploadedMedia;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.content.SharedPreferences;
import android.util.Log;

class TwitterUtils {
    final static String TAG = "TwitterUtils";

    /**
     * Weak check for authorization
     * @param prefs PreferenceManager.getDefaultSharedPreference(context)
     * @return true if tokens exist in app, false otherwise
     */
    public static boolean tokensExist(SharedPreferences prefs) {
        String[] tokens = new SharedPreferencesCredentialStore(prefs).read();
        return tokens[0].length() != 0 && tokens[1].length() != 0;
    }

    /**
     * Retrieves tokens at tries to get account settings through Internet
     * @param prefs PreferenceManager.getDefaultSharedPreference(context)
     * @return true if tokens valid, false otherwise
     */
    public static boolean isAuthenticated(SharedPreferences prefs) {
        String[] tokens = new SharedPreferencesCredentialStore(prefs).read();
        AccessToken a = new AccessToken(tokens[0],tokens[1]);
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(Constants.TWI_API_KEY, Constants.TWI_API_SECRET);
		twitter.setOAuthAccessToken(a);
		try {
			twitter.getAccountSettings();
			return true;
		} catch (TwitterException e) {
			e.printStackTrace();
		}
        return false;
    }

    public static ResponseList<Status> getHomeTimeline(SharedPreferences prefs) throws Exception {
        String[] tokens = new SharedPreferencesCredentialStore(prefs).read();
        AccessToken a = new AccessToken(tokens[0],tokens[1]);
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(Constants.TWI_API_KEY, Constants.TWI_API_SECRET);
        twitter.setOAuthAccessToken(a);
        return twitter.getHomeTimeline();
    }

    public static Status sendTweet(SharedPreferences prefs, String msg){
        String[] tokens = new SharedPreferencesCredentialStore(prefs).read();
        AccessToken a = new AccessToken(tokens[0],tokens[1]);
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(Constants.TWI_API_KEY, Constants.TWI_API_SECRET);
        twitter.setOAuthAccessToken(a);
        try {
            return twitter.updateStatus(msg);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Status sendTweetWithImages(SharedPreferences prefs, String msg, File[] images) {
        String[] tokens = new SharedPreferencesCredentialStore(prefs).read();
        AccessToken a = new AccessToken(tokens[0], tokens[1]);

        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(Constants.TWI_API_KEY, Constants.TWI_API_SECRET);
        twitter.setOAuthAccessToken(a);

        StatusUpdate newStatus = new StatusUpdate(msg);

        long[] mediaIds = new long[images.length];
        for (int i = 0; i < images.length; ++i) {
            Log.d(TAG, "Uploading...[" + (i + 1) + "/" + (images.length) + "][" + images[i].toString() + "]");
            try {
                UploadedMedia media = twitter.uploadMedia(images[i]);
                Log.d(TAG, "Uploaded: id=" + media.getMediaId() + ", w=" + media.getImageWidth() + ", h=" + media.getImageHeight() + ", type=" + media.getImageType() + ", size=" + media.getSize());
                mediaIds[i] = media.getMediaId();
            } catch (TwitterException e) {
                e.printStackTrace();
                Log.d(TAG, "An error occurred while uploading images");
            }
        }
        newStatus.setMediaIds(mediaIds);

        try {
            return twitter.updateStatus(newStatus);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return null;
    }

}
