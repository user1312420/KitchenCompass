package com.app.kitchencompass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MyDatebaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "OfflineRecipe.db";
    private static final int DATABASE_VERSION = 4;


    private static final String TABLE_NAME = "my_recipe";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "recipe_name";
    private static final String COLUMN_INGREDIENTS = "recipe_ingredients";
    private static final String COLUMN_INSTRUCTIONS = "recipe_instructions";
    private static final String COLUMN_TIME = "recipe_time";
    private static final String COLUMN_IMAGE = "recipe_image";


    public MyDatebaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_INGREDIENTS + " TEXT, " +
                COLUMN_TIME + " TEXT, " +
                COLUMN_INSTRUCTIONS + " TEXT, " +
                COLUMN_IMAGE + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    void addFavorites(String name, String ingredients, String time, String instructions, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_INGREDIENTS, ingredients);
        cv.put(COLUMN_TIME, time);
        cv.put(COLUMN_INSTRUCTIONS, instructions);
        cv.put(COLUMN_IMAGE, image);
        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Failed to insert", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(context, "Added successfully", Toast.LENGTH_SHORT).show();
    }

     public List <Recipe> getAllRecipes() {
         List<Recipe> recipeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String ingredients = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENTS));
                String instructions = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSTRUCTIONS));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME));
                String image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE));

                Recipe recipe = new Recipe(id, name, ingredients, instructions, time, image);
                recipeList.add(recipe);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return recipeList;
    }

    public void deleteRecipe(int recipeID){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = " + recipeID;
        db.execSQL(query);

        Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
    }
}
