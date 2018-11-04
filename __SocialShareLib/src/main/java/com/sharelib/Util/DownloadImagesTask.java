package com.sharelib.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadImagesTask extends AsyncTask<Void, Integer, File[]> {
    private String[] imgUrl;
    private File[] file;
    private ProgressDialog pd;

    private DownloadListener mListener;
    public DownloadImagesTask(Context context, String[] imageUrl, DownloadListener downloadListener) {
        imgUrl = imageUrl;
        mListener = downloadListener;
        file = new File[imgUrl.length];
        pd = new ProgressDialog(context);
        pd.setTitle("Please, wait");
        pd.setMessage("Preparing images...");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMax(imageUrl.length);
    }

    @Override
    protected void onPreExecute() {
        pd.show();
    }

    public File[] doInBackground(Void... params) {
        for (int i = 0; i != imgUrl.length; ++i) {
            file[i] = new File(saveImage(imgUrl[i]));
            onProgressUpdate(i);
        }
        return file;
    }



    @Override
    protected void onCancelled() {
        deleteFiles();
    }

    @Override
    protected void onCancelled(File[] files) {
        deleteFiles();
    }

    private void deleteFiles() {
        if (file != null && file.length != 0) {
            for (int i = 0; i != file.length; ++i) {
                if (file[i] != null) {
                    file[i].delete();
                }
            }
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        pd.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(File[] files) {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        mListener.onComplete(files);
    }

    private String saveImage(String url) {
        String filePath;
        String extension = url.substring(url.lastIndexOf(".") + 1);
        if (extension.contains("?")) {
            extension.substring(0, extension.indexOf("?"));
        }

        File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        File f = new File(String.format("%s/__temp/", externalStoragePublicDirectory));
        if (!f.isDirectory())
            f.mkdir();

        int j = 0;

        while (true) {
            String path = String.format("%s/__temp/tmp_img%d.%s", externalStoragePublicDirectory, j, extension);
            f = new File(path);
            if (!f.exists())
                break;
            ++j;
        }
        filePath = f + "";

        int count;
        try {
            URLConnection urlConnection = new URL(url).openConnection();
            urlConnection.setConnectTimeout(30000);
            urlConnection.setReadTimeout(30000);

            InputStream in = urlConnection.getInputStream();
            FileOutputStream out = new FileOutputStream(filePath);

            byte[] bytes = new byte[1024];
            while (true) {
                count = in.read(bytes);
                if (count == -1) {
                    in.close();
                    out.flush();
                    out.close();
                    return filePath;
                }
                if (isCancelled()) {
                    new File(filePath).delete();
                    in.close();
                    out.flush();
                    out.close();
                    return null;
                }
                out.write(bytes, 0, count);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(getClass().getName(), "An error occured while downloading from url=" + url + " to destination=" + filePath);
        return null;
    }
}