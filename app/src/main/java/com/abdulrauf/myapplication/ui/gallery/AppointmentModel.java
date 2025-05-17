package com.abdulrauf.myapplication.ui.gallery;

public class AppointmentModel {
    private final String id;
    private final String date;
    private final String startTime;
    private final String endTime;

    public AppointmentModel(String id, String date, String startTime, String endTime) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getId() { return id; }
    public String getDate() { return date; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
}
