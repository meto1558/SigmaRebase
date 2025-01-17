package com.mentalfrostbyte.jello.gui.impl;

import com.mentalfrostbyte.jello.gui.base.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.unmapped.AnimatedIconPanel;
import com.mentalfrostbyte.jello.gui.unmapped.UIBase;
import com.mentalfrostbyte.jello.gui.unmapped.Class6649;
import com.mentalfrostbyte.jello.util.ClientColors;
import com.mentalfrostbyte.jello.util.render.ColorUtils;
import com.mentalfrostbyte.jello.util.render.RenderUtil;

import java.util.ArrayList;
import java.util.List;

public class VolumeSlider extends UIBase {
   private static String[] field21371;
   private float field21372 = 1.0F;
   private boolean field21373 = false;
   private final List<Class6649> field21374 = new ArrayList<Class6649>();

   /**
    * Constructs a new VolumeSlider instance.
    *
    * @param parent    The CustomGuiScreen that this VolumeSlider belongs to.
    * @param iconName  The name of the icon associated with this VolumeSlider.
    * @param xV      The x-coordinate of the VolumeSlider.
    * @param yV      The y-coordinate of the VolumeSlider.
    * @param width     The width of the VolumeSlider.
    * @param height    The height of the VolumeSlider.
    */
   public VolumeSlider(CustomGuiScreen parent, String iconName, int xV, int yV, int width, int height) {
      super(parent, iconName, xV, yV, width, height, false);
   }

   @Override
   public void draw(float f) {
      RenderUtil.drawRoundedRect(
         (float)this.xA,
         (float)this.yA,
         (float)(this.xA + this.widthA),
         (float)this.yA + (float)this.heightA * this.field21372,
         ColorUtils.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.2F)
      );
      RenderUtil.drawRoundedRect(
         (float)this.xA,
         (float)(this.yA + this.heightA),
         (float)(this.xA + this.widthA),
         (float)this.yA + (float)this.heightA * this.field21372,
              ColorUtils.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.2F)
      );
      super.draw(f);
   }

   @Override
   public boolean onClick(int mouseX, int mouseY, int probablyTimes) {
      if (!super.onClick(this.xA, this.yA, probablyTimes)) {
         this.field21373 = true;
         return false;
      } else {
         return true;
      }
   }

   public float method13706(int var1) {
      return (float)(var1 - this.method13272()) / (float)this.heightA;
   }

   @Override
   public void updatePanelDimensions(int newHeight, int newWidth) {
      super.updatePanelDimensions(newHeight, newWidth);
      if (this.field21373) {
         this.setField21372(this.method13706(newWidth));
         this.method13710();
      }
   }

   @Override
   public void voidEvent1(int var1, int var2, int var3) {
      super.voidEvent1(var1, var2, var3);
      this.field21373 = false;
   }

   @Override
   public void voidEvent3(float var1) {
      if (this.method13298()) {
         this.setField21372(this.getField21372() - var1 / 2000.0F);
         this.method13710();
      }

      super.voidEvent3(var1);
   }

   public float getField21372() {
      return this.field21372;
   }

   public void setField21372(float value) {
      this.field21372 = Math.min(Math.max(value, 0.0F), 1.0F);
   }

   public AnimatedIconPanel method13709(Class6649 var1) {
      this.field21374.add(var1);
      return this;
   }

   public void method13710() {
      for (Class6649 var4 : this.field21374) {
         var4.method20301(this);
      }
   }
}
