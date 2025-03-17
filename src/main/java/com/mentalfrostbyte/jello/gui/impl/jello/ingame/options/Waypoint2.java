package com.mentalfrostbyte.jello.gui.impl.jello.ingame.options;

import com.google.gson.JsonParseException;
import totalcross.json.JSONObject;

public class Waypoint2 {
    public String name;
    public int x;
    public int z;
    public int color;
    public float field35893 = 64.0F;
    public final boolean config;

    public Waypoint2(String var1, int var2, int var3, int var4) {
        this.x = var2;
        this.z = var3;
        this.name = var1;
        this.color = var4;
        this.config = true;
    }

    public Waypoint2(String var1, int var2, int var3, int var4, int var5) {
        this.x = var2;
        this.z = var4;
        this.field35893 = (float) var3;
        this.name = var1;
        this.color = var5;
        this.config = false;
    }

    public Waypoint2(JSONObject var1) throws JsonParseException {
        if (var1.has("name")) {
            this.name = var1.getString("name");
        }

        if (var1.has("color")) {
            this.color = var1.getInt("color");
        }

        if (var1.has("x")) {
            this.x = var1.getInt("x");
        }

        if (var1.has("z")) {
            this.z = var1.getInt("z");
        }

        this.config = true;
    }

    public JSONObject method29263() {
        JSONObject var3 = new JSONObject();
        var3.put("name", this.name);
        var3.put("color", this.color);
        var3.put("x", this.x);
        var3.put("z", this.z);
        return var3;
    }

    @Override
    public boolean equals(Object var1) {
        if (this == var1) {
            return true;
        } else if (var1 != null && this.getClass() == var1.getClass()) {
            Waypoint2 var4 = (Waypoint2) var1;
            return var4.name.equals(this.name)
                    && var4.x == this.x
                    && var4.z == this.z
                    && var4.color == this.color;
        } else {
            return false;
        }
    }
}
