package com.example.demo;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
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
public class CovidDataService {

    private static final String url = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private static List<Double> pointListLat;
    private static List<Double> pointListLon;
    private static List<String> pointListDsc;
    private static Date lastUpdate;
    private static String dateStr = turnDateToString(new Date(System.currentTimeMillis()));

    public static void getCovidData() throws IOException {
        Date chcecker = new Date(System.currentTimeMillis() - (1000 * 3600 * 12));

        if (lastUpdate == null || lastUpdate.before(chcecker)) {
            dateStr = turnDateToString(new Date(System.currentTimeMillis()));

            pointListLat = new ArrayList<>();
            pointListLon = new ArrayList<>();
            pointListDsc = new ArrayList<>();

            RestTemplate restTemplate = new RestTemplate();
            String values = restTemplate.getForObject(url, String.class);

            StringReader stringReader = new StringReader(values);

            CSVParser parse = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(stringReader);

            for (CSVRecord strings : parse) {


                if(!strings.isMapped(dateStr)) {
                    dateStr = turnDateToString(new Date(System.currentTimeMillis() - (1000 * 3600 * 24)));
                }

                double lat = Double.parseDouble(strings.get("Lat"));
                double lon = Double.parseDouble(strings.get("Long"));
                String casses = strings.get(dateStr);

                String country = strings.get("Country/Region");
                String state = strings.get("Province/State");
                String description;
                if (state.equals("")) {
                    description = (country + ": " + casses);
                } else {
                    description = (country + ", " + state + ": " + casses);
                }

                pointListLat.add(lat);
                pointListLon.add(lon);
                pointListDsc.add(description);
            }
            lastUpdate = new Date(System.currentTimeMillis());
        }
    }

    public static List<Double> getPointListLat() {
        // latitude is the first field take by controller, if this list of latitude's is null, induce getCoviData() to download data
        if (pointListLat == null) {
            try {
                getCovidData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return pointListLat;
    }

    public static List<Double> getPointListLon() {
        return pointListLon;
    }

    public static List<String> getPointListDsc() {
        return pointListDsc;
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

