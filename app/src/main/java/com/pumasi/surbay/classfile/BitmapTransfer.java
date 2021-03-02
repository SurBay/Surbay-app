package com.pumasi.surbay.classfile;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class BitmapTransfer {
    public static Bitmap bitmap = null;
    public static ArrayList<Bitmap> bitmap_list = new ArrayList();

    public static void setBitmap(Bitmap bitmap) {
        BitmapTransfer.bitmap = bitmap;
    }

    public static Bitmap getBitmap() {
        return bitmap;
    }

    public static ArrayList<Bitmap> getBitmap_list() {
        return bitmap_list;
    }

    public static void setBitmap_list(ArrayList<Bitmap> bitmap_list) {
        BitmapTransfer.bitmap_list = bitmap_list;
    }
}