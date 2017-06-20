package com.dewes.odonto.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Dewes on 13/06/2017.
 */

public class DateTime implements Serializable {

    DateFormat df = new SimpleDateFormat("dd/MM/yyyy ás hh:MM:ss");

    @Expose
    @SerializedName("date")
    private Date date;

    @Expose
    @SerializedName("timezone_type")
    private int timezoneType;

    @Expose
    @SerializedName("timezone")
    private String timezone;

    @Override
    public String toString() {
        return "DateTime{" +
                "df=" + df +
                ", date=" + date +
                ", timezoneType=" + timezoneType +
                ", timezone='" + timezone + '\'' +
                '}';
    }

    public String toHumanDate() {
        return df.format(this.date);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getTimezoneType() {
        return timezoneType;
    }

    public void setTimezoneType(int timezoneType) {
        this.timezoneType = timezoneType;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
