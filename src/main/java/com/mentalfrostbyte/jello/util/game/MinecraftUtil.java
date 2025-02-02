package com.mentalfrostbyte.jello.util.game;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;

public class MinecraftUtil {

    private static final Minecraft mc = Minecraft.getInstance();

    /**
     * Adds a chat message to the chat. Not to be confused with com.mentalfrostbyte.jello.util.MultiUtilities#sendChatMessage(java.lang.String)
     * @param text
     */
    public static void addChatMessage(String text) {
        StringTextComponent textComp = new StringTextComponent(text);
        mc.ingameGUI.getChatGUI().printChatMessage(textComp);
    }

}
