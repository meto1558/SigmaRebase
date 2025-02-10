package com.mentalfrostbyte.jello.managers;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.util.client.network.auth.Account;
import com.mentalfrostbyte.jello.util.client.network.auth.SigmaIRC;
import org.apache.commons.io.FileUtils;
import team.sdhq.eventBus.EventBus;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class NetworkManager {

    public Account account;
    public String field38425;
    public SigmaIRC sigmaIRC;

    public static boolean premium = false;

    public NetworkManager() {
        File license = new File("jello/jello.lic");
        if (license.exists()) {
            try {
                String licenseContent = Files.readString(license.toPath()).trim();
                Map<String, Boolean> licenseData = parseLicense(licenseContent);

                String username = licenseData.keySet().iterator().next();
                premium = licenseData.get(username);

                this.loadLicense(username);
            } catch (IOException ignored) {
            }
        } else {
            try {
                handleLoginResponse(System.getProperty("user.name"));
            } catch (IOException ignored) {
            }
        }
    }

    public void init() {
        EventBus.register(this);
        this.sigmaIRC = new SigmaIRC();
    }

    public String newAccount(String username, String password) {
        if (username == null || username.isEmpty() || username.isBlank()) {
            return "Unexpected error";
        } else {
            loadLicense(username);
            return null;
        }
    }

    public void signup(String username, String password, String email) {
        resetLicense();
    }

    public String redeemPremium(String s) {
        String username = account.username;
        resetLicense();
        premium = true;
        try {
            handleLoginResponse(username);
        } catch (IOException ignored) {
        }
        return null;
    }

    public void loadLicense(String username) {
        if (this.account != null) {
            return;
        }

        File file = new File("jello/jello.lic");

        if (file.exists()) {
            this.account = new Account(username);
            Client.getInstance().getLogger().setThreadName("Logged in!");
        } else {
            try {
                handleLoginResponse(username);
                Client.getInstance().getLogger().setThreadName("Logged in!");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void resetLicense() {
        account = null;
        premium = false;

        File file = new File("jello/jello.lic");
        if (file.exists()) {
            file.delete();
        }
    }

    public void handleLoginResponse(String username) throws IOException {
        this.account = new Account(username);
        String full = username + ":" + premium;
        FileUtils.writeByteArrayToFile(new File("jello/jello.lic"), full.getBytes(StandardCharsets.UTF_8));
    }

    public boolean isPremium() {
        return premium;
    }

    private Map<String, Boolean> parseLicense(String content) {
        Map<String, Boolean> result = new HashMap<>();
        String[] parts = content.split(":");
        if (parts.length == 2) {
            String username = parts[0].trim();
            boolean value = Boolean.parseBoolean(parts[1].trim());
            result.put(username, value);
        }
        return result;
    }
}
