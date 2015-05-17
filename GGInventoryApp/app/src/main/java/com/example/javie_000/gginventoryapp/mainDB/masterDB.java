package com.example.javie_000.gginventoryapp.mainDB;

import android.database.sqlite.SQLiteOpenHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class masterDB {

    // Default system path of the application database.
    private static final String DB_PATH = "data/data/com.example.javie_000.gginventoryapp/databases/";
    private static final String DB_NAME = "masterInventory";
    private static final int DB_VERSION = 1;
    private static final String TAG = "masterDB";

    // masterDB field key names
    public static final String KEY_ROWID = "_id";
    public static final String KEY_TAGNAME = "_tagName";
    public static final String KEY_BOTANICALNAME = "_botanicalName";
    public static final String KEY_COMMONNAME = "_commonName";
    public static final String KEY_DESCRIPTION = "_description";
    public static final String KEY_USDAZONE = "_usdaZone";
    public static final String KEY_TYPE1 = "_type1";
    public static final String KEY_TYPE2 = "_type2";
    public static final String KEY_DROUGHTTOLERANT = "_droughtTolerant";

    public static final String TABLE_NAME = "inventory";
    public static final String[] ALL_KEYS = {
            KEY_ROWID, KEY_TAGNAME, KEY_BOTANICALNAME, KEY_COMMONNAME, KEY_DESCRIPTION, KEY_USDAZONE,
            KEY_TYPE1, KEY_TYPE2, KEY_DROUGHTTOLERANT
    };

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
            Log.w(TAG, "DatabaseHelper has been created.....");
            //db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            Log.w(TAG, "Upgrading database from version " + oldVersion + "to " + newVersion
                    + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        }
    }

    // Constructor.
    public masterDB(Context context) {
        this.context = context;

        /////////////
        Log.w(TAG, "Master DB has been instantiated.");
        /////////////
    }

    // Open the database
    public masterDB open() throws SQLException{
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getReadableDatabase();
        return this;
    }

    public void close() {
        if (dbHelper != null){
            dbHelper.close();
        }
        Log.w(TAG, "Master DB has been closed....");
    }

    // Creates an empty database on the system and rewrites it with our database.
    public void create() throws IOException{
        boolean dbExist = checkDatabase();

        if(dbExist){
            //do nothing - database already exists
            Log.w(TAG, "DB already exists....");
        }else{
            //Call method to create an empty database into the default system path
            dbHelper = new DatabaseHelper(context);
            db = dbHelper.getReadableDatabase();
            db.close();

            try{
                copyDatabase();
            } catch (IOException e){
                throw new Error("Error copying database");
            }
            ///////////////
            Log.w(TAG, "DB has been copied....");
            ///////////////
        }
    }

    public boolean checkDatabase(){
        SQLiteDatabase checkDB = null;

        try{
            String path = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            // database doesn't exist yet.
            e.printStackTrace();
        }

        if(checkDB != null){
            checkDB.close();
            return true;
        } else{
            return false;
        }
    }

    // Copy our assets db to the new system DB
    private void copyDatabase() throws IOException{
        //Open our local db as the input stream
        InputStream myInput = context.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        // Transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0){
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }


    // Public methods to access DB content
    // -----------------------------------

    // Get a specific record (by tagName)
    public Cursor getRecord(String tagName) throws SQLException{
        Log.w(TAG, "masterDB has been opened to get record. Looking for tagName " + tagName);
        String whereClause = KEY_TAGNAME + "=" + "'" + tagName + "'";
        Cursor cursor = db.query(true, TABLE_NAME, ALL_KEYS,
                whereClause, null, null, null, null, null);

        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    // Get Master Records
    public List<MasterRecord> getRecords(){
        List<MasterRecord> records = null;

        try {
            String query = "SELECT * FROM " + TABLE_NAME;
            SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
            Cursor cursor = db.rawQuery(query, null);

            // go over each row, build elements and add it to list
            records = new LinkedList<MasterRecord>();

            if (cursor.moveToFirst()){
                do{
                    MasterRecord record = new MasterRecord();
                    record.id = cursor.getInt(0);
                    record.botanicalName = cursor.getString(1);
                    record.commonName = cursor.getString(2);
                    record.usdaZone = cursor.getString(3);
                    record.type1 = cursor.getString(4);
                    record.type2 = cursor.getString(5);
                    record.droughtTolerant = cursor.getString(6);

                    records.add(record);
                } while (cursor.moveToNext());
            }
        } catch(Exception e) {
            // sql error
        }

        return records;
    }

    // Return all records in the database
    public Cursor getAllRecords(){
        //String whereClause = null;
        //Cursor cursor = db.query(true, TABLE_NAME, ALL_KEYS, whereClause,
        //        null, null, null, null, null);
        Cursor cursor = db.query(true, TABLE_NAME, ALL_KEYS, null,
                null, null, null, null, null);

        if(cursor != null)
            cursor.moveToFirst();

        return cursor;
    }
}
