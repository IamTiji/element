package com.tiji.elements.settings;

import com.google.gson.*;
import com.tiji.elements.settings.fields.Language;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class TranslationHandler {
    private final HashMap<String, String> translation = new HashMap<>();

    private static final ArrayList<Language> supportedLanguages = new ArrayList<>();

    private static final Language DUMMY_LANG = new Language("","");
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
    public static Language[] loadSupportedLanguage() {
        if (supportedLanguages.isEmpty()) {
            try (InputStream list = TranslationHandler.class.getResourceAsStream("/lang/translations.json")) {
                if (list == null)
                    throw new NullPointerException("Translation list file not found");

                JsonArray translationList;
                try {
                    String jsonString = new String(list.readAllBytes());
                    translationList = new Gson().fromJson(jsonString, JsonArray.class);
                } catch (IOException | JsonSyntaxException e) {
                    throw new RuntimeException(e);
                }

                for (JsonElement element : translationList) {
                    supportedLanguages.add((Language) DUMMY_LANG.getFromString(element.getAsString()));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return supportedLanguages.toArray(new Language[0]);
    }
}
