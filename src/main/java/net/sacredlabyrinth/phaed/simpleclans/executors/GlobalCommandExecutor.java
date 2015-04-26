package net.sacredlabyrinth.phaed.simpleclans.executors;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GlobalCommandExecutor implements CommandExecutor
{
    SimpleClans plugin;

    public GlobalCommandExecutor()
    {
        plugin = SimpleClans.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings)
    {
        Player player = (Player) commandSender;

        if (strings.length == 0)
        {
            return false;
        }

        ClanPlayer cp;
        if (SimpleClans.getInstance().hasUUID())
        {
            cp = plugin.getClanManager().getClanPlayer(player.getUniqueId());
        }
        else
        {
            cp = plugin.getClanManager().getClanPlayer(player.getName());
        }

        if (cp == null)
        {
            return false;
        }

        String subCommand = strings[0];

        if (subCommand.equals(plugin.getLang("on")))
        {
            cp.setGlobalChat(true);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have enabled global chat");
        }
        else if (subCommand.equals(plugin.getLang("off")))
        {
            cp.setGlobalChat(false);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have disabled global chat");
        }
        else
        {
            return true;
        }

        return false;
    }
}
