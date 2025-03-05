package com.mentalfrostbyte.jello.managers;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.event.impl.game.action.EventKeyPress;
import com.mentalfrostbyte.jello.gui.impl.others.ChatUtil;
import team.sdhq.eventBus.EventBus;
import team.sdhq.eventBus.annotations.EventTarget;
import totalcross.json.JSONArray;
import net.minecraft.entity.Entity;
import net.minecraft.client.Minecraft;
import totalcross.json.JSONException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FriendManager {
    public List<String> pureTextFriends = new CopyOnWriteArrayList<>();
    public List<String> entityFriends = new CopyOnWriteArrayList<>();
    private final Minecraft mc = Minecraft.getInstance();

    public void init() {
        EventBus.register(this);
        try {
            this.loadFromCurrentConfig();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @EventTarget
    public void method26996(EventKeyPress event) throws JSONException {
        if (event.getKey() == this.mc.gameSettings.keyBindPickBlock.keyCode.getKeyCode()
                && this.mc.pointedEntity != null
                && this.mc.pointedEntity.getName() != null) {
            CommandManager var4 = Client.getInstance().commandManager;
            var4.method30236();
            if (this.isFriendPure(this.mc.pointedEntity)) {
                this.method27005(this.mc.pointedEntity.getName().getUnformattedComponentText());
                ChatUtil.method32487(
                        var4.getPrefix() + " " + this.mc.pointedEntity.getName().getUnformattedComponentText() + " is no longer your friend."
                );
            } else {
                this.method27001(this.mc.pointedEntity.getName().getUnformattedComponentText());
                ChatUtil.method32487(var4.getPrefix() + " " + this.mc.pointedEntity.getName().getUnformattedComponentText() + " is now your friend.");
            }

            this.method27009();
        }
    }

    public boolean isFriendPure(Entity name) {
        return this.pureTextFriends.contains(name.getName().getUnformattedComponentText().toLowerCase());
    }

    public boolean isFriendPure(String name) {
        return this.pureTextFriends.contains(name.toLowerCase());
    }

    public boolean isFriend(Entity name) {
        return this.entityFriends.contains(name.getName().getUnformattedComponentText().toLowerCase());
    }

    public boolean isFriend(String var1) {
        return this.entityFriends.contains(var1.toLowerCase());
    }

    public boolean method27001(String var1) {
        if (this.isFriendPure(var1)) {
            return false;
        } else {
            this.pureTextFriends.add(var1.toLowerCase());
            this.method27009();
            return true;
        }
    }

    public boolean method27002(String var1) {
        if (this.isFriend(var1)) {
            return false;
        } else {
            this.entityFriends.add(var1.toLowerCase());
            this.method27010();
            return true;
        }
    }

    public List<String> method27003() {
        return this.pureTextFriends;
    }

    public List<String> method27004() {
        return this.entityFriends;
    }

    public boolean method27005(String var1) {
        boolean var4 = this.pureTextFriends.remove(var1.toLowerCase());
        if (var4) {
            this.method27009();
        }

        return var4;
    }

    public boolean method27006(String var1) {
        boolean var4 = this.entityFriends.remove(var1.toLowerCase());
        if (var4) {
            this.method27010();
        }

        return var4;
    }

    public boolean method27007() {
        if (!this.pureTextFriends.isEmpty()) {
            this.pureTextFriends.clear();
            this.method27009();
            return true;
        } else {
            return false;
        }
    }

    public boolean method27008() {
        if (!this.entityFriends.isEmpty()) {
            this.entityFriends.clear();
            this.method27010();
            return true;
        } else {
            return false;
        }
    }

    public void method27009() {
        Client.getInstance().getConfig().put("friends", this.pureTextFriends);
    }

    public void method27010() {
        Client.getInstance().getConfig().put("enemies", this.entityFriends);
    }

    private void loadFromCurrentConfig() throws JSONException {
        if (Client.getInstance().getConfig().has("friends")) {
            JSONArray var3 = Client.getInstance().getConfig().getJSONArray("friends");
            if (var3 != null) {
                var3.forEach(var1 -> this.pureTextFriends.add((String) var1));
            }
        }

        if (Client.getInstance().getConfig().has("enemies")) {
            JSONArray var4 = Client.getInstance().getConfig().getJSONArray("enemies");
            if (var4 != null) {
                var4.forEach(var1 -> this.entityFriends.add((String) var1));
            }
        }
    }
}