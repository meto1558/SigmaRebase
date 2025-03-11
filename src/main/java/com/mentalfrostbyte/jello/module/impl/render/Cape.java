package com.mentalfrostbyte.jello.module.impl.render;

import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import com.mentalfrostbyte.jello.module.settings.impl.ModeSetting;
import com.mentalfrostbyte.jello.module.settings.impl.NumberSetting;

public class Cape extends Module {

    public Cape() {
        super(ModuleCategory.RENDER, "Cape", "Enable and customize a custom cape.");
        registerSetting(new ModeSetting("Cape", "Select a cape design.", 0, "Minecraft","Monkey","Spade"));
        registerSetting(new NumberSetting("Movement Factor", "Adjusts cape motion sensitivity.", 1, Float.class, 1, 3.0F, 0.1f));

    }


}