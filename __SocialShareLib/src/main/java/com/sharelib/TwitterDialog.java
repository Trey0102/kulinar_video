package com.sharelib;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import com.lyn.sharelib.R;
import com.sharelib.Util.NotificationHelper;
import org.apache.commons.validator.routines.UrlValidator;

import java.io.File;
import java.util.ArrayList;

public class TwitterDialog extends Dialog {
    public enum ShareType {
        TEXT_ONLY, IMAGE
    }

    private Context context;
    private SharedPreferences preferences;

    private View tweetSheet;

    private TextView charCounter;
    private EditText tweetEditText;

    private LinearLayout linearLayout;
    private HorizontalScrollView horizontalScroll;

    private Button sendButton;

    private ProgressBar progressBar;
    private CheckBoxUtil checkBoxUtil;

    private int tweetMaxLength = 140;
    private ShareType shareType = ShareType.TEXT_ONLY;

    private boolean recycleImages = false;

    protected TwitterDialog(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public void setText(String text) {
        if (text.length() > tweetMaxLength) {
            text = text.substring(0, tweetMaxLength);
        }
        tweetEditText.setText(text);
        tweetEditText.setSelection(text.length());
    }

    public void setImages(File[] images, boolean recycleOnDismiss) {
        showImages(images);
        recycleImages = recycleOnDismiss;
    }

    private void init() {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        tweetSheet = getLayoutInflater().inflate(R.layout.tweet_send_layout, null, false);
        charCounter = (TextView) tweetSheet.findViewById(R.id.charCounter);

        linearLayout = (LinearLayout) tweetSheet.findViewById(R.id.imagesLinearLayout);
        horizontalScroll = (HorizontalScrollView) tweetSheet.findViewById(R.id.imagesScrollView);

        tweetEditText = (EditText) tweetSheet.findViewById(R.id.tweetEditText);
        tweetEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                setCharCounter(s.toString());
            }
        });

        sendButton = (Button)tweetSheet.findViewById(R.id.twi_dlg_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = tweetEditText.getText().toString();
                switch (shareType) {
                    case TEXT_ONLY:
                        trySendText(text);
                        break;
                    case IMAGE:
                        int checked = checkBoxUtil.getCheckedCounter();
                        if (0 < checked && checked <= 4) {
                            trySendTxtAndImg(text, checkBoxUtil.getCheckedImages());
                        } else {
                            trySendText(text);
                        }
                        break;
                }
            }
        });
        progressBar = (ProgressBar)tweetSheet.findViewById(R.id.twi_dlg_progress);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    private boolean trySendText(String text) {
        if ((tweetMaxLength - count(text)) >= 0) {
            new SendingTask(text).execute();
            return true;
        } else {
            tweetEditText.setText(text.trim());
            tweetEditText.setSelection(tweetEditText.getText().length());
        }
        return false;
    }

    private boolean trySendTxtAndImg(String text, File[] files) {
        if ((tweetMaxLength - count(text) - 23) >= 0) {
            new SendingTask(text, files).execute();
            return true;
        } else {
            tweetEditText.setText(text.trim());
            tweetEditText.setSelection(tweetEditText.getText().length());
        }
        return false;
    }

    private class SendingTask extends AsyncTask<Void, Integer, Boolean> {
        String text;
        File[] files;
        String username = null;
        twitter4j.Status status;

        public SendingTask(String text) {
            this.text = text;
        }

        public SendingTask(String text, File[] files) {
            this.text = text;
            this.files = files;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            sendButton.setVisibility(View.GONE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            switch (shareType) {
                case TEXT_ONLY:
                    status = TwitterUtils.sendTweet(preferences, text);
                    break;
                case IMAGE:
                    status = TwitterUtils.sendTweetWithImages(preferences, text, files);
                    break;
            }

            if (status != null) {
                username = status.getUser().getScreenName();
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            String txt;
            if (result) {
                txt = context.getString(R.string.tweet_send_success);
            }
            else {
                txt = context.getString(R.string.tweet_send_failure);
            }

            NotificationHelper.showNotification(context, txt, (username != null ? "@" + username + ": " + text : ""), null, R.drawable.ic_stat_twitter_bird_icon, 1);

            dismiss();
        }
    }


    private void showImages(File[] images) {
        if (images != null && images.length != 0) {
            shareType = ShareType.IMAGE;

            horizontalScroll.setVisibility(View.VISIBLE);
            int heightDP = 128, marginDp = 8;
            int heightPx = dpToPixels(heightDP), marginPx = dpToPixels(marginDp);

            checkBoxUtil = new CheckBoxUtil();
            for (int i = 0; i != images.length; ++i) {
                FrameLayout frameLayout = new FrameLayout(context);
                linearLayout.addView(frameLayout);
                frameLayout.getLayoutParams().height = heightPx;
                frameLayout.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                frameLayout.setPadding(marginPx, marginPx, marginPx, marginPx);

                ImageView imageView = new ImageView(context);
                frameLayout.addView(imageView);
                imageView.getLayoutParams().width = imageView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageDrawable(Drawable.createFromPath(images[i].toString()));

                CheckBox checkBox = new CheckBox(context);
                frameLayout.addView(checkBox);
                checkBox.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                checkBox.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                checkBox.setBackgroundColor(Color.argb(185, 0, 0, 0));
                checkBoxUtil.add(checkBox, images[i]);

            }
        }
    }

    /**
     * do not use manually (unless you know what you're doing)
     */
    private void deleteImages() {
        File[] files = checkBoxUtil.getAllImages();
        if (files != null) {
            for (int i = 0; i != files.length; ++i) {
                if (files[i] != null) {
                    files[i].delete();
                }
            }
        }
    }

    private int dpToPixels(int dp) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int)px;
    }

    private void setCharCounter(String s) {
        int imgLength = 0;
        if (shareType != ShareType.TEXT_ONLY) {
            if (checkBoxUtil != null && checkBoxUtil.getCheckedCounter() != 0) {
                imgLength = 23;
            }
        }
        int length = tweetMaxLength - count(s) - imgLength;
        charCounter.setText("" + length);
    }

    private int count(String text) {
        int whitespaces = 0;
        for (int i = 0; i < text.length(); ++i) {
            if (Character.isWhitespace(text.charAt(i))) {
                ++whitespaces;
            }
        }

        int length = 0;
        UrlValidator urlValidator = new UrlValidator() {
            /** allow missing scheme. */
            @Override
            public boolean isValid(String value) {
                return super.isValid(value) || super.isValid("http://" + value);
            }
        };
        String[] parts = text.split("\\s+");
        for (String item : parts) {
            boolean valid = urlValidator.isValid(item);
            if (valid)
                length += 22;
            else
                length += item.length();
        }
        return length + whitespaces;
    }

    private class CheckBoxUtil {
        class Holder {

            CheckBox checkBox;
            File file;
            public Holder(CheckBox checkBox, File file) {
                this.checkBox = checkBox;
                this.file = file;
            }
            public CheckBox getCheckBox() {
                return checkBox;
            }

            public File getFile() {
                return file;
            }

        }
        ArrayList<Holder> holders;

        int checked;
        public CheckBoxUtil() {
            holders = new ArrayList<Holder>();
            checked = 0;
        }

        public void add(final CheckBox checkBox, File file) {
            holders.add(new Holder(checkBox, file));
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        ++checked;
                        if (checked > 4) {
                            checkBox.setChecked(false);
                            --checked;
                            Toast.makeText(context, R.string.tweet_img_warn, Toast.LENGTH_SHORT).show();
                        }
                    }
                    setCharCounter(tweetEditText.getText().toString());
                }
            });
        }

        public int getCheckedCounter() {
            return checked;
        }

        public File[] getAllImages() {
            ArrayList<File> files = new ArrayList<File>();
            for (int i = 0; i != holders.size(); ++i) {
                files.add(holders.get(i).getFile());
            }
            return files.toArray(new File[files.size()]);
        }

        public File[] getCheckedImages() {
            ArrayList<File> files = new ArrayList<File>();
            for (int i = 0; i != holders.size(); ++i) {
                if (holders.get(i).getCheckBox().isChecked()) {
                    files.add(holders.get(i).getFile());
                }
            }
            return files.toArray(new File[files.size()]);
        }
    }

    @Override
    public void show() {
        super.show();
        setContentView(tweetSheet);

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (recycleImages) {
                    deleteImages();
                }
            }
        });
    }
}
