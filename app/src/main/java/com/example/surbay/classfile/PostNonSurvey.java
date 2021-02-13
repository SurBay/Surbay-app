package com.example.surbay.classfile;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.LinearLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostNonSurvey implements Parcelable{
    private String id;
    private String title;
    private String author;
    private Integer author_lvl;
    private String content;
    private Date date;
    private String category;
    private List<Reply> comments;


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
    public void setCategory(String category) { this.category = category;    }
    public String getCategory() { return category;    }
    public List<Reply> getComments() {        return comments;    }
    public void setComments(List<Reply> comments) { this.comments = comments;    }

    public PostNonSurvey(String id, String title, String author, Integer author_lvl, String content, Date date, String category, List<Reply> comments){
        this.id = id;
        this.title = title;
        this.author = author;
        this.author_lvl = author_lvl;
        this.content = content;
        this.date = date;
        this.category = category;
        this.comments = comments;
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
        if (category != null ) {
            this.category = in.readString();
        }

        int size = in.readInt();
        if (size == 0) {
            comments = null;
        } else {
            Class<Reply> type = (Class<Reply>) in.readSerializable();
            comments = new ArrayList<>(size);
            in.readList(comments, type.getClassLoader());
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
        dest.writeString(this.category);

        if (comments == null || comments.size() == 0){
            dest.writeInt(0);
        } else {
            dest.writeInt(comments.size());

            final Class<Reply> objectsType = (Class<Reply>)comments.get(0).getClass();
            dest.writeSerializable(objectsType);

            dest.writeList(comments);
        }
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