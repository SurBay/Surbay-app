package com.pumasi.surbay.classfile;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Surveytip implements Parcelable {
    private String id;
    private String title;
    private String author;
    private Integer author_lvl;
    private String content;
    private Date date;
    private String category;
    private Integer likes;
    private ArrayList<String> liked_users;

    private String dateformat = "yyyy-MM-dd'T'kk:mm:ss.SSS";

    public String getID() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
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
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public Integer getAuthor_lvl() {return author_lvl;}
    public void setAuthor_lvl(Integer author_lvl) {this.author_lvl = author_lvl;}

    public void setCategory(String category) { this.category = category;    }
    public String getCategory() { return category;    }
    public void setLikes(Integer likes) { this.likes = likes;    }
    public Integer getLikes() { return likes;    }

    public void setLiked_users(ArrayList<String> liked_users) {
        this.liked_users = liked_users;
    }

    public ArrayList<String> getLiked_users() {
        return liked_users;
    }

    public Surveytip(String id, String title, String author, Integer author_lvl, String content, Date date, String category, Integer likes, ArrayList<String> liked_users){
        this.id = id;
        this.title = title;
        this.author = author;
        this.author_lvl = author_lvl;
        this.content = content;
        Date seoul_date = date;
        seoul_date.setTime(seoul_date.getTime()+(9*60*60*1000));
        this.date = seoul_date;
        this.category = category;
        this.likes = likes;
        this.liked_users = liked_users;
    }

    public Surveytip(Parcel in){
        this.id = in.readString();
        this.title = in.readString();
        this.author = in.readString();
        this.author_lvl = in.readInt();
        this.content = in.readString();
        try {
            this.date = new SimpleDateFormat(dateformat).parse(in.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.category = in.readString();
        this.likes = in.readInt();
        this.liked_users = new ArrayList<>();
        in.readStringList(liked_users);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.author);
        dest.writeInt(this.author_lvl);
        dest.writeString(this.content);
        dest.writeString(new SimpleDateFormat(dateformat).format(this.date));
        if (category != null ) {
            dest.writeString(this.category);
        }
        dest.writeInt(this.likes);
        dest.writeStringList(this.liked_users);
    }
    public static final Creator<Surveytip> CREATOR = new Creator<Surveytip>() {
        @Override
        public Surveytip createFromParcel(Parcel in) {
            return new Surveytip(in);
        }
        @Override
        public Surveytip[] newArray (int size) {
            return new Surveytip[size];
        }
    };

}