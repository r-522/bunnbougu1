package com.bunnbougu.app.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class JsonUtil {
    private JsonUtil() {
    }

    public static final Gson GSON = new GsonBuilder().serializeNulls().create();
}
