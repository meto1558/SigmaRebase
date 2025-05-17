package com.mentalfrostbyte.jello.module.impl.movement.fly;

import com.mentalfrostbyte.jello.event.impl.player.movement.EventMotion;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.data.ModuleCategory;
import com.mentalfrostbyte.jello.module.settings.impl.ModeSetting;
import com.mentalfrostbyte.jello.util.game.player.MovementUtil;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.play.client.CPlayerPacket;
import team.sdhq.eventBus.annotations.EventTarget;

/**
 * @author alarmingly_good (on discord)
 */
public class VerusFly extends Module {
    public final ModeSetting mode;
    public double speed;

    public VerusFly() {
        super(ModuleCategory.MOVEMENT, "Verus", "A fly for Verus");
        registerSetting(mode = new ModeSetting(
                        "Mode",
                        "Mode",
                        0,
                        "Glide", "Fast"
                )
        );
    }

    @Override
    public void onEnable() {
        if (!mode.currentValue.equals("Fast")) return;
        ClientPlayerEntity player = mc.player;
        ClientPlayNetHandler network = mc.getConnection();
        assert player != null;
        assert network != null;
        network.sendPacket(
                new CPlayerPacket.PositionPacket(
                        player.getPosX(),
                        player.getPosY(),
                        player.getPosZ(),
                        false
                )
        );
        network.sendPacket(
                new CPlayerPacket.PositionPacket(
                        player.getPosX(),
                        player.getPosY() + 3.25,
                        player.getPosZ(),
                        false
                )
        );
        network.sendPacket(
                new CPlayerPacket.PositionPacket(
                        player.getPosX(),
                        player.getPosY(),
                        player.getPosZ(),
                        false
                )
        );
        network.sendPacket(
                new CPlayerPacket.PositionPacket(
                        player.getPosX(),
                        player.getPosY(),
                        player.getPosZ(),
                        true
                )
        );
        speed = MovementUtil.getDumberSpeed();
        player.setMotion(player.getMotion().x, 0.17, player.getMotion().z);
    }

    @SuppressWarnings("unused")
    @EventTarget
    public void onMotion(EventMotion event) {
        ClientPlayerEntity player = mc.player;
        ClientPlayNetHandler network = mc.getConnection();
        assert player != null;
        assert network != null;
        switch (mode.currentValue) {
            case "Glide":
                if (!MovementUtil.isMoving()) {
                    MovementUtil.stop();
                }

                if (!player.onGround) MovementUtil.strafe(0.334);

                if (mc.gameSettings.keyBindJump.pressed) {
                    if (player.ticksExisted % 2 == 0) {
                        player.setMotion(player.getMotion().x, 0.42F, player.getMotion().z);
                    }
                } else {
                    player.setMotion(player.getMotion().x, Math.max(player.getMotion().y, -0.09800000190734863), player.getMotion().z);
                }
                break;
            case "Fast":
                break;
        }
    }
}
