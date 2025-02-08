package com.mentalfrostbyte.jello.managers;

import com.mentalfrostbyte.jello.Client;
import com.mentalfrostbyte.jello.gui.base.interfaces.OnlineProfileListener;
import com.mentalfrostbyte.jello.managers.util.profile.Profile;
import com.mentalfrostbyte.jello.module.Module;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import totalcross.json.JSONArray;
import totalcross.json.JSONException;
import totalcross.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OnlineProfilesManager {
    public static List<String> cachedOnlineProfiles;

    public OnlineProfilesManager(OnlineProfileListener listener) {
        new Thread(() -> {
            if (cachedOnlineProfiles == null) {
                cachedOnlineProfiles = this.fetchOnlineProfiles();
            }

            listener.onProfilesRetrieved(cachedOnlineProfiles);
        }).start();
    }

    public List<String> fetchOnlineProfiles() {
        try {
            HttpGet request = new HttpGet("http://localhost/profiles.php?v=" + Client.RELEASE_TARGET);
            CloseableHttpResponse response = HttpClients.createDefault().execute(request);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (InputStream inputStream = entity.getContent()) {
                    String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                    JSONArray jsonArray = new JSONArray(content);
                    List<String> profileNames = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        profileNames.add(jsonArray.getString(i));
                    }
                    return profileNames;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private String encode(String input) {
        return URLEncoder.encode(input, StandardCharsets.UTF_8);
    }

    public JSONObject fetchProfileConfig(String profileName) {
        try {
            HttpGet request = new HttpGet("http://localhost/profiles/" + encode(profileName) + ".profile?v=" + Client.RELEASE_TARGET);
            CloseableHttpResponse response = HttpClients.createDefault().execute(request);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (InputStream inputStream = entity.getContent()) {
                    String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                    return new JSONObject(content);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public Profile createProfileFromOnlineConfig(Profile baseProfile, String profileName) {
        Profile newProfile = new Profile(profileName, baseProfile);
        try {
            newProfile.disableNonGuiModules();
            Profile settingsProfile = new Profile("settings", fetchProfileConfig(profileName).getJSONObject("modConfig"));
            for (Module module : Client.getInstance().moduleManager.getModuleMap().values()) {
                JSONObject moduleConfig = settingsProfile.getModuleConfig(module);
                if (moduleConfig != null) {
                    newProfile.updateModuleConfig(moduleConfig, module);
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException("Failed to parse profile configuration", e);
        }
        return newProfile;
    }
}
