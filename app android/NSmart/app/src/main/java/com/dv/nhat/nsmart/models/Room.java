package com.dv.nhat.nsmart.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Room implements Parcelable {
    private String id;
    private String iduser;
    private String name;
    private String codeEsp8266;
    private int indexIcon;
    private int numberDevices;

    public Room(String id, String iduser, String name, String codeEsp8266, int indexIcon, int numberDevices) {
        this.id = id;
        this.iduser = iduser;
        this.name = name;
        this.codeEsp8266 = codeEsp8266;
        this.indexIcon = indexIcon;
        this.numberDevices = numberDevices;
    }

    public Room() {
    }

    protected Room(Parcel in) {
        id = in.readString();
        iduser = in.readString();
        name = in.readString();
        codeEsp8266 = in.readString();
        indexIcon = in.readInt();
        numberDevices = in.readInt();
    }

    public static final Creator<Room> CREATOR = new Creator<Room>() {
        @Override
        public Room createFromParcel(Parcel in) {
            return new Room(in);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCodeEsp8266() {
        return codeEsp8266;
    }

    public void setCodeEsp8266(String codeEsp8266) {
        this.codeEsp8266 = codeEsp8266;
    }

    public int getIndexIcon() {
        return indexIcon;
    }

    public void setIndexIcon(int indexIcon) {
        this.indexIcon = indexIcon;
    }

    public int getNumberDevices() {
        return numberDevices;
    }

    public void setNumberDevices(int numberDevices) {
        this.numberDevices = numberDevices;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(iduser);
        parcel.writeString(name);
        parcel.writeString(codeEsp8266);
        parcel.writeInt(indexIcon);
        parcel.writeInt(numberDevices);
    }
}
