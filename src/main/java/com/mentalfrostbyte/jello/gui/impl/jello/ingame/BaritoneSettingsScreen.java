package com.mentalfrostbyte.jello.gui.impl.jello.ingame;

import baritone.api.BaritoneAPI;
import com.mentalfrostbyte.jello.gui.base.animations.Animation;
import com.mentalfrostbyte.jello.gui.base.elements.impl.critical.Screen;
import com.mentalfrostbyte.jello.gui.impl.jello.ingame.buttons.BaritoneSettings;
import com.mentalfrostbyte.jello.util.client.render.ResourceRegistry;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil2;
import com.mentalfrostbyte.jello.util.system.math.smoothing.EasingFunctions;
import net.minecraft.client.Minecraft;

import java.io.File;

public class BaritoneSettingsScreen extends Screen {
    public Animation animation;
    public BaritoneSettings settingsWindow;

    public BaritoneSettingsScreen() {
        super("BaritoneSettingsScreen");
        this.setListening(false);
        this.animation = new Animation(200, 0);
        RenderUtil2.blur();
        int var3 = 48;
        int var4 = 27;
        int var5 = 14;
        int var6 = var3 * var5;
        int var7 = var4 * var5;
        this.addToList(this.settingsWindow = new BaritoneSettings(this, "baritone", (this.widthA - var6) / 2, (this.getHeightA() - var7) / 2 + 30, var3, 27, var5));
    }

    @Override
    public void draw(float partialTicks) {
        partialTicks = this.animation.calcPercent();
        float var4 = EasingFunctions.easeOutBack(partialTicks, 0.0F, 1.0F, 1.0F);
        this.method13279(0.8F + var4 * 0.2F, 0.8F + var4 * 0.2F);
        float var5 = 0.25F * partialTicks;
        RenderUtil.drawRoundedRect(
                (float)this.xA,
                (float)this.yA,
                (float)(this.xA + this.widthA),
                (float)(this.yA + this.heightA),
                RenderUtil2.applyAlpha(ClientColors.DEEP_TEAL.getColor(), var5)
        );
        super.method13224();
        RenderUtil.drawRoundedRect(
                (float)this.settingsWindow.getXA(),
                (float)this.settingsWindow.getYA(),
                (float)this.settingsWindow.getWidthA(),
                (float)this.settingsWindow.getHeightA(),
                40.0F,
                partialTicks
        );
        RenderUtil.drawRoundedRect(
                (float)(this.settingsWindow.getXA() - 20),
                (float)(this.settingsWindow.getYA() - 20),
                (float)(this.settingsWindow.getWidthA() + 40),
                (float)(this.settingsWindow.getHeightA() + 40),
                14.0F,
                ClientColors.LIGHT_GREYISH_BLUE.getColor()
        );
        super.draw(partialTicks);
        int var6 = (this.widthA - this.settingsWindow.getWidthA()) / 2;
        int var7 = (this.heightA - this.settingsWindow.getHeightA()) / 2;
        RenderUtil.drawString(ResourceRegistry.JelloMediumFont40, (float)var6, (float)(var7 - 60), "Baritone Settings", ClientColors.LIGHT_GREYISH_BLUE.getColor());
        String var8 = "Baritone Version: " + getVersionFromJar();
        RenderUtil.drawString(
                ResourceRegistry.JelloLightFont20,
                (float)(var6 + this.settingsWindow.getWidthA() - ResourceRegistry.JelloLightFont20.getWidth(var8)),
                (float)(var7 - 45),
                var8,
                RenderUtil2.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.8F)
        );
    }

    @Override
    public void keyPressed(int keyCode) {
        super.keyPressed(keyCode);
        if (keyCode == 256) {
            RenderUtil2.resetShaders();
            Minecraft.getInstance().displayGuiScreen(null);
        }
    }

    public static String getVersionFromJar() {
        try {
            return BaritoneAPI.class.getProtectionDomain().getCodeSource().getLocation().getPath().endsWith(".jar") ? new File(BaritoneAPI.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName().replaceAll("Baritone-(.*)\\.jar", "$1") : "Development";
        } catch (Exception ignored) {
            return "Unknown";
        }
    }

}
