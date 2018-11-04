package com.sharelib;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.lyn.sharelib.R;
import com.sharelib.Util.NotificationHelper;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.photo.VKImageParameters;
import com.vk.sdk.api.photo.VKUploadImage;
import com.vk.sdk.dialogs.VKCaptchaDialog;
import com.vk.sdk.dialogs.VKShareDialog;

import java.io.File;

/**
 * To use VK or Facebook sharing options extend your Activity from {@link com.sharelib.SocialShareActivity}.
 *
 * <p>To use Twitter there is a {@link com.sharelib.TwitterShare} class.</p>
 *
 * <p><strong>INTERNET permission required</strong></p>
 * <p>SocialShareActivity needs to be declared in your AndroidManifest.xml</p>
 *
 * <p>For Facebook:</p>
 * <ol>
 *     <li>Register new app at <a href="https://developers.facebook.com/">Facebook developers page</a></li>
 *     <li>Press <strong>Add Platform</strong> in <strong>Settings/Basic</strong> of your newly registered app</li>
 *     <li>You can obtain <strong>Key Hashes</strong> from Java's keytool</li>
 *     <li>Paste obtained key to <strong>Key Hashes</strong> field, then click on: <strong>Single Sign On</strong></li>
 *     <li>Fill <strong>Package Name</strong> and <strong>Class Name</strong> properly, for example:
 *         <ul>
 *             <li><strong>Package Name</strong>: com.example.myapp</li>
 *             <li><strong>Class Name</strong>: com.example.myapp.MyActivity</li>
 *         </ul>
 *     </li>
 *     <li>Go to <strong>Status and Review</strong> and turn your app ON</li>
 *     <li>Create new string value <strong>fb_app_id</strong> and paste <strong>app's ID</strong> as value.</li>
 *     <li>Add <strong>metadata</strong> to your AndroidManifest.xml: name="com.facebook.sdk.ApplicationId" value=yourId</li>
 *     <li>Add activity to your AndroidManifest.xml: <strong>com.facebook.LoginActivity</strong></li>
 *     <li>Now you can use:
 *          <ul>
 *              <li>{@link #makeFacebookPost(String, String, String, String, String)}</li>
 *              <li>{@link #logoutFacebook()}</li>
 *          </ul>
 *     </li>
 * </ol>
 * <p>For VK:</p>
 * <ol>
 *     <li>Add <strong>"com.vk.sdk.VKOpenAuthActivity"</strong> to your AndroidManifest.xml</li>
 *     <li>(OPTIONALLY) Register your own app and paste it's ID to Constants.VK_APP_ID</li>
 *     <li>Now you can use these methods:
 *          <ul>
 *              <li>{@link #makeVKWallPost(String)}</li>
 *              <li>{@link #makeVKWallPost(String, android.graphics.Bitmap[])}</li>
 *              <li>{@link #makeVKWallPost(String, java.io.File[])}</li>
 *              <li>{@link #logoutVK()}</li>
 *          </ul>
 *     </li>
 * </ol>
 *
 * Example usage:
 * <pre>
     {@code public class MainActivity extends SocialShareActivity {
             &#064;Override
             public void onCreate(Bundle savedInstanceState) {
                 super.onCreate(savedInstanceState);
                 setContentView(R.layout.main);
             }

             public void postToVKWall(View view) {
                 String text = "Hi all! " + new Date().toString();
                 makeVKWallPost(text);
             }

             public void postImgToVKWall() {
                 String text = "Hi all! " + new Date().toString();
                 File[] image = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).listFiles();
                 makeVKWallPost(text, image);
             }

             public void postToFB(View view) {
                 String name = "SocialShareLib";
                 String description = "An example of post sharing using Facebook Android SDK";
                 String caption = "Really nice SDK";
                 String link = "https://developers.facebook.com/docs/android/getting-started";
                 String pictureUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR5rh2YJ-5AVlyzppOpnHQsKZa2hfI2m5qZDIkRB4DzrtN0oovxog";

                 makeFacebookPost(name, description, caption, link, pictureUrl);
            }
         }
     }
 * </pre>
 * @see <a href="https://developers.facebook.com/docs/android/getting-started/">Getting Started with the Facebook SDK for Android</a>
 * @see <a href="https://vk.com/dev/android_sdk">VK SDK for Android</a>
 */
public class SocialShareActivity extends FragmentActivity {

    /**
     * Uncomment all occurences of fbUiHelper after you register new Facebook app at https://developers.facebook.com/
     */
    //private UiLifecycleHelper fbUiHelper;

    enum ShareType {
        TEXT, IMAGE
    }
    protected ShareType shareType;
    protected String text;
    protected Bitmap[] image;

