package com.aristocrat.cooking;

/**
 * Created by Nursultan on 29.09.2014.
 */
public class CategoryHelper {
    public static String getCategoryName(int categoryId){
        String categoryName;
        switch (categoryId){
            case 1: categoryName="hot";break;
            case 2: categoryName="snk";break;
            case 3: categoryName="brk";break;
            case 4: categoryName="des";break;
            case 5: categoryName="sup";break;
            case 6: categoryName="sld";break;
            case 7: categoryName="soc";break;
            case 8: categoryName="drk";break;
            case 9: categoryName="for";break;
            case 10: categoryName="bak";break;
            case 11:categoryName="adv";break;
            default: categoryName="hot";break;
        }
        return categoryName;
    }

    public static int getCategoryIdByName(String name){
        int categoryId;
        switch (name){
            case "hot": categoryId=1;break;
            case "snk": categoryId=2;break;
            case "brk": categoryId=3;break;
            case "des": categoryId=4;break;
            case "sup": categoryId=5;break;
            case "sld": categoryId=6;break;
            case "soc": categoryId=7;break;
            case "drk": categoryId=8;break;
            case "for": categoryId=9;break;
            case "bak": categoryId=10;break;
            case "adv": categoryId= 11;break;
            default: categoryId=1;break;
        }
        return categoryId;
    }
}
