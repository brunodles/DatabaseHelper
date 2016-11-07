package com.github.brunodles.databasehelper.sample;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SQLiteDatabase writableDatabase = new AppDataOpenHelper(this).getWritableDatabase();
        writableDatabase.execSQL("SELECT * FROM " + UserSqlHelper.TABLE_NAME);
    }
}
