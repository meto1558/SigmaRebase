package com.mentalfrostbyte.jello.util.minecraft;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CChatMessagePacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

public class MinecraftUtil {

    public static final Minecraft mc = Minecraft.getInstance();

    public static void addChatMessage(String text) {
        StringTextComponent textComp = new StringTextComponent(text);
        mc.ingameGUI.getChatGUI().printChatMessage(textComp);
    }

    public static void swing(Entity target, boolean swing) {
        if (target == null) {
            return;
        }

        boolean isOnePointEight = ViaLoadingBase.getInstance().getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8);

        if (isOnePointEight && swing) {
            mc.player.swingArm(Hand.MAIN_HAND);
        }

        boolean canSwing = (double) mc.player.getCooledAttackStrength(0.5F) > 0.9 || isOnePointEight;

        mc.player.resetCooldown();
        if (!isOnePointEight && swing && canSwing) {
            mc.player.swingArm(Hand.MAIN_HAND);
        }

        mc.playerController.attackEntity(mc.player, target);
    }

    public static void sendChatMessage(String text) {
        mc.getConnection().sendPacket(new CChatMessagePacket(text));
    }

    public static void block() {
        mc.getConnection().sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
        mc.getConnection().sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
    }

    public static void unblock() {
        mc.getConnection().sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), Direction.DOWN));
    }

    public static double method17750() {
        return Math.random() * 1.0E-8;
    }
}
