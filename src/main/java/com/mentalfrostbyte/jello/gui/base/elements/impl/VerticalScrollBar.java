package com.mentalfrostbyte.jello.gui.base.elements.impl;

import com.mentalfrostbyte.jello.Client;
import com.mentalfrostbyte.jello.gui.base.interfaces.IScroll;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientMode;
import com.mentalfrostbyte.jello.gui.base.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.impl.others.panels.AnimatedIconPanelWrap;
import com.mentalfrostbyte.jello.gui.impl.jello.buttons.ScrollableContentPanel;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.system.math.counter.TimerUtil;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil2;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.client.render.Resources;
import totalcross.json.CJsonUtils;
import totalcross.json.JSONObject;

public class VerticalScrollBar extends AnimatedIconPanelWrap implements IScroll {
   public int scrollOffset;
   public float field20794;
   public boolean field20795;
   public final VerticalScrollBarButton field20796;
   public TimerUtil field20797 = new TimerUtil();

   public VerticalScrollBar(CustomGuiScreen var1, int var2) {
      super(var1, "verticalScrollBar", var1.getWidthA() - var2 - 5, 5, var2, var1.getHeightA() - 10, false);
      this.setSize((var1x, var2x) -> {
         var1x.setXA(var2x.getWidthA() - var2 - 5);
         var1x.setYA(5);
         var1x.setWidthA(var2);
         var1x.setHeightA(var2x.getHeightA() - 10);
      });
      this.addToList(this.field20796 = new VerticalScrollBarButton(this, this, var2));
   }

   @Override
   public void voidEvent3(float scroll) {
      super.voidEvent3(scroll);
      if (this.parent != null && this.parent.method13228(this.getHeightO(), this.getWidthO(), false) || ((ScrollableContentPanel)this.parent).field21208) {
         float var4 = (float)((ScrollableContentPanel)this.getParent()).getButton().getHeightA();
         float var5 = (float)this.getParent().getHeightA();
         float var6 = (float)this.getHeightA();
         if (var4 == 0.0F) {
            return;
         }

         float var7 = var5 / var4;
         if (var7 >= 1.0F) {
            return;
         }

         this.scrollOffset = this.scrollOffset
            - Math.round(!(scroll < 0.0F) ? (float)((ScrollableContentPanel)this.parent).field21207 * scroll : 1.0F * (float)((ScrollableContentPanel)this.parent).field21207 * scroll);
         this.field20797.reset();
         this.field20797.start();
      }
   }

   @Override
   public void updatePanelDimensions(int newHeight, int newWidth) {
      super.updatePanelDimensions(newHeight, newWidth);
      this.field20908 = this.method13228(newHeight, newWidth, false);
      this.field20794 = this.field20794
         + (
            this.field20796.getHeightA() >= this.getHeightA()
               ? -1.0F
               : (
                  !this.method13298() && !this.field20796.method13216() && (!this.field20797.isEnabled() || this.field20797.getElapsedTime() >= 500L)
                     ? -0.05F
                     : 0.05F
               )
         );
      this.field20794 = Math.min(Math.max(0.0F, this.field20794), 1.0F);
      float var5 = (float)((ScrollableContentPanel)this.getParent()).getButton().getHeightA();
      float var6 = (float)this.getParent().getHeightA();
      float var7 = (float)this.getHeightA();
      float var8 = var6 / var5;
      boolean var9 = var8 < 1.0F && var5 > 0.0F && this.field20794 >= 0.0F;
      this.setEnabled(var9);
      this.method13296(var9);
   }

   @Override
   public void draw(float partialTicks) {
      partialTicks *= this.field20794;
      int var4 = 5;
      int var5 = RenderUtil2.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.2F * partialTicks);
      int var6 = this.xA;
      int var7 = this.widthA;
      if (Client.getInstance().clientMode != ClientMode.JELLO) {
         var4 = 0;
         var7 -= 8;
         var6 += 8;
         RenderUtil.drawRoundedRect(
            (float)var6,
            (float)(this.yA + var4),
            (float)(var6 + var7),
            (float)(this.yA + this.heightA - var4),
            RenderUtil2.applyAlpha(ClientColors.MID_GREY.getColor(), 0.1F * partialTicks)
         );
      } else {
         RenderUtil.drawImage((float)var6, (float)this.yA, (float)var7, 5.0F, Resources.verticalScrollBarTopPNG, 0.45F * partialTicks);
         RenderUtil.drawImage((float)var6, (float)(this.yA + this.heightA - var4), (float)var7, 5.0F, Resources.verticalScrollBarBottomPNG, 0.45F * partialTicks);
         RenderUtil.drawRoundedRect((float)var6, (float)(this.yA + var4), (float)(var6 + var7), (float)(this.yA + this.heightA - var4), var5);
      }

