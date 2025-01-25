package com.mentalfrostbyte.jello.module.impl.world.disabler;

import com.mentalfrostbyte.jello.event.impl.ReceivePacketEvent;
import com.mentalfrostbyte.jello.event.impl.SendPacketEvent;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import com.mentalfrostbyte.jello.module.impl.player.NoFall;
import com.mentalfrostbyte.jello.module.settings.impl.NumberSetting;
import com.mentalfrostbyte.jello.util.MinecraftUtil;
import com.mentalfrostbyte.jello.util.player.Rotations;
import com.mentalfrostbyte.jello.util.player.Rots;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CClientStatusPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.server.SEntityHeadLookPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.util.math.vector.Vector3d;
import team.sdhq.eventBus.annotations.EventTarget;

import java.util.Objects;

public class MinibloxDisabler extends Module {
//    public static RemoteClientPlayerEntity clientPlayerEntity;
    private final NumberSetting<Integer> clearDelay;
    private boolean waitForPos;
    private long lastWait;
    private Vector3d serverPos;
    private Rotations serverRot;

    public MinibloxDisabler() {
        super(ModuleCategory.EXPLOIT, "Miniblox", "Shitty & Horrible but working disabler for Miniblox.");
        this.registerSetting(this.clearDelay = new NumberSetting<>("Clear Threshold Delay", "Delay", 20, Integer.class, 1, 25, 1));
    }

//    void updateServerPlayer() {
//        assert mc.player != null;
//        clientPlayerEntity = new RemoteClientPlayerEntity(mc.world, mc.player.getGameProfile());
//        clientPlayerEntity.inventory = mc.player.inventory;
//        clientPlayerEntity.setPositionAndRotation(serverPos.x, serverPos.y, serverPos.z, serverRot.yaw, serverRot.pitch);
//        clientPlayerEntity.rotationYawHead = mc.player.rotationYawHead;
//        clientPlayerEntity.collidedHorizontally = false;
//        clientPlayerEntity.collidedVertically = false;
//    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.player == null) return;
        serverPos = new Vector3d(mc.player.lastReportedPosX, mc.player.lastReportedPosY, mc.player.lastReportedPosZ);
        serverRot = new Rotations(mc.player.lastReportedYaw, mc.player.lastReportedPitch);
//        updateServerPlayer();
//        assert mc.world != null;
//        mc.world.addEntity(-2, clientPlayerEntity);
    }

    @Override
    public void onDisable() {
        super.onDisable();
//        if (clientPlayerEntity != null) {
//            clientPlayerEntity.remove();
//            mc.world.removeEntityFromWorld(clientPlayerEntity.getEntityId());
//            clientPlayerEntity = null;
//        }
    }

    public void onSendPacket(SendPacketEvent event) {
        if (event.getPacket() instanceof CClientStatusPacket packet && packet.getStatus() == CClientStatusPacket.State.PERFORM_RESPAWN) {
            waitForPos = true;
        }
    }
    @SuppressWarnings("unused")
    @EventTarget
    public void onReceivedPacket(ReceivePacketEvent event) {
        IPacket<?> rawPacket = event.getPacket();
        if (rawPacket instanceof SRespawnPacket) {
            waitForPos = true;
            lastWait = System.currentTimeMillis();
            return;
        }
        if (mc.player == null) return;
        if (rawPacket instanceof SEntityHeadLookPacket) {
            event.cancelled = true;
        }
        if (rawPacket instanceof SPlayerPositionLookPacket packet
                && mc.getConnection() != null
                && mc.player.ticksExisted >= 100
                && !NoFall.falling) {
            if (waitForPos || System.currentTimeMillis() - lastWait <= clearDelay.currentValue) {
                MinecraftUtil.addChatMessage("Accepting pos");
                waitForPos = false;
                return;
            }
            event.cancelled = true;
            serverPos = new Vector3d(packet.getX(), packet.getY(), packet.getZ());
            serverRot = new Rotations(packet.getYaw(), packet.getPitch());
//            updateServerPlayer();
            mc.getConnection().sendPacket(new CPlayerPacket.PositionRotationPacket(
                            serverPos.x, serverPos.y, serverPos.z,
                            serverRot.yaw, serverRot.pitch, mc.player.isOnGround()
                    )
            );
            // prevent a too many packets kick, also happens to make this disabler the same as the Rise disabler :skull:
            if (!mc.player.isOnGround()) {
                mc.getConnection().sendPacket(new CPlayerPacket.PositionRotationPacket(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), Rots.yaw, Rots.pitch, mc.player.isOnGround()));
            }
            // this disabler probably performs worse with this, since if the server accepts our pos,
            // and we'll probably be far away
//            mc.getConnection().sendPacket(new CPlayerPacket.PositionRotationPacket(
//                            serverPos.x, serverPos.y, serverPos.z,
//                            serverRot.yaw, serverRot.pitch, mc.player.isOnGround()
//                    )
//            );
        } else {
            serverPos = new Vector3d(mc.player.lastReportedPosX, mc.player.lastReportedPosY, mc.player.lastReportedPosZ);
            serverRot = new Rotations(mc.player.lastReportedYaw, mc.player.lastReportedPitch);
//            clientPlayerEntity.inventory = mc.player.inventory;
//            clientPlayerEntity.collidedHorizontally = false;
//            clientPlayerEntity.collidedVertically = false;
//            clientPlayerEntity.setPositionAndRotation(serverPos.x, serverPos.y, serverPos.z, serverRot.yaw, serverRot.pitch);
//            clientPlayerEntity.rotationYawHead = mc.player.rotationYawHead;
        }
    }
}