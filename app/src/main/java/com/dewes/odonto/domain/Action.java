package com.dewes.odonto.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Dewes on 13/06/2017.
 */

public class Action extends AbstractEntity {

    @Expose
    @SerializedName("actionType")
    private String actionType;

    @Expose
    @SerializedName("whatafield")
    private String whatafield;

    @Expose
    @SerializedName("timeAgo")
    private String timeAgo;

    @Expose
    @SerializedName("thumbUrl")
    private String thumbUrl;

    @Override
    public String toString() {
        return "Action{" +
                "actionType='" + actionType + '\'' +
                ", whatafield='" + whatafield + '\'' +
                ", timeAgo='" + timeAgo + '\'' +
                ", thumbUrl='" + thumbUrl + '\'' +
                ", abstractEntity='" + super.toString() + '\'' +
                '}';
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
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

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }
}
