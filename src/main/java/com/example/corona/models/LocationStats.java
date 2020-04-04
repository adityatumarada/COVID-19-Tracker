package com.example.corona.models;


public class LocationStats {
    private String state;
    private String country;
    private int latestCases;
    private int changeCases;
    private int recoverd;
    private int deaths;

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getRecoverd() {
        return recoverd;
    }

    public void setRecoverd(int recoverd) {
        this.recoverd = recoverd;
    }

    public int getChangeCases() {
        return changeCases;
    }

    public void setChangeCases(int changeCases) {
        this.changeCases = changeCases;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getLatestCases() {
        return latestCases;
    }

    public void setLatestCases(int latestCases) {
        this.latestCases = latestCases;
    }


}
