package com.ewaste.app;

import android.app.Application;
import com.ewaste.app.data.local.SessionManager;

public class EWasteApplication extends Application {

    private static EWasteApplication instance;
    private SessionManager sessionManager;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        sessionManager = new SessionManager(this);
    }

    public static EWasteApplication getInstance() {
        return instance;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
