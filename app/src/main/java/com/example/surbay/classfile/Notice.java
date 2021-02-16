package com.example.surbay.classfile;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Notice implements Parcelable{
    private String id;
    private String title;
    private String author;
    private String content;
    private Date date;

    private String dateformat = "yyyy-MM-dd'T'hh:mm:ss.SSS";

    public Notice(String id,String title, String author, String content, Date date){
        this.id = id;
        this.title = title;
        this.author = author;
        this.content = content;
        this.date = date;
    }

    public String getID() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {        this.author = author;    }
    public String getContent() {        return content;    }
    public void setContent(String content) {
        this.content = content;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public String getTitle() {        return title;    }
    public void setTitle(String title) {        this.title = title;   }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.author);
        dest.writeString(this.content);
        dest.writeString(this.title);
        dest.writeString(new SimpleDateFormat(dateformat).format(this.date));
    }

    @SuppressLint("NewApi")
    public Notice(Parcel in){
        this.id = in.readString();
        this.author = in.readString();
        this.content = in.readString();
        this.title = in.readString();
        try {
            this.date = new SimpleDateFormat(dateformat).parse(in.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Notice createFromParcel(Parcel in) {
            return new Notice(in);
        }
        @Override
        public Notice[] newArray (int size) {
            return new Notice[size];
        }
    };
}
