package de.dhbw.meetme.rest;

import de.dhbw.meetme.database.dao.UserDao;
import de.dhbw.meetme.domain.User;
import groovy.lang.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.Collection;

import static java.lang.Math.*;

/**
 * Created by mordelt on 24.09.2015.
 * This class handles all GPS-related interactions.
 */
@Path("api/gps")
@Produces({"application/json"}) // mime type
@Singleton
public class GPSService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Inject
    UserDao userDao;

    @PUT
    @Path("/{username}/{laengenGrad}/{breitenGrad}")
    // schickt die aktuelle GPS Position und speichert sie in der DB
    public void updateGps (@PathParam("username") String username,@PathParam("laengenGrad") double laengenGrad ,@PathParam("breitenGrad") double breitenGrad)
    {
        Collection<User> meineUsers = userDao.findByName(username);
        if (meineUsers.size() > 0)
        {
            log.debug(username + " hat seine GPS Daten aktualisiert");
            //userDao.updateGPS(username, breitenGrad, laengenGrad);
            //array liste von allen usern GPS daten
           /* Array GPSdata = userDao.gpsDara

            for (int i = 0; i < GPSdata.length; i++)
            {
             if(distanceInMeter(laengenGrad,breitenGrad,GPSdata[i].leangenGrad, GPSdata[i].breitenGrad) <= 10) {

             }
            }
            return;
            */
        }
        log.debug("Jemand hat versucht seine GPS Daten aktualisiert aber userName war nicht in der DB");
        return;
    }

    @GET
    @Path("/{userName}")
    // Anfrage der eigenen GPS Koordianten...später werden die Koordinaten von allen Mitspielern geschickt
    /*public String getGps (@PathParam("userName")String userName)
    {
        if (userDao.existCheckName(userName)== true)
        {
            log.debug(userName + " hat seine GPS Daten angefragt");
            return userDao.getGPS(userName);
        }
        log.debug("Jemand hat versucht seine GPS Daten anzufragen aber userName war nicht in der DB");
        return "Leider bist du nicht angemeldet";
    }*/


    public static double distanceInMeter(double lat1, double lon1, double lat2, double lon2) {
        int earthRadius = 6371000; //meters
        double lat = Math.toRadians(lat2 - lat1);
        double lon = Math.toRadians(lon2- lon1);

        double a = Math.sin(lat / 2) * Math.sin(lat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lon / 2) * Math.sin(lon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = earthRadius * c;
        return Math.abs(d); // in meter

    }

    public static String checkDistance(double lat1, double lon1, double lat2, double lon2){

        double dist = distanceInMeter(lat1,lon1,lat2,lon2);
        if (dist <= 100){
            return "test";
        }
        log.debug("the distance is > 10");
        return null;

    }



}