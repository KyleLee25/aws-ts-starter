package com.example.utils;

import java.util.HashMap;
import java.util.Map;

public class CorsHeaders {
    public static Map<String, String> getCorsHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Credentials", "true");
        headers.put("Access-Control-Allow-Methods", "OPTIONS,POST,GET,PUT,DELETE");
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token");
        return headers;
    }
} 