package com.mentalfrostbyte.emulator.handler;

import com.mentalfrostbyte.emulator.utils.FileUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SigmaHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().toString();
        String response = "";

        if (uri.contains("/profiles.php?v=")) {
            response = "[\"Intave\", \"BlocksMC\", \"LibreCraft\", \"Mineland\", \"Minemenclub\", \"NoRules\", \"Vulcan\", \"Old Hypixel\"]";
            exchange.sendResponseHeaders(202, response.length());
        } else {
            if (uri.contains("/profiles/") && uri.contains(".profile")) {
                String[] servers = {"Intave", "BlocksMC", "LibreCraft", "Mineland", "Minemenclub", "NoRules", "Vulcan", "Old Hypixel"};

                for (String server : servers) {
                    if (uri.contains(server)) {
                        response = getConfig(server);
                        exchange.sendResponseHeaders(202, response.length());
                        break;
                    }
                }
            }
        }

        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private String getConfig(String configName) {
        try {
            InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("profiles/" + configName + ".profile");
            String content = "";

            if (inputStream != null) {
                content = FileUtil.toString_ByteArrayOutputStream(inputStream);
            }

            return content;
        } catch (IOException e) {
            return "{\"error\": \"Invalid request\"}";
        }
    }
}
