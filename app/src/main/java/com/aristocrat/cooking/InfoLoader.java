package com.aristocrat.cooking;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Log;

import org.mozilla.universalchardet.Constants;
import org.mozilla.universalchardet.UniversalDetector;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Nursultan on 25.09.2014.
 */
public class InfoLoader {
    private static InfoLoader infoLoader = null;
    private AssetManager assetManager;
    private ArrayList<RecipeObj> recipeObjs;
    private Context context;
    private String categoryName;
    private LinkedHashMap<String, Boolean> favoritesMap;
    private LinkedHashMap<String,RecipeObj> forumMap;
    private MapWrapper wrapper;



    public static InfoLoader getInstance(Activity activity) {
        if (infoLoader == null) {
            infoLoader = new InfoLoader(activity);
        }
        return infoLoader;
    }

    private InfoLoader(Activity activity) {
        this.context = activity;
        wrapper = new MapWrapper();
        favoritesMap = new LinkedHashMap<String, Boolean>();
        Gson gson = new Gson();
        SharedPreferences preferences = activity.getSharedPreferences("FAVORITES", Context.MODE_PRIVATE);
        String wrapperStr = preferences.getString("FAVORITES_MAP", null);
        if (wrapperStr == null) {
            favoritesMap.put("hot1", false);
        } else {
            MapWrapper wrapper = gson.fromJson(wrapperStr, MapWrapper.class);
            favoritesMap = wrapper.getMyMap();
        }

        String read_data = null;

        try {
            FileInputStream fis = context.openFileInput("forumfavs");
            byte[] dataArray = new byte[fis.available()];
            while (fis.read(dataArray) != -1) {
                if (read_data == null) {
                    read_data = new String(dataArray);
                } else {
                    read_data = read_data.concat(new String(dataArray));
                }
            }
            MapWrapper2 wrapper2 = new MapWrapper2();
            Gson gson2= new Gson();
            wrapper2 = gson2.fromJson(read_data, MapWrapper2.class);
            forumMap = wrapper2.getMyMap();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<RecipeObj> loadRecipes(int categoryId) {

        assetManager = context.getAssets();
//       if(categoryId==11){
//           return generateRecipesForAdv();
//       }
        return generateFromSerialized(CategoryHelper.getCategoryName(categoryId));
    }


    public ArrayList<RecipeObj> generateRecipesForAdv(){
        ArrayList<RecipeObj> recipeObjs=listAssetFiles("adv");
        return recipeObjs;
    }

    private ArrayList<RecipeObj> listAssetFiles(String categoryName) {
        recipeObjs = new ArrayList<RecipeObj>();
        boolean finished = false;
        int iterator = 0;
        while (finished == false) {
            try {
                String str = "";
                StringBuffer buf = new StringBuffer();
                InputStream is = assetManager.open(categoryName + "/" + String.valueOf(iterator + 1) + ".txt");
                //BufferedReader reader = new BufferedReader(new InputStreamReader(is, "windows-1251"));
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, detectEncoding(categoryName, iterator, assetManager)));
                if (is != null) {
                    while ((str = reader.readLine()) != null) {
                        buf.append(str);
                    }
                }
                is.close();
                Gson gson=new Gson();
                RecipeObj recipeObj=gson.fromJson(buf.toString(), RecipeObj.class);
                recipeObj.setCategoryName(categoryName);
                recipeObjs.add(recipeObj);
                iterator++;
            }catch (Exception ex){
                ex.printStackTrace();
                finished=true;
            }
        }
        return recipeObjs;

    }

    public ArrayList<RecipeObj> getTransformedRecipes(){
        assetManager = context.getAssets();
        for(int i=1;i<12;i++){
            if(i==9 || i==10){
                continue;
            }
            ArrayList<RecipeObj> transformedObjs=new ArrayList<>();
            transformedObjs=generateRecipes(CategoryHelper.getCategoryName(i));
            for(int x=1;x<transformedObjs.size()+1;x++){
                Morpher.writeToFileExternal("transformed/" + CategoryHelper.getCategoryName(i), x + ".txt", new Gson().toJson(transformedObjs.get(x-1)), context);
            }
        }
        return recipeObjs;
    }

