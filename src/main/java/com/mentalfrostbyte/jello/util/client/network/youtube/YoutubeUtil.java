package com.mentalfrostbyte.jello.util.client.network.youtube;

import java.io.IOException;
import java.net.URL;
import java.text.Normalizer;
import java.text.Normalizer.Form;

public class YoutubeUtil {
    public static String method34955(int var0) {
        if (var0 < 0) {
            var0 = 0;
        }

        return var0 <= 3600
                ? var0 / 60 + ":" + (var0 % 60 >= 10 ? "" : "0") + var0 % 60
                : var0 / 3600 + ":" + (var0 / 60 % 60 >= 10 ? "" : "0") + var0 / 60 % 60 + ":" + (var0 % 60 >= 10 ? "" : "0") + var0 % 60;
    }

    public static URL getVideoStreamURL(String var0) {
        try {
            return new URL("https://www.youtube.com/watch?v=" + var0);
        } catch (IOException var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public static String method34963(String var0) {
        var0 = var0.replaceAll("\\(.*\\)", "");
        var0 = var0.replaceAll("\\[.*\\]", "");
        var0 = Normalizer.normalize(var0, Form.NFD);
        return var0.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
    }
}
