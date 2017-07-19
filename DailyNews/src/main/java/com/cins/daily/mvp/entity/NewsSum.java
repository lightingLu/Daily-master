package com.cins.daily.mvp.entity;

import java.io.Serializable;

/**
 * Created by light on 2017/6/6.
 */

public class NewsSum implements Serializable{
    private String digest;
    private String imgsrc;
    private String ptime;
    private String title;

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getImgsrc() {
        return imgsrc;
    }

    public void setImgsrc(String imgsrc) {
        this.imgsrc = imgsrc;
    }

    public String getPtime() {
        return ptime;
    }

    public void setPtime(String ptime) {
        this.ptime = ptime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
