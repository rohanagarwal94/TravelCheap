package com.codeslayers.hack.travelcheap;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lenovo on 01/10/2016.
 */

public class Step implements Parcelable{
    private String source;
    private String destination;
    private String mode;
    private int distance;
    private float fare;

    protected Step(Parcel in) {
        source = in.readString();
        destination = in.readString();
        mode = in.readString();
        distance = in.readInt();
        fare = in.readFloat();
        duration = in.readInt();
    }

    public static final Creator<Step> CREATOR = new Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel in) {
            return new Step(in);
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
        }
    };

    public float getFare() {
        return fare;
    }

    public void setFare(float fare) {
        this.fare = fare;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Step(String source, String destination, String mode, int distance, int duration, float fare) {
        this.fare=fare;
        this.source = source;
        this.destination = destination;
        this.mode = mode;
        this.distance = distance;
        this.duration = duration;
    }

    private int duration;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(source);
        dest.writeString(destination);
        dest.writeString(mode);
        dest.writeInt(distance);
        dest.writeFloat(fare);
        dest.writeInt(duration);
    }
}
