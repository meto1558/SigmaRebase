package com.mentalfrostbyte.jello.module.impl.world;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.event.impl.game.action.EventKeyPress;
import com.mentalfrostbyte.jello.event.impl.game.render.EventRender3D;
import com.mentalfrostbyte.jello.event.impl.game.world.EventUpdate;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import com.mentalfrostbyte.jello.module.settings.impl.*;
import com.mentalfrostbyte.jello.util.ClientColors;
import com.mentalfrostbyte.jello.util.MultiUtilities;
import com.mentalfrostbyte.jello.util.render.Box3D;
import com.mentalfrostbyte.jello.util.world.BlockUtil;
import com.mentalfrostbyte.jello.util.player.RotationHelper;
import com.mentalfrostbyte.jello.util.render.RenderUtil;

import net.minecraft.block.*;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import org.lwjgl.opengl.GL11;
import team.sdhq.eventBus.annotations.EventTarget;

import java.util.ArrayList;
import java.util.List;

public class Nuker extends Module {
    public BlockPos field23566;
    public List<BlockPos> field23567;

    public Nuker() {
        super(ModuleCategory.WORLD, "Nuker", "Destroys blocks around you");
        this.registerSetting(new NumberSetting<Float>("Range", "Range value for nuker", 6.0F, Float.class, 2.0F, 10.0F, 1.0F));
        this.registerSetting(new ModeSetting("Mode", "Mode", 0, "All", "One hit", "Bed", "Egg"));
        this.registerSetting(new BooleanSetting("NoSwing", "Removes the swing animation.", false));
        this.registerSetting(new BooleanListSetting("Blocks", "Blocks to destroy", true));
        this.registerSetting(new ColorSetting("Color", "The rendered block color", ClientColors.MID_GREY.getColor(), true));
    }

    public static void destroyBlock(BlockPos pos) {
        if (mc.player == null || mc.world == null) return;
        mc.getConnection().sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, pos, Direction.UP));
        mc.getConnection().sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.UP));
        mc.world.setBlockState(pos, Blocks.AIR.getDefaultState());
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (this.isEnabled() && event.isPre()) {
            this.field23567 = this.getBlocksToDestroy(this.getNumberValueBySettingName("Range") / 2.0F);
            if (this.field23567.isEmpty()) {
                this.field23566 = null;
            } else if (mc.playerController.getCurrentGameType() != GameType.CREATIVE) {
                handleBlockDestruction(event);
            } else {
                destroyAllBlocks();
            }
        }
    }

    public void handleBlockDestruction(EventUpdate event) {
        if (this.field23566 != null) {
            if (mc.world.getBlockState(this.field23566).isAir()
                    || Math.sqrt(mc.player.getDistanceNearest(
                    this.field23566.getX() + 0.5,
                    this.field23566.getY() + 0.5,
                    this.field23566.getZ() + 0.5
            )) > 6.0) {
                this.field23566 = this.field23567.get(0);
            }

            float[] rotations = RotationHelper.method34144(
                    this.field23566.getX(), this.field23566.getZ(), this.field23566.getY()
            );
            event.setYaw(rotations[0]);
            event.setPitch(rotations[1]);
            EventKeyPress keyPressEvent = new EventKeyPress(0, false, this.field23566);
            mc.playerController.onPlayerDamageBlock(this.field23566, BlockUtil.method34580(this.field23566));

            if (!this.getBooleanValueFromSettingName("NoSwing")) {
                mc.player.swingArm(Hand.MAIN_HAND);
            } else {
                mc.getConnection().sendPacket(new CAnimateHandPacket(Hand.MAIN_HAND));
            }
        } else {
            this.field23566 = this.field23567.get(0);
            float[] rotations = RotationHelper.method34144(
                    this.field23566.getX() + 0.5, this.field23566.getZ(), this.field23566.getY() + 0.5
            );
            event.setYaw(rotations[0]);
            event.setPitch(rotations[1]);
            EventKeyPress keyPressEvent = new EventKeyPress(0, false, this.field23566);
            mc.playerController.onPlayerDamageBlock(this.field23566, BlockUtil.method34580(this.field23566));

            if (!this.getBooleanValueFromSettingName("NoSwing")) {
                mc.player.swingArm(Hand.MAIN_HAND);
            } else {
                mc.getConnection().sendPacket(new CAnimateHandPacket(Hand.MAIN_HAND));
            }
        }
    }

    public void destroyAllBlocks() {
        for (BlockPos pos : this.field23567) {
            mc.getConnection().sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, pos, BlockUtil.method34580(pos)));
            if (!this.getBooleanValueFromSettingName("NoSwing")) {
                mc.player.swingArm(Hand.MAIN_HAND);
            } else {
                mc.getConnection().sendPacket(new CAnimateHandPacket(Hand.MAIN_HAND));
            }
        }
    }

    @EventTarget
    public void onRender3D(EventRender3D event) {
        if (this.field23566 != null && !mc.world.getBlockState(this.field23566).isAir()) {
            int color = MultiUtilities.applyAlpha(this.parseSettingValueToIntBySettingName("Color"), 0.4F);
            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            double x = this.field23566.getX() - mc.gameRenderer.getActiveRenderInfo().getPos().getX();
            double y = this.field23566.getY() - mc.gameRenderer.getActiveRenderInfo().getPos().getY();
            double z = this.field23566.getZ() - mc.gameRenderer.getActiveRenderInfo().getPos().getZ();
            AxisAlignedBB box = mc.world.getBlockState(this.field23566).getCollisionShape(mc.world, this.field23566).getBoundingBox();
            Box3D box3D = new Box3D(x + box.minX, y + box.minY, z + box.minZ, x + box.maxX, y + box.maxY, z + box.maxZ);
            RenderUtil.render3DColoredBox(box3D, color);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glPopMatrix();
        }
    }

    public boolean isValidBlock(BlockPos pos) {
        Block block = mc.world.getBlockState(pos).getBlock();
        return mc.world.getBlockState(pos).getMaterial().isReplaceable() || block instanceof BushBlock;
    }

    public List<BlockPos> getBlocksToDestroy(float range) {
        List<BlockPos> blocks = new ArrayList<>();
        for (float y = range + 2.0F; y >= -range + 1.0F; y--) {
            for (float x = -range; x <= range; x++) {
                for (float z = -range; z <= range; z++) {
                    BlockPos pos = new BlockPos(
                            mc.player.getPosX() + x,
                            mc.player.getPosY() + y,
                            mc.player.getPosZ() + z
                    );
                    if (!mc.world.getBlockState(pos).isAir()
                            && mc.world.getBlockState(pos).getFluidState().isEmpty()
                            && Math.sqrt(mc.player.getDistanceNearest(
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)) < range) {
                        String mode = this.getStringSettingValueByName("Mode");
                        if (isValidMode(mode, pos)) {
                            blocks.add(pos);
                        }
                    }
                }
            }
        }
        return blocks;
    }

    public boolean isValidMode(String mode, BlockPos pos) {
        switch (mode) {
            case "One hit":
                return isValidBlock(pos);
            case "Bed":
                return mc.world.getBlockState(pos).getBlock() instanceof BedBlock;
            case "Egg":
                return mc.world.getBlockState(pos).getBlock() instanceof DragonEggBlock;
            default:
                return true;
        }
    }
}
