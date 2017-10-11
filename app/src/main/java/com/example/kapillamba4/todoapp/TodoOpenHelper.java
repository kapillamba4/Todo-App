package com.example.kapillamba4.todoapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by kapil on 29/9/17.
 */

public class TodoOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "todo_list.db";
    private static TodoOpenHelper mInstance = null;

    private TodoOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static TodoOpenHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TodoOpenHelper(context);
        }

        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + Contract.TODO_TABLE_NAME + " ( " +
                Contract.TODO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Contract.TODO_TITLE + " TEXT, " +
                Contract.TODO_DATE + " TEXT, " +
                Contract.TODO_TIME + " TEXT, " +
                Contract.TODO_CREATED + " INTEGER, " +
                Contract.TODO_ACCESSED + " INTEGER, " +
                Contract.TODO_CONTENT + " TEXT)";

        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // TODO
    }
}
