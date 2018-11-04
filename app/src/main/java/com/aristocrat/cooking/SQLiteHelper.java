package com.aristocrat.cooking;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aristocrat.cooking.RecipeDBContract.Recipe;
import com.aristocrat.cooking.RecipeDBContract.Category;
import com.aristocrat.cooking.RecipeDBContract.Relation;
/**
 * Created by Nursultan on 02.11.2015.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    private static SQLiteHelper instance;

    public static void init(Context context){
        instance=new SQLiteHelper(context);
    }

    public static SQLiteHelper getInstance(){
        return instance;
    }


    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 7;
    public static final String DATABASE_NAME = "Recipes.db";


    private SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Recipe.SQL_CREATE_ENTRIES);
        db.execSQL(Category.SQL_CREATE_ENTRIES);
        db.execSQL(Relation.SQL_CREATE_ENTRIES);
        db.execSQL(RecipeDBContract.Attachment.SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(Recipe.SQL_DELETE_ENTRIES);
        db.execSQL(Category.SQL_DELETE_ENTRIES);
        db.execSQL(Relation.SQL_DELETE_ENTRIES);
        db.execSQL(RecipeDBContract.Attachment.SQL_DELETE_ENTRIES);
        onCreate(db);
    }



}
