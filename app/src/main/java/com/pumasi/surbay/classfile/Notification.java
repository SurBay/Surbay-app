package com.pumasi.surbay.classfile;

import java.util.Date;

public class Notification {
    private String title;
    private String content;
    private String post_id;
    private Date date;
    private Integer post_type;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getPost_type() {
        return post_type;
    }

    public void setPost_type(Integer post_type) {
        this.post_type = post_type;
    }

    public Notification(String title, String content, String post_id, Date date, Integer post_type){
        this.title =  title;
        this.content = content;
        this.post_id = post_id;
        if (date != null) {
            Date seoul_date = date;
            seoul_date.setTime(seoul_date.getTime()+(9*60*60*1000));
            this.date = seoul_date;
        } else {
            this.date = date;
        }
        this.post_type = post_type;
    }
}
