package com.dewes.odonto.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Dewes on 13/06/2017.
 */

public class Attachment extends AbstractEntity {

    @Expose
    @SerializedName("url")
    private String url;

    @Expose
    @SerializedName("thumbUrl")
    private String thumbUrl;

    @Expose
    @SerializedName("alt")
    private String alt;

    @Expose
    @SerializedName("originalName")
    private String originalName;

    @Expose
    @SerializedName("fileSize")
    private double fileSize;

    @Expose
    @SerializedName("isImage")
    private boolean isImage;

    @Expose
    @SerializedName("imageWidth")
    private int imageWidth;

    @Expose
    @SerializedName("imageHeight")
    private int imageHeight;

    @Override
    public String toString() {
        return "Attachment{" +
                "url='" + url + '\'' +
                ", thumbUrl='" + thumbUrl + '\'' +
                ", alt='" + alt + '\'' +
                ", originalName='" + originalName + '\'' +
                ", fileSize=" + fileSize +
                ", isImage=" + isImage +
                ", imageWidth=" + imageWidth +
                ", imageHeight=" + imageHeight +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public double getFileSize() {
        return fileSize;
    }

    public void setFileSize(double fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setImage(boolean image) {
        isImage = image;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }
}
