package aditya.covidtracker.controller;

import aditya.covidtracker.models.LocationStats;
import aditya.covidtracker.services.CovidDataService;
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
        List<LocationStats> temp = covidDataService.getAllStats();
        List<String> dates = covidDataService.getDates();

        for (int i = 0; i < temp.size(); i++) {
            total = total+ temp.get(i).getLatestCases();
        }
        int size = dates.size()-1;

        String date = "Last updated on ".concat(dates.get(size));
        model.addAttribute("date",date);
        model.addAttribute("total",total);
        model.addAttribute("locationStats",covidDataService.getAllStats());
        return "home";
    }
}
