package com.gc.android.market.api;

public class User {

    /**
     * The username that the user wishes to use to access the Marketplace
     */
    private String username;

    /**
     * The password respective to the username in use
     */
    private String password;

    /**
     * A boolean flag which indicates if the username is currently being used
     */
    private boolean status;

    /**
     * Constructs a <code>User</code> object using the passed parameters.
     * 
     * @param username
     *            The username that the user wishes to use to access the
     *            Marketplace
     * @param password
     *            The password respective to the username in use
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.status = false;
    }

    /**
     * Gets the username
     * 
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password
     * 
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the status of the user in question. returns true, if the username is
     * in use; false otherwise.
     * 
     * @return status
     */
    public boolean inUse() {
        return status;
    }

    /**
     * Set the status of username is question.
     * 
     * @param status
     *            true if the username is in use; false otherwise
     */
    public void inUse(boolean status) {
        this.status = status;
    }
}
