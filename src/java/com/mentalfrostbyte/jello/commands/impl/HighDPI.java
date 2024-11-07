package com.mentalfrostbyte.jello.commands.impl;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.commands.*;
import net.minecraft.client.Minecraft;

public class HighDPI extends Command {
   public HighDPI() {
      super("highdpi", "Toggles macOS HiDPI, Needs mc restart.", "hidpi");
   }

   @Override
   public void run(String var1, ChatCommandArguments[] var2, ChatCommandExecutor var3) throws CommandException {
      if (var2.length <= 0) {
         if (Minecraft.IS_RUNNING_ON_MAC) {
            if (!Client.getInstance().guiManager.method33474()) {
               var3.send("HighDPI was enabled!");
            } else {
               var3.send("HighDPI was disabled!");
            }

            Client.getInstance().guiManager.method33473(!Client.getInstance().guiManager.method33474());
         } else {
            throw new CommandException("This feature is only available on macOS!");
         }
      } else {
         throw new CommandException("Too many arguments");
      }
   }
}
