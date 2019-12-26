package com.dv.nhat.nsmart.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Buttonn implements Parcelable {
    private String id;
    private String name;
    private int state;
    private int pin;
    private int indexIcon;

    public Buttonn(String id, String name, int state, int pin, int indexIcon) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.pin = pin;
        this.indexIcon = indexIcon;
    }

    public Buttonn() {
    }

    protected Buttonn(Parcel in) {
        id = in.readString();
        name = in.readString();
        state = in.readInt();
        pin = in.readInt();
        indexIcon = in.readInt();
    }

    public static final Creator<Buttonn> CREATOR = new Creator<Buttonn>() {
        @Override
        public Buttonn createFromParcel(Parcel in) {
            return new Buttonn(in);
        }

        @Override
        public Buttonn[] newArray(int size) {
            return new Buttonn[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public int getIndexIcon() {
        return indexIcon;
    }

    public void setIndexIcon(int indexIcon) {
        this.indexIcon = indexIcon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeInt(state);
        parcel.writeInt(pin);
        parcel.writeInt(indexIcon);
    }
}
