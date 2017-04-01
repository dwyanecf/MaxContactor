package com.dwyanecf.maxcontactor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by fan on 2016/3/23.
 */
import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;

public class FileOp extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "gcontacts.db";
    public static final int DATABASE_VERSION = 2;
    public static final String CONTACTS_TABLE = "contacts";
    //创建数据库
    private static final String DATABASE_CREATE = "CREATE TABLE " + CONTACTS_TABLE +" ("
            + ContactColumn._ID+" integer primary key autoincrement,"
            + ContactColumn.NAME+" text,"
            + ContactColumn.MOBILE+" text,"
            + ContactColumn.EMAIL+" text,"
            + ContactColumn.CREATED+" long,"
            + ContactColumn.MODIFIED+" long);";


    public FileOp(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE);
        onCreate(db);
    }

}
