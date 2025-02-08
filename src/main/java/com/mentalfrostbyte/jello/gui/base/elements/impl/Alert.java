package com.mentalfrostbyte.jello.gui.base.elements.impl;

import com.mentalfrostbyte.jello.Client;
import com.mentalfrostbyte.jello.gui.base.alerts.AlertComponent;
import com.mentalfrostbyte.jello.gui.base.alerts.ComponentType;
import com.mentalfrostbyte.jello.gui.base.animations.Animation;
import com.mentalfrostbyte.jello.gui.base.elements.Element;
import com.mentalfrostbyte.jello.gui.base.elements.impl.button.Button;
import com.mentalfrostbyte.jello.gui.combined.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.impl.jello.buttons.TextField;
import com.mentalfrostbyte.jello.gui.combined.AnimatedIconPanel;
import com.mentalfrostbyte.jello.util.client.render.ResourceRegistry;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.client.render.theme.ColorHelper;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil2;
import com.mentalfrostbyte.jello.util.system.math.smoothing.QuadraticEasing;
import com.mentalfrostbyte.jello.util.system.network.ImageUtil;
import net.minecraft.client.Minecraft;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.BufferedImageUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Alert extends Element {
    public CustomGuiScreen field21279;
    public String alertName;
    public Texture field21281;
    private Animation field21282 = new Animation(285, 100);
    public boolean field21283;
    public int field21284 = 240;
    public int field21285 = 0;
    private Map<String, String> inputMap;
    private final List<Class9448> field21287 = new ArrayList<>();

    public Alert(CustomGuiScreen screen, String iconName, boolean var3, String name, AlertComponent... var5) {
        super(screen, iconName, 0, 0, Minecraft.getInstance().getMainWindow().getWidth(), Minecraft.getInstance().getMainWindow().getHeight(), false);
        this.field21283 = var3;
        this.alertName = name;
        this.method13296(false);
        this.method13292(false);
        this.method13243();
        TextField var8 = null;
        TextField var9 = null;

        for (AlertComponent var13 : var5) {
            this.field21285 = this.field21285 + var13.field44773 + 10;
        }

        this.field21285 -= 10;
        this.addToList(
                this.field21279 = new CustomGuiScreen(
                        this, "modalContent", (this.widthA - this.field21284) / 2, (this.heightA - this.field21285) / 2, this.field21284, this.field21285
                )
        );
        int var17 = 0;
        int var18 = 0;

        for (AlertComponent var15 : var5) {
            var17++;
            if (var15.componentType != ComponentType.FIRST_LINE) {
                if (var15.componentType != ComponentType.SECOND_LINE) {
                    if (var15.componentType != ComponentType.BUTTON) {
                        if (var15.componentType == ComponentType.HEADER) {
                            this.field21279
                                    .addToList(
                                            new Text(
                                                    this.field21279,
                                                    "Item" + var17,
                                                    0,
                                                    var18,
                                                    this.field21284,
                                                    var15.field44773,
                                                    new ColorHelper(
                                                            ClientColors.DEEP_TEAL.getColor(),
                                                            ClientColors.DEEP_TEAL.getColor(),
                                                            ClientColors.DEEP_TEAL.getColor(),
                                                            ClientColors.DEEP_TEAL.getColor()
                                                    ),
                                                    var15.field44772,
                                                    ResourceRegistry.JelloLightFont36
                                            )
                                    );
                        }
                    } else {
                        Button var16;
                        this.field21279
                                .addToList(
                                        var16 = new Button(
                                                this.field21279, "Item" + var17, 0, var18, this.field21284, var15.field44773, ColorHelper.field27961, var15.field44772
                                        )
                                );
                        var16.field20586 = 4;
                        var16.doThis((var1x, var2x) -> this.method13601());
                    }
                } else {
                    TextField var22;
                    this.field21279
                            .addToList(
                                    var22 = new TextField(
                                            this.field21279, "Item" + var17, 0, var18, this.field21284, var15.field44773, TextField.field20741, "", var15.field44772
                                    )
                            );
                    if (!var15.field44772.contains("Password")) {
                        if (var15.field44772.contains("Email")) {
                            var8 = var22;
                        }
                    } else {
                        var9 = var22;
                        var22.method13155(true);
                    }
                }
            } else {
                this.field21279
                        .addToList(
                                new Text(
                                        this.field21279,
                                        "Item" + var17,
                                        0,
                                        var18,
                                        this.field21284,
                                        var15.field44773,
                                        new ColorHelper(
                                                ClientColors.MID_GREY.getColor(), ClientColors.MID_GREY.getColor(), ClientColors.MID_GREY.getColor(), ClientColors.MID_GREY.getColor()
                                        ),
                                        var15.field44772,
                                        ResourceRegistry.JelloLightFont20
                                )
                        );
            }

            var18 += var15.field44773 + 10;
        }

        if (var8 != null && var9 != null) {
            TextField var20 = var9;
            var8.method13151(var2x -> {
                String var5x = var2x.getTypedText();
                if (var5x != null && var5x.contains(":")) {
                    String[] var6 = var5x.split(":");
                    if (var6.length <= 2) {
                        if (var6.length > 0) {
                            var2x.setTypedText(var6[0].replace("\n", ""));
                            if (var6.length == 2) {
                                var20.setTypedText(var6[1].replace("\n", ""));
                            }
                        }
                    } else {
                        this.method13601();
                    }
                }
            });
        }
    }

    @Override
    public void method13296(boolean var1) {
        if (var1) {
            for (CustomGuiScreen var5 : this.field21279.getChildren()) {
                if (var5 instanceof TextField) {
                    ((TextField) var5).setTypedText("");
                    ((TextField) var5).method13146();
                }
            }
        }

        this.field21282.changeDirection(!var1 ? Animation.Direction.BACKWARDS : Animation.Direction.FORWARDS);
        super.method13296(var1);
    }

    public CustomGuiScreen method13598() {
        return this.field21279;
    }

    private Map<String, String> method13599() {
        HashMap var3 = new HashMap();

        for (CustomGuiScreen var5 : this.field21279.getChildren()) {
            AnimatedIconPanel var6 = (AnimatedIconPanel) var5;
            if (var6 instanceof TextField) {
                TextField var7 = (TextField) var6;
                var3.put(var7.method13153(), var7.getTypedText());
            }
        }

        return var3;
    }

    public Map<String, String> getInputMap() {
        return this.inputMap;
    }

    public void method13601() {
        this.inputMap = this.method13599();
        this.method13603(false);
        this.callUIHandlers();
    }

    @Override
    public void onClick3(int mouseX, int mouseY, int mouseButton) {
        super.onClick3(mouseX, mouseY, mouseButton);
    }

    public float method13602(float var1, float var2) {
        return this.field21282.getDirection() != Animation.Direction.BACKWARDS
                ? (float) (Math.pow(2.0, (double) (-10.0F * var1)) * Math.sin((double) (var1 - var2 / 4.0F) * (Math.PI * 2) / (double) var2) + 1.0)
                : 0.5F + QuadraticEasing.easeOutQuad(var1, 0.0F, 1.0F, 1.0F) * 0.5F;
    }

    @Override
    public void draw(float partialTicks) {
        if (this.field21282.calcPercent() != 0.0F) {
            int var4 = this.field21284 + 60;
            int var5 = this.field21285 + 60;
            float var7 = !this.isHovered() ? this.field21282.calcPercent() : Math.min(this.field21282.calcPercent() / 0.25F, 1.0F);
            float var8 = this.method13602(this.field21282.calcPercent(), 1.0F);
            var4 = (int) ((float) var4 * var8);
            var5 = (int) ((float) var5 * var8);
            RenderUtil.drawTexture(
                    -5.0F,
                    -5.0F,
                    (float) (this.getWidthA() + 10),
                    (float) (this.getHeightA() + 10),
                    this.field21281,
                    RenderUtil2.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), var7)
            );
            RenderUtil.drawRoundedRect(
                    0.0F, 0.0F, (float) this.getWidthA(), (float) this.getHeightA(), RenderUtil2.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.1F * var7)
            );
            if (var4 > 0) {
                RenderUtil.method11465(
                        (this.widthA - var4) / 2, (this.heightA - var5) / 2, var4, var5, RenderUtil2.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), var7)
                );
            }

            super.method13279(var8, var8);
            super.method13224();
            super.draw(var7);
        } else {
            if (this.method13297()) {
                this.method13145(false);
                this.setEnabled(false);
                this.method13243();
            }
        }
    }

    @Override
    public boolean onClick(int mouseX, int mouseY, int mouseButton) {
        if (!super.onClick(mouseX, mouseY, mouseButton)) {
            int var6 = this.field21284 + 60;
            int var7 = this.field21285 + 60;
            if (mouseX > (this.widthA - var6) / 2
                    && mouseX < (this.widthA - var6) / 2 + var6
                    && mouseY > (this.heightA - var7) / 2
                    && mouseY < (this.heightA - var7) / 2 + var7) {
                return false;
            } else {
                this.method13603(false);
                return false;
            }
        } else {
            return true;
        }
    }

    public void method13603(boolean var1) {
        if (var1 && !this.isHovered()) {
            try {
                if (this.field21281 != null) {
                    this.field21281.release();
                }

                this.field21281 = BufferedImageUtil.getTexture(
                        "blur", ImageUtil.method35036(0, 0, this.getWidthA(), this.getHeightA(), 5, 10, ClientColors.LIGHT_GREYISH_BLUE.getColor(), true)
                );
            } catch (IOException var5) {
                Client.getInstance().getLogger().error(var5.getMessage());
            }
        }

        if (this.isHovered() != var1 && !var1) {
            this.method13605();
        }

        this.method13296(var1);
        if (var1) {
            this.setEnabled(true);
        }

        this.method13292(var1);
    }

    @Override
    public void finalize() throws Throwable {
        try {
            if (this.field21281 != null) {
                Client.getInstance().addTexture(this.field21281);
            }
        } finally {
            super.finalize();
        }
    }

    public final void method13604(Class9448 var1) {
        this.field21287.add(var1);
    }

    public final void method13605() {
        for (Class9448 var4 : this.field21287) {
            var4.method36327(this);
        }
    }

    public static interface Class9448 {
       void method36327(Element var1);
    }
}
