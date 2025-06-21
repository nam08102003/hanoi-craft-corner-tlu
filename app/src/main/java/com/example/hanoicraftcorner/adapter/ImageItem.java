package com.example.hanoicraftcorner.adapter;

import android.net.Uri;

public class ImageItem {
    private Uri uri;
    private String type; // "certificate", "cccd_front", "cccd_back"

    public ImageItem(Uri uri, String type) {
        this.uri = uri;
        this.type = type;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

