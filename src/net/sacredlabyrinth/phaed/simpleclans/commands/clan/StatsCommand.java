package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import java.text.DecimalFormat;
import java.text.NumberFormat;
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
public class StatsCommand
{
    public StatsCommand()
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

                            ChatBlock.sendBlank(player);
                            ChatBlock.saySingle(player, plugin.getSettingsManager().getPageClanNameColor() + clan.getName() + subColor + " stats " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                            ChatBlock.sendBlank(player);
                            ChatBlock.sendMessage(player, headColor + "KDR = " + subColor + "Kill/Death Ratio");
                            ChatBlock.sendMessage(player, headColor + "Weights = Rival: " + subColor + plugin.getSettingsManager().getKwRival() + headColor + " Neutral: " + subColor + plugin.getSettingsManager().getKwNeutral() + headColor + " Civilian: " + subColor + plugin.getSettingsManager().getKwCivilian());
                            ChatBlock.sendBlank(player);

                            chatBlock.setFlexibility(true, false, false, false, false, false, false);
                            chatBlock.setAlignment("l", "c", "c", "c", "c", "c", "c");

                            chatBlock.addRow("  " + headColor + "Name", "KDR", "Rival", "Neutral", "Civ", "Deaths");

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
                            ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " kills");
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "Only trusted players can access clan stats");
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
