package com.mentalfrostbyte.jello.managers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.util.client.ClientMode;
import com.mentalfrostbyte.jello.managers.util.profile.Profile;
import com.mentalfrostbyte.jello.util.client.ModuleSettingInitializr;
import org.apache.commons.io.IOUtils;
import totalcross.json.JSONException2;
import totalcross.json.JSONObject;

public class ProfileManager {
    private final List<Profile> savedConfigs = new ArrayList<>();
    private Profile currentConfigs;

    private static final String configFolder = "/profiles/";
    private static final String configFileExtension = ".profile";

    public void saveConfig(Profile config) {
        try {
            this.savedConfigs.add(0, config);
            File configItself = new File(Client.getInstance().file + configFolder + config.profileName + configFileExtension);
            if (!configItself.exists()) {
                configItself.createNewFile();
            }
            IOUtils.write(config.saveToJson(new JSONObject()).toString(0), Files.newOutputStream(configItself.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void listOnly(Profile config) {
        this.savedConfigs.add(config);
    }

    public void removeConfig(Profile config) {
        this.savedConfigs.remove(config);
    }

    public boolean checkConfig(Profile config) {
        if (Client.getInstance().clientMode == ClientMode.CLASSIC && config.profileName.equals("Classic")) {
            return false;
        } else if (this.savedConfigs.size() <= 1) {
            return false;
        } else {
            this.savedConfigs.remove(config);
            if (config == this.currentConfigs) {
                this.loadConfig(this.savedConfigs.get(0));
            }

            return true;
        }
    }

    public boolean removeConfig(String configName) {
        for (Profile var5 : this.savedConfigs) {
            if (var5.profileName.equals(configName) && this.checkConfig(var5)) {
                return true;
            }
        }

        return false;
    }

    public Profile getConfigByName(String var1) {
        for (Profile var5 : this.savedConfigs) {
            if (var5.profileName.toLowerCase().equals(var1.toLowerCase())) {
                return var5;
            }
        }

        return null;
    }

    public void loadProfile(String name) throws IOException {
        File configFolderFolder = new File(Client.getInstance().file + configFolder);
        if (!configFolderFolder.exists()) {
            configFolderFolder.mkdirs();
        }

        File[] configsFound = configFolderFolder.listFiles((var0, var1x) -> var1x.toLowerCase().endsWith(configFileExtension));

        for (File config : configsFound) {
            try {
                JSONObject object = new JSONObject(IOUtils.toString(Files.newInputStream(config.toPath())));
                Profile profile = new Profile().loadFromJson(object);
                profile.profileName = config.getName().substring(0, config.getName().length() - ".profile".length());
                this.savedConfigs.add(profile);
                if (profile.profileName.equalsIgnoreCase(name)) {
                    this.currentConfigs = profile;
                }
            } catch (JSONException2 var12) {
                System.err.println("Unable to load profile from " + config.getName());
            }
        }

        if (this.savedConfigs.isEmpty() || this.currentConfigs == null) {
            if (name == null || name.isEmpty()) {
                name = "Default";
            }

            this.savedConfigs.add(this.currentConfigs = new Profile(name, new JSONObject()));
        }

        Client.getInstance().moduleManager.load(this.currentConfigs.moduleConfig);
    }

    public boolean getConfigByCaseInsensitiveName(String name) {
        for (Profile config : this.savedConfigs) {
            if (config.profileName.toLowerCase().equals(name.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    public void saveAndReplaceConfigs() throws IOException {
        this.currentConfigs.moduleConfig = Client.getInstance().moduleManager.saveCurrentConfigToJSON(new JSONObject());
        File configFolderFolder = new File(Client.getInstance().file + configFolder);
        if (!configFolderFolder.exists()) {
            configFolderFolder.mkdirs();
        }

        File[] configs = configFolderFolder.listFiles((var0, var1) -> var1.toLowerCase().endsWith(configFileExtension));

        for (File configItself : configs) {
            configItself.delete();
        }

        for (Profile savedConfig : this.savedConfigs) {
            File configItself = new File(Client.getInstance().file + configFolder + savedConfig.profileName + configFileExtension);
            if (!configItself.exists()) {
                configItself.createNewFile();
            }

            IOUtils.write(savedConfig.saveToJson(new JSONObject()).toString(0), Files.newOutputStream(configItself.toPath()));
        }
    }

    public Profile getCurrentConfig() {
        return this.currentConfigs;
    }

    public void loadConfig(Profile config) {
        Client.getInstance().saveClientData();
        ModuleSettingInitializr.field8343 = new HashMap<>();
        if (Client.getInstance().clientMode != ClientMode.CLASSIC) {
            this.currentConfigs.moduleConfig = Client.getInstance().moduleManager.saveCurrentConfigToJSON(new JSONObject());
            this.currentConfigs = config;
            Client.getInstance().getConfig().put("profile", config.profileName);
            Client.getInstance().moduleManager.load(config.moduleConfig);
            Client.getInstance().saveClientData();
        } else {
            this.currentConfigs.moduleConfig = config.getDefaultConfig();
            Client.getInstance().getConfig().put("profile", "Classic");
            Client.getInstance().moduleManager.load(config.moduleConfig);
            Client.getInstance().saveClientData();
        }
    }

    public List<Profile> getAllConfigs() {
        return this.savedConfigs;
    }
}
