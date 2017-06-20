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

    private User user;

    @Override
    public String toString() {
        return "Card{" +
                "whatafield='" + whatafield + '\'' +
                ", user=" + user +
                '}';
    }

    public String getWhatafield() {
        return whatafield;
    }

    public void setWhatafield(String whatafield) {
        this.whatafield = whatafield;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
