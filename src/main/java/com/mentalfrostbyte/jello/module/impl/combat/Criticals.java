package com.mentalfrostbyte.jello.module.impl.combat;

import com.mentalfrostbyte.jello.module.data.ModuleCategory;
import com.mentalfrostbyte.jello.module.data.ModuleWithModuleSettings;
import com.mentalfrostbyte.jello.module.impl.combat.criticals.*;

public class Criticals extends ModuleWithModuleSettings {
    public Criticals() {
        super(
                ModuleCategory.COMBAT,
                "Criticals",
                "Automatically does criticals without jumping",
                new PacketCriticals(),
                new HoverCriticals(),
                new MinemenCriticals()
        );
    }
}