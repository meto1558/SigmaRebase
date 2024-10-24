package com.mentalfrostbyte.jello.managers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;

import java.util.HashMap;
import java.util.Map;

public class GuiManager {

    public static final Map<Class<? extends Screen>, String> field41338 = new HashMap<>();
    private static final Map<Class<? extends net.minecraft.client.gui.screen.Screen>, Class<? extends Screen>> field41337 = new HashMap<>();

    public static float scaleFactor = 1.0F;
    public static long field41344;
    public static long field41345;
    public static long field41346;
    private long field41355;

    static {
        //field41337.put(MainMenuScreen.class, JelloMainMenuScreen.class);
        /*
        field41337.put(ClickGui.class, JelloClickGUI.class);
        field41337.put(Class1144.class, JelloKeyboardScreen.class);
        field41337.put(Maps.class, JelloMaps.class);
        field41337.put(Snake.class, SnakeGameScreen.class);
        field41337.put(Bird.class, BirdGameScreen.class);
        field41337.put(SpotLight.class, SearchBar.class);
        field41337.put(Class1309.class, JelloInGameOptions.class);
        field41337.put(Class1133.class, CreditsToCreators.class);
        field41338.put(ClickGui.class, "Click GUI");
        field41338.put(Class1144.class, "Keybind Manager");
        field41338.put(Maps.class, "Jello Maps");
        field41338.put(Snake.class, "Snake");
        field41338.put(Bird.class, "Bird");
        field41338.put(SpotLight.class, "Spotlight");

         */
    }

    public GuiManager() {
        this.field41355 = field41344;
        scaleFactor = (float) (Minecraft.getInstance().getMainWindow().getFramebufferHeight() / Minecraft.getInstance().getMainWindow().getHeight());
    }

    public void method33459(long var1) {
        this.field41355 = var1;
    }

    public void method33452() {
        field41337.clear();
        /*
        field41337.put(MainMenuScreen.class, ClassicMainScreen.class);
        field41337.put(ClickGui.class, ClassicScreenk.class);

         */
    }

    public Class<? extends net.minecraft.client.gui.screen.Screen> method33477(String var1) {
        for (Map.Entry var5 : field41338.entrySet()) {
            if (var1.equals(var5.getValue())) {
                return (Class<? extends net.minecraft.client.gui.screen.Screen>) var5.getKey();
            }
        }

        return null;
    }

    public String method33478(Class<? extends net.minecraft.client.gui.screen.Screen> var1) {
        if (var1 == null) {
            return "";
        } else {
            for (Map.Entry var5 : field41338.entrySet()) {
                if (var1 == var5.getKey()) {
                    return (String) var5.getValue();
                }
            }

            return "";
        }
    }
}
