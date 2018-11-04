package com.aristocrat.cooking;

import java.util.HashMap;

/**
 * Created by Nursultan on 21.09.2015.
 */
public class IngredientWrapper {

    private HashMap<String,Ingredient> ingredients;

    public IngredientWrapper(){

    }

    public HashMap<String,Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(HashMap<String,Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

}
