package com.aristocrat.cooking;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.gson.Gson;
import com.sharelib.SocialShareActivity;
import com.sharelib.TwitterShare;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends SocialShareActivity {


    private TextView title;
    private static MainActivity mainActivity=null;
    public Typeface typeface;
    public Typeface cambriaTypeFace;
    private ImageView barCategoryPic;
    private Button gotofav;
    private String sentObjString;
    private ImageButton opensearch;

    private class LoadSearchTask extends AsyncTask<Void,Void,Void>{
        private Context context;
        private ProgressDialog loading;

        public LoadSearchTask(Context context){
            this.context=context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(null != context){
                loading = new ProgressDialog(context);
                loading.setIndeterminate(true);
                loading.setCancelable(true);
                loading.setCanceledOnTouchOutside(false);
                loading.setMessage("Готовим рецепты...");
                loading.show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            SearchMapCreator.getInstance(MainActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (MainActivity.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                return;
            }
            if(null != loading && loading.isShowing()){
                loading.dismiss();
                loading = null;
            }
        }
    }

    public RecipeObj getSentObj() {
        Gson gson=new Gson();
        SharedPreferences sharedPreferences=getSharedPreferences("SENTOBJ", MODE_PRIVATE);
        sentObjString=sharedPreferences.getString("OBJSTRING",null);
        return gson.fromJson(sentObjString,RecipeObj.class);
    }

    public void setSentObj(RecipeObj sentObj) {
        sentObjString=new Gson().toJson(sentObj);
        SharedPreferences sharedPreferences=getSharedPreferences("SENTOBJ",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("OBJSTRING",sentObjString);
        editor.commit();
    }

    private int categoryId;
    public static MainActivity getInstance(){
        return mainActivity;
    }
    private int getAdmobId(){
        return R.string.admob_id;
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity=this;

        new LoadSearchTask(this).execute();

        FlurryAgent.init(this,"WH3DS2335DCV7PRVTH6J");

        typeface=Typeface.createFromAsset(getAssets(),"fonts/Nautilus.otf");
        cambriaTypeFace=Typeface.createFromAsset(getAssets(),"fonts/Cambria.ttf");
        setContentView(R.layout.activity_main);
        title=(TextView)findViewById(R.id.categoryTitle);

        barCategoryPic=(ImageView)findViewById(R.id.categoryPic);
        title.setTypeface(typeface);

        gotofav=(Button)findViewById(R.id.gotofav);
        gotofav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFavorites();
                gotofav.setVisibility(View.GONE);
            }
        });

        opensearch=(ImageButton) findViewById(R.id.opensearch);
        opensearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearch();
                opensearch.setVisibility(View.GONE);
            }
        });
/*
        LinearLayout rootLayout = (LinearLayout) findViewById(R.id.adView);
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(getString(getAdmobId()));
        rootLayout.addView(adView, 0);

        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        adView.loadAd(adRequest);
*/

        final AdView adView=(com.google.ads.AdView) findViewById(R.id.adView);
        adView.isInEditMode();
        (new Thread() {
            public void run() {
                Looper.prepare();
                adView.loadAd(new AdRequest());
            }
        }).start();


        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            MenuFragment menuFragment = new MenuFragment();
            menuFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, menuFragment).commit();
