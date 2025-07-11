package com.tiji.elements.settings.fields;

public record Language(String languageCode, String countryCode) implements SettingFieldType {
    public String toString() {
        return languageCode + "-" + countryCode;
    }

    public String getAsId() {
        return languageCode + "_" + countryCode;
    }

    @Override
    public String getAsString() {
        return toString();
    }

    @Override
    public SettingFieldType getFromString(String value) {
        String[] item = value.split("-");
        return new Language(item[0], item[1]);
    }
}
