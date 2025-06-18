package com.tiji.elements.settings;

public interface SettingFieldType {
    String getAsString();
    Object getFromString(String value);
}
