package com.aristocrat.cooking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aristocrat.cooking.ImageLoader.core.ImageLoader;
import com.google.gson.Gson;
import com.sharelib.TwitterShare;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Nursultan on 07.10.2015.
 */
public class PreBakeFragment extends Fragment implements View.OnClickListener {
    private ImageView recipeimage;
    private EditText recipetext;
    private EditText recipename;
    private ImageView fblogo;
    private ImageView vklogo;
    private ImageView twilogo;
    private ImageView shareico;
    private RecipeObj recipeObj;
    private TextView bakeprogress;

    private ArrayList<RecipeObj> allRecipes;
    private int i=0;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.prebake, container, false);
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
        recipeimage=(ImageView)getActivity().findViewById(R.id.recipeImage);
        recipename=(EditText)getActivity().findViewById(R.id.recipeName);
        recipetext=(EditText)getActivity().findViewById(R.id.recipeText);
        fblogo=(ImageView)getActivity().findViewById(R.id.fb);
        vklogo=(ImageView)getActivity().findViewById(R.id.vk);
        twilogo=(ImageView)getActivity().findViewById(R.id.twi);
        shareico=(ImageView)getActivity().findViewById(R.id.share);

        //recipetext.setTypeface(MainActivity.getInstance().typeface);
        //TYPEFACE УБРАЛ , ИБО СЛИШКОМ ТОНКИЙ ШРИФТ . РОДНОЙ ЛУЧШЕ//recipename.setTypeface(MainActivity.getInstance().typeface);

        getActivity().findViewById(R.id.buttonhot).setOnClickListener(this);
        getActivity().findViewById(R.id.buttonbreak).setOnClickListener(this);
        getActivity().findViewById(R.id.buttondeset).setOnClickListener(this);
        getActivity().findViewById(R.id.buttondrinks).setOnClickListener(this);
        getActivity().findViewById(R.id.buttonsalad).setOnClickListener(this);
        getActivity().findViewById(R.id.buttonsauce).setOnClickListener(this);
        getActivity().findViewById(R.id.buttonsnack).setOnClickListener(this);
        getActivity().findViewById(R.id.buttonsoup).setOnClickListener(this);
        getActivity().findViewById(R.id.buttonvtopku).setOnClickListener(this);
        getActivity().findViewById(R.id.buttonbaking).setOnClickListener(this);

        getActivity().findViewById(R.id.loadnext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBaking();
                getActivity().findViewById(R.id.loadnext).setVisibility(View.GONE);
            }
        });

        allRecipes=SearchMapCreator.getInstance(getActivity()).allRecipeMap.get(11);

        SharedPreferences sharedPreferences= getActivity().getSharedPreferences("PREBAKE", Context.MODE_PRIVATE);
        i=sharedPreferences.getInt("lastopened",2300);

        bakeprogress=(TextView)getActivity().findViewById(R.id.bakeprogress);

        recipename.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(recipeObj!=null) recipeObj.setRecipeName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        recipetext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(recipeObj!=null) recipeObj.setRecipeText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonhot : { saveToCategory(1); break;}
            case R.id.buttonbreak : { saveToCategory(3); break;}
            case R.id.buttondeset : { saveToCategory(4); break;}
            case R.id.buttondrinks : { saveToCategory(8); break;}
            case R.id.buttonsalad : { saveToCategory(6); break;}
            case R.id.buttonsauce : { saveToCategory(7); break;}
            case R.id.buttonsnack : { saveToCategory(2); break;}
            case R.id.buttonsoup : { saveToCategory(5); break;}
            case R.id.buttonbaking: {saveToCategory(10); break;}
            case R.id.buttonvtopku : { loadNext(); break;}
            default:{break;}
        }
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


    private void saveToCategory(int categoryId){
        String categoryName;
        int startfrom;
        switch (categoryId){
            case 1: categoryName="hot";startfrom=0; break;
            case 2: categoryName="snk";startfrom=0; break;
            case 3: categoryName="brk";startfrom=0; break;
            case 4: categoryName="des";startfrom=0; break;
            case 5: categoryName="sup";startfrom=0; break;
            case 6: categoryName="sld";startfrom=0; break;
            case 7: categoryName="soc";startfrom=0; break;
            case 8: categoryName="drk";startfrom=0; break;
            case 9: categoryName="for";startfrom=0; break;
            case 10: categoryName="bak"; startfrom=0; break;
            case 11:categoryName="adv";startfrom=0; break;
            default: categoryName="adv";startfrom= 0; break;
        }

        SharedPreferences sharedPreferences= getActivity().getSharedPreferences(categoryName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        int currentNumber=sharedPreferences.getInt("last",startfrom);
        Morpher.writeToFileExternal("NURSRECIPE/"+categoryName,currentNumber+".txt",new Gson().toJson(recipeObj),getActivity());
        editor.putInt("last", currentNumber + 1);
        editor.commit();
        loadNext();
    }

    private void startBaking(){
        getActivity().findViewById(R.id.sortbuttons).setVisibility(View.VISIBLE);
        loadNext();
    }

    private void loadNext(){

        recipeObj=allRecipes.get(i);
        SharedPreferences sharedPreferences= getActivity().getSharedPreferences("PREBAKE",Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("lastopened",i).commit();
        //bakeprogress.setText("Осталось рецептов: "+(allRecipes.size()-i));
        bakeprogress.setText("Осталось рецептов: "+(allRecipes.size()-i));
        i++;
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
            photosLayout.removeAllViews();
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
                    photosLayout.addView(image, layoutParams);

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

    }

}
