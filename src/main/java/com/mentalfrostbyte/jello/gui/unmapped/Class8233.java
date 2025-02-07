package com.mentalfrostbyte.jello.gui.unmapped;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.managers.util.profile.Configuration;
import com.mentalfrostbyte.jello.module.Module;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import totalcross.json.JSONArray;
import totalcross.json.JSONException;
import totalcross.json.JSONObject;

public class Class8233 {
   public static List<String> field35347;
   public Class6353 field35348;

   public Class8233(Class6353 var1) {
      this.field35348 = var1;
      new Thread(() -> {
         if (field35347 == null) {
            field35347 = this.getOnlineConfigs();
         }

         var1.method19340(field35347);
      }).start();
   }

   public List<String> getOnlineConfigs() {
      try {
         HttpGet var3 = new HttpGet("http://localhost/profiles.php?v=" + Client.RELEASE_TARGET);
         CloseableHttpResponse var4 = HttpClients.createDefault().execute(var3);
         HttpEntity var5 = var4.getEntity();
         if (var5 != null) {
            List<String> var24;
            try (InputStream var6 = var5.getContent()) {
               String var8 = IOUtils.toString(var6, StandardCharsets.UTF_8);
               JSONArray var9 = new JSONArray(var8);
               List<String> var10 = new ArrayList<>();

               for (int var11 = 0; var11 < var9.length(); var11++) {
                  var10.add(var9.getString(var11));
               }

               var24 = var10;
            }

            return var24;
         }
      } catch (IOException var23) {
         var23.printStackTrace();
      }

      return Collections.EMPTY_LIST;
   }

   public String encode(String var1) {
      try {
         return URLEncoder.encode(var1, "UTF-8");
      } catch (UnsupportedEncodingException var5) {
         return var1;
      }
   }

   public JSONObject getJSONObject(String var1) {
      try {
         HttpGet var4 = new HttpGet("http://localhost/profiles/" + this.encode(var1) + ".profile?v=" + Client.RELEASE_TARGET);
         CloseableHttpResponse var5 = HttpClients.createDefault().execute(var4);
         HttpEntity var6 = var5.getEntity();
         if (var6 != null) {
            JSONObject var10;
            try (InputStream var7 = var6.getContent()) {
               String var9 = IOUtils.toString(var7, "UTF-8");
               var10 = new JSONObject(var9);
            }

            return var10;
         }
      } catch (IOException var22) {
         var22.printStackTrace();
      }

      return new JSONObject();
   }

   public Configuration method28657(Configuration var1, String var2) {
      Configuration var5 = new Configuration(var2, var1);
      Configuration var6 = null;
       try {
          var5.method22988();
          var6 = new Configuration("settings", this.getJSONObject(var2).getJSONObject("modConfig"));
       } catch (JSONException e) {
           throw new RuntimeException(e);
       }

       for (Module var8 : Client.getInstance().moduleManager.getModuleMap().values()) {
         JSONObject var9 = var6.method22990(var8);
         if (var9 != null) {
            var5.method22989(var9, var8);
         }
      }

      return var5;
   }
}
