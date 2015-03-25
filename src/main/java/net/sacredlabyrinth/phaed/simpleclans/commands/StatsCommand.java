package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * @author phaed
 */
public class StatsCommand
{
    public StatsCommand()
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

        if (plugin.getPermissionsManager().has(player, "simpleclans.member.stats"))
        {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null)
            {
                Clan clan = cp.getClan();

                if (clan.isVerified())
                {
                    if (cp.isTrusted())
                    {
                        if (arg.length == 0)
                        {
                            ChatBlock chatBlock = new ChatBlock();


                            ChatBlock.saySingle(player, plugin.getSettingsManager().getPageClanNameColor() + Helper.capitalize(clan.getName()) + subColor+ " " + plugin.getLang("stats") + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                            ChatBlock.sendBlank(player);

                            ChatBlock.sendMessage(player, headColor + plugin.getLang("kdr") + " = " + subColor + plugin.getLang("kill.death.ratio"));
                            ChatBlock.sendMessage(player, headColor + plugin.getLang("weights") + " = " + plugin.getLang("rival") + ": " + subColor + plugin.getSettingsManager().getKwRival() + headColor + " " + plugin.getLang("neutral") + ": " + subColor + plugin.getSettingsManager().getKwNeutral() + headColor + " " + plugin.getLang("civilian") + ": " + subColor + plugin.getSettingsManager().getKwCivilian());
                            ChatBlock.sendBlank(player);

                            chatBlock.setFlexibility(true, false, false, false, false, false, false);
                            chatBlock.setAlignment("l", "c", "c", "c", "c", "c", "c");

                            chatBlock.addRow("  " + headColor + plugin.getLang("name"), plugin.getLang("kdr"), plugin.getLang("rival"), plugin.getLang("neutral"), plugin.getLang("civilian.abbreviation"), plugin.getLang("deaths"));

                            List<ClanPlayer> leaders = clan.getLeaders();
                            plugin.getClanManager().sortClanPlayersByKDR(leaders);

                            List<ClanPlayer> members = clan.getNonLeaders();
                            plugin.getClanManager().sortClanPlayersByKDR(members);

                            for (ClanPlayer cpm : leaders)
                            {
                                String name = (cpm.isLeader() ? plugin.getSettingsManager().getPageLeaderColor() : ((cpm.isTrusted() ? plugin.getSettingsManager().getPageTrustedColor() : plugin.getSettingsManager().getPageUnTrustedColor()))) + cpm.getName();
                                String rival = NumberFormat.getInstance().format(cpm.getRivalKills());
                                String neutral = NumberFormat.getInstance().format(cpm.getNeutralKills());
                                String civilian = NumberFormat.getInstance().format(cpm.getCivilianKills());
                                String deaths = NumberFormat.getInstance().format(cpm.getDeaths());
                                String kdr = formatter.format(cpm.getKDR());

                                chatBlock.addRow("  " + name, ChatColor.YELLOW + kdr, ChatColor.WHITE + rival, ChatColor.GRAY + neutral, ChatColor.DARK_GRAY + civilian, ChatColor.DARK_RED + deaths);
                            }

                            for (ClanPlayer cpm : members)
                            {
                                String name = (cpm.isLeader() ? plugin.getSettingsManager().getPageLeaderColor() : ((cpm.isTrusted() ? plugin.getSettingsManager().getPageTrustedColor() : plugin.getSettingsManager().getPageUnTrustedColor()))) + cpm.getName();
                                String rival = NumberFormat.getInstance().format(cpm.getRivalKills());
                                String neutral = NumberFormat.getInstance().format(cpm.getNeutralKills());
                                String civilian = NumberFormat.getInstance().format(cpm.getCivilianKills());
                                String deaths = NumberFormat.getInstance().format(cpm.getDeaths());
                                String kdr = formatter.format(cpm.getKDR());

                                chatBlock.addRow("  " + name, ChatColor.YELLOW + kdr, ChatColor.WHITE + rival, ChatColor.GRAY + neutral, ChatColor.DARK_GRAY + civilian, ChatColor.DARK_RED + deaths);
                            }

                            chatBlock.sendBlock(player);

                            ChatBlock.sendBlank(player);
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.0.stats"), plugin.getSettingsManager().getCommandClan()));
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("only.trusted.players.can.access.clan.stats"));
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clan.is.not.verified"));
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
            }
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
    }
}
