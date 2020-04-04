package com.example.corona.controller;

import com.example.corona.models.LocationStats;
import com.example.corona.services.CovidDataService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class HomeContoller {

    @Autowired
    CovidDataService covidDataService;



    @GetMapping("/")
    public String home(Model model)
    {
        int total=0;
        int rec=0;
        int deaths=0;
        List<LocationStats> temp= new ArrayList<>();
        temp = covidDataService.getAllStats();
        List<String> dates= new ArrayList<>();
        dates = covidDataService.getDates();
        List<Integer> india = new ArrayList<>();
        india = covidDataService.getIndia();
        List<Integer> global = new ArrayList<>();
        global = covidDataService.getGlobal();


        for (int i = 0; i < temp.size(); i++) {
            total = total+ temp.get(i).getLatestCases();
            rec=rec+temp.get(i).getRecoverd();
            deaths=deaths+temp.get(i).getDeaths();
        }
        int size = dates.size()-1;
        List<String> last = Arrays.asList(dates.get(size).split("/"));
        String date = "Last updated on ".concat(last.get(1)).concat("-").concat(last.get(0).concat("-")).concat(last.get(2));

        List<String> t = new ArrayList<>();
        for(int i=0;i<dates.size();i++)
        {
          t.add(date_conv(dates.get(i)));
        }
        model.addAttribute("date",date);
        model.addAttribute("confirmed",total);
        model.addAttribute("active",total-rec-deaths);
        model.addAttribute("recovered",rec);
        model.addAttribute("deaths",deaths);
        model.addAttribute("locationStats",covidDataService.getAllStats());
        model.addAttribute("india",india);
        model.addAttribute("global",global);
        model.addAttribute("dates",t);

        System.out.println(india.size());
        System.out.println(t.size());

        return "home";

    }



    public String date_conv(String string)
    {
        List<String> last = Arrays.asList(string.split("/"));
        return last.get(1).concat("-").concat(last.get(0).concat("-")).concat(last.get(2));
    }
}
