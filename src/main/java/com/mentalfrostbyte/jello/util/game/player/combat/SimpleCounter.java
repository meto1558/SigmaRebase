package com.mentalfrostbyte.jello.util.game.player.combat;

public class SimpleCounter {

    private long lastTimestamp = System.currentTimeMillis();

    public void reset() {
        lastTimestamp = System.currentTimeMillis();
    }

    public boolean hasElapsed(long duration, boolean reset) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTimestamp >= duration) {
            if (reset) {
                this.lastTimestamp = currentTime;
            }
            return true;
        }
        return false;
    }

}
