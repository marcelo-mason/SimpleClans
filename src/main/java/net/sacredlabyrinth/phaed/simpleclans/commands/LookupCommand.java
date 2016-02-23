package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

/**
 * @author phaed
 */
public class LookupCommand
{
    public LookupCommand()
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
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
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
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            }
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.lookup.tag"), plugin.getSettingsManager().getCommandClan()));
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
                ChatBlock.saySingle(player, MessageFormat.format(plugin.getLang("s.player.info"), plugin.getSettingsManager().getPageClanNameColor() + targetCp.getName() + subColor) + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                ChatBlock.sendBlank(player);

                String clanName = ChatColor.WHITE + plugin.getLang("none");

                if (targetClan != null)
                {
                    clanName = plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketLeft() + plugin.getSettingsManager().getTagDefaultColor() + targetClan.getColorTag() + plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketRight() + " " + plugin.getSettingsManager().getPageClanNameColor() + targetClan.getName();
                }

                String status = targetClan == null ? ChatColor.WHITE + plugin.getLang("free.agent") : (targetCp.isLeader() ? plugin.getSettingsManager().getPageLeaderColor() + plugin.getLang("leader") : (targetCp.isTrusted() ? plugin.getSettingsManager().getPageTrustedColor() + plugin.getLang("trusted") : plugin.getSettingsManager().getPageUnTrustedColor() + plugin.getLang("untrusted")));
                String rank = ChatColor.WHITE + "" + Helper.parseColors(targetCp.getRank());
                String joinDate = ChatColor.WHITE + "" + targetCp.getJoinDateString();
                String lastSeen = ChatColor.WHITE + "" + targetCp.getLastSeenString();
                String inactive = ChatColor.WHITE + "" + targetCp.getInactiveDays() + subColor + "/" + ChatColor.WHITE + plugin.getSettingsManager().getPurgePlayers() + " days";
                String rival = ChatColor.WHITE + "" + targetCp.getRivalKills();
                String neutral = ChatColor.WHITE + "" + targetCp.getNeutralKills();
                String civilian = ChatColor.WHITE + "" + targetCp.getCivilianKills();
                String deaths = ChatColor.WHITE + "" + targetCp.getDeaths();
                String kdr = ChatColor.YELLOW + "" + formatter.format(targetCp.getKDR());
                String pastClans = ChatColor.WHITE + "" + targetCp.getPastClansString(headColor + ", ");

                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("clan.0"), clanName));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("rank.0"), rank));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("status.0"), status));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("kdr.0"), kdr));
                ChatBlock.sendMessage(player, "  " + subColor + plugin.getLang("kill.totals") + " " + headColor + "[" + plugin.getLang("rival") + ":" + rival + " " + headColor + "" + plugin.getLang("neutral") + ":" + neutral + " " + headColor + "" + plugin.getLang("civilian") + ":" + civilian + headColor + "]");
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("deaths.0"), deaths));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("join.date.0"), joinDate));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("last.seen.0"), lastSeen));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("past.clans.0"), pastClans));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("inactive.0"), inactive));

                if (arg.length == 1 && targetClan != null && !targetCp.equals(myCp))
                {
                	String killType = ChatColor.GRAY + plugin.getLang("neutral");

                    if (targetClan == null)
                    {
                        killType = ChatColor.DARK_GRAY + plugin.getLang("civilian");
                    }
                    else if (myClan != null && myClan.isRival(targetClan.getTag()))
                    {
                        killType = ChatColor.WHITE + plugin.getLang("rival");
                    }

                    ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("kill.type.0"), killType));
                }

                ChatBlock.sendBlank(player);
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.player.data.found"));

                if (arg.length == 1 && myClan != null)
                {
                    ChatBlock.sendBlank(player);
                    ChatBlock.sendMessage(player, MessageFormat.format(plugin.getLang("kill.type.civilian"), ChatColor.DARK_GRAY));
                }
            }
        }
    }
}
