package com.mentalfrostbyte.jello.gui.impl.jello.ingame.panels;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.gui.base.animations.Animation;
import com.mentalfrostbyte.jello.gui.base.elements.Element;
import com.mentalfrostbyte.jello.gui.base.elements.impl.button.Button;
import com.mentalfrostbyte.jello.gui.base.interfaces.Class7875;
import com.mentalfrostbyte.jello.gui.combined.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.impl.jello.buttons.ScrollableContentPanel;
import com.mentalfrostbyte.jello.gui.impl.jello.buttons.TextField;
import com.mentalfrostbyte.jello.gui.base.elements.impl.VerticalScrollBar;
import com.mentalfrostbyte.jello.gui.impl.jello.ingame.buttons.keybind.Class6984;
import com.mentalfrostbyte.jello.managers.GuiManager;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.client.render.theme.ColorHelper;
import com.mentalfrostbyte.jello.util.client.render.ResourceRegistry;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil2;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.system.math.smoothing.EasingFunctions;
import com.mentalfrostbyte.jello.util.system.math.smoothing.QuadraticEasing;
import com.mentalfrostbyte.jello.util.client.render.FontSizeAdjust;
import net.minecraft.client.gui.screen.Screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ModsPanel extends Element {
   public Animation field21302;
   public int field21303;
   public int field21304;
   public int field21305;
   public int field21306;
   public String field21307;
   public ScrollableContentPanel field21308;
   public Class6984 field21309;
   public boolean field21311 = false;
   private final List<Class7875> field21312 = new ArrayList<>();

   public ModsPanel(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6, false);
      this.field21305 = 500;
      this.field21306 = 600;
      this.field21304 = (var5 - this.field21305) / 2;
      this.field21303 = (var6 - this.field21306) / 2;
      TextField var10;
      this.addToList(
         var10 = new TextField(
            this, "search", this.field21304 + 30, this.field21303 + 30 + 50, this.field21305 - 30 * 2, 60, TextField.field20741, "", "Search..."
         )
      );
      var10.addChangeListener(var2x -> {
         this.field21307 = var10.getText();
         this.field21308.method13512(0);
      });
      var10.method13242();
      this.addToList(
         this.field21308 = new ScrollableContentPanel(
            this, "mods", this.field21304 + 30, this.field21303 + 30 + 120, this.field21305 - 30 * 2, this.field21306 - 30 * 2 - 120
         )
      );
      int var11 = 10;

      for (Entry var13 : GuiManager.screenToScreenName.entrySet()) {
         Class6984 var14 = new Class6984((Class<? extends Screen>)var13.getKey());
         ColorHelper var15 = new ColorHelper(RenderUtil2.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.02F), -986896)
            .setTextColor(RenderUtil2.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.5F))
            .method19412(FontSizeAdjust.NEGATE_AND_DIVIDE_BY_2);
         Button var16;
         this.field21308
            .addToList(
               var16 = new Button(this.field21308, var14.method21596(), 0, var11++ * 55, this.field21308.getWidthA(), 55, var15, var14.method21596())
            );
         var16.onClick((var2x, var3x) -> {
            for (Entry var7 : GuiManager.screenToScreenName.entrySet()) {
               Class6984 var8 = new Class6984((Class<? extends Screen>)var7.getKey());
               if (var8.method21596().equals(var16.getName()) && !this.field21311) {
                  this.field21309 = var8;
                  this.field21311 = true;
                  break;
               }
            }
         });
      }

      var11 += 50;

      for (Module var19 : Client.getInstance().moduleManager.getModuleMap().values()) {
         ColorHelper var20 = new ColorHelper(16777215, -986896).setTextColor(ClientColors.DEEP_TEAL.getColor()).method19412(FontSizeAdjust.field14488);
         Button var21;
         this.field21308
            .addToList(
               var21 = new Button(
                  this.field21308, var19.getName(), 0, var11++ * 40, this.field21308.getWidthA(), 40, var20, new Class6984(var19).method21596()
               )
            );
         var21.method13034(10);
         var21.onClick((var2x, var3x) -> {
            for (Module var7 : Client.getInstance().moduleManager.getModuleMap().values()) {
               if (var7.getName().equals(var21.getText()) && !this.field21311) {
                  this.field21309 = new Class6984(var7);
                  this.field21311 = true;
                  break;
               }
            }
         });
      }

      this.field21302 = new Animation(200, 120);
      this.setListening(false);
   }

   @Override
   public void updatePanelDimensions(int newHeight, int newWidth) {
      if (this.method13212()
         && (newHeight < this.field21304 || newWidth < this.field21303 || newHeight > this.field21304 + this.field21305 || newWidth > this.field21303 + this.field21306)) {
         this.field21311 = true;
      }

      this.field21302.changeDirection(this.field21311 ? Animation.Direction.BACKWARDS : Animation.Direction.FORWARDS);
      Map<String, Button> var5 = new TreeMap();
      Map<String, Button> var6 = new TreeMap();
      Map<String, Button> var7 = new TreeMap();
      List<Button> var8 = new ArrayList();

      for (CustomGuiScreen var10 : this.field21308.getChildren()) {
         if (!(var10 instanceof VerticalScrollBar)) {
            for (CustomGuiScreen var12 : var10.getChildren()) {
               if (var12 instanceof Button var13) {
				   boolean var14 = var13.getHeightA() != 40;
                  if (!var14 || this.field21307 != null && (this.field21307 == null || this.field21307.length() != 0)) {
                     if (!var14 && this.method13622(this.field21307, var13.getText())) {
                        var6.put(var13.getText(), var13);
                     } else if (!var14 && this.method13621(this.field21307, var13.getText())) {
                        var7.put(var13.getText(), var13);
                     } else {
                        var8.add(var13);
                     }
                  } else {
                     var5.put(var13.getText(), var13);
                  }
               }
            }
         }
      }

      int var15 = var5.size() <= 0 ? 0 : 10;

      for (Button var20 : var5.values()) {
         var20.setSelfVisible(true);
         var20.setYA(var15);
         var15 += var20.getHeightA();
      }

      if (var5.size() > 0) {
         var15 += 10;
      }

      for (Button var21 : var6.values()) {
         var21.setSelfVisible(true);
         var21.setYA(var15);
         var15 += var21.getHeightA();
      }

      for (Button var22 : var7.values()) {
         var22.setSelfVisible(true);
         var22.setYA(var15);
         var15 += var22.getHeightA();
      }

      for (Button var23 : var8) {
         var23.setSelfVisible(false);
      }

      super.updatePanelDimensions(newHeight, newWidth);
   }

   private boolean method13621(String var1, String var2) {
      return var1 == null || var1 == "" || var2 == null || var2.toLowerCase().contains(var1.toLowerCase());
   }

   private boolean method13622(String var1, String var2) {
      return var1 == null || var1 == "" || var2 == null || var2.toLowerCase().startsWith(var1.toLowerCase());
   }

   @Override
   public void draw(float partialTicks) {
      partialTicks = this.field21302.calcPercent();
      float var4 = EasingFunctions.easeOutBack(partialTicks, 0.0F, 1.0F, 1.0F);
      if (this.field21311) {
         var4 = QuadraticEasing.easeOutQuad(partialTicks, 0.0F, 1.0F, 1.0F);
      }

      this.method13279(0.8F + var4 * 0.2F, 0.8F + var4 * 0.2F);
      if (partialTicks == 0.0F && this.field21311) {
         this.method13624(this.field21309);
      }

      RenderUtil.drawColoredRect(
         (float)this.xA,
         (float)this.yA,
         (float)this.widthA,
         (float)this.heightA,
              RenderUtil2.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.3F * partialTicks)
      );
      super.method13224();
      RenderUtil.drawRoundedRect(
         (float)this.field21304,
         (float)this.field21303,
         (float)this.field21305,
         (float)this.field21306,
         10.0F,
              RenderUtil2.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), partialTicks)
      );
      RenderUtil.drawString(
         ResourceRegistry.JelloLightFont36,
         (float)(30 + this.field21304),
         (float)(30 + this.field21303),
         "Select mod to bind",
              RenderUtil2.applyAlpha(ClientColors.DEEP_TEAL.getColor(), partialTicks * 0.7F)
      );
      super.draw(partialTicks);
   }

   public final void method13623(Class7875 var1) {
      this.field21312.add(var1);
   }

   public final void method13624(Class6984 var1) {
      for (Class7875 var5 : this.field21312) {
         var5.method26411(this, var1);
      }
   }
}
