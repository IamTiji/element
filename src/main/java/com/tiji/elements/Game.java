package com.tiji.elements;

import com.tiji.elements.core.ElementFactory;
import com.tiji.elements.core.World;
import com.tiji.elements.display.Window;
import com.tiji.elements.elements.*;
import com.tiji.elements.settings.SettingHandler;
import com.tiji.elements.settings.fields.Language;
import com.tiji.elements.settings.TranslationHandler;

public class Game {
    public static TranslationHandler translationHandler = new TranslationHandler();
    public static SettingHandler settingHandler = new SettingHandler();
    public static World world;
    public static Window window;
    private static final ElementFactory initElement = Air::new;
    public static ElementFactory paintElement = Sand::new;
    public static int brushSize = 4;
    public static final ElementFactory[] elementRegistry = {
            Air::new,
            Water::new,
            Sand::new,
            Stone::new,
            Mud::new,
            Fire::new,
            Wood::new,
            Oil::new
    };
    public static int DrawScale;
    public static final boolean isDevelopment = false;
    public static final int WIDTH = 320;
    public static final int HEIGHT = 180;
    public static final int TARGET_MSPT = 1000 / 60;
    public static final int THREAD_COUNT = 8;

    public static void main(String[] args) {
        settingHandler.loadSettings();
        Language language = (Language) settingHandler.getSetting("language");
        translationHandler.loadTranslations(language);
        world = new World(WIDTH, HEIGHT, initElement);
        window = new Window(world::init);
        window.loop();
    }
}
