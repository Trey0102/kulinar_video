package com.aristocrat.cooking;

import android.provider.BaseColumns;

/**
 * Created by Nursultan on 02.11.2015.
 */
public final class RecipeDBContract {

    public  static final String TEXT_TYPE = " TEXT";
    public static final String INT_TYPE = " INT";
    public static final String COMMA_SEP = ",";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public RecipeDBContract() {}

    /* Inner class that defines the table contents */
    public static abstract class Recipe implements BaseColumns {
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + Recipe.TABLE_NAME + " (" +
                        Recipe._ID + " INTEGER PRIMARY KEY," +
                        Recipe.COLUMN_NAME_RECIPE_ID + INT_TYPE + COMMA_SEP +
                        Recipe.COLUMN_NAME_RECIPE_NAME + TEXT_TYPE + COMMA_SEP +
                        Recipe.COLUMN_NAME_RECIPE_TEXT + TEXT_TYPE + COMMA_SEP +
                        Recipe.COLUMN_NAME_RECIPE_ATTACHMENTS + TEXT_TYPE +
                        // Any other options for the CREATE command
                        " )";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + Recipe.TABLE_NAME;

        public static final String TABLE_NAME = "recipes";
        public static final String COLUMN_NAME_RECIPE_ID = "recipeid";
        public static final String COLUMN_NAME_RECIPE_TEXT = "recipetext";
        public static final String COLUMN_NAME_RECIPE_NAME ="recipename";
        public static final String COLUMN_NAME_RECIPE_ATTACHMENTS = "attachments";

    }

    public static abstract class Category implements BaseColumns{

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE "+ Category.TABLE_NAME  + " (" +
                        Category._ID + " INTEGER PRIMARY KEY,"+
                        Category.COLUMN_NAME_CATEGORY_ID + INT_TYPE + COMMA_SEP +
                        Category.COLUMN_NAME_CATEGORY_NAME + TEXT_TYPE +
                        " )";
        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + Category.TABLE_NAME;

        public static final String TABLE_NAME = "categories";
        public static final String COLUMN_NAME_CATEGORY_ID = "categoryid";
        public static final String COLUMN_NAME_CATEGORY_NAME = "categoryname";


    }

    public static abstract class Relation implements BaseColumns{
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + Relation.TABLE_NAME + " (" +
                        Relation._ID +" INTEGER PRIMARY KEY," +
                        Relation.COLUMN_NAME_CATEGORY_ID + INT_TYPE + COMMA_SEP +
                        Relation.COLUMN_NAME_RECIPE_ID + INT_TYPE +
                        " )";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS "+ Relation.TABLE_NAME;

        public static final String TABLE_NAME = "relations";
        public static final String COLUMN_NAME_CATEGORY_ID = "categoryid";
        public static final String COLUMN_NAME_RECIPE_ID = "recipeid";

    }

    public static abstract class Attachment implements BaseColumns {

        public static final String SQL_CREATE_ENTRIES=
                "CREATE TABLE " + Attachment.TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        Attachment.COLUMN_NAME_RECIPE_ID + INT_TYPE + COMMA_SEP+
                        Attachment.COLUMN_NAME_POSITION + INT_TYPE + COMMA_SEP+
                        Attachment.COLUMN_NAME_URL + TEXT_TYPE +COMMA_SEP +
                        Attachment.COLUMN_NAME_WIDTH + INT_TYPE + COMMA_SEP+
                        Attachment.COLUMN_NAME_HEIGHT + INT_TYPE +
                        " )";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS "+ Attachment.TABLE_NAME;

        public static final String TABLE_NAME = "attachments";
        public static final String COLUMN_NAME_POSITION = "position";
        public static final String COLUMN_NAME_RECIPE_ID = "recipeid";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_WIDTH = "width";
        public static final String COLUMN_NAME_HEIGHT = "height";


    }

}


