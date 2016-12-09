package edu.sjsu.ireportgrp8;

import android.graphics.Bitmap;

/**
 * Created by dmodh on 12/5/16.
 */

public class ImageItem {
    private Bitmap image;
    private String url;

    public ImageItem(Bitmap image, String url) {
        super();
        this.image = image;
        this.url = url;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}