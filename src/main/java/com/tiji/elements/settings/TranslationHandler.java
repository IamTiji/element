package com.tiji.elements.settings;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tiji.elements.settings.fields.Language;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class TranslationHandler {
    private final HashMap<String, String> translation = new HashMap<>();
    public void loadTranslations(Language language) {
        try (InputStream translationFile = getClass().getResourceAsStream("/lang/" + language.getAsId() + ".json")) {
            if (translationFile == null)
                throw new NullPointerException("Translation file not found for key: " + language.getAsId());

            JsonObject parsedTranslation;
            try {
                String jsonString = new String(translationFile.readAllBytes());
                parsedTranslation = new Gson().fromJson(jsonString, JsonObject.class);
            } catch (IOException e) {
                throw new RuntimeException("Error reading translation file: " + e.getMessage());
            }
            if (parsedTranslation.get("schemaVersion").getAsInt() == 1) {
                JsonObject translations = parsedTranslation.getAsJsonObject("translations");
                for (String key : translations.keySet()) {
                    this.translation.put(key, translations.get(key).getAsString());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    public String translate(String key) {
        return translation.getOrDefault(key, key);
    }
}
