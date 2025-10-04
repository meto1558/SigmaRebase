package com.mentalfrostbyte.jello.gui.base.elements.impl.button.types;

import com.mentalfrostbyte.jello.gui.base.animations.Animation;
import com.mentalfrostbyte.jello.gui.base.elements.Element;
import com.mentalfrostbyte.jello.gui.combined.CustomGuiScreen;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.system.math.MathHelper;

public class SpectrumButton extends Element {
    private boolean field20682 = true;
    private final Animation field20683 = new Animation(100, 100);

    public SpectrumButton(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6, boolean var7) {
        super(var1, var2, var3, var4, var5, var6, false);
        this.field20682 = var7;
    }

    @Override
    public void draw(float partialTicks) {
        this.field20683.changeDirection(!this.method13298() ? Animation.Direction.BACKWARDS : Animation.Direction.FORWARDS);
        partialTicks *= 0.09F + 0.25F * this.field20683.calcPercent() + (this.field20682 ? 0.0F : 0.2F);
        RenderUtil.drawRoundedRect2(
                (float) (this.xA + 10), (float) (this.yA + 16), 5.0F, 14.0F, MathHelper.applyAlpha2(ClientColors.LIGHT_GREYISH_BLUE.getColor(), partialTicks)
        );
        RenderUtil.drawRoundedRect2(
                (float) (this.xA + 17), (float) (this.yA + 10), 5.0F, 20.0F, MathHelper.applyAlpha2(ClientColors.LIGHT_GREYISH_BLUE.getColor(), partialTicks)
        );
        RenderUtil.drawRoundedRect2(
                (float) (this.xA + 24), (float) (this.yA + 20), 5.0F, 10.0F, MathHelper.applyAlpha2(ClientColors.LIGHT_GREYISH_BLUE.getColor(), partialTicks)
        );
        super.draw(partialTicks);
    }

    public void method13099(boolean var1) {
        this.field20682 = var1;
    }
}
