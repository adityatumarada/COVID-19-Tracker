package com.example.corona.services;

import com.example.corona.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.HttpRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



@Service
public class CovidDataService {

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

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    public List<String> getDates() {
        return dates;
    }

    private List<LocationStats> allStats = new ArrayList<>();
    private List<String> dates = new ArrayList<>();


    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws IOException {
        List<LocationStats> newStats = new ArrayList<>();

        BufferedReader br = new BufferedReader(new InputStreamReader(DATA_URL.openStream()));
        String header = br.readLine();
        dates = Arrays.asList(header.split(","));

        String s = null;
        while ((s = br.readLine()) != null) {
            List<String> temp = Arrays.asList(s.split(","));
            if (!temp.get(1).toLowerCase().equals("canada")) {
                LocationStats locationStats = new LocationStats();
                locationStats.setState(temp.get(0));
                locationStats.setCountry(temp.get(1));
                int size = temp.size();
                locationStats.setLatestCases((Integer.parseInt(temp.get(size - 1))));
                locationStats.setChangeCases((Integer.parseInt(temp.get(size - 1))) - (Integer.parseInt(temp.get(size - 2))));
                newStats.add(locationStats);
            }
        }

        br = new BufferedReader(new InputStreamReader(RECOVERED_URL.openStream()));
        header = br.readLine();
        int i = 0;
        while ((s = br.readLine()) != null) {
            List<String> temp = Arrays.asList(s.split(","));
            if (!temp.get(1).toLowerCase().equals("canada")) {
                int size = temp.size();
                newStats.get(i).setRecoverd((Integer.parseInt(temp.get(size - 1))));
                i++;
            }
        }


        i = 0;
        br = new BufferedReader(new InputStreamReader(DEATHS_URL.openStream()));
        header = br.readLine();
        while ((s = br.readLine()) != null) {
            List<String> temp = Arrays.asList(s.split(","));
            if (!temp.get(1).toLowerCase().equals("canada")) {
                int size = temp.size();
                newStats.get(i).setDeaths((Integer.parseInt(temp.get(size - 1))));
                i++;
            }
        }
        allStats = newStats;

 }

}






