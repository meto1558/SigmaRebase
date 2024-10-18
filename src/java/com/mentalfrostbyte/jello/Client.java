package com.mentalfrostbyte.jello;

import com.mentalfrostbyte.jello.utilities.interfaces.Texture;
import net.minecraft.client.Minecraft;
import totalcross.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Client {

    public static final String VERSION = "5.0.0b15";
    public static final boolean field28963 = false;
    private static final Minecraft mc = Minecraft.getInstance();
    public static String field28960 = "Jello";
    public static String field28962 = "Sigma Production";
    public static List<Texture> textureList = new ArrayList<Texture>();
    public static boolean dontRenderHand = false;
    private static Client instance;
    private File file = new File("sigma5");
    private JSONObject config;
    private boolean field28968 = true;
    private Logger logger;

    private Client() {
    }

    public static Client getInstance() {
        return instance != null ? instance : (instance = new Client());
    }

    public boolean method19930() {
        return this.field28968;
    }

    public void method19931(boolean var1) {
        this.field28968 = var1;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public File getFile() {
        return this.file;
    }

    public JSONObject getConfig() {
        return this.config;
    }

}
