package com.mentalfrostbyte;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import com.mentalfrostbyte.jello.managers.*;
import com.mentalfrostbyte.jello.utils.ClientLogger;
import com.mentalfrostbyte.jello.utils.FileUtil;
import com.mentalfrostbyte.jello.utils.render.Texture;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import totalcross.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Client {

    private static final Minecraft mc = Minecraft.getInstance();
    public static int currentVersionIndex = 28;

    public static final String VERSION = "5.0.0b15";
    public static String NAME = "Jello";
    public static String PROD = "Sigma Production";

    public File file = new File("sigma5");

    public static List<Texture> textureList = new ArrayList<Texture>();

    private static Client instance;
    public ClientMode clientMode = ClientMode.PREMIUM;
    public DiscordRichPresence discordRichPresence;

    private JSONObject config;

    public GuiManager guiManager;
    public ModuleManager moduleManager;
    public NetworkManager networkManager;
    public CombatManager combatManager;
    public SoundManager soundManager;
    public AccountManager accountManager;
    private Logger logger;

    public void start() {
        this.logger = new ClientLogger(System.out, System.out, System.err);
        this.logger.info("Initializing...");

        this.soundManager = new SoundManager();
        this.soundManager.init();
        guiManager = new GuiManager();
        this.accountManager = new AccountManager();
        this.accountManager.registerEvents();
        GLFW.glfwSetWindowTitle(mc.getMainWindow().getHandle(), "Sigma 5.0");
        this.logger.info("Initialized.");
    }

    public void method19927(Texture var1) {
        textureList.add(var1);
    }

    public DiscordRichPresence getDRPC() {
        return this.discordRichPresence;
    }

    private Client() {
    }

    public static Client getInstance() {
        return instance != null ? instance : (instance = new Client());
    }

    private void initRPC() {
        DiscordRPC var3 = DiscordRPC.INSTANCE;
        String var4 = "693493612754763907";
        String var5 = "";
        DiscordEventHandlers var6 = new DiscordEventHandlers();
        var6.ready = var0 -> System.out.println("Ready!");
        var3.Discord_Initialize(var4, var6, true, var5);
        discordRichPresence = new DiscordRichPresence();
        discordRichPresence.startTimestamp = System.currentTimeMillis() / 1000L;
        discordRichPresence.state = "Playing Minecraft";
        discordRichPresence.details = "Jello for Sigma";
        discordRichPresence.largeImageKey = "jello";
        var3.Discord_UpdatePresence(discordRichPresence);
    }

    public void setupClient(ClientMode mode) {
        this.clientMode = mode;
        if (mode != ClientMode.CLASSIC) {
            if (mode == ClientMode.JELLO) {
                this.initRPC();
                GLFW.glfwSetWindowTitle(mc.getMainWindow().getHandle(), "Jello for Sigma 5.0");
            }
        } else {
            getInstance().guiManager.method33452();
            GLFW.glfwSetWindowTitle(mc.getMainWindow().getHandle(), "Classic Sigma 5.0");
        }
    }

    public void saveClientData() {
        try {
            FileUtil.save(this.config, new File(this.file + "/config.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getConfig() {
        return this.config;
    }

    public Logger getLogger() {
        return this.logger;
    }
}
