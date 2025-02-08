package com.mentalfrostbyte.jello.gui.impl.jello.buttons;

import com.mentalfrostbyte.jello.gui.combined.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.unmapped.AnimatedIconPanel;
import com.mentalfrostbyte.jello.gui.impl.others.Class8906;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.client.render.theme.ColorHelper;
import com.mentalfrostbyte.jello.util.client.render.ResourceRegistry;
import com.mentalfrostbyte.jello.util.system.math.counter.TimerUtil;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil2;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.client.render.FontSizeAdjust;
import org.newdawn.slick.TrueTypeFont;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.InputMappings;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class TextField extends AnimatedIconPanel {
   public static final ColorHelper field20741 = new ColorHelper(
      -892679478, -892679478, -892679478, ClientColors.DEEP_TEAL.getColor(), FontSizeAdjust.field14488, FontSizeAdjust.NEGATE_AND_DIVIDE_BY_2
   );
   public static final ColorHelper field20742 = new ColorHelper(-1, -1, -1, ClientColors.LIGHT_GREYISH_BLUE.getColor(), FontSizeAdjust.field14488, FontSizeAdjust.NEGATE_AND_DIVIDE_BY_2);
   private String placeholder = "";
   private float field20744;
   private final float field20745 = 2.0F;
   private float field20746;
   private float field20747;
   private final float field20748 = 2.0F;
   private int maxLen;
   private int field20750;
   private int field20751;
   private boolean field20752;
   private boolean field20753;
   private boolean censorText = false;
   private String censorChar = Character.toString('·');
   private TimerUtil timer = new TimerUtil();
   private final List<Class7902> field20757 = new ArrayList<Class7902>();
   private boolean field20758 = true;

   public TextField(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6, field20741, "", false);
      this.timer.start();
   }

   public TextField(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6, ColorHelper var7) {
      super(var1, var2, var3, var4, var5, var6, var7, "", false);
      this.timer.start();
   }

   public TextField(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6, ColorHelper var7, String var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8, false);
      this.timer.start();
   }

   public TextField(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6, ColorHelper var7, String var8, String placeholder) {
      super(var1, var2, var3, var4, var5, var6, var7, var8, ResourceRegistry.JelloLightFont25, false);
      this.placeholder = placeholder;
      this.timer.start();
   }

   public TextField(CustomGuiScreen screen, String id, int x, int y, int width, int height, ColorHelper color, String text, String placeholder, TrueTypeFont _font) {
      super(screen, id, x, y, width, height, color, text, false);
      this.placeholder = placeholder;
      this.timer.start();
   }

   @Override
   public void updatePanelDimensions(int newHeight, int newWidth) {
      super.updatePanelDimensions(newHeight, newWidth);
      String text = this.typedText;
      if (this.censorText) {
         text = this.typedText.replaceAll(".", this.censorChar);
      }

      this.field20744 = this.field20744 + ((!this.field20905 ? 0.0F : 1.0F) - this.field20744) / 2.0F;
      if (this.field20905) {
         if (this.field20752) {
            this.maxLen = Class8906.getStringLen(text, this.font, (float)this.method13271(), newHeight, this.field20746);
         }
      } else {
         this.maxLen = 0;
         this.field20750 = 0;
         this.field20747 = 0.0F;
      }

      this.maxLen = Math.min(Math.max(0, this.maxLen), text.length());
      this.field20751 = this.maxLen;
   }

   public void method13146() {
      this.field20746 = 0.0F;
   }

   public void method13147(String var1) {
      this.censorChar = var1;
   }

   @Override
   public boolean onClick(int mouseX, int mouseY, int mouseButton) {
      if (!super.onClick(mouseX, mouseY, mouseButton)) {
         String var6 = this.typedText;
         if (this.censorText) {
            var6 = this.typedText.replaceAll(".", this.censorChar);
         }

         this.field20752 = true;
         this.maxLen = Class8906.getStringLen(var6, this.font, (float)this.method13271(), mouseX, this.field20746);
         if (!InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 340)
            && !InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 344)) {
            this.field20750 = this.maxLen;
         }

         return false;
      } else {
         return true;
      }
   }

   public void method13148() {
      this.method13242();
      this.maxLen = this.typedText.length();
      this.field20750 = 0;
      this.field20751 = this.maxLen;
   }

   @Override
   public void onClick2(int mouseX, int mouseY, int mouseButton) {
      super.onClick2(mouseX, mouseY, mouseButton);
      this.field20752 = false;
   }

   @Override
   public void keyPressed(int keyCode) {
      super.keyPressed(keyCode);
      if (this.field20905) {
         switch (keyCode) {
            case 65:
               if (this.method13149()) {
                  this.maxLen = this.typedText.length();
                  this.field20750 = 0;
                  this.field20751 = this.maxLen;
               }
               break;
            case 67:
               if (this.method13149() && this.field20750 != this.field20751) {
                  GLFW.glfwSetClipboardString(
                     Minecraft.getInstance().getMainWindow().getHandle(),
                     this.typedText.substring(Math.min(this.field20750, this.field20751), Math.max(this.field20750, this.field20751))
                  );
               }
               break;
            case 86:
               if (this.method13149()) {
                  String var12 = "";

                  try {
                     var12 = GLFW.glfwGetClipboardString(Minecraft.getInstance().getMainWindow().getHandle());
                     if (var12 == null) {
                        var12 = "";
                     }
                  } catch (Exception var7) {
                  }

                  if (var12 != "") {
                     if (this.field20750 != this.field20751) {
                        this.typedText = Class8906.method32493(this.typedText, var12, this.field20750, this.field20751);
                        if (this.maxLen > this.field20750) {
                           this.maxLen = this.maxLen - (Math.max(this.field20750, this.field20751) - Math.min(this.field20750, this.field20751));
                        }

                        this.maxLen = this.maxLen + var12.length();
                        this.field20750 = this.maxLen;
                     } else {
                        this.typedText = Class8906.method32492(this.typedText, var12, this.maxLen);
                        this.maxLen = this.maxLen + var12.length();
                        this.field20750 = this.maxLen;
                     }

                     this.method13152();
                  }
               }
               break;
            case 88:
               if (this.method13149() && this.field20750 != this.field20751) {
                  GLFW.glfwSetClipboardString(
                     Minecraft.getInstance().getMainWindow().getHandle(),
                     this.typedText.substring(Math.min(this.field20750, this.field20751), Math.max(this.field20750, this.field20751))
                  );
                  this.typedText = Class8906.method32493(this.typedText, "", this.field20750, this.field20751);
                  if (this.maxLen > this.field20750) {
                     this.maxLen = this.maxLen - (Math.max(this.field20750, this.field20751) - Math.min(this.field20750, this.field20751));
                  }

                  this.field20750 = this.maxLen;
                  this.field20751 = this.maxLen;
                  this.method13152();
               }
               break;
            case 256:
               this.method13145(false);
               break;
            case 259:
               if (this.typedText.length() > 0) {
                  if (this.field20750 != this.field20751) {
                     this.typedText = Class8906.method32493(this.typedText, "", this.field20750, this.field20751);
                     if (this.maxLen > this.field20750) {
                        this.maxLen = this.maxLen - (Math.max(this.field20750, this.field20751) - Math.min(this.field20750, this.field20751));
                     }
                  } else if (this.method13149()) {
                     int var11 = -1;

                     for (int var14 = Math.max(this.maxLen - 1, 0); var14 >= 0; var14--) {
                        if ((String.valueOf(this.typedText.charAt(var14)).equalsIgnoreCase(" ") || var14 == 0) && Math.abs(this.maxLen - var14) > 1) {
                           var11 = var14 + (var14 == 0 ? 0 : 1);
                           break;
                        }
                     }

                     if (var11 != -1) {
                        this.typedText = Class8906.method32493(this.typedText, "", var11, this.maxLen);
                        this.maxLen = var11;
                     }
                  } else {
                     this.typedText = Class8906.method32493(this.typedText, "", this.maxLen - 1, this.maxLen);
                     this.maxLen--;
                  }

                  this.method13152();
               }

               this.field20750 = this.maxLen;
               break;
            case 262:
               if (!this.method13149()) {
                  this.maxLen++;
               } else {
                  int var10 = -1;

                  for (int var13 = this.maxLen; var13 < this.typedText.length(); var13++) {
                     try {
                        if ((String.valueOf(this.typedText.charAt(var13)).equalsIgnoreCase(" ") || var13 == this.typedText.length() - 1)
                           && (Math.abs(this.maxLen - var13) > 1 || var13 == this.typedText.length() - 1)) {
                           var10 = var13 + 1;
                           break;
                        }
                     } catch (Exception var9) {
                        break;
                     }
                  }

                  if (var10 != -1) {
                     this.maxLen = var10;
                  }
               }

               if (!InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 340)
                  && !InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 344)) {
                  this.field20750 = this.maxLen;
               }
               break;
            case 263:
               if (!this.method13149()) {
                  this.maxLen--;
               } else {
                  int var4 = -1;

                  for (int var5 = Math.max(this.maxLen - 1, 0); var5 >= 0; var5--) {
                     try {
                        if ((String.valueOf(this.typedText.charAt(var5)).equalsIgnoreCase(" ") || var5 == 0) && Math.abs(this.maxLen - var5) > 1) {
                           var4 = var5;
                           break;
                        }
                     } catch (Exception var8) {
                        break;
                     }
                  }

                  if (var4 != -1) {
                     this.maxLen = var4;
                  }
               }

               if (!InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 340)
                  && !InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 344)) {
                  this.field20750 = this.maxLen;
               }
               break;
            case 268:
               this.maxLen = 0;
               if (!InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 340)
                  && !InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 344)) {
                  this.field20750 = this.maxLen;
               }
               break;
            case 269:
               this.maxLen = this.typedText.length();
               if (!InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 340)
                  && !InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 344)) {
                  this.field20750 = this.maxLen;
               }
         }
      }
   }

   public boolean method13149() {
      return InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 341)
         || InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 345)
         || InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 343);
   }

   @Override
   public void charTyped(char typed) {
      super.charTyped(typed);
      if (this.method13297() && Class8906.method32486(typed)) {
         if (this.field20750 == this.field20751) {
            this.typedText = Class8906.method32492(this.typedText, Character.toString(typed), this.maxLen);
         } else {
            this.typedText = Class8906.method32493(this.typedText, Character.toString(typed), this.field20750, this.field20751);
         }

         this.maxLen++;
         this.field20750 = this.maxLen;
         this.method13152();
      }
   }

   @Override
   public void draw(float partialTicks) {
      this.method13225();
      float var4 = 1000.0F;
      boolean var5 = !this.field20905 ? false : (float)this.timer.getElapsedTime() > var4 / 2.0F;
      if ((float)this.timer.getElapsedTime() > var4) {
         this.timer.reset();
      }

      String var6 = this.typedText;
      if (this.censorText) {
         var6 = this.typedText.replaceAll(".", this.censorChar);
      }

      RenderUtil.drawBlurredBackground(this.getXA(), this.getYA(), this.getXA() + this.widthA, this.getYA() + this.heightA, true);
      int var7 = this.xA + 4;
      int var8 = this.widthA - 4;
      float var9 = (float)var7 + this.field20746 + (float)this.font.getWidth(var6.substring(0, this.maxLen));
      if (this.method13297()) {
         RenderUtil.drawRoundedRect(
            var9 + (float)(var6.isEmpty() ? 0 : -1),
            (float)(this.yA + this.heightA / 2 - this.font.getHeight(var6) / 2 + 2),
            var9 + (float)(var6.isEmpty() ? 1 : 0),
            (float)(this.yA + this.heightA / 2 + this.font.getHeight(var6) / 2 - 1),
            RenderUtil2.applyAlpha(this.textColor.getTextColor(), !var5 ? 0.1F * partialTicks : 0.8F)
         );
         float var10 = (float)(var7 + this.font.getWidth(var6.substring(0, this.maxLen))) + this.field20747;
         if (var10 < (float)var7) {
            this.field20747 += (float)var7 - var10;
            this.field20747 = this.field20747 - Math.min((float)var8, this.field20747);
         }

         if (var10 > (float)(var7 + var8)) {
            this.field20747 += (float)(var7 + var8) - var10;
         }
      }

      this.field20746 = this.field20746 + (this.field20747 - this.field20746) / 2.0F;
      this.field20750 = Math.min(Math.max(0, this.field20750), var6.length());
      this.field20751 = Math.min(Math.max(0, this.field20751), var6.length());
      float var14 = (float)var7 + this.field20746 + (float)this.font.getWidth(var6.substring(0, this.field20750));
      float var11 = (float)var7 + this.field20746 + (float)this.font.getWidth(var6.substring(0, this.field20751));
      RenderUtil.drawRoundedRect(
         var14,
         (float)(this.yA + this.heightA / 2 - this.font.getHeight(var6) / 2),
         var11,
         (float)(this.yA + this.heightA / 2 + this.font.getHeight(var6) / 2),
         RenderUtil2.applyAlpha(-5516546, partialTicks)
      );
      FontSizeAdjust var12 = this.textColor.method19411();
      FontSizeAdjust var13 = this.textColor.method19413();
      RenderUtil.drawString(
         this.font,
         (float)var7 + this.field20746,
         (float)(this.yA + this.heightA / 2),
         var6.length() == 0 && (!this.field20905 || var6.length() <= 0) ? this.placeholder : var6,
         RenderUtil2.applyAlpha(this.textColor.getTextColor(), (this.field20744 / 2.0F + 0.4F) * partialTicks * (this.field20905 && var6.length() > 0 ? 1.0F : 0.5F)),
         var12,
         var13
      );
      RenderUtil.endScissor();
      if (this.field20758) {
         RenderUtil.drawRoundedRect(
            (float)this.xA,
            (float)(this.yA + this.heightA - 2),
            (float)(this.xA + this.widthA),
            (float)(this.yA + this.heightA),
                 RenderUtil2.applyAlpha(this.textColor.getPrimaryColor(), (this.field20744 / 2.0F + 0.5F) * partialTicks)
         );
      }

      super.draw(partialTicks);
   }

   public final void method13151(Class7902 var1) {
      this.field20757.add(var1);
   }

   public void method13152() {
      for (Class7902 var4 : this.field20757) {
         var4.method26474(this);
      }
   }

   public String method13153() {
      return this.placeholder;
   }

   public void method13154(String var1) {
      this.placeholder = var1;
   }

   public void method13155(boolean var1) {
      this.censorText = var1;
   }

   public void method13156(boolean var1) {
      this.field20758 = var1;
   }

    public static interface Class7902 {
       void method26474(TextField var1);
    }
}
