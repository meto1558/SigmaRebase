package com.mentalfrostbyte.jello.util.system.other;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class GsonUtil {
    public static boolean getBooleanOrDefault(JsonObject jsonObject, String key, boolean defaultValue) {
        try {
            return jsonObject.get(key).getAsBoolean();
        } catch (JsonParseException e) {
            return defaultValue;
        }
    }

    public static int getIntOrDefault(JsonObject jsonObject, String key, int defaultValue) {
        try {
            return jsonObject.get(key).getAsInt();
        } catch (JsonParseException e) {
            return defaultValue;
        }
    }

    public static float getFloatOrDefault(JsonObject jsonObject, String key, float defaultValue) {
        try {
            return (float) jsonObject.get(key).getAsDouble();
        } catch (JsonParseException e) {
            return defaultValue;
        }
    }

    public static String getStringOrDefault(JsonObject jsonObject, String key, String defaultValue) {
        try {
            return jsonObject.get(key).getAsString();
        } catch (JsonParseException e) {
            return defaultValue;
        }
    }

    public static JsonObject getJSONObjectOrNull(JsonObject jsonObject, String key) {
        try {
            return jsonObject.getAsJsonObject(key);
        } catch (JsonParseException e) {
            return null;
        }
    }

    public static JsonArray getJSONArrayOrNull(JsonObject jsonObject, String key) {
        try {
            return jsonObject.getAsJsonArray(key);
        } catch (JsonParseException e) {
            return null;
        }
    }
}
