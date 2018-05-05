package com.example.andrew.mtgsearch;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Andrew on 5/2/2018.
 */

public class RulingObject implements Parcelable {

    public final String date;
    public final String ruling;

    public RulingObject(String date, String ruling) {
        this.date = date;
        this.ruling = ruling;
    }

    private RulingObject(Parcel in) {
        this.date = in.readString();
        this.ruling = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(date);
        out.writeString(ruling);
    }

    public static final Parcelable.Creator<RulingObject> CREATOR = new Parcelable.Creator<RulingObject>() {
        public RulingObject createFromParcel(Parcel in) {
            return new RulingObject(in);
        }

        public RulingObject[] newArray(int size) {
            return new RulingObject[size];
        }
    };

    @Override
    public String toString() {
        return date + " " + ruling;
    }
}
