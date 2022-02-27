package com.pumasi.surbay.classfile;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kotlin.text.UStringsKt;

public class General implements Parcelable{
    private String id;
    private String title;
    private String author;
    private Integer author_lvl;
    private String content;
    private Date date;
    private Date deadline;
    private ArrayList<Reply> comments;
    private Boolean done;
    private String author_userid;
    private ArrayList<String> reports;
    private Boolean multi_response;
    private Integer participants;
    private ArrayList<String> participants_userids;
    private Boolean with_image;
    private ArrayList<Poll> polls;
    private ArrayList<String> liked_users;
    private Integer likes;
    private Boolean hide;
    private Boolean special;
    private Integer visit;

    public String getID(){return id;}
    public void setId(String id){this.id = id;}
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

    public Integer getAuthor_lvl() {
        return author_lvl;
    }

    public void setAuthor_lvl(Integer author_lvl) {
        this.author_lvl = author_lvl;
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

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public ArrayList<Reply> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Reply> comments) {
        this.comments = comments;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public String getAuthor_userid() {
        return author_userid;
    }

    public void setAuthor_userid(String author_userid) {
        this.author_userid = author_userid;
    }

    public ArrayList<String> getReports() {
        return reports;
    }

    public void setReports(ArrayList<String> reports) {
        this.reports = reports;
    }

    public Boolean getMulti_response() {
        return multi_response;
    }

    public void setMulti_response(Boolean multi_response) {
        this.multi_response = multi_response;
    }

    public Integer getParticipants() {
        return participants;
    }

    public void setParticipants(Integer participants) {
        this.participants = participants;
    }

    public ArrayList<String> getParticipants_userids() {
        return participants_userids;
    }

    public void setParticipants_userids(ArrayList<String> participants_userids) {
        this.participants_userids = participants_userids;
    }

    public Boolean getWith_image() {
        return with_image;
    }

    public void setWith_image(Boolean with_image) {
        this.with_image = with_image;
    }

    public ArrayList<Poll> getPolls() {
        return polls;
    }

    public void setPolls(ArrayList<Poll> polls) {
        this.polls = polls;
    }

    public ArrayList<String> getLiked_users() {
        return liked_users;
    }

    public void setLiked_users(ArrayList<String> liked_users) {
        this.liked_users = liked_users;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }



    private String dateformat = "yyyy-MM-dd'T'kk:mm:ss.SSS";

    public General(String id, String title, String author, Integer author_lvl, String content,
                   Date date, Date deadline, ArrayList<Reply> comments, Boolean done,
                   String author_userid, ArrayList<String> reports, Boolean multi_response,
                   Integer participants, ArrayList<String> participants_userids, Boolean with_image,
                   ArrayList<Poll> polls, ArrayList<String> liked_users, Integer likes, Boolean hide){
        this.id = id;
        this.title = title;
        this.author = author;
        this.author_lvl = author_lvl;
        this.content = content;
        Date seoul_date = date;
        seoul_date.setTime(seoul_date.getTime()+(9*60*60*1000));
        this.date = seoul_date;
        Date seoul_deadline = deadline;
        seoul_deadline.setTime(seoul_deadline.getTime()+(9*60*60*1000));
        this.deadline = seoul_deadline;
        this.comments = comments;
        this.done = done;
        this.author_userid = author_userid;
        this.reports = reports;
        this.multi_response = multi_response;
        this.participants = participants;
        this.participants_userids = participants_userids;
        this.with_image = with_image;
        this.polls = polls;
        this.liked_users = liked_users;
        this.likes = likes;
        this.hide = hide;
        for (Poll poll: polls) {
            Log.d("poll_images", "General: " + poll.getImage());
        }
    }
    public General(String id, String title, String author, Integer author_lvl, String content,
                   Date date, Date deadline, ArrayList<Reply> comments, Boolean done,
                   String author_userid, ArrayList<String> reports, Boolean multi_response,
                   Integer participants, ArrayList<String> participants_userids, Boolean with_image,
                   ArrayList<Poll> polls, ArrayList<String> liked_users, Integer likes, Boolean hide, Boolean special, Integer visit){
        this.id = id;
        this.title = title;
        this.author = author;
        this.author_lvl = author_lvl;
        this.content = content;
        Date seoul_date = date;
        seoul_date.setTime(seoul_date.getTime()+(9*60*60*1000));
        this.date = seoul_date;
        Date seoul_deadline = deadline;
        seoul_deadline.setTime(seoul_deadline.getTime()+(9*60*60*1000));
        this.deadline = seoul_deadline;
        this.comments = comments;
        this.done = done;
        this.author_userid = author_userid;
        this.reports = reports;
        this.multi_response = multi_response;
        this.participants = participants;
        this.participants_userids = participants_userids;
        this.with_image = with_image;
        this.polls = polls;
        this.liked_users = liked_users;
        this.likes = likes;
        this.hide = hide;
        this.special = special;
        this.visit = visit;
    }

    public General(Parcel in){
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
        try {
            this.deadline = new SimpleDateFormat(dateformat).parse(in.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.comments = new ArrayList();
        in.readTypedList(this.comments, Reply.CREATOR);
        this.done = Boolean.parseBoolean(in.readString());
        this.author_userid = in.readString();
        this.reports = new ArrayList<>();
        in.readStringList(reports);
        this.multi_response = Boolean.parseBoolean(in.readString());
        this.participants = in.readInt();
        this.participants_userids = new ArrayList<>();
        in.readStringList(participants_userids);
        this.with_image = Boolean.parseBoolean(in.readString());
        this.polls = new ArrayList<>();
        in.readTypedList(this.polls, Poll.CREATOR);
        this.liked_users = new ArrayList<>();
        in.readStringList(liked_users);
        this.likes = in.readInt();
        this.hide = Boolean.parseBoolean(in.readString());
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.author);
        dest.writeInt(this.author_lvl);
        dest.writeString(this.content);
        dest.writeString(new SimpleDateFormat(dateformat).format(this.date));
        dest.writeString(new SimpleDateFormat(dateformat).format(this.deadline));
        dest.writeTypedList(this.comments);
        dest.writeString(String.valueOf(this.done));
        dest.writeString(this.author_userid);
        dest.writeStringList(this.reports);
        dest.writeString(String.valueOf(this.multi_response));
        dest.writeInt(this.participants);
        dest.writeStringList(this.participants_userids);
        dest.writeString(String.valueOf(this.with_image));
        dest.writeTypedList(this.polls);
        dest.writeStringList(this.liked_users);
        dest.writeInt(this.likes);
        dest.writeString(String.valueOf(this.hide));
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public General createFromParcel(Parcel in) {
            return new General(in);
        }
        @Override
        public General[] newArray (int size) {
            return new General[size];
        }
    };

    public Boolean getHide() {
        return hide;
    }

    public void setHide(Boolean hide) {
        this.hide = hide;
    }

    public Boolean getSpecial() {
        return special;
    }

    public void setSpecial(Boolean special) {
        this.special = special;
    }

    public Integer getVisit() {
        return visit;
    }

    public void setVisit(Integer visit) {
        this.visit = visit;
    }
}