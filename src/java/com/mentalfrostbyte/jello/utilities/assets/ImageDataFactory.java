package com.mentalfrostbyte.jello.utilities.assets;

import com.mentalfrostbyte.jello.utilities.Class6958;
import com.mentalfrostbyte.jello.utilities.interfaces.LoadableImageData;
import mapped.Class6959;
import mapped.Class6960;
import mapped.PNGImageData;
import mapped.PrivilegedAction;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class ImageDataFactory {
   private static boolean field39050 = true;
   private static boolean field39051 = false;
   private static final String PNG_LOADER = "org.newdawn.slick.pngloader";

   private static void checkProperty() {
      if (!field39051) {
         field39051 = true;

         try {
            AccessController.doPrivileged(new PrivilegedAction() {
               public Object run() {
                  String val = System.getProperty(PNG_LOADER);
                  if ("false".equalsIgnoreCase(val)) {
                     usePngLoader = false;
                  }

                  Log.method25665("Use Java PNG Loader = " + usePngLoader);
                  return null;
               }
            });
         } catch (Throwable e) {
            // ignore, security failure - probably an applet
         }
      }
   }

   public static LoadableImageData getImageDataFor(String var0) {
      checkProperty();
      var0 = var0.toLowerCase();
      if (!var0.endsWith(".tga")) {
         if (!var0.endsWith(".png")) {
            return new Class6958();
         } else {
            Class6959 var3 = new Class6959();
            if (field39050) {
               var3.method21472(new PNGImageData());
            }

            var3.method21472(new Class6958());
            return var3;
         }
      } else {
         return new Class6960();
      }
   }

   // $VF: synthetic method
   public static boolean method31205(boolean var0) {
      field39050 = var0;
      return var0;
   }

   // $VF: synthetic method
   public static boolean method31206() {
      return field39050;
   }
}
