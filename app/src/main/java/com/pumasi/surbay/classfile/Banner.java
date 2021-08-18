package com.pumasi.surbay.classfile;

import java.util.Date;

public class Banner {
    private String id;
    private int type;
    private boolean hide;
    private boolean done;
    private String title;
    private String author;
    private String content;
    private String url;
    private Date date;
    private String image_url;

    public Banner (String id, int type, boolean hide, boolean done, String title, String author, String content, String url, Date date, String image_url) {
        this.id = id;
        this.type = type;
        this.hide = hide;
        this.done = done;
        this.title = title;
        this.author = author;
        this.content = content;
        this.url = url;
        this.date = date;
        this.image_url = image_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
