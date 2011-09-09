package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

/**
 * @author phaed
 */
public class FfCommand
{
    public FfCommand()
    {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg)
    {
        SimpleClans plugin = SimpleClans.getInstance();

        if (plugin.getPermissionsManager().has(player, "simpleclans.member.ff"))
        {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null)
            {
                if (arg.length == 1)
                {
                    String action = arg[0];

                    if (action.equalsIgnoreCase(plugin.getLang().getString("allow")))
                    {
                        cp.setFriendlyFire(true);
                        plugin.getStorageManager().updateClanPlayer(cp);
                        ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang().getString("personal.friendly.fire.is.set.to.allowed"));
                    }
                    else if (action.equalsIgnoreCase(plugin.getLang().getString("auto")))
                    {
                        cp.setFriendlyFire(false);
                        plugin.getStorageManager().updateClanPlayer(cp);
                        ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang().getString("friendy.fire.is.now.managed.by.your.clan"));
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang().getString("usage.0.ff.allow.auto"), plugin.getSettingsManager().getCommandClan()));
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang().getString("usage.0.ff.allow.auto"), plugin.getSettingsManager().getCommandClan()));
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("not.a.member.of.any.clan"));
            }
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("insufficient.permissions"));
        }
    }
}
