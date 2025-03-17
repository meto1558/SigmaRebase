package com.mentalfrostbyte.jello.managers.util.account.microsoft;

import com.google.gson.JsonParseException;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import totalcross.json.JSONObject;
import net.minecraft.client.Minecraft;

import java.util.Calendar;
import java.util.Date;

public class Ban {
    private final String serverIP;
    private final Date date;

    public Ban(String serverIP, Date date) {
        this.serverIP = serverIP;
        this.date = date;
    }

    public Ban(JSONObject var1) throws JsonParseException {
        Calendar var4 = Calendar.getInstance();
        long bannedUntil;
        if (!(var1.get("until") instanceof Integer)) {
            bannedUntil = (Long) var1.get("until");
        } else {
            bannedUntil = ((Integer) var1.get("until")).longValue();
        }

        if (bannedUntil == 1L) {
            bannedUntil = 9223372036854775806L;
        }

        var4.setTimeInMillis(bannedUntil);
        this.serverIP = var1.getString("server");
        this.date = var4.getTime();
    }

    public JSONObject asJSONObject() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("server", this.serverIP);
        jsonObj.put("until", this.date.getTime());
        return jsonObj;
    }

    public String getServerIP() {
        return this.serverIP;
    }

    public Date getDate() {
        return this.date;
    }

    public ServerData getServer() {
        ServerList serverList = new ServerList(Minecraft.getInstance());
        serverList.loadServerList();
        int count = serverList.countServers();

        for (int i = 0; i < count; i++) {
            ServerData serverData = serverList.getServerData(i);
            if (serverData.serverIP.equals(this.serverIP)) {
                return serverData;
            }
        }

        return null;
    }
}
