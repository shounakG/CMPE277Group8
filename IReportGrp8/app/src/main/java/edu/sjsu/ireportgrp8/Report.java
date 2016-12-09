package edu.sjsu.ireportgrp8;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dmodh on 11/29/16.
 */

public class Report implements Serializable{
    private String title;
    private String reportId;
    private String latitude;
    private String longitude;
    private String address;
    private String description;
    private String size;
    private String severity;
    private String datetime;
    private String email;
    private String screenname;
    private String status;
    private Boolean annonymous;
    private List<String> images;

    public Report() {
    }

    public Report(String title, String reportId, String latitude, String longitude, String address, String description, String size, String severity, String datetime, String email, String screenname, String status, Boolean annonymous, List<String> images) {
        this.title = title;
        this.reportId = reportId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.description = description;
        this.size = size;
        this.severity = severity;
        this.datetime = datetime;
        this.email = email;
        this.screenname = screenname;
        this.status = status;
        this.annonymous = annonymous;
        this.images = images;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getScreenname() {
        return screenname;
    }

    public void setScreenname(String screenname) {
        this.screenname = screenname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public Boolean getAnnonymous() {
        return annonymous;
    }

    public void setAnnonymous(Boolean annonymous) {
        this.annonymous = annonymous;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}