package com.pumasi.surbay;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


public class AnalyticsApplication extends Application {
    public static GoogleAnalytics analytics;
    public static Tracker tracker;
    private final String trackingId = "<your Tracking ID>";

    public void onCreate() {
        super.onCreate();
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        tracker = getDefaultTracker();
    }

    synchronized public Tracker getDefaultTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            tracker = analytics.newTracker(trackingId);
            tracker.enableExceptionReporting(true);
            tracker.enableAdvertisingIdCollection(true);
            tracker.enableAutoActivityTracking(true);
        }
        return tracker;
    }

    public void sendEventGoogleAnalytics(String title, String message) {
        tracker = this.getDefaultTracker();
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(title).setAction(message)
                .build());
    }



}
