package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public class InviteCommand
{
    public InviteCommand()
    {
    }

    /**
     * Run the command
     * @param player
     * @param arg
     */
    public void run(Player player, String[] arg)
    {
        SimpleClans plugin = SimpleClans.getInstance();

        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.invite"))
        {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null)
            {
                Clan clan = cp.getClan();

                if (clan.isLeader(player))
                {
                    if (arg.length == 1)
                    {
                        Player invited = Helper.matchOnePlayer(arg[0]);

                        if (invited != null)
                        {
                            if (plugin.getPermissionsManager().has(player, "simpleclans.member.can-join"))
                            {
                                if (!invited.getName().equals(player.getName()))
                                {
                                    if (!plugin.getSettingsManager().isBanned(player.getName()))
                                    {
                                        ClanPlayer cpInv = plugin.getClanManager().getClanPlayer(invited);

                                        if (cpInv == null)
                                        {
                                            plugin.getRequestManager().addInviteRequest(plugin, cp, invited.getName(), clan);
                                            ChatBlock.sendMessage(player, ChatColor.AQUA + Helper.capitalize(invited.getName()) + " has been asked to join " + clan.getName());
                                        }
                                        else
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.RED + "The player is already member of another clan");
                                        }
                                    }
                                    else
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.RED + "This player is banned from using " + plugin.getSettingsManager().getCommandClan() + " commands");
                                    }
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + "You cannot invite yourself");
                                }
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + "The player doesn't not have the permissions to join clans");
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + "No player matched");
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " invite [player]");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "You do not have leader permissions");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "You are not a member of any clan");
            }
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
        }
    }
}
