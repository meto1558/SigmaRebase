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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;
import org.apache.commons.lang3.StringUtils;
import team.sdhq.eventBus.annotations.EventTarget;

public class MinibloxGamePlay extends Module {
    private GamePlay parentModule;

    public MinibloxGamePlay() {
        super(ModuleCategory.MISC, "Miniblox", "Gameplay for Miniblox");
        registerSetting(new BooleanSetting("FriendAccept", "Automatically accept friend requests", false));
        registerSetting(new BooleanSetting("Hide infos", "Hide scoreboard server informations & date when ingame", false));
    }

    @Override
    public void initialize() {
        parentModule = (GamePlay) access();
    }

    @EventTarget
    public void onReceive(ReceivePacketEvent event) {
        if (mc.player != null) {
            IPacket<?> packet = event.getPacket();
            if (packet instanceof SChatPacket chatPacket) {
                String text = chatPacket.getChatComponent().getString().replaceAll("§.", "");
//                if (chatPacket.getType() != ChatType.SYSTEM && chatPacket.getType() != ChatType.GAME_INFO) {
//                    return;
//                }

                String playerName = mc.player.getName().getString().toLowerCase();

                if (parentModule.getBooleanValueFromSettingName("AutoL")) {
//                    boolean confirmedKill = false;

//                    if (text.startsWith("KILL! ")) {
//                        confirmedKill = true;
//                    }

//                    if (confirmedKill) {
//                        String[] splitText = text.split(" ");
//                        if (splitText.length > 3) {
//                            parentModule.processAutoLMessage(splitText[3]);
//                        }
//                    }

                    if (text.toLowerCase().contains("was slain by " + playerName)
                            || (text.toLowerCase().contains("has been eliminated by") && text.toLowerCase().endsWith(playerName + "!"))
                            || text.toLowerCase().contains("burned to death while fighting " + playerName)
                            || text.toLowerCase().contains("was shot by " + playerName)
                            || text.toLowerCase().contains("burnt to a crisp while fighting " + playerName)
                            || text.toLowerCase().contains("couldn't fly while escaping " + playerName)
                            || text.toLowerCase().contains("thought they could survive in the void while escaping " + playerName)
                            || text.toLowerCase().contains("fell to their death while escaping " + playerName)
                            || text.toLowerCase().contains("died in the void while escaping " + playerName)) {
                        this.parentModule.processAutoLMessage(text);
                    }
                }

                if (getBooleanValueFromSettingName("FriendAccept") && text.contains("[ACCEPT] - [DENY] - [IGNORE]")) {
                    for (ITextComponent textCom : chatPacket.getChatComponent().getSiblings()) {
                        ClickEvent clickEvent = textCom.getStyle().getClickEvent();
                        if (clickEvent != null && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND && clickEvent.getValue().contains("/f accept")) {
                            MultiUtilities.sendChatMessage(clickEvent.getValue());
                        }
                    }
                }

                if (text.contains("Click here to play again")) {
                    if (parentModule.getBooleanValueFromSettingName("Auto Join")) {
                        for (ITextComponent textCom : chatPacket.getChatComponent().getSiblings()) {
                            ClickEvent clickEvent = textCom.getStyle().getClickEvent();

                            if (clickEvent != null && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                                parentModule.updateTimedMessage(
                                        new TimedMessage(
                                                clickEvent.getValue(),
                                                (long)
                                                        parentModule.
                                                                getNumberValueBySettingName("Auto Join delay") * 1000L));
                            }
                        }
                    }

                    if (parentModule.getBooleanValueFromSettingName("AutoGG")) {
                        parentModule.initializeAutoL();
                    }
                }
            } else if (packet instanceof STeamsPacket teamsPacket && getBooleanValueFromSettingName("Hide infos")) {
                if (teamsPacket.getAction() == 2 && teamsPacket.getName().startsWith("team_")) {
                    String teamPrefixSuffixCombined = teamsPacket.getPrefix().getString() + teamsPacket.getSuffix().getString();
                    String[] splitPrefixSuffix = teamPrefixSuffixCombined.split(" ");
                    if (splitPrefixSuffix != null && splitPrefixSuffix.length > 1 && StringUtils.countMatches(splitPrefixSuffix[0], "/") == 2) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
