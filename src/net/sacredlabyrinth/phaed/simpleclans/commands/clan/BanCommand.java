package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public class BanCommand
{
    public BanCommand()
    {
    }

    /**
     * Execute the command
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg)
    {
        SimpleClans plugin = SimpleClans.getInstance();

        if (plugin.getPermissionsManager().has(player, "simpleclans.mod.ban"))
        {
            if (arg.length == 1)
            {
                String banned = arg[0];

                if (!plugin.getSettingsManager().isBanned(banned))
                {
                    Player pl = Helper.matchOnePlayer(banned);

                    if (pl != null)
                    {
                        ChatBlock.sendMessage(pl, ChatColor.AQUA + "You have been banned from clan commands");
                    }

                    plugin.getClanManager().ban(banned);
                    ChatBlock.sendMessage(player, ChatColor.AQUA + "Player added to banned list");
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "This player is already banned");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /clan ban/unban [player]");
            }
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
        }
    }
}
