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
public class ResignCommand
{
    public ResignCommand()
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

        if (plugin.getPermissionsManager().has(player, "simpleclans.member.resign"))
        {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null)
            {
                Clan clan = cp.getClan();

                if (!clan.isLeader(player) || clan.getLeaders().size() > 1)
                {
                    clan.addBb(player.getName(), ChatColor.AQUA + Helper.capitalize(player.getName()) + " has resigned");
                    clan.removePlayerFromClan(player);
                }
                else if (clan.isLeader(player) && clan.getLeaders().size() == 1)
                {
                    plugin.getClanManager().serverAnnounce(ChatColor.AQUA + "Clan " + clan.getName() + " has been disbanded");
                    clan.disband();
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "Last leader cannot resign.  You must appoint another leader or disband the clan");
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
