package com.mentalfrostbyte.jello.command;
import com.mentalfrostbyte.jello.managers.impl.command.ChatCommandArguments;
import com.mentalfrostbyte.jello.managers.impl.command.ChatCommandExecutor;
import com.mentalfrostbyte.jello.managers.impl.command.Command;
import com.mentalfrostbyte.jello.managers.impl.command.CommandException;

public class ClearChat extends Command {
   public ClearChat() {
      super("clearchat", "Clears your chat client side", "cc", "chatclear");
   }

   @Override
   public void run(String var1, ChatCommandArguments[] var2, ChatCommandExecutor var3) throws CommandException {
      if (var2.length == 0) {
         mc.ingameGUI.getChatGUI().clearChatMessages(true);
      } else {
         throw new CommandException("Too many arguments");
      }
   }
}
