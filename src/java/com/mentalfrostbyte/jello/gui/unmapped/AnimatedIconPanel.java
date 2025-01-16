package com.mentalfrostbyte.jello.gui.unmapped;

import com.mentalfrostbyte.jello.gui.base.CustomGuiScreen;
import com.mentalfrostbyte.jello.util.ColorHelper;
import com.mentalfrostbyte.jello.util.TimerUtil;
import org.newdawn.slick.TrueTypeFont;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;
/**
 * A custom GUI panel that displays an animated icon.
 * <p>
 * This panel extends the CustomGuiScreen class and implements the Class4347 interface.
 * It provides functionality for animating an icon and handling user interactions.
 */
 public class AnimatedIconPanel extends CustomGuiScreen implements Class4347 {
    public boolean isEnabled;
   public boolean isVisible;
   public int xPos;
   public int yPos;
   public int panelWidth;
   public int panelHeight;
   public boolean isFocused = true;
   public boolean isHovered = false;
   public boolean isPressed = true;
   public boolean isActive = true;
   public boolean field20886 = false;
   public final TimerUtil timerUtil = new TimerUtil();
   public int field20888 = 300;
   public int field20889 = 2;
   private final List<AnimatedIconPanelAnimation> animations = new ArrayList<AnimatedIconPanelAnimation>();

   public AnimatedIconPanel(CustomGuiScreen screen, String iconNanme, int var3, int var4, int var5, int var6, boolean isEnabled) {
      super(screen, iconNanme, var3, var4, var5, var6);
      this.isEnabled = isEnabled;
   }

   public AnimatedIconPanel(CustomGuiScreen screen, String iconName, int var3, int var4, int var5, int var6, ColorHelper colorHelper, boolean isEnabled) {
      super(screen, iconName, var3, var4, var5, var6, colorHelper);
      this.isEnabled = isEnabled;
   }

   public AnimatedIconPanel(CustomGuiScreen screen, String iconName, int var3, int var4, int var5, int var6, ColorHelper colorHelper, String var8, boolean isEnabled) {
      super(screen, iconName, var3, var4, var5, var6, colorHelper, var8);
      this.isEnabled = isEnabled;
   }

   public AnimatedIconPanel(CustomGuiScreen screen, String iconName, int var3, int var4, int var5, int var6, ColorHelper colorHelper, String var8, TrueTypeFont font, boolean isEnabled) {
      super(screen, iconName, var3, var4, var5, var6, colorHelper, var8, font);
      this.isEnabled = isEnabled;
   }

   @Override
   public boolean method13212() {
      return this.field20909 && !this.isVisible();
   }

   @Override
   public void updatePanelDimensions(int x, int y) {
      super.updatePanelDimensions(x, y);
      if (this.isEnabled()) {
         if (!this.field20909 && !this.isVisible) {
            this.panelWidth = this.getWidthA() / 2;
            this.panelHeight = this.getHeightA() / 2;
         }

         this.updateVisibilityAndPosition(x, y);
      }
   }

   @Override
   public boolean method13078(int var1, int var2, int var3) {
      if (!super.method13078(var1, var2, var3)) {
         if (this.isEnabled()) {
            this.timerUtil.start();
            this.xPos = var1;
            this.yPos = var2;
            this.panelWidth = this.xPos - this.method13271();
            this.panelHeight = this.yPos - this.method13272();
         }

         return false;
      } else {
         return true;
      }
   }

   @Override
   public void method13095(int var1, int var2, int var3) {
      super.method13095(var1, var2, var3);
      if (this.isEnabled()) {
         this.timerUtil.stop();
         this.timerUtil.reset();
      }

      this.setVisible(false);
   }
   /**
    * Updates the visibility and position of the panel based on the given coordinates.
    * <p>
    * This method checks the current visibility and enabled state of the panel,
    * and adjusts its position and visibility accordingly. It also ensures that
    * the panel remains within the bounds of the screen or parent component.
    *
    * @param x the x-coordinate to update the panel's position
    * @param y the y-coordinate to update the panel's position
    */
   @Override
   public void updateVisibilityAndPosition(int x, int y) {
      boolean var5 = this.isVisible;
      if (!this.isVisible() && this.isEnabled()) {
         boolean var6 = this.isPressed && this.timerUtil.getElapsedTime() >= (long)this.field20888;
         boolean var7 = this.isActive
            && this.field20909
            && (Math.abs(this.xPos - x) > this.field20889 || Math.abs(this.yPos - y) > this.field20889);
         boolean var8 = this.field20886 && this.field20909;
         if (var6 || var7 || var8) {
            this.setVisible(true);
         }
      } else if (this.isVisible()) {
         this.setXA(x - this.panelWidth - (this.screen == null ? 0 : this.screen.method13271()));
         this.setYA(y - this.panelHeight - (this.screen == null ? 0 : this.screen.method13272()));
         if (this.isFocused) {
            if (this.screen == null) {
               if (this.getXA() < 0) {
                  this.setXA(0);
               }

               if (this.getXA() + this.getWidthA() > Minecraft.getInstance().getMainWindow().getWidth()) {
                  this.setXA(Minecraft.getInstance().getMainWindow().getWidth() - this.getWidthA());
               }

               if (this.getYA() < 0) {
                  this.setYA(0);
               }

               if (this.getYA() + this.getHeightA() > Minecraft.getInstance().getMainWindow().getHeight()) {
                  this.setYA(Minecraft.getInstance().getMainWindow().getHeight() - this.getHeightA());
               }
            } else {
               if (this.getXA() < 0) {
                  this.setXA(0);
               }

               if (this.getXA() + this.getWidthA() > this.screen.getWidthA()) {
                  this.setXA(this.screen.getWidthA() - this.getWidthA());
               }

               if (this.getYA() < 0) {
                  this.setYA(0);
               }

               if (this.getYA() + this.getHeightA() > this.screen.getHeightA() && !this.isHovered) {
                  this.setYA(this.screen.getHeightA() - this.getHeightA());
               }
            }
         }
      }

      if (this.isVisible() && !var5) {
         this.timerUtil.stop();
         this.timerUtil.reset();
      }
   }

   @Override
   public boolean isEnabled() {
      return this.isEnabled;
   }

   @Override
   public void setEnabled(boolean enabled) {
      this.isEnabled = enabled;
   }

   @Override
   public boolean isVisible() {
      return this.isVisible;
   }

   @Override
   public void setVisible(boolean visible) {
      this.isVisible = visible;
      if (visible) {
         this.setEnabled(true);
         this.method13219();
      }
   }

   public AnimatedIconPanel addAnimation(AnimatedIconPanelAnimation var1) {
      this.animations.add(var1);
      return this;
   }

   public void method13219() {
      for (AnimatedIconPanelAnimation animation : this.animations) {
         animation.animate(this);
      }
   }
}
