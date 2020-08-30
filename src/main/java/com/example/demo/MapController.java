package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MapController {
    private final CovidDataService covidDataService;


    @Autowired
    public MapController(CovidDataService covidDataService) {
        this.covidDataService = covidDataService;
    }

    @GetMapping
    public String map(Model model) {

        // pointListLat should be first!
        List<Double> pointListLat = covidDataService.getLatitudePointList();
        model.addAttribute("pointListLat", pointListLat);

        List<Double> pointListLon = covidDataService.getLongitudePointList();
        model.addAttribute("pointListLon", pointListLon);

        List<String> pointListDsc = covidDataService.getDescriptionPointList();
        model.addAttribute("pointListDsc", pointListDsc);

        String lastUpdate = covidDataService.getDateStr();
        model.addAttribute("lastUpdate", lastUpdate);

        return "map";

    }
}

