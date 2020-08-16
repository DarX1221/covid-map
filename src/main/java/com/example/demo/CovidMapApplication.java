package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CovidMapApplication {

    public static void main(String[] args) {
        SpringApplication.run(CovidMapApplication.class, args);
    }

}


/*
Token for map

pk.eyJ1IjoiZGFyazE0MjAwIiwiYSI6ImNrZHVka3hqYjI3Mmkycm1xNjRvdjlrNDAifQ.AuWw_Zp7txeDs2nPsu8pkg


    points.forEach(value => {
        console.log("------------------------------------------------------------------------------------");
        var lat = ${value[lat]}
        console.log(lat);
        var marker = L.marker([value.lat, value.lon]).addTo(mymap);
        marker.bindPopup("value.text")
            .openPopup();
    })



    DB

    Username: PyQLcyl7qk

Database name: PyQLcyl7qk

Password: PDhln9Kpub

Server: remotemysql.com

Port: 3306
 */