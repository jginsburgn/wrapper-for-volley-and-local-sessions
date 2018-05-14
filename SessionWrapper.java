package me.itsof.volleywrapper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jonathan on 5/14/18.
 */

public class SessionWrapper {

    public SessionWrapper(Context context) {
        this.helper = new DbHelper(context);
        this.db = this.helper.getWritableDatabase();
    }

    public void logout() {
        db.execSQL(SQL_CLEAR_TABLE);
    }

    public boolean isLoggedIn() {
        JSONObject jo = getUserDetails();
        return jo.has("username");
    }

    public String getUserName() throws JSONException {
        JSONObject jo = getUserDetails();
        return jo.getString("username");
    }

    public void login(JSONObject user) {
        logout();
        ContentValues values = new ContentValues();
        values.put(DbEntries.COLUMN_NAME_KEY, "username");
        try {
            values.put(DbEntries.COLUMN_NAME_VALUE, user.getString("username"));
        }
        catch (Exception e) {}
        db.insert(DbEntries.TABLE_NAME, null, values);

        ContentValues values1 = new ContentValues();
        values1.put(DbEntries.COLUMN_NAME_KEY, "password");
        try {
            values1.put(DbEntries.COLUMN_NAME_VALUE, user.getString("password"));
        }
        catch (Exception e) {}
        db.insert(DbEntries.TABLE_NAME, null, values1);
    }

    public JSONObject getUserDetails() {
        String[] projection = {
                DbEntries._ID,
                DbEntries.COLUMN_NAME_KEY,
                DbEntries.COLUMN_NAME_VALUE
        };

        String selection = DbEntries.COLUMN_NAME_KEY + " in (?, ?)";
        String[] selectionArgs = {"username", "password"};

        Cursor cursor = db.query(
                DbEntries.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        JSONObject jo = new JSONObject();
        try {
            for (int i = 0; i < cursor.getCount(); ++i) {
                cursor.moveToNext();
                String key = cursor.getString(cursor.getColumnIndex(DbEntries.COLUMN_NAME_KEY));
                if (key.equals("username")) {
                    jo.put("username", cursor.getString(cursor.getColumnIndex(DbEntries.COLUMN_NAME_VALUE)));
                }
                else {
                    jo.put("password", cursor.getString(cursor.getColumnIndex(DbEntries.COLUMN_NAME_VALUE)));
                }
            }
            cursor.close();
        } catch (Exception e) {
        }
        return jo;
    }

    private DbHelper helper;
    private SQLiteDatabase db;

    /* Inner class that defines the table contents */
    public static class DbEntries implements BaseColumns {
        public static final String TABLE_NAME = "keyval";
        public static final String COLUMN_NAME_KEY = "key";
        public static final String COLUMN_NAME_VALUE = "value";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DbEntries.TABLE_NAME + " (" +
                    DbEntries._ID + " INTEGER PRIMARY KEY," +
                    DbEntries.COLUMN_NAME_KEY + " TEXT," +
                    DbEntries.COLUMN_NAME_VALUE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DbEntries.TABLE_NAME;

    private static final String SQL_CLEAR_TABLE = "DELETE FROM " + DbEntries.TABLE_NAME;

    public class DbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "local.db";

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}