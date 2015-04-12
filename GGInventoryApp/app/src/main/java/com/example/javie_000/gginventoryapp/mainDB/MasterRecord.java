package com.example.javie_000.gginventoryapp.mainDB;

public class MasterRecord {

    public int id;
    public String tagName;
    public String botanicalName;
    public String commonName;
    public String description;
    public String usdaZone;
    public String type1;
    public String type2;
    public String droughtTolerant;

    public MasterRecord() {}

    @Override
    public String toString() {
         return "Master Record [id=" + id
                + ",tag name" + tagName
                + ",botanical name" + botanicalName
                + ",common name" + commonName
                + ",description" + description
                + ",usdaZone" + usdaZone
                + ",type1" + type1
                + ",type2" + type2
                + ",droughtTolerant" + droughtTolerant + "]";
    }
}
