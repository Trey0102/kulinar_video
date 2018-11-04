package com.aristocrat.cooking;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Nursultan on 30.09.2015.
 */
public class BakedMapsWrapper {

    public HashMap< Integer, HashMap<String,ArrayList<SearchItem>>> searchMap;
    public HashMap <Integer,ArrayList<RecipeObj> >  allRecipeMap;
    public HashMap<String,Boolean> existingmap;
    public HashMap<String, SearchItem > recipeTitleMap;

}
