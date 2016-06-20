package de.rwth.pulsuhr.pulsuhr;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by steffen on 16.06.16.
 */
public class SqlHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PulsUhrData.db";
    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE Data (Timestamp INTEGER PRIMARY KEY, Measurement BLOB, Puls INTEGER, Comment TEXT, Ranking INTEGER)";

    public SqlHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
       // db.execSQL(SQL_DELETE_ENTRIES);
        //onCreate(db);
    }
}
