package com.example.javie_000.gginventoryapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.*;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.*;

import com.example.javie_000.gginventoryapp.inventoryDB.*;
import com.example.javie_000.gginventoryapp.mainDB.*;

//import sun.rmi.runtime.Log;

public class RecordActivity extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener{

    private static final String TAG = "recordActivity";
    public WeeklyRecord record;
    private weeklyDB dbWeekly;
    private masterDB dbMaster;
    private int rowID;
    private String tagName;
    private boolean fromBarcode;

    private TextView ViewBotanicalName, ViewCommonName, ViewType1, ViewType2;
    private EditText ETnumAvail, ETprice;
    private Spinner SpinnerSize, SpinnerNotes, SpinnerQuality, SpinnerLocation, SpinnerDetails, SpinnerDetails2, SpinnerDetails3;
    private Button saveButton, deleteButton, priceButton;

    // weeklyDB column numbers
    private static final int W_ROWID = 0;
    private static final int W_TAGNAME = 1;
    private static final int W_BOTANICALNAME = 2;
    private static final int W_COMMONNAME = 3;
    private static final int W_SIZE = 4;
    private static final int W_NUMAVAIL = 5;
    private static final int W_DETAILS = 6;
    private static final int W_NOTES = 7;
    private static final int W_TYPE1 = 8;
    private static final int W_TYPE2 = 9;
    private static final int W_QUALITY = 10;
    private static final int W_LOCATION = 11;
    private static final int W_PRICE = 12;

    // masterDB column numbers
    private static final int M_ROWID = 0;
    private static final int M_TAGNAME = 1;
    private static final int M_BOTANICALNAME = 2;
    private static final int M_COMMONNAME = 3;
    private static final int M_DESCRIPTION = 4;
    private static final int M_USDAZONE = 5;
    private static final int M_TYPE1 = 6;
    private static final int M_TYPE2 = 7;
    private static final int M_DROUGHTTOLERANT = 8;

    private static String detail1 = "";
    private static String detail2 = "";
    private static String detail3 = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        getActionBar().hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Log.w(TAG, "Record Activity view has been set....");

        // Obtain the rowID from Availability List Activity
        RetrieveIntentValue();

        // Open weekly database
        dbWeekly = new weeklyDB(this);
        Log.w(TAG, "Weekly DB has been instantiated.....");
        dbWeekly.open();
        Log.w(TAG, "Weekly DB has been opened....");

        // fromBarcode = true means that the String tag name value is being used.
        if(fromBarcode){
            Log.w(TAG, "Adding plant record from masterDB....");
            AddWithMasterDB();
        }
        // fromBarcode = false means that the Integer rowID value is being used.
        else{
            Log.w(TAG, "Editing plant record from weeklyDB...");
            EditWithWeeklyDB();
        }
        SetViewsAndListeners();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to go back without saving?")
                .setPositiveButton(this.getString(R.string.positive), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton(this.getString(R.string.negative), null)
                .show();
    }

    private void RetrieveIntentValue(){
        Intent intent = getIntent();
        rowID = intent.getIntExtra("rowID", 0);
        tagName = intent.getStringExtra("tagName");
        fromBarcode = intent.getBooleanExtra("fromBarcode", true);

        Log.w(TAG, "RowID " + rowID + " has been passed into RecordActivity....");
        Log.w(TAG, "TagName " + tagName + " has been passed into RecordActivity....");
        Log.w(TAG, "Value is from the barcode as a String: " + fromBarcode);
    }

    private void EditWithWeeklyDB(){
        // Create a new empty WeeklyRecord
        record = new WeeklyRecord();
        Cursor cursor = dbWeekly.getRecord(rowID);

        // Fill in record with weeklyDB values
        record.id = cursor.getInt(W_ROWID);
        record.tagName = cursor.getString(W_TAGNAME);
        record.botanicalName = cursor.getString(W_BOTANICALNAME);
        record.commonName = cursor.getString(W_COMMONNAME);
        record.size = cursor.getString(W_SIZE);
        record.numAvail = cursor.getInt(W_NUMAVAIL);
        record.details = cursor.getString(W_DETAILS);
        record.notes = cursor.getString(W_NOTES);
        record.type1 = cursor.getString(W_TYPE1);
        record.type2 = cursor.getString(W_TYPE2);
        record.quality = cursor.getString(W_QUALITY);
        record.location = cursor.getString(W_LOCATION);
        record.price = cursor.getDouble(W_PRICE);
    }

