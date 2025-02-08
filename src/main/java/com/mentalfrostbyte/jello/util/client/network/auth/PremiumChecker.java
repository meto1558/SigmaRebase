package com.mentalfrostbyte.jello.util.client.network.auth;

import com.mentalfrostbyte.jello.Client;
import com.mentalfrostbyte.jello.managers.NetworkManager;
import com.mentalfrostbyte.jello.util.client.ClientMode;

public class PremiumChecker implements Runnable
{
    private final boolean premium;
    
    public PremiumChecker(final boolean premium) {
        this.premium = premium;
    }
    
    @Override
    public void run() {
        while (!Thread.interrupted()) {
            if (Client.getInstance().clientMode == ClientMode.INDETERMINATE) {
                try {
                    Thread.sleep(200L);
                    continue;
                }
                catch (final InterruptedException ex) {
                    break;
                }
            }
            NetworkManager.premium = this.premium;
            break;
        }
    }
}
