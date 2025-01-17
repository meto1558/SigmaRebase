package com.mentalfrostbyte.jello.gui.unmapped;

public interface IGuiEventListener {
   void charTyped(char var1);

   void keyPressed(int var1);

   boolean onClick(int mouseX, int mouseY, int mouseButton);

   void onClick2(int mouseX, int mouseY, int mouseButton);

   void voidEvent2(int var1, int var2, int var3);

   void voidEvent3(float var1);
}
