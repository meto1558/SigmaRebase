package com.mentalfrostbyte.jello.util.client.logger;

public record TimedMessage(String message, long expirationTime) {
    public TimedMessage(String message, long expirationTime) {
        this.message = message;
        this.expirationTime = System.currentTimeMillis() + expirationTime;
    }

    public boolean hasExpired() {
        return this.getRemainingTime() < 0L;
    }

    public long getRemainingTime() {
        return this.expirationTime - System.currentTimeMillis();
    }
}
