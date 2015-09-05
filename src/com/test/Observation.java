package com.test;

/**
 * Created by Adrian on 02/09/2015.
 */
public class Observation {

    private String data;
    private int timestamp;


    public Observation(String data, int timestamp) {
        this.data = data;
        this.timestamp = timestamp;
    }

    public Observation() {

    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
