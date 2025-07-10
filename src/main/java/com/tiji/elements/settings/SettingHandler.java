package com.tiji.elements.settings;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tiji.elements.settings.fields.Language;
import com.tiji.elements.settings.fields.SettingFieldType;
import com.tiji.elements.settings.fields.SettingKeybind;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingHandler {
    public record SettingsField(String key, SettingFieldType defaultValue) {
    }

    List<SettingsField> settingsFields = Arrays.asList(
            new SettingsField("language", new Language("en", "us")),
            new SettingsField("brushKeybind", new SettingKeybind(GLFW.GLFW_KEY_F1))
    );

    private final HashMap<String, SettingFieldType> settings = new HashMap<>();

    public void loadSettings() {
        try (InputStream settingsFile = new File("settings.json").toURI().toURL().openStream()) {
            if (settingsFile == null) {
                resetSettings();
                saveSettings();
                return;
            }

            String json = new String(settingsFile.readAllBytes());
            JsonObject parsedSettings;
            parsedSettings = new Gson().fromJson(json, JsonObject.class);
            if (parsedSettings.get("schemaVersion").getAsInt() == 1) {
                for (SettingsField field : settingsFields) {
                    if (parsedSettings.has(field.key)) {
                        settings.put(field.key, field.defaultValue.getFromString(parsedSettings.get(field.key).getAsString()));
                    } else {
                        settings.put(field.key, field.defaultValue);
                    }
                }
            }
        } catch (IOException e) {
            resetSettings();
            saveSettings();
        }
    }

    public void resetSettings() {
        this.settings.clear();
        for (SettingsField field : settingsFields) {
            this.settings.put(field.key, field.defaultValue);
        }
    }

    public void saveSettings() {
        Gson gson = new Gson();
        try (java.io.FileWriter writer = new java.io.FileWriter("./settings.json")) {
            HashMap<String, Object> file = new HashMap<>();
            settings.forEach((key, value) -> file.put(key, value.getAsString()));
            file.put("schemaVersion", 1);
            gson.toJson(file, writer);
        } catch (IOException e) {
            throw new RuntimeException("Error writing settings file: " + e.getMessage());
        }
    }

    public SettingFieldType getSetting(String key) {
        return settings.get(key);
    }

    public Map<String, SettingFieldType> getSettings() {
        return settings;
    }

    public void setSetting(String key, SettingFieldType value) {
        if (settings.containsKey(key)) {
            settings.put(key, value);
        } else {
            throw new IllegalArgumentException("Setting not found: " + key);
        }
    }
}
