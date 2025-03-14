package com.mentalfrostbyte.jello.util.client.logger;

import java.io.OutputStream;

import com.mentalfrostbyte.Client;
import org.apache.logging.log4j.LogManager;

public class ClientLogger implements Logger {
   public OutputStream infoStream;
   public OutputStream warningStream;
   public OutputStream errorStream;
   public org.apache.logging.log4j.Logger logger = LogManager.getLogger("Jello");

   public ClientLogger(OutputStream infoStream, OutputStream warningStream, OutputStream errorStream) {
      this.infoStream = infoStream;
      this.warningStream = warningStream;
      this.errorStream = errorStream;
   }

   @Override
   public void info(String message) {
      logger.info(message);
   }

   @Override
   public void warn(String message) {
      logger.warn(message);
   }

   @Override
   public void error(String message) {
      logger.error(message);
   }

   /**
    * wtf
    */
   @Override
   public void setThreadName(String name) {
      Client.getInstance();
   }
}
