package com.mentalfrostbyte.jello.gui.unmapped;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.gui.base.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.impl.jello.buttons.LoadingIndicator;
import com.mentalfrostbyte.jello.gui.impl.jello.buttons.TextField;
import com.mentalfrostbyte.jello.gui.impl.jello.mainmenu.RegisterScreen;
import com.mentalfrostbyte.jello.managers.util.account.Class9507;
import com.mentalfrostbyte.jello.util.ClientColors;
import com.mentalfrostbyte.jello.util.ColorHelper;
import com.mentalfrostbyte.jello.util.ResourceRegistry;
import com.mentalfrostbyte.jello.util.render.ColorUtils;
import com.mentalfrostbyte.jello.util.render.RenderUtil;
import com.mentalfrostbyte.jello.util.render.Resources;
import com.mentalfrostbyte.jello.util.unmapped.Class2218;
import net.minecraft.util.Util;

public class LoginScreen extends UIBase {
    private TextField inputUsername;
   private TextField inputPassword;
   private TextField field21355;
   private UIButton loginButton;
   private UIButton registerButton;
   private UIButton forgotButton;
   private LoadingIndicator loadingThingy;
   public static int widthy = 334;
   public static int heighty = 571;

   public LoginScreen(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6, false);
       this.addToList(
               new Text(
                  this,
                  "Login",
                  228,
                  43,
                  ResourceRegistry.JelloMediumFont40.getWidth("Login"),
                  50,
                  new ColorHelper(ClientColors.DEEP_TEAL.getColor(), ClientColors.DEEP_TEAL.getColor(), ClientColors.DEEP_TEAL.getColor(), -7631989),
                  "Login",
                  ResourceRegistry.JelloMediumFont40
               )
      );
      this.addToList(
         this.loginButton = new UIButton(
            this, "LoginButton", 468, 238, ResourceRegistry.JelloLightFont25.getWidth("Login"), 70, ColorHelper.field27961, "Login", ResourceRegistry.JelloLightFont25
         )
      );
      this.addToList(
         this.registerButton = new UIButton(
            this, "RegisterButton", 88, 250, ResourceRegistry.JelloLightFont14.getWidth("Register"), 14, ColorHelper.field27961, "Register", ResourceRegistry.JelloLightFont14
         )
      );
      this.addToList(
         this.forgotButton = new UIButton(
            this,
            "ForgotButton",
            60,
            275,
            ResourceRegistry.JelloLightFont14.getWidth("Forgot password?"),
            14,
            ColorHelper.field27961,
            "Forgot password?",
            ResourceRegistry.JelloLightFont14
         )
      );
      this.addToList(this.loadingThingy = new LoadingIndicator(this, "loading", 511, 260, 30, 30));
      this.loadingThingy.method13296(false);
      this.loadingThingy.method13294(true);
      int var9 = 50;
      int var10 = 300;
      int var11 = 106;
      ColorHelper var12 = new ColorHelper(-892679478, -892679478, -892679478, ClientColors.MID_GREY.getColor(), Class2218.field14488, Class2218.field14492);
      this.addToList(this.inputUsername = new TextField(this, "Username", 228, var11, var10, var9, var12, "", "Username"));
      this.addToList(this.inputPassword = new TextField(this, "Password", 228, var11 + 53, var10, var9, var12, "", "Password"));
      this.inputUsername.setFont(ResourceRegistry.JelloLightFont20);
      this.inputPassword.setFont(ResourceRegistry.JelloLightFont20);
      this.inputPassword.method13155(true);
      this.addToList(this.field21355 = new TextField(this, "CaptchaBox", 228, var11 + 135, 84, var9, var12, "", "Captcha"));
      this.field21355.setFont(ResourceRegistry.JelloLightFont20);
      this.field21355.setEnabled(false);
      this.loginButton.doThis((var1x, var2x) -> this.method13688());
      this.registerButton.doThis((var1x, var2x) -> {
         RegisterScreen var5x = (RegisterScreen)this.getParent();
         var5x.method13422();
      });
      this.forgotButton.doThis((var0, var1x) -> Util.getOSType().openLink("https://sigmaclient.cloud"));
   }

   @Override
   public void draw(float partialTicks) {
      super.method13224();
      super.method13225();
      int var4 = 28;
      RenderUtil.drawImage((float)(this.xA + var4), (float)(this.yA + var4 + 10), 160.0F, 160.0F, Resources.sigmaPNG, partialTicks);
      Class9507 var5 = Client.getInstance().networkManager.method30452();
      if (var5 != null) {
         this.field21355.setEnabled(var5.method36702());
         if (var5.method36702()) {
            RenderUtil.drawRoundedRect2(
               (float)(this.xA + 330), (float)(this.yA + 255), 114.0F, 40.0F, ColorUtils.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.04F)
            );
         }

         if (var5.method36701() != null) {
            RenderUtil.startScissor((float)(this.xA + 316), (float)(this.yA + 255), 190.0F, 50.0F);
            RenderUtil.drawImage((float)(this.xA + 316), (float)(this.yA + 255), 190.0F, 190.0F, var5.method36701());
            RenderUtil.endScissor();
         }
      }

      super.draw(partialTicks);
   }

   public void method13688() {
      new Thread(() -> {
         this.loadingThingy.method13296(true);
         this.loginButton.setEnabled(false);
         Class9507 var3 = Client.getInstance().networkManager.method30452();
         if (var3 != null) {
            var3.method36706(this.field21355.getTypedText());
         }

         String var4 = Client.getInstance().networkManager.newAccount(this.inputUsername.getTypedText());
         if (var4 != null) {
            RegisterScreen var5 = (RegisterScreen)this.getParent();
            var5.method13424("Error", var4);
            this.field21355.setTypedText("");
         } else {
            this.callUIHandlers();
         }

         this.loadingThingy.method13296(false);
         this.loginButton.setEnabled(true);
      }).start();
   }
}
