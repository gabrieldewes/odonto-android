package com.dewes.odonto.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Dewes on 13/06/2017.
 */

public class AbstractEntity implements Serializable {

    private static DateFormat DF;

    private static DateFormat getDateFormat() {
        if (DF == null)
            DF = new SimpleDateFormat("dd MMM yyyy 'ás' HH:MM", new Locale("pt", "BR"));
        return DF;
    }

    @Expose
    @SerializedName("id")
    private Long id;

    @Expose
    @SerializedName("createdBy")
    private String createdBy;

    @Expose
    @SerializedName("createdAt")
    private Date createdAt;

    @Expose
    @SerializedName("lastModifiedBy")
    private String lastModifiedBy;

    @Expose
    @SerializedName("lastModifiedAt")
    private Date lastModifiedAt;

    @Expose
    @SerializedName("deleted")
    private boolean deleted;

    @Override
    public String toString() {
        return "AbstractEntity{" +
                "id=" + id +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", lastModifiedBy='" + lastModifiedBy + '\'' +
                ", lastModifiedAt='" + lastModifiedAt + '\'' +
                ", deleted=" + deleted +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getCreatedAtFormatted() {
        return getDateFormat().format(createdAt);
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getLastModifiedAt() {
        return lastModifiedAt;
    }

    public String getLastModifiedAtFormatted() {
        return getDateFormat().format(lastModifiedAt);
    }

    public void setLastModifiedAt(Date lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

}