    private void AddWithMasterDB(){
        String message;

        // Create a new empty WeeklyRecord
        record = new WeeklyRecord();
        dbMaster = new masterDB(this);
        dbMaster.open();
        Log.w(TAG, "Master DB has been opened....");
        Cursor cursor = dbMaster.getRecord(tagName);

/*        if(cursor == null || cursor.getCount() == 0) {
            message = "Inventory does not contain " + tagName;
            Toast.makeText(RecordActivity.this, message, Toast.LENGTH_SHORT).show();
            return;
        }*/

        Log.w(TAG, "TagName " + tagName + " has been retrieved from masterDB");
        dbMaster.close();
        Log.w(TAG, "Master DB has been closed....");

        // Fill in record with masterDB values
        //record.id = cursor.getInt(M_ROWID);
        record.tagName = cursor.getString(M_TAGNAME);
        record.botanicalName = cursor.getString(M_BOTANICALNAME);
        record.commonName = cursor.getString(M_COMMONNAME);
        record.size = "";
        record.numAvail = 0;
        record.details = " / / ";
        record.notes = "";
        record.type1 = cursor.getString(M_TYPE1);
        record.type2 = cursor.getString(M_TYPE2);
        record.quality = "";
        record.location = "";
        record.price = 0;
    }

    private void SetViewsAndListeners(){
        // Set up View Ids
        ViewBotanicalName = (TextView)findViewById(R.id.ViewBotanicalName);
        ViewCommonName = (TextView)findViewById(R.id.ViewCommonName);
        SpinnerSize = (Spinner)findViewById(R.id.SpinnerSize);
        ETnumAvail = (EditText)findViewById(R.id.ETnumAvail);
        SpinnerDetails = (Spinner)findViewById(R.id.SpinnerDetails);
        SpinnerDetails2 = (Spinner)findViewById(R.id.SpinnerDetails2);
        SpinnerDetails3 = (Spinner) findViewById(R.id.SpinnerDetails3);
        SpinnerNotes = (Spinner)findViewById(R.id.SpinnerNotes);
        ViewType1 = (TextView)findViewById(R.id.ViewType1);
        ViewType2 = (TextView)findViewById(R.id.ViewType2);
        SpinnerQuality = (Spinner)findViewById(R.id.SpinnerQuality);
        SpinnerLocation = (Spinner)findViewById(R.id.SpinnerLocation);
        ETprice = (EditText)findViewById(R.id.ETprice);

        saveButton = (Button)findViewById(R.id.saveButton);
        deleteButton = (Button)findViewById(R.id.deleteButton);
        //backToListButton = (Button)findViewById(R.id.backToListButton);
        priceButton = (Button)findViewById(R.id.priceButton);


        // Set TextView and EditTexts
        ViewBotanicalName.setText(record.botanicalName);
        ViewCommonName.setText(record.commonName);
        ETnumAvail.setText(Integer.toString(record.numAvail));
        /*SpinnerDetails.setText(record.details);*/
        ViewType1.setText(record.type1);
        ViewType2.setText(record.type2);
        ETprice.setText(Double.toString(record.price));
        ETprice.setFocusable(false);
        ETprice.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        ETprice.setClickable(false);
        ((EditText)findViewById(R.id.ETprice)).getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);

        // Size spinner
        ArrayAdapter<CharSequence> sizeAdapter = ArrayAdapter.createFromResource(this, R.array.size, android.R.layout.simple_spinner_item);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerSize.setAdapter(sizeAdapter);
        int spinnerSizePosition = sizeAdapter.getPosition(record.size);
        SpinnerSize.setSelection(spinnerSizePosition);

