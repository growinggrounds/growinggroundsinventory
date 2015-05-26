package com.example.javie_000.gginventoryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.text.TextWatcher;
import android.text.Editable;
import java.util.ArrayList;
import android.widget.FilterQueryProvider;
import android.widget.Toast;

import com.example.javie_000.gginventoryapp.inventoryDB.weeklyDB;
import com.example.javie_000.gginventoryapp.mainDB.masterDB;

public class AvailListActivity extends Activity {

    private static final String TAG = "availList";
    private weeklyDB db;
    private SimpleCursorAdapter dataAdapter;
    private EditText scanBarcode;
    private Button searchButton;
    //private Button clearAllButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avail_list);
        getActionBar().hide();
        // Hide the keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Open the weekly database
        db = new weeklyDB(this);
        Log.w(TAG, "Weekly DB has been instantiated.....");
        db.open();
        Log.w(TAG, "Weekly DB has been opened....");

        // Generate ListView from SQLite Database
        displayListView();
    }

    @Override
    public void onResume() {
        super.onStart();
        scanBarcode.setText("");
        displayListView();
    }

    public Cursor getList (CharSequence constraint)  {
        if (constraint == null || constraint.length() == 0) {
            return db.getAllRecords();
        }
        else {
            String value = "%"+constraint.toString()+"%";
            return db.query(weeklyDB.TABLE_NAME, weeklyDB.ALL_KEYS, "_botanicalName like ? ", new String[]{value}, null, null, null);
        }
    }


    private void displayListView(){
        Cursor cursor;
        String[] columns;
        final ListView listView;

        cursor = db.getAllRecords();
        Log.w(TAG, "All records have been retrieved.....");

        // The desired columns to be bound
        columns = new String[]{
                weeklyDB.KEY_BOTANICALNAME,
                weeklyDB.KEY_NUMAVAIL,
                weeklyDB.KEY_SIZE,
                weeklyDB.KEY_QUALITY,
                weeklyDB.KEY_DETAILS,
                weeklyDB.KEY_LOCATION
        };

        // The XML defined views which the data will be bound to
        int[] to = new int[]{
                R.id.listRowBotanicalName,
                R.id.listRowQuantity,
                R.id.listRowSize,
                R.id.listRowQuality,
                R.id.listRowDetails,
                R.id.listRowLocation
        };

        // Create the adapter using the cursor pointing to the desired data as well as the
        // layout information
        dataAdapter = new SimpleCursorAdapter(
                this,                   // Context
                R.layout.list_row,      // Row layout template
                cursor,                 // cursor
                columns,                // DB column names
                to);                     // view IDs

        listView = (ListView) findViewById(R.id.listView);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
        listView.setTextFilterEnabled(true);
        listView.setFastScrollEnabled(true);
        Log.w(TAG, "ListView dataAdapter has been set....");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                // Get the record's ID from this row in the database.
                int rowID = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                Log.w(TAG, "RowID to pass to RecordScreen activity is " + rowID);
                //Toast.makeText(getApplicationContext(), rowID, Toast.LENGTH_SHORT).show();

                //Starting a new Intent with Bundle
                Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
                //Send the rowId to recordScreen
                intent.putExtra("rowID", rowID);
                intent.putExtra("fromBarcode", false);
                Log.w(TAG, "Row ID " + rowID + " being sent to RECORD SCREEN activity...");
                //Sending data to another Activity
                startActivity(intent);
            }
        });

        searchButton = (Button)findViewById(R.id.searchButton);
        /*clearAllButton = (Button)findViewById(R.id.clearAllButton);
        clearAllButton.setVisibility(View.GONE);*/
        scanBarcode = (EditText)findViewById(R.id.scanBarcodeEditText);
        scanBarcode.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                ListView av = (ListView) findViewById(R.id.listView);
                SimpleCursorAdapter filterAdapter = (SimpleCursorAdapter) av.getAdapter();
                filterAdapter.getFilter().filter(s.toString());
            }
        });

        dataAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                return getList(constraint);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message;
                masterDB dbMaster;

                dbMaster = new masterDB(AvailListActivity.this);
                dbMaster.open();
                Log.w(TAG, "Master DB has been opened....");
                Cursor cursor = dbMaster.getRecord(scanBarcode.getText().toString());

                if (scanBarcode.getText().length() == 0) {
                    message = "Empty entry, must get input from scanner!";
                    Toast.makeText(AvailListActivity.this, message, Toast.LENGTH_SHORT).show();

                } else if (cursor == null || cursor.getCount() == 0) {
                    message = "Inventory does not contain " + scanBarcode.getText().toString();
                    Toast.makeText(AvailListActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
                    // Send the scanned rowID to recordScreen
                    //intent.putExtra("sentValue", Integer.parseInt(scanBarcode.getText().toString()));
                    intent.putExtra("tagName", scanBarcode.getText().toString());
                    intent.putExtra("fromBarcode", true);
                    Log.w(TAG, "Tag Name " + scanBarcode.getText() + " being sent to recordScreen....");
                    // Sending data to another Activity
                    startActivity(intent);
                }
            }
        });


        /*clearAllButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Log.w(TAG, "clearAll Button pressed....");
                db.deleteAllRecords();
                Log.w(TAG, "Availability List cleared...");

                // Refresh Activity
                finish();
                startActivity(getIntent());
            }
        });*/
    }
}