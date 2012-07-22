/*
 * Copyright (C) 2012 p000ison
 * 
 * This work is licensed under the Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of
 * this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send
 * a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco,
 * California, 94105, USA.
 * 
 */
package net.sacredlabyrinth.phaed.simpleclans.beta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import net.sacredlabyrinth.phaed.simpleclans.commands.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Max
 */
public class BetaCommandManager
{

    private LinkedHashMap<String, Command> commands;

    public BetaCommandManager()
    {
        commands = new LinkedHashMap<String, Command>();
    }

    public void addCommand(Command command)
    {
        commands.put(command.getName().toLowerCase(), command);
    }

    public void removeCommand(String command)
    {
        commands.remove(command);
    }

    public Command getCommand(String name)
    {
        return commands.get(name.toLowerCase());
    }

    public List<Command> getCommands()
    {
        return new ArrayList<Command>(commands.values());
    }

    public boolean executeAll(final CommandSender sender, org.bukkit.command.Command command, String label, String[] args)
    {

        String[] arguments;

        //Build the args; if the args length is 0 then build if from the base command
        if (args.length == 0) {
            arguments = new String[]{command.getName()};
        } else {
            arguments = args;
        }


        //Iterate through all arguments from the last to the first argument
        for (int argsIncluded = arguments.length; argsIncluded >= 0; argsIncluded--) {
            String identifier = "";
            //Build the identifier string
            for (int i = 0; i < argsIncluded; i++) {
                identifier += " " + arguments[i];
            }

            //trim the last ' '
            identifier = identifier.trim();
            for (Command cmd : commands.values()) {
                if (cmd.isIdentifier(identifier)) {
                    String[] realArgs = Arrays.copyOfRange(arguments, argsIncluded, arguments.length);

                    if (realArgs.length < cmd.getMinArguments() || realArgs.length > cmd.getMaxArguments()) {
                        displayCommandHelp(cmd, sender);
                        return true;
                    } else if (realArgs.length > 0 && realArgs[0].equals("?")) {
                        displayCommandHelp(cmd, sender);
                        return true;
                    }

                    cmd.execute(sender, identifier, realArgs);
                    return true;
                }
            }
        }

        return true;
    }

    private void displayCommandHelp(Command cmd, CommandSender sender)
    {
        sender.sendMessage("§cCommand:§e " + cmd.getName());
        sender.sendMessage("§cUsage:§e " + cmd.getUsage());
    }
}
