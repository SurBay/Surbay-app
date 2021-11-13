package com.pumasi.surbay.classfile;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyCoupon implements Parcelable {
    private String id;
    private String coupon_id;
    private Boolean used;
    private Date used_date;
    private String date;
    private Date due_date;
    private ArrayList<String> image_urls;
    private String store;
    private String menu;
    private String content;



    private String dateformat = "yyyy-MM-dd'T'kk:mm:ss.SSS";

    public MyCoupon(String id, String coupon_id, Boolean used, Date used_date, String date, Date due_date, ArrayList<String> image_urls, String store, String menu, String content) {
        this.id = id;
        this.coupon_id = coupon_id;
        this.used = used;
        this.used_date = used_date;
        this.date = date;
        if (due_date != null) {
            this.due_date = new Date(due_date.getTime() + 9 * 60 * 60 * 1000);
        }
        this.image_urls = image_urls;
        this.store = store;
        this.menu = menu;
        this.content = content;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(String coupon_id) {
        this.coupon_id = coupon_id;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    public Date getUsed_date() {
        return used_date;
    }

    public void setUsed_date(Date used_date) {
        this.used_date = used_date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<String> getImage_urls() {
        return image_urls;
    }

    public void setImage_urls(ArrayList<String> image_urls) {
        this.image_urls = image_urls;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDue_date() {
        return due_date;
    }

    public void setDue_date(Date due_date) {
        this.due_date = due_date;
    }
    public static Creator<MyCoupon> getCREATOR() {
        return CREATOR;
    }


    @SuppressLint("SimpleDateFormat")
    protected MyCoupon(Parcel in) {
        id = in.readString();
        coupon_id = in.readString();
        byte tmpUsed = in.readByte();
        used = tmpUsed == 0 ? null : tmpUsed == 1;
        image_urls = in.createStringArrayList();
        store = in.readString();
        menu = in.readString();
        content = in.readString();
        date = in.readString();
        if (this.used_date != null) {
            try {
                this.used_date = new SimpleDateFormat(dateformat).parse(in.readString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (this.due_date != null) {
            try {
                this.due_date = new SimpleDateFormat(dateformat).parse(in.readString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


    }

    public static final Creator<MyCoupon> CREATOR = new Creator<MyCoupon>() {
        @Override
        public MyCoupon createFromParcel(Parcel in) {
            return new MyCoupon(in);
        }

        @Override
        public MyCoupon[] newArray(int size) {
            return new MyCoupon[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(coupon_id);
        dest.writeByte((byte) (used == null ? 0 : used ? 1 : 2));
        dest.writeStringList(image_urls);
        dest.writeString(store);
        dest.writeString(menu);
        dest.writeString(content);
        dest.writeString(date);
        if (this.used_date != null) {
            dest.writeString(new SimpleDateFormat(dateformat).format(this.used_date));
        }
        if (this.due_date != null) {
            dest.writeString(new SimpleDateFormat(dateformat).format(this.due_date));
        }

    }
}
