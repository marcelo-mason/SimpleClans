package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import java.util.ArrayList;
import java.util.List;
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
public class RosterCommand
{
    public RosterCommand()
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
        String headColor = plugin.getSettingsManager().getPageHeadingsColor();
        String subColor = plugin.getSettingsManager().getPageSubTitleColor();

        Clan clan = null;

        if (arg.length == 0)
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.roster"))
            {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                if (cp == null)
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "You are not a member of any clan");
                }
                else
                {
                    clan = cp.getClan();
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
            }
        }
        else if (arg.length == 1)
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.roster"))
            {
                clan = plugin.getClanManager().getClan(arg[0]);

                if (clan == null)
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "No clan matched");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
            }
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /clan roster [tag]");
        }

        if (clan != null)
        {
            if (clan.isVerified())
            {
                ChatBlock chatBlock = new ChatBlock();

                ChatBlock.sendBlank(player);
                ChatBlock.saySingle(player, plugin.getSettingsManager().getPageClanNameColor() + clan.getName() + subColor + " roster " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                ChatBlock.sendBlank(player);
                ChatBlock.sendMessage(player, headColor + "Legend: " + plugin.getSettingsManager().getPageLeaderColor() + "leader" + headColor + ", " + plugin.getSettingsManager().getPageTrustedColor() + "trusted" + headColor + ", " + plugin.getSettingsManager().getPageUnTrustedColor() + "untrusted");
                ChatBlock.sendBlank(player);

                chatBlock.setFlexibility(false, true, false, true);
                chatBlock.addRow("  " + headColor + "Player", "Seen", "Player", "Seen");

                List<String> row = new ArrayList<String>();

                List<ClanPlayer> leaders = clan.getLeaders();
                plugin.getClanManager().sortClanPlayersByLastSeen(leaders);

                List<ClanPlayer> members = clan.getNonLeaders();
                plugin.getClanManager().sortClanPlayersByLastSeen(members);

                for (ClanPlayer cp : leaders)
                {
                    Player p = plugin.getServer().getPlayer(cp.getName());

                    boolean isOnline = false;

                    if (p != null)
                    {
                        isOnline = true;
                    }

                    String name = plugin.getSettingsManager().getPageLeaderColor() + cp.getName();
                    String lastSeen = (isOnline ? ChatColor.GREEN + "Online" : ChatColor.WHITE + cp.getLastSeenDaysString());

                    row.add(name);
                    row.add(lastSeen);

                    if (row.size() == 4)
                    {
                        chatBlock.addRow("  " + row.get(0), row.get(1), row.get(2), row.get(3));
                        row.clear();
                    }
                }

                for (ClanPlayer cp : members)
                {
                    Player p = plugin.getServer().getPlayer(cp.getName());

                    boolean isOnline = false;

                    if (p != null)
                    {
                        isOnline = true;
                    }

                    String name = (cp.isTrusted() ? plugin.getSettingsManager().getPageTrustedColor() : plugin.getSettingsManager().getPageUnTrustedColor()) + cp.getName();
                    String online = isOnline ? ChatColor.GREEN + "*" : "";
                    String lastSeen = ChatColor.WHITE + cp.getLastSeenDaysString();

                    row.add(name + online);
                    row.add(lastSeen);

                    if (row.size() == 4)
                    {
                        chatBlock.addRow("  " + row.get(0), row.get(1), row.get(2), row.get(3));
                        row.clear();
                    }
                }

                if (!row.isEmpty())
                {
                    chatBlock.addRow("  " + row.get(0), row.size() > 1 ? row.get(1) : "", row.size() > 2 ? row.get(2) : "", row.size() > 4 ? row.get(3) : "");
                }

                boolean more = chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

                if (more)
                {
                    plugin.getStorageManager().addChatBlock(player, chatBlock);
                    ChatBlock.sendBlank(player);
                    ChatBlock.sendMessage(player, headColor + "Type /" + plugin.getSettingsManager().getCommandMore() + " to view next page.");
                }

                ChatBlock.sendBlank(player);
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Clan is not verified");
            }
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /clan roster [tag]");
        }
    }
}
