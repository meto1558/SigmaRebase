package com.mentalfrostbyte.jello.module.settings.impl;

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
    public JSONObject loadCurrentValueFromJSONObject(JSONObject jsonObject) throws JsonParseException {
        JSONArray array = CJsonUtils.getJSONArrayOrNull(jsonObject, this.getName());
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                JSONObject settingObject = array.getJSONObject(i);
                String settingName = CJsonUtils.getStringOrDefault(settingObject, "name", null);

                for (Setting<?> setting : this.getSubSettings()) {
                    if (setting.getName().equals(settingName)) {
                        setting.loadCurrentValueFromJSONObject(settingObject);
                        break;
                    }
                }
            }
        }

        this.currentValue = CJsonUtils.getBooleanOrDefault(jsonObject, "value", this.getDefaultValue());
        return jsonObject;
    }

    @Override
    public JSONObject buildUpSettingData(JSONObject jsonObject) {
        JSONArray children = new JSONArray();

        for (Setting<?> setting : this.getSubSettings()) {
            children.put(setting.buildUpSettingData(new JSONObject()));
        }

        jsonObject.put("children", children);
        jsonObject.put("name", this.getName());
        return super.buildUpSettingData(jsonObject);
    }

    public List<Setting<?>> getSubSettings() {
        return this.subSettings;
    }
}