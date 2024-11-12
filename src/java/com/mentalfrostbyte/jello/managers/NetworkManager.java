package com.mentalfrostbyte.jello.managers;

import com.mentalfrostbyte.jello.trackers.CombatTracker;

public class NetworkManager {
    public CombatTracker combatTracker;

    public void init() {
        this.combatTracker = new CombatTracker();
    }
}
