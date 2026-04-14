package com.bunnbougu.app.model;

public class SessionInfo {
    private String token;
    private String staffCode;
    private String displayName;

    public SessionInfo() {
    }

    public SessionInfo(String token, String staffCode, String displayName) {
        this.token = token;
        this.staffCode = staffCode;
        this.displayName = displayName;
    }

    public String getToken() {
        return token;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public String getDisplayName() {
        return displayName;
    }
}
