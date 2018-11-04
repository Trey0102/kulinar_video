package com.aristocrat.cooking;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.sharelib.Constants;
import com.sharelib.TwitterShare;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class RecipeFragment extends Fragment {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ImageView recipeimage;
    private TextView recipetext;
    private TextView recipename;
    private ImageView fblogo;
    private ImageView vklogo;
    private ImageView twilogo;
    private ImageView shareico;
    private RecipeObj recipeObj;
    private LinearLayout favorite_container;
    private TextView favorite_text;
    private ImageView favorite_imageview;

    //youtube player fragment
    private YouTubePlayerSupportFragment youTubePlayerFragment;

    //youtube player to play video when new video selected
    private YouTubePlayer youTubePlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        youTubePlayerFragment = new YouTubePlayerSupportFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.youtube_player_fragment, youTubePlayerFragment);
        ft.commit();
        return inflater.inflate(R.layout.fragment_recipe, container, false);
    }

    /**
     * initialize youtube player via Fragment and get instance of YoutubePlayer
     */
    private void initializeYoutubePlayer() {

//        youTubePlayerFragment = (YouTubePlayerSupportFragment) this.getFragmentManager()
//          .findFragmentById(R.id.youtube_player_fragment);

        if (youTubePlayerFragment == null)
            return;

        youTubePlayerFragment.initialize(Constants.DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                                boolean wasRestored) {
                if (!wasRestored) {
                    youTubePlayer = player;

                    //set the player style default
                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);

                    //cue the 1st video by default
                    youTubePlayer.cueVideo("W3FXMnQQskQ");
                    youTubePlayer.play();
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {

                //print or show error if initialization failed
                Log.e(TAG, "Youtube Player View initialization failed");
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        MainActivity.getInstance().hideFavButton();
        MainActivity.getInstance().hideSearchButton();

        initializeYoutubePlayer();


        recipeimage=(ImageView)getActivity().findViewById(R.id.recipeImage);
        recipename=(TextView)getActivity().findViewById(R.id.recipeName);
        recipetext=(TextView)getActivity().findViewById(R.id.recipeText);
        fblogo=(ImageView)getActivity().findViewById(R.id.fb);
        vklogo=(ImageView)getActivity().findViewById(R.id.vk);
        twilogo=(ImageView)getActivity().findViewById(R.id.twi);
        shareico=(ImageView)getActivity().findViewById(R.id.share);
        recipeObj=((MainActivity)getActivity()).getSentObj();
        //recipetext.setTypeface(MainActivity.getInstance().typeface);
        //TYPEFACE УБРАЛ , ИБО СЛИШКОМ ТОНКИЙ ШРИФТ . РОДНОЙ ЛУЧШЕ//recipename.setTypeface(MainActivity.getInstance().typeface);
        try {
            recipetext.setText(recipeObj.getRecipeText().replaceAll("[\n]+", "\n\n"));
        }catch (NullPointerException npe){
            try {
                recipetext.setText(recipeObj.getRecipeText());
            }catch (Exception ex){

            }
        }



        if(recipeObj!=null && recipeObj.getRecipeName()!=null) {
            recipename.setText(recipeObj.getRecipeName());
        }else if(recipeObj!=null && recipeObj.getRecipeName()==null){
            recipename.setText(recipeObj.getRecipeText());
        }else {
            recipename.setText("Вкусный Рецепт");
        }
        if(!recipeObj.isFromForum()) {
            if(recipeObj.getPicUrl()!=null){
                ImageLoader.getInstance().displayImage(recipeObj.getPicUrl(), recipeimage, getActivity());
            }else {
                ImageLoader.getInstance().displayImage("assets://" + recipeObj.getCategoryName() + "/" + recipeObj.getFileName() + ".jpg", recipeimage, getActivity());
            }
            vklogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (MainActivity.getInstance().haveNetworkConnection()) {
                        MainActivity.getInstance().postImgToVKWall("Опубликовано через приложение Кулинарная Книга для Android \n https://play.google.com/store/apps/details?id=com.aristocrat.cooking \n"+recipeObj.getRecipeName() + "\n" + recipeObj.getRecipeText(), recipeObj.getCategoryName(), String.valueOf(recipeObj.getFileName()));
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
                        MainActivity.getInstance().tweetWithImages(recipeObj.getRecipeName() , recipeObj.getRecipeText(), recipeObj.getCategoryName(), String.valueOf(recipeObj.getFileName()));
                    } else {
                        Toast.makeText(MainActivity.getInstance(), "Проверьте подключение к интернету", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            fblogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (MainActivity.getInstance().haveNetworkConnection()) {
                        MainActivity.getInstance().postToFB(recipeObj.getRecipeName(), recipeObj.getRecipeText());
                    } else {
                        Toast.makeText(MainActivity.getInstance(), "Проверьте подключение к интернету", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            shareico.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = recipeObj.getRecipeName() + "\n" + recipeObj.getRecipeText();
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, recipeObj.getRecipeName());
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Cooking"));
                }

            });
            LinearLayout photosLayout = (LinearLayout) getActivity().findViewById(R.id.photos);
            ImageView image = new ImageView(MainActivity.getInstance());

            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics()) );
            layoutParams.setMargins(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()), 0, 0);
            photosLayout.addView(image, layoutParams);

            image.setScaleType(ImageView.ScaleType.CENTER_CROP);

            if(recipeObj.getPicUrl()!=null){
                ImageLoader.getInstance().displayImage(recipeObj.getPicUrl(), recipeimage, getActivity());
            }else {
                ImageLoader.getInstance().displayImage("assets://" + recipeObj.getCategoryName() + "/" + recipeObj.getFileName() + ".jpg", recipeimage, getActivity());
            }
        }

        if (recipeObj.isFromForum()){

            if(null != recipeObj.getPhotos()){
                LinearLayout photosLayout = (LinearLayout) getActivity().findViewById(R.id.photos);
                String murl = recipeObj.getPhotos().get(0).getPhotoUrls().get(recipeObj.getPhotos().get(0).getPhotoUrls().size() - 1);
                ImageLoader.getInstance().displayImage(murl, recipeimage, getActivity());
                for(int i = 0; i < recipeObj.getPhotos().size(); ++i){
                    final String url = recipeObj.getPhotos().get(i).getPhotoUrls().get(recipeObj.getPhotos().get(i).getPhotoUrls().size() - 1);
                    ImageView image = new ImageView(MainActivity.getInstance());
                    if ((getResources().getConfiguration().screenLayout &
                            Configuration.SCREENLAYOUT_SIZE_MASK) ==
                            Configuration.SCREENLAYOUT_SIZE_LARGE) {
                        // on a large screen device ...

                    }
                    LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getDp(getActivity()), getResources().getDisplayMetrics()) );
                    layoutParams.setMargins(0,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10, getResources().getDisplayMetrics()),0,0);
                    photosLayout.addView(image,layoutParams);

                    image.setScaleType(ImageView.ScaleType.CENTER_CROP);



                    ImageLoader.getInstance().displayImage(url, image, getActivity());

                    if(null != recipeObj.getPhotos().get(i).getText() && 0 != recipeObj.getPhotos().get(i).getText().length()){
                        TextView text = new TextView(getActivity());
                        photosLayout.addView(text, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        text.setText(recipeObj.getPhotos().get(i).getText());
                        text.setTextSize(TypedValue.COMPLEX_UNIT_SP,17);
                        text.setTextColor(Color.BLACK);

                    }
                }
            }
            ImageLoader.getInstance().displayImage("assets://" + recipeObj.getCategoryName() + "/" + recipeObj.getFileName() + ".jpg", recipeimage, getActivity());
            vklogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (MainActivity.getInstance().haveNetworkConnection()) {
                        MainActivity.getInstance().postImgToVKWallforum("Опубликовано через приложение Кулинарная Книга для Android \n https://play.google.com/store/apps/details?id=com.aristocrat.cooking \n" + recipeObj.getRecipeName() + "\n" + recipeObj.getRecipeText(), recipeimage.getDrawable());
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
                        String[] picurl = new String[1];
                        picurl[0] = recipeObj.getPicUrl();
                        TwitterShare.makeTweet(getActivity(), recipeObj.getRecipeName() + "Опубликовано через приложение Кулинарная Книга для Android \n https://play.google.com/store/apps/details?id=com.aristocrat.cooking \n", picurl);
                    } else {
                        Toast.makeText(MainActivity.getInstance(), "Проверьте подключение к интернету", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            fblogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (MainActivity.getInstance().haveNetworkConnection()) {
                        MainActivity.getInstance().postToFBwithImage(recipeObj.getRecipeName(), recipeObj.getRecipeText(), recipeObj.getPicUrl());
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
                    String shareBody = recipeObj.getRecipeName() + "\n" + recipeObj.getRecipeText();
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, recipeObj.getRecipeName());
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Cooking"));
                }

            });
        }

        favorite_container= (LinearLayout)getActivity().findViewById(R.id.recipe_fragment_favoritebtn_container);
        favorite_imageview=(ImageView)getActivity().findViewById(R.id.recipe_fragment_favorite);
        favorite_text=(TextView)getActivity().findViewById(R.id.recipe_fragment_favorite_text);

        if(recipeObj.isFavorited()){
            favorite_text.setText("Рецепт в избранном");
            favorite_imageview.setImageResource(R.drawable.favicoenabled);
        }else {
            favorite_text.setText("Добавить в избранное");
            favorite_imageview.setImageResource(R.drawable.favico);
        }
        favorite_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recipeObj.isFavorited()){
                    favorite_text.setText("Добавить в избранное");
                    favorite_imageview.setImageResource(R.drawable.favico);
                    recipeObj.setFavorited(false);
                    SearchMapCreator.getInstance(getActivity()).allRecipeMap.get(CategoryHelper.getCategoryIdByName(recipeObj.getCategoryName())).get(recipeObj.getFileName()-1).setFavorited(false);
                    InfoLoader.getInstance(getActivity()).setFavorited(recipeObj.getCategoryName(), String.valueOf(recipeObj.getFileName()), false);
                }else {
                    favorite_text.setText("Рецепт в избранном");
                    favorite_imageview.setImageResource(R.drawable.favicoenabled);
                    recipeObj.setFavorited(true);
                    SearchMapCreator.getInstance(getActivity()).allRecipeMap.get(CategoryHelper.getCategoryIdByName(recipeObj.getCategoryName())).get(recipeObj.getFileName()-1).setFavorited(true);
                    InfoLoader.getInstance(getActivity()).setFavorited(recipeObj.getCategoryName(), String.valueOf(recipeObj.getFileName()), true);
                }
            }
        });

    }
    public static int getSizeName(Context context) {
        int screenLayout = context.getResources().getConfiguration().screenLayout;
        screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;

        switch (screenLayout) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                return Configuration.SCREENLAYOUT_SIZE_SMALL;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                return Configuration.SCREENLAYOUT_SIZE_NORMAL;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                return Configuration.SCREENLAYOUT_SIZE_LARGE;
            case 4: // Configuration.SCREENLAYOUT_SIZE_XLARGE is API >= 9
                return 4;
            default:
                return Configuration.SCREENLAYOUT_SIZE_NORMAL;
        }
    }
    public static int getDp(Context context){
        switch (getSizeName(context)){
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                return 220;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                return 250;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                return 450;
            case 4: return 500;
        }
        return 220;
    }

}
