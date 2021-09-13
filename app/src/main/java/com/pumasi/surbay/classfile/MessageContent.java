package com.pumasi.surbay.classfile;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class MessageContent implements Parcelable {
    private String id;
    private Boolean check;
    private String writer;
    private String content;
    private Date date;

    public MessageContent(String id, Boolean check, String writer, String content, Date date) {
        this.id = id;
        this.check = check;
        this.writer = writer;
        this.content = content;
        this.date = date;
    }

    protected MessageContent(Parcel in) {
        id = in.readString();
        byte tmpCheck = in.readByte();
        check = tmpCheck == 0 ? null : tmpCheck == 1;
        writer = in.readString();
        content = in.readString();
    }

    public static final Creator<MessageContent> CREATOR = new Creator<MessageContent>() {
        @Override
        public MessageContent createFromParcel(Parcel in) {
            return new MessageContent(in);
        }

        @Override
        public MessageContent[] newArray(int size) {
            return new MessageContent[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
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
        dest.writeByte((byte) (check == null ? 0 : check ? 1 : 2));
        dest.writeString(writer);
        dest.writeString(content);
    }
}
