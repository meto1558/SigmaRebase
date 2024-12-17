package com.mentalfrostbyte.jello.gui.unmapped;

import com.mentalfrostbyte.jello.gui.base.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.impl.JelloClickGUI;
import net.minecraft.client.Minecraft;

public class ClickGUIBlurOverlay extends AnimatedIconPanelWrap {
   private static String[] field21277;
   public final JelloClickGUI field21278;

   public ClickGUIBlurOverlay(JelloClickGUI var1, CustomGuiScreen var2, String var3) {
      super(var2, var3, 0, 0, Minecraft.getInstance().getMainWindow().getWidth(), Minecraft.getInstance().getMainWindow().getHeight(), false);
      this.field21278 = var1;
   }

   @Override
   public void method13089(int var1, int var2, int var3) {
      super.method13089(var1, var2, var3);
   }
}
