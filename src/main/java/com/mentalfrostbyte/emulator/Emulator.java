package com.mentalfrostbyte.emulator;

import com.mentalfrostbyte.emulator.handler.SigmaHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Emulator {

    public static void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);
        server.createContext("/", new SigmaHandler());
        server.setExecutor(null);
        server.start();

        synchronized (Emulator.class) {
            try {
                Emulator.class.wait();
            } catch (InterruptedException e) {
                System.err.println("Server interrupted: " + e.getMessage());
            }
        }
    }

}
