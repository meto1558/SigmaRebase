package com.mentalfrostbyte.jello.gui.base.elements.impl.altmanager;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.gui.base.animations.Animation;
import com.mentalfrostbyte.jello.gui.combined.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.combined.AnimatedIconPanel;
import com.mentalfrostbyte.jello.util.client.render.ResourceRegistry;
import com.mentalfrostbyte.jello.util.client.render.Resources;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.system.math.MathHelper;
import com.mentalfrostbyte.jello.util.system.math.smoothing.EasingFunctions;
import com.mentalfrostbyte.jello.util.system.math.smoothing.QuadraticEasing;
import com.mentalfrostbyte.jello.util.game.render.ImageUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.codec.binary.Base64;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.BufferedImageUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

public class Ban extends AnimatedIconPanel {
    public com.mentalfrostbyte.jello.managers.util.account.microsoft.Ban field21243 = null;
    public ServerData field21244 = null;
    public Texture field21245 = null;
    public Texture field21246 = null;
    private BufferedImage field21247;
    private final Animation field21248;

    public Ban(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6, com.mentalfrostbyte.jello.managers.util.account.microsoft.Ban var7) {
        super(var1, var2, var3, var4, var5, var6, false);
        this.field21243 = var7;
        this.field21244 = var7.getServer();
        this.field21248 = new Animation(200, 200, Animation.Direction.BACKWARDS);
    }

    @Override
	protected void finalize() throws Throwable {
        try {
            if (this.field21246 != null) {
                Client.getInstance().addTexture(this.field21246);
            }

            if (this.field21245 != null) {
                Client.getInstance().addTexture(this.field21245);
            }
        } finally {
            super.finalize();
        }
    }

    @Override
    public void draw(float partialTicks) {
        this.method13225();
        float var4 = EasingFunctions.easeOutBack(this.field21248.calcPercent(), 0.0F, 1.0F, 1.0F);
        float var5 = QuadraticEasing.easeInQuad(this.field21248.calcPercent(), 0.0F, 1.0F, 1.0F);
        if (this.method13298()) {
            this.field21248.changeDirection(Animation.Direction.FORWARDS);
        } else if ((double) Math.abs(var4 - var5) < 0.7) {
            this.field21248.changeDirection(Animation.Direction.BACKWARDS);
        }

        if (this.method13272() + this.method13282() < Minecraft.getInstance().getMainWindow().getHeight() - 36 && this.method13272() + this.method13282() > 52) {
            if (this.field21244 != null && this.field21246 == null) {
                try {
                    BufferedImage var6 = method13578(this.field21244.getBase64EncodedIconData());
                    if (var6 != null) {
                        this.field21245 = BufferedImageUtil.getTexture("servericon", var6);
                        this.field21246 = BufferedImageUtil.getTexture(
                                "servericon", ImageUtil.applyBlur(ImageUtil.adjustImageHSB(method13579(var6, 2.5, 2.5), 0.0F, 1.1F, 0.0F), 25)
                        );
                    }
                } catch (IOException var8) {
                    var8.printStackTrace();
                }
            }

            RenderUtil.startScissor(this);
            RenderUtil.drawColoredRect(
                    (float) this.xA,
                    (float) this.yA,
                    (float) (this.xA + this.widthA),
                    (float) (this.yA + this.heightA),
                    ClientColors.LIGHT_GREYISH_BLUE.getColor()
            );
            GL11.glTexParameteri(3553, 10241, 9728);
            GL11.glPushMatrix();
            int var9 = this.widthA / 2;
            int var7 = this.heightA / 2;
            if (this.field21248.getDirection() == Animation.Direction.BACKWARDS) {
                var4 = QuadraticEasing.easeInQuad(this.field21248.calcPercent(), 0.0F, 1.0F, 1.0F);
            }

            GL11.glTranslatef((float) (this.getXA() + var9), (float) (this.getYA() + var7), 0.0F);
            GL11.glScaled(1.0 + 0.4 * (double) var4, 1.0 + 0.4 * (double) var4, 0.0);
            GL11.glTranslatef((float) (-this.getXA() - var9), (float) (-this.getYA() - var7), 0.0F);
            if (this.field21246 != null) {
                RenderUtil.drawImage(
                        (float) this.xA,
                        (float) (this.yA - (this.widthA - this.heightA) / 2),
                        (float) this.widthA,
                        (float) this.widthA,
                        this.field21246,
                        MathHelper.applyAlpha2(MathHelper.shiftTowardsOther(ClientColors.LIGHT_GREYISH_BLUE.getColor(), ClientColors.DEEP_TEAL.getColor(), 0.7F), 0.8F)
                );
            }

            GL11.glPopMatrix();
            RenderUtil.endScissor();
            RenderUtil.drawColoredRect(
                    (float) this.xA,
                    (float) this.yA,
                    (float) (this.xA + this.widthA),
                    (float) (this.yA + this.heightA),
                    MathHelper.applyAlpha2(ClientColors.DEEP_TEAL.getColor(), 0.3F + 0.3F * this.field21248.calcPercent())
            );
        }

        if (this.field21243 != null) {
            if (this.field21244 != null) {
                this.method13576();
                this.method13577();
                Resources.shoutIconPNG.bind();
                Resources.shoutIconPNG.bind();
                super.draw(partialTicks);
            }
        }
    }

