package com.mentalfrostbyte;

import com.mentalfrostbyte.jello.managers.GuiManager;

public class Client {

    public static int currentVersionIndex = 28;

    public static final String VERSION = "5.0.0b15";
    public static String NAME = "Jello";
    public static String PROD = "Sigma Production";

    private static Client instance;


    private Client() {
    }

    public static Client getInstance() {
        return instance != null ? instance : (instance = new Client());
    }

    private GuiManager guiManager;

    public void start() {
        guiManager = new GuiManager();
    }

}
