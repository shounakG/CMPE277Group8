package edu.sjsu.ireportgrp8;

/**
 * Created by akshaymathur on 11/23/16.
 */

public class PublicUser {

    private String emailAddress;
    private String screenName;
    private String firstName;
    private String lastName;
    private String streetAddress;
    private String aptNumber;
    private String cityName;
    private String zipCode;

    public PublicUser(String emailAddress, String screenName, String firstName, String lastName, String streetAddress, String aptNumber, String cityName, String zipCode) {
        this.emailAddress = emailAddress;
        this.screenName = screenName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.streetAddress = streetAddress;
        this.aptNumber = aptNumber;
        this.cityName = cityName;
        this.zipCode = zipCode;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getAptNumber() {
        return aptNumber;
    }

    public void setAptNumber(String aptNumber) {
        this.aptNumber = aptNumber;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
