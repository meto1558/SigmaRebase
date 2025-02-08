package com.mentalfrostbyte.jello.managers.util.account.microsoft;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.util.system.network.ImageUtil;
import com.mentalfrostbyte.jello.util.client.render.Resources;
import org.newdawn.slick.opengl.Texture;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import net.minecraft.util.Session;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.newdawn.slick.util.BufferedImageUtil;
import totalcross.json.JSONArray;
import totalcross.json.JSONException;
import totalcross.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class Account {
    private String knownName = "Unknown name";
    private String knownUUID = "steve";
    private String email;
    private String password;
    private ArrayList<Ban> bans = new ArrayList<>();
    private long lastUsed;
    private final long dateAdded;
    private int useCount;
    private BufferedImage skin;
    private BufferedImage skinImg;
    private Texture skinTexture;
    private Thread skinUpdateThread;

    public Account(String email, String password, ArrayList<Ban> bans, String knownName) {
        this.email = email;
        this.password = password;
        this.dateAdded = System.currentTimeMillis();
        this.lastUsed = 0L;
        this.useCount = 0;
        if (bans != null) {
            this.bans = bans;
        }

        if (knownName != null) {
            this.knownName = knownName;
        }
    }

    public Account(String email, String password, ArrayList<Ban> bans) {
        this(email, password, bans, null);
    }

    public Account(String email, String password) {
        this(email, password, null, null);
    }

    public Account(JSONObject json) throws JSONException {
        if (json.has("email")) {
            this.email = json.getString("email");
        }

        if (json.has("password")) {
            this.password = decodeBase64(json.getString("password"));
        }

        if (json.has("bans")) {
            for (Object var5 : json.getJSONArray("bans")) {
                this.bans.add(new Ban((JSONObject) var5));
            }
        }

        if (json.has("knownName")) {
            this.knownName = json.getString("knownName");
        }

        if (json.has("knownUUID")) {
            this.knownUUID = json.getString("knownUUID");
        }

        if (json.has("dateAdded")) {
            this.dateAdded = json.getLong("dateAdded");
        } else {
            this.dateAdded = System.currentTimeMillis();
        }

        if (json.has("lastUsed")) {
            this.lastUsed = json.getLong("lastUsed");
        }

        if (json.has("useCount")) {
            this.useCount = json.getInt("useCount");
        }

        if (json.has("skin")) {
            byte[] var7 = parseBase64Binary(json.getString("skin"));

            try {
                this.skin = ImageIO.read(new ByteArrayInputStream(var7));
            } catch (IOException var6) {
                var6.printStackTrace();
            }
        }
    }

    /**
     * Seems to be unused. Maybe for a prototype of a clipboard login or smth
     *
     * @param email    Email
     * @param password Password
     * @return Returns the session ig
     * @throws MicrosoftAuthenticationException if the authentication fails horribly
     */
    public static Session fastLogin(String email, String password) throws MicrosoftAuthenticationException {
        return alternativeLogin(new Account(email, password));
    }

    public static CompletableFuture<Session> cookieLogin() throws MicrosoftAuthenticationException {
        MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
        CompletableFuture<MicrosoftAuthResult> future = authenticator.loginWithAsyncWebview();
        return future.thenApply(result -> new Session(
                result.getProfile().getName(),
                result.getProfile().getId(),
                result.getAccessToken(),
                "mojang"
        ));
    }

    public static Session alternativeLogin(Account account) throws MicrosoftAuthenticationException {
        if (!account.isEmailAValidEmailFormat()) {
            MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
            MicrosoftAuthResult result = authenticator.loginWithCredentials("email", "password");
            // Or using refresh token: authenticator.loginWithRefreshToken("refresh token");
            // Or using your own way: authenticator.loginWithTokens("access token", "refresh token");

            System.out.printf("Logged in with '%s'%n", result.getProfile().getName());
            account.updateUsedCount();
            return new Session(
                    result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), account.getAccountType().name()
            );
        } else {
            return new Session(account.getEmail(), "", "", "mojang");
        }
    }

    public static String encodeBase64(String s) {
        byte[] bytes = Base64.encodeBase64(s.getBytes());
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static String decodeBase64(String s) {
        byte[] bytes = Base64.decodeBase64(s.getBytes());
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public void updateUsedCount() {
        this.useCount++;
    }

    public ArrayList<Ban> getBans() {
        return this.bans;
    }

    public String getEmail() {
        return this.email;
    }

    public String getKnownName() {
        return this.knownName;
    }

    public String getName() {
        return !this.knownName.equals("Unknown name") ? this.knownName : this.email;
    }

    public String getKnownUUID() {
        return this.knownUUID;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String var1) {
        this.password = var1;
    }

    public void setEmail(String var1) {
        this.email = var1;
    }

    public void registerBan(Ban ban) {
        this.unbanFromServerIP(ban.getServerIP());
        this.bans.add(ban);
    }

    public void unbanFromServerIP(String serverIP) {
        this.bans.removeIf(ban -> ban.getServerIP().equals(serverIP));
    }

    public void setName(String name) {
        this.knownName = name;
        this.skinUpdateThread = null;
    }

    public void setKnownUUID(String var1) {
        this.knownUUID = var1;
    }

    public Texture setSkinTexture() {
        if (this.skinTexture == null && this.skin != null) {
            try {
                this.skinTexture = BufferedImageUtil.getTexture("skin", this.skin.getSubimage(8, 8, 8, 8));
            } catch (IOException var4) {
                var4.printStackTrace();
            }
        }

        return this.skinTexture != null ? this.skinTexture : Resources.skinPNG;
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (this.skinTexture != null) {
                Client.getInstance().addTexture(this.skinTexture);
            }
        } finally {
            super.finalize();
        }
    }

    public BufferedImage getSkinImg() {
        if (this.skinImg == null && this.skin != null) {
            Rectangle var3 = new Rectangle(64, 64);
            this.skinImg = new BufferedImage((int) var3.getWidth(), (int) var3.getHeight(), 3);
            Graphics2D skinImg = this.skinImg.createGraphics();
            skinImg.drawImage(this.skin, 0, 0, null);
            if (this.skin.getHeight() == 32) {
                BufferedImage skinSubImage1 = this.skin.getSubimage(0, 16, 16, 16);
                BufferedImage skinSubImage2 = this.skin.getSubimage(40, 16, 16, 16);
                skinImg.drawImage(skinSubImage1, 16, 48, null);
                skinImg.drawImage(skinSubImage2, 32, 48, null);
            }

            skinImg.dispose();
        }

        return this.skinImg;
    }

    public void updateSkin() {
        // new Date(); // Imma just leave this here
        if (!this.getKnownUUID().contains("steve") && this.skinUpdateThread == null) {
            this.skinUpdateThread = new Thread(() -> {
                try {
                    this.skin = ImageIO.read(new URL(ImageUtil.getSkinUrlByID(this.getKnownUUID().replaceAll("-", ""))));
                } catch (Exception var4) {
                    var4.printStackTrace();
                }
            });
            this.skinUpdateThread.start();
        }
    }

    /**
     * Maybe a cracked account check??
     *
     * @return Session Type
     */
    public Session.Type getAccountType() {
        return this.email.contains("@") ? Session.Type.MOJANG : Session.Type.LEGACY;
    }

    public Session login() throws MicrosoftAuthenticationException {
        if (!this.isEmailAValidEmailFormat()) {
            MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
            MicrosoftAuthResult result = authenticator.loginWithCredentials(email, password);
            System.out.printf("Logged in with '%s'%n", result.getProfile().getName());
            this.setName(result.getProfile().getName());
            this.setKnownUUID(fixUUID(result.getProfile().getId()));
            this.updateSkin();
            this.lastUsed = System.currentTimeMillis();
            return new Session(
                    result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), getAccountType().name()
            );
        } else {
            this.setName(this.getEmail());
            return new Session(this.getEmail(), "", "", "mojang");
        }
    }

    /**
     * I made this to fix autism caused by OpenAuth
     */
    public static String fixUUID(String uuidString) {
        return uuidString.replaceFirst(
                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                "$1-$2-$3-$4-$5"
        );
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("bans", this.makeBanJSONArray());
        obj.put("email", this.email);
        obj.put("password", encodeBase64(this.password));
        obj.put("knownName", this.knownName);
        obj.put("knownUUID", this.knownUUID);
        obj.put("useCount", this.useCount);
        obj.put("lastUsed", this.lastUsed);
        obj.put("dateAdded", this.dateAdded);
        if (this.skin != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Base64OutputStream base64OutputStream = new Base64OutputStream(outputStream);
            String skinBase64 = "";

            try {
                ImageIO.write(this.skin, "png", base64OutputStream);
                skinBase64 = outputStream.toString("UTF-8");
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            obj.put("skin", skinBase64);
        }

        return obj;
    }

    public static byte[] parseBase64Binary(String base64String) {
        return Base64.decodeBase64(base64String);
    }

    public JSONArray makeBanJSONArray() {
        JSONArray jsonArray = new JSONArray();

        for (Ban ban : this.bans) {
            jsonArray.put(ban.asJSONObject());
        }

        return jsonArray;
    }

    public int getUseCount() {
        return this.useCount;
    }

    public long getLastUsed() {
        return this.lastUsed;
    }

    public long getDateAdded() {
        return this.dateAdded;
    }

    public Ban getBanInfo(String serverIP) {
        for (Ban ban : this.getBans()) {
            if (ban.getServerIP().equals(serverIP)) {
                return ban;
            }
        }

        return null;
    }

    /**
     * This is definitely used for a cracked account check!!
     *
     * @return if the input email is a valid email
     */
    public boolean isEmailAValidEmailFormat() {
        if (this.getPassword().isEmpty())
            return true;
        Pattern var3 = Pattern.compile("[a-zA-Z0-9_]{2,16}");
        return var3.matcher(this.getEmail()).matches();
    }
}
