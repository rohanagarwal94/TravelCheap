package com.codeslayers.hack.travelcheap.Model;

import java.util.ArrayList;

/**
 * Created by lenovo on 01/10/2016.
 */

public class Route{
    private ArrayList<Step> steps;
    private String startAddress;
    private String endAddress;
    private int duration;
    private int distance;
    private float fare;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    private String mode;

    public float getFare() {
        return fare;
    }

    public void setFare(float fare) {
        this.fare = fare;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public void setSteps(ArrayList<Step> steps) {
        this.steps = steps;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Route(){
        steps=new ArrayList<>();
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public void addStep(Step step){
        steps.add(step);
    }

}
