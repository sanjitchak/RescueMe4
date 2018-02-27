package com.example.kavya.rescueme4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class MyDBHandler extends SQLiteOpenHelper {

    public static final String TABLE_USER = "userinfo";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_EMAIL = "email";
    private static final String TAG = "somuMessage";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "userDB.db";
    SQLiteDatabase db;

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_USER + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY , " +
                COLUMN_NAME + " TEXT, " + COLUMN_EMAIL + " TEXT,"+COLUMN_PHONE+" TEXT"+
                ");";

        try {
            sqLiteDatabase.execSQL(query);
            db = sqLiteDatabase;



        }
        catch (Exception e)
        {
            Log.i(TAG, "Create Table error: "+e.getMessage());
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(sqLiteDatabase);
    }



    void setDetails(String name, String email, String phone){


        try {
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("DELETE FROM " + TABLE_USER );

            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, name);
            values.put(COLUMN_EMAIL,email);
            values.put(COLUMN_PHONE,phone);

            db.insert(TABLE_USER, null, values);
            db.close();
            Log.i(TAG, "Data added " );
        }
        catch (Exception e)
        {

            Log.i(TAG, "DB Error: "+e.getMessage());
        }

    }
}
