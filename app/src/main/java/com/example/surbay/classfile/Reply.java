package com.example.surbay.classfile;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Reply implements Parcelable {
    private String id;
    private String content;
    private Date date;

    private String dateformat = "yyyy-MM-dd'T'hh:mm:ss.SSS";

    public Reply(String id, String content, Date date){
        this.id = id;
        this.content = content;
        this.date = date;
    }

    public String getid() {
        return id;
    }
    public void setid(String author) {        this.id = author;    }
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

    @SuppressLint("NewApi")
    public Reply(Parcel in){
        id = in.readString();
        content = in.readString();
        try {
            this.date = new SimpleDateFormat(dateformat).parse(in.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static final Creator<Reply> CREATOR = new Creator<Reply>() {
        @Override
        public Reply createFromParcel(Parcel in) {            return new Reply(in);        }

        @Override
        public Reply[] newArray(int size) {            return new Reply[size];        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(content);
        dest.writeString(new SimpleDateFormat(dateformat).format(this.date));
    }
}
