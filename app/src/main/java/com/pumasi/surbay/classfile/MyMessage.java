package com.pumasi.surbay.classfile;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

public class MyMessage implements Parcelable {
    private String id;
    private Integer text_num;
    private Integer unchecked_num;
    private String from_userID;
    private String to_userID;
    private String from_name;
    private String to_name;
    private ArrayList<MessageContent> content;
    private Date date;

    public MyMessage(String id, Integer text_num, Integer unchecked_num, String from_userID, String to_userID, String from_name, String to_name, ArrayList<MessageContent> content, Date date) {
        this.id = id;
        this.text_num = text_num;
        this.unchecked_num = unchecked_num;
        this.from_userID = from_userID;
        this.to_userID = to_userID;
        this.from_name = from_name;
        this.to_name = to_name;
        this.content = content;
        this.date = date;
    }

    protected MyMessage(Parcel in) {
        id = in.readString();
        if (in.readByte() == 0) {
            text_num = null;
        } else {
            text_num = in.readInt();
        }
        if (in.readByte() == 0) {
            unchecked_num = null;
        } else {
            unchecked_num = in.readInt();
        }
        from_userID = in.readString();
        to_userID = in.readString();
        from_name = in.readString();
        to_name = in.readString();
        content = in.createTypedArrayList(MessageContent.CREATOR);
    }

    public static final Creator<MyMessage> CREATOR = new Creator<MyMessage>() {
        @Override
        public MyMessage createFromParcel(Parcel in) {
            return new MyMessage(in);
        }

        @Override
        public MyMessage[] newArray(int size) {
            return new MyMessage[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getText_num() {
        return text_num;
    }

    public void setText_num(Integer text_num) {
        this.text_num = text_num;
    }

    public Integer getUnchecked_num() {
        return unchecked_num;
    }

    public void setUnchecked_num(Integer unchecked_num) {
        this.unchecked_num = unchecked_num;
    }

    public String getFrom_userID() {
        return from_userID;
    }

    public void setFrom_userID(String from_userID) {
        this.from_userID = from_userID;
    }

    public String getTo_userID() {
        return to_userID;
    }

    public void setTo_userID(String to_userID) {
        this.to_userID = to_userID;
    }

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public String getTo_name() {
        return to_name;
    }

    public void setTo_name(String to_name) {
        this.to_name = to_name;
    }

    public ArrayList<MessageContent> getContent() {
        return content;
    }

    public void setContent(ArrayList<MessageContent> content) {
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
        if (text_num == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(text_num);
        }
        if (unchecked_num == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(unchecked_num);
        }
        dest.writeString(from_userID);
        dest.writeString(to_userID);
        dest.writeString(from_name);
        dest.writeString(to_name);
        dest.writeTypedList(content);
    }
}
