package com.tiji.elements.settings.fields;

// Same as `SettingInt`
public record SettingKeybind(int value) implements SettingFieldType {
    @Override
    public String getAsString() {
        return String.valueOf(value);
    }

    @Override
    public SettingFieldType getFromString(String value) {
        return new SettingKeybind(Integer.parseInt(value));
    }
}
