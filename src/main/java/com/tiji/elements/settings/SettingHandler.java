package com.tiji.elements.settings;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;

public class SettingHandler {
    public enum SettingsField {
        LANGUAGE("key", new Language("en", "us")),
        BRUSH_KEYBIND("brushKeybind", new SettingInt(KeyEvent.VK_F1));

        public final String key;
        public final SettingFieldType defaultValue;

        SettingsField(String key, SettingFieldType defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }
    }

    private HashMap<String, SettingFieldType> settings;
    public void loadSettings() {
        try (InputStream settingsFile = URI.create("./settings.json").toURL().openStream()) {
            if (settingsFile == null) {
                resetSettings();
                return;
            }

            JsonObject parsedSettings;
            try {
                parsedSettings = new Gson().fromJson(Arrays.toString(settingsFile.readAllBytes()), JsonObject.class);
            } catch (IOException e) {
                throw new RuntimeException("Error reading settings file: " + e.getMessage());
            }
            if (parsedSettings.get("schemaVersion").getAsInt() == 1) {
                for (String key : parsedSettings.keySet()) {
//                    this.settings.put(key, parsedSettings.get(key).getAsString());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void resetSettings() {
        this.settings.clear();
        for (SettingsField field : SettingsField.values()) {
            this.settings.put(field.key, field.defaultValue);
        }
    }

    public void saveSettings() {
        Gson gson = new Gson();
        try (java.io.FileWriter writer = new java.io.FileWriter("./settings.json")) {
            HashMap<String, Object> file = new HashMap<>();
            settings.forEach((key, value) -> file.put(key, value.getAsString()));
            gson.toJson(this.settings, writer);
        } catch (IOException e) {
            throw new RuntimeException("Error writing settings file: " + e.getMessage());
        }
    }
}
