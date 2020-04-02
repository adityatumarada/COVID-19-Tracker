package aditya.covidtracker.services;


import aditya.covidtracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CovidDataService {

    private static String DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private static String RECOVERED_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv";
    private static String DEATHS_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv";

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
    public void fetchVirusData() throws IOException, InterruptedException {

        List<LocationStats> newStats = new ArrayList<>();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest confRequest = HttpRequest.newBuilder().uri(URI.create(DATA_URL)).build();
        HttpResponse<String> confResponse = client.send(confRequest, HttpResponse.BodyHandlers.ofString());
        HttpRequest recRequest = HttpRequest.newBuilder().uri(URI.create(RECOVERED_URL)).build();
        HttpResponse<String> recResponse = client.send(recRequest, HttpResponse.BodyHandlers.ofString());
        HttpRequest deathRequest = HttpRequest.newBuilder().uri(URI.create(DEATHS_URL)).build();
        HttpResponse<String> deathResponse = client.send(deathRequest, HttpResponse.BodyHandlers.ofString());

        StringReader confReader = new StringReader(confResponse.body());
        Iterable<CSVRecord> confRecords = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(confReader);
        StringReader recReader = new StringReader(recResponse.body());
        Iterable<CSVRecord> recRecords = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(recReader);
        StringReader deathReader = new StringReader(deathResponse.body());
        Iterable<CSVRecord> deathRecords = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(deathReader);

        BufferedReader br = new BufferedReader(new StringReader(confResponse.body()));
        String header = br.readLine();
        dates = Arrays.asList(header.split(","));

        for (CSVRecord record : confRecords) {
            if(!record.get("Country/Region").toLowerCase().equals("canada")) {
                LocationStats locationStats = new LocationStats();
                locationStats.setState(record.get("Province/State"));
                locationStats.setCountry(record.get("Country/Region"));
                int size = record.size();
                locationStats.setLatestCases((Integer.parseInt(record.get(size - 1))));
                locationStats.setChangeCases((Integer.parseInt(record.get(size - 1))) - (Integer.parseInt(record.get(size - 2))));
                newStats.add(locationStats);
            }
        }

        int i=0;
        for (CSVRecord record : recRecords) {
            if(!record.get("Country/Region").toLowerCase().equals("canada")) {

                int size = record.size();
                newStats.get(i).setRecoverd((Integer.parseInt(record.get(size - 1))));
                i++;
            }
        }

        i=0;
        for (CSVRecord record : deathRecords) {
            if(!record.get("Country/Region").toLowerCase().equals("canada")) {

                int size = record.size();
                newStats.get(i).setDeaths((Integer.parseInt(record.get(size - 1))));
                i++;
            }
        }



        allStats = newStats;
    }


}
