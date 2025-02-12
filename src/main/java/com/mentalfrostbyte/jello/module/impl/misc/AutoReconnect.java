package com.mentalfrostbyte.jello.module.impl.misc;

import com.mentalfrostbyte.jello.event.impl.game.world.EventLoadWorld;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import net.minecraft.client.multiplayer.ServerData;
import team.sdhq.eventBus.annotations.EventTarget;

public class AutoReconnect extends Module {

    public ServerData serverData;

    public AutoReconnect() {
        super(ModuleCategory.MISC, "AutoReconnect", "Automatically reconnects you to the server if you got disconnected");
    }

    @EventTarget
    public void worldLoad(EventLoadWorld event) {
        if(mc.getCurrentServerData() != null) {
            serverData = mc.getCurrentServerData();
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.getCurrentServerData() != null) {
            serverData = mc.getCurrentServerData();
        }
    }


}

