package com.example.andrew.mtgsearch;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Andrew on 5/1/2018.
 */

public class CardObject implements Parcelable {


    public final String name;
    public final String manaCost;
    public final String power;
    public final String toughness;
    public final String text;
    public final String type;
    public final String imageURL;
    public final ArrayList<RulingObject> rulings;

        public CardObject(String name, String manaCost, String power, String toughness, String text, String type, String imageURL, ArrayList<RulingObject> rulings) {
            this.name = name;
            this.manaCost = manaCost;
            this.power = power;
            this.toughness = toughness;
            this.text = text;
            this.type = type;
            this.imageURL = imageURL;
            this.rulings = rulings;

        }

    private CardObject(Parcel in) {
        this.name = in.readString();
        this.manaCost = in.readString();
        this.power = in.readString();
        this.toughness = in.readString();
        this.text = in.readString();
        this.type = in.readString();
        this.imageURL = in.readString();
        rulings = new ArrayList<RulingObject>();
        in.readList(this.rulings, null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(manaCost);
        out.writeString(power);
        out.writeString(toughness);
        out.writeString(text);
        out.writeString(type);
        out.writeString(imageURL);
        out.writeList(rulings);
    }

    public static final Parcelable.Creator<CardObject> CREATOR = new Parcelable.Creator<CardObject>() {
        public CardObject createFromParcel(Parcel in) {
            return new CardObject(in);
        }

        public CardObject[] newArray(int size) {
            return new CardObject[size];
        }
    };

        @Override
        public String toString() {
            return name;
        }
    }
