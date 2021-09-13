package com.pumasi.surbay.classfile;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Content implements Parcelable {
    private String id;
    private ArrayList<String> image_urls;
    private Integer likes;
    private Integer visit;
    private Boolean hide;
    private String title;
    private String author;
    private String content;
    private Date date;
    private ArrayList<ContentReply> comments;
    private ArrayList<String> liked_users;
    private String dateformat = "yyyy-MM-dd'T'kk:mm:ss.SSS";


    public Content(String id, ArrayList<String> image_urls, Integer likes, Integer visit, Boolean hide, String title, String author, String content, Date date, ArrayList<ContentReply> comments, ArrayList<String> liked_users) {
        this.id = id;
        this.image_urls = image_urls;
        this.likes = likes;
        this.visit = visit;
        this.hide = hide;
        this.title = title;
        this.author = author;
        this.content = content;
        this.date = date;
        this.comments = comments;
        this.liked_users = liked_users;
    }

    protected Content(Parcel in) {
        id = in.readString();
        image_urls = in.createStringArrayList();
        if (in.readByte() == 0) {
            likes = null;
        } else {
            likes = in.readInt();
        }
        if (in.readByte() == 0) {
            visit = null;
        } else {
            visit = in.readInt();
        }
        byte tmpHide = in.readByte();
        hide = tmpHide == 0 ? null : tmpHide == 1;
        title = in.readString();
        author = in.readString();
        content = in.readString();
        try {
            this.date = new SimpleDateFormat(dateformat).parse(in.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        comments = in.createTypedArrayList(ContentReply.CREATOR);
        liked_users = in.createStringArrayList();
    }

    public static final Creator<Content> CREATOR = new Creator<Content>() {
        @Override
        public Content createFromParcel(Parcel in) {
            return new Content(in);
        }

        @Override
        public Content[] newArray(int size) {
            return new Content[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getImage_urls() {
        return image_urls;
    }

    public void setImage_urls(ArrayList<String> image_urls) {
        this.image_urls = image_urls;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getVisit() {
        return visit;
    }

    public void setVisit(Integer visit) {
        this.visit = visit;
    }

    public Boolean getHide() {
        return hide;
    }

    public void setHide(Boolean hide) {
        this.hide = hide;
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

    public ArrayList<ContentReply> getComments() {
        return comments;
    }

    public void setComments(ArrayList<ContentReply> comments) {
        this.comments = comments;
    }

    public ArrayList<String> getLiked_users() {
        return liked_users;
    }

    public void setLiked_users(ArrayList<String> liked_users) {
        this.liked_users = liked_users;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeStringList(image_urls);
        if (likes == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(likes);
        }
        if (visit == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(visit);
        }
        dest.writeByte((byte) (hide == null ? 0 : hide ? 1 : 2));
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(new SimpleDateFormat(dateformat).format(this.date));
        dest.writeTypedList(comments);
        dest.writeStringList(liked_users);
    }
}
