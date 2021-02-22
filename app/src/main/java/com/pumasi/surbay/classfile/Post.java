package com.pumasi.surbay.classfile;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Post implements Parcelable{
    private String id;
    private String title;
    private String author;
    private Integer author_lvl;
    private String content;
    private Integer participants;
    private Integer goal_participants;
    private String url;
    private Date date;
    private Date deadline;
    private boolean with_prize;
    private String prize;
    private Integer num_prize;
    private Integer est_time;
    private String target;
    private ArrayList<Reply> comments;
    private boolean done;
    private Integer extended;
    private ArrayList<String> participants_userids;
    private ArrayList<String> reports;
    private boolean hide;
    private String author_userid;



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
    public Integer getParticipants() {
        return participants;
    }
    public void setParticipants(Integer participants) {
        this.participants = participants;
    }
    public Integer getGoal_participants() {
        return goal_participants;
    }
    public void setGoal_participants(Integer goal_participants) {this.goal_participants = goal_participants;}
    public String getUrl() {
        return url;
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
    public boolean isWith_prize() {
        return with_prize;
    }
    public void setWith_prize(boolean with_prize) {
        this.with_prize = with_prize;
    }
    public void setNum_prize(Integer num_prize) { this.num_prize = num_prize;    }
    public Integer getNum_prize() {        return num_prize; }
    public Integer getAuthor_lvl() {return author_lvl;}
    public void setAuthor_lvl(Integer author_lvl) {this.author_lvl = author_lvl;}
    public String getPrize() {return prize;}
    public void setPrize(String prize) {this.prize = prize;}
    public Integer getEst_time() {return est_time;}
    public void setEst_time(Integer est_time) {this.est_time = est_time;}
    public void setUrl(String url) {
        this.url = url;
    }
    public String getTarget() {
        return target;
    }
    public void setTarget(String target) {
        this.target = target;
    }
    public void setDone(boolean done) { this.done = done;    }
    public boolean isDone() { return done;    }
    public ArrayList<Reply> getComments() {        return comments;    }
    public void setComments(ArrayList<Reply> comments) { this.comments = comments;    }
    public Integer getExtended() {
        return extended;
    }
    public void setExtended(Integer extended) {
        this.extended = extended;
    }
    public ArrayList<String> getParticipants_userids() {
        return participants_userids;
    }
    public void setParticipants_userids(ArrayList<String> participants_userids) {  this.participants_userids = participants_userids;    }
    public ArrayList<String> getReports() {        return reports;    }
    public void setReports(ArrayList<String> reports) {        this.reports = reports;    }
    public boolean isHide() {        return hide;    }
    public void setHide(boolean hide) {        this.hide = hide;    }

    public Post(String id, String title, String author, Integer author_lvl, String content, Integer participants, Integer goal_participants, String url, Date date, Date deadline, Boolean with_prize, String prize, Integer est_time, String target, Integer num_prize, ArrayList<Reply> comments, Boolean done, Integer extended, ArrayList<String> participants_userids,
                ArrayList<String> reports, Boolean hide, String author_userid){
        this.id = id;
        this.title = title;
        this.author = author;
        this.author_lvl = author_lvl;
        this.content = content;
        this.participants = participants;
        this.goal_participants = goal_participants;
        this.url = url;
        Date seoul_date = date;
        seoul_date.setTime(seoul_date.getTime()+(9*60*60*1000));
        this.date = seoul_date;
        Date seoul_deadline = deadline;
        seoul_deadline.setTime(seoul_deadline.getTime()+(9*60*60*1000));
        this.deadline = seoul_deadline;
        this.with_prize = with_prize;
        this.prize = prize;
        this.est_time = est_time;
        this.target = target;
        this.num_prize = num_prize;
        this.comments = new ArrayList<>(comments);
        this.done = done;
        this.extended = extended;
        this.participants_userids = new ArrayList<>(participants_userids);
        this.reports = new ArrayList<>(reports);
        this.hide = hide;
        this.author_userid = author_userid;
    }

    public Post(Parcel in){
        this.id = in.readString();
        this.title = in.readString();
        this.author = in.readString();
        this.author_lvl = in.readInt();
        this.content = in.readString();
        this.participants = in.readInt();
        this.goal_participants = in.readInt();
        this.url = in.readString();
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
        this.with_prize = Boolean.parseBoolean(in.readString());
        this.prize = in.readString();
        this.num_prize = in.readInt();
        this.est_time = in.readInt();
        this.target = in.readString();
        this.done = Boolean.parseBoolean(in.readString());
        this.comments = new ArrayList();
        in.readTypedList(this.comments, Reply.CREATOR);
        this.extended = in.readInt();
        this.participants_userids = new ArrayList<>();
        in.readStringList(participants_userids);
        this.reports = new ArrayList<>();
        in.readStringList(reports);
        this.hide=Boolean.parseBoolean(in.readString());
        this.author_userid = in.readString();
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
        dest.writeInt(this.participants);
        dest.writeInt(this.goal_participants);
        dest.writeString(this.url);
        dest.writeString(new SimpleDateFormat(dateformat).format(this.date));
        dest.writeString(new SimpleDateFormat(dateformat).format(this.deadline));
        dest.writeString(String.valueOf(this.with_prize));
        dest.writeString(this.prize);
        dest.writeInt(this.num_prize);
        dest.writeInt(this.est_time);
        dest.writeString(this.target);
        dest.writeString(String.valueOf(this.done));
        dest.writeTypedList(this.comments);
        dest.writeInt(this.extended);
        dest.writeStringList(this.participants_userids);
        dest.writeStringList(this.reports);
        dest.writeString(String.valueOf(this.hide));
        dest.writeString(this.author_userid);
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }
        @Override
        public Post[] newArray (int size) {
            return new Post[size];
        }
    };

    public String getAuthor_userid() {
        return author_userid;
    }

    public void setAuthor_userid(String author_userid) {
        this.author_userid = author_userid;
    }
}