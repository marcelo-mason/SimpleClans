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
public class ProfileCommand
{
    public ProfileCommand()
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

        Clan clan = null;

        if (arg.length == 0)
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.profile"))
            {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                if (cp == null)
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("not.a.member.of.any.clan"));
                }
                else
                {
                    if (cp.getClan().isVerified())
                    {
                        clan = cp.getClan();
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("clan.is.not.verified"));
                    }
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("insufficient.permissions"));
            }
        }
        else if (arg.length == 1)
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.profile"))
            {
                clan = plugin.getClanManager().getClan(arg[0]);

                if (clan == null)
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("no.clan.matched"));
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("insufficient.permissions"));
            }
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang().getString("usage.0.profile.tag"), plugin.getSettingsManager().getCommandClan()));
        }

        if (clan != null)
        {
            if (clan.isVerified())
            {
                ChatBlock.sendBlank(player);
                ChatBlock.saySingle(player, plugin.getSettingsManager().getPageClanNameColor() + Helper.capitalize(clan.getName()) + subColor+ " " + plugin.getLang().getString("profile") + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                ChatBlock.sendBlank(player);

                String name = plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketLeft() + plugin.getSettingsManager().getTagDefaultColor() + clan.getColorTag() + plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketRight() + " " + plugin.getSettingsManager().getPageClanNameColor() + clan.getName();
                String leaders = clan.getLeadersString(plugin.getSettingsManager().getPageLeaderColor(), subColor + ", ");
                String onlineCount = ChatColor.WHITE + "" + Helper.stripOffLinePlayers(clan.getMembers()).size();
                String membersOnline = onlineCount + subColor + "/" + ChatColor.WHITE + clan.getSize();
                String inactive = ChatColor.WHITE + "" + clan.getInactiveDays() + subColor + "/" + ChatColor.WHITE + (clan.isVerified() ? plugin.getSettingsManager().getPurgeClan() : plugin.getSettingsManager().getPurgeUnverified()) + " " + plugin.getLang().getString("days");
                String founded = ChatColor.WHITE + "" + clan.getFoundedString();
                String allies = ChatColor.WHITE + "" + clan.getAllyString(subColor + ", ");
                String rivals = ChatColor.WHITE + "" + clan.getRivalString(subColor + ", ");
                String kdr = ChatColor.YELLOW + "" + formatter.format(clan.getTotalKDR());
                String deaths = ChatColor.WHITE + "" + clan.getTotalDeaths();
                String rival = ChatColor.WHITE + "" + clan.getTotalRival();
                String neutral = ChatColor.WHITE + "" + clan.getTotalNeutral();
                String civ = ChatColor.WHITE + "" + clan.getTotalCivilian();
                String status = ChatColor.WHITE + "" + (clan.isVerified() ? plugin.getSettingsManager().getPageTrustedColor() + plugin.getLang().getString("verified") : plugin.getSettingsManager().getPageUnTrustedColor() + plugin.getLang().getString("unverified"));

                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang().getString("name.0"), name));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang().getString("status.0"), status));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang().getString("leaders.0"), leaders));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang().getString("members.online.0"), membersOnline));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang().getString("kdr.0"), kdr));
                ChatBlock.sendMessage(player, "  " + subColor + plugin.getLang().getString("kill.totals") + " " + headColor + "[" + plugin.getLang().getString("rival") + ":" + rival + " " + headColor + "" + plugin.getLang().getString("neutral") + ":" + neutral + " " + headColor + "" + plugin.getLang().getString("civilian") + ":" + civ + headColor + "]");
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang().getString("deaths.0"), deaths));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang().getString("allies.0"), allies));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang().getString("rivals.0"), rivals));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang().getString("founded.0"), founded));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang().getString("inactive.0"), inactive));

                ChatBlock.sendBlank(player);
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("clan.is.not.verified"));
            }
        }
    }
}
