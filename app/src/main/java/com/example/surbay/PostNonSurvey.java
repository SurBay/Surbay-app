package com.example.surbay;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostNonSurvey implements Parcelable{
    private String id;
    private String title;
    private String author;
    private Integer author_lvl;
    private String content;
    private Date date;

    private String dateformat = "yyyy-MM-dd'T'hh:mm:ss.SSS";

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

    public PostNonSurvey(String id, String title, String author, Integer author_lvl, String content, Date date){
        this.id = id;
        this.title = title;
        this.author = author;
        this.author_lvl = author_lvl;
        this.content = content;
        this.date = date;
    }

    @SuppressLint("NewApi")
    public PostNonSurvey(Parcel in){
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
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressLint("NewApi")
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.author);
        dest.writeInt(this.author_lvl);
        dest.writeString(this.content);
        dest.writeString(new SimpleDateFormat(dateformat).format(this.date));
    }
    public static final Creator<PostNonSurvey> CREATOR = new Creator<PostNonSurvey>() {
        @Override
        public PostNonSurvey createFromParcel(Parcel in) {
            return new PostNonSurvey(in);
        }
        @Override
        public PostNonSurvey[] newArray (int size) {
            return new PostNonSurvey[size];
        }
    };



}