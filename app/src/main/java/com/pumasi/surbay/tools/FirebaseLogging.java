package com.pumasi.surbay.tools;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;


public class FirebaseLogging {
    private Context context;
    public FirebaseLogging(Context context) {
        this.context = context;
    }
    FirebaseAnalytics fa = FirebaseAnalytics.getInstance(context);

    public void LogScreen(String SCREEN_CLASS, String SCREEN_NAME) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, SCREEN_CLASS);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, SCREEN_NAME);
        fa.logEvent(SCREEN_NAME, bundle);
    }
}
