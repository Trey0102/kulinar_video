package com.aristocrat.cooking.vkgroup;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aristocrat.cooking.ImageLoader.core.ImageLoader;
import com.aristocrat.cooking.MainActivity;
import com.aristocrat.cooking.R;
import com.aristocrat.cooking.RecipeFragment;

import com.sharelib.TwitterShare;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class VKRecipeFragment extends Fragment {
    private ImageView recipeimage;
    private TextView recipetext;
    private TextView recipename;
    private ImageView fblogo;
    private ImageView vklogo;
    private ImageView twilogo;
    private ImageView shareico;
    protected Post post = null;

    public void setInfo(Post post) {
        this.post = post;
    }

    protected String getCuttedText(String originalText){
        try{
            return (originalText.substring(0,originalText.indexOf("\n")));
        }catch (StringIndexOutOfBoundsException e){
            return originalText;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("ON_CREATE "+ savedInstanceState);
        if(null != savedInstanceState){
            try {
                post = Post.parse(new JSONObject(savedInstanceState.getString("post")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_vkrecipe,container,false);


        recipeimage=(ImageView)v.findViewById(R.id.recipeImage);
        recipename=(TextView)v.findViewById(R.id.recipeName);
        recipetext=(TextView)v.findViewById(R.id.recipeText);
        fblogo=(ImageView)v.findViewById(R.id.fb);
        vklogo=(ImageView)v.findViewById(R.id.vk);
        twilogo=(ImageView)v.findViewById(R.id.twi);
        shareico=(ImageView)v.findViewById(R.id.share);

        recipetext.setText(post.getText());
        recipename.setText(getCuttedText(post.getText()));
        //recipetext.setTypeface(MainActivity.getInstance().typeface);
        //recipename.setTypeface(MainActivity.getInstance().typeface);

        if(null != post.getPhotos()){
            LinearLayout photosLayout = (LinearLayout) v.findViewById(R.id.photos);
            String murl = post.getPhotos().get(0).getPhotoUrls().get(post.getPhotos().get(0).getPhotoUrls().size() - 1);
            ImageLoader.getInstance().displayImage(murl, recipeimage, getActivity());

            for(int i = 0; i < post.getPhotos().size(); ++i){
                final String url = post.getPhotos().get(i).getPhotoUrls().get(post.getPhotos().get(i).getPhotoUrls().size() - 1);

                ImageView image = new ImageView(MainActivity.getInstance());
                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, RecipeFragment.getDp(getActivity()), getResources().getDisplayMetrics()) );
                layoutParams.setMargins(0,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10, getResources().getDisplayMetrics()),0,0);
                photosLayout.addView(image, layoutParams);

                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                image.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                        if (currentapiVersion >= 11) {
                            //Bitmap temp = ((BitmapDrawable)image.getDrawable()).getBitmap();
                            //downloadOnClick(url, temp);
                        } else {
                            // downloadOnCLick(url);
                        }
                    }
                });



                ImageLoader.getInstance().displayImage(url, image, getActivity());

                if(null != post.getPhotos().get(i).getText() && 0 != post.getPhotos().get(i).getText().length()){
                    TextView text = new TextView(getActivity());
                    photosLayout.addView(text, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    text.setText(post.getPhotos().get(i).getText());
                    text.setTextSize(TypedValue.COMPLEX_UNIT_SP,17);
                    text.setTextColor(Color.BLACK);

                }
            }
        }



        return v;

    }


    @Override
    public void onPause() {

        super.onPause();

    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MainActivity.getInstance().hideFavButton();

        //ImgLoader.getInstance().loadDrawable(recipeObj.getCategoryName(),String.valueOf(recipeObj.getFileName()),recipeimage,getActivity().getResources().getDrawable(R.drawable.placeholder),getActivity());
        vklogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.getInstance().haveNetworkConnection()) {
                    MainActivity.getInstance().postImgToVKWallforum("Опубликовано через приложение Кулинарная Книга для Android \n https://play.google.com/store/apps/details?id="+getActivity().getPackageName() + recipename.getText().toString() + "\n" + recipetext.getText().toString(), recipeimage.getDrawable());
                } else {
                    (MainActivity.getInstance()).runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.getInstance(), "Проверьте подключение к интернету", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });
        twilogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.getInstance().haveNetworkConnection()) {
                    String[] picurl=new String[1];
                    picurl[0]= post.getPhotos().get(0).getPhotoUrls().get(post.getPhotos().get(0).getPhotoUrls().size() - 1);
                    TwitterShare.makeTweet(getActivity(), recipename.getText().toString()+"\n https://play.google.com/store/apps/details?id="+getActivity().getPackageName(), picurl);
                } else {
                    Toast.makeText(MainActivity.getInstance(), "Проверьте подключение к интернету", Toast.LENGTH_SHORT).show();
                }

            }
        });
        fblogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (MainActivity.getInstance().haveNetworkConnection()) {
                    MainActivity.getInstance().postToFBwithImage(recipename.getText().toString(),recipetext.getText().toString(),post.getPhotos().get(0).getPhotoUrls().get(post.getPhotos().get(0).getPhotoUrls().size() - 1));
                } else {
                    Toast.makeText(MainActivity.getInstance(), "Проверьте подключение к интернету", Toast.LENGTH_SHORT).show();
                }

            }
        });
        shareico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = recipename.getText().toString() + "\n" + recipetext.getText().toString();
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, recipename.getText().toString());
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Cooking"));
            }

        });


    /*
    private void downloadOnCLick(final String url)
    {
        new AlertDialog.Builder(getActivity()).setItems(
                new String[]{"Скачать"},
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (arg1 == 0)
                            new SaveImageTask().execute(url);
                    }
                }).create().show();
    }
    private void downloadOnClick(final String url, final Bitmap bitmap){

        new AlertDialog.Builder(getActivity()).setItems(
                new String[]{"Скачать", "Увеличить"},
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (arg1 == 0)
                            new SaveImageTask().execute(url);
                        else if (arg1 == 1)
                        {
                            ImagePreviewDialog dial = new ImagePreviewDialog(getActivity());
                            dial.setImageResource(bitmap);

                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            lp.copyFrom(dial.getWindow().getAttributes());
                            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                            dial.show();
                            dial.getWindow().setAttributes(lp);
                            dial.show();
                        }
                    }
                }).create().show();
    }

    public void startDownloadTask(Video video) {
        new SaveImageTask().execute(video.getUrl());
    }*/
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("post", post.toString());
        super.onSaveInstanceState(outState);
    }

