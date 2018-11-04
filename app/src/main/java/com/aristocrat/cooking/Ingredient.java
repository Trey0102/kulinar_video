package com.aristocrat.cooking;


import java.util.ArrayList;

/**
 * Created by Nursultan on 21.09.2015.
 */
public class Ingredient {

    private String name;
    private ArrayList<String> variants;

    public Ingredient(String name, ArrayList<String> variants){
        this.name=name;
        this.variants=variants;
    }

    public String getName(){
        return name;
    }

    public ArrayList<String> getVariants(){
        return variants;
    }

    public boolean existsInRecipe(String recipetext){
        String recipe=recipetext.toLowerCase();
        if(recipe.contains(name+" ")){
            return true;
        }
        for(String item:variants){
            if(recipe.contains(item+" ")){
                return true;
            }else if(item.contains(" ")){
                String[] split= item.split(" ");
                String reversed="";
                for(String word:split){
                    reversed=(word+" "+reversed).trim();
                }
                if(recipe.contains(reversed+" ")){
                    return true;
                }
            }
        }
        return false;
    }

}
