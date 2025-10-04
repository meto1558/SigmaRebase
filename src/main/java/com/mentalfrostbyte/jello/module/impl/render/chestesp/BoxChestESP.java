package com.mentalfrostbyte.jello.module.impl.render.chestesp;

import com.mentalfrostbyte.jello.event.impl.game.render.EventRender3D;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.data.ModuleCategory;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.game.world.BoundingBox;
import com.mentalfrostbyte.jello.util.game.world.PositionUtil;
import com.mentalfrostbyte.jello.util.system.math.MathHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.EnderChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TrappedChestTileEntity;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureImpl;
import team.sdhq.eventBus.annotations.EventTarget;

public class BoxChestESP extends Module {
    public BoxChestESP() {
        super(ModuleCategory.RENDER, "Box", "Draws a box where chests are");
    }

    @EventTarget
    public void onRender3D(EventRender3D event) {
        if (this.isEnabled()) {
            if (mc.player != null && mc.world != null) {
                prepareRenderSettings();
                renderChestBoxes();
                applyTextureSettings();
            }
        }
    }

    private void renderChestBoxes() {
        int regularColor = MathHelper.applyAlpha2(this.access().parseSettingValueToIntBySettingName("Regular Color"), 0.14F);
        int enderColor = MathHelper.applyAlpha2(this.access().parseSettingValueToIntBySettingName("Ender Color"), 0.14F);
        int trappedColor = MathHelper.applyAlpha2(this.access().parseSettingValueToIntBySettingName("Trapped Color"), 0.14F);

        for (TileEntity tileEntity : mc.world.loadedTileEntityList) {
            boolean showRegularChests = tileEntity instanceof ChestTileEntity && !(tileEntity instanceof TrappedChestTileEntity)
                    && this.access().getBooleanValueFromSettingName("Show Regular Chests");
            boolean showEnderChests = tileEntity instanceof EnderChestTileEntity && this.access().getBooleanValueFromSettingName("Show Ender Chests");
            boolean showTrappedChests = tileEntity instanceof TrappedChestTileEntity && this.access().getBooleanValueFromSettingName("Show Trapped Chests");

            if (showRegularChests || showEnderChests || showTrappedChests) {
                double x = PositionUtil.getRelativePosition(tileEntity.getPos()).x;
                double y = PositionUtil.getRelativePosition(tileEntity.getPos()).y;
                double z = PositionUtil.getRelativePosition(tileEntity.getPos()).z;

                GL11.glDisable(2929);
                GL11.glEnable(3042);
                int color = regularColor;

                if (tileEntity instanceof TrappedChestTileEntity) {
                    color = trappedColor;
                } else if (tileEntity instanceof EnderChestTileEntity) {
                    color = enderColor;
                }

                BoundingBox boundingBox = new BoundingBox(
                        tileEntity.getBlockState().getShape(mc.world, tileEntity.getPos()).getBoundingBox().offset(x, y, z)
                );

                GL11.glAlphaFunc(519, 0.0F);
                RenderUtil.render3DColoredBox(boundingBox, color);
                RenderUtil.renderWireframeBox(boundingBox, 2.0F, color);
                GL11.glDisable(3042);
            }
        }
    }

    private void prepareRenderSettings() {
        GL11.glLineWidth(3.0F);
        GL11.glPointSize(3.0F);
        GL11.glEnable(2832);
        GL11.glEnable(2848);
        GL11.glEnable(3042);
        GL11.glDisable(2896);
        GL11.glEnable(3008);
        GL11.glDisable(3553);
        GL11.glDisable(2903);
        GL11.glDisable(2929);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.gameRenderer.lightmapTexture.disableLightmap();
    }

    private void applyTextureSettings() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(2896);
        GL11.glEnable(3553);
        GL11.glEnable(2903);
        RenderSystem.glMultiTexCoord2f(33986, 240.0F, 240.0F);
        TextureImpl.unbind();
        mc.getTextureManager().bindTexture(TextureManager.RESOURCE_LOCATION_EMPTY);
        mc.gameRenderer.lightmapTexture.enableLightmap();
    }
}