private class SaveImageTask extends AsyncTask<String, Integer, Boolean> {

    private String filePath;

    @Override
    protected Boolean doInBackground(String... urls) {
        String extension = urls[0].substring(urls[0].lastIndexOf(".")+1);
        if(extension.contains("?")){
            extension = extension.substring(0, extension.indexOf("?"));
        }
        File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!f.isDirectory())
            f.mkdir();

        int j = 1;

        while(true){
            f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/"+getString(R.string.app_name) +"_"+ j + "." + extension);
            if(!f.exists())
                break;
            ++j;
        }
        filePath = f+"";


        try {
            URLConnection urlConnection = new URL(urls[0]).openConnection();
            urlConnection.setConnectTimeout(30000);
            urlConnection.setReadTimeout(30000);

            InputStream in = urlConnection.getInputStream();
            FileOutputStream out = new FileOutputStream(filePath);

            byte[] arrayOfByte = new byte[1024];
            while(true){
                int len = in.read(arrayOfByte);
                if(-1 == len){
                    in.close();
                    out.close();
                    return true;
                }
                if(isCancelled()){
                    new File(filePath).delete();
                    in.close();
                    out.close();
                    return false;
                }
                out.write(arrayOfByte, 0, len);
            }
        } catch(Exception e) {
            new File(filePath).delete();
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(result){
            MediaScannerConnection.scanFile(getActivity(),
                    new String[]{new File(filePath).toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {

                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
            Toast.makeText(getActivity(), "Download complete.\n Image was saved in " + filePath, Toast.LENGTH_LONG).show();

        }
        else{
            Toast.makeText(getActivity(), "Sorry, unable to save image", Toast.LENGTH_LONG).show();
        }
        super.onPostExecute(result);
    }

}
}
