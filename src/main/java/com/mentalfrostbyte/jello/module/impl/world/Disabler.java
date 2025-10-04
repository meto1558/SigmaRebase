package com.mentalfrostbyte.jello.module.impl.world;

import com.mentalfrostbyte.jello.module.data.ModuleCategory;
import com.mentalfrostbyte.jello.module.data.ModuleWithModuleSettings;
import com.mentalfrostbyte.jello.module.impl.world.disabler.*;

public class Disabler extends ModuleWithModuleSettings {
    public Disabler() {
        super(ModuleCategory.WORLD,
                "Disabler",
                "Disables some anticheats",
                new PingSpoofDisabler(),
                new NullDisabler(),
                new HypixelDisabler(),
                new TPDisabler(),
                new ViperDisabler(),
                new VeltPvPDisabler(),
                new GhostlyDisabler(),
                new MinibloxDesyncDisabler(),
                new CustomDisabler(),
                new VerusTimerDisabler(),
                new VerusTestDisabler(),
                new VerusFlyDisabler()
        );
    }
}
