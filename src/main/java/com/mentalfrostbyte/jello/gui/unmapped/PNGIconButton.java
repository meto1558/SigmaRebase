package com.mentalfrostbyte.jello.gui.unmapped;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.gui.base.CustomGuiScreen;
import com.mentalfrostbyte.jello.util.client.ClientColors;
import com.mentalfrostbyte.jello.util.client.ColorHelper;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil2;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.client.render.Resources;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;

public class PNGIconButton extends Button {
    public static final ColorHelper color = new ColorHelper(ClientColors.LIGHT_GREYISH_BLUE.getColor(), RenderUtil2.shiftTowardsBlack(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.1F));
    public Texture texture;

    public PNGIconButton(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6, Texture var7, ColorHelper var8, String var9, TrueTypeFont var10) {
        super(var1, var2, var3, var4, var5, var6, var8, var9, var10);
        this.texture = var7;
    }

    public PNGIconButton(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6, Texture texture, ColorHelper var8, String var9) {
        super(var1, var2, var3, var4, var5, var6, var8, var9);
        this.texture = texture;
    }

    public PNGIconButton(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6, Texture var7, ColorHelper var8) {
        super(var1, var2, var3, var4, var5, var6, var8);
        this.texture = var7;
    }

    public PNGIconButton(CustomGuiScreen screen, String iconName, int x, int y, int width, int height, Texture texture) {
        super(screen, iconName, x, y, width, height, color);
        this.texture = texture;
    }

    public Texture getTexture() {
        return this.texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    @Override
    public void draw(float partialTicks) {
        if (this.texture.equals(Resources.optionsPNG1) && Client.getInstance().notificationManager.isRenderingNotification()) {
            return;
        }

        float var4 = !this.isHovered() ? 0.3F : (!this.method13216() ? (!this.method13212() ? Math.max(partialTicks * this.field20584, 0.0F) : 1.5F) : 0.0F);
        RenderUtil.drawImage(
                (float) this.getXA(),
                (float) this.getYA(),
                (float) this.getWidthA(),
                (float) this.getHeightA(),
                this.getTexture(),
                RenderUtil2.applyAlpha(
                        RenderUtil2.shiftTowardsOther(this.textColor.method19405(), this.textColor.method19403(), 1.0F - var4),
                        (float) (this.textColor.method19405() >> 24 & 0xFF) / 255.0F * partialTicks
                )
        );
        if (this.getTypedText() != null) {
            RenderUtil.drawString(
                    this.getFont(),
                    (float) (this.getXA() + this.getWidthA() / 2),
                    (float) (this.getYA() + this.getHeightA() / 2),
                    this.getTypedText(),
                    RenderUtil2.applyAlpha(this.textColor.getTextColor(), partialTicks),
                    this.textColor.method19411(),
                    this.textColor.method19413()
            );
        }

        GL11.glPushMatrix();
        super.method13226(partialTicks);
        GL11.glPopMatrix();
    }
}
