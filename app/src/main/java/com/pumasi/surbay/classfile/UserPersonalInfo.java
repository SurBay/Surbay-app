package com.pumasi.surbay.classfile;

import java.util.ArrayList;

public class UserPersonalInfo {
    public static String token;
    public static String userID;
    public static String name;
    public static String email;
    public static String userPassword;
    public static Integer points, level, gender, yearBirth;
    public static ArrayList<String> participations, prizes;
    public static ArrayList<Notification> notifications;
    public static Boolean notificationAllow;
    public static Integer prize_check;

    public static void clearInfo(){
        token = null;
        userID = null;
        name = null;
        email = null;
//        userPassword= null;
        points = null;
        level = null;
        gender = null;
        yearBirth = null;
        participations = null;
        prizes = null;
        notifications = null;
        notificationAllow = true;
        prize_check = 0;
    }
}