package com.example.andrew.mtgsearch;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Andrew on 5/1/2018.
 */

public class RecentCardDBContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private RecentCardDBContract() {}

    /* Inner class that defines the table contents */
    public static class RecentCardEntry implements BaseColumns {
        public static final String TABLE_NAME = "Recent";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_MANACOST = "manaCost";
        public static final String COLUMN_NAME_POWER = "power";
        public static final String COLUMN_NAME_TOUGHNESS = "toughness";
        public static final String COLUMN_NAME_TEXT = "text";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_URL = "imageURL";
    }

    public static class RulingsEntry implements BaseColumns {
        public static final String TABLE_NAME = "Rulings";
        public static final String COLUMN_NAME_CARDID = "cardId";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_RULING = "ruling";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_RECENTS =
            "CREATE TABLE " + RecentCardEntry.TABLE_NAME + " (" +
                    RecentCardEntry._ID + " INTEGER PRIMARY KEY," +
                    RecentCardEntry.COLUMN_NAME_NAME + TEXT_TYPE + " UNIQUE"
                    + COMMA_SEP +
                    RecentCardEntry.COLUMN_NAME_MANACOST + TEXT_TYPE
                    + COMMA_SEP +
                    RecentCardEntry.COLUMN_NAME_POWER + TEXT_TYPE
                    + COMMA_SEP +
                    RecentCardEntry.COLUMN_NAME_TOUGHNESS + TEXT_TYPE
                    + COMMA_SEP +
                    RecentCardEntry.COLUMN_NAME_TEXT + TEXT_TYPE
                    + COMMA_SEP +
                    RecentCardEntry.COLUMN_NAME_TYPE + TEXT_TYPE +
                    COMMA_SEP +
                    RecentCardEntry.COLUMN_NAME_URL + TEXT_TYPE + " )";
    private static final String SQL_CREATE_RULINGS =
            "CREATE TABLE " + RulingsEntry.TABLE_NAME + " (" +
                    RulingsEntry._ID + " INTEGER PRIMARY KEY," +
                    RulingsEntry.COLUMN_NAME_CARDID + " INTEGER" +
                    COMMA_SEP +
                    RulingsEntry.COLUMN_NAME_DATE + TEXT_TYPE +
                    COMMA_SEP +
                    RulingsEntry.COLUMN_NAME_RULING + TEXT_TYPE +" )";

    private static final String SQL_DELETE_RECENTS =
            "DROP TABLE IF EXISTS " + RecentCardEntry.TABLE_NAME;
    private static final String SQL_DELETE_RULINGS =
                    "DROP TABLE IF EXISTS " + RulingsEntry.TABLE_NAME;

    public static class RecentCardDBHelper extends
            SQLiteOpenHelper {
        //if the schema changes, the version must change
        public static final int DATABASE_VERSION = 3;
        public static final String DATABASE_NAME =
                "RecentCards.db";

        public RecentCardDBHelper(Context context) {
            super(context, DATABASE_NAME,
                    null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(SQL_CREATE_RECENTS);
            db.execSQL(SQL_CREATE_RULINGS);
        }
        public void onUpgrade(SQLiteDatabase db,
                              int oldVersion, int newVersion) {
            //onUpgrade, just start over
            db.execSQL(SQL_DELETE_RECENTS);
            db.execSQL(SQL_DELETE_RULINGS);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db,
                                int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
