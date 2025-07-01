package com.tiji.elements.settings.fields;

public record SettingInt(int value) implements SettingFieldType {

    @Override
    public String getAsString() {
        return String.valueOf(value);
    }

    @Override
    public SettingFieldType getFromString(String value) {
        return new SettingInt(Integer.parseInt(value));
    }
}
