package com.mentalfrostbyte.jello.module.settings.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mentalfrostbyte.jello.module.settings.Setting;
import com.mentalfrostbyte.jello.module.settings.SettingType;

import java.util.Arrays;
import java.util.List;

public abstract class SubOptionSetting2 extends Setting<Boolean> {
    public List<Setting<?>> subSettings;

    public SubOptionSetting2(String name, String description, SettingType type, boolean defaultValue, List<Setting<?>> subSettings) {
        super(name, description, type, defaultValue);
        this.subSettings = subSettings;
    }

    public SubOptionSetting2(String name, String description, SettingType type, boolean defaultValue, Setting<?>... subSettings) {
        this(name, description, type, defaultValue, Arrays.asList(subSettings));
    }

    @Override
    public JsonObject loadCurrentValueFromJSONObject(JsonObject jsonObject) throws JsonParseException {
        JsonArray array = jsonObject.getAsJsonArray(this.getName());
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                JsonObject settingObject = array.get(i).getAsJsonObject();
                String settingName = settingObject.get("name").getAsString();

                for (Setting<?> setting : this.getSubSettings()) {
                    if (setting.getName().equals(settingName)) {
                        setting.loadCurrentValueFromJSONObject(settingObject);
                        break;
                    }
                }
            }
        }

        this.currentValue = jsonObject.get("value").getAsBoolean();
        return jsonObject;
    }

    @Override
    public JsonObject buildUpSettingData(JsonObject jsonObject) {
        JsonArray children = new JsonArray();

        for (Setting<?> setting : this.getSubSettings()) {
            children.add(setting.buildUpSettingData(new JsonObject()));
        }

        jsonObject.add("children", children);
        jsonObject.addProperty("name", this.getName());
        return super.buildUpSettingData(jsonObject);
    }

    public List<Setting<?>> getSubSettings() {
        return this.subSettings;
    }
}