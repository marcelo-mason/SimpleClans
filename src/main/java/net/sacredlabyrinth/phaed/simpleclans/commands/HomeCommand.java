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
            if (!plugin.getClanManager().purchaseHomeTeleportSet(player))
            {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("the.clan.does.not.exist"));
                return;
            }

            Location loc = player.getLocation();

            Clan clan = plugin.getClanManager().getClan(arg[1]);

            if (clan != null)
            {
                clan.setHomeLocation(loc);
                ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang("hombase.mod.set"), clan.getName()) + " " + ChatColor.YELLOW + Helper.toLocationString(loc));
            }
        }

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp == null)
        {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
            return;
        }

        Clan clan = cp.getClan();

        if (!clan.isVerified())
        {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clan.is.not.verified"));
            return;
        }

        if (!cp.isTrusted())
        {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("only.trusted.players.can.access.clan.vitals"));
            return;
        }

        if (arg.length == 0)
        {
            if (!plugin.getPermissionsManager().has(player, "simpleclans.member.home"))
            {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                return;
            }

            if (plugin.getClanManager().purchaseHomeTeleport(player))
            {
                Location loc = clan.getHomeLocation();

                if (loc == null)
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("hombase.not.set"));
                    return;
                }

                plugin.getTeleportManager().addPlayer(player, clan.getHomeLocation(), clan.getName());
            }
        }
        else
        {
            String ttag = arg[0];

            if (ttag.equalsIgnoreCase("set"))
            {
                if (!cp.isLeader())
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                    return;
                }

                if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.home-set"))
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                    return;
                }

                if (plugin.getSettingsManager().isHomebaseSetOnce() && clan.getHomeLocation() != null && !plugin.getPermissionsManager().has(player, "simpleclans.mod.home"))
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("home.base.only.once"));
                    return;
                }

                clan.setHomeLocation(player.getLocation());
                ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang("hombase.set"), ChatColor.YELLOW + Helper.toLocationString(player.getLocation())));
            }
            else if (ttag.equalsIgnoreCase("clear"))
            {
                if (!cp.isLeader())
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                    return;
                }

                if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.home-set"))
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                    return;
                }

                if (plugin.getSettingsManager().isHomebaseSetOnce() && clan.getHomeLocation() != null && !plugin.getPermissionsManager().has(player, "simpleclans.mod.home"))
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("home.base.only.once"));
                    return;
                }

                clan.setHomeLocation(null);
                ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("hombase.cleared"));
            }
            else if (ttag.equalsIgnoreCase("regroup"))
            {
                Location loc = player.getLocation();

                if (!cp.isLeader())
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                }

                if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.regroup"))
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                }

                List<ClanPlayer> members = clan.getAllMembers();

                for (ClanPlayer ccp : members)
                {
                    Player pl = ccp.toPlayer();

                    if (pl == null || pl.equals(player))
                    {
                        continue;
                    }

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

                ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("hombase.set") + ChatColor.YELLOW + Helper.toLocationString(loc));
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            }
        }
    }
}


