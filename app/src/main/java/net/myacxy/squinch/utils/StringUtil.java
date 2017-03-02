package net.myacxy.squinch.utils;

import java.util.Iterator;
import java.util.Map;

public class StringUtil {

    private StringUtil() {
        throw new IllegalAccessError();
    }

    public static String mapToString(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            sb.append(String.format("%s: %s", entry.getKey(), entry.getValue()));
            if (iterator.hasNext()) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
