package com.pumasi.surbay.classfile;

import java.util.ArrayList;

public class UserPersonalInfo {
    public static String token;
    public static String userID, name, email, phoneNumber, userPassword;
    public static Integer points, level, gender, yearBirth;
    public static ArrayList<String> participations, prizes;

    public static void clearInfo(){
        token = null;
        userID = null;
        name = null;
        email = null;
        phoneNumber = null;
        userPassword= null;
        points = null;
        level = null;
        gender = null;
        yearBirth = null;
        participations = null;
        prizes = null;

    }
}