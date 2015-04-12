package com.example.javie_000.gginventoryapp.inventoryDB;

import android.os.Parcel;
import android.os.Parcelable;

public class WeeklyRecord {
    public int id;
    public String tagName;
    public String botanicalName;
    public String commonName;
    public int qty;
    public String size;
    public int numAvail;
    public String details;
    public String notes;
    public String type1;
    public String type2;
    public String quality;
    public String location;
    public double price;

    public WeeklyRecord(){}

    @Override
    public String toString() {
        return "Weekly Record [ id=" + id
                + ", tagName=" + tagName
                + ", botanicalName=" + botanicalName
                + ", common name=" + commonName
                + ", quantity=" + qty
                + ", size=" + size
                + ", numAvail=" + numAvail
                + ", details=" + details
                + ", notes=" + notes
                + ", type1=" + type1
                + ", type2=" + type2
                + ", quality=" + quality
                + ", location=" + location
                + ", price=" + price
                + "]";
    }
}
