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
public class ProfileCommand
{
    public ProfileCommand()
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

        Clan clan = null;

        if (arg.length == 0)
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.profile"))
            {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                if (cp == null)
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "You are not a member of any clan");
                }
                else
                {
                    if (cp.getClan().isVerified())
                    {
                        clan = cp.getClan();
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "Clan is not verified");
                    }
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
            }
        }
        else if (arg.length == 1)
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.profile"))
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
            ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /clan profile [tag]");
        }

        if (clan != null)
        {
            if (clan.isVerified())
            {
                ChatBlock.sendBlank(player);
                ChatBlock.saySingle(player, plugin.getSettingsManager().getPageClanNameColor() + clan.getName() + subColor + " profile " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                ChatBlock.sendBlank(player);

                String name = plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketLeft() + plugin.getSettingsManager().getTagDefaultColor() + clan.getColorTag() + plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketRight() + " " + plugin.getSettingsManager().getPageClanNameColor() + clan.getName();
                String leaders = clan.getLeadersString(plugin.getSettingsManager().getPageLeaderColor(), subColor + ", ");
                String onlineCount = ChatColor.WHITE + "" + Helper.stripOffLinePlayers(clan.getMembers()).size();
                String membersOnline = onlineCount + subColor + "/" + ChatColor.WHITE + clan.getSize();
                String inactive = ChatColor.WHITE + "" + clan.getInactiveDays() + subColor + "/" + ChatColor.WHITE + (clan.isVerified() ? plugin.getSettingsManager().getPurgeClan() : plugin.getSettingsManager().getPurgeUnverified()) + " days";
                String founded = ChatColor.WHITE + "" + clan.getFoundedString();
                String allies = ChatColor.WHITE + "" + clan.getAllyString(subColor + ", ");
                String rivals = ChatColor.WHITE + "" + clan.getRivalString(subColor + ", ");
                String kdr = ChatColor.YELLOW + "" + formatter.format(clan.getTotalKDR());
                String deaths = ChatColor.WHITE + "" + clan.getTotalDeaths();
                String rival = ChatColor.WHITE + "" + clan.getTotalRival();
                String neutral = ChatColor.WHITE + "" + clan.getTotalNeutral();
                String civ = ChatColor.WHITE + "" + clan.getTotalCivilian();

                ChatBlock.sendMessage(player, "  " + subColor + "Name: " + name);
                ChatBlock.sendMessage(player, "  " + subColor + "Leaders: " + leaders);
                ChatBlock.sendMessage(player, "  " + subColor + "Members Online: " + membersOnline);
                ChatBlock.sendMessage(player, "  " + subColor + "KDR: " + kdr);
                ChatBlock.sendMessage(player, "  " + subColor + "Kill Totals: " + headColor + "[Rival:" + rival + " " + headColor + "Neutral:" + neutral + " " + headColor + "Civilian:" + civ + headColor + "]");
                ChatBlock.sendMessage(player, "  " + subColor + "Deaths: " + deaths);
                ChatBlock.sendMessage(player, "  " + subColor + "Allies: " + allies);
                ChatBlock.sendMessage(player, "  " + subColor + "Rivals: " + rivals);
                ChatBlock.sendMessage(player, "  " + subColor + "Founded: " + founded);
                ChatBlock.sendMessage(player, "  " + subColor + "Inactive: " + inactive);
                ChatBlock.sendBlank(player);
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Clan is not verified");
            }
        }
    }
}
