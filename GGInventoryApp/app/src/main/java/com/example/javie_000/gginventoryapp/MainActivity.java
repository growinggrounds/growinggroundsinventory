package com.example.javie_000.gginventoryapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.javie_000.gginventoryapp.inventoryDB.weeklyDB;
import com.example.javie_000.gginventoryapp.mainDB.*;

import java.io.IOException;

public class MainActivity extends ActionBarActivity {

    private Button downloadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().hide();
        // Load the database if it hasn't been loaded
        loadSQLiteDB();

        // Button OnClickListeners
        setButtonListeners();
    }

    public void loadSQLiteDB(){
        masterDB db = new masterDB(this);

        // copy assets DB to app DB.
        try{
            db.create();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
    }

    public void setButtonListeners(){
        Button btnAvailListActivity = (Button) findViewById(R.id.avail_list_button);
        //Button btnDatabaseSync = (Button) findViewById(R.id.sync_master_db_button);
        //Button btnGGDataSync = (Button) findViewById(R.id.sync_avail_list_button);


        btnAvailListActivity.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //Starting a new Intent
                Intent AvailList = new Intent(getApplicationContext(), AvailListActivity.class);
                //Sending data to another Activity
                startActivity(AvailList);
            }
        });


        Button downloadButton = (Button)findViewById(R.id.sync_master_db_button);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weeklyDB dbWeekly;
                dbWeekly = new weeklyDB(getApplicationContext());
                dbWeekly.open();
                dbWeekly.exportCSV("weeklyinventory.csv");

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("CSV Export Success!")
                        .setMessage("Exported to /GrowingGroundsWeekly/weeklyinventory.csv")
                        .setNeutralButton(getString(R.string.neutral), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();

            }
        });
        /**
        btnDatabaseSync.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //Starting a new Intent
                Intent DatabaseSyncProvider = new Intent(getApplicationContext(), DatabaseSyncProvider.class);
                //Sending data to another Activity
                startActivity(DatabaseSyncProvider);
            }
        });*/

        /**
        btnGGDataSync.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //Starting a new Intent
                Intent GGDataSyncProvider = new Intent(getApplicationContext(), GGDataSyncProvider.class);
                //Sending data to another Activity
                startActivity(GGDataSyncProvider);
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
