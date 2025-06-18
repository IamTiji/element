package com.tiji.elements.settings;

public class SettingInt implements SettingFieldType {
    private final int value;
    public SettingInt(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }

    @Override
    public String getAsString() {
        return String.valueOf(value);
    }

    @Override
    public Object getFromString(String value) {
        return Integer.parseInt(value);
    }
}
