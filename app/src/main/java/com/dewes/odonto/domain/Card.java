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

    private List<Action> actions;

    private List<Attachment> attachments;

    @Override
    public String toString() {
        return "Card{" +
                "whatafield='" + whatafield + '\'' +
                ", actions=" + actions +
                ", attachments=" + attachments +
                '}';
    }

    public String getWhatafield() {
        return whatafield;
    }

    public void setWhatafield(String whatafield) {
        this.whatafield = whatafield;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }
}