        // Notes spinner
        ArrayAdapter<CharSequence> notesAdapter = ArrayAdapter.createFromResource(this, R.array.notes, android.R.layout.simple_spinner_item);
        notesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerNotes.setAdapter(notesAdapter);
        int spinnerNotesPosition = notesAdapter.getPosition(record.notes);
        SpinnerNotes.setSelection(spinnerNotesPosition);

        // Quality Spinner
        ArrayAdapter<CharSequence> qualityAdapter = ArrayAdapter.createFromResource(this, R.array.quality, android.R.layout.simple_spinner_item);
        qualityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerQuality.setAdapter(qualityAdapter);
        int spinnerQualityPosition = qualityAdapter.getPosition(record.quality);
        SpinnerQuality.setSelection(spinnerQualityPosition);

        // Location Spinner
        ArrayAdapter<CharSequence> locationAdapter = ArrayAdapter.createFromResource(this, R.array.location, android.R.layout.simple_spinner_item);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerLocation.setAdapter(locationAdapter);
        int spinnerLocationPosition = locationAdapter.getPosition(record.location);
        SpinnerLocation.setSelection(spinnerLocationPosition);

        // Details Spinner

        Log.w(TAG, "record.details is " + record.details.split(";").length + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        ArrayAdapter<CharSequence> detailAdapter = ArrayAdapter.createFromResource(this, R.array.details, android.R.layout.simple_spinner_item);
        detailAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerDetails.setAdapter(detailAdapter);
        if (record.details.split(";").length >= 1) {
            int spinnerDetailsPosition = detailAdapter.getPosition(record.details.split(";")[0]);
            SpinnerDetails.setSelection(spinnerDetailsPosition);
        }

        ArrayAdapter<CharSequence> detailAdapter2 = ArrayAdapter.createFromResource(this, R.array.details, android.R.layout.simple_spinner_item);
        detailAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerDetails2.setAdapter(detailAdapter2);
        if (record.details.split(";").length >= 2) {
            int spinnerDetails2Position = detailAdapter2.getPosition(record.details.split(";")[1]);
            SpinnerDetails2.setSelection(spinnerDetails2Position);
        }

        ArrayAdapter<CharSequence> detailAdapter3 = ArrayAdapter.createFromResource(this, R.array.details, android.R.layout.simple_spinner_item);
        detailAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerDetails3.setAdapter(detailAdapter3);
        if (record.details.split(";").length == 3) {
            int spinnerDetails3Position = detailAdapter3.getPosition(record.details.split(";")[2]);
            SpinnerDetails3.setSelection(spinnerDetails3Position);
        }

        // Set up Spinner Listeners
        SpinnerSize.setOnItemSelectedListener(this);
        SpinnerNotes.setOnItemSelectedListener(this);
        SpinnerQuality.setOnItemSelectedListener(this);
        SpinnerLocation.setOnItemSelectedListener(this);
        SpinnerDetails.setOnItemSelectedListener(this);
        SpinnerDetails2.setOnItemSelectedListener(this);
        SpinnerDetails3.setOnItemSelectedListener(this);

        // Set up Button Listeners
        saveButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        //backToListButton.setOnClickListener(this);
        priceButton.setOnClickListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
        ((TextView)parent.getChildAt(0)).setTextColor(Color.BLACK);
        ((TextView)parent.getChildAt(0)).setTextSize(50);
        switch(parent.getId()){
            case R.id.SpinnerSize:
                record.size = parent.getItemAtPosition(pos).toString();
                break;
            case R.id.SpinnerNotes:
                record.notes = parent.getItemAtPosition(pos).toString();
                break;
            case R.id.SpinnerQuality:
                record.quality = parent.getItemAtPosition(pos).toString();
                break;
            case R.id.SpinnerLocation:
                record.location = parent.getItemAtPosition(pos).toString();
                break;
            case R.id.SpinnerDetails:
                detail1 = parent.getItemAtPosition(pos).toString();
                record.details = detail1 + ";" + detail2 + ";" + detail3;
                break;
            case R.id.SpinnerDetails2:
                detail2 = parent.getItemAtPosition(pos).toString();
                record.details = detail1 + ";" + detail2 + ";" + detail3;
                break;
            case R.id.SpinnerDetails3:
                detail3 = parent.getItemAtPosition(pos).toString();
                record.details = detail1 + ";" + detail2 + ";" + detail3;
                break;
        }
    }

