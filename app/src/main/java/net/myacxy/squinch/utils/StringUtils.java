package net.myacxy.squinch.utils;

import org.json.JSONObject;

import java.util.Map;

public class StringUtils {

    private StringUtils() {
        throw new IllegalAccessError();
    }

    public static String booleanMapToString(Map<String, Boolean> map) {
        return new JSONObject(map).toString();
    }

    public static String integerMapToString(Map<String, Integer> map) {
        return new JSONObject(map).toString();
    }

    public static String stringMapToString(Map<String, String> map) {
        return new JSONObject(map).toString();
    }
}
