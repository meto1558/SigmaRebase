package com.mentalfrostbyte.jello.gui.impl.jello.buttons;

import com.mentalfrostbyte.jello.gui.combined.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.base.elements.impl.VerticalScrollBar;
import com.mentalfrostbyte.jello.gui.combined.AnimatedIconPanel;
import com.mentalfrostbyte.jello.gui.combined.ContentSize;
import com.mentalfrostbyte.jello.gui.base.interfaces.IWidthSetter;
import com.mentalfrostbyte.jello.util.client.render.theme.ColorHelper;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import org.newdawn.slick.TrueTypeFont;

public class ScrollableContentPanel extends AnimatedIconPanel {
   private boolean field21201;
   private boolean field21202;
   private boolean field21203 = false;
   public CustomGuiScreen buttonList;
   public VerticalScrollBar scrollBar;
   private boolean field21206 = true;
   public int field21207 = 35;
   public boolean field21208 = false;

   public ScrollableContentPanel(CustomGuiScreen var1, String name, int var3, int var4, int var5, int var6) {
      super(var1, name, var3, var4, var5, var6, false);
      this.method13511();
   }

   public ScrollableContentPanel(CustomGuiScreen var1, String name, int var3, int var4, int var5, int var6, ColorHelper var7) {
      super(var1, name, var3, var4, var5, var6, var7, false);
      this.method13511();
   }

   public ScrollableContentPanel(CustomGuiScreen var1, String name, int var3, int var4, int var5, int var6, ColorHelper var7, String var8) {
      super(var1, name, var3, var4, var5, var6, var7, var8, false);
      this.method13511();
   }

   public ScrollableContentPanel(CustomGuiScreen var1, String name, int var3, int var4, int var5, int var6, ColorHelper var7, String var8, TrueTypeFont var9) {
      super(var1, name, var3, var4, var5, var6, var7, var8, var9, false);
      this.method13511();
   }

   private void method13511() {
      this.getChildren().add(this.buttonList = new CustomGuiScreen(this, "content", 0, 0, this.widthA, this.heightA));
      this.buttonList.setSize(new ContentSize());
      this.getChildren().add(this.scrollBar = new VerticalScrollBar(this, 11));
      this.scrollBar.setReAddChildren(true);
   }

   public void method13512(int var1) {
      this.scrollBar.offset = var1;
   }

   public int method13513() {
      return this.scrollBar != null ? this.scrollBar.offset : 0;
   }

   public void method13514(boolean var1) {
      this.field21203 = var1;
   }

   @Override
   public void updatePanelDimensions(int newHeight, int newWidth) {
      if (!this.field21203 || this.isSelfVisible()) {
         super.updatePanelDimensions(newHeight, newWidth);
         this.buttonList.setYA(-1 * this.scrollBar.method13162());

         for (CustomGuiScreen var6 : this.getButton().getChildren()) {
            for (IWidthSetter var8 : var6.method13260()) {
               var8.setWidth(var6, this);
            }
         }
      }
   }

   public void method13515(boolean var1) {
      this.field21206 = var1;
   }

   public boolean method13516() {
      return this.field21206;
   }

   @Override
   public void draw(float partialTicks) {
      this.method13224();
      if (!this.field21203 || this.isSelfVisible()) {
         if (this.field21206) {
            RenderUtil.startScissor(this);
         }

         super.draw(partialTicks);
         if (this.field21206) {
            RenderUtil.endScissor();
         }
      }
   }

   @Override
   public void addToList(CustomGuiScreen var1) {
      this.buttonList.addToList(var1);
   }

   @Override
   public boolean hasChild(CustomGuiScreen child) {
      return this.buttonList.hasChild(child);
   }

   @Override
   public boolean isntQueue(String var1) {
      return this.buttonList.isntQueue(var1);
   }

   public CustomGuiScreen getButton() {
      return this.buttonList;
   }

   public void method13518(boolean var1) {
      this.field21208 = var1;
   }
}
