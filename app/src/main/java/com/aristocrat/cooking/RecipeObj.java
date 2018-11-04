package com.aristocrat.cooking;


import com.aristocrat.cooking.vkgroup.Photo;

import java.util.ArrayList;

public class RecipeObj {
    private int fileName;
    private String recipeText;
    private String recipeName;
    private String categoryName;
    private boolean isFavorited;
    private ArrayList<Photo> photos;

    private String picUrl;
    private boolean fromForum=false;

    public boolean isFromForum() {
        return fromForum;
    }

    public void setFromForum(boolean fromForum) {
        this.fromForum = fromForum;
    }
    public void setRecipeName(String recipeName){
        this.recipeName=recipeName;
    }


    public String getPicUrl() {
        return picUrl;
    }

    public void setPicurl(String picUrl) {
        this.picUrl = picUrl;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }
    public void setPhotos(ArrayList<Photo>  photos) {
        this.photos = photos;
    }




    public int getFileName() {
        return fileName;
    }
    public String getRecipeText() {
        return recipeText;
    }
    public String getRecipeName(){
        return recipeName;
    }

    public void setFileName(int fileName) {
        this.fileName = fileName;
    }

    public void setRecipeText(String recipeText) {
        if(recipeText!="") {
            this.recipeText = recipeText;
            int breakindex = this.recipeText.indexOf("\n");
            if (breakindex > 2) {
                recipeName = this.recipeText.substring(0, breakindex);
                this.recipeText = this.recipeText.substring(breakindex);
            }
        }
        else {
            this.recipeText = "1";
            recipeName ="1";
        }

    }
    public void setCategoryName(String categoryName){
        this.categoryName=categoryName;
    }
    public String getCategoryName(){
        return this.categoryName;
    }

    public void setFavorited(boolean isFavorited){
        this.isFavorited=isFavorited;
    }
    public boolean isFavorited(){
        return isFavorited;
    }
}
