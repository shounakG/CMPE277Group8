package edu.sjsu.ireportgrp8;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by pnedunuri on 11/30/16.
 */
public class ResidentReport implements Serializable {

    private String annonymous;
    private String address;
    private String title;
    private String user_email;
    private String user_screen_name;
    private ArrayList<String> images;
    private String description;
    private String fulldate;
    private String location;
    private String severity_level;
    private String size;
    private String status;
    private String uri;

    private String reportId;
    private String userId;

    public ResidentReport() {
    }

    public ResidentReport(String title, String user_email, ArrayList<String> images, String user_screen_name, String description, String fulldate, String location, String severity_level, String size, String status) {
        this.title = title;
        this.user_email = user_email;
        this.images = images;

        this.user_screen_name = user_screen_name;
        this.description = description;
        this.fulldate = fulldate;
        this.location = location;
        this.severity_level = severity_level;
        this.size = size;
        this.status = status;
    }

    public ResidentReport(Object title, Object email, Object images, Object screenname, Object description, Object datetime, String location, Object severity, Object size, Object status, Object address, Object annonymous) {
        this.title = (title != null) ? title.toString() : null;
        this.user_email = (email != null) ? email.toString() : null;
        this.user_screen_name = (screenname != null) ? screenname.toString() : null;
        this.description = (description != null) ? description.toString() : null;
        this.fulldate = (fulldate != null) ? fulldate.toString() : null;
        this.images = (images != null) ? (ArrayList<String>)images : null;
        this.location = (location != null) ? location.toString() : null;
        this.severity_level = (severity != null) ? severity.toString() : null;
        this.size = (size != null) ? size.toString() : null;
        this.status = (status != null) ? status.toString() : null;
        this.address = (address != null) ? address.toString() : null;
        this.annonymous = (annonymous != null) ? annonymous.toString() : null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser_Email() {
        return user_email;
    }

    public void setUser_Email(String user_email) {
        this.user_email = user_email;
    }

    public ArrayList<String> getImage() {
        return images;
    }

    public void setImage(ArrayList<String> image) {
        this.images = images;
    }

    public String getUser_Screen_Name() {
        return user_screen_name;
    }

    public void setUser_Screen_Name(String user_screen_name) {
        this.user_screen_name = user_screen_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFullDate() {
        return fulldate;
    }

    public void setFullDate(String fulldate) {
        this.fulldate = fulldate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSeverity_Level() {
        return severity_level;
    }

    public void setSeverity_Level(String severity_level) {
        this.severity_level = severity_level;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getReportId() {
        return reportId;
    }

    public String getAnnonymous() {
        return annonymous;
    }

    public void setAnnonymous(String annonymous) {
        this.annonymous = annonymous;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri.toString();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String id) {
        userId = id;
    }
}
