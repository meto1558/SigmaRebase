package com.mentalfrostbyte.jello.managers.util.profile;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import totalcross.json.*;

public class Profile {
    public JSONObject moduleConfig;
    public String profileName;

    public Profile() {
    }

    public Profile(String profileName, JSONObject moduleConfig) {
        this.profileName = profileName;
        this.moduleConfig = moduleConfig;
    }

    public Profile(String profileName, Profile profile) {
        this.profileName = profileName;
        this.moduleConfig = profile.moduleConfig;
    }

    public Profile loadFromJson(JSONObject jsonObject) throws JSONException {
        this.moduleConfig = jsonObject.getJSONObject("modConfig");
        this.profileName = jsonObject.getString("name");
        return this;
    }

    public JSONObject saveToJson(JSONObject jsonObject) {
        jsonObject.put("modConfig", this.moduleConfig);
        jsonObject.put("name", this.profileName);
        return jsonObject;
    }

    public JSONObject getDefaultConfig() {
        return null;
    }

    public Profile cloneWithName(String newName) {
        return new Profile(newName, this.moduleConfig);
    }

    public void disableNonGuiModules() throws JSONException {
        JSONArray modulesArray = null;

        try {
            modulesArray = CJsonUtils.getJSONArrayOrNull(this.moduleConfig, "mods");
        } catch (JSONException2 ignored) {
        }

        if (modulesArray != null) {
            for (int i = 0; i < modulesArray.length(); i++) {
                JSONObject moduleObject = modulesArray.getJSONObject(i);
                String moduleName = null;

                try {
                    moduleName = CJsonUtils.getStringOrDefault(moduleObject, "name", null);
                } catch (JSONException2 e) {
                    System.out.println("Invalid name in mod list config");
                }

                for (Module module : Client.getInstance().moduleManager.getModuleMap().values()) {
                    if (module.getName().equals(moduleName) && module.getCategoryBasedOnMode() != ModuleCategory.GUI && module.getCategoryBasedOnMode() != ModuleCategory.RENDER) {
                        moduleObject.put("enabled", "false");
                    }
                }
            }
        }
    }

    public void updateModuleConfig(JSONObject newConfig, Module module) {
        JSONArray modulesArray = null;

        try {
            modulesArray = CJsonUtils.getJSONArrayOrNull(this.moduleConfig, "mods");
        } catch (JSONException2 ignored) {
        }

        boolean updated = false;
        if (modulesArray != null) {
            for (int i = 0; i < modulesArray.length(); i++) {
                try {
                    JSONObject moduleObject = modulesArray.getJSONObject(i);
                    String moduleName = CJsonUtils.getStringOrDefault(moduleObject, "name", null);

                    if (module.getName().equals(moduleName)) {
                        if (module.getCategoryBasedOnMode() != ModuleCategory.GUI && module.getCategoryBasedOnMode() != ModuleCategory.RENDER) {
                            modulesArray.put(i, newConfig);
                        }

                        updated = true;
                    }
                } catch (JSONException e) {
                    System.out.println("Invalid name in mod list config");
                }

            }
        }

        if (!updated) {
            modulesArray.put(newConfig);
        }
    }

    public JSONObject getModuleConfig(Module module) {
        JSONArray modulesArray = null;

        try {
            modulesArray = CJsonUtils.getJSONArrayOrNull(this.moduleConfig, "mods");
        } catch (JSONException2 ignored) {
        }

        if (modulesArray != null) {
            for (int i = 0; i < modulesArray.length(); i++) {
                JSONObject moduleObject;

                try {
                    moduleObject = modulesArray.getJSONObject(i);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                String moduleName = null;

                try {
                    moduleName = CJsonUtils.getStringOrDefault(moduleObject, "name", null);
                } catch (JSONException2 e) {
                    System.out.println("Invalid name in mod list config");
                }

                if (module.getName().equals(moduleName)) {
                    return moduleObject;
                }
            }
        }

        return null;
    }
}
