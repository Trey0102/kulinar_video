package com.aristocrat.cooking;

/**
 * Created by Nursultan on 14.10.2015.
 */
public class Tag {

    private boolean isIngredient=true; // is a recipe name if false
    private String text;

    public Tag(String text, boolean isIngredient){
        this.text=text;
        this.isIngredient=isIngredient;
    }

    public boolean isIngredient() {
        return isIngredient;
    }

    public void setIsIngredient(boolean isIngredient) {
        this.isIngredient = isIngredient;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
