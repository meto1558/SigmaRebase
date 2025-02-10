package com.mentalfrostbyte.jello.module.impl.movement.phase;


import com.mentalfrostbyte.jello.event.impl.game.world.EventPushBlock;
import com.mentalfrostbyte.jello.event.impl.player.movement.EventMove;
import com.mentalfrostbyte.jello.event.impl.player.movement.EventUpdateWalkingPlayer;
import com.mentalfrostbyte.jello.gui.base.JelloPortal;
import com.mentalfrostbyte.jello.util.game.player.NewMovementUtil;
import com.mentalfrostbyte.jello.util.system.other.SimpleEntryPair;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import com.mentalfrostbyte.jello.util.game.player.PlayerUtil;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import team.sdhq.eventBus.annotations.EventTarget;

public class VanillaPhase extends Module {
    public VanillaPhase() {
        super(ModuleCategory.MOVEMENT, "Vanilla", "Vanilla phase");
    }

    @EventTarget
    public void EventUpdate(EventUpdateWalkingPlayer event) {
        if (this.isEnabled()) {
            if (mc.player.collidedHorizontally) {
                SimpleEntryPair var4 = PlayerUtil.findCollisionDirection(1.0E-4);
                double var5 = !JelloPortal.getVersion().equalTo(ProtocolVersion.v1_8) ? 1.0E-6 : 0.0625;
                if (((Direction) var4.getKey()).getAxis() != Direction.Axis.X) {
                    event.setZ(
                            (double) Math.round((((Vector3d) var4.getValue()).z + 1.1921022E-8) * 10000.0) / 10000.0
                                    + (double) ((Direction) var4.getKey()).getZOffset() * var5
                    );
                } else {
                    event.setX(
                            (double) Math.round((((Vector3d) var4.getValue()).x + 1.1921022E-8) * 10000.0) / 10000.0
                                    + (double) ((Direction) var4.getKey()).getXOffset() * var5
                    );
                }
            }
        }
    }

    @EventTarget
    public void EventMove(EventMove event) {
        if (this.isEnabled()) {
            if (mc.player.collidedHorizontally || PlayerUtil.method17761()) {
                NewMovementUtil.setMotion(event, 0.0);
                NewMovementUtil.movePlayerInDirection(1.7);
            }
        }
    }

    @EventTarget
    public void EventPushBlock(EventPushBlock event) {
        if (this.isEnabled()) {
            event.setCancelled(true);
        }
    }
}
