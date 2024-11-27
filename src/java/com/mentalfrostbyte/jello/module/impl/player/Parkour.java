package com.mentalfrostbyte.jello.module.impl.player;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.impl.EventWalkingUpdate;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class Parkour extends Module {
    public Parkour() {
        super(ModuleCategory.PLAYER, "Parkour", "Automatically jumps at the edge of blocks");
    }

    @EventTarget
    public void EventWalkingUpdate(EventWalkingUpdate event) {
        if (this.isEnabled()) {
            if (mc.player.onGround && isOnEdge()) {
                mc.player.jump();
            }
        }
    }

    private boolean isOnEdge() {
        AxisAlignedBB boundingBox = mc.player.getBoundingBox();
        double playerX = mc.player.getPosX();
        double playerZ = mc.player.getPosZ();

        // Check if there's no block directly below the player's edges
        boolean blockBelow = isBlockSolidAt(playerX, playerZ) ||
                isBlockSolidAt(playerX + boundingBox.getXSize() / 2, playerZ) ||
                isBlockSolidAt(playerX - boundingBox.getXSize() / 2, playerZ) ||
                isBlockSolidAt(playerX, playerZ + boundingBox.getZSize() / 2) ||
                isBlockSolidAt(playerX, playerZ - boundingBox.getZSize() / 2);

        return !blockBelow;
    }

    private boolean isBlockSolidAt(double x, double z) {
        BlockPos blockPos = new BlockPos(x, mc.player.getPosY() - 0.5, z);
        return !mc.world.isAirBlock(blockPos);
    }
}
