package com.mentalfrostbyte.jello.module.impl.gui.jello;

import com.mentalfrostbyte.jello.event.impl.game.render.EventRender2DOffset;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import com.mentalfrostbyte.jello.module.settings.impl.BooleanSetting;
import com.mentalfrostbyte.jello.module.settings.impl.ModeSetting;
import com.mentalfrostbyte.jello.util.client.ClientColors;
import com.mentalfrostbyte.jello.util.client.render.ResourceRegistry;
import com.mentalfrostbyte.jello.util.game.player.MovementUtil2;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.item.AirItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import team.sdhq.eventBus.annotations.EventTarget;

public class InfoHUD extends Module {
    public float previousYaw = 0.0F;

    public InfoHUD() {
        super(ModuleCategory.GUI, "Info HUD", "Shows a bunch of usefull stuff");
        this.registerSetting(new ModeSetting("Cords", "Coordinate display type", 1, "None", "Normal", "Precise"));
        this.registerSetting(new BooleanSetting("Show Player", "Renders a miniature version of your character", true));
        this.registerSetting(new BooleanSetting("Show Armor", "Shows your armor's status", true));
        this.setAvailableOnClassic(false);
    }

    @EventTarget
    public void onRender2D(EventRender2DOffset event) {
        if (this.isEnabled() && mc.player != null) {
            if (!Minecraft.getInstance().gameSettings.hideGUI) {
                if (!(mc.currentScreen instanceof ChatScreen)) {
                    float yawDifference = mc.player.rotationYaw % 360.0F - this.previousYaw % 360.0F;
                    this.previousYaw += yawDifference / (float) Minecraft.getFps() * 1.5F;
                    int yOffset = 14;

                    if (this.getBooleanValueFromSettingName("Show Player")) {
                        yOffset += this.renderPlayerModel(0, mc.mainWindow.getHeight() - 23, 114);
                    }

                    if (this.getBooleanValueFromSettingName("Show Armor")) {
                        yOffset += this.renderArmorStatus(yOffset, mc.mainWindow.getHeight() - 14) + 10;
                    }

                    if (!this.getStringSettingValueByName("Cords").equals("None")) {
                        yOffset += this.renderCoordinates(yOffset, 42) + 10;
                    }
                }
            }
        }
    }

    public String getFormattedCoordinates(boolean precise) {
        return !precise
                ? Math.round(mc.player.getPosX()) + " " +
                Math.round(mc.player.getPosY()) + " " +
                Math.round(mc.player.getPosZ())
                : (float) Math.round(mc.player.getPosX() * 10.0) / 10.0F + " " +
                (float) Math.round(mc.player.getPosY() * 10.0) / 10.0F + " " +
                (float) Math.round(mc.player.getPosZ() * 10.0) / 10.0F;
    }

    public int renderCoordinates(int x, int yOffset) {
        String direction = "Facing South";
        String coordinates = this.getFormattedCoordinates(this.getStringSettingValueByName("Cords").equals("Precise"));
        RenderUtil.drawString(
                ResourceRegistry.JelloMediumFont20,
                (float) x,
                (float) (mc.mainWindow.getHeight() - yOffset),
                coordinates,
                MovementUtil2.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.8F));
        return Math.max(ResourceRegistry.JelloLightFont20.getWidth(direction),
                ResourceRegistry.JelloMediumFont20.getWidth(coordinates));
    }

    public int renderArmorStatus(int x, int y) {
        int armorCount = 0;

        for (int i = 0; i < mc.player.inventory.armorInventory.size(); i++) {
            ItemStack armorPiece = mc.player.inventory.armorInventory.get(i);
            if (!(armorPiece.getItem() instanceof AirItem)) {
                armorCount++;
                int armorY = y - 32 * armorCount;
                RenderUtil.renderItem(armorPiece, x, armorY, 32, 32);
                GL11.glDisable(GL11.GL_LIGHTING);
                float durability = 1.0F - (float) armorPiece.getDamage() / (float) armorPiece.getMaxDamage();
                if (durability != 1.0F) {
                    RenderUtil.renderBackgroundBox((float) (x + 2), (float) (armorY + 28), 28.0F, 5.0F,
                            MovementUtil2.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.5F));
                    RenderUtil.renderBackgroundBox(
                            (float) (x + 2),
                            (float) (armorY + 28),
                            28.0F * durability,
                            3.0F,
                            MovementUtil2.applyAlpha(durability > 0.2 ? ClientColors.DARK_SLATE_GREY.getColor()
                                    : ClientColors.PALE_YELLOW.getColor(), 0.9F));
                }
            }
        }
        return armorCount != 0 ? 32 : -7;
    }

    public int renderPlayerModel(int x, int y, int height) {
        return height - 24;
    }
}
