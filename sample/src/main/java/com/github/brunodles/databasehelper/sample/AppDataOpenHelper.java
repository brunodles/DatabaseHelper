package com.github.brunodles.databasehelper.sample;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.brunodles.databasehelper.annotation.InjectCreate;
import com.github.brunodles.databasehelper.annotation.SqlHelper;
import com.github.brunodles.databasehelper.annotation.SqlHelpers;


/**
 * Created by bruno on 14/10/16.
 */
//@SqlHelpers({
//        @SqlHelper(value = User.class,
//                fieldGetter = SqlHelper.FieldGetter.FIELD,
//                fieldSetter = SqlHelper.FieldSetter.FIELD),
//        @SqlHelper(Href.class)
//})
public class AppDataOpenHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;

    public AppDataOpenHelper(Context context) {
        super(context, "sample.db", null, VERSION);
    }

    @Override
//    @InjectCreate
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        YayMigration.onUpgrade(db, oldVersion, newVersion);
    }
}
