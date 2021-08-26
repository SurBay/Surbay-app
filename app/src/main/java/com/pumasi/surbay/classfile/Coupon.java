package com.pumasi.surbay.classfile;

import java.util.ArrayList;
import java.util.Date;

public class Coupon {
    private String id;
    private ArrayList<String> user;
    private boolean hide;
    private ArrayList<String> image_urls;
    private String title;
    private String content;
    private String author;
    private String category;
    private Integer cost;
    private Date date;

    public Coupon(String id, ArrayList<String> user, boolean hide, ArrayList<String> image_urls, String title, String content, String author, String category, Integer cost, Date date) {
        this.id = id;
        this.user = user;
        this.hide = hide;
        this.image_urls = image_urls;
        this.title = title;
        this.content = content;
        this.author = author;
        this.category = category;
        this.cost = cost;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getUser() {
        return user;
    }

    public void setUser(ArrayList<String> user) {
        this.user = user;
    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public ArrayList<String> getImage_urls() {
        return image_urls;
    }

    public void setImage_urls(ArrayList<String> image_urls) {
        this.image_urls = image_urls;
    }

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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
