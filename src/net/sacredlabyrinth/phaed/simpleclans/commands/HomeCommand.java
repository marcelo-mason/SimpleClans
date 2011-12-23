package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.List;
import java.util.Random;

/**
 * @author phaed
 */
public class HomeCommand
{
    public HomeCommand()
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

        if (arg.length == 2 && arg[0].equalsIgnoreCase("set") && plugin.getPermissionsManager().has(player, "simpleclans.mod.home"))
        {
            Location loc = player.getLocation();

            Clan clan = plugin.getClanManager().getClan(arg[1]);

            if (clan != null)
            {
                clan.setHomeLocation(loc);
                ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang().getString("hombase.mod.set"), clan.getName()) + " " + ChatColor.YELLOW + Helper.toLocationString(loc));
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("the.clan.does.not.exist"));
            }
            return;
        }

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
                        if (plugin.getPermissionsManager().has(player, "simpleclans.member.home"))
                        {
                            Location loc = clan.getHomeLocation();

                            if (loc == null)
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("hombase.not.set"));
                                return;
                            }

                            plugin.getTeleportManager().addPlayer(player, clan.getHomeLocation(), clan.getName());
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("insufficient.permissions"));
                        }
                    }
                    else
                    {
                        String ttag = arg[0];

                        if (ttag.equalsIgnoreCase("set"))
                        {
                            Location loc = player.getLocation();

                            if (cp.isLeader())
                            {
                                if (plugin.getPermissionsManager().has(player, "simpleclans.leader.home-set"))
                                {
                                    if (plugin.getSettingsManager().isHomebaseSetOnce() && clan.getHomeLocation() != null && !plugin.getPermissionsManager().has(player, "simpleclans.mod.home"))
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("home.base.only.once"));
                                        return;
                                    }

                                    clan.setHomeLocation(loc);
                                    ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang().getString("hombase.set"), ChatColor.YELLOW + Helper.toLocationString(loc)));
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("insufficient.permissions"));
                                }
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("no.leader.permissions"));
                            }
                        }
                        else if (ttag.equalsIgnoreCase("regroup"))
                        {
                            Location loc = player.getLocation();

                            if (cp.isLeader())
                            {
                                if (plugin.getPermissionsManager().has(player, "simpleclans.leader.regroup"))
                                {
                                    List<ClanPlayer> members = clan.getAllMembers();

                                    for (ClanPlayer ccp : members)
                                    {
                                        Player pl = ccp.toPlayer();

                                        if (pl == null || pl.equals(player))
                                        {
                                            continue;
                                        }

                                        if (pl != null)
                                        {
                                            int x = loc.getBlockX();
                                            int z = loc.getBlockZ();

                                            player.sendBlockChange(new Location(loc.getWorld(), x + 1, loc.getBlockY(), z + 1), Material.GLASS, (byte) 0);
                                            player.sendBlockChange(new Location(loc.getWorld(), x - 1, loc.getBlockY(), z - 1), Material.GLASS, (byte) 0);
                                            player.sendBlockChange(new Location(loc.getWorld(), x + 1, loc.getBlockY(), z - 1), Material.GLASS, (byte) 0);
                                            player.sendBlockChange(new Location(loc.getWorld(), x - 1, loc.getBlockY(), z + 1), Material.GLASS, (byte) 0);

                                            Random r = new Random();

                                            int xx = r.nextInt(2) - 1;
                                            int zz = r.nextInt(2) - 1;

                                            if (xx == 0 && zz == 0)
                                            {
                                                xx = 1;
                                            }

                                            x = x + xx;
                                            z = z + zz;

                                            pl.teleport(new Location(loc.getWorld(), x + .5, loc.getBlockY(), z + .5));
                                        }
                                    }
                                    ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang().getString("hombase.set") + ChatColor.YELLOW + Helper.toLocationString(loc));
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("insufficient.permissions"));
                                }
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("no.leader.permissions"));
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("insufficient.permissions"));
                        }
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("only.trusted.players.can.access.clan.vitals"));
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
}

