package com.mentalfrostbyte.jello.gui.impl.jello.mainmenu;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.gui.base.Animation;
import com.mentalfrostbyte.jello.gui.base.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.unmapped.UIButton;
import com.mentalfrostbyte.jello.gui.impl.jello.buttons.TextField;
import com.mentalfrostbyte.jello.managers.util.account.Class9507;
import com.mentalfrostbyte.jello.util.client.ClientColors;
import com.mentalfrostbyte.jello.util.client.ColorHelper;
import com.mentalfrostbyte.jello.util.system.math.MathUtils;
import com.mentalfrostbyte.jello.util.client.render.ResourceRegistry;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil2;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import totalcross.json.JSONArray;

public class RedeemKeyScreen extends CustomGuiScreen {
   public String field21135 = "";
   public Animation field21136 = new Animation(380, 200, Animation.Direction.BACKWARDS);
   private static JSONArray field21137;
   private TextField field21138;

   public RedeemKeyScreen(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
      this.setListening(false);
      TextField var9;
      this.addToList(var9 = new TextField(this, "redeemBox", 100, 200, 350, 50, TextField.field20742, "", "Premium Code"));
      UIButton var10;
      this.addToList(
         var10 = new UIButton(
            this,
            "redeembtn",
            100,
            290,
            80,
            30,
            new ColorHelper(ClientColors.LIGHT_GREYISH_BLUE.getColor(), ClientColors.LIGHT_GREYISH_BLUE.getColor(), ClientColors.LIGHT_GREYISH_BLUE.getColor()),
            "Redeem",
            ResourceRegistry.JelloLightFont20
         )
      );
      this.addToList(this.field21138 = new TextField(this, "captcha", 195, 290, 75, 35, TextField.field20742, "", "Captcha"));
      this.field21138.setFont(ResourceRegistry.JelloLightFont18);
      this.field21138.setEnabled(false);
      var10.doThis((var2x, var3x) -> new Thread(() -> {
            Class9507 var4x = Client.getInstance().networkManager.method30452();
            if (var4x != null) {
               var4x.method36706(this.field21138.getTypedText());
            }

            this.field21135 = Client.getInstance().networkManager.redeemPremium(var9.getTypedText(), Client.getInstance().networkManager.method30452());
            if (this.field21135 == null) {
               this.field21135 = "";
            }

            if (Client.getInstance().networkManager.isPremium()) {
               this.runThisOnDimensionUpdate(() -> ((MainMenuScreen)this.getParent()).goOut());
            }
         }).start());
   }

   @Override
   public void draw(float partialTicks) {
      this.field21136.changeDirection(!this.isHovered() ? Animation.Direction.BACKWARDS : Animation.Direction.FORWARDS);
      partialTicks = 1.0F;
      partialTicks *= this.field21136.calcPercent();
      float var4 = MathUtils.lerp(this.field21136.calcPercent(), 0.17, 1.0, 0.51, 1.0);
      if (this.field21136.getDirection() == Animation.Direction.BACKWARDS) {
         var4 = 1.0F;
      }

      this.drawBackground((int)(150.0F * (1.0F - var4)));
      this.method13225();
      Class9507 var5 = Client.getInstance().networkManager.method30452();
      if (var5 != null) {
         this.field21138.setEnabled(var5.method36702());
         if (var5.method36701() != null) {
            RenderUtil.startScissor((float)(this.xA + 295), (float)(this.yA + 280), 190.0F, 50.0F);
            RenderUtil.drawImage(
               (float)(this.xA + 316),
               (float)(this.yA + 280),
               190.0F,
               190.0F,
               var5.method36701(),
               RenderUtil2.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), partialTicks)
            );
            RenderUtil.endScissor();
         }
      }

      RenderUtil.drawString(ResourceRegistry.JelloLightFont36, 100.0F, 100.0F, "Redeem Premium", RenderUtil2.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), partialTicks));
      RenderUtil.drawString(
         ResourceRegistry.JelloLightFont25,
         100.0F,
         150.0F,
         "Visit http://sigmaclient.info for more info",
              RenderUtil2.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.6F * partialTicks)
      );
      RenderUtil.drawString(ResourceRegistry.JelloLightFont18, 100.0F, 263.0F, this.field21135, RenderUtil2.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.6F * partialTicks));
      super.draw(partialTicks);
   }
}
