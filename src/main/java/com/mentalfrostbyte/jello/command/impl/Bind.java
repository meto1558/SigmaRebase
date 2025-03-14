package com.mentalfrostbyte.jello.command.impl;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.command.Command;
import com.mentalfrostbyte.jello.gui.impl.jello.ingame.holders.KeyboardHolder;
import com.mentalfrostbyte.jello.managers.CommandManager;
import com.mentalfrostbyte.jello.managers.util.command.ChatCommandArguments;
import com.mentalfrostbyte.jello.managers.util.command.ChatCommandExecutor;
import com.mentalfrostbyte.jello.managers.util.command.CommandException;
import com.mentalfrostbyte.jello.managers.util.command.CommandType;
import com.mentalfrostbyte.jello.module.Module;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.StringTextComponent;

import java.util.Map.Entry;

public class Bind extends Command {
    public Bind() {
        super("bind", "Bind a module to a key");
        this.registerSubCommands("module");
        this.registerSubCommands("key/none");
    }

    @Override
    public void run(String var1, ChatCommandArguments[] args, ChatCommandExecutor executor) throws CommandException {
        Module module;
        if (args.length == 0) {
            CommandManager.runRunnable(() -> mc.displayGuiScreen(new KeyboardHolder(new StringTextComponent("GuiKeybinds"))));
        }

        if (args.length < 1) {
            throw new CommandException();
        } else {
            if (args.length != 1) {
                if (args.length != 2) {
                    throw new CommandException("Too many arguments");
                } else {
                    module = this.method18330(args[0].getArguments());
                    if (module == null || args[0].getCommandType() != CommandType.TEXT) {
                        throw new CommandException("Module " + args[0].getArguments() + " not found");
                    }

                    int var14 = this.method18329(args[1].getArguments().toLowerCase());
                    if (var14 == -2) {
                        throw new CommandException("Key " + args[1].getArguments() + " not found");
                    }

                    if (var14 != -1) {
                        Client.getInstance().moduleManager.getMacOSTouchBar().method13725(var14, module);
                        executor.send("Key " + args[1].getArguments() + " was set for module " + module.getFormattedName());
                    } else {
                        Client.getInstance().moduleManager.getMacOSTouchBar().method13727(module);
                        executor.send("Keybind was reset for module " + module.getFormattedName());
                    }
                }
            } else {
                module = this.method18330(args[0].getArguments());
                if (module == null || args[0].getCommandType() != CommandType.TEXT) {
                    throw new CommandException("Module " + args[0].getArguments() + " not found");
                }

                String var7 = "key.keyboard.";
                int var8 = Client.getInstance().moduleManager.getMacOSTouchBar().method13729(module);
                String var9 = null;

                for (Entry var11 : InputMappings.Input.REGISTRY.entrySet()) {
                    if (((String) var11.getKey()).startsWith(var7) && ((InputMappings.Input) var11.getValue()).getKeyCode() == var8) {
                        var9 = ((String) var11.getKey()).substring(var7.length());
                    }
                }

                if (var9 != null) {
                    executor.send(module.getFormattedName() + " is bound to : " + var9);
                } else {
                    executor.send("§c[Error] " + module.getFormattedName() + " is bound to an unknown key");
                }
            }
        }
    }

    public int method18329(String var1) {
        if (!var1.equals("none") && !var1.equals("none")) {
            String var4 = "key.keyboard.";

            for (Entry var6 : InputMappings.Input.REGISTRY.entrySet()) {
                if (((String) var6.getKey()).startsWith(var4)) {
                    String var7 = ((String) var6.getKey()).substring(var4.length());
                    var7 = var7.replace("keypad.", "");
                    var7 = var7.replace(".", "_");
                    if (var1.equals(var7)) {
                        return ((InputMappings.Input) var6.getValue()).getKeyCode();
                    }
                }
            }

            return -2;
        } else {
            return -1;
        }
    }

    public Module method18330(String var1) {
        for (Module var5 : Client.getInstance().moduleManager.getModuleMap().values()) {
            if (var5.getName().replace(" ", "").equalsIgnoreCase(var1)) {
                return var5;
            }
        }

        return null;
    }
}