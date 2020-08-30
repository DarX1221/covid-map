package com.example.demo;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@PropertySource("classpath:application.properties")
public class CovidDataService {

    private static String url;
    private static List<Double> latitudePointList;
    private static List<Double> longitudePointList;
    private static List<String> descriptionPointList;
    private static Date lastUpdate;
    private static String dateStr = turnDateToString(new Date(System.currentTimeMillis()));

    @Autowired
    public CovidDataService(@Value("${covidDataService.url}") String url) {
        this.url = url;
    }

    // getCovidData() should be static, otherwise will be called every time
    private static void getCovidData() throws IOException {
        Date checker = new Date(System.currentTimeMillis() - (1000 * 3600 * 12));

        if(lastUpdate == null || lastUpdate.before(checker)) {
            dateStr = turnDateToString(new Date(System.currentTimeMillis()));

            /*
            Parameters are split on 3 ArrayList
            TODO: implement model Point(latitude, longitude, description)
             implement displaying the model in map.html !
             */
            latitudePointList = new ArrayList<>();
            longitudePointList = new ArrayList<>();
            descriptionPointList = new ArrayList<>();

            RestTemplate restTemplate = new RestTemplate();
            String values = restTemplate.getForObject(url, String.class);

            StringReader stringReader = new StringReader(values);

            CSVParser parsedData = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(stringReader);

            for (CSVRecord covidData : parsedData) {

                // if data doesn't have number of cases for this day, change dateStr on one day back
                if(!covidData.isMapped(dateStr)) {
                    dateStr = turnDateToString(new Date(System.currentTimeMillis() - (1000 * 3600 * 24)));
                }

                // parameters for each points(country)
                double latitude = Double.parseDouble(covidData.get("Lat"));
                double longitude = Double.parseDouble(covidData.get("Long"));
                String numberOfCases = covidData.get(dateStr);

                // parameters to create description, state can be empty
                String country = covidData.get("Country/Region");
                String state = covidData.get("Province/State");
                String description;
                if (state.equals("")) {
                    description = (country + ": " + numberOfCases);
                } else {
                    description = (country + ", " + state + ": " + numberOfCases);
                }

                // add all parameters to create point on the map
                latitudePointList.add(latitude);
                longitudePointList.add(longitude);
                descriptionPointList.add(description);
            }
            lastUpdate = new Date(System.currentTimeMillis());
        }
    }

    public static List<Double> getLatitudePointList() {
        // latitude is the first field take by controller, if this list of latitude's is null, induce getCovidData() to download data
        if (latitudePointList == null) {
            try {
                getCovidData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return latitudePointList;
    }

    public static List<Double> getLongitudePointList() {
        return longitudePointList;
    }

    public static List<String> getDescriptionPointList() {
        return descriptionPointList;
    }

    public static String getDateStr() {
        return dateStr;
    }


    // change date (in Date format) to String in format like in header of csv file
    static String turnDateToString(Date date) {
        String pattern = "MM/dd/yy";
        DateFormat df = new SimpleDateFormat(pattern);
        String dateStr = df.format(date);
        if(dateStr.charAt(0) == '0') {dateStr = dateStr.substring(1);}
        return dateStr;
    }

}

