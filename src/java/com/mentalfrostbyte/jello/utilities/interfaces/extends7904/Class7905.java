package com.mentalfrostbyte.jello.utilities.interfaces.extends7904;

import com.mentalfrostbyte.jello.utilities.interfaces.Class7904;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

public class Class7905 implements Class7904 {
   private final File file;

   public Class7905(File fileIn) {
      this.file = fileIn;
   }

   @Override
   public URL method26475(String var1) {
      try {
         File var4 = new File(this.file, var1);
         if (!var4.exists()) {
            var4 = new File(var1);
         }

         return !var4.exists() ? null : var4.toURI().toURL();
      } catch (IOException var5) {
         return null;
      }
   }

   @Override
   public InputStream method26476(String var1) {
      try {
         File nFile = new File(this.file, var1);
         if (!nFile.exists()) {
            nFile = new File(var1);
         }

         return Files.newInputStream(nFile.toPath());
      } catch (IOException var5) {
         return null;
      }
   }
}
