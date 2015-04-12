package com.example.javie_000.gginventoryapp.inventoryDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.webkit.WebChromeClient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class weeklyDB {
    // Default system path of the weekly database
    private static final String DB_PATH = "data/data/com.example.javie_000.gginventoryapp/databases/";
    private static final String DB_NAME = "weeklyInventory";
    private static final String TAG = "weeklyDB";
    private static final int DB_VERSION = 1;

    // weeklyDB field key names
    public static final String KEY_ROWID = "_id";
    public static final String KEY_TAGNAME = "_tagName";
    public static final String KEY_BOTANICALNAME = "_botanicalName";
    public static final String KEY_COMMONNAME = "_commonName";
    public static final String KEY_SIZE = "_size";
    public static final String KEY_NUMAVAIL = "_numAvail";
    public static final String KEY_DETAILS = "_details";
    public static final String KEY_NOTES = "_notes";
    public static final String KEY_TYPE1 = "_type1";
    public static final String KEY_TYPE2 = "_type2";
    public static final String KEY_QUALITY = "_quality";
    public static final String KEY_LOCATION = "_location";
    public static final String KEY_PRICE = "_price";

    public static final String TABLE_NAME = "availList";
    public static final String[] ALL_KEYS = {
            KEY_ROWID, KEY_TAGNAME, KEY_BOTANICALNAME, KEY_COMMONNAME, KEY_SIZE, KEY_NUMAVAIL,
            KEY_DETAILS, KEY_NOTES, KEY_TYPE1, KEY_TYPE2, KEY_QUALITY, KEY_LOCATION, KEY_PRICE
    };

    // Database creation sql statement.
    // SQLite standard data types are the following:
    //      null, integer, real-floating point, text, blob
    private static final String DB_CREATE = "CREATE TABLE "
            + TABLE_NAME + "("
            + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_TAGNAME + " TEXT, "
            + KEY_BOTANICALNAME + " TEXT, "
            + KEY_COMMONNAME + " TEXT, "
            + KEY_SIZE + " INTEGER, "
            + KEY_NUMAVAIL + " INTEGER, "
            + KEY_DETAILS + " TEXT, "
            + KEY_NOTES + " TEXT, "
            + KEY_TYPE1 + " TEXT, "
            + KEY_TYPE2 + " TEXT, "
            + KEY_QUALITY + " TEXT, "
            + KEY_LOCATION + " TEXT, "
            + KEY_PRICE + " REAL"
            + ");";

    private final Context context;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    // Database helper class.
    private static class DatabaseHelper extends SQLiteOpenHelper{
        DatabaseHelper(Context context){
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            Log.w(TAG, DB_CREATE);
            db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            Log.w(TAG, "Upgrading database from version " + oldVersion + "to " + newVersion
                + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        }
    }

    // Constructor
    public weeklyDB(Context context) {
        this.context = context;
    }

    // Open the database connection
    public weeklyDB open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        if (dbHelper != null){
            dbHelper.close();
        }
        Log.w(TAG, "Weekly DB has been closed....");
    }

    // Add a new record to the database.
    public long createRecord(WeeklyRecord record){
        ContentValues values = new ContentValues();

        //values.put(KEY_ROWID, record.id);
        values.put(KEY_TAGNAME, record.tagName);
        values.put(KEY_BOTANICALNAME, record.botanicalName);
        values.put(KEY_COMMONNAME, record.commonName);
        values.put(KEY_SIZE, record.size);
        values.put(KEY_NUMAVAIL, record.numAvail);
        values.put(KEY_DETAILS, record.details);
        values.put(KEY_NOTES, record.notes);
        values.put(KEY_TYPE1, record.type1);
        values.put(KEY_TYPE2, record.type2);
        values.put(KEY_QUALITY, record.quality);
        values.put(KEY_LOCATION, record.location);
        values.put(KEY_PRICE, record.price);

        return db.insert(TABLE_NAME, null, values);
    }

    // Delete a record from the database, by rowID
    public boolean deleteRecord(int rowid){
        String whereClause = KEY_ROWID + "=" + rowid;
        return db.delete(TABLE_NAME, whereClause, null) != 0;
    }

    public boolean deleteAllRecords(){
        int doneDelete = 0;
        doneDelete = db.delete(TABLE_NAME, null, null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;
    }

    public boolean checkID(int rowID){
        Log.w(TAG, Long.toString(rowID));
        String whereClause = KEY_ROWID + "=" + rowID;
        Cursor cursor = db.query(true, TABLE_NAME, ALL_KEYS,
                whereClause, null, null, null, null, null);

        cursor.moveToFirst();
        boolean status = false;
        if(!cursor.isAfterLast()){
            status = true;
        }

        return status;
    }

    // Get a specific record (by rowID)
    public Cursor getRecord(int rowID) throws SQLException{
        Log.w(TAG, Long.toString(rowID));
        String whereClause = KEY_ROWID + "=" + rowID;
        Cursor cursor = db.query(true, TABLE_NAME, ALL_KEYS,
                whereClause, null, null, null, null, null);

        if(cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    // Return all records in the database
    public Cursor getAllRecords(){
        Cursor cursor = db.query(true, TABLE_NAME, ALL_KEYS, null,
                null, null, null, KEY_BOTANICALNAME, null);

        if(cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    // Change an existing record to new record
    public boolean updateRecord(WeeklyRecord record){
        String whereClause = KEY_ROWID + "=" + record.id;
        ContentValues values = new ContentValues();

        values.put(KEY_ROWID, record.id);
        values.put(KEY_TAGNAME, record.tagName);
        values.put(KEY_BOTANICALNAME, record.botanicalName);
        values.put(KEY_COMMONNAME, record.commonName);
        values.put(KEY_SIZE, record.size);
        values.put(KEY_NUMAVAIL, record.numAvail);
        values.put(KEY_DETAILS, record.details);
        values.put(KEY_NOTES, record.notes);
        values.put(KEY_TYPE1, record.type1);
        values.put(KEY_TYPE2, record.type2);
        values.put(KEY_QUALITY, record.quality);
        values.put(KEY_LOCATION, record.location);
        values.put(KEY_PRICE, record.price);

        // Insert new record into the database
        return db.update(TABLE_NAME, values, whereClause, null) != 0;
    }
}