      super.draw(partialTicks);
   }

   @Override
   public boolean onClick(int mouseX, int mouseY, int mouseButton) {
      if (!super.onClick(mouseX, mouseY, mouseButton)) {
         this.field20908 = this.method13228(mouseX, mouseY, false);
         if (this.method13298()) {
            int var6 = mouseY - this.method13272();
            if (var6 <= this.field20796.getYA() + this.field20796.getHeightA()) {
               if (var6 < this.field20796.getYA()) {
                  this.scrollOffset = this.scrollOffset - (int)((float)((ScrollableContentPanel)this.parent).getButton().getHeightA() / 4.0F);
               }
            } else {
               this.scrollOffset = this.scrollOffset + (int)((float)((ScrollableContentPanel)this.parent).getButton().getHeightA() / 4.0F);
            }
         }

         return false;
      } else {
         return true;
      }
   }

   @Override
   public JSONObject toConfigWithExtra(JSONObject config) {
      config.put("offset", this.scrollOffset);
      return super.toConfigWithExtra(config);
   }

   @Override
   public void loadConfig(JSONObject config) {
      super.loadConfig(config);
      this.scrollOffset = CJsonUtils.getIntOrDefault(config, "offset", this.scrollOffset);
   }

   @Override
   public int getScrollOffset() {
      return this.scrollOffset;
   }

   public static class VerticalScrollBarButton extends AnimatedIconPanelWrap {
      private final ScrollableContentPanel field20780;
      public final VerticalScrollBar field20781;

      public VerticalScrollBarButton(VerticalScrollBar var1, VerticalScrollBar var2, int var3) {
         super(var2, "verticalScrollBarButton", 0, 0, var3, 10, true);
         this.field20781 = var1;
         this.field20886 = true;
         this.field20780 = (ScrollableContentPanel)var2.getParent();
         this.setListening(false);
      }

      @Override
      public void updatePanelDimensions(int newHeight, int newWidth) {
         super.updatePanelDimensions(newHeight, newWidth);
         float var5 = (float)this.field20780.getButton().getHeightA();
         float var6 = (float)this.parent.getParent().getHeightA();
         float var7 = (float)this.parent.getHeightA();
         float var8 = var6 / var5;
         float var9 = var7 * var8;
         float var10 = 20.0F;
         if (!(var9 < 20.0F)) {
            if (var9 > var7) {
               var9 = var7;
            }
         } else {
            var9 = 20.0F;
         }

         this.setHeightA((int)var9);
         if (!this.field20877 && this.getHeightA() != this.parent.getHeightA()) {
            if (this.field20781.scrollOffset >= 0) {
               if (this.field20781.scrollOffset + this.parent.getParent().getHeightA() > this.field20780.getButton().getHeightA()) {
                  this.field20781.scrollOffset = this.field20780.getButton().getHeightA() - this.parent.getParent().getHeightA();
               }
            } else {
               this.field20781.scrollOffset = 0;
            }

            float var16 = var5 - var6;
            float var13 = (float)this.field20781.scrollOffset / var16;
            float var14 = (float)(this.parent.getHeightA() - this.getHeightA());
            float var15 = var14 * var13 + 0.5F;
            this.setYA((int)var15);
         } else if (this.method13216()) {
            float var12 = (float)this.getYA() / (float)this.parent.getHeightA();
            this.field20781.scrollOffset = (int)(var12 * (float)this.field20780.getButton().getHeightA());
            if (this.field20781.scrollOffset >= 0) {
               if (this.field20781.scrollOffset + this.parent.getParent().getHeightA() > this.field20780.getButton().getHeightA()) {
                  this.field20781.scrollOffset = this.field20780.getButton().getHeightA() - this.parent.getParent().getHeightA();
               }
            } else {
               this.field20781.scrollOffset = 0;
            }

            this.field20781.field20797.reset();
            this.field20781.field20797.start();
         }
      }

      @Override
      public void draw(float partialTicks) {
         int var4 = 5;
         partialTicks *= !this.field20877 ? (!this.field20908 ? 0.3F : 0.7F) : 0.75F;
         int var5 = this.xA;
         int var6 = this.widthA;
         if (Client.getInstance().clientMode != ClientMode.JELLO) {
            var4 = 0;
            var6 -= 7;
            var5 += 7;
            RenderUtil.drawRoundedRect(
               (float)var5,
               (float)(this.yA + var4),
               (float)(var5 + var6),
               (float)(this.yA + this.getHeightA() - var4),
               RenderUtil2.applyAlpha(ClientColors.MID_GREY.getColor(), 1.0F * partialTicks)
            );
         } else {
            RenderUtil.drawImage((float)var5, (float)this.yA, (float)var6, (float)var4, Resources.verticalScrollBarTopPNG, partialTicks);
            RenderUtil.drawImage((float)var5, (float)(this.yA + this.heightA - var4), (float)var6, (float)var4, Resources.verticalScrollBarBottomPNG, partialTicks);
            RenderUtil.drawRoundedRect(
               (float)var5,
               (float)(this.yA + var4),
               (float)(var5 + var6),
               (float)(this.yA + this.getHeightA() - var4),
                    RenderUtil2.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.45F * partialTicks)
            );
         }

         super.draw(partialTicks);
      }
   }
}
