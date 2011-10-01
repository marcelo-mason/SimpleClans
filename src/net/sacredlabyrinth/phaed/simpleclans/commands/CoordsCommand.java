package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author phaed
 */
public class CoordsCommand
{
    public CoordsCommand()
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

                            chatBlock.addRow("  " + headColor + plugin.getLang().getString("name"), plugin.getLang().getString("distance"), plugin.getLang().getString("coords.upper"), plugin.getLang().getString("world"));

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
                                ChatBlock.saySingle(player, plugin.getSettingsManager().getPageClanNameColor() + Helper.capitalize(clan.getName()) + subColor+ " " + plugin.getLang().getString("coords") + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                                ChatBlock.sendBlank(player);

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
                                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("you.are.the.only.member.online"));
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang().getString("usage.0.coords"), plugin.getSettingsManager().getCommandClan()));
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("only.trusted.players.can.access.clan.coords"));
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("clan.is.not.verified"));
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("not.a.member.of.any.clan"));
            }
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("insufficient.permissions"));
        }
    }
}
