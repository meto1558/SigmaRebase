package com.mentalfrostbyte.jello.module.impl.movement.clicktp;

import com.mentalfrostbyte.jello.event.impl.game.action.EventClick;
import com.mentalfrostbyte.jello.event.impl.game.render.EventRender3D;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import com.mentalfrostbyte.jello.util.client.ClientColors;
import com.mentalfrostbyte.jello.util.game.player.MovementUtil2;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.game.world.BoundingBox;
import com.mentalfrostbyte.jello.util.game.world.blocks.BlockUtil;
import com.mentalfrostbyte.jello.util.system.math.counter.TimerUtil;
import com.mentalfrostbyte.jello.util.system.math.vector.Vector3d;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import team.sdhq.eventBus.annotations.EventTarget;

import java.util.ArrayList;
import java.util.List;

public class MinibloxClickTP extends Module {
    private final List<com.mentalfrostbyte.jello.util.system.math.vector.Vector3d> positions = new ArrayList<com.mentalfrostbyte.jello.util.system.math.vector.Vector3d>();
    private final TimerUtil timer = new TimerUtil();

    public MinibloxClickTP() {
        super(ModuleCategory.MOVEMENT, "Miniblox", "Click TP for Miniblox");
    }

    @Override
    public void onEnable() {
        this.positions.clear();
    }

    @Override
    public void onDisable() {
        this.positions.clear();
    }

    @EventTarget
    public void onClick(EventClick event) {
        if (this.isEnabled() && (mc.player.isSneaking() || !this.access().getBooleanValueFromSettingName("Sneak"))) {
            if (event.getButton() == EventClick.Button.RIGHT) {
                BlockRayTraceResult var4 = BlockUtil.rayTrace(
                        mc.player.rotationYaw, mc.player.rotationPitch,
                        this.access().getNumberValueBySettingName("Maximum range"));
                BlockPos hit = var4.getPos();

                double targetX = (double) hit.getX() + 0.5;
                double targetY = hit.getY() + 1;
                double targetZ = (double) hit.getZ() + 0.5;
                double curX = mc.player.getPosX();
                double curZ = mc.player.getPosZ();
                double curY = mc.player.getPosY();
                this.positions.clear();
                this.positions.add(new com.mentalfrostbyte.jello.util.system.math.vector.Vector3d(curX, curY, curZ));

                this.positions.add(new com.mentalfrostbyte.jello.util.system.math.vector.Vector3d(targetX, targetY, targetZ));
                mc.player.setPosition(targetX, targetY, targetZ);
                for (int i = 0; i < 5; i++) {
                    Vector3d pos = new Vector3d(targetX + 15 + (i * 0.2),
                            targetY + (i * 1.12),
                            targetZ);
                    this.positions.add(pos);
                    mc.getConnection().sendPacket(
                            new CPlayerPacket.PositionPacket(
                                    pos.x,
                                    pos.y,
                                    pos.z,
                                    false
                            )
                    );
                }
                mc.player.setPosition(targetX, targetY, targetZ);
                mc.player.setMotion(mc.player.getMotion().x, 0.42f, mc.player.getMotion().z);
                this.timer.reset();
                this.timer.start();
                if (this.access().getBooleanValueFromSettingName("Auto Disable")) {
                    this.access().toggle();
                }
            }
        }
    }

    @EventTarget
    public void onRender3D(EventRender3D event) {
        if (this.isEnabled() && this.positions != null && this.positions.size() != 0) {
            if (this.timer.getElapsedTime() > 4000L) {
                this.timer.stop();
                this.timer.reset();
                this.positions.clear();
            }

            GL11.glPushMatrix();
            GL11.glEnable(2848);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3042);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glEnable(32925);
            GL11.glLineWidth(1.4F);
            GL11.glColor4d(1.0, 1.0, 1.0, 1.0);
            GL11.glBegin(3);

            for (com.mentalfrostbyte.jello.util.system.math.vector.Vector3d var5 : this.positions) {
                GL11.glVertex3d(
                        var5.getX() - mc.gameRenderer.getActiveRenderInfo().getPos().getX(),
                        var5.getY() - mc.gameRenderer.getActiveRenderInfo().getPos().getY(),
                        var5.getZ() - mc.gameRenderer.getActiveRenderInfo().getPos().getZ());
            }

            GL11.glEnd();

            for (com.mentalfrostbyte.jello.util.system.math.vector.Vector3d position : this.positions) {
                BoundingBox bb = getRenderBoundingBox(position);
                RenderUtil.render3DColoredBox(bb,
                        MovementUtil2.applyAlpha(ClientColors.PALE_ORANGE.getColor(), 0.2F));
            }

            GL11.glPushMatrix();
            GL11.glTranslated(
                    mc.gameRenderer.getActiveRenderInfo().getPos().getX(),
                    mc.gameRenderer.getActiveRenderInfo().getPos().getY(),
                    mc.gameRenderer.getActiveRenderInfo().getPos().getZ());
            GL11.glPopMatrix();
            GL11.glDisable(3042);
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDisable(32925);
            GL11.glDisable(2848);
            GL11.glPopMatrix();
        }
    }

    private static @NotNull BoundingBox getRenderBoundingBox(Vector3d pos) {
        double renderPosX = pos.getX() - mc.gameRenderer.getActiveRenderInfo().getPos().getX();
        double renderPosZ = pos.getZ() - mc.gameRenderer.getActiveRenderInfo().getPos().getZ();
        return new BoundingBox(
                renderPosX - 0.3F,
                pos.getY() - mc.gameRenderer.getActiveRenderInfo().getPos().getY(),
                renderPosZ - 0.3F,
                renderPosX + 0.3F,
                pos.getY() - mc.gameRenderer.getActiveRenderInfo().getPos().getY() + 1.6F,
                renderPosZ + 0.3F);
    }
}