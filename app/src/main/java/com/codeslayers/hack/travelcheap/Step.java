package com.codeslayers.hack.travelcheap;

/**
 * Created by lenovo on 01/10/2016.
 */

public class Step {
    private String source;
    private String destination;
    private String mode;
    private int distance;
    private float fare;

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


}
