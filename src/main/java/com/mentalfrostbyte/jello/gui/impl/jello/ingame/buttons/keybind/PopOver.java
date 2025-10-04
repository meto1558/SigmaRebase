package com.mentalfrostbyte.jello.gui.impl.jello.ingame.buttons.keybind;

import com.mentalfrostbyte.jello.gui.base.animations.Animation;
import com.mentalfrostbyte.jello.gui.base.elements.Element;
import com.mentalfrostbyte.jello.gui.base.elements.impl.button.types.TextButton;
import com.mentalfrostbyte.jello.gui.combined.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.impl.jello.ingame.KeyboardScreen;
import com.mentalfrostbyte.jello.util.client.render.ResourceRegistry;
import com.mentalfrostbyte.jello.util.client.render.Resources;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.client.render.theme.ColorHelper;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.system.math.MathHelper;
import com.mentalfrostbyte.jello.util.system.math.smoothing.EasingFunctions;
import com.mentalfrostbyte.jello.util.system.math.smoothing.QuadraticEasing;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class PopOver extends Element {
    private final int field21376;
    private final Animation field21377;
    private boolean field21378 = false;
    private final List<Class6601> field21379 = new ArrayList<Class6601>();

    public PopOver(CustomGuiScreen var1, String var2, int var3, int var4, int var5, String var6) {
        super(var1, var2, var3 - 125, var4, 250, 330, ColorHelper.field27961, var6, false);
        if (this.yA + this.heightA <= Minecraft.getInstance().getMainWindow().getHeight()) {
            this.yA += 10;
        } else {
            this.yA -= 400;
            this.field21378 = true;
        }

        this.field21376 = var5;
        this.field21377 = new Animation(250, 0);
        this.setReAddChildren(true);
        this.setListening(false);
        this.method13712();
        TextButton var9;
        this.addToList(
                var9 = new TextButton(
                        this,
                        "addButton",
                        this.widthA - 70,
                        this.heightA - 70,
                        ResourceRegistry.JelloLightFont25.getWidth("Add"),
                        70,
                        ColorHelper.field27961,
                        "Add",
                        ResourceRegistry.JelloLightFont25
                )
        );
        var9.onClick((var1x, var2x) -> this.method13714());
    }

    public void method13712() {
        int var3 = 1;
        ArrayList var4 = new ArrayList();

        for (CustomGuiScreen var6 : this.getChildren()) {
            if (var6.getHeightA() != 0) {
                var4.add(var6.getName());
            }
        }

        this.method13242();
        this.setFocused(true);
        this.clearChildren();

        for (Class6984 var10 : KeyboardScreen.method13328()) {
            int var7 = var10.method21599();
            if (var7 == this.field21376) {
                Class4253 var8;
                this.addToList(var8 = new Class4253(this, var10.method21596(), 0, 20 + 55 * var3, this.widthA, 55, var10, var3++));
                var8.onPress(var2 -> {
                    var10.method21598(0);
                    this.callUIHandlers();
                });
                if (var4.size() > 0 && !var4.contains(var10.method21596())) {
                    var8.method13056();
                }
            }
        }
    }

    @Override
    public void updatePanelDimensions(int newHeight, int newWidth) {
        Map<Integer, Class4253> var5 = new HashMap();

        for (CustomGuiScreen var7 : this.getChildren()) {
            if (var7 instanceof Class4253) {
                var5.put(((Class4253) var7).field20626, (Class4253) var7);
            }
        }

        int var9 = 75;

        for (Entry<Integer, Class4253> var11 : var5.entrySet()) {
            var11.getValue().setYA(var9);
            var9 += var11.getValue().getHeightA();
        }

        super.updatePanelDimensions(newHeight, newWidth);
    }

    @Override
    public void draw(float partialTicks) {
        partialTicks = this.field21377.calcPercent();
        float var4 = EasingFunctions.easeOutBack(partialTicks, 0.0F, 1.0F, 1.0F);
        this.method13279(0.8F + var4 * 0.2F, 0.8F + var4 * 0.2F);
        this.method13284((int) ((float) this.widthA * 0.2F * (1.0F - var4)) * (!this.field21378 ? 1 : -1));
        super.method13224();
        int var6 = MathHelper.applyAlpha2(-723724, QuadraticEasing.easeOutQuad(partialTicks, 0.0F, 1.0F, 1.0F));
        RenderUtil.drawRoundedRect(
                (float) (this.xA + 10 / 2),
                (float) (this.yA + 10 / 2),
                (float) (this.widthA - 10),
                (float) (this.heightA - 10),
                35.0F,
                partialTicks
        );
        RenderUtil.drawColoredRect(
                (float) (this.xA + 10 / 2),
                (float) (this.yA + 10 / 2),
                (float) (this.xA - 10 / 2 + this.widthA),
                (float) (this.yA - 10 / 2 + this.heightA),
                MathHelper.applyAlpha2(ClientColors.DEEP_TEAL.getColor(), partialTicks * 0.25F)
        );
        RenderUtil.drawRoundedRect((float) this.xA, (float) this.yA, (float) this.widthA, (float) this.heightA, (float) 10, var6);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) this.xA, (float) this.yA, 0.0F);
        GL11.glRotatef(!this.field21378 ? -90.0F : 90.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef((float) (-this.xA), (float) (-this.yA), 0.0F);
        RenderUtil.drawImage(
                (float) (this.xA + (!this.field21378 ? 0 : this.heightA)),
                (float) this.yA + (float) ((this.widthA - 47) / 2) * (!this.field21378 ? 1.0F : -1.5F),
                18.0F,
                47.0F,
                Resources.selectPNG,
                var6
        );
        GL11.glPopMatrix();
        RenderUtil.drawString(
                ResourceRegistry.JelloLightFont25,
                (float) (this.xA + 25),
                (float) (this.yA + 20),
                this.text + " Key",
                MathHelper.applyAlpha2(ClientColors.DEEP_TEAL.getColor(), 0.8F * partialTicks)
        );
        RenderUtil.drawColoredRect(
                (float) (this.xA + 25),
                (float) (this.yA + 68),
                (float) (this.xA + this.widthA - 25),
                (float) (this.yA + 69),
                MathHelper.applyAlpha2(ClientColors.DEEP_TEAL.getColor(), 0.05F * partialTicks)
        );
        super.draw(partialTicks);
    }

    public final void method13713(Class6601 var1) {
        this.field21379.add(var1);
    }

    public final void method13714() {
        for (Class6601 var4 : this.field21379) {
            var4.method20001(this);
        }
    }

    public interface Class6601 {
        void method20001(PopOver var1);
    }
}
