package com.mentalfrostbyte.jello.gui.impl.jello.ingame.clickgui.groups;

import com.mentalfrostbyte.jello.gui.combined.AnimatedIconPanel;
import com.mentalfrostbyte.jello.gui.combined.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.impl.jello.ingame.clickgui.panels.ModListView;
import com.mentalfrostbyte.jello.gui.impl.jello.ingame.clickgui.panels.ModListViewSize;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.data.ModuleCategory;
import com.mentalfrostbyte.jello.util.client.render.FontSizeAdjust;
import com.mentalfrostbyte.jello.util.client.render.ResourceRegistry;
import com.mentalfrostbyte.jello.util.client.render.Resources;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.system.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class PanelGroup extends AnimatedIconPanel {
    public final ModuleCategory category;
    public ModListView modListView;
    public float field21195;
    public int field21197;
    public int field21198;
    private int field21199;
    private final List<Class9476> field21200 = new ArrayList<>();

    public PanelGroup(CustomGuiScreen var1, String var2, int var3, int var4, ModuleCategory category) {
        super(var1, var2, var3, var4, 200, 350, true);
        this.setWidthA(200);
        this.setHeightA(350);
        this.field20886 = true;
        this.category = category;
        this.method13505();
    }

    public void method13504() {
        this.runThisOnDimensionUpdate(() -> {
            this.removeChildren(this.modListView);
            this.addToList(this.modListView = new ModListView(this, "modListView", 0, 60, this.getWidthA(), this.getHeightA() - 60, this.category));
        });
    }

    private void method13505() {
        this.addToList(this.modListView = new ModListView(this, "modListView", 0, 60, this.getWidthA(), this.getHeightA() - 60, this.category));
        this.modListView.setSize(new ModListViewSize());
        this.modListView.setSize((var0, var1) -> {
            var0.setYA(60);
            var0.setHeightA(var1.getHeightA() - 60);
        });
    }

    @Override
    public void updatePanelDimensions(int newHeight, int newWidth) {
        if (!(this.field21195 >= 1.0F)) {
            this.method13215(false);
            this.field20909 = false;
        } else {
            this.field21197 = this.getXA();
            this.field21198 = this.getYA();
            this.method13215(true);
        }

        float var5 = 200.0F;
        float var6 = 320.0F;
        float var7 = 0.7F;
        float var8 = 0.1F;
        int var9 = (int) (200.0F + 140.0F * (1.0F - this.field21195));
        int var10 = (int) (320.0F + 320.0F * 0.1F * (1.0F - this.field21195));
        int var11 = this.field21198;
        int var12 = (int) ((float) this.field21197 - ((float) var9 - 200.0F) / 2.0F + 0.5F);
        if (this.field21195 < 1.0F) {
            if (var12 < 0) {
                var12 = 0;
            }

            if (var12 + var9 > this.parent.getWidthA()) {
                var12 = this.parent.getWidthA() - var9;
            }

            if (var11 + var10 > this.parent.getHeightA()) {
                var11 = this.parent.getHeightA() - var10;
            }
        }

        this.setWidthA(var9);
        this.setHeightA(var10);
        this.setXA(var12);
        this.setYA(var11);
        super.updatePanelDimensions(newHeight, newWidth);
    }

    @Override
    public void draw(float partialTicks) {
        super.method13224();
        super.method13225();
        int var4 = (int) (1.0F + 10.0F * (1.0F - this.field21195));
        RenderUtil.drawRoundedRect(
                (float) (this.getXA() + (var4 - 1)),
                (float) (this.getYA() + (var4 - 1)),
                (float) (this.getWidthA() - (var4 - 1) * 2),
                (float) (this.getHeightA() - (var4 - 1) * 2),
                (float) this.field21199 + (1.0F - this.field21195) * (float) var4,
                partialTicks
        );
        RenderUtil.drawColoredRect(
                (float) this.getXA(),
                (float) this.getYA(),
                (float) (this.getXA() + this.getWidthA()),
                (float) (this.getYA() + 60),
                MathHelper.applyAlpha2(ClientColors.LIGHT_GREYISH_BLUE.getColor(), Math.min(1.0F, partialTicks * 0.9F * this.field21195))
        );
        RenderUtil.drawRoundedRect2(
                (float) this.getXA(),
                (float) this.getYA() + 60.0F * this.field21195,
                (float) this.getWidthA(),
                (float) this.getHeightA() - 60.0F * this.field21195,
                MathHelper.applyAlpha2(ClientColors.LIGHT_GREYISH_BLUE.getColor(), partialTicks)
        );
        if (!(this.field21195 > 0.8F)) {
            if (this.field21195 < 0.2F) {
                this.field21199 = 30;
            }
        } else {
            this.field21199 = 20;
        }

        String categoryName = this.getCategory().name();
        RenderUtil.drawString(
                ResourceRegistry.JelloLightFont25,
                (float) (this.getXA() + 20),
                (float) (this.getYA() + 30),
                categoryName,
                MathHelper.applyAlpha2(ClientColors.DEEP_TEAL.getColor(), partialTicks * 0.5F * this.field21195),
                FontSizeAdjust.field14488,
                FontSizeAdjust.NEGATE_AND_DIVIDE_BY_2
        );
        GL11.glPushMatrix();
        super.draw(partialTicks * partialTicks);
        GL11.glPopMatrix();
        if (this.modListView.method13513() > 0) {
            RenderUtil.drawImage(
                    (float) this.getXA(),
                    (float) (this.getYA() + 60),
                    (float) this.getWidthA(),
                    18.0F,
                    Resources.shadowBottomPNG,
                    MathHelper.applyAlpha2(ClientColors.LIGHT_GREYISH_BLUE.getColor(), partialTicks * this.field21195 * 0.5F)
            );
        }
    }

    public ModuleCategory getCategory() {
        return this.category;
    }

    public final void method13507(Class9476 var1) {
        this.field21200.add(var1);
    }

    public final void method13508(Module var1) {
        for (Class9476 var5 : this.field21200) {
            var5.method36568(var1);
        }
    }

    public interface Class9476 {
       void method36568(Module var1);
    }
}
