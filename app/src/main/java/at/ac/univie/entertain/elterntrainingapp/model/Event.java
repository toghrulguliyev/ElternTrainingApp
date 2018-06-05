package at.ac.univie.entertain.elterntrainingapp.model;

import android.annotation.SuppressLint;
import android.graphics.Color;

import com.alamkanak.weekview.WeekViewEvent;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class Event {

    @Expose
    @SerializedName("name")
    private String mName;
    @Expose @SerializedName("dayOfMonth")
    private int mDayOfMonth;
    @Expose @SerializedName("startTime")
    private String mStartTime;
    @Expose @SerializedName("endTime")
    private String mEndTime;
    @Expose @SerializedName("color")
    private String mColor;

    //Extra variables
    private String autor;
    private String familyId;
    private long id;
    private String message;
    private String eventType;
    private String notifyUser;

    public Event(String message, String mName, String mStartTime, String mEndTime, String mColor, String autor) {
        this.id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        this.mName = mName;
        //this.mDayOfMonth = mDayOfMonth;
        this.mStartTime = mStartTime;
        this.mEndTime = mEndTime;
        this.mColor = mColor;
        this.autor = autor;
        this.message = message;
    }

    public Event() {}

    public String getNotifyUser() {
        return notifyUser;
    }

    public void setNotifyUser(String notifyUser) {
        this.notifyUser = notifyUser;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void generateId() {
        this.id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getDayOfMonth() {
        return mDayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.mDayOfMonth = dayOfMonth;
    }

    public String getStartTime() {
        return mStartTime;
    }

    public void setStartTime(String startTime) {
        this.mStartTime = startTime;
    }

    public String getEndTime() {
        return mEndTime;
    }

    public void setEndTime(String endTime) {
        this.mEndTime = endTime;
    }

    public String getColor() {
        return mColor;
    }

    public void setColor(String color) {
        this.mColor = color;
    }



    @SuppressLint("SimpleDateFormat")
    public WeekViewEvent toWeekViewEvent(){

        // Parse time.
        //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.GERMANY);
        SimpleDateFormat firstSdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.GERMANY);
        //Date start = new Date();
        //Date end = new Date();
        Calendar mStart = Calendar.getInstance();
        Calendar mEnd = Calendar.getInstance();
        try {
            //start = sdf.parse(getStartTime());
            mStart.setTime(sdf.parse(getStartTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            //end = sdf.parse(getEndTime());
            mEnd.setTime(sdf.parse(getEndTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Initialize start and end time.
//        Calendar now = Calendar.getInstance();
//        Calendar startTime = (Calendar) now.clone();
//        startTime.setTimeInMillis(start.getTime());
//        startTime.set(Calendar.YEAR, now.get(Calendar.YEAR));
//        startTime.set(Calendar.MONTH, now.get(Calendar.MONTH));
//        startTime.set(Calendar.DAY_OF_MONTH, getDayOfMonth());
//        Calendar endTime = (Calendar) startTime.clone();
//        endTime.setTimeInMillis(end.getTime());
//        endTime.set(Calendar.YEAR, startTime.get(Calendar.YEAR));
//        endTime.set(Calendar.MONTH, startTime.get(Calendar.MONTH));
//        endTime.set(Calendar.DAY_OF_MONTH, startTime.get(Calendar.DAY_OF_MONTH));




        // Create an week view event.
        WeekViewEvent weekViewEvent = new WeekViewEvent();
        weekViewEvent.setName(getName());
        //weekViewEvent.setStartTime(startTime);
        //weekViewEvent.setEndTime(endTime);
        weekViewEvent.setStartTime(mStart);
        weekViewEvent.setEndTime(mEnd);
        weekViewEvent.setColor(Color.parseColor(getColor()));
        weekViewEvent.setId(getId());

        return weekViewEvent;
    }

}
