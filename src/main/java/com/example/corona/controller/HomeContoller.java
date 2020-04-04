package com.example.corona.controller;

import com.example.corona.models.LocationStats;
import com.example.corona.models.Render;
import com.example.corona.services.CovidDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
        HashMap<String,LocationStats> temp= new HashMap<>();
        temp = covidDataService.getAllStats();
        List<String> dates= new ArrayList<>();
        dates = covidDataService.getDates();
        List<String> keys = new ArrayList<>();
        keys = covidDataService.getKeys();

        //dashboard related stuff
        for (int i = 0; i < temp.size(); i++) {
            int size1 = temp.get(keys.get(i)).getConfirmed().size();
            int size2 = temp.get(keys.get(i)).getRecovered().size();
            int size3 = temp.get(keys.get(i)).getDeaths().size();
            total = total+ Integer.parseInt(temp.get(keys.get(i)).getConfirmed().get(size1-1).toString());
            rec=rec+Integer.parseInt(temp.get(keys.get(i)).getRecovered().get(size2-1).toString());
            deaths=deaths+Integer.parseInt(temp.get(keys.get(i)).getDeaths().get(size3-1).toString());
        }

        //dates
        int size = dates.size()-1;
        List<String> last = Arrays.asList(dates.get(size).split("/"));
        String date = "Last updated on ".concat(last.get(1)).concat("-").concat(last.get(0).concat("-")).concat(last.get(2));
        List<String> t = new ArrayList<>();
        for(int i=0;i<dates.size();i++)
        {
          t.add(date_conv(dates.get(i)));
        }


        List<Integer> global = new ArrayList<>();
        List<Integer> india = new ArrayList<>();
        int bool=0;

        //global
        for(int i=0;i<keys.size();i++)
        {
            List<Integer> newtemp= temp.get(keys.get(i)).getConfirmed();
            if(bool==0)
            {
                global=newtemp;
                bool++;
            }
            else
            {
                for (int j=0;j<newtemp.size();j++)
                {
                    global.set(j,global.get(j)+newtemp.get(j));
                }
            }
        }

        //india
        india = temp.get("21.078.0").getConfirmed();

        //location stats for rendering
        List<Render> renderStats = new ArrayList<>();
        for (int i=1;i<keys.size();i++)
        {
            Render temprender = new Render();
            temprender.setCountry(temp.get(keys.get(i)).getCountry());
            temprender.setState(temp.get(keys.get(i)).getState());
            List<Integer> conf = temp.get(keys.get(i)).getConfirmed();
            List<Integer> reco = temp.get(keys.get(i)).getRecovered();
            List<Integer> dea = temp.get(keys.get(i)).getDeaths();
            temprender.setLatestCases(conf.get(conf.size()-1));
            temprender.setRecoverd(reco.get(reco.size()-1));
            temprender.setDeaths(dea.get(dea.size()-1));
            temprender.setChangeCases(conf.get(conf.size()-1)-conf.get(conf.size()-2));
            renderStats.add(temprender);
        }

        model.addAttribute("date",date);
        model.addAttribute("confirmed",total);
        model.addAttribute("active",total-rec-deaths);
        model.addAttribute("recovered",rec);
        model.addAttribute("deaths",deaths);
        model.addAttribute("locationStats",renderStats);
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
