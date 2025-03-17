package totalcross.json;

import com.google.gson.JsonParseException;

public class CJsonUtils {

    public static boolean getBooleanOrDefault(JSONObject jsonObject, String key, boolean defaultValue) {
        try {
            return jsonObject.getBoolean(key);
        } catch (JsonParseException e) {
            return defaultValue;
        }
    }

    public static int getIntOrDefault(JSONObject jsonObject, String key, int defaultValue) {
        try {
            return jsonObject.getInt(key);
        } catch (JsonParseException e) {
            return defaultValue;
        }
    }

    public static float getFloatOrDefault(JSONObject jsonObject, String key, float defaultValue) {
        try {
            return (float) jsonObject.getDouble(key);
        } catch (JsonParseException e) {
            return defaultValue;
        }
    }

    public static String getStringOrDefault(JSONObject jsonObject, String key, String defaultValue) {
        try {
            return jsonObject.getString(key);
        } catch (JsonParseException e) {
            return defaultValue;
        }
    }

    public static JSONObject getJSONObjectOrNull(JSONObject jsonObject, String key) {
        try {
            return jsonObject.getJSONObject(key);
        } catch (JsonParseException e) {
            return null;
        }
    }

    public static JSONArray getJSONArrayOrNull(JSONObject jsonObject, String key) {
        try {
            return jsonObject.getJSONArray(key);
        } catch (JsonParseException e) {
            return null;
        }
    }
}