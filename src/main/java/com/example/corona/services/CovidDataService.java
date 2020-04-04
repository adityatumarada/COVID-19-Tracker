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
import java.util.Iterator;
import java.util.List;



@Service
public class CovidDataService {

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
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

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    public List<String> getDates() {
        return dates;
    }
    public List<Integer> getIndia() {
        return india;
    }
    public List<Integer> getGlobal() {
        return global;
    }

    private List<LocationStats> allStats = new ArrayList<>();
    private List<String> dates = new ArrayList<>();
    private List<Integer> india = new ArrayList() ;
    private List<Integer> global = new ArrayList() ;

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws IOException {
        List<LocationStats> newStats = new ArrayList<>();
        List<String> newdates = new ArrayList<>();

        BufferedReader br = new BufferedReader(new InputStreamReader(DATA_URL.openStream()));
        String header = br.readLine();
        List<String> temdate = Arrays.asList(header.split(","));
        for(int i=4;i<temdate.size();i++)
            newdates.add(temdate.get(i));

        dates = newdates;

        String s = null;
        List<Integer> newindia = new ArrayList() ;
        List<Integer> newglobal = new ArrayList() ;
        int flag=0;
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

                if(flag==0) {
                    for (int k = 4; k < size; k++) {
                        newglobal.add((Integer.parseInt(temp.get(k))));
                    }
                    flag++;
                }
                else
                {
                    int k=0;
                    while(!isInteger(temp.get(k)))
                    {
                        k++;
                    }
                    int a=k;
                    List<Integer> templist = new ArrayList<>();

                        for (; k < temp.size(); k++) {
                            templist.add(newglobal.get(k-a)+(Integer.parseInt(temp.get(k))));
                        }

                    newglobal=templist;
                }
            }
            global=newglobal;
            if (temp.get(1).toLowerCase().equals("india")) {

                for ( int j=4;j<temp.size();j++)
                {
                    if(!temp.get(j).equals(""))
                        newindia.add((Integer.parseInt(temp.get(j))));
                    else
                        newindia.add(0);
                }
            }
        }

        india = newindia;
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






