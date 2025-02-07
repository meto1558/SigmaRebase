package com.mentalfrostbyte.jello.gui.unmapped;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.gui.base.CustomGuiScreen;
import com.mentalfrostbyte.jello.util.client.ClientColors;
import com.mentalfrostbyte.jello.util.client.ColorHelper;
import com.mentalfrostbyte.jello.util.client.network.auth.Encryptor;
import com.mentalfrostbyte.jello.util.client.render.ResourceRegistry;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil2;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.client.render.Resources;
import net.minecraft.client.Minecraft;
import org.newdawn.slick.opengl.Texture;

public class TextButtonWithImage extends UIBase {
    public float field21334 = 0.0F;
    public int field21337;
    public int field21338;

    public TextButtonWithImage(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6, String var7) {
        super(var1, var2, var3, var4, var5, var6, ColorHelper.field27961, var7, false);
        this.font = ResourceRegistry.JelloLightFont20;
    }

    @Override
    public void updatePanelDimensions(int newHeight, int newWidth) {
        this.field21337 = newHeight;
        this.field21338 = newWidth;
    }

    @Override
    public void draw(float partialTicks) {
        Encryptor encryptor = Client.getInstance().networkManager.encryptor;
        String text = "Log in";

        Texture texture = Resources.accountPNG;
        if (encryptor != null) {
            text = encryptor.username;
        }

        this.setWidthA(this.font.getWidth(text) + 50 + 60);
        this.setXA(Minecraft.getInstance().getMainWindow().getWidth() - this.widthA - 20);
        boolean var6 = this.field21337 >= this.xA && this.field21338 <= this.yA + this.getHeightA();
        this.field21334 = Math.max(0.0F, Math.min(1.0F, this.field21334 + (!var6 ? -0.1F : 0.1F)));
        RenderUtil.drawRoundedRect(
                (float) this.xA, (float) this.yA, (float) this.getWidthA(), (float) this.getHeightA(), 20.0F, this.field21334 * 0.2F * partialTicks
        );
        RenderUtil.drawRoundedRect(
                (float) this.xA,
                (float) this.yA,
                (float) (this.xA + this.getWidthA()),
                (float) (this.yA + this.getHeightA()),
                RenderUtil2.applyAlpha(ClientColors.DULL_GREEN.getColor(), (0.2F * this.field21334 + (!this.method13212() ? 0.0F : 0.2F)) * partialTicks)
        );
        RenderUtil.drawImage(
                (float) (this.xA + this.widthA - 60 - 10),
                (float) (this.yA + 10),
                60.0F,
                60.0F,
                texture,
                RenderUtil2.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), (0.5F + 0.5F * this.field21334) * partialTicks)
        );
        RenderUtil.drawString(
                this.font,
                (float) (this.xA + this.widthA - 90 - this.font.getWidth(text)),
                (float) (this.yA + 27),
                text,
                RenderUtil2.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), (0.5F + 0.5F * this.field21334) * partialTicks)
        );
        super.draw(partialTicks);
    }
}
