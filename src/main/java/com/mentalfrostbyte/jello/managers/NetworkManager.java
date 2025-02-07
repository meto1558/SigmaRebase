package com.mentalfrostbyte.jello.managers;

import com.mentalfrostbyte.jello.Client;
import com.mentalfrostbyte.jello.util.client.network.auth.CaptchaChecker;
import com.mentalfrostbyte.jello.util.client.network.auth.Encryptor;
import com.mentalfrostbyte.jello.util.client.network.auth.PremiumChecker;
import com.mentalfrostbyte.jello.util.client.SigmaIRC;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import team.sdhq.eventBus.EventBus;
import totalcross.json.JSONException;
import totalcross.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NetworkManager {

    public HttpClient httpClient;
    public CaptchaChecker captcha;
    public Encryptor encryptor;
    public String field38425;
    public SigmaIRC sigmaIRC;

    public String mainURL;
    public String loginUrl;
    public String registerUrl;
    public String claimPremiumUrl;
    public String challengeUrl;
    public String session;
    public String token;

    public static boolean premium = false;
    public static boolean field25696 = false;

    public NetworkManager() {
        this.mainURL = "http://localhost/";
        this.loginUrl = this.mainURL + "/login";
        this.registerUrl = this.mainURL + "/register";
        this.claimPremiumUrl = this.mainURL + "/claim_premium";
        this.challengeUrl = this.mainURL + "/challenge";
        this.token = UUID.randomUUID().toString().replaceAll("-", "");
        this.httpClient = HttpClients.createDefault();
    }

    public void init() {
        EventBus.register(this);
        this.sigmaIRC = new SigmaIRC();
    }

    public String newAccount(String var1, String var2, CaptchaChecker var3) {
        String var6 = "Unexpected error";

        try {
            HttpPost var7 = new HttpPost(this.loginUrl);
            List<BasicNameValuePair> var8 = new ArrayList<>();
            var8.add(new BasicNameValuePair("username", var1));
            var8.add(new BasicNameValuePair("password", var2));
            var8.add(new BasicNameValuePair("challengeUid", var3.getChallengeUid()));
            var8.add(new BasicNameValuePair("challengeAnswer", var3.getAnswer()));
            var8.add(new BasicNameValuePair("token", this.token));
            var3.method30473(false);
            var7.setEntity(new UrlEncodedFormEntity(var8, StandardCharsets.UTF_8));
            HttpResponse var9 = this.httpClient.execute(var7);
            HttpEntity var10 = var9.getEntity();
            if (var10 != null) {
                String var15;
                try (InputStream var11 = var10.getContent()) {
                    String var13 = IOUtils.toString(var11, StandardCharsets.UTF_8);
                    JSONObject jsonInstance = new JSONObject(var13);
                    if (jsonInstance.getBoolean("success")) {
                        if (jsonInstance.has("premium")) {
                            new Thread(new PremiumChecker(jsonInstance.has("premium"))).start();
                        }

                        this.handleLoginResponse(jsonInstance);
                        return null;
                    }

                    if (jsonInstance.has("error")) {
                        var6 = jsonInstance.getString("error");
                    }

                    var15 = var6;
                }

                return var15;
            }
        } catch (IOException var28) {
            var6 = var28.getMessage();
            var28.printStackTrace();
        }

        return var6;
    }

    public void method30448(final String s, final String s2, String s3, final CaptchaChecker captchaChecker) {
        if (s3 == null) {
            s3 = "";
        }
        String s4 = "Unexpected error";
        try {
            final HttpPost httpPost = new HttpPost(this.registerUrl);
            final List<BasicNameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("username", s));
            list.add(new BasicNameValuePair("password", s2));
            list.add(new BasicNameValuePair("email", s3));
            list.add(new BasicNameValuePair("challengeUid", captchaChecker.getChallengeUid()));
            list.add(new BasicNameValuePair("challengeAnswer", captchaChecker.getAnswer()));
            list.add(new BasicNameValuePair("token", this.token));
            captchaChecker.method30473(false);
            httpPost.setEntity(new UrlEncodedFormEntity(list, StandardCharsets.UTF_8));
            final HttpEntity entity = this.httpClient.execute(httpPost).getEntity();
            if (entity != null) {
                try (final InputStream content = entity.getContent()) {
                    final JSONObject class8774 = new JSONObject(IOUtils.toString(content, StandardCharsets.UTF_8));
                    if (class8774.getBoolean("success")) {
                        this.handleLoginResponse(class8774);
                        return;
                    }
                    if (class8774.has("error")) {
                        s4 = class8774.getString("error");
                    }
                }
            }
        }
        catch (final IOException ex) {
            s4 = ex.getMessage();
            ex.printStackTrace();
        }
    }

    public String validateToken() {
        new Thread(new PremiumChecker(true)).start();
        return "Cracked";
    }

    public void loadLicense() {
        if (this.encryptor != null) {
            return;
        }
        final File file = new File("jello/jello.lic");
        if (file.exists()) {
            try {
                this.encryptor = new Encryptor(Files.readAllBytes(file.toPath()));
                if (this.encryptor.username == null || this.encryptor.username.isEmpty()) {
                    this.encryptor = null;
                }
                if (this.validateToken() != null) {
                    this.encryptor = null;
                }
                else {
                    Client.getInstance().getLogger().setThreadName("Logged in!");
                }
            }
            catch (IOException ex) {}
        }
    }

    public String redeemPremium(String s, CaptchaChecker captchaChecker) {
        String s2 = "Unexpected error";
        try {
            HttpPost httpPost = new HttpPost(this.claimPremiumUrl);
            List<BasicNameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("key", s));
            list.add(new BasicNameValuePair("challengeUid", captchaChecker.getChallengeUid()));
            list.add(new BasicNameValuePair("challengeAnswer", captchaChecker.getAnswer()));
            list.add(new BasicNameValuePair("token", this.token));
            captchaChecker.method30473(false);
            httpPost.setEntity(new UrlEncodedFormEntity(list, StandardCharsets.UTF_8));
            HttpEntity entity = this.httpClient.execute(httpPost).getEntity();
            if (entity != null) {
                try (InputStream content = entity.getContent()) {
                    JSONObject object = new JSONObject(IOUtils.toString(content, StandardCharsets.UTF_8));
                    if (object.getBoolean("success")) {
                        System.out.println(object);
                        this.handleLoginResponse(object);
                        return null;
                    }
                    if (object.has("error")) {
                        s2 = object.getString("error");
                    }
                    return s2;
                }
            }
        }
        catch (final IOException ex) {
            s2 = ex.getMessage();
            ex.printStackTrace();
        }
        return s2;
    }

    public void validateSession(final String session) {
        if (!session.equals("error")) {
            this.session = session;
        }
        else {
            this.session = null;
        }
    }

    public Encryptor method19347() {
        return this.encryptor;
    }

    public void resetLicense() {
        this.session = null;
        this.encryptor = null;
        premium = false;
        final File file = new File("jello/jello.lic");
        if (file.exists()) {
            file.delete();
        }
    }

    public CaptchaChecker getCaptcha() {
        if (this.captcha != null && this.captcha.isCompleted()) {
            return this.captcha;
        }
        try {
            final HttpPost httpPost = new HttpPost(this.challengeUrl);
            final List<BasicNameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("token", this.token));
            httpPost.setEntity(new UrlEncodedFormEntity(list, StandardCharsets.UTF_8));
            final HttpEntity entity = this.httpClient.execute(httpPost).getEntity();
            if (entity != null) {
                try (final InputStream content = entity.getContent()) {
                    final JSONObject jsonInstance = new JSONObject(IOUtils.toString(content, StandardCharsets.UTF_8));
                    if (jsonInstance.getBoolean("success")) {
                        final String uid = jsonInstance.getString("uid");
                        boolean completed = false;

                        if (jsonInstance.has("captcha")) {
                            completed = jsonInstance.getBoolean("captcha");
                        }
                        this.captcha = new CaptchaChecker(uid, completed);
                        return this.captcha;
                    }
                    return null;
                }
            }
        }
        catch (final IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void handleLoginResponse(final JSONObject JSONObject) throws JSONException {
        String authToken = null;
        String username = null;
        String agoraToken = null;
        if (JSONObject.has("username")) {
            username = JSONObject.getString("username");
        }
        if (JSONObject.has("auth_token")) {
            authToken = JSONObject.getString("auth_token");
        }
        if (JSONObject.has("agora_token")) {
            agoraToken = JSONObject.getString("agora_token");
        }
        if (authToken != null && username != null && agoraToken != null) {
            try {
                this.encryptor = new Encryptor(username, authToken, agoraToken);
                FileUtils.writeByteArrayToFile(new File("jello/jello.lic"), this.encryptor.encrypt());
            }
            catch (final IOException ex) {}
        }
        if (JSONObject.has("session")) {
            this.validateSession(JSONObject.getString("session"));
        }
    }

    public boolean isPremium() {
        return premium;
    }
}
