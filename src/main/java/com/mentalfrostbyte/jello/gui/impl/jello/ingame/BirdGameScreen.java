package com.mentalfrostbyte.jello.gui.impl.jello.ingame;

import com.mentalfrostbyte.jello.gui.base.animations.Animation;
import com.mentalfrostbyte.jello.util.system.math.smoothing.EasingFunctions;
import com.mentalfrostbyte.jello.gui.base.Screen;
import com.mentalfrostbyte.jello.gui.impl.jello.ingame.panels.BirdPanel;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.client.render.ResourceRegistry;
import com.mentalfrostbyte.jello.util.system.math.counter.TimerUtil;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil2;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import net.minecraft.client.Minecraft;

public class BirdGameScreen extends Screen {
   public Minecraft field21044 = Minecraft.getInstance();
   public TimerUtil field21045 = new TimerUtil();
   public BirdPanel birdPanel;
   public Animation field21047;
   public int field21048 = 0;
   public int field21049 = 14;

   public BirdGameScreen() {
      super("BirdGameScreen");
      this.setListening(false);
      this.field21045.start();
      this.field21047 = new Animation(200, 0);
      RenderUtil2.blur();
      int var3 = 48;
      int var4 = 27;
      int var5 = 14;
      int var6 = var3 * var5;
      int var7 = var4 * var5;
      this.addToList(this.birdPanel = new BirdPanel(this, "bird", (this.widthA - var6) / 2, (this.getHeightA() - var7) / 2 + 30, var3, 27, var5));
   }

   @Override
   public void draw(float partialTicks) {
      partialTicks = this.field21047.calcPercent();
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
         (float)this.birdPanel.getXA(),
         (float)this.birdPanel.getYA(),
         (float)this.birdPanel.getWidthA(),
         (float)this.birdPanel.getHeightA(),
         40.0F,
              partialTicks
      );
      RenderUtil.drawRoundedRect(
         (float)(this.birdPanel.getXA() - 20),
         (float)(this.birdPanel.getYA() - 20),
         (float)(this.birdPanel.getWidthA() + 40),
         (float)(this.birdPanel.getHeightA() + 40),
         14.0F,
         ClientColors.LIGHT_GREYISH_BLUE.getColor()
      );
      super.draw(partialTicks);
      int var6 = (this.widthA - this.birdPanel.getWidthA()) / 2;
      int var7 = (this.heightA - this.birdPanel.getHeightA()) / 2;
      RenderUtil.drawString(ResourceRegistry.JelloMediumFont40, (float)var6, (float)(var7 - 60), "Bird", ClientColors.LIGHT_GREYISH_BLUE.getColor());
      this.field21048 = Math.max(this.birdPanel.method13179(), this.field21048);
      String var8 = "Max: " + this.field21048 + "   |   Score: 0";
      RenderUtil.drawString(
         ResourceRegistry.JelloLightFont20,
         (float)(var6 + this.birdPanel.getWidthA() - ResourceRegistry.JelloLightFont20.getWidth(var8)),
         (float)(var7 - 50),
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
}
