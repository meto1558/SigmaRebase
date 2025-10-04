package com.mentalfrostbyte.jello.gui.base.elements.impl.image.types;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.gui.base.elements.impl.button.Button;
import com.mentalfrostbyte.jello.gui.combined.CustomGuiScreen;
import com.mentalfrostbyte.jello.util.client.render.Resources;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.client.render.theme.ColorHelper;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.system.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;

public class SmallImage extends Button {
    public static final ColorHelper color = new ColorHelper(ClientColors.LIGHT_GREYISH_BLUE.getColor(), MathHelper.shiftTowardsBlack(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.1F));
    public Texture texture;

    public SmallImage(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6, Texture var7, ColorHelper var8, String var9, TrueTypeFont var10) {
        super(var1, var2, var3, var4, var5, var6, var8, var9, var10);
        this.texture = var7;
    }

    public SmallImage(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6, Texture texture, ColorHelper var8, String var9) {
        super(var1, var2, var3, var4, var5, var6, var8, var9);
        this.texture = texture;
    }

    public SmallImage(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6, Texture var7, ColorHelper var8) {
        super(var1, var2, var3, var4, var5, var6, var8);
        this.texture = var7;
    }

    public SmallImage(CustomGuiScreen screen, String iconName, int x, int y, int width, int height, Texture texture) {
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
                MathHelper.applyAlpha2(
                        MathHelper.shiftTowardsOther(this.textColor.getPrimaryColor(), this.textColor.getSecondaryColor(), 1.0F - var4),
                        (float) (this.textColor.getPrimaryColor() >> 24 & 0xFF) / 255.0F * partialTicks
                )
        );
        if (this.getText() != null) {
            RenderUtil.drawString(
                    this.getFont(),
                    (float) (this.getXA() + this.getWidthA() / 2),
                    (float) (this.getYA() + this.getHeightA() / 2),
                    this.getText(),
                    MathHelper.applyAlpha2(this.textColor.getTextColor(), partialTicks),
                    this.textColor.method19411(),
                    this.textColor.method19413()
            );
        }

        GL11.glPushMatrix();
        super.drawChildren(partialTicks);
        GL11.glPopMatrix();
    }
}
