package com.example.corona.services;

import com.example.corona.models.LocationStats;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


@Service
public class CovidDataService {

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    private static URL DATA_URL, RECOVERED_URL, DEATHS_URL;

    static {
        try {
            DATA_URL = new URL("https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv");
            RECOVERED_URL = new URL("https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv");
            DEATHS_URL = new URL("https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, LocationStats> allStats = new HashMap<>();
    private List<String> dates = new ArrayList<>();
    private List<String> keys = new ArrayList<>();

    public List<String> getKeys() {
        return keys;
    }
    public HashMap<String, LocationStats> getAllStats() {
        return allStats;
    }

    public List<String> getDates() {
        return dates;
    }

    @PostConstruct
    @Scheduled(cron = "* * * 1 * *")
    public void fetchVirusData() throws IOException {
        HashMap<String, LocationStats> newStats = new HashMap<>();
        List<String> newdates = new ArrayList<>();


//extracting dates
        BufferedReader br = new BufferedReader(new InputStreamReader(DATA_URL.openStream()));
        String header = br.readLine();
        List<String> temdate = Arrays.asList(header.split(","));
        for (int i = 4; i < temdate.size(); i++)
            newdates.add(temdate.get(i));
        dates = newdates;


//adding confirmed list
        String s = null;
        List<String> tempkeys= new ArrayList<>();
        while ((s = br.readLine()) != null) {
            List<String> temp = Arrays.asList(s.split(","));

            if (temp.size()-4 == (dates.size()) ) {

                String temphash = temp.get(2).concat(temp.get(3));
                if (temp.get(1).toLowerCase().equals("india"))
                    System.out.println(temphash);
                tempkeys.add(temphash);
                LocationStats locationStats = new LocationStats();
                locationStats.setState(temp.get(0));
                locationStats.setCountry(temp.get(1));
                int size = temp.size();
                List<Integer> confstats = new ArrayList<>();
                List<Integer> zero = new ArrayList<>();
                for (int i = 4; i < size; i++) {
                    confstats.add(Integer.parseInt(temp.get(i)));
                    zero.add(0);
                }
                locationStats.setConfirmed(confstats);
                locationStats.setRecovered(zero);
                locationStats.setDeaths(zero);
                newStats.put(temphash, locationStats);
            }
        }


        keys=tempkeys;
//adding recovered list
        br = new BufferedReader(new InputStreamReader(RECOVERED_URL.openStream()));
        header = br.readLine();
        while ((s = br.readLine()) != null) {
            List<String> temp = Arrays.asList(s.split(","));
            String temphash = temp.get(2).concat(temp.get(3));
            if (temp.size()-4 == (dates.size()) && newStats.containsKey(temphash)) {
                LocationStats locationStats = newStats.get(temphash);
                int size = temp.size();
                List<Integer> recstats = new ArrayList<>();
                for (int i = 4; i < size; i++)
                    recstats.add(Integer.parseInt(temp.get(i)));

                locationStats.setRecovered(recstats);
                newStats.replace(temphash, locationStats);
            }
        }

//adding death list
        br = new BufferedReader(new InputStreamReader(DEATHS_URL.openStream()));
        header = br.readLine();
        while ((s = br.readLine()) != null) {
            List<String> temp = Arrays.asList(s.split(","));
            String temphash = temp.get(2).concat(temp.get(3));
            if (temp.size()-4 == (dates.size())&&newStats.containsKey(temphash)) {

                LocationStats locationStats = newStats.get(temphash);
                int size = temp.size();
                List<Integer> deathstats = new ArrayList<>();
                for (int i = 4; i < size; i++)
                    deathstats.add(Integer.parseInt(temp.get(i)));

                locationStats.setDeaths(deathstats);
                newStats.replace(temphash, locationStats);
            }

        }
        allStats = newStats;

    }
}








