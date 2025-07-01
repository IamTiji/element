package com.tiji.elements.settings.fields;

public interface SettingFieldType {
    String getAsString();
    SettingFieldType getFromString(String value);
}
