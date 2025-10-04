package com.mentalfrostbyte.jello.gui.impl.jello.buttons;

import com.mentalfrostbyte.jello.gui.combined.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.combined.AnimatedIconPanel;
import com.mentalfrostbyte.jello.gui.impl.others.ChatUtil;
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

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT;

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
   private int startSelect;
   private int endSelect;
   private boolean field20752;
   private boolean field20753;
   private boolean censorText = false;
   private String censorChar = Character.toString('·');
   private final TimerUtil timer = new TimerUtil();
   private final List<ChangeListener> changeListeners = new ArrayList<ChangeListener>();
   private boolean roundedThingy = true;

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
      String text = this.text;
      if (this.censorText) {
         text = this.text.replaceAll(".", this.censorChar);
      }

      this.field20744 = this.field20744 + ((!this.focused ? 0.0F : 1.0F) - this.field20744) / 2.0F;
      if (this.focused) {
         if (this.field20752) {
            this.maxLen = ChatUtil.getStringLen(text, this.font, (float)this.method13271(), newHeight, this.field20746);
         }
      } else {
         this.maxLen = 0;
         this.startSelect = 0;
         this.field20747 = 0.0F;
      }

      this.maxLen = Math.min(Math.max(0, this.maxLen), text.length());
      this.endSelect = this.maxLen;
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
         String var6 = this.text;
         if (this.censorText) {
            var6 = this.text.replaceAll(".", this.censorChar);
         }

         this.field20752 = true;
         this.maxLen = ChatUtil.getStringLen(var6, this.font, (float)this.method13271(), mouseX, this.field20746);
         if (!InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 340)
            && !InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 344)) {
            this.startSelect = this.maxLen;
         }

         return false;
      } else {
         return true;
      }
   }

   public void method13148() {
      this.method13242();
      this.maxLen = this.text.length();
      this.startSelect = 0;
      this.endSelect = this.maxLen;
   }

   @Override
   public void onClick2(int mouseX, int mouseY, int mouseButton) {
      super.onClick2(mouseX, mouseY, mouseButton);
      this.field20752 = false;
   }

   @Override
   public void keyPressed(int keyCode) {
      super.keyPressed(keyCode);
      if (this.focused) {
         switch (keyCode) {
            case GLFW.GLFW_KEY_A:
               if (this.isModifierKeyPressed()) {
                  this.maxLen = this.text.length();
                  this.startSelect = 0;
                  this.endSelect = this.maxLen;
               }
               break;
            case GLFW.GLFW_KEY_C:
               if (this.isModifierKeyPressed() && this.startSelect != this.endSelect) {
                  GLFW.glfwSetClipboardString(
                     Minecraft.getInstance().getMainWindow().getHandle(),
                     this.text.substring(Math.min(this.startSelect, this.endSelect), Math.max(this.startSelect, this.endSelect))
                  );
               }
               break;
            case GLFW.GLFW_KEY_V:
               if (!this.isModifierKeyPressed()) break;
               String clip = "";

               try {
                  clip = GLFW.glfwGetClipboardString(Minecraft.getInstance().getMainWindow().getHandle());
                  if (clip == null) {
                     clip = "";
                  }
               } catch (Exception ignored) {
               }

               if (!clip.isEmpty()) {
                  if (this.startSelect != this.endSelect) {
                     this.text = ChatUtil.cut(this.text, clip, this.startSelect, this.endSelect);
                     if (this.maxLen > this.startSelect) {
                        this.maxLen = this.maxLen - (Math.max(this.startSelect, this.endSelect) - Math.min(this.startSelect, this.endSelect));
                     }

				  } else {
                     this.text = ChatUtil.paste(this.text, clip, this.maxLen);
				  }
				   this.maxLen = this.maxLen + clip.length();
				   this.startSelect = this.maxLen;

				   this.callChangeListeners();
               }
               break;
            case GLFW.GLFW_KEY_X:
               if (this.isModifierKeyPressed() && this.startSelect != this.endSelect) {
                  GLFW.glfwSetClipboardString(
                     Minecraft.getInstance().getMainWindow().getHandle(),
                     this.text.substring(Math.min(this.startSelect, this.endSelect), Math.max(this.startSelect, this.endSelect))
                  );
                  this.text = ChatUtil.cut(this.text, "", this.startSelect, this.endSelect);
                  if (this.maxLen > this.startSelect) {
                     this.maxLen = this.maxLen - (Math.max(this.startSelect, this.endSelect) - Math.min(this.startSelect, this.endSelect));
                  }

                  this.startSelect = this.maxLen;
                  this.endSelect = this.maxLen;
                  this.callChangeListeners();
               }
               break;
            case GLFW.GLFW_KEY_ESCAPE:
               this.setFocused(false);
               break;
            case GLFW.GLFW_KEY_BACKSPACE:
               if (!this.text.isEmpty()) {
                  if (this.startSelect != this.endSelect) {
                     this.text = ChatUtil.cut(this.text, "", this.startSelect, this.endSelect);
                     if (this.maxLen > this.startSelect) {
                        this.maxLen = this.maxLen - (Math.max(this.startSelect, this.endSelect) - Math.min(this.startSelect, this.endSelect));
                     }
                  } else if (this.isModifierKeyPressed()) {
                     int var11 = -1;

                     for (int var14 = Math.max(this.maxLen - 1, 0); var14 >= 0; var14--) {
                        if ((String.valueOf(this.text.charAt(var14)).equalsIgnoreCase(" ") || var14 == 0) && Math.abs(this.maxLen - var14) > 1) {
                           var11 = var14 + (var14 == 0 ? 0 : 1);
                           break;
                        }
                     }

                     if (var11 != -1) {
                        this.text = ChatUtil.cut(this.text, "", var11, this.maxLen);
                        this.maxLen = var11;
                     }
                  } else {
                     this.text = ChatUtil.cut(this.text, "", this.maxLen - 1, this.maxLen);
                     this.maxLen--;
                  }

                  this.callChangeListeners();
               }

               this.startSelect = this.maxLen;
               break;
            case GLFW.GLFW_KEY_RIGHT:
               if (!this.isModifierKeyPressed()) {
                  this.maxLen++;
               } else {
                  int previousIdx = -1;

                  for (int i = this.maxLen; i < this.text.length(); i++) {
                     try {
                        if ((String.valueOf(this.text.charAt(i)).equalsIgnoreCase(" ") || i == this.text.length() - 1)
                           && (Math.abs(this.maxLen - i) > 1 || i == this.text.length() - 1)) {
                           previousIdx = i + 1;
                           break;
                        }
                     } catch (Exception ignored) {
                        break;
                     }
                  }

                  if (previousIdx != -1) {
                     this.maxLen = previousIdx;
                  }
               }

               if (!InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 340)
                  && !InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 344)) {
                  this.startSelect = this.maxLen;
               }
               break;
            case GLFW.GLFW_KEY_LEFT:
               if (!this.isModifierKeyPressed()) {
                  this.maxLen--;
               } else {
                  int previousIdx = -1;

                  for (int i = Math.max(this.maxLen - 1, 0); i >= 0; i--) {
                     try {
                        if ((String.valueOf(this.text.charAt(i)).equalsIgnoreCase(" ") || i == 0) && Math.abs(this.maxLen - i) > 1) {
                           previousIdx = i;
                           break;
                        }
                     } catch (Exception var8) {
                        break;
                     }
                  }

                  if (previousIdx != -1) {
                     this.maxLen = previousIdx;
                  }
               }

               if (!InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), GLFW_KEY_LEFT_SHIFT)
                  && !InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), GLFW_KEY_RIGHT_SHIFT)) {
                  this.startSelect = this.maxLen;
               }
               break;
            case GLFW.GLFW_KEY_HOME:
               this.maxLen = 0;
               if (!InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), GLFW_KEY_LEFT_SHIFT)
                  && !InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), GLFW_KEY_RIGHT_SHIFT)) {
                  this.startSelect = this.maxLen;
               }
               break;
            case GLFW.GLFW_KEY_END:
               this.maxLen = this.text.length();
               if (!InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), GLFW_KEY_LEFT_SHIFT)
                  && !InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), GLFW_KEY_RIGHT_SHIFT)) {
                  this.startSelect = this.maxLen;
               }
         }
      }
   }

   public boolean isModifierKeyPressed() {
      long handle = Minecraft.getInstance().getMainWindow().getHandle();
      return InputMappings.isKeyDown(handle, GLFW.GLFW_KEY_LEFT_CONTROL)
         || InputMappings.isKeyDown(handle, GLFW.GLFW_KEY_RIGHT_CONTROL)
         || InputMappings.isKeyDown(handle, GLFW.GLFW_KEY_LEFT_SUPER);
   }

   @Override
   public void charTyped(char typed) {
      super.charTyped(typed);
      if (this.isFocused() && ChatUtil.method32486(typed)) {
         if (this.startSelect == this.endSelect) {
            this.text = ChatUtil.paste(this.text, Character.toString(typed), this.maxLen);
         } else {
            this.text = ChatUtil.cut(this.text, Character.toString(typed), this.startSelect, this.endSelect);
         }

         this.maxLen++;
         this.startSelect = this.maxLen;
         this.callChangeListeners();
      }
   }

   @Override
   public void draw(float partialTicks) {
      this.method13225();
      float var4 = 1000.0F;
      boolean var5 = this.focused && (float) this.timer.getElapsedTime() > var4 / 2.0F;
      if ((float)this.timer.getElapsedTime() > var4) {
         this.timer.reset();
      }

      String var6 = this.text;
      if (this.censorText) {
         var6 = this.text.replaceAll(".", this.censorChar);
      }

      RenderUtil.startScissor(this.getXA(), this.getYA(), this.getXA() + this.widthA, this.getYA() + this.heightA, true);
      int var7 = this.xA + 4;
      int var8 = this.widthA - 4;
      float var9 = (float)var7 + this.field20746 + (float)this.font.getWidth(var6.substring(0, this.maxLen));
      if (this.isFocused()) {
         RenderUtil.drawColoredRect(
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
      this.startSelect = Math.min(Math.max(0, this.startSelect), var6.length());
      this.endSelect = Math.min(Math.max(0, this.endSelect), var6.length());
      float var14 = (float)var7 + this.field20746 + (float)this.font.getWidth(var6.substring(0, this.startSelect));
      float var11 = (float)var7 + this.field20746 + (float)this.font.getWidth(var6.substring(0, this.endSelect));
      RenderUtil.drawColoredRect(
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
         var6.length() == 0 && (!this.focused || var6.length() <= 0) ? this.placeholder : var6,
         RenderUtil2.applyAlpha(this.textColor.getTextColor(), (this.field20744 / 2.0F + 0.4F) * partialTicks * (this.focused && var6.length() > 0 ? 1.0F : 0.5F)),
         var12,
         var13
      );
      RenderUtil.endScissor();
      if (this.roundedThingy) {
         RenderUtil.drawColoredRect(
            (float)this.xA,
            (float)(this.yA + this.heightA - 2),
            (float)(this.xA + this.widthA),
            (float)(this.yA + this.heightA),
                 RenderUtil2.applyAlpha(this.textColor.getPrimaryColor(), (this.field20744 / 2.0F + 0.5F) * partialTicks)
         );
      }

      super.draw(partialTicks);
   }

   public final void addChangeListener(ChangeListener listener) {
      this.changeListeners.add(listener);
   }

   public void callChangeListeners() {
      for (ChangeListener listener : this.changeListeners) {
         listener.onUpdate(this);
      }
   }

   public String getPlaceholder() {
      return this.placeholder;
   }

   public void setPlaceholder(String placeholder) {
      this.placeholder = placeholder;
   }

   public void setCensorText(boolean censorText) {
      this.censorText = censorText;
   }

   public void setRoundedThingy(boolean roundedThingy) {
      this.roundedThingy = roundedThingy;
   }

    public interface ChangeListener {
       void onUpdate(TextField var1);
    }
}