    public void onNothingSelected(AdapterView<?> parent){
        // Another interface callback
    }


    private Boolean isInputValid(String size, int numAvail, String quality, String location, float price) {
        boolean isValid = false;
        String message;
        if (size.isEmpty()) {
            message = getString(R.string.validation_size);
        }
        else if (numAvail < 0) {
            message = getString(R.string.validation_numAvail);
        }
        else if (quality.isEmpty()) {
            message = getString(R.string.validation_quality);
        }
        else if (location.isEmpty()) {
            message = getString(R.string.validation_location);
        }
        /*else if (price <= 0) {
            message = getString(R.string.validation_price);
        }*/
        else {
            isValid = true;
            message = getString(R.string.validation_success);
        }
        Toast.makeText(RecordActivity.this, message, Toast.LENGTH_SHORT).show();
        return isValid;
    }

    public void onClick(View v){
        Intent intent;

        switch(v.getId()){
            case R.id.priceButton:
                AlertDialog.Builder alert = new AlertDialog.Builder(RecordActivity.this);
                alert.setTitle("Enter Password");
                final EditText input = new EditText(RecordActivity.this);
                alert.setView(input);
                alert.setNeutralButton("Enter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (input.getText().toString().equals("42513")) {
                            ETprice.setFocusable(true);
                            ETprice.setFocusableInTouchMode(true);
                            ETprice.setClickable(true);
                            ((EditText)findViewById(R.id.ETprice)).getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                        }
                        else {
                            Toast.makeText(RecordActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
                alert.show();
                break;
            case R.id.saveButton:
                String size = SpinnerSize.getSelectedItem().toString();
                int numAvail = Integer.parseInt(ETnumAvail.getText().toString());
                String details = SpinnerDetails.getSelectedItem().toString() + ";" + SpinnerDetails2.getSelectedItem().toString() + ";" + SpinnerDetails3.getSelectedItem().toString();
                String notes = SpinnerNotes.getSelectedItem().toString();
                String quality = SpinnerQuality.getSelectedItem().toString();
                String location = SpinnerLocation.getSelectedItem().toString();
                float price = Float.parseFloat(ETprice.getText().toString());
                if(isInputValid(size, numAvail, quality, location, price)) {


                    record.size = size;
                    record.numAvail = numAvail;
                    record.details = details;
                    record.notes = notes;
                    record.quality = quality;
                    record.location = location;
                    record.price = price;

                    if(!fromBarcode && dbWeekly.checkID(rowID)){
                        // Update record on weeklyDB
                        if(dbWeekly.updateRecord(record))
                            Log.w(TAG, "Record " + rowID + "has been updated.....");
                        else
                            Log.w(TAG, "Record " + rowID + "has not been updated....");
                    } else{
                        // Create record on weeklyDB
                        long newID = dbWeekly.createRecord(record);
                        Log.w(TAG, "Record " + newID + " has been created....");
                    }

                    //intent = new Intent(RecordActivity.this, AvailListActivity.class);
                    //startActivity(intent);
                    finish();
                }
                break;
            case R.id.deleteButton:
                new AlertDialog.Builder(RecordActivity.this)
                        .setTitle(getString(R.string.title_delete_entry))
                        .setMessage(getString(R.string.confirm_delete_entry))
                        .setPositiveButton(this.getString(R.string.positive), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.w(TAG, "delete Button pressed......");
                                if(dbWeekly.deleteRecord(rowID))
                                    Log.w(TAG, "Record " + rowID + "has been deleted....");
                                else
                                    Log.w(TAG, "Record " + rowID + "not successfully deleted....");

                                //intent = new Intent(RecordActivity.this, AvailListActivity.class);
                                //startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton(this.getString(R.string.negative), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
                break;

            /*case R.id.backToListButton:
                Log.w(TAG, "Back to List Button pressed......");
                //intent = new Intent(RecordActivity.this, AvailListActivity.class);
                //startActivity(intent);
                finish();

                break;*/
        }
    }

}