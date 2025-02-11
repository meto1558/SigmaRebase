package com.mentalfrostbyte.jello.util.game.player.combat;

import com.mentalfrostbyte.jello.util.game.MinecraftUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CombatUtil implements MinecraftUtil {

    public static List<PlayerEntity> getAllPlayersInWorld() {
        List<PlayerEntity> players = new ArrayList<>();
        mc.world.entitiesById.forEach((id, entity) -> {
            if (entity instanceof PlayerEntity) {
                players.add((PlayerEntity) entity);
            }
        });
        return players;
    }

    public static List<PlayerEntity> getPlayers() {
        ArrayList<PlayerEntity> players = new ArrayList<>();
        mc.world.entitiesById.forEach((entityId, entity) -> {
            if (entity instanceof PlayerEntity) {
                players.add((PlayerEntity) entity);
            }
        });
        return players;
    }

    public static void block() {
        mc.getConnection().sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
        mc.getConnection().sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
    }

    public static void unblock() {
        mc.getConnection().sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), Direction.DOWN));
    }

    public static boolean arePlayersOnSameTeam(PlayerEntity player) {
        return getPlayerTeamColorCode(mc.player) == getPlayerTeamColorCode(player);
    }

    public static int getPlayerTeamColorCode(PlayerEntity player) {
        ScorePlayerTeam team = (ScorePlayerTeam) player.getTeam();
        return team != null && team.getColor().getColor() != null ? team.getColor().getColor() : 16777215;
    }

    public static boolean isValid(LivingEntity entity, boolean raytrace, float searchRange, boolean players, boolean animals, boolean monsters, boolean invisibles) {
        if (entity == mc.player ||
                !entity.isAlive() ||
                entity.getName().getString().isEmpty() ||
                (raytrace && !mc.player.canEntityBeSeen(entity)) ||
                entity.getDistance(mc.player) > searchRange) {
            return false;
        }

        boolean validType = false;

        if (entity instanceof PlayerEntity && players) {
            validType = true;
        }

        if ((entity instanceof AnimalEntity || entity instanceof VillagerEntity) && animals) {
            validType = true;
        }

        if (entity instanceof MonsterEntity && monsters) {
            validType = true;
        }

        if (entity.isInvisible() && invisibles) {
            validType = true;
        }

        return validType;
    }

    public static Comparator<LivingEntity> getComparatorForSorting(String mode) {
        switch (mode) {
            case "Health":
                return Comparator.comparingDouble(LivingEntity::getHealth);

            case "Armor":
                return Comparator.comparingDouble(LivingEntity::getTotalArmorValue);

            case "Hurt-time":
                return Comparator.comparingDouble(LivingEntity::getHurtTime);

            case "Ticks existed":
                return Comparator.comparingDouble(LivingEntity::getTicksExisted);

            default: //Range or if null
                return Comparator.comparingDouble(e -> e.getDistance(mc.player));
        }
    }
}
