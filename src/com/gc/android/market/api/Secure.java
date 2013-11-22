package com.gc.android.market.api;

import java.util.prefs.Preferences;

public class Secure {
    private Preferences preferences = Preferences.userNodeForPackage(Secure.class);

    public Secure() {
        preferences.put("username_key", "hoge@localhost.localdomain");  // googleplay account
        preferences.put("password_key", "xxxxxxxxx");  // googleplay password
        preferences.put("apk_username_key", "fuga@localhost.localdomain");  // googleplay account
        preferences.put("apk_password_key", "ooooooooooo");  // googleplay password
    }

    public String getUsername() {
        return preferences.get("username_key", null);
    }

    public String getPassword() {
        return preferences.get("password_key", null);
    }
    
    public String getApkUsername() {
        return preferences.get("apk_username_key", null);
    }

    public String getApkPassword() {
        return preferences.get("apk_password_key", null);
    }
    
    public User[] getUsers() {
        /*
         * Ideally, one would want 11+ username. 1 username per Android version,
         * and the other username for crawling images and comments. For Testing
         * purposes, one can add the same username to different index array.
         */
        User[] users = new User[2];
        users[0] = new User(getUsername(), getPassword());
        users[1] = new User(getApkUsername(), getApkPassword());

        return users;
    }

}
