package com.pumasi.surbay.classfile;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ContentReReply implements Parcelable {

    private String dateformat = "yyyy-MM-dd'T'kk:mm:ss.SSS";

    private String id;
    private ArrayList<String> reports;
    private Boolean hide;
    private ArrayList<String> report_reasons;
    private String writer;
    private String writer_name;
    private String replyID;
    private String content;
    private Date date;

    public ContentReReply(String id, ArrayList<String> reports, Boolean hide, ArrayList<String> report_reasons, String writer, String writer_name, String replyID, String content, Date date) {
        this.id = id;
        this.reports = reports;
        this.hide = hide;
        this.report_reasons = report_reasons;
        this.writer = writer;
        this.writer_name = writer_name;
        this.replyID = replyID;
        this.content = content;
        this.date = date;
    }

    public ContentReReply(Parcel in) {
        id = in.readString();
        reports = in.createStringArrayList();
        byte tmpHide = in.readByte();
        hide = tmpHide == 0 ? null : tmpHide == 1;
        report_reasons = in.createStringArrayList();
        writer = in.readString();
        writer_name = in.readString();
        replyID = in.readString();
        content = in.readString();
        try {
            this.date = new SimpleDateFormat(dateformat).parse(in.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static final Creator<ContentReReply> CREATOR = new Creator<ContentReReply>() {
        @Override
        public ContentReReply createFromParcel(Parcel in) {
            return new ContentReReply(in);
        }

        @Override
        public ContentReReply[] newArray(int size) {
            return new ContentReReply[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getReports() {
        return reports;
    }

    public void setReports(ArrayList<String> reports) {
        this.reports = reports;
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

    public String getReplyID() {
        return replyID;
    }

    public void setReplyID(String replyID) {
        this.replyID = replyID;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeStringList(reports);
        dest.writeByte((byte) (hide == null ? 0 : hide ? 1 : 2));
        dest.writeStringList(report_reasons);
        dest.writeString(writer);
        dest.writeString(writer_name);
        dest.writeString(replyID);
        dest.writeString(content);
        dest.writeString(new SimpleDateFormat(dateformat).format(this.date));

    }
}
