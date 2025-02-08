package com.mentalfrostbyte.jello.module.impl.misc.gameplay;

import com.mentalfrostbyte.jello.event.impl.game.network.EventReceivePacket;
import com.mentalfrostbyte.jello.event.impl.game.world.EventLoadWorld;
import com.mentalfrostbyte.jello.event.impl.player.LivingDeathEvent;
import com.mentalfrostbyte.jello.event.impl.player.movement.EventUpdateWalkingPlayer;
import com.mentalfrostbyte.jello.module.impl.misc.gameplay.miniblox.AutoBuy;
import com.mentalfrostbyte.jello.util.client.logger.TimedMessage;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import com.mentalfrostbyte.jello.module.impl.misc.GamePlay;
import com.mentalfrostbyte.jello.module.settings.impl.*;
import com.mentalfrostbyte.jello.util.game.player.MovementUtil2;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;
import team.sdhq.eventBus.annotations.EventTarget;

public class MinibloxGamePlay extends Module {
//    private final ModeSetting skywarsKit;
    private final ModeSetting autoVoteMode;
    private final BooleanSetting autoVote;
    private final BooleanSetting autoBuy;
    private final BooleanSetting oldTranslationLayerCompat;
    private GamePlay parentModule;
//    private final ModeSetting kitPvPKit;

    public MinibloxGamePlay() {
        super(ModuleCategory.MISC, "Miniblox", "Gameplay for Miniblox");
        registerSetting(new BooleanSetting("FriendAccept", "Automatically accept friend requests", false));
        registerSetting(
                this.autoVote = new BooleanSetting(
                        "AutoVote",
                        "Automatically vote on the skywars gamemode poll",
                        true
                )
        );
        registerSetting(this.oldTranslationLayerCompat = new BooleanSetting(
                "Archived Translation Layer Compatibility (7GrandDadPGN's Translation Layer)",
                "Tries to be compatible with the archived Miniblox Translation Layer by 7GrandDadPGN",
                false
        ));
        registerSetting(
                this.autoVoteMode = new ModeSetting(
                        "Mode",
                        "Mode to vote on Skywars",
                        "Insane (2)",
                        "Normal (1)",
                        "Insane (2)"
                )
        );
//        registerSetting(new BooleanSetting("AutoKit", "Automatically select kits", false));
        registerSetting(this.autoBuy = new BooleanSetting("AutoBuy", "Automatically buys stuff", false));
        registerSetting(AutoBuy.armor, AutoBuy.chainmailArmor, AutoBuy.ironArmor, AutoBuy.diamondArmor);
        registerSetting(AutoBuy.sword, AutoBuy.stoneSword, AutoBuy.ironSword, AutoBuy.diamondSword);
        registerSetting(AutoBuy.doUpgrades, AutoBuy.sharpnessUpgrade, AutoBuy.protectionUpgrade,
                AutoBuy.hasteUpgrade, AutoBuy.healPoolUpgrade,
                AutoBuy.forgeUpgrade
        );
    }

    @Override
    public void initialize() {
        parentModule = (GamePlay) access();
    }

    @SuppressWarnings("unused")
    @EventTarget
    public void onWorldEvent(EventLoadWorld e) {
        if (!autoBuy.currentValue) return;
        AutoBuy.onWorldEvent(e);
    }

    @SuppressWarnings("unused")
    @EventTarget
    public void onUpdateEvent(EventUpdateWalkingPlayer e) {
        if (!autoBuy.currentValue) return;
        AutoBuy.onUpdateEvent(e);
    }

    @SuppressWarnings("unused")
    @EventTarget
    public void onLivingDeath(LivingDeathEvent e) {
        if (!autoBuy.currentValue) return;
        AutoBuy.onLivingDeathEvent(e);
    }


    @SuppressWarnings("unused")
    @EventTarget
    public void onReceive(EventReceivePacket event) {
        if (mc.player != null) {
            IPacket<?> packet = event.getPacket();
            if (packet instanceof SChatPacket chatPacket) {
                String text = chatPacket.getChatComponent().getString().replaceAll("§.", "");
                if (!oldTranslationLayerCompat.currentValue && chatPacket.getType() != ChatType.SYSTEM) {
                    return;
                }

                String playerName = mc.player.getName().getString().toLowerCase();

                if (autoVote.currentValue && text.equals("Poll started: Choose a gamemode")) {
                    switch (autoVoteMode.currentValue) {
                        case "Normal (1)":
                            MovementUtil2.sendChatMessage("/vote 1");
                            break;
                        case "Insane (2)":
                            MovementUtil2.sendChatMessage("/vote 2");
                            break;
                    }
                }

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
                            MovementUtil2.sendChatMessage(clickEvent.getValue());
                        }
                    }
                }

                if (text.contains("The game is currently in progress. Please wait for the next game to start.")) {
                    if (parentModule.getBooleanValueFromSettingName("Auto Join")) {
                        // TODO: get the gamemode from the first line of the scoreboard
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
            }
        }
    }
}
