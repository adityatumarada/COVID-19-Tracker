package com.example.corona.models;


import java.util.List;

public class LocationStats {
    private String state;
    private String country;
    private List<Integer> Confirmed;
    private List<Integer> recovered;
    private List<Integer> deaths;

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



    public List<Integer> getConfirmed() {
        return Confirmed;
    }

    public void setConfirmed(List<Integer> confirmed) {
        Confirmed = confirmed;
    }

    public List<Integer> getRecovered() {
        return recovered;
    }

    public void setRecovered(List<Integer> recovered) {
        this.recovered = recovered;
    }

    public List<Integer> getDeaths() {
        return deaths;
    }

    public void setDeaths(List<Integer> deaths) {
        this.deaths = deaths;
    }
}
