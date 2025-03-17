package com.mentalfrostbyte.jello.managers;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.util.client.render.Resources;

import java.io.InputStream;
import java.util.*;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

public class SoundManager {
    private static final String fileType = ".mp3";
    private static final List<String> VALID_SOUNDS = new ArrayList<>(
            Arrays.asList("activate", "deactivate", "click", "error", "pop", "connect", "switch", "clicksound")
    );

    public void play(String url) {
        if (!VALID_SOUNDS.contains(url)) {
            Client.getInstance().getLogger().warn("Invalid audio file attempted to be played: " + url);
        } else {
            try {
                InputStream audioStream = Resources.readInputStream("com/mentalfrostbyte/gui/resources/audio/" + url + fileType);

                AdvancedPlayer player = new AdvancedPlayer(audioStream);
                new Thread(() -> {
                    try {
                        player.play();
                    } catch (JavaLayerException e) {
                        Client.getInstance().getLogger().error("Error playing audio file: " + url + " : " + e.getMessage());
                    }
                }).start();
            } catch (JavaLayerException e) {
                Client.getInstance().getLogger().error("Unsupported audio file: " + url + " : " + e.getMessage());
            }
        }
    }

    public void init() {
    }
}
