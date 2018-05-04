package com.example.andrew.mtgsearch;

/**
 * Created by Andrew on 5/2/2018.
 */

public class RulingObject {

    public final String date;
    public final String ruling;

    public RulingObject(String date, String ruling) {
        this.date = date;
        this.ruling = ruling;
    }

    @Override
    public String toString() {
        return date + " " + ruling;
    }
}
