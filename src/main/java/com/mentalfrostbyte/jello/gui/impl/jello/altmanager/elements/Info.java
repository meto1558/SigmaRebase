package com.mentalfrostbyte.jello.gui.impl.jello.altmanager.elements;

import com.mentalfrostbyte.jello.Client;
import com.mentalfrostbyte.jello.gui.base.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.base.animations.Animation;
import com.mentalfrostbyte.jello.gui.impl.others.panels.AnimatedIconPanelWrap;
import com.mentalfrostbyte.jello.managers.util.account.microsoft.Account;
import com.mentalfrostbyte.jello.managers.util.account.microsoft.Ban;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.client.render.ResourceRegistry;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil2;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.client.render.Resources;
import com.mentalfrostbyte.jello.util.system.math.smoothing.EasingFunctions;
import com.mentalfrostbyte.jello.util.system.math.smoothing.QuadraticEasing;
import com.mentalfrostbyte.jello.util.system.network.ImageUtil;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Info extends AnimatedIconPanelWrap {
   private static String[] field20812;
   private Account field20813 = null;
   private List<Class4348> field20814 = new ArrayList<Class4348>();
   private float field20815 = 0.0F;

   public Info(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6, false);
   }

   public void handleSelectedAccount(Account var1) {
      this.field20813 = var1;

      for (Class4348 var5 : this.field20814) {
         this.method13234(var5);
      }

      if (var1 != null) {
         List<Ban> var11 = new ArrayList();

         for (Ban var6 : var1.getBans()) {
            var11.add(var6);
         }

         Collections.reverse(var11);
         int var13 = 0;
         int var14 = 90;
         int var7 = 14;

         for (Ban var9 : var11) {
            if (var9.getServer() != null && var9.getServer().getBase64EncodedIconData() != null) {
               Class4348 var10 = new Class4348(
                  this, ((Ban)var11.get(var13)).getServerIP(), 40, 100 + var13 * (var14 + var7), this.widthA - 90, var14, var9
               );
               this.addToList(var10);
               this.field20814.add(var10);
               var13++;
            }
         }

         this.setHeightA(var13 * (var14 + var7) + 116);
      }
   }

   @Override
   public void draw(float partialTicks) {
      this.method13225();
      this.field20815 = (float)((double)this.field20815 + (this.isVisible() ? 0.33 : -0.33));
      this.field20815 = Math.min(1.0F, Math.max(0.0F, this.field20815));
      if (this.field20813 == null) {
         int var4 = this.widthA - 30;
         int var5 = this.xA + 5;
         RenderUtil.drawImage(
            (float)var5,
            (float)((Minecraft.getInstance().getMainWindow().getHeight() - var4 * 342 / 460) / 2 - 60),
            (float)var4,
            (float)(var4 * 342 / 460),
            Resources.imgPNG
         );
      }

      if (this.field20813 != null) {
         int var7 = RenderUtil2.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.7F);
         RenderUtil.drawString(
            ResourceRegistry.JelloLightFont36,
            (float)(this.xA + (this.widthA - ResourceRegistry.JelloLightFont36.getWidth(this.field20813.getKnownName())) / 2),
            (float)this.yA,
            this.field20813.getKnownName(),
            var7
         );
         super.draw(partialTicks);
      }
   }

   public static class Class4348 extends AnimatedIconPanelWrap {
      public Ban field21243 = null;
      public ServerData field21244 = null;
      public Texture field21245 = null;
      public Texture field21246 = null;
      private BufferedImage field21247;
      private Animation field21248;

      public Class4348(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6, Ban var7) {
         super(var1, var2, var3, var4, var5, var6, false);
         this.field21243 = var7;
         this.field21244 = var7.getServer();
         this.field21248 = new Animation(200, 200, Animation.Direction.BACKWARDS);
      }

      @Override
      public void finalize() throws Throwable {
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
         } else if ((double)Math.abs(var4 - var5) < 0.7) {
            this.field21248.changeDirection(Animation.Direction.BACKWARDS);
         }

         if (this.method13272() + this.method13282() < Minecraft.getInstance().getMainWindow().getHeight() - 36 && this.method13272() + this.method13282() > 52) {
            if (this.field21244 != null && this.field21246 == null) {
               try {
                  BufferedImage var6 = method13578(this.field21244.getBase64EncodedIconData());
                  if (var6 != null) {
                     this.field21245 = BufferedImageUtil.getTexture("servericon", var6);
                     this.field21246 = BufferedImageUtil.getTexture(
                        "servericon", ImageUtil.applyBlur(ImageUtil.method35042(method13579(var6, 2.5, 2.5), 0.0F, 1.1F, 0.0F), 25)
                     );
                  }
               } catch (IOException var8) {
                  var8.printStackTrace();
               }
            }

            RenderUtil.method11415(this);
            RenderUtil.drawRoundedRect(
               (float)this.xA,
               (float)this.yA,
               (float)(this.xA + this.widthA),
               (float)(this.yA + this.heightA),
               ClientColors.LIGHT_GREYISH_BLUE.getColor()
            );
            GL11.glTexParameteri(3553, 10241, 9728);
            GL11.glPushMatrix();
            int var9 = this.widthA / 2;
            int var7 = this.heightA / 2;
            if (this.field21248.getDirection() == Animation.Direction.BACKWARDS) {
               var4 = QuadraticEasing.easeInQuad(this.field21248.calcPercent(), 0.0F, 1.0F, 1.0F);
            }

            GL11.glTranslatef((float)(this.getXA() + var9), (float)(this.getYA() + var7), 0.0F);
            GL11.glScaled(1.0 + 0.4 * (double)var4, 1.0 + 0.4 * (double)var4, 0.0);
            GL11.glTranslatef((float)(-this.getXA() - var9), (float)(-this.getYA() - var7), 0.0F);
            if (this.field21246 != null) {
               RenderUtil.drawImage(
                  (float)this.xA,
                  (float)(this.yA - (this.widthA - this.heightA) / 2),
                  (float)this.widthA,
                  (float)this.widthA,
                  this.field21246,
                       RenderUtil2.applyAlpha(RenderUtil2.shiftTowardsOther(ClientColors.LIGHT_GREYISH_BLUE.getColor(), ClientColors.DEEP_TEAL.getColor(), 0.7F), 0.8F)
               );
            }

            GL11.glPopMatrix();
            RenderUtil.endScissor();
            RenderUtil.drawRoundedRect(
               (float)this.xA,
               (float)this.yA,
               (float)(this.xA + this.widthA),
               (float)(this.yA + this.heightA),
                    RenderUtil2.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.3F + 0.3F * this.field21248.calcPercent())
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

         GL11.glTranslatef((float)(this.getXA() + 44), (float)(this.getYA() + 44), 0.0F);
         GL11.glScaled(1.0 + 0.1 * (double)var5, 1.0 + 0.1 * (double)var5, 0.0);
         GL11.glTranslatef((float)(-this.getXA() - 44), (float)(-this.getYA() - 44), 0.0F);
         if (this.field21245 == null) {
            Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation("textures/misc/unknown_server.png"));
            RenderUtil.method11457(
               (float)(this.xA + 12), (float)(this.yA + 12), 64.0F, 64.0F, ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.0F, 0.0F, 64.0F, 64.0F
            );
         } else {
            RenderUtil.drawImage(
               (float)(this.xA + 12), (float)(this.yA + 12), 64.0F, 64.0F, this.field21245, ClientColors.LIGHT_GREYISH_BLUE.getColor(), true
            );
         }

         GL11.glPopMatrix();
         Resources.shoutIconPNG.bind();
         Resources.shoutIconPNG.bind();
      }

      public void method13577() {
         long var3 = this.field21243.getDate().getTime() - new Date().getTime();
         int var5 = (int)(var3 / 1000L) % 60;
         int var6 = (int)(var3 / 60000L % 60L);
         int var7 = (int)(var3 / 3600000L % 24L);
         int var8 = (int)(var3 / 86400000L);
         RenderUtil.drawPortalBackground(
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

         GL11.glTranslatef((float)(this.getXA() + 76), (float)(this.getYA() + 44), 0.0F);
         GL11.glScaled(1.0 - 0.1 * (double)var11, 1.0 - 0.1 * (double)var11, 0.0);
         GL11.glTranslatef((float)(-this.getXA() - 76), (float)(-this.getYA() - 44), 0.0F);
         RenderUtil.drawString(
            ResourceRegistry.JelloMediumFont25,
            (float)(this.xA + 94),
            (float)(this.yA + 16),
            !this.field21244.serverName.equals("Minecraft Server")
               ? this.field21244.serverName
               : this.field21244.serverIP.substring(0, 1).toUpperCase() + this.field21244.serverIP.substring(1, this.field21244.serverIP.length()),
            RenderUtil2.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.9F)
         );
         int var12 = 94;
         int var13 = 46;
         if (this.field21243.getDate().getTime() != 9223372036854775806L) {
            if (var3 > 0L && this.field21243.getDate().getTime() != Long.MAX_VALUE) {
               RenderUtil.drawString(
                  ResourceRegistry.JelloLightFont18,
                  (float)(this.xA + var12),
                  (float)(this.yA + var13),
                  "Unban: " + var8 + " days, " + var7 + "h " + var6 + "m " + var5 + "s",
                       RenderUtil2.shiftTowardsOther(ClientColors.DEEP_TEAL.getColor(), ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.2F)
               );
            } else if (this.field21243.getDate().getTime() != Long.MAX_VALUE) {
               RenderUtil.drawString(
                  ResourceRegistry.JelloLightFont18,
                  (float)(this.xA + var12),
                  (float)(this.yA + var13),
                  "Unbanned!",
                       RenderUtil2.shiftTowardsOther(ClientColors.DARK_SLATE_GREY.getColor(), ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.3F)
               );
            } else {
               RenderUtil.drawString(
                  ResourceRegistry.JelloLightFont18,
                  (float)(this.xA + var12),
                  (float)(this.yA + var13),
                  "Permanently banned!",
                       RenderUtil2.shiftTowardsOther(ClientColors.PALE_YELLOW.getColor(), ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.3F)
               );
            }
         } else {
            RenderUtil.drawString(
               ResourceRegistry.JelloLightFont18,
               (float)(this.xA + var12),
               (float)(this.yA + var13),
               "Compromised ban (unbannable)!",
                    RenderUtil2.shiftTowardsOther(ClientColors.DARK_OLIVE.getColor(), ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.3F)
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
            int var8 = (int)((double)var0.getHeight() * var3);
            int var9 = (int)((double)var0.getWidth() * var1);
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
}
