package com.mentalfrostbyte.jello.gui.impl.jello.mainmenu;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.gui.combined.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.impl.jello.buttons.LoadingIndicator;
import com.mentalfrostbyte.jello.gui.impl.jello.buttons.TextField;
import com.mentalfrostbyte.jello.gui.base.elements.impl.Text;
import com.mentalfrostbyte.jello.gui.base.elements.Element;
import com.mentalfrostbyte.jello.gui.base.elements.impl.button.types.TextButton;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.client.render.theme.ColorHelper;
import com.mentalfrostbyte.jello.util.client.render.ResourceRegistry;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.client.render.Resources;
import com.mentalfrostbyte.jello.util.client.render.FontSizeAdjust;
import net.minecraft.util.Util;

public class LoginScreen extends Element {
    private TextField inputUsername;
    private TextField inputPassword;
    private TextButton loginButton;
    private TextButton registerButton;
    private TextButton forgotButton;
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
                this.loginButton = new TextButton(
                        this, "LoginButton", 468, 238, ResourceRegistry.JelloLightFont25.getWidth("Login"), 70, ColorHelper.field27961, "Login", ResourceRegistry.JelloLightFont25
                )
        );
        this.addToList(
                this.registerButton = new TextButton(
                        this, "RegisterButton", 88, 250, ResourceRegistry.JelloLightFont14.getWidth("Register"), 14, ColorHelper.field27961, "Register", ResourceRegistry.JelloLightFont14
                )
        );
        this.addToList(
                this.forgotButton = new TextButton(
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
        this.loadingThingy.setHovered(false);
        this.loadingThingy.method13294(true);
        int var9 = 50;
        int var10 = 300;
        int var11 = 106;
        ColorHelper var12 = new ColorHelper(-892679478, -892679478, -892679478, ClientColors.MID_GREY.getColor(), FontSizeAdjust.field14488, FontSizeAdjust.NEGATE_AND_DIVIDE_BY_2);
        this.addToList(this.inputUsername = new TextField(this, "Username", 228, var11, var10, var9, var12, "", "Username"));
        this.addToList(this.inputPassword = new TextField(this, "Password", 228, var11 + 53, var10, var9, var12, "", "Password"));
        this.inputUsername.setFont(ResourceRegistry.JelloLightFont20);
        this.inputPassword.setFont(ResourceRegistry.JelloLightFont20);
        this.inputPassword.method13155(true);
        this.loginButton.doThis((var1x, var2x) -> this.method13688());
        this.registerButton.doThis((var1x, var2x) -> {
            RegisterScreen var5x = (RegisterScreen) this.getParent();
            var5x.method13422();
        });
        this.forgotButton.doThis((var0, var1x) -> Util.getOSType().openLink("https://sigmaclient.cloud"));
    }

    @Override
    public void draw(float partialTicks) {
        super.method13224();
        super.method13225();
        int var4 = 28;
        RenderUtil.drawImage((float) (this.xA + var4), (float) (this.yA + var4 + 10), 160.0F, 160.0F, Resources.sigmaPNG, partialTicks);
        super.draw(partialTicks);
    }

    public void method13688() {
        new Thread(() -> {
            this.loadingThingy.setHovered(true);
            this.loginButton.setEnabled(false);

            String account = Client.getInstance().networkManager.newAccount(this.inputUsername.getTypedText(), this.inputPassword.getTypedText());
            RegisterScreen reg = (RegisterScreen) this.getParent();

            if (account != null) {
                reg.method13424("Error", account);
            } else {
                this.callUIHandlers();
            }

            this.loadingThingy.setHovered(false);
            this.loginButton.setEnabled(true);
        }).start();
    }
}
