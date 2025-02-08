package com.mentalfrostbyte.jello.gui.unmapped;

import com.mentalfrostbyte.jello.gui.impl.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.base.interfaces.IWidthSetter;
import net.minecraft.client.Minecraft;

public class CustomGuiScreenWidthSetter implements IWidthSetter {
   @Override
   public void setWidth(CustomGuiScreen forScreen, CustomGuiScreen fromWidthOfThisScreen) {
      forScreen.setXA(0);
      if (fromWidthOfThisScreen == null) {
         forScreen.setWidthA(Minecraft.getInstance().getMainWindow().getWidth());
      } else {
         forScreen.setWidthA(fromWidthOfThisScreen.getWidthA());
      }
   }
}
