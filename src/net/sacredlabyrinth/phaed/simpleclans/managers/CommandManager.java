package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import java.util.List;
import java.util.logging.Level;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public final class CommandManager
{
    private SimpleClans plugin;

    /**
     *
     */
    public CommandManager()
    {
        plugin = SimpleClans.getInstance();
    }

    /**
     * Processes a clan chat command
     * @param player
     * @param clanTag
     * @param msg
     */
    public void processClanChat(Player player, String clanTag, String msg)
    {
        Clan clan = plugin.getClanManager().getClan(clanTag);

        if (!clan.isMember(player))
        {
            return;
        }

        String message = plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketLeft() + plugin.getSettingsManager().getTagDefaultColor() + clan.getColorTag() + plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketRight() + " " + plugin.getSettingsManager().getClanChatNameColor() + plugin.getSettingsManager().getClanChatPlayerBracketLeft() + player.getName() + plugin.getSettingsManager().getClanChatPlayerBracketRight() + " " + plugin.getSettingsManager().getClanChatMessageColor() + msg;
        SimpleClans.log(Level.INFO, plugin.getSettingsManager().getClanChatTagBracketLeft() + clan.getTag() + plugin.getSettingsManager().getClanChatTagBracketRight() + " " + plugin.getSettingsManager().getClanChatPlayerBracketLeft() + player.getName() + plugin.getSettingsManager().getClanChatPlayerBracketRight() + " " + msg);

        List<ClanPlayer> cps = clan.getMembers();

        for (ClanPlayer cp : cps)
        {
            Player member = plugin.getServer().getPlayer(cp.getName());
            ChatBlock.sendMessage(member, message);
        }
    }

    /**
     * Process the accept command
     * @param player
     */
    public void processAccept(Player player)
    {
        if (plugin.getSettingsManager().isBanned(player.getName()))
        {
            ChatBlock.sendMessage(player, ChatColor.RED + "You are banned from using clan commands");
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp != null)
        {
            Clan clan = cp.getClan();

            if (clan.isLeader(player))
            {
                if (plugin.getRequestManager().hasRequest(clan.getTag()))
                {
                    if (cp.getVote() == null)
                    {
                        plugin.getRequestManager().accept(cp);
                        clan.leaderAnnounce(player.getDisplayName(), ChatColor.GREEN + Helper.capitalize(player.getDisplayName()) + " voted to accept");
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "You have already voted");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "Nothing to accept");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "You do not have leader permissions");
            }
        }
        else
        {
            if (plugin.getRequestManager().hasRequest(player.getName().toLowerCase()))
            {
                cp = plugin.getClanManager().getCreateClanPlayer(player.getName());
                plugin.getRequestManager().accept(cp);
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Nothing to accept");
            }
        }
    }

    /**
     * Process the deny command
     * @param player
     */
    public void processDeny(Player player)
    {
        if (plugin.getSettingsManager().isBanned(player.getName()))
        {
            ChatBlock.sendMessage(player, ChatColor.RED + "You are banned from using clan commands");
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp != null)
        {
            Clan clan = cp.getClan();

            if (clan.isLeader(player))
            {
                if (plugin.getRequestManager().hasRequest(clan.getTag()))
                {
                    if (cp.getVote() == null)
                    {
                        plugin.getRequestManager().deny(cp);
                        clan.leaderAnnounce(player.getName(), ChatColor.RED + Helper.capitalize(player.getName()) + " has voted to deny");
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "You have already voted");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "Nothing to deny");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "You do not have leader permissions");
            }
        }
        else
        {
            if (plugin.getRequestManager().hasRequest(player.getName().toLowerCase()))
            {
                cp = plugin.getClanManager().getCreateClanPlayer(player.getName());
                plugin.getRequestManager().deny(cp);
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Nothing to deny");
            }
        }
    }

    /**
     * Process the more command
     * @param player
     */
    public void processMore(Player player)
    {
        if (plugin.getSettingsManager().isBanned(player.getName()))
        {
            ChatBlock.sendMessage(player, ChatColor.RED + "You are banned from using clan commands");
            return;
        }

        ChatBlock chatBlock = plugin.getStorageManager().getChatBlock(player);

        if (chatBlock != null && chatBlock.size() > 0)
        {
            chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

            if (chatBlock.size() > 0)
            {
                ChatBlock.sendBlank(player);
                ChatBlock.sendMessage(player, plugin.getSettingsManager().getPageHeadingsColor() + "Type /" + plugin.getSettingsManager().getCommandMore() + " to view next page.");
            }
            ChatBlock.sendBlank(player);
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + "Nothing more to see.");
        }
    }
}
