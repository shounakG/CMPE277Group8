package edu.sjsu.ireportgrp8;

/**
 * Created by akshaymathur on 11/25/16.
 */

public class UserSettingsData {

    private Boolean reportConf;
    private Boolean statusConf;
    private Boolean anonymous;

    public UserSettingsData() {
    }

    public UserSettingsData(Boolean reportConf, Boolean statusConf, Boolean anonymous) {
        this.reportConf = reportConf;
        this.statusConf = statusConf;
        this.anonymous = anonymous;
    }

    public Boolean getReportConf() {
        return reportConf;
    }

    public void setReportConf(Boolean reportConf) {
        this.reportConf = reportConf;
    }

    public Boolean getStatusConf() {
        return statusConf;
    }

    public void setStatusConf(Boolean statusConf) {
        this.statusConf = statusConf;
    }

    public Boolean getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
    }
}
