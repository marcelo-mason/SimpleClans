package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import java.text.DecimalFormat;
import java.text.NumberFormat;
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
public class LookupCommand
{
    public LookupCommand()
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
        String headColor = plugin.getSettingsManager().getPageHeadingsColor();
        String subColor = plugin.getSettingsManager().getPageSubTitleColor();
        NumberFormat formatter = new DecimalFormat("#.#");

        String playerName = null;

        if (arg.length == 0)
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.lookup"))
            {
                playerName = player.getName();
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
            }
        }
        else if (arg.length == 1)
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.lookup"))
            {
                playerName = arg[0];
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
            }
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " lookup [tag]");
        }

        if (playerName != null)
        {
            ClanPlayer targetCp = plugin.getClanManager().getAnyClanPlayer(playerName);
            ClanPlayer myCp = plugin.getClanManager().getClanPlayer(player.getName());
            Clan myClan = myCp == null ? null : myCp.getClan();

            if (targetCp != null)
            {
                Clan targetClan = targetCp.getClan();

                ChatBlock.sendBlank(player);
                ChatBlock.saySingle(player, plugin.getSettingsManager().getPageClanNameColor() + targetCp.getName() + subColor + "'s player info " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                ChatBlock.sendBlank(player);

                String clanName = ChatColor.WHITE + "None";

                if (targetClan != null)
                {
                    clanName = plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketLeft() + plugin.getSettingsManager().getTagDefaultColor() + targetClan.getColorTag() + plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketRight() + " " + plugin.getSettingsManager().getPageClanNameColor() + targetClan.getName();
                }

                String status = targetClan == null ? ChatColor.WHITE + "Free Agent" : (targetCp.isLeader() ? plugin.getSettingsManager().getPageLeaderColor() + "Leader" : ChatColor.WHITE + "Member");
                String joinDate = ChatColor.WHITE + "" + targetCp.getJoinDateString();
                String lastSeen = ChatColor.WHITE + "" + targetCp.getLastSeenString();
                String inactive = ChatColor.WHITE + "" + targetCp.getInactiveDays() + subColor + "/" + ChatColor.WHITE + plugin.getSettingsManager().getPurgePlayers() + " days";
                String rival = ChatColor.WHITE + "" + targetCp.getRivalKills();
                String neutral = ChatColor.WHITE + "" + targetCp.getNeutralKills();
                String civilian = ChatColor.WHITE + "" + targetCp.getCivilianKills();
                String deaths = ChatColor.WHITE + "" + targetCp.getDeaths();
                String kdr = ChatColor.YELLOW + "" + formatter.format(targetCp.getKDR());
                String pastClans = ChatColor.WHITE + "" + targetCp.getPastClansString(headColor + ", ");

                ChatBlock.sendMessage(player, "  " + subColor + "Clan: " + clanName);
                ChatBlock.sendMessage(player, "  " + subColor + "Status: " + status);
                ChatBlock.sendMessage(player, "  " + subColor + "KDR: " + kdr);
                ChatBlock.sendMessage(player, "  " + subColor + "Kill Totals: " + headColor + "[Rival:" + rival + " " + headColor + "Neutral:" + neutral + " " + headColor + "Civilian:" + civilian + headColor + "]");
                ChatBlock.sendMessage(player, "  " + subColor + "Deaths: " + deaths);
                ChatBlock.sendMessage(player, "  " + subColor + "Join Date: " + joinDate);
                ChatBlock.sendMessage(player, "  " + subColor + "Last Seen: " + lastSeen);
                ChatBlock.sendMessage(player, "  " + subColor + "Past Clans: " + pastClans);
                ChatBlock.sendMessage(player, "  " + subColor + "Inactive: " + inactive);

                if (arg.length == 1 && targetClan != null)
                {
                    String killType = ChatColor.GRAY + "Neutral";

                    if (targetClan == null)
                    {
                        killType = ChatColor.DARK_GRAY + "Civilian";
                    }
                    else if (myClan.isRival(targetClan.getTag()))
                    {
                        killType = ChatColor.WHITE + "Rival";
                    }

                    ChatBlock.sendMessage(player, "  " + subColor + "Kill Type: " + killType);
                }

                ChatBlock.sendBlank(player);
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "No player data found");

                if (arg.length == 1 && myClan != null)
                {
                    ChatBlock.sendBlank(player);
                    ChatBlock.sendMessage(player, "  " + subColor + "Kill Type: " + ChatColor.DARK_GRAY + "Civilian");
                }
            }
        }
    }
}
