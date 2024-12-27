package com.mentalfrostbyte.jello.gui.impl;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.gui.base.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.unmapped.*;
import com.mentalfrostbyte.jello.util.ClientColors;
import com.mentalfrostbyte.jello.util.ColorHelper;
import com.mentalfrostbyte.jello.util.ResourceRegistry;
import com.mentalfrostbyte.jello.util.render.ColorUtils;
import com.mentalfrostbyte.jello.util.render.RenderUtil;
import net.minecraft.util.text.StringTextComponent;

public class JelloOptionsMainMenu extends CustomGuiScreen {
    public JelloOptionsMainMenu(CustomGuiScreen screen, String name, int x, int y, int var5, int var6) {
        super(screen, name, x, y, var5, var6);
        this.method13300(false);
        ColorHelper var9 = ColorHelper.field27961.method19415();
        var9.method19406(ClientColors.LIGHT_GREYISH_BLUE.getColor());
        UIButton openKeybinds;
        this.addToList(openKeybinds = new UIButton(this, "openKeybinds", var5 / 2 - 300, var6 - 80, 300, 38, var9, "Open Keybind Manager", ResourceRegistry.JelloLightFont24));
        UIButton openGui;
        this.addToList(openGui = new UIButton(this, "openGui", var5 / 2, var6 - 80, 300, 38, var9, "Open Jello's Click GUI", ResourceRegistry.JelloLightFont24));
        UIButton Credits;
        this.addToList(Credits = new UIButton(this, "credits", var5 / 2 - 100, var6 - 280, 200, 38, var9, "Credits", ResourceRegistry.JelloLightFont18));
        openKeybinds.doThis((var0, var1x) -> JelloInGameOptions.method13438(new KeyboardScreen(new StringTextComponent("Keybind Manager"))));
        openGui.doThis((var0, var1x) -> JelloInGameOptions.method13438(new ClickGui(new StringTextComponent("Click GUI"))));
        Credits.doThis((var0, var1x) -> JelloInGameOptions.method13438(new CreditToCreatorsScreen(new StringTextComponent("GuiCredits"))));
        UICheckBox guiBlurCheckBox;
        this.addToList(guiBlurCheckBox = new UICheckBox(this, "guiBlurCheckBox", var5 / 2 - 70, var6 - 220, 25, 25));
        guiBlurCheckBox.method13705(Client.getInstance().guiManager.getGuiBlur(), false);
        guiBlurCheckBox.method13036(var1x -> Client.getInstance().guiManager.setGuiBlur(guiBlurCheckBox.method13703()));
        UICheckBox guiBlurIngameCheckBox;
        this.addToList(guiBlurIngameCheckBox = new UICheckBox(this, "guiBlurIngameCheckBox", var5 / 2 + 130, var6 - 220, 25, 25));
        guiBlurIngameCheckBox.method13705(Client.getInstance().guiManager.getHqIngameBlur(), false);
        guiBlurIngameCheckBox.method13036(var1x -> Client.getInstance().guiManager.setHqIngameBlur(guiBlurIngameCheckBox.method13703()));
    }

    @Override
    public void draw(float var1) {
        this.drawWatermark(this.xA + (this.getWidthA() - 202) / 2, this.yA + 10, var1);
        Client.getInstance();
        RenderUtil.drawString(
                ResourceRegistry.JelloLightFont20,
                (float) (this.xA + (this.getWidthA() - ResourceRegistry.JelloLightFont20.getWidth("You're currently using Sigma " + Client.VERSION)) / 2),
                (float) (this.yA + 70),
                "You're currently using Sigma " + Client.VERSION,
                ColorUtils.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.4F * var1)
        );
        String clickguiBound = "Click GUI is currently bound to: "
                + RenderUtil.getKeyName(Client.getInstance().moduleManager.getMacOSTouchBar().getKeybindFor(ClickGui.class))
                + " Key";
        RenderUtil.drawString(
                ResourceRegistry.JelloLightFont20,
                (float) (this.getXA() + (this.getWidthA() - ResourceRegistry.JelloLightFont20.getWidth(clickguiBound)) / 2),
                (float) (this.getYA() + this.getHeightA() - 180),
                clickguiBound,
                ColorUtils.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.6F * var1)
        );
        RenderUtil.drawString(
                ResourceRegistry.JelloLightFont14,
                (float) (this.getXA() + (this.getWidthA() - ResourceRegistry.JelloLightFont14.getWidth("Configure all your keybinds in the keybind manager!")) / 2),
                (float) (this.getYA() + this.getHeightA() - 150),
                "Configure all your keybinds in the keybind manager!",
                ColorUtils.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.4F * var1)
        );
        RenderUtil.drawString(
                ResourceRegistry.JelloLightFont20,
                (float) (this.getXA() + (this.getWidthA() - ResourceRegistry.JelloLightFont20.getWidth("GUI Blur: ")) / 2 - 114),
                (float) (this.getYA() + this.getHeightA() - 221),
                "GUI Blur: ",
                ColorUtils.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.5F * var1)
        );
        RenderUtil.drawString(
                ResourceRegistry.JelloLightFont20,
                (float) (this.getXA() + (this.getWidthA() - ResourceRegistry.JelloLightFont20.getWidth("GPU Accelerated: ")) / 2 + 52),
                (float) (this.getYA() + this.getHeightA() - 221), "GPU Accelerated: ",
                ColorUtils.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.5F * var1)
        );
        super.draw(var1);
    }

    private void drawWatermark(int var1, int var2, float var3) {
        RenderUtil.drawString(ResourceRegistry.JelloMediumFont40, (float) var1, (float) (var2 + 1), "Jello", ColorUtils.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), var3));
        RenderUtil.drawString(
                ResourceRegistry.JelloLightFont25, (float) (var1 + 95), (float) (var2 + 14), "for Sigma", ColorUtils.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.86F * var3)
        );
    }
}
