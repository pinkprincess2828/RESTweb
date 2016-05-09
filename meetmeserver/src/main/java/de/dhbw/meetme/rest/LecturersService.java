package de.dhbw.meetme.rest;

import de.dhbw.meetme.database.Transaction;
import de.dhbw.meetme.database.dao.AppointmentDao;
import de.dhbw.meetme.database.dao.LecturerDao;
import de.dhbw.meetme.domain.Appointment;
import de.dhbw.meetme.domain.Lecturers;
import groovy.lang.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.*;

/**
 *this class handles all the interaction with the lecturer
 */
@Path("api/lecturer")
@Produces({"application/json"}) // mime type
@Singleton
public class LecturersService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Inject
    LecturerDao lecturerDao;
    @Inject
    AppointmentDao appointmentDao;

    @Inject
    Transaction transaction;

    @GET
    @Path("/list")
    //returns list of all lecturers saved in the database
    public Collection<Lecturers> listLecturers() {
        log.debug("list all lecturers");
        return lecturerDao.list();
    }



    @GET
    @Path("/{lecturerName}")
    // returns the DB entry of one single lecturer
    public Lecturers findLecturer(@PathParam("lecturerName") String lecturerName){
        log.debug("list lecturer"+ lecturerName);

        return lecturerDao.findLecturer(lecturerName);
    }

    @POST
    @Path("/save")
    //to add new lecturers to the DB
        public String save(@FormParam("lecturerFirstname") String lecturerFirstname, @FormParam("lecturerLastname") String lecturerLastname, @FormParam("lecturerTopic") String lecturerTopic,@FormParam("lecturerMail")String lecturerMail, @FormParam("lecturerPw")String lecturerPw) {
        transaction.begin();
        Lecturers lecturer = new Lecturers(lecturerFirstname, lecturerLastname, lecturerTopic,lecturerMail,lecturerPw, true);

        lecturerDao.persist(lecturer);
        log.debug("Save lecturer " + lecturer);
        transaction.commit();
        return "save lecturer";
    }

    @POST
    @Path("/appointment")
    //to schedule an appointment in advance
        public String appointment(@FormParam("advisor") String lecturerName, @FormParam("name") String studentFName,@FormParam("surname") String studentLName, @FormParam("email") String studentMail, @FormParam("topic") String topic, @FormParam("date") String date, @FormParam("time") String proposedTime,@FormParam("course") String course){
        transaction.begin();
        if (course.equals("") || course == null){
            course = "N/A";
        }
        Appointment appointment = new Appointment(lecturerName, date, proposedTime, studentFName,studentLName, studentMail,course,topic);
        appointmentDao.persist(appointment);
        log.debug("Save appoinment " + appointment);
        transaction.commit();
        return "save appointment";
        }

    @GET
    @Path("/appointment/{lecturerName}")
    //provide all requests for scheduled appointments
    public Collection<Appointment> listLecturers(@PathParam("lecturerName") String lecturerName) {
        log.debug("list all appoiintments of "+lecturerName);
        return appointmentDao.getLecturerAppointment(lecturerName);
    }

    @GET
    @Path("/availability/{lecturerName}")
    //to fetch the availabailty of the lecturer
        public boolean availability(@PathParam("lecturerName") String lecturerName){
        Lecturers lecturer = lecturerDao.findLecturer(lecturerName);
        return lecturer.isLecturerAvailability();
    }
    @POST
    @Path("/availability/{setAvailability}/{lecturerName}")
    //set the availability ...boolean true for availabale or false for unavailable
    public void setAvailability(@PathParam("setAvailability") boolean availability,@PathParam("lecturerName") String lecturerName){
        transaction.begin();
        Lecturers lecturer = lecturerDao.findLecturer(lecturerName);
        lecturer.setLecturerAvailability(availability);
        transaction.commit();
        return;
    }
    @GET
    @Path("/login")
    //verify the lecturer by checking mail and password
    public boolean login(@FormParam("email") String mail, @FormParam("password") String pw){
        Lecturers lecturer = lecturerDao.findLecturerMail(mail);
        if (lecturer.getLecturerPw()==pw){
            return true;
        }
        else return false;

    }
}
