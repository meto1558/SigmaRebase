package com.mentalfrostbyte.jello.util.client.logger;

public interface Logger {
   void info(String message);

   void warn(String message);

   void error(String message);

   void setThreadName(String name);
}
