package com.pumasi.surbay.classfile;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

public class Coupon implements Parcelable {
    private String id;
    private boolean hide;
    private ArrayList<String> image_urls;
    private String title;
    private String content;
    private String author;
    private String category;
    private Integer cost;
    private Date date;

    public Coupon(String id, boolean hide, ArrayList<String> image_urls, String title, String content, String author, String category, Integer cost, Date date) {
        this.id = id;
        this.hide = hide;
        this.image_urls = image_urls;
        this.title = title;
        this.content = content;
        this.author = author;
        this.category = category;
        this.cost = cost;
        this.date = date;
    }


    protected Coupon(Parcel in) {
        id = in.readString();
        hide = in.readByte() != 0;
        image_urls = in.createStringArrayList();
        title = in.readString();
        content = in.readString();
        author = in.readString();
        category = in.readString();
        if (in.readByte() == 0) {
            cost = null;
        } else {
            cost = in.readInt();
        }
    }

    public static final Creator<Coupon> CREATOR = new Creator<Coupon>() {
        @Override
        public Coupon createFromParcel(Parcel in) {
            return new Coupon(in);
        }

        @Override
        public Coupon[] newArray(int size) {
            return new Coupon[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public ArrayList<String> getImage_urls() {
        return image_urls;
    }

    public void setImage_urls(ArrayList<String> image_urls) {
        this.image_urls = image_urls;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
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
        dest.writeByte((byte) (hide ? 1 : 0));
        dest.writeStringList(image_urls);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(author);
        dest.writeString(category);
        if (cost == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(cost);
        }
    }
}
