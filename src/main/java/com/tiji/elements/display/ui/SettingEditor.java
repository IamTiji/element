package com.tiji.elements.display.ui;

import com.tiji.elements.Game;
import com.tiji.elements.core.Position;
import com.tiji.elements.display.ui.widget.*;
import com.tiji.elements.settings.TranslationHandler;
import com.tiji.elements.settings.fields.Language;
import com.tiji.elements.settings.fields.SettingInt;
import com.tiji.elements.settings.fields.SettingKeybind;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SettingEditor extends AbstractUI {
    private static final int UI_WIDTH = 600;
    private static final int UI_HEIGHT = 400;

    private static final int BUTTON_WIDTH = 130;

    public SettingEditor(int width, int height) {
        final int screenXOffset = (width - UI_WIDTH) / 2;
        final int screenYOffset = (height - UI_HEIGHT) / 2;
        addWidget(new Box(new Position(screenXOffset, screenYOffset),
                UI_WIDTH, UI_HEIGHT));

        AtomicInteger y = new AtomicInteger(screenYOffset + 10);

        Game.settingHandler.getSettings().forEach((key, value) -> {
            String translatedKey = Game.translationHandler.translate("setting.key." + key);
            addWidget(new Label(new Position(screenXOffset + 10, y.get()), translatedKey));

            addWidget(new Button(Game.translationHandler.translate("setting.restore_default"),
                    new Position(screenXOffset + UI_WIDTH - BUTTON_WIDTH - 10, y.get()),
                    BUTTON_WIDTH));

            Position position = new Position(screenXOffset + UI_WIDTH - (BUTTON_WIDTH*2) - 10, y.getAndAdd(30));
            Widget setting = switch (value) {
                case SettingKeybind s ->
                        new KeybindButton(position, s.value(), BUTTON_WIDTH);
                case SettingInt s ->
                        new NumberOnlyEntry(position, BUTTON_WIDTH, String.valueOf(s.value()));
                case Language s -> {
                        List<Language> values = Arrays.stream(TranslationHandler.loadSupportedLanguage()).toList();
                        List<String> options = new ArrayList<>(values.size());
                        for (Language l : values) {
                                options.add(Game.translationHandler.translate("language." + l.getAsId()));
                        }
                        yield new OptionDropdown(position, options, BUTTON_WIDTH, Game.translationHandler.translate("language." + s.getAsId()));
                }
                default ->
                        new Label(position, ";(");
            };
            addWidget(setting);
        });
    }
}