//            PreBakeFragment menuFragment = new PreBakeFragment();
//            menuFragment.setArguments(getIntent().getExtras());
//            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, menuFragment).commit();
        }
    }
    private void openFavorites(){
        FavoritesFragment favoritesFragment=new FavoritesFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, favoritesFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openSearch(){
        SearchFragment searchFragment=new SearchFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, searchFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void showSearchButton(){
        opensearch.setVisibility(View.VISIBLE);
    }

    public void hideSearchButton(){
        findViewById(R.id.opensearch).setVisibility(View.GONE);
    }

    //public void hideSearchBar(){
    //    findViewById(R.id.autoboxview).setVisibility(View.GONE);
    //}



    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt("categoryId", categoryId);

    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        changeTitle(savedInstanceState.getInt("categoryId", 0));
    }

    public void updateFavButton(){
        gotofav.setVisibility(View.VISIBLE);
        gotofav.setText(String.valueOf(InfoLoader.getInstance(this).getFavoritesCount()));
    }
    public void hideFavButton(){
        gotofav.setVisibility(View.GONE);
    }

   /* @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view != null && view instanceof EditText) {
                Rect r = new Rect();
                view.getGlobalVisibleRect(r);
                int rawX = (int)ev.getRawX();
                int rawY = (int)ev.getRawY();
                if (!r.contains(rawX, rawY)) {
                    view.clearFocus();
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }*/


    public void changeTitle(int categoryId) {
        switch (categoryId) {
            case 0:
                this.categoryId = categoryId;
                title.setText("Категории");
                barCategoryPic.setVisibility(View.GONE);
                break;

            case 1:
                this.categoryId = categoryId;
                title.setText("Горячие");
                barCategoryPic.setVisibility(View.VISIBLE);
                barCategoryPic.setImageResource(R.drawable.barcathot);
                break;
            case 2:
                this.categoryId = categoryId;
                title.setText("Закуски");
                barCategoryPic.setVisibility(View.VISIBLE);
                barCategoryPic.setImageResource(R.drawable.barcatsnack);
                break;
            case 3:
                this.categoryId = categoryId;
                title.setText("Завтраки");
                barCategoryPic.setVisibility(View.VISIBLE);
                barCategoryPic.setImageResource(R.drawable.barcatbreak);
                break;
            case 4:
                this.categoryId = categoryId;
                title.setText("Десерты");
                barCategoryPic.setVisibility(View.VISIBLE);
                barCategoryPic.setImageResource(R.drawable.barcatdes);
                break;
            case 5:
                this.categoryId = categoryId;
                title.setText("Супы");
                barCategoryPic.setVisibility(View.VISIBLE);
                barCategoryPic.setImageResource(R.drawable.barcatsoup);
                break;
            case 6:
                this.categoryId = categoryId;
                title.setText("Салаты");
                barCategoryPic.setVisibility(View.VISIBLE);
                barCategoryPic.setImageResource(R.drawable.barcatsld);
                break;
            case 7:
                this.categoryId = categoryId;
                title.setText("Соусы");
                barCategoryPic.setVisibility(View.VISIBLE);
                barCategoryPic.setImageResource(R.drawable.barcatsauce);
                break;
            case 8:
                this.categoryId = categoryId;
                title.setText("Напитки");
                barCategoryPic.setVisibility(View.VISIBLE);
                barCategoryPic.setImageResource(R.drawable.barcatdrink);
                break;
            case 9:
                this.categoryId = categoryId;
                title.setText("Форум");
                barCategoryPic.setVisibility(View.VISIBLE);
                barCategoryPic.setImageResource(R.drawable.barcatforum);
                break;
            case 10:
                this.categoryId = categoryId;
                title.setText("Избранное");
                barCategoryPic.setVisibility(View.GONE);
                break;
            case 11:
                this.categoryId = categoryId;
                title.setText("Советы");
                barCategoryPic.setVisibility(View.GONE);
                break;
        }
    }


    public void changeTitle(String text){
        title.setText(text);
        barCategoryPic.setVisibility(View.GONE);
    }








    // 1 Горячие
// 2 Закуски
// 3 завтрак
// 4 десерты
// 5 супы
// 6 салаты
// 7 соусы
// 8 напитки
// 9 форум
// 10 избранное

    public void postImgToVKWall(String text, String categoryName, String fileid) {
        AssetManager assetManager= this.getAssets();
        Bitmap[] image=new Bitmap[1];
        try {
            InputStream inputStream= assetManager.open((categoryName+"/"+fileid+".jpg"));
            image[0]= BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (FileNotFoundException e) {
            return;
        } catch (IOException e) {
            return;
        }
        makeVKWallPost(text, image);
    }
    public void postImgToVKWallforum(String text,  Drawable drawable) {
        Bitmap[] image=new Bitmap[1];

        if (drawable instanceof BitmapDrawable) {
            image[0]=((BitmapDrawable)drawable).getBitmap();
            makeVKWallPost(text, image);
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        image[0]=bitmap;

        makeVKWallPost(text, image);


    }
    public void tweetWithImages(String recipeName,String text, String categoryName,String fileid) {
        Bitmap bitmap;
        File[] image=new File[1];

        File file=new File(getFilesDir(),categoryName+".png");
        FileOutputStream fos = null;
        try {
            AssetManager assetManager=getAssets();
            InputStream inputStream=null;
            inputStream=assetManager.open(categoryName+"/"+fileid+".jpg");
            bitmap=BitmapFactory.decodeStream(inputStream);
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.close();
            inputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        file=new File(getFilesDir(),categoryName+".png");
        image[0]=file;

        TwitterShare.makeTweet(this, recipeName+"https://play.google.com/store/apps/details?id="+getPackageName() , image);
    }

    public void postToFB(String recipeName, String recipeText) {
        String name = recipeName;
        String description = recipeText;
        String caption = "Опубликовано через приложение Кулинарная Книга для Android";
        String link = "https://play.google.com/store/apps/details?id="+getPackageName();
        String pictureUrl = "https://pp.vk.me/c623629/v623629472/4504/knO19aJI7FU.jpg";

        makeFacebookPost(name, caption, description, link,pictureUrl);
    }
    public void postToFBwithImage(String recipeName, String recipeText,String pictureUrl) {
        String name = recipeName;
        String description = recipeText;
        String caption = "Опубликовано через приложение Кулинарная Книга для Android";
        String link = "https://play.google.com/store/apps/details?id="+getPackageName();
        makeFacebookPost(name, caption, description, link,pictureUrl);
    }


    public boolean haveNetworkConnection() {
        Boolean haveConnectedWifi = false;
        Boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
