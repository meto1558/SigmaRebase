package com.mentalfrostbyte.jello.util.client.logger;

import java.io.IOException;
import java.io.OutputStream;

import com.mentalfrostbyte.Client;
import org.apache.commons.io.IOUtils;

public class ClientLogger implements Logger {
   public OutputStream infoStream;
   public OutputStream warningStream;
   public OutputStream errorStream;

   public ClientLogger(OutputStream var1, OutputStream var2, OutputStream var3) {
      this.infoStream = var1;
      this.warningStream = var2;
      this.errorStream = var3;
   }

   @Override
   public void info(String message) {
      try {
         IOUtils.write("Jello: INFO - " + message + "\n", this.infoStream);
      } catch (IOException var5) {
         var5.printStackTrace();
      }
   }

   @Override
   public void warn(String message) {
      try {
         IOUtils.write("Jello: WARNING - " + message + "\n", this.warningStream);
      } catch (IOException var5) {
         var5.printStackTrace();
      }
   }

   @Override
   public void error(String message) {
      try {
         IOUtils.write("Jello: ERROR - " + message + "\n", this.errorStream);
      } catch (IOException var5) {
         var5.printStackTrace();
      }
   }

   /**
    * wtf
    */
   @Override
   public void setThreadName(String name) {
      Client.getInstance();
   }
}
