package com.mentalfrostbyte.jello.managers;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.ClientMode;
import com.mentalfrostbyte.jello.gui.base.Bird;
import com.mentalfrostbyte.jello.gui.base.Screen;
import com.mentalfrostbyte.jello.gui.impl.*;
import com.mentalfrostbyte.jello.gui.unmapped.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import totalcross.json.JSONException;
import totalcross.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiManager {

    public static final Map<Class<? extends net.minecraft.client.gui.screen.Screen>, String> field41338 = new HashMap<Class<? extends net.minecraft.client.gui.screen.Screen>, String>();
    private static final Map<Class<? extends net.minecraft.client.gui.screen.Screen>, Class<? extends Screen>> field41337 = new HashMap<Class<? extends net.minecraft.client.gui.screen.Screen>, Class<? extends Screen>>();

    static {
        field41337.put(MainMenuScreen.class, JelloMainMenuScreen.class);
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
    }

    public static long field41344;
    public static long field41345;
    public static long field41346;
    public static float scaleFactor = 1.0F;
    private static boolean field41351 = true;

    public double field41347;
    public int[] field41354 = new int[2];
    public boolean field41357;
    private final List<Integer> field41339 = new ArrayList<Integer>();
    private final List<Integer> field41340 = new ArrayList<Integer>();
    private final List<Integer> field41341 = new ArrayList<Integer>();
    private final List<Integer> field41342 = new ArrayList<Integer>();
    private final List<Integer> field41343 = new ArrayList<Integer>();
    private boolean field41349 = true;
    private boolean field41350 = true;
    private Screen screen;

    public GuiManager() {
        scaleFactor = (float) (Minecraft.getInstance().getMainWindow().getFramebufferHeight() / Minecraft.getInstance().getMainWindow().getHeight());
    }

    public void method33452() {
        field41337.clear();
        field41337.put(MainMenuScreen.class, ClassicMainScreen.class);
        field41337.put(ClickGui.class, ClassicScreenk.class);
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

    public static Screen handleScreen(net.minecraft.client.gui.screen.Screen var0) {
        if (var0 == null) {
            return null;
        } else if (Client.getInstance().clientMode == ClientMode.PREMIUM) {
            return new SwitchScreen();
        } else if (method33457(var0)) {
            return null;
        } else if (!field41337.containsKey(var0.getClass())) {
            return null;
        } else {
            try {
                return field41337.get(var0.getClass()).newInstance();
            } catch (InstantiationException | IllegalAccessException var4) {
                var4.printStackTrace();
            }

            return null;
        }
    }

    public static boolean method33457(net.minecraft.client.gui.screen.Screen var0) {
        if (var0 instanceof MultiplayerScreen && !(var0 instanceof JelloPortalScreen)) {
            Minecraft.getInstance().currentScreen = null;
            Minecraft.getInstance().displayGuiScreen(new JelloPortalScreen(((MultiplayerScreen) var0).parentScreen));
            return true;
        } else if (var0 instanceof IngameMenuScreen && !(var0 instanceof JelloForSigmaOptions)) {
            Minecraft.getInstance().currentScreen = null;
            Minecraft.getInstance().displayGuiScreen(new JelloForSigmaOptions());
            return true;
        } else if (Client.getInstance().clientMode == ClientMode.NOADDONS && var0 instanceof MainMenuScreen && !(var0 instanceof NoAddOnnScreenMenu)) {
            Minecraft.getInstance().currentScreen = null;
            Minecraft.getInstance().displayGuiScreen(new NoAddOnnScreenMenu());
            return true;
        } else {
            return false;
        }
    }

    public void method33481() throws JSONException {
        this.method33482(handleScreen(Minecraft.getInstance().currentScreen));
    }

    public void method33482(Screen var1) {
        if (this.screen != null) {
            this.method33468(Client.getInstance().getConfig());
        }

        this.screen = var1;
        this.method33476(Client.getInstance().getConfig());
        if (this.screen != null) {
            this.screen.method13028(this.field41354[0], this.field41354[1]);
        }

        if (Client.getInstance().moduleManager != null) {
            Client.getInstance().moduleManager.getMacOSTouchBar().method13734(null);
        }
    }

    public void method33476(JSONObject var1) {
        if (this.screen != null) {
            JSONObject var4 = null;

            try {
                var4 = Client.getInstance().getConfig().getJSONObject(this.screen.method13257());
            } catch (Exception var9) {
                var4 = new JSONObject();
            } finally {
                this.screen.method13161(var4);
            }
        }

        if (var1.has("guiBlur")) {
            this.field41349 = var1.getBoolean("guiBlur");
        }

        if (var1.has("hqIngameBlur")) {
            this.field41350 = var1.getBoolean("hqIngameBlur");
        }
    }

    public JSONObject method33468(JSONObject var1) {
        if (this.screen != null) {
            JSONObject var4 = this.screen.method13160(new JSONObject());
            if (var4.length() != 0) {
                var1.put(this.screen.method13257(), var4);
            }
        }

        var1.put("guiBlur", this.field41349);
        var1.put("hqIngameBlur", this.field41350);
        var1.put("hidpicocoa", field41351);
        return var1;
    }

    public boolean method33470() {
        return this.field41349;
    }

    public void method33469(boolean var1) {
        this.field41349 = var1;
    }

    public boolean method33472() {
        return this.field41350;
    }

    public void method33471(boolean var1) {
        this.field41350 = var1;
    }
}
