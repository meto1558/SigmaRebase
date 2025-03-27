package com.mentalfrostbyte.jello.util.client.network.auth;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class SigmaConfigs {

    public static void start() {
        new Thread(() -> {
            try {
                HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);
                server.createContext("/", new SigmaHandler());
                server.setExecutor(null);
                server.start();
            } catch (IOException ignored) {
                return;
            }

            synchronized (SigmaConfigs.class) {
                try {
                    SigmaConfigs.class.wait();
                } catch (InterruptedException e) {
                    System.err.println("Server interrupted: " + e.getMessage());
                }
            }
        }).start();
    }

    public static class SigmaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String uri = exchange.getRequestURI().toString();
            String response = "";

            if (uri.contains("/profiles.php?v=")) {
                response = "[\"Intave\", \"BlocksMC\", \"Old LibreCraft\", \"Old Mineland\", \"Old Minemenclub\", \"Old NoRules\", \"Old Vulcan\", \"Old Hypixel\"]";
                exchange.sendResponseHeaders(202, response.length());
            } else {
                if (uri.contains("/profiles/") && uri.contains(".profile")) {
                    String[] servers = {"Intave", "BlocksMC", "Old LibreCraft", "Old Mineland", "Old Minemenclub", "Old NoRules", "Old Vulcan", "Old Hypixel"};

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
                    content = toString_ByteArrayOutputStream(inputStream);
                }

                return content;
            } catch (IOException e) {
                return "{\"error\": \"Invalid request\"}";
            }
        }

        //from: https://sentry.io/answers/inputstream-to-string/
        private String toString_ByteArrayOutputStream(InputStream stream) throws IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];

            int readBytes = stream.read(buffer);

            while (readBytes != -1) {
                outputStream.write(buffer, 0, readBytes);
                readBytes = stream.read(buffer);
            }

            return outputStream.toString();
        }
    }

}
