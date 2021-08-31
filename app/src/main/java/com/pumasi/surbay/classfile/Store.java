package com.pumasi.surbay.classfile;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Store implements Parcelable {
    private String id;
    private Boolean hide;
    private ArrayList<String> img_urls;
    private ArrayList<Coupon> coupons_list;
    private String name;
    private String category;


    public Store(String id, Boolean hide, ArrayList<String> img_urls, ArrayList<Coupon> coupons_list, String name, String category) {
        this.id = id;
        this.hide = hide;
        this.img_urls = img_urls;
        this.coupons_list = coupons_list;
        this.name = name;
        this.category = category;
    }


    protected Store(Parcel in) {
        id = in.readString();
        byte tmpHide = in.readByte();
        hide = tmpHide == 0 ? null : tmpHide == 1;
        img_urls = in.createStringArrayList();
        coupons_list = in.createTypedArrayList(Coupon.CREATOR);
        name = in.readString();
        category = in.readString();
    }

    public static final Creator<Store> CREATOR = new Creator<Store>() {
        @Override
        public Store createFromParcel(Parcel in) {
            return new Store(in);
        }

        @Override
        public Store[] newArray(int size) {
            return new Store[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getHide() {
        return hide;
    }

    public void setHide(Boolean hide) {
        this.hide = hide;
    }

    public ArrayList<String> getImg_urls() {
        return img_urls;
    }

    public void setImg_urls(ArrayList<String> img_urls) {
        this.img_urls = img_urls;
    }

    public ArrayList<Coupon> getCoupons_list() {
        return coupons_list;
    }

    public void setCoupons_list(ArrayList<Coupon> coupons_list) {
        this.coupons_list = coupons_list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeByte((byte) (hide == null ? 0 : hide ? 1 : 2));
        dest.writeStringList(img_urls);
        dest.writeTypedList(coupons_list);
        dest.writeString(name);
        dest.writeString(category);
    }
}
