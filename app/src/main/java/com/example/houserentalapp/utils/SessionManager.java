package com.example.houserentalapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(String uid, String role, String name, String email) {
        editor.putString(Constants.PREF_UID, uid);
        editor.putString(Constants.PREF_ROLE, role);
        editor.putString(Constants.PREF_NAME, name);
        editor.putString(Constants.PREF_EMAIL, email);
        editor.apply();
    }

    public void saveProfileImage(String imageUrl) {
        editor.putString(Constants.PREF_PROFILE_IMAGE, imageUrl);
        editor.apply();
    }

    public void saveCurrency(String currency) {
        editor.putString(Constants.PREF_CURRENCY, currency);
        editor.apply();
    }

    public void setDarkMode(boolean enabled) {
        editor.putBoolean(Constants.PREF_DARK_MODE, enabled);
        editor.apply();
    }

    public String getUid() {
        return prefs.getString(Constants.PREF_UID, null);
    }

    public String getRole() {
        return prefs.getString(Constants.PREF_ROLE, Constants.ROLE_TENANT);
    }

    public String getName() {
        return prefs.getString(Constants.PREF_NAME, "");
    }

    public String getEmail() {
        return prefs.getString(Constants.PREF_EMAIL, "");
    }

    public String getProfileImage() {
        return prefs.getString(Constants.PREF_PROFILE_IMAGE, null);
    }

    public String getCurrency() {
        return prefs.getString(Constants.PREF_CURRENCY, Constants.CURRENCY_INR);
    }

    public String getCurrencySymbol() {
        return Constants.getCurrencySymbol(getCurrency());
    }

    public boolean isDarkMode() {
        return prefs.getBoolean(Constants.PREF_DARK_MODE, false);
    }

    public boolean isLoggedIn() {
        return getUid() != null;
    }

    public boolean isTenant() {
        return Constants.ROLE_TENANT.equals(getRole());
    }

    public boolean isLandlord() {
        return Constants.ROLE_LANDLORD.equals(getRole());
    }

    public boolean isAdmin() {
        return Constants.ROLE_ADMIN.equals(getRole());
    }

    public void clear() {
        editor.clear();
        editor.apply();
    }
}
