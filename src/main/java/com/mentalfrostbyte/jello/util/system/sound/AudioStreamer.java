package com.mentalfrostbyte.jello.util.system.sound;

import java.io.InputStream;

public record AudioStreamer(MusicStream musicStream, InputStream inputStream) implements Runnable {

    @Override
    public void run() {
        int bytesRead;
        byte[] buffer = new byte[16384];

        try {
            while ((bytesRead = this.inputStream.read(buffer)) != -1 && !Thread.interrupted()) {
                if (bytesRead > 0) {
                    musicStream.getByteStream().write(buffer, 0, bytesRead);
                    musicStream.setBufferEnd(musicStream.getBufferEnd() + bytesRead);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!Thread.interrupted()) {
            musicStream.setEndOfStream(true);
            if (musicStream.getAudioProcessor() != null && musicStream.getByteStream() != null) {
                musicStream.getAudioProcessor()
                        .processBuffer(musicStream.getByteStream().getBuffer(), 0, musicStream.getBufferEnd());
            }
        }
    }
}
