package com.mentalfrostbyte.emulator;

import com.mentalfrostbyte.emulator.handler.SigmaHandler;
import com.mentalfrostbyte.jello.Client;
import com.mewebstudio.captcha.Captcha;
import com.mewebstudio.captcha.Config;
import com.mewebstudio.captcha.GeneratedCaptcha;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Emulator {

    public static Captcha captcha;
    public static GeneratedCaptcha generatedCaptcha;

    public static void start() throws IOException {
        Client.getInstance().getLogger().info("Starting Sigma Emulator v3");
        final Config config = new Config();
        config.setWidth(70);
        config.setHeight(70);
        captcha = new Captcha(config);
        generatedCaptcha = captcha.generate();

        Client.getInstance().getLogger().info("Generated a new captcha with the code: " + generatedCaptcha.getCode());

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
