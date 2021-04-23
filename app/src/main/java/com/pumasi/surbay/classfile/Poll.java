package com.pumasi.surbay.classfile;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Poll implements Parcelable {
    private String id;
    private String content;
    private ArrayList<String> participants_userids;
    private String image;

    public Poll(String id, String content, ArrayList<String> participants_userids, @Nullable String image){
        this.id = id;
        this.content = content;
        this.participants_userids = participants_userids;
        this.image = image;
    }
    public String getID() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public ArrayList<String> getParticipants_userids() {
        return participants_userids;
    }
    public void setParticipants_userids(ArrayList<String> participants_userids) {
        this.participants_userids = participants_userids;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public void pushParticipant(String userid){this.participants_userids.add(userid);}
    public void pullParticipant(String userid){this.participants_userids.remove(userid);}
    @SuppressLint("NewApi")
    public Poll(Parcel in){
        this.id = in.readString();
        this.content = in.readString();
        this.participants_userids = new ArrayList<>();
        in.readStringList(participants_userids);
        this.image = in.readString();
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Poll createFromParcel(Parcel in) {            return new Poll(in);        }
        @Override
        public Poll[] newArray(int size) {            return new Poll[size];        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.content);
        dest.writeStringList(this.participants_userids);
        dest.writeString(this.image);
    }

}