package com.mentalfrostbyte.jello.util.client.network.auth;

import com.google.gson.JsonArray;
import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.event.impl.player.EventUpdate;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import team.sdhq.eventBus.EventBus;
import team.sdhq.eventBus.annotations.EventTarget;

import java.io.IOException;
import java.net.Proxy;
import java.util.*;

public class SigmaIRC {
    public HashMap<UUID, Class8433> field36054 = new HashMap<>();
    private final Minecraft mc = Minecraft.getInstance();
    private final List<UUID> field36053 = new ArrayList<>();

    public SigmaIRC() {
        EventBus.register(this);
    }

    public static Minecraft method29522(SigmaIRC var0) {
        return var0.mc;
    }

    public static void method29523(SigmaIRC var0, GameProfile var1, String var2) throws AuthenticationException, IOException {
        var0.method29520(var1, var2);
    }


    @EventTarget
    public void method29513(EventUpdate tickEvent) {
        if (this.mc.player.ticksExisted % 100 == 0) {
            this.method29514();
            List<AbstractClientPlayerEntity> var4 = this.mc.world.getPlayers();

            var4.removeIf(var6 -> this.field36053.contains(var6.getUniqueID())
                    || Client.getInstance().botManager.isBot(var6)
                    || var6.getName().getUnformattedComponentText().isEmpty());

            if (!var4.isEmpty()) {
                Iterator<AbstractClientPlayerEntity> var10 = var4.iterator();
                int var7 = 0;
                JsonArray var8 = new JsonArray();

                while (var10.hasNext() && var7++ < 70) {
                    Entity var9 = var10.next();
                    var8.add(var9.getName().getUnformattedComponentText());
                    this.field36053.add(var9.getUniqueID());
                }
            }
        }
    }

    private void method29514() {
        Iterator<UUID> var3 = this.field36053.iterator();

        while (var3.hasNext()) {
            if (this.mc.world.getPlayerByUuid(var3.next()) == null) {
                var3.remove();
            }
        }

        Iterator<UUID> var4 = this.field36054.keySet().iterator();

        while (var3.hasNext()) {
            if (this.mc.world.getPlayerByUuid(var4.next()) == null) {
                var4.remove();
            }
        }
    }

    private void method29520(GameProfile var1, String var2) throws AuthenticationException, IOException {
        String var5 = this.mc.getSession().getToken();
        YggdrasilAuthenticationService var6 = new YggdrasilAuthenticationService(Proxy.NO_PROXY, var5);
        YggdrasilMinecraftSessionService var7 = (YggdrasilMinecraftSessionService) var6.createMinecraftSessionService();
        var7.joinServer(var1, var5, var2);
        Client.getInstance();
        System.out.println("Jello Connect: successfully reached out mojangs servers " + var2);
        System.out
                .println("https://sessionserver.mojang.com/session/minecraft/hasJoined?serverId=" + var2 + "&username=" + this.mc.getSession().getUsername());
    }

    /**
     * Default profile idk
     */
    public static class Class8433 {
        public String field36141;
        public String field36142;
        public String field36143;

        public Class8433(String var1, String var2, String var3) {
            this.field36141 = var1;
            this.field36142 = var2;
            this.field36143 = var3;
        }
    }
}
