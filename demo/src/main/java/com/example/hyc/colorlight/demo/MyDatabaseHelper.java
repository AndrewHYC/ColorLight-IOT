package com.example.hyc.colorlight.demo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hyc on 18-5-25.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "LightStore.db";
    private static final int VERSION = 1;

    public static final String CREATE_LIGHT = "create table Light("
            +"name text,"
            +"type text,"
            +"isConfig integer,"
            +"id text)";
    private Context mContext;

    public MyDatabaseHelper(Context context){
        super(context, DB_NAME, null, VERSION);

        mContext = context;
    }

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_LIGHT);
//        Toast.makeText(mContext,"Open Successfully",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public void onOpen(SQLiteDatabase sqLiteDatabase){
        super.onOpen(sqLiteDatabase);
//        Toast.makeText(mContext,"Open Successfully",Toast.LENGTH_SHORT).show();
    }
}