    public void method13576() {
        GL11.glPushMatrix();
        float var5 = EasingFunctions.easeOutBack(this.field21248.calcPercent(), 0.0F, 1.0F, 1.0F);
        if (this.field21248.getDirection() == Animation.Direction.BACKWARDS) {
            var5 = QuadraticEasing.easeInQuad(this.field21248.calcPercent(), 0.0F, 1.0F, 1.0F);
        }

        GL11.glTranslatef((float) (this.getXA() + 44), (float) (this.getYA() + 44), 0.0F);
        GL11.glScaled(1.0 + 0.1 * (double) var5, 1.0 + 0.1 * (double) var5, 0.0);
        GL11.glTranslatef((float) (-this.getXA() - 44), (float) (-this.getYA() - 44), 0.0F);
        if (this.field21245 == null) {
            Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation("textures/misc/unknown_server.png"));
            RenderUtil.drawTexturedRect(
                    (float) (this.xA + 12), (float) (this.yA + 12), 64.0F, 64.0F, ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.0F, 0.0F, 64.0F, 64.0F
            );
        } else {
            RenderUtil.drawImage(
                    (float) (this.xA + 12), (float) (this.yA + 12), 64.0F, 64.0F, this.field21245, ClientColors.LIGHT_GREYISH_BLUE.getColor(), true
            );
        }

        GL11.glPopMatrix();
        Resources.shoutIconPNG.bind();
        Resources.shoutIconPNG.bind();
    }

    public void method13577() {
        long var3 = this.field21243.getDate().getTime() - new Date().getTime();
        int var5 = (int) (var3 / 1000L) % 60;
        int var6 = (int) (var3 / 60000L % 60L);
        int var7 = (int) (var3 / 3600000L % 24L);
        int var8 = (int) (var3 / 86400000L);
        RenderUtil.startScissorUnscaled(
                this.method13271() + this.method13280(),
                this.method13272() + this.method13282(),
                this.method13271() + this.method13280() + this.widthA,
                this.method13272() + this.method13282() + this.heightA
        );
        GL11.glPushMatrix();
        float var11 = EasingFunctions.easeOutBack(this.field21248.calcPercent(), 0.0F, 1.0F, 1.0F);
        if (this.field21248.getDirection() == Animation.Direction.BACKWARDS) {
            var11 = QuadraticEasing.easeInQuad(this.field21248.calcPercent(), 0.0F, 1.0F, 1.0F);
        }

        GL11.glTranslatef((float) (this.getXA() + 76), (float) (this.getYA() + 44), 0.0F);
        GL11.glScaled(1.0 - 0.1 * (double) var11, 1.0 - 0.1 * (double) var11, 0.0);
        GL11.glTranslatef((float) (-this.getXA() - 76), (float) (-this.getYA() - 44), 0.0F);
        RenderUtil.drawString(
                ResourceRegistry.JelloMediumFont25,
                (float) (this.xA + 94),
                (float) (this.yA + 16),
                !this.field21244.serverName.equals("Minecraft Server")
                        ? this.field21244.serverName
                        : this.field21244.serverIP.substring(0, 1).toUpperCase() + this.field21244.serverIP.substring(1),
                MathHelper.applyAlpha2(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.9F)
        );
        int var12 = 94;
        int var13 = 46;
        if (this.field21243.getDate().getTime() != 9223372036854775806L) {
            if (var3 > 0L && this.field21243.getDate().getTime() != Long.MAX_VALUE) {
                RenderUtil.drawString(
                        ResourceRegistry.JelloLightFont18,
                        (float) (this.xA + var12),
                        (float) (this.yA + var13),
                        "Unban: " + var8 + " days, " + var7 + "h " + var6 + "m " + var5 + "s",
                        MathHelper.shiftTowardsOther(ClientColors.DEEP_TEAL.getColor(), ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.2F)
                );
            } else if (this.field21243.getDate().getTime() != Long.MAX_VALUE) {
                RenderUtil.drawString(
                        ResourceRegistry.JelloLightFont18,
                        (float) (this.xA + var12),
                        (float) (this.yA + var13),
                        "Unbanned!",
                        MathHelper.shiftTowardsOther(ClientColors.DARK_SLATE_GREY.getColor(), ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.3F)
                );
            } else {
                RenderUtil.drawString(
                        ResourceRegistry.JelloLightFont18,
                        (float) (this.xA + var12),
                        (float) (this.yA + var13),
                        "Permanently banned!",
                        MathHelper.shiftTowardsOther(ClientColors.PALE_YELLOW.getColor(), ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.3F)
                );
            }
        } else {
            RenderUtil.drawString(
                    ResourceRegistry.JelloLightFont18,
                    (float) (this.xA + var12),
                    (float) (this.yA + var13),
                    "Compromised ban (unbannable)!",
                    MathHelper.shiftTowardsOther(ClientColors.DARK_OLIVE.getColor(), ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.3F)
            );
        }

        GL11.glPopMatrix();
        RenderUtil.endScissor();
    }

    public static BufferedImage method13578(String var0) {
        if (var0 == null) {
            return null;
        } else if (!Base64.isBase64(var0)) {
            return null;
        } else {
            try {
                return ImageIO.read(new ByteArrayInputStream(Base64.decodeBase64(var0)));
            } catch (IOException var4) {
                return null;
            }
        }
    }

    public static BufferedImage method13579(BufferedImage var0, double var1, double var3) {
        BufferedImage var7 = null;
        if (var0 != null) {
            int var8 = (int) ((double) var0.getHeight() * var3);
            int var9 = (int) ((double) var0.getWidth() * var1);
            var7 = new BufferedImage(var9, var8, var0.getType());
            Graphics2D var10 = var7.createGraphics();
            AffineTransform var11 = AffineTransform.getScaleInstance(var1, var3);
            var10.drawRenderedImage(var0, var11);
        }

        return var7;
    }

    @Override
    public boolean onClick(int mouseX, int mouseY, int mouseButton) {
        return false;
    }
}
