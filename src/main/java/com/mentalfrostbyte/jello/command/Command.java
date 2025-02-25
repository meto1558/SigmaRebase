package com.mentalfrostbyte.jello.command;

import com.mentalfrostbyte.jello.managers.util.command.ChatCommandArguments;
import com.mentalfrostbyte.jello.managers.util.command.ChatCommandExecutor;
import com.mentalfrostbyte.jello.managers.util.command.CommandException;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Command {
    public static final Minecraft mc = Minecraft.getInstance();
    private final String name;
    private final String descriptor;
    private final String[] alias;
    private final List<String> field25702 = new ArrayList<String>();

    public Command(String name, String desc, String... alias) {
        this.name = name;
        this.descriptor = desc;
        this.alias = alias;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.descriptor;
    }

    public String getOptions() {
        String var3 = "";

        for (String var5 : this.field25702) {
            var3 = var3 + "[" + var5 + "] ";
        }

        return var3;
    }

    public String[] getAlias() {
        return this.alias;
    }

    public void registerSubCommands(String... commands) {
        Collections.addAll(this.field25702, commands);
    }

    public abstract void run(String var1, ChatCommandArguments[] args, ChatCommandExecutor executor) throws CommandException;
}
