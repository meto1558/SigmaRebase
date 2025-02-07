package com.mentalfrostbyte.jello.gui.impl.jello.mainmenu;

import com.mentalfrostbyte.jello.Client;
import com.mentalfrostbyte.jello.gui.base.animations.Animation;
import com.mentalfrostbyte.jello.gui.base.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.impl.jello.buttons.TextField;
import com.mentalfrostbyte.jello.gui.base.elements.impl.TextButton;
import com.mentalfrostbyte.jello.util.client.network.auth.CaptchaChecker;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.client.render.theme.ColorHelper;
import com.mentalfrostbyte.jello.util.client.render.ResourceRegistry;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil2;
import com.mentalfrostbyte.jello.util.system.math.MathUtils;

public class RedeemKeyScreen extends CustomGuiScreen {
    public String premiumLicense = "";
    public Animation animation = new Animation(380, 200, Animation.Direction.BACKWARDS);
    private TextField captchaField;

    public RedeemKeyScreen(CustomGuiScreen parent, String var2, int var3, int var4, int var5, int var6) {
        super(parent, var2, var3, var4, var5, var6);
        this.setListening(false);
        TextField redeemBox;
        this.addToList(redeemBox = new TextField(this, "redeemBox", 100, 200, 350, 50, TextField.field20742, "", "Premium Code"));
        TextButton redeemButton;
        this.addToList(
                redeemButton = new TextButton(
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
        this.addToList(this.captchaField = new TextField(this, "captcha", 195, 290, 75, 35, TextField.field20742, "", "Captcha"));
        this.captchaField.setFont(ResourceRegistry.JelloLightFont18);
        redeemButton.doThis((var2x, var3x) -> new Thread(() -> {
            CaptchaChecker captcha = Client.getInstance().networkManager.getCaptcha();
            if (captcha != null) {
                captcha.setAnswer(this.captchaField.getTypedText());
            }

            this.premiumLicense = Client.getInstance().networkManager.redeemPremium(redeemBox.getTypedText(), Client.getInstance().networkManager.getCaptcha());
            if (this.premiumLicense == null) {
                this.premiumLicense = "";
            }

            if (Client.getInstance().networkManager.isPremium()) {
                this.runThisOnDimensionUpdate(() -> ((MainMenuScreen) this.getParent()).goOut());
            }
        }).start());
    }

    @Override
    public void draw(float partialTicks) {
        this.animation.changeDirection(!this.isHovered() ? Animation.Direction.BACKWARDS : Animation.Direction.FORWARDS);
        partialTicks = 1.0F;
        partialTicks *= this.animation.calcPercent();
        float var4 = MathUtils.lerp(this.animation.calcPercent(), 0.17, 1.0, 0.51, 1.0);
        if (this.animation.getDirection() == Animation.Direction.BACKWARDS) {
            var4 = 1.0F;
        }

        CaptchaChecker captchaChecker = Client.getInstance().networkManager.getCaptcha();
        if (captchaChecker != null) {
            this.captchaField.setEnabled(captchaChecker.isActuallyCompleted());
            if (captchaChecker.getCaptchaImage() != null) {
                RenderUtil.startScissor((float) (this.xA + 295), (float) (this.yA + 280), 190.0F, 50.0F);
                RenderUtil.drawImage(
                        (float) (this.xA + 316),
                        (float) (this.yA + 280),
                        190.0F,
                        190.0F,
                        captchaChecker.getCaptchaImage(),
                        RenderUtil2.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), partialTicks)
                );
                RenderUtil.endScissor();
            }
        }
        this.drawBackground((int) (150.0F * (1.0F - var4)));
        this.method13225();

        RenderUtil.drawString(ResourceRegistry.JelloLightFont36, 100.0F, 100.0F, "Redeem Premium", RenderUtil2.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), partialTicks));
        RenderUtil.drawString(
                ResourceRegistry.JelloLightFont25,
                100.0F,
                150.0F,
                "Visit https://sigmaclient.cloud for more info",
                RenderUtil2.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.6F * partialTicks)
        );
        RenderUtil.drawString(ResourceRegistry.JelloLightFont18, 100.0F, 263.0F, this.premiumLicense, RenderUtil2.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.6F * partialTicks));
        super.draw(partialTicks);
    }
}
