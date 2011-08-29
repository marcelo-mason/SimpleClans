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
public class ModtagCommand
{
    public ModtagCommand()
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

        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.modtag"))
        {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null)
            {
                Clan clan = cp.getClan();

                if (clan.isVerified())
                {
                    if (clan.isLeader(player))
                    {
                        if (arg.length == 1)
                        {
                            String newtag = arg[0];
                            String cleantag = Helper.cleanTag(newtag);

                            if (Helper.stripColors(newtag).length() <= plugin.getSettingsManager().getTagMaxLength())
                            {
                                if (!plugin.getSettingsManager().hasDisallowedColor(newtag))
                                {
                                    if (Helper.stripColors(newtag).matches("[0-9a-zA-Z]*"))
                                    {
                                        if (cleantag.equals(clan.getTag()))
                                        {
                                            clan.addBb(player.getName(), ChatColor.AQUA + "Tag changed to " + Helper.parseColors(newtag));
                                            clan.changeClanTag(newtag);
                                        }
                                        else
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.RED + "You can only modify the color and case of the tag");
                                        }
                                    }
                                    else
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.RED + "Your clan's tag can only contain letters, numbers, and color codes");
                                    }
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + "Your tag cannot contain the following colors: " + plugin.getSettingsManager().getDisallowedColorString());
                                }

                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + "Your clan's tag cannot be longer than " + plugin.getSettingsManager().getTagMaxLength() + " characters");
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " modtag [tag]");
                            ChatBlock.sendMessage(player, ChatColor.RED + "Example: /" + plugin.getSettingsManager().getCommandClan() + " modtag &4K&fo&4L");
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "You do not have leader permissions");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "Clan is not verified");
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
