package com.mentalfrostbyte.jello.module.impl.misc.gameplay;

import com.mentalfrostbyte.jello.event.impl.ReceivePacketEvent;
import com.mentalfrostbyte.jello.misc.TimedMessage;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import com.mentalfrostbyte.jello.module.impl.misc.GamePlay;
import com.mentalfrostbyte.jello.module.settings.impl.BooleanSetting;
import com.mentalfrostbyte.jello.util.MultiUtilities;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.STeamsPacket;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import org.apache.commons.lang3.StringUtils;
import team.sdhq.eventBus.annotations.EventTarget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class HypixelGamePlay extends Module {
    private GamePlay parentModule;

    public HypixelGamePlay() {
        super(ModuleCategory.MISC, "Hypixel", "Gameplay for Hypixel");
        this.registerSetting(new BooleanSetting("FriendAccept", "Automatically accept friend requests", false));
        this.registerSetting(new BooleanSetting("Hide infos", "Hide scoreboard server informations & date when ingame", false));
    }

    @Override
    public void initialize() {
        this.parentModule = (GamePlay) this.access();
    }

    @EventTarget
    private void onReceive(ReceivePacketEvent event) {
        if (mc.player != null) {
            IPacket<?> packet = event.getPacket();
            if (packet instanceof SChatPacket chatPacket) {
                String var6 = chatPacket.getChatComponent().getString().replaceAll("§.", "");
                if (chatPacket.getType() != ChatType.SYSTEM && chatPacket.getType() != ChatType.CHAT) {
                    return;
                }

                String playerName = mc.player.getName().getString().toLowerCase();

                if (this.parentModule.getBooleanValueFromSettingName("AutoL")) {
                    String[] var8 = new String[]{"MULTI ", "PENTA ", "QUADRA ", "TRIPLE ", "DOUBLE ", ""};
                    boolean var9 = false;

                    for (int i = 0; i < 6; i++) {
                        if (var6.startsWith(var8[i] + "KILL! ")) {
                            var9 = true;
                            break;
                        }
                    }

                    if (var9) {
                        String[] var33 = var6.split(" ");
                        if (var33.length > 3) {
                            this.parentModule.processAutoLMessage(var33[3]);
                        }
                    }

                    if (var6.toLowerCase().contains("was killed by " + playerName)
                            || var6.toLowerCase().contains("was thrown into the void by " + playerName + ".")
                            || var6.toLowerCase().contains("was thrown off a cliff by " + playerName + ".")
                            || var6.toLowerCase().contains("was struck down by " + playerName + ".")
                            || var6.toLowerCase().contains("be sent to davy jones' locker by " + playerName + ".")) {
                        Scoreboard scoreboard = mc.world.getScoreboard();
                        ScoreObjective scoreobjective = null;
                        ScorePlayerTeam playerTeam = scoreboard.getPlayersTeam(mc.player.getScoreboardName());
                        if (playerTeam != null) {
                            int var13 = playerTeam.getColor().getColorIndex();
                            if (var13 >= 0) {
                                scoreobjective = scoreboard.getObjectiveInDisplaySlot(3 + var13);
                            }
                        }

                        ScoreObjective var38 = scoreobjective != null ? scoreobjective : scoreboard.getObjectiveInDisplaySlot(1);
                        Collection<Score> var14 = scoreboard.getSortedScores(var38);
                        int var15 = -1;

                        label155:
                        for (Score var17 : var14) {
                            ScorePlayerTeam var18 = scoreboard.getPlayersTeam(var17.getPlayerName());
                            String var19 = ScorePlayerTeam.func_237500_a_(var18, new StringTextComponent(var17.getPlayerName())).getString().replaceAll("§t", "");

                            for (String var22 : new ArrayList<String>(Arrays.asList("players left", "joueurs restants", "spieler verbleibend"))) {
                                if (var19.toLowerCase().contains(var22 + ":")) {
                                    String[] var23 = var19.split(" ");
                                    if (var23.length > 2) {
                                        try {
                                            var15 = Integer.parseInt(var23[2]);
                                        } catch (NumberFormatException var25) {
                                        }
                                        break label155;
                                    }
                                }
                            }
                        }

                        if (var15 > 2) {
                            this.parentModule.processAutoLMessage(var6);
                        }
                    }
                }

                if (this.getBooleanValueFromSettingName("FriendAccept") && var6.contains("[ACCEPT] - [DENY] - [IGNORE]")) {
                    for (ITextComponent var31 : chatPacket.getChatComponent().getSiblings()) {
                        ClickEvent var35 = var31.getStyle().getClickEvent();
                        if (var35 != null && var35.getAction() == ClickEvent.Action.RUN_COMMAND && var35.getValue().contains("/f accept")) {
                            MultiUtilities.sendChatMessage(var35.getValue());
                        }
                    }
                }

                if (var6.contains("Want to play again? Click here! ") || var6.contains("coins! (Win)")) {
                    if (this.parentModule.getBooleanValueFromSettingName("Auto Join")) {
                        for (ITextComponent var32 : chatPacket.getChatComponent().getSiblings()) {
                            ClickEvent var36 = var32.getStyle().getClickEvent();
                            if (var36 != null && var36.getAction() == ClickEvent.Action.RUN_COMMAND) {
                                TimedMessage var37 = new TimedMessage(var36.getValue(), (long) this.parentModule.getNumberValueBySettingName("Auto Join delay") * 1000L);
                                this.parentModule.updateTimedMessage(var37);
                            }
                        }
                    }

                    if (this.parentModule.getBooleanValueFromSettingName("AutoGG")) {
                        this.parentModule.initializeAutoL();
                    }
                }
            } else if (packet instanceof STeamsPacket && this.getBooleanValueFromSettingName("Hide infos")) {
                STeamsPacket var26 = (STeamsPacket) packet;
                if (var26.getAction() == 2 && var26.getName().startsWith("team_")) {
                    String var27 = var26.getPrefix().getString() + var26.getSuffix().getString();
                    String[] var28 = var27.split(" ");
                    if (var28 != null && var28.length > 1 && StringUtils.countMatches(var28[0], "/") == 2) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
