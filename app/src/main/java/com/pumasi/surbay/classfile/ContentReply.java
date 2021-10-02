package com.pumasi.surbay.classfile;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ContentReply implements Parcelable {

    private String dateformat = "yyyy-MM-dd'T'kk:mm:ss.SSS";

    private String id;
    private ArrayList<ContentReReply> reply;
    private Boolean hide;
    private ArrayList<String> reports;
    private ArrayList<String> report_reasons;
    private String writer;
    private String writer_name;
    private Date date;
    private String content;

    public ContentReply(String id, ArrayList<ContentReReply> reply, Boolean hide, ArrayList<String> reports, ArrayList<String> report_reasons, String writer, String writer_name, Date date, String content) {
        this.id = id;
        this.reply = reply;
        this.hide = hide;
        this.reports = reports;
        this.report_reasons = report_reasons;
        this.writer = writer;
        this.writer_name = writer_name;
        this.date = date;
        this.content = content;
    }

    protected ContentReply(Parcel in) {
        id = in.readString();
        reply = in.createTypedArrayList(ContentReReply.CREATOR);
        byte tmpHide = in.readByte();
        hide = tmpHide == 0 ? null : tmpHide == 1;
        reports = in.createStringArrayList();
        report_reasons = in.createStringArrayList();
        writer = in.readString();
        writer_name = in.readString();
        try {
            this.date = new SimpleDateFormat(dateformat).parse(in.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        content = in.readString();
    }

    public static final Creator<ContentReply> CREATOR = new Creator<ContentReply>() {
        @Override
        public ContentReply createFromParcel(Parcel in) {
            return new ContentReply(in);
        }

        @Override
        public ContentReply[] newArray(int size) {
            return new ContentReply[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<ContentReReply> getReply() {
        return reply;
    }

    public void setReply(ArrayList<ContentReReply> reply) {
        this.reply = reply;
    }

    public Boolean getHide() {
        return hide;
    }

    public void setHide(Boolean hide) {
        this.hide = hide;
    }

    public ArrayList<String> getReport_reasons() {
        return report_reasons;
    }

    public void setReport_reasons(ArrayList<String> report_reasons) {
        this.report_reasons = report_reasons;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getWriter_name() {
        return writer_name;
    }

    public void setWriter_name(String writer_name) {
        this.writer_name = writer_name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<String> getReports() {
        return reports;
    }

    public void setReports(ArrayList<String> reports) {
        this.reports = reports;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeTypedList(reply);
        dest.writeByte((byte) (hide == null ? 0 : hide ? 1 : 2));
        dest.writeStringList(reports);
        dest.writeStringList(report_reasons);
        dest.writeString(writer);
        dest.writeString(writer_name);
        dest.writeString(new SimpleDateFormat(dateformat).format(this.date));
        dest.writeString(content);
    }
}