    private ArrayList<String> listAssetFilesCommon(String categoryName) {
        ArrayList<String> strings = new ArrayList<String>();
        boolean finished = false;
        int iterator = 0;
        while (finished == false) {
            try {
                String str = "";
                StringBuffer buf = new StringBuffer();
                InputStream is = assetManager.open(categoryName + "/" + String.valueOf(iterator + 1) + ".txt");
                //BufferedReader reader = new BufferedReader(new InputStreamReader(is, "windows-1251"));
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, detectEncoding(categoryName, iterator, assetManager)));
                if (is != null) {
                    while ((str = reader.readLine()) != null) {
                        buf.append(str);
                    }
                }
                is.close();
                strings.add(str);
                iterator++;
            }catch (Exception ex){
                ex.printStackTrace();
                finished=true;
            }
        }
        return strings;

    }

    private ArrayList<RecipeObj> generateFromSerialized(String categoryName) {
        recipeObjs = new ArrayList<RecipeObj>();
        boolean finished = false;
        int iterator = 0;
        while (finished == false) {
            try {
                String str = "";
                StringBuffer buf = new StringBuffer();
                InputStream is = assetManager.open(categoryName + "/" + String.valueOf(iterator + 1) + ".txt");
                //BufferedReader reader = new BufferedReader(new InputStreamReader(is, "windows-1251"));
                String encoding= detectEncoding(categoryName,iterator,assetManager);
                if(encoding=="IBM855"){
                    iterator++;
                    continue;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, encoding));
                if (is != null) {
                    while ((str = reader.readLine()) != null) {
                        buf.append(str);
                    }
                }
                is.close();
                Gson gson=new Gson();
                RecipeObj recipeObj=gson.fromJson(buf.toString(), RecipeObj.class);
                recipeObj.setFileName(iterator+1);
                recipeObj.setCategoryName(categoryName);
                recipeObjs.add(recipeObj);
                iterator++;
            }catch (Exception ex){
                ex.printStackTrace();
                finished=true;
            }
        }
        return recipeObjs;
    }

    private ArrayList<RecipeObj> generateRecipes(String categoryName) {
        recipeObjs = new ArrayList<RecipeObj>();
        boolean finished = false;
        int iterator = 0;

        while (finished == false) {
            try {
                RecipeObj recipeObj = new RecipeObj();
                String str = "";
                StringBuffer buf = new StringBuffer();
                InputStream is = assetManager.open(categoryName + "/" + String.valueOf(iterator + 1) + ".txt");
                //BufferedReader reader = new BufferedReader(new InputStreamReader(is, "windows-1251"));
                String encoding= detectEncoding(categoryName,iterator,assetManager);

                BufferedReader reader = new BufferedReader(new InputStreamReader(is,encoding));
                if (is != null) {
                    while ((str = reader.readLine()) != null) {
                        buf.append(str + "\n");
                    }
                }
                is.close();
                recipeObj.setFileName(iterator + 1);
                recipeObj.setRecipeText(buf.toString());
                recipeObj.setCategoryName(categoryName);
                recipeObj.setFromForum(false);
                try {
                    if (favoritesMap.get(categoryName + String.valueOf(recipeObj.getFileName()))) {
                        recipeObj.setFavorited(true);
                    } else {
                        recipeObj.setFavorited(false);
                    }
                } catch (NullPointerException npe) {
                    favoritesMap.put(categoryName + String.valueOf(recipeObj.getFileName()), false);
                    recipeObj.setFavorited(false);
                }


                recipeObjs.add(recipeObj);
                iterator++;
            } catch (IOException e) {
                // e.printStackTrace();
                finished = true;
            }
        }
        return recipeObjs;

    }
    public static String detectEncoding(String categoryName, int iterator,AssetManager assetManager)  throws IOException {
        byte[] buf = new byte[4096];
        UniversalDetector detector=new UniversalDetector(null);
        InputStream is = assetManager.open(categoryName + "/" + String.valueOf(iterator + 1) + ".txt");
        int nread;
        while ((nread = is.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        if(encoding!=null && encoding.equals("MACCYRILLIC")){
            encoding="WINDOWS-1251";
        }else if(encoding==null) {
            Log.e("unknown encoding",""+iterator);
            encoding= "WINDOWS-1251";
        }
        return encoding;
    }

    public static String detectEncodingCommon(String name,AssetManager assetManager)  throws IOException {
        byte[] buf = new byte[4096];
        UniversalDetector detector=new UniversalDetector(null);
        InputStream is = assetManager.open(name);
        int nread;
        while ((nread = is.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        if(encoding!=null && encoding.equals("MACCYRILLIC")){
            encoding="WINDOWS-1251";
        }else if(encoding==null) {
            Log.e("unknown encoding",name);
            encoding= "WINDOWS-1251";
        }
        return encoding;
    }

    public void setFavorited(String categoryName, String fileName, boolean favorited) {
        favoritesMap.put(categoryName + fileName, favorited);
        saveFavorites();
    }
    public void setForumFavorited(String id, RecipeObj recipeObj){
        if(forumMap!=null) {
            forumMap.put(id, recipeObj);
        }
        else {
            forumMap=new LinkedHashMap<String, RecipeObj>();
            forumMap.put(id,recipeObj);
        }
    }
    public void setForumFavorited(String id,RecipeObj recipeObj, boolean truefalse){
        if(forumMap!=null) {
            recipeObj.setFavorited(false);
            forumMap.put(id,recipeObj);

        }
    }

    public void saveFavorites() {
        Gson gson = new Gson();
        MapWrapper wrapper = new MapWrapper();
        wrapper.setMyMap(favoritesMap);
        String serializedMap = gson.toJson(wrapper);
        SharedPreferences.Editor editor = context.getSharedPreferences("FAVORITES", Context.MODE_PRIVATE).edit();
        editor.putString("FAVORITES_MAP", serializedMap);
        editor.commit();

        MapWrapper2 wrapper2=new MapWrapper2();
        wrapper2.setMyMap(forumMap);
        String serializedforummap= gson.toJson(wrapper2);
        try {
            FileOutputStream fos= context.openFileOutput("forumfavs",Context.MODE_PRIVATE);
            fos.write(serializedforummap.getBytes());
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class MapWrapper2 {
        private LinkedHashMap<String, RecipeObj> myMap;

        public void setMyMap(LinkedHashMap<String, RecipeObj> myMap) {
            this.myMap = myMap;
        }

        public LinkedHashMap<String,RecipeObj> getMyMap() {
            return this.myMap;
        }

    }
    public class MapWrapper {
        private LinkedHashMap<String, Boolean> myMap;
        private LinkedHashMap<String,RecipeObj> forummap;

        public void setMyMap(LinkedHashMap myMap) {
            this.myMap = myMap;
        }

        public LinkedHashMap getMyMap() {
            return this.myMap;
        }

        public LinkedHashMap<String,RecipeObj> getForummap(){
            return this.forummap;
        }
        public void setForummap(LinkedHashMap<String,RecipeObj> forummap){
            this.forummap=forummap;
        }


    }

    public ArrayList<RecipeObj> loadFavorites() {

        assetManager = context.getAssets();
        return generateFavorites();
    }

    private ArrayList<RecipeObj> generateFavorites() {
        HashMap<Integer,ArrayList<RecipeObj>> allRecipeMap=SearchMapCreator.getInstance(context).allRecipeMap;

        recipeObjs = new ArrayList<RecipeObj>();
        for (Map.Entry<String, Boolean> entry : favoritesMap.entrySet()) {
                if (entry.getValue()) {
                    try {
                        RecipeObj recipeObj = allRecipeMap.get(CategoryHelper.getCategoryIdByName(entry.getKey().substring(0,3))).get(Integer.valueOf(entry.getKey().substring(3))-1);
                        recipeObj.setFavorited(true);
                        recipeObjs.add(recipeObj);
                    }catch (NullPointerException npe){
                        npe.printStackTrace();
                        Log.e("entry",entry.getKey());
                    }

                }
        }
        return loadForumFavorites(recipeObjs);
    }

    public int getFavoritesCount() {
        int favcount = 0;
        for (Map.Entry<String, Boolean> e : favoritesMap.entrySet()) {
            if (e.getValue()) {
                favcount++;
            }
        }
        try {
            if (forumMap!=null) {
                for (Map.Entry<String, RecipeObj> e : forumMap.entrySet()) {
                    if (e.getValue().isFavorited()) {
                        favcount++;
                    }
                }
            }
        }catch (NullPointerException npe){
            npe.printStackTrace();
            RecipeObj tempObj=new RecipeObj();
            tempObj.setFavorited(false);
            forumMap=new LinkedHashMap<String, RecipeObj>();
            forumMap.put("1",tempObj);
            saveFavorites();
        }
        return favcount;
    }

    private ArrayList<RecipeObj> loadForumFavorites(ArrayList<RecipeObj> recipeObjs) {

        if(forumMap!=null) {
            for (Map.Entry<String, RecipeObj> entry : forumMap.entrySet()) {
                if (entry.getValue().isFavorited()) {
                    RecipeObj recipeObj = new RecipeObj();
                    recipeObj.setFromForum(true);
                    recipeObj.setRecipeText(entry.getValue().getRecipeText());
                    recipeObj.setFileName(entry.getValue().getFileName());
                    recipeObj.setFavorited(true);
                    recipeObj.setRecipeName(entry.getValue().getRecipeName());
                    recipeObj.setPicurl(entry.getValue().getPicUrl());
                    recipeObj.setPhotos(entry.getValue().getPhotos());
                    recipeObjs.add(recipeObj);
                }
            }
        }
        return recipeObjs;
    }

    protected String getCuttedText(String originalText){
        try{
            return (originalText.substring(0,originalText.indexOf("\n")));
        }catch (StringIndexOutOfBoundsException e){
            return originalText;
        }
    }
}
