package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public class CoordsCommand
{
    public CoordsCommand()
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

        if (plugin.getPermissionsManager().has(player, "simpleclans.member.coords"))
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

                            chatBlock.setFlexibility(true, false, false, false);
                            chatBlock.setAlignment("l", "c", "c", "c");

                            chatBlock.addRow("  " + headColor + "Name", "Distance", "Coords", "World");

                            List<ClanPlayer> members = Helper.stripOffLinePlayers(clan.getMembers());

                            Map<Integer, List<String>> rows = new TreeMap<Integer, List<String>>();

                            for (ClanPlayer cpm : members)
                            {
                                Player p = plugin.getServer().getPlayer(cpm.getName());

                                if (p != null)
                                {
                                    String name = (cpm.isLeader() ? plugin.getSettingsManager().getPageLeaderColor() : ((cpm.isTrusted() ? plugin.getSettingsManager().getPageTrustedColor() : plugin.getSettingsManager().getPageUnTrustedColor()))) + cpm.getName();
                                    Location loc = p.getLocation();
                                    int distance = (int) Math.ceil(loc.toVector().distance(player.getLocation().toVector()));
                                    String coords = loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();
                                    String world = loc.getWorld().getName();

                                    List<String> cols = new ArrayList<String>();
                                    cols.add("  " + name);
                                    cols.add(ChatColor.AQUA + "" + distance);
                                    cols.add(ChatColor.WHITE + "" + coords);
                                    cols.add(world);
                                    rows.put(distance, cols);
                                }
                            }

                            if (!rows.isEmpty())
                            {
                                for (List<String> col : rows.values())
                                {
                                    chatBlock.addRow(col.get(0), col.get(1), col.get(2), col.get(3));
                                }

                                ChatBlock.sendBlank(player);
                                ChatBlock.saySingle(player, plugin.getSettingsManager().getPageClanNameColor() + clan.getName() + subColor + " coords " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                                ChatBlock.sendBlank(player);

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
                                ChatBlock.sendMessage(player, ChatColor.RED + "You are the only member online");
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /clan coords");
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "Only trusted players can access clan coords");
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
