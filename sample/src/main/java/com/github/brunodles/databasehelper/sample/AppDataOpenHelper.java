package com.github.brunodles.databasehelper.sample;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.brunodles.databasehelper.annotation.CreateTable;
import com.github.brunodles.databasehelper.annotation.InjectCreate;


/**
 * Created by bruno on 14/10/16.
 */

@CreateTable(value = User.class,
        fieldGetter = CreateTable.FieldGetter.FIELD,
        fieldSetter = CreateTable.FieldSetter.FIELD)
public class AppDataOpenHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;

    public AppDataOpenHelper(Context context) {
        super(context, "sample.db", null, VERSION);
    }

    @Override
    @InjectCreate
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(UserSqlHelper.CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
