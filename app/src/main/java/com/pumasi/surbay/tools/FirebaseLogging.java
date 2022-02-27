package com.pumasi.surbay.tools;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.pumasi.surbay.classfile.UserPersonalInfo;


public class FirebaseLogging {
    private Context context;

    public FirebaseLogging(Context context) {
        this.context = context;
    }
    FirebaseAnalytics fa = FirebaseAnalytics.getInstance(context);

    public void LogScreen(String SCREEN_CLASS, String SCREEN_NAME) {
        try {
            if (UserPersonalInfo.userID != null && UserPersonalInfo.userID.equals("nonMember")) {
                SCREEN_CLASS = "N_" + SCREEN_CLASS;
                SCREEN_NAME = "N_" + SCREEN_NAME;
            }
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, SCREEN_CLASS);
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, SCREEN_NAME);
            fa.logEvent(SCREEN_NAME, bundle);
        } catch (Exception e) {
        }

    }
}
