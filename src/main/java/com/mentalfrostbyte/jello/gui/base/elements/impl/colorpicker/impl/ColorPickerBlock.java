package com.mentalfrostbyte.jello.gui.base.elements.impl.colorpicker.impl;

import com.mentalfrostbyte.jello.gui.base.elements.Element;
import com.mentalfrostbyte.jello.gui.base.elements.impl.colorpicker.ColorPicker;
import com.mentalfrostbyte.jello.gui.combined.CustomGuiScreen;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil2;

import java.awt.*;

public class ColorPickerBlock extends Element {
    private static String[] field20602;
    public float field21347;
    private float field21348 = 0.0F;
    private float field21349 = 1.0F;
    public boolean field21350 = false;

    public ColorPickerBlock(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6, float var7, float var8, float var9) {
        super(var1, var2, var3, var4, var5, var6, false);
        this.field21347 = var7;
        this.field21348 = var8;
        this.field21349 = var9;
    }

    public void method13678(float var1) {
        this.field21347 = var1;
    }

    @Override
    public void updatePanelDimensions(int newHeight, int newWidth) {
        if (this.field21350) {
            int var5 = this.getHeightO() - this.method13271();
            this.method13680((float) var5 / (float) this.getWidthA());
            int var6 = this.getWidthO() - this.method13272();
            this.method13683(1.0F - (float) var6 / (float) this.getHeightA());
        }

        super.updatePanelDimensions(newHeight, newWidth);
    }

    @Override
    public void draw(float partialTicks) {
        int var4 = RenderUtil2.applyAlpha(Color.HSBtoRGB(this.field21347, 0.0F, 1.0F), partialTicks);
        int var5 = RenderUtil2.applyAlpha(Color.HSBtoRGB(this.field21347, 1.0F, 1.0F), partialTicks);
        int var6 = RenderUtil2.applyAlpha(ClientColors.DEEP_TEAL.getColor(), partialTicks);
        RenderUtil.startScissor(this);
        RenderUtil.drawQuad(
                this.getXA(), this.getYA(), this.getXA() + this.getWidthA(), this.getYA() + this.getHeightA(), var4, var5, var5, var4
        );
        RenderUtil.drawQuad(
                this.getXA(),
                this.getYA(),
                this.getXA() + this.getWidthA(),
                this.getYA() + this.getHeightA(),
                RenderUtil2.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.0F),
                RenderUtil2.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.0F),
                var6,
                var6
        );
        ColorPicker.method13052(
                this.xA + Math.round((float) this.widthA * this.method13679()),
                this.yA + Math.round((float) this.heightA * (1.0F - this.method13682())),
                Color.HSBtoRGB(this.field21347, this.field21348, this.field21349),
                partialTicks
        );
        RenderUtil.drawBorder(
                (float) this.getXA(),
                (float) this.getYA(),
                (float) (this.getXA() + this.getWidthA()),
                (float) (this.getYA() + this.getHeightA()),
                RenderUtil2.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.25F * partialTicks)
        );
        RenderUtil.endScissor();
        super.draw(partialTicks);
    }

    @Override
    public boolean onClick(int mouseX, int mouseY, int mouseButton) {
        this.field21350 = true;
        return super.onClick(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onClick2(int mouseX, int mouseY, int mouseButton) {
        this.field21350 = false;
    }

    public float method13679() {
        return this.field21348;
    }

    public void method13680(float var1) {
        this.method13681(var1, true);
    }

    public void method13681(float var1, boolean var2) {
        var1 = Math.min(Math.max(var1, 0.0F), 1.0F);
        float var5 = this.field21348;
        this.field21348 = var1;
        if (var2 && var5 != var1) {
            this.callUIHandlers();
        }
    }

    public float method13682() {
        return this.field21349;
    }

    public void method13683(float var1) {
        this.method13684(var1, true);
    }

    public void method13684(float var1, boolean var2) {
        var1 = Math.min(Math.max(var1, 0.0F), 1.0F);
        float var5 = this.field21349;
        this.field21349 = var1;
        if (var2 && var5 != var1) {
            this.callUIHandlers();
        }
    }

    public int method13685() {
        return Color.HSBtoRGB(this.field21347, this.field21348, this.field21349);
    }
}
