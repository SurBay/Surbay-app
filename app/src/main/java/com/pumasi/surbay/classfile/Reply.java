package com.pumasi.surbay.classfile;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Reply implements Parcelable {
    private String id;
    private String writer;
    private String content;
    private Date date;
    private String dateformat = "yyyy-MM-dd'T'hh:mm:ss.SSS";
    private ArrayList<String> reports;
    private boolean hide;

    public Reply(String id, String writer, String content, Date date, ArrayList<String> reports, Boolean hide){
        this.id = id;
        this.writer = writer;
        this.content = content;
        Date seoul_date = date;
        seoul_date.setTime(seoul_date.getTime()+(9*60*60*1000));
        this.date = seoul_date;
        this.reports = new ArrayList<>(reports);
        this.hide = hide;
    }
    public String getID() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getWriter() {
        return writer;
    }
    public void setWriter(String author) {        this.writer = author;    }
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
    public ArrayList<String> getReports() {        return reports;    }
    public void setReports(ArrayList<String> reports) {        this.reports = reports;    }
    public boolean isHide() {        return hide;    }
    public void setHide(boolean hide) {        this.hide = hide;    }

    @SuppressLint("NewApi")
    public Reply(Parcel in){
        this.id = in.readString();
        this.writer = in.readString();
        this.content = in.readString();
        try {
            Log.d("in reply", ""+this.id+this.writer+this.content);
            this.date = new SimpleDateFormat(dateformat).parse(in.readString());
            Log.d("in reply2", ""+this.id+this.writer+this.content+this.date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.reports = new ArrayList<>();
        in.readStringList(reports);
        this.hide=Boolean.parseBoolean(in.readString());
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
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
        dest.writeString(this.id);
        dest.writeString(this.writer);
        dest.writeString(this.content);
        dest.writeString(new SimpleDateFormat(dateformat).format(this.date));
        Log.d("in reply3", ""+this.id+this.writer+this.content+this.date);
        dest.writeStringList(this.reports);
        dest.writeString(String.valueOf(this.hide));
    }
}