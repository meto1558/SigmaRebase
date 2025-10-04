package com.mentalfrostbyte.jello.module.impl.world;


import com.mentalfrostbyte.jello.event.impl.game.network.EventReceivePacket;
import com.mentalfrostbyte.jello.event.impl.game.network.EventSendPacket;
import com.mentalfrostbyte.jello.event.impl.game.render.EventRender3D;
import com.mentalfrostbyte.jello.event.impl.player.movement.EventMotion;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.data.ModuleCategory;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.game.player.InvManagerUtil;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.game.world.BoundingBox;
import com.mentalfrostbyte.jello.util.game.world.blocks.BlockUtil;
import com.mentalfrostbyte.jello.util.system.math.MathHelper;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SCloseWindowPacket;
import net.minecraft.network.play.server.SOpenWindowPacket;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import org.lwjgl.opengl.GL11;
import team.sdhq.eventBus.annotations.EventTarget;

public class Auto32k extends Module {
    public BlockPos field23870;
    public int field23871 = -1;
    public int field23872 = -1;
    public int field23873 = 0;
    public int field23874 = -1;
    public short field23875 = 0;
    public SOpenWindowPacket field23876;
    public boolean field23877 = false;

    public Auto32k() {
        super(ModuleCategory.WORLD, "Auto32k", "Automaticly places 32k shulker + hopper in hotbar");
    }

    @Override
    public void onEnable() {
        this.field23870 = null;

        for (BlockPos var4 : BlockUtil.sortPositionsByDistance(BlockUtil.getBlockPositionsInRange(mc.playerController.getBlockReachDistance()))) {
            if (!(BlockUtil.getDistance(mc.player, var4) < 2.0F)
                    && BlockUtil.canPlaceAt(mc.player, var4)
                    && (double) var4.getY() >= mc.player.getPosY() - 2.0
                    && (double) var4.getY() <= mc.player.getPosY() - 1.0
                    && this.method16717(var4)) {
                this.field23870 = var4;
                break;
            }
        }

        this.field23872 = InvManagerUtil.getSlotWithMaxItemCount(
                Items.SHULKER_BOX,
                Items.WHITE_SHULKER_BOX,
                Items.ORANGE_SHULKER_BOX,
                Items.MAGENTA_SHULKER_BOX,
                Items.LIGHT_BLUE_SHULKER_BOX,
                Items.YELLOW_SHULKER_BOX,
                Items.LIME_SHULKER_BOX,
                Items.PINK_SHULKER_BOX,
                Items.GRAY_SHULKER_BOX,
                Items.LIGHT_GRAY_SHULKER_BOX,
                Items.CYAN_SHULKER_BOX,
                Items.PURPLE_SHULKER_BOX,
                Items.BLUE_SHULKER_BOX,
                Items.BROWN_SHULKER_BOX,
                Items.GREEN_SHULKER_BOX,
                Items.RED_SHULKER_BOX,
                Items.BLACK_SHULKER_BOX

        );
        this.field23871 = InvManagerUtil.getSlotWithMaxItemCount(Items.HOPPER);
        if (this.field23871 == -1) {
            this.field23871 = InvManagerUtil.findItemSlot(Items.HOPPER);
            if (this.field23871 != -1) {
                if (this.field23871 >= 36 && this.field23871 <= 44) {
                    this.field23871 %= 9;
                } else {
                    this.field23871 = InvManagerUtil.swapToolToHotbar(this.field23871);
                }
            }
        }

        if (this.field23872 == -1) {
            this.field23872 = InvManagerUtil.findItemInContainer(
                    Items.SHULKER_BOX,
                    Items.WHITE_SHULKER_BOX,
                    Items.ORANGE_SHULKER_BOX,
                    Items.MAGENTA_SHULKER_BOX,
                    Items.LIGHT_BLUE_SHULKER_BOX,
                    Items.YELLOW_SHULKER_BOX,
                    Items.LIME_SHULKER_BOX,
                    Items.PINK_SHULKER_BOX,
                    Items.GRAY_SHULKER_BOX,
                    Items.LIGHT_GRAY_SHULKER_BOX,
                    Items.CYAN_SHULKER_BOX,
                    Items.PURPLE_SHULKER_BOX,
                    Items.BLUE_SHULKER_BOX,
                    Items.BROWN_SHULKER_BOX,
                    Items.GREEN_SHULKER_BOX,
                    Items.RED_SHULKER_BOX,
                    Items.BLACK_SHULKER_BOX
            );
            if (this.field23872 != -1) {
                if (this.field23872 >= 36 && this.field23872 <= 44) {
                    this.field23872 %= 9;
                } else {
                    this.field23872 = InvManagerUtil.swapToolToHotbar(this.field23872);
                }
            }
        }

        this.field23873 = 0;
    }

    public boolean method16717(BlockPos var1) {
        BlockState var4 = mc.world.getBlockState(var1);
        BlockState var5 = mc.world.getBlockState(var1.up());
        BlockState var6 = mc.world.getBlockState(var1.up(2));
        return var4.isSolid() && var5.isAir() && var6.isAir();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventTarget
    public void method16718(EventRender3D var1) {
        if (this.isEnabled()) {
            if (this.field23870 != null) {
                GL11.glAlphaFunc(516, 0.0F);
                BlockPos var4 = this.field23870;
                double var5 = (double) var4.getX() - mc.gameRenderer.getActiveRenderInfo().getPos().getX();
                double var7 = (double) var4.getY() - mc.gameRenderer.getActiveRenderInfo().getPos().getY();
                double var9 = (double) var4.getZ() - mc.gameRenderer.getActiveRenderInfo().getPos().getZ();
                RenderUtil.render3DColoredBox(
                        new BoundingBox(var5, var7 + 1.625, var9, var5 + 1.0, var7 + 3.0, var9 + 1.0), MathHelper.applyAlpha(ClientColors.PALE_ORANGE.getColor(), 0.3F)
                );
                GL11.glColor3f(1.0F, 1.0F, 1.0F);
                GL11.glBlendFunc(770, 771);
                GL11.glEnable(3042);
                GL11.glEnable(2848);
                GL11.glDisable(3553);
                GL11.glEnable(2929);
                GL11.glDisable(2896);
                GL11.glDepthMask(false);
                float var11 = 8.0F;
                boolean var12 = true;
                if (var12) {
                    GL11.glPushMatrix();
                    int var13 = MathHelper.applyAlpha(ClientColors.PALE_ORANGE.getColor(), 0.5F);
                    float var14 = (float) (var13 >> 24 & 0xFF) / 255.0F;
                    float var15 = (float) (var13 >> 16 & 0xFF) / 255.0F;
                    float var16 = (float) (var13 >> 8 & 0xFF) / 255.0F;
                    float var17 = (float) (var13 & 0xFF) / 255.0F;
                    GL11.glColor4f(var15, var16, var17, var14);
                    GL11.glTranslated(var5 + 0.5, var7 + 1.01F, var9 + 0.5);
                    GL11.glRotatef(90.0F, 0.0F, 0.0F, 0.0F);
                    GL11.glLineWidth(3.4F);
                    this.method16719(var11);
                    GL11.glPopMatrix();
                }

                int var22 = MathHelper.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.5F);
                float var23 = (float) (var22 >> 24 & 0xFF) / 255.0F;
                float var24 = (float) (var22 >> 16 & 0xFF) / 255.0F;
                float var25 = (float) (var22 >> 8 & 0xFF) / 255.0F;
                float var26 = (float) (var22 & 0xFF) / 255.0F;

                for (int var18 = 0; (float) var18 < var11 - 1.0F; var18++) {
                    float var19 = (float) var18 / var11;
                    double var20 = Math.cos((double) var19 * Math.PI / 2.0);
                    GL11.glPushMatrix();
                    GL11.glColor4f(var24, var25, var26, var23 * (1.0F - var19) * 0.25F);
                    GL11.glTranslated(var5 + 0.5, var7 + 2.01F + (double) var18, var9 + 0.5);
                    GL11.glRotatef(90.0F, 0.0F, 0.0F, 0.0F);
                    GL11.glLineWidth(3.4F);
                    this.method16719((float) ((double) var11 * var20));
                    GL11.glPopMatrix();
                }

                for (int var27 = 0; (float) var27 < var11 - 1.0F; var27++) {
                    float var28 = (float) var27 / var11;
                    double var29 = Math.cos((double) var28 * Math.PI / 2.0);
                    GL11.glPushMatrix();
                    GL11.glColor4f(var24, var25, var26, var23 * (1.0F - var28) * 0.25F);
                    GL11.glTranslated(var5 + 0.5, var7 - (double) var27 + 0.01F, var9 + 0.45);
                    GL11.glRotatef(90.0F, 0.0F, 0.0F, 0.0F);
                    GL11.glLineWidth(3.4F);
                    this.method16719((float) ((double) var11 * var29));
                    GL11.glPopMatrix();
                }

                GL11.glEnable(3553);
                GL11.glDisable(2848);
                GL11.glDepthMask(true);
                GL11.glEnable(2896);
                GL11.glColor3f(1.0F, 1.0F, 1.0F);
            }
        }
    }

    public void method16719(float var1) {
        GL11.glBegin(2);

        for (int var4 = 0; var4 < 360; var4++) {
            double var5 = (double) var4 * Math.PI / 180.0;
            GL11.glVertex2d(Math.cos(var5) * (double) var1, Math.sin(var5) * (double) var1);
        }

        GL11.glEnd();
    }

    @EventTarget
    public void method16720(EventMotion event) {
        if (this.isEnabled()) {
            if (this.field23871 != -1) {
                if (this.field23872 != -1) {
                    if (this.field23870 != null) {
                        if (this.field23873 != 0) {
                            if (this.field23873 == 1) {
                                float yaw = BlockUtil.rotationsToBlock(this.field23870.up(), Direction.UP)[0];
                                float pitch = BlockUtil.rotationsToBlock(this.field23870.up(), Direction.UP)[1];
                                event.setPitch(pitch);
                                event.setYaw(yaw);

                                mc.player.inventory.currentItem = this.field23871;
                                net.minecraft.util.math.vector.Vector3d var7 = BlockUtil.getRandomlyOffsettedPos(Direction.UP, this.field23870);
                                BlockRayTraceResult var8 = new BlockRayTraceResult(var7, Direction.UP, this.field23870, false);
                                ActionResultType var9 = mc.playerController.func_217292_a(mc.player, mc.world, Hand.MAIN_HAND, var8);
                                mc.player.swingArm(Hand.MAIN_HAND);
                                if (var9 == ActionResultType.SUCCESS) {
                                    this.field23873++;
                                    mc.getConnection().sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.PRESS_SHIFT_KEY));
                                    mc.player.movementInput.sneaking = true;
                                    mc.player.inventory.currentItem = this.field23872;
                                    net.minecraft.util.math.vector.Vector3d var10 = BlockUtil.getRandomlyOffsettedPos(Direction.UP, this.field23870.up());
                                    BlockRayTraceResult var11 = new BlockRayTraceResult(var10, Direction.UP, this.field23870.up(), false);
                                    mc.playerController.func_217292_a(mc.player, mc.world, Hand.MAIN_HAND, var11);
                                    mc.player.swingArm(Hand.MAIN_HAND);
                                    mc.player.movementInput.sneaking = false;
                                    mc.getConnection().sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.RELEASE_SHIFT_KEY));
                                    mc.playerController.func_217292_a(mc.player, mc.world, Hand.MAIN_HAND, var11);
                                }
                            }
                        } else {
                            float yaw = BlockUtil.rotationsToBlock(this.field23870, Direction.UP)[0];
                            float pitch = BlockUtil.rotationsToBlock(this.field23870, Direction.UP)[1];
                            event.setPitch(pitch);
                            event.setYaw(yaw);

                            this.field23873++;
                        }
                    }
                }
            }
        }
    }

    @EventTarget
    public void method16721(EventReceivePacket var1) {
        if (this.isEnabled()) {
            if (var1.packet instanceof SOpenWindowPacket) {
                this.field23876 = (SOpenWindowPacket) var1.packet;
                if (this.isEnabled() && this.field23876.getTitle() == ContainerType.HOPPER) {
                    var1.cancelled = true;
                }

                this.field23874 = this.field23876.getWindowId();
                this.field23877 = false;
            }

            if (var1.packet instanceof SSetSlotPacket var4) {
				int var5 = var4.getSlot();
                ItemStack var6 = var4.getStack();
                int var7 = var4.getWindowId();
                if (this.field23874 == var7 && var5 == 0 && var6.getItem() != Items.AIR && !this.field23877) {
                    var1.cancelled = true;
                    mc.getConnection().sendPacket(new CClickWindowPacket(var7, var5, 1, ClickType.QUICK_MOVE, var6, this.field23875++));
                    int var8 = -1;

                    for (int var9 = 44; var9 > 9; var9--) {
                        ItemStack var10 = mc.player.container.getSlot(var9).getStack();
                        if (var10.isEmpty()) {
                            var8 = var9;
                            break;
                        }
                    }

                    if (var8 != -1) {
                        mc.player.container.getSlot(var8).putStack(var6);
                        if (var8 >= 36) {
                            mc.player.inventory.currentItem = var8 % 9;
                        }
                    }

                    this.field23877 = true;
                }
            }

            if (var1.packet instanceof SCloseWindowPacket) {
                this.setEnabled(false);
            }
        }
    }

    @EventTarget
    public void method16722(EventSendPacket var1) {
        if (this.isEnabled()) {
            if (var1.packet instanceof CCloseWindowPacket) {
                var1.cancelled = true;
            }

            if (var1.packet instanceof CUseEntityPacket) {
                float var4 = BlockUtil.rotationsToBlock(this.field23870.up(), Direction.UP)[0];
                float var5 = BlockUtil.rotationsToBlock(this.field23870.up(), Direction.UP)[1];
            }

            if (var1.packet instanceof CPlayerPacket var6) {
				var6.onGround = false;
            }
        }
    }
}
