package com.pumasi.surbay.classfile;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PostNonSurvey implements Parcelable{
    private String id;
    private String title;
    private String author;
    private Integer author_lvl;
    private String content;
    private Date date;
    private Integer category;
    private ArrayList<Reply> comments;


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
    public void setCategory(Integer category) { this.category = category;    }
    public Integer getCategory() { return category;    }
    public ArrayList<Reply> getComments() {        return comments;    }
    public void setComments(ArrayList<Reply> comments) { this.comments = comments;    }

    public PostNonSurvey(String id, String title, String author, Integer author_lvl, String content, Date date, Integer category, ArrayList<Reply> comments){
        this.id = id;
        this.title = title;
        this.author = author;
        this.author_lvl = author_lvl;
        this.content = content;
        Date seoul_date = date;
        seoul_date.setTime(seoul_date.getTime()+(9*60*60*1000));
        this.date = seoul_date;
        this.category = category;
        this.comments = new ArrayList<>(comments);
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
        this.category = in.readInt();
        this.comments = new ArrayList();
        in.readTypedList(this.comments, Reply.CREATOR);
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
        dest.writeInt(this.category);
        dest.writeTypedList(this.comments);
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
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