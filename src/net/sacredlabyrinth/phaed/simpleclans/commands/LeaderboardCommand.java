package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.List;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public class LeaderboardCommand
{
    public LeaderboardCommand()
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
        NumberFormat formatter = new DecimalFormat("#.#");

        if (arg.length == 0)
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.leaderboard"))
            {
                List<ClanPlayer> clanPlayers = plugin.getClanManager().getAllClanPlayers();
                plugin.getClanManager().sortClanPlayersByKDR(clanPlayers);

                ChatBlock chatBlock = new ChatBlock();

                ChatBlock.sendBlank(player);
                ChatBlock.saySingle(player, plugin.getSettingsManager().getServerName() + subColor + " leaderboard " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                ChatBlock.sendBlank(player);
                ChatBlock.sendMessage(player, headColor + "Total clan players: " + subColor + clanPlayers.size());
                ChatBlock.sendBlank(player);

                chatBlock.setAlignment("c", "l", "c", "c", "c", "c");
                chatBlock.addRow("  " + headColor + "Rank", "Player", "KDR", "Clan", "Seen");

                int rank = 1;

                for (ClanPlayer cp : clanPlayers)
                {
                    Player p = plugin.getServer().getPlayer(cp.getName());

                    boolean isOnline = false;

                    if (p != null)
                    {
                        isOnline = true;
                    }

                    String name = (cp.isLeader() ? plugin.getSettingsManager().getPageLeaderColor() : ((cp.isTrusted() ? plugin.getSettingsManager().getPageTrustedColor() : plugin.getSettingsManager().getPageUnTrustedColor()))) + cp.getName();
                    String lastSeen = (isOnline ? ChatColor.GREEN + "Green" : ChatColor.WHITE + cp.getLastSeenDaysString());

                    String clanTag = ChatColor.WHITE + "Free Agent";

                    if (cp.getClan() != null)
                    {
                        clanTag = cp.getClan().getColorTag();
                    }

                    chatBlock.addRow("  " + rank, name, ChatColor.YELLOW + "" + formatter.format(cp.getKDR()), ChatColor.WHITE + clanTag, lastSeen);
                    rank++;
                }

                boolean more = chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

                if (more)
                {
                    plugin.getStorageManager().addChatBlock(player, chatBlock);
                    ChatBlock.sendBlank(player);
                    ChatBlock.sendMessage(player, headColor + MessageFormat.format(plugin.getLang().getString("view.next.page"), plugin.getSettingsManager().getCommandMore()));
                }

                ChatBlock.sendBlank(player);
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("insufficient.permissions"));
            }
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " leaderboard");
        }
    }
}
