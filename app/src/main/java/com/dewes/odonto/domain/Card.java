package com.dewes.odonto.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Dewes on 13/06/2017.
 */

public class Card extends AbstractEntity implements Serializable {

    @Expose
    @SerializedName("whatafield")
    private String whatafield;

    @Expose
    @SerializedName("timeAgo")
    private String timeAgo;

    @Override
    public String toString() {
        return "Card{" +
                "whatafield='" + whatafield + '\'' +
                ", timeAgo='" + timeAgo + '\'' +
                ", abstractEntity='" + super.toString() + '\'' +
                '}';
    }

    public String getWhatafield() {
        return whatafield;
    }

    public void setWhatafield(String whatafield) {
        this.whatafield = whatafield;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }
}
