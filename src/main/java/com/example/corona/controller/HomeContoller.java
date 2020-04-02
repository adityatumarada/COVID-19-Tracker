package com.example.corona.controller;

import com.example.corona.models.LocationStats;
import com.example.corona.services.CovidDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
        List<LocationStats> temp = covidDataService.getAllStats();
        List<String> dates = covidDataService.getDates();

        for (int i = 0; i < temp.size(); i++) {
            total = total+ temp.get(i).getLatestCases();
            rec=rec+temp.get(i).getRecoverd();
            deaths=deaths+temp.get(i).getDeaths();
        }
        int size = dates.size()-1;

        String date = "Last updated on ".concat(dates.get(size));
        model.addAttribute("date",date);
        model.addAttribute("total",total);
        model.addAttribute("recovered",rec);
        model.addAttribute("deaths",deaths);
        return "home";
    }

    @GetMapping("/global")
    public String global(Model model)
    {
        model.addAttribute("locationStats",covidDataService.getAllStats());
        return "global";
    }
}