    enum PendingAction {
        NONE, POST
    }
    private PendingAction pendingAction = PendingAction.NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // You need to initialize SDK when the application runs using this method.
        VKSdk.initialize(sdkListener, Constants.VK_APP_ID);

        //fbUiHelper = new UiLifecycleHelper(this, null);
        //fbUiHelper.onCreate(savedInstanceState);
    }

    /** UIHelper Applying
     *
     * VKSdk uses new activities' launch and displaying of some dialogs.
     * This requires up to date information about what activity is now on the screen.
     *
     * So for the correct SDK work in all of your activities you should redefine the following methods:
     *      onResume, onDestroy, onActivityResult
     */
    @Override
    protected void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);
        //fbUiHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //fbUiHelper.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
        //fbUiHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //fbUiHelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);

        Session activeSession = Session.getActiveSession();
        if (activeSession != null) activeSession.onActivityResult(this, requestCode, resultCode, data);

        //fbUiHelper.onActivityResult(requestCode, resultCode, data);
    }

    private void onClickPostStatus(String name, String description, String caption, String link, String pictureURL) {
        Log.e("Onclickpoststatus","ENTERED");
        if (FacebookDialog.canPresentShareDialog(getApplicationContext(), FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
            FacebookDialog.ShareDialogBuilder shareDialogBuilder = new FacebookDialog.ShareDialogBuilder(this);
            Log.e("Onclickpoststatus","DIALOG BUILT");
            if (name != null)
                shareDialogBuilder.setName(name);

            if (description != null)
                shareDialogBuilder.setDescription(description);

            if (caption != null)
                shareDialogBuilder.setCaption(caption);

            if (link != null)
                shareDialogBuilder.setLink(link);

            if (pictureURL != null)shareDialogBuilder.setPicture(pictureURL);

            FacebookDialog shareDialog = shareDialogBuilder.build();
            //fbUiHelper.trackPendingDialogCall(shareDialog.present());

        } else {
            Log.e("Onclickpoststatus","pre-publishfeed");
            publishFeedDialog(name, description, caption, link, pictureURL);
        }
    }

    /**
     *  Provides share dialog with specified parameters if the user is logged in.
     *  Otherwise tries to authorize user (and then provides the same share dialog).
     * @param name The title of the item to be shared. May be null.
     * @param description The description of the item to be shared. May be null.
     * @param caption The subtitle of the item to be shared. May be null.
     * @param link The URL of the item to be shared. May be null.
     * @param pictureURL The URL of the image May be null.
     */
    protected void makeFacebookPost(final String name, final String description, final String caption, final String link, final String pictureURL) {
        Session activeSession = Session.getActiveSession();
        if (activeSession != null && activeSession.isOpened()) {
            onClickPostStatus(name, description, caption, link, pictureURL);
        } else {
            pendingAction = PendingAction.POST;
            loginFacebook(new Session.StatusCallback() {
                @Override
                public void call(Session session, SessionState state, Exception exception) {
                    if (session != null && session.isOpened() && pendingAction == PendingAction.POST) {
                        pendingAction = PendingAction.NONE;
                        Log.e("LOGINFACEBOOK", "CHECK");
                        onClickPostStatus(name, description, caption, link, pictureURL);
                    }
                    Log.e("LOGINFACEBOOK", "POST IF");
                }
            });
        }
    }

    /**
     * Opens login dialog if the user isn't authorized.
     * @param callback The SessionStatusCallback to notify regarding Session state changes. May be null.
     */
    protected void loginFacebook(Session.StatusCallback callback) {
        Log.e("LOGINFACEBOOK", "CHECK");
        Session.openActiveSession(this, true, callback);
    }

    private void publishFeedDialog(String name, String caption, String description, String link, String pictureURL) {
        Bundle params = new Bundle();
        if (name != null)
            params.putString("name", name);

        if (caption != null)
            params.putString("caption", caption);

        if (description != null)
            params.putString("description", description);

        if (link != null)
            params.putString("link", link);

        if (pictureURL != null)
            params.putString("picture", pictureURL);

        WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(this, Session.getActiveSession(), params))
                .setOnCompleteListener(new WebDialog.OnCompleteListener() {
                    @Override
                    public void onComplete(Bundle values, FacebookException error) {
                        if (error == null) {
                            final String postId = values.getString("post_id");
                            if (postId != null) {
                                // User posted new status
                                Toast.makeText(SocialShareActivity.this, getString(R.string.fb_story_posted), Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                // User clicked the Cancel button
                                Toast.makeText(SocialShareActivity.this, getString(R.string.fb_story_cancelled), Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } else if (error instanceof FacebookOperationCanceledException) {
                            // User clicked the "x" button
                            Toast.makeText(SocialShareActivity.this, getString(R.string.fb_story_cancelled), Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            // Generic, ex: network error
                            Toast.makeText(SocialShareActivity.this, getString(R.string.fb_story_error), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                }).build();
        feedDialog.show();
    }

    protected void logoutFacebook() {
        Session activeSession = Session.getActiveSession();
        if (activeSession != null && activeSession.isOpened()) {
            activeSession.closeAndClearTokenInformation();
            Toast.makeText(this, "You're logged off", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Shows a post preview dialog with text in it.
     * If the user is not authorized, offers a way to authorize.
     * @param text The text to be shown in preview dialog.
     */
    protected void makeVKWallPost(String text) {
        if (!VKSdk.wakeUpSession()) {
            shareType = ShareType.TEXT;
            this.text = text;
            authorize();
        } else {
            showPostPreview(text, null, null, null);
        }
    }

    /**
     * Shows a post preview dialog with text and image(s) in it.
     * If the user is not authorized, offers a way to authorize.
     * @param text The text to be shown in preview dialog.
     * @param images The image(s) to be uploaded with the post (At most 10 images allowed).
     */
    protected void makeVKWallPost(String text, File[] images) {
        if (!VKSdk.wakeUpSession()) {
            shareType = ShareType.IMAGE;
            this.text = text;
            this.image = decodeFiles(images);
            authorize();
        } else {
            showPostPreview(text, image, null, null);
        }
    }

    /**
     * Shows a post preview dialog with text and image(s) in it.
     * If the user is not authorized offers a way to authorize.
     * @param text The text to be shown in preview dialog.
     * @param image The image(s) to be uploaded with the post (At most 10 images allowed).
     */
    protected void makeVKWallPost(String text, Bitmap[] image) {
        if (!VKSdk.wakeUpSession()) {
            shareType = ShareType.IMAGE;
            this.text = text;
            this.image = image;
            authorize();
        } else {
            showPostPreview(text, image, null, null);
        }
    }

    protected void logoutVK() {
        VKSdk.logout();
        Toast.makeText(this, R.string.vk_logout, Toast.LENGTH_LONG).show();
    }

    private void authorize() {
        VKSdk.authorize(Constants.VK_SCOPE, true, true);
    }

    private VKSdkListener sdkListener = new VKSdkListener() {
        @Override
        public void onCaptchaError(VKError captchaError) {
            new VKCaptchaDialog(captchaError).show();
        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {
            authorize();
        }

        @Override
        public void onAccessDenied(VKError authorizationError) {
            new AlertDialog.Builder(SocialShareActivity.this).setMessage(authorizationError.errorMessage).show();
        }

        /**
         * Called when the user authorizes application.
         * @param newToken new token for API requests
         */
        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            newToken.saveTokenToSharedPreferences(SocialShareActivity.this, Constants.VK_TOKEN_KEY);
            switch (shareType) {
                case TEXT:
                    showPostPreview(text, null, null, null);
                    break;
                case IMAGE:
                    showPostPreview(text, image, null, null);
                    break;
            }
        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
        }
    };

    private void showPostPreview(String message, Bitmap[] images, String linkTitle, String linkUrl) {
        VKShareDialog dialog = new VKShareDialog();
        dialog.setText(message);
        dialog.setAttachmentLink(linkTitle, linkUrl);

        if (images != null && images.length != 0) {
            VKUploadImage[] uploadImages = prepareImages(images);
            dialog.setAttachmentImages(uploadImages);
        }

        dialog.setShareDialogListener(new VKShareDialog.VKShareDialogListener() {
            @Override
            public void onVkShareComplete(int postId) {
                String contentTitle = getString(R.string.vk_posted);
                String contentText = getString(R.string.vk_click);
                Uri link = Uri.parse(String.format("https://vk.com/wall" + VKSdk.getAccessToken().userId + "_%s", postId));
                int icon = R.drawable.ic_ab_app;
                int id = 2;
                NotificationHelper.showNotification(SocialShareActivity.this, contentTitle, contentText, link, icon, id);
            }

            @Override
            public void onVkShareCancel() {
            }
        });
        dialog.show(getSupportFragmentManager(), "VK_DIALOG");
    }

    private Bitmap[] decodeFiles(File[] image) {
        if (image != null) {
            Bitmap[] btmp = new Bitmap[image.length];
            for (int i = 0; i != image.length; ++i) {
                btmp[i] = BitmapFactory.decodeFile(image[i].toString());
            }
            return btmp;
        }
        return null;
    }

    private VKUploadImage[] prepareImages(Bitmap[] images) {
        VKUploadImage[] uploadImages = new VKUploadImage[images.length];
        for (int i = 0; i != images.length; ++i) {
            uploadImages[i] = new VKUploadImage(images[i], VKImageParameters.jpgImage(1));
        }
        return uploadImages;
    }


}
