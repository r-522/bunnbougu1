package com.bunnbougu.app.service;

import com.bunnbougu.app.model.SessionInfo;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionService {
    private final Map<String, SessionInfo> sessions = new ConcurrentHashMap<>();

    public SessionInfo login(String staffCode, String displayName) {
        String token = UUID.randomUUID().toString();
        SessionInfo sessionInfo = new SessionInfo(token, staffCode, displayName == null || displayName.isBlank() ? "担当者" : displayName);
        sessions.put(token, sessionInfo);
        return sessionInfo;
    }

    public SessionInfo validate(String token) {
        return sessions.get(token);
    }
}
