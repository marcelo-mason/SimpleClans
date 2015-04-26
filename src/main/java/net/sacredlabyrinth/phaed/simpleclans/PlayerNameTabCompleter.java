package net.sacredlabyrinth.phaed.simpleclans;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class PlayerNameTabCompleter implements TabCompleter
{
    private SimpleClans plugin;

    public PlayerNameTabCompleter()
    {
        plugin = SimpleClans.getInstance();
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings)
    {
        if (command.getName().equalsIgnoreCase(plugin.getSettingsManager().getCommandClan()))
        {
            if (strings[0].equalsIgnoreCase(plugin.getLang("lookup.command")) ||
                    strings[0].equalsIgnoreCase(plugin.getLang("ban.command")) ||
                    strings[0].equalsIgnoreCase(plugin.getLang("unban.command")) ||
                    strings[0].equalsIgnoreCase(plugin.getLang("kick.command")) ||
                    strings[0].equalsIgnoreCase(plugin.getLang("trust.command")) ||
                    strings[0].equalsIgnoreCase(plugin.getLang("untrust.command")) ||
                    strings[0].equalsIgnoreCase(plugin.getLang("promote.command")) ||
                    strings[0].equalsIgnoreCase(plugin.getLang("demote.command")) ||
                    strings[0].equalsIgnoreCase(plugin.getLang("setrank.command")) ||
                    strings[0].equalsIgnoreCase(plugin.getLang("place.command")) ||
                    strings[0].equalsIgnoreCase(plugin.getLang("invite.command")) ||
                    strings[0].equalsIgnoreCase(plugin.getLang("kills.command")))
            {
                List<String> list = new ArrayList<String>();

                for (OfflinePlayer player : plugin.getServer().getOnlinePlayers())
                {
                    list.add(player.getName());
                }

                return list;
            }

        }

        return null;
    }
}
