package com.sharelib;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import com.google.api.client.auth.oauth.*;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.lyn.sharelib.R;
import com.sharelib.Store.CredentialStore;
import com.sharelib.Store.SharedPreferencesCredentialStore;
import com.sharelib.Util.QueryStringParser;

import java.io.File;
import java.io.IOException;

public class OAuthAccessTokenDialog {
    private Context sContext;
    private SharedPreferences sPreferences;

    private WebView webview;
    private ProgressBar progressBar;
    private boolean handled;

    private Dialog dialog;

    private String text;
    private File[] images;

    TwitterDialog.ShareType shareType;
    private boolean recycleImages;


    public OAuthAccessTokenDialog(Context context, String text) {
        init(context);
        this.text = text;
        shareType = TwitterDialog.ShareType.TEXT_ONLY;
    }

    public OAuthAccessTokenDialog(Context context, String text, File[] images, boolean recycleImages) {
        init(context);
        this.text = text;

        if (images != null && images.length != 0) {
            shareType = TwitterDialog.ShareType.IMAGE;
            this.images = images;
            this.recycleImages = recycleImages;
        } else {
            shareType = TwitterDialog.ShareType.TEXT_ONLY;
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init(Context context) {
        sContext = context;
        sPreferences = PreferenceManager.getDefaultSharedPreferences(sContext);
        LayoutInflater layoutInflater = (LayoutInflater) sContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View authDialogView = layoutInflater.inflate(R.layout.tweet_auth_dialog, null);

        dialog = new Dialog(sContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressBar = (ProgressBar) authDialogView.findViewById(R.id.authProgress);
        webview = (WebView) authDialogView.findViewById(R.id.authWebView);
        webview.getSettings().setJavaScriptEnabled(true);
        handled = false;

        dialog.setContentView(authDialogView);
        dialog.setCancelable(true);

    }

    public void show() {
        new PreProcessToken().execute();
        dialog.show();
    }

    private class PreProcessToken extends AsyncTask<Uri, Void, Void> {

        final OAuthHmacSigner signer = new OAuthHmacSigner();
        private String authorizationUrl;

        @Override
        protected Void doInBackground(Uri... params) {

            try {
                signer.clientSharedSecret = Constants.TWI_API_SECRET;

                OAuthGetTemporaryToken temporaryToken;
                temporaryToken = new OAuthGetTemporaryToken(Constants.TWI_REQUEST_URL);
                temporaryToken.transport = new ApacheHttpTransport();
                temporaryToken.signer = signer;
                temporaryToken.consumerKey = Constants.TWI_API_KEY;
                temporaryToken.callback = Constants.TWI_OAUTH_CALLBACK_URL;

                OAuthCredentialsResponse tempCredentials = temporaryToken.execute();
                signer.tokenSharedSecret = tempCredentials.tokenSecret;

                OAuthAuthorizeTemporaryTokenUrl authorizeUrl;
                authorizeUrl = new OAuthAuthorizeTemporaryTokenUrl(Constants.TWI_AUTHORIZE_URL);
                authorizeUrl.temporaryToken = tempCredentials.token;
                authorizationUrl = authorizeUrl.build();

                //Log.i(Constants.TAG, "Using authorizationUrl = " + authorizationUrl);

                handled = false;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //Log.i(TAG, "Retrieving request token from Google servers");

            webview.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap bitmap) {
                    Log.i(Constants.TAG, "onPageStarted : " + url + " handled = " + handled);
                     if (url.startsWith(Constants.TWI_OAUTH_CALLBACK_URL)) {
                        if (url.contains("denied")) {
                            dialog.dismiss();
                        }
                    }
                }

                @Override
                public void onPageFinished(final WebView view, final String url) {
                    Log.i(Constants.TAG, "onPageFinished : " + url + " handled = " + handled);

                    if (url.startsWith(Constants.TWI_OAUTH_CALLBACK_URL)) {
                        if (url.contains("oauth_token=")) {
                            webview.setVisibility(View.INVISIBLE);
                            if (!handled) {
                                new ProcessToken(url, signer).execute();
                            }
                        } else {
                            webview.setVisibility(View.VISIBLE);
                        }
                    }

                    if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
                        progressBar.setVisibility(View.GONE);
                    }
                }

            });

            webview.loadUrl(authorizationUrl);
        }

    }

    private class ProcessToken extends AsyncTask<Uri, Void, Boolean> {
        String url;
        private OAuthHmacSigner signer;
        private ProgressDialog pd;

        public ProcessToken(String url, OAuthHmacSigner signer) {
            this.url = url;
            this.signer = signer;
            pd = new ProgressDialog(sContext);
        }

        @Override
        protected void onPreExecute() {
            pd.setMessage(sContext.getString(R.string.twitter_auth_wait));
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Boolean doInBackground(Uri... params) {
            //Log.i(Constants.TAG, "doInBackground called with url " + url);
            if (url.startsWith(Constants.TWI_OAUTH_CALLBACK_URL) && !handled) try {
                if (url.contains("oauth_token=")) {
                    handled = true;
                    String requestToken = extractParamFromUrl(url, "oauth_token");
                    String verifier = extractParamFromUrl(url, "oauth_verifier");

                    signer.clientSharedSecret = Constants.TWI_API_SECRET;

                    OAuthGetAccessToken accessToken;
                    accessToken = new OAuthGetAccessToken(Constants.TWI_ACCESS_URL);
                    accessToken.transport = new ApacheHttpTransport();
                    accessToken.temporaryToken = requestToken;
                    accessToken.signer = signer;
                    accessToken.consumerKey = Constants.TWI_API_KEY;
                    accessToken.verifier = verifier;

                    OAuthCredentialsResponse credentials = accessToken.execute();
                    signer.tokenSharedSecret = credentials.tokenSecret;

                    CredentialStore credentialStore = new SharedPreferencesCredentialStore(sPreferences);
                    credentialStore.write(new String[]{credentials.token, credentials.tokenSecret});
                    return true;

                } else if (url.contains("error=")) {
                    new SharedPreferencesCredentialStore(sPreferences).clearCredentials();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        private String extractParamFromUrl(String url, String paramName) {
            String queryString = url.substring(url.indexOf("?", 0) + 1, url.length());
            QueryStringParser queryStringParser = new QueryStringParser(queryString);
            return queryStringParser.getQueryParamValue(paramName);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();

            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }

            if (TwitterUtils.tokensExist(sPreferences)) {
                switch (shareType) {
                    case TEXT_ONLY:
                        TwitterDialog td = new TwitterDialog(sContext);
                        td.setText(text);
                        td.show();
                        break;
                    case IMAGE:
                        TwitterDialog tdImg = new TwitterDialog(sContext);
                        tdImg.setText(text);
                        tdImg.setImages(images, recycleImages);
                        tdImg.show();
                        break;
                }
            }
        }
    }
}