package com.mentalfrostbyte.jello.util.system.sound;

import com.mentalfrostbyte.Client;

public class BasicAudioProcessor implements AudioProcessor {
    @Override
    public void processBuffer(byte[] var1, int var2, int var3) {
        Client.getInstance().getLogger().setThreadName("DONE");
    }
}
