package com.ewaste.app.data.local;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manages user session and authentication token persistence in SharedPreferences.
 */
public class SessionManager {

    private static final String PREF_NAME = "ewaste_session_prefs";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_ROLE = "role";
    private static final String KEY_DASHBOARD_SUMMARY = "dashboard_summary";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(String token, long userId, String email, String fullName, String role, String summary) {
        prefs.edit()
                .putString(KEY_TOKEN, token)
                .putLong(KEY_USER_ID, userId)
                .putString(KEY_EMAIL, email)
                .putString(KEY_FULL_NAME, fullName)
                .putString(KEY_ROLE, role)
                .putString(KEY_DASHBOARD_SUMMARY, summary)
                .apply();
    }

    public boolean isLoggedIn() {
        return getToken() != null && !getToken().trim().isEmpty();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public long getUserId() {
        return prefs.getLong(KEY_USER_ID, -1);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public String getFullName() {
        return prefs.getString(KEY_FULL_NAME, "");
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, "");
    }

    public String getDashboardSummary() {
        return prefs.getString(KEY_DASHBOARD_SUMMARY, "");
    }

    public void setDashboardSummary(String summary) {
        prefs.edit().putString(KEY_DASHBOARD_SUMMARY, summary).apply();
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }
}
