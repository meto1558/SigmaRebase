package com.mentalfrostbyte.jello.gui.impl.others.buttons;

import com.mentalfrostbyte.jello.gui.base.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.unmapped.AnimatedIconPanelWrap;
import com.mentalfrostbyte.jello.util.client.ClientColors;
import com.mentalfrostbyte.jello.util.render.ColorUtils;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import org.newdawn.slick.opengl.Texture;

public class ImageQ extends AnimatedIconPanelWrap {
   private final Texture image;

   public ImageQ(CustomGuiScreen screen, String text, int var3, int var4, int var5, int var6, Texture image) {
      super(screen, text, var3, var4, var5, var6, false);
      this.image = image;
   }

   @Override
   public void draw(float partialTicks) {
      RenderUtil.drawImage(
         (float)this.xA,
         (float)this.yA,
         (float)this.widthA,
         (float)this.heightA,
         this.image,
         ColorUtils.shiftTowardsOther(
            ClientColors.DEEP_TEAL.getColor(), ClientColors.LIGHT_GREYISH_BLUE.getColor(), !this.method13298() ? 0.0F : (!this.method13212() ? 0.15F : 0.3F)
         )
      );
      super.draw(partialTicks);
   }
}
