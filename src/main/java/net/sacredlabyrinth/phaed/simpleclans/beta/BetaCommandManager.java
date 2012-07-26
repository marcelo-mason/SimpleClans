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
import java.util.logging.Level;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Max
 */
public class BetaCommandManager
{

    private LinkedHashMap<String, Command> commands;
    private SimpleClans plugin;

    public BetaCommandManager(SimpleClans plugin)
    {
        this.plugin = plugin;
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

    public boolean executeAll(final Player player, final CommandSender sender, String command, String label, String[] args)
    {
        long end;
        long start = System.currentTimeMillis();
        String[] arguments;

        //Build the args; if the args length is 0 then build if from the base command
        if (args.length == 0) {
            arguments = new String[]{command};
        } else {
            arguments = args;
        }
//        ClanPlayer cp = null;
//
//        if (sender instanceof Player) {
//            cp = plugin.getClanManager().getClanPlayer(sender.getName());
//        }

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
                        displayCommandHelp(cmd, sender, player);
                        end = System.currentTimeMillis();
                        System.out.println(end - start);
                        return true;
                    } else if (realArgs.length > 0 && realArgs[0].equals("?")) {
                        displayCommandHelp(cmd, sender, player);
                        end = System.currentTimeMillis();
                        System.out.println(end - start);
                        return true;
                    }


                    if (cmd instanceof GenericConsoleCommand) {
                        if (sender != null) {
                            ((GenericConsoleCommand) cmd).execute(sender, label, realArgs);
                        } else {
                            ((GenericConsoleCommand) cmd).execute((CommandSender) player, label, realArgs);
                        }
                    } else if (cmd instanceof GenericPlayerCommand) {
                        if (player != null) {
                            ((GenericPlayerCommand) cmd).execute(player, label, realArgs);
                        } else {
                            SimpleClans.debug(Level.WARNING, "Failed at parsing the command :(");
                        }
                    } else {
                        SimpleClans.debug(Level.WARNING, "Failed at parsing the command :(");
                    }
                    end = System.currentTimeMillis();
                    System.out.println(end - start);
                    return true;
                }
            }
        }
        (sender == null ? player : sender).sendMessage(ChatColor.DARK_RED + "Command not found!");

        return true;
    }

    private void displayCommandHelp(Command cmd, CommandSender sender, Player player)
    {
        if (player == null) {
            sender.sendMessage("§cCommand:§e " + cmd.getName());
            String[] usages = cmd.getUsages();
            StringBuilder sb = new StringBuilder("§cUsage:§e ").append(usages[0]).append("\n");

            for (int i = 1; i < usages.length; i++) {
                sb.append("           ").append(usages[i]).append("\n");
            }
            sender.sendMessage(sb.toString());
        } else if (sender == null) {
            player.sendMessage("§cCommand:§e " + cmd.getName());
            String[] usages = cmd.getUsages();
            StringBuilder sb = new StringBuilder("§cUsage:§e ").append(usages[0]).append("\n");

            for (int i = 1; i < usages.length; i++) {
                sb.append("           ").append(usages[i]).append("\n");
            }
            player.sendMessage(sb.toString());
        }
    }
}
