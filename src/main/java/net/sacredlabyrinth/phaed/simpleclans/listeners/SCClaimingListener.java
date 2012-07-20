/*
 * Copyright (C) 2012 p000ison
 * 
 * This work is licensed under the Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of
 * this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send
 * a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco,
 * California, 94105, USA.
 * 
 */
package net.sacredlabyrinth.phaed.simpleclans.listeners;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.PermissionType;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 *
 * @author Max
 */
public class SCClaimingListener implements Listener
{

    private SimpleClans plugin;

    public SCClaimingListener(SimpleClans plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        Player player = event.getPlayer();

        if (plugin.getSettingsManager().isBlacklistedWorld(player.getLocation().getWorld().getName())) {
            return;
        }

        Block block = event.getBlock();

        if (plugin.getSettingsManager().isClaimedBlockAllowed(block.getType())) {
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(player.getName());

        if (cp == null) {
            return;
        }

        Clan clan = cp.getClan();

//        if (clan == null) {
//            return;
//        }

        Location loc = player.getLocation();

        long start = System.currentTimeMillis();
        long startns = System.nanoTime();
        Clan clanHere = plugin.getClanManager().getClanAt(loc);

        if (clanHere != null) {

            if (clan != null) {
                if (clanHere.hasPermission(PermissionType.ALLOW_ALLY_BREAK) && clanHere.isAlly(clan.getTag())) {
                    //System.out.println("ALLOW_ALLY_BREAK");
                    return;
                }

                if (clanHere.hasPermission(PermissionType.ALLOW_UNVERIFIED_BREAK) && !cp.isTrusted()) {
                    //System.out.println("ALLOW_UNVERIFIED_BREAK");
                    return;
                }
            }

            if (clanHere.hasPermission(PermissionType.ALLOW_OUTSIDER_BREAK)) {
                //System.out.println("ALLOW_OUTSIDER_BREAK");
                if (clanHere == null) {
                    return;
                }
                if (!clanHere.equals(clan)) {
                    return;
                }
            }

            if (!clanHere.hasPermission(PermissionType.DENY_MEMBER_BREAK) && clanHere.isMember(player)) {
                //System.out.println("DENY_MEMBER_BREAK");
                return;
            }

            //return always if the player is the leader of the clan
            if (clanHere.isLeader(player)) {
                return;
            }

            event.setCancelled(true);
        }
        long endns = System.nanoTime();
        long end = System.currentTimeMillis();
        System.out.println("Checking(break) took: " + (endns - startns) + "ns " + (end - start) + "ms");
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        Player player = event.getPlayer();

        if (plugin.getSettingsManager().isBlacklistedWorld(player.getLocation().getWorld().getName())) {
            return;
        }

        Block block = event.getBlock();

        if (plugin.getSettingsManager().isClaimedBlockAllowed(block.getType())) {
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(player.getName());

        if (cp == null) {
            return;
        }

        Clan clan = cp.getClan();

        Location loc = player.getLocation();

        Clan clanHere = plugin.getClanManager().getClanAt(loc);

        if (clanHere != null) {

            if (clan != null) {
                if (clanHere.hasPermission(PermissionType.ALLOW_ALLY_BUILD) && clanHere.isAlly(clan.getTag())) {
                    //System.out.println("ALLOW_ALLY_BREAK");
                    return;
                }

                if (clanHere.hasPermission(PermissionType.ALLOW_UNVERIFIED_BUILD) && !cp.isTrusted()) {
                    //System.out.println("ALLOW_UNVERIFIED_BREAK");
                    return;
                }
            }

            if (clanHere.hasPermission(PermissionType.ALLOW_OUTSIDER_BUILD)) {
                //System.out.println("ALLOW_OUTSIDER_BREAK");
                if (clanHere == null) {
                    return;
                }
                if (!clanHere.equals(clan)) {
                    return;
                }
            }

            if (!clanHere.hasPermission(PermissionType.DENY_MEMBER_BUILD) && clanHere.isMember(player)) {
                //System.out.println("DENY_MEMBER_BREAK");
                return;
            }

            //return always if the player is the leader of the clan
            if (clanHere.isLeader(player)) {
                return;
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();

        if (plugin.getSettingsManager().isBlacklistedWorld(player.getLocation().getWorld().getName())) {
            return;
        }

        Block block = event.getClickedBlock();

        if (block == null) {
            return;
        }

        if (plugin.getSettingsManager().isClaimedBlockAllowed(block.getType())) {
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(player.getName());

        if (cp == null) {
            return;
        }

        Clan clan = cp.getClan();

        Location loc = player.getLocation();

        Clan clanHere = plugin.getClanManager().getClanAt(loc);

        if (clanHere != null) {

            if (clan != null) {
                if (clanHere.hasPermission(PermissionType.ALLOW_ALLY_BREAK) && clanHere.isAlly(clan.getTag())) {
                    //System.out.println("ALLOW_ALLY_BREAK");
                    return;
                }

                if (clanHere.hasPermission(PermissionType.ALLOW_UNVERIFIED_BREAK) && !cp.isTrusted()) {
                    //System.out.println("ALLOW_UNVERIFIED_BREAK");
                    return;
                }
            }

            if (clanHere.hasPermission(PermissionType.ALLOW_OUTSIDER_BUILD)) {
                //System.out.println("ALLOW_OUTSIDER_BREAK");
                if (clanHere == null) {
                    return;
                }
                if (!clanHere.equals(clan)) {
                    return;
                }
            }

            if (!clanHere.hasPermission(PermissionType.DENY_MEMBER_BREAK) && clanHere.isMember(player)) {
                //System.out.println("DENY_MEMBER_BREAK");
                return;
            }

            //return always if the player is the leader of the clan
            if (clanHere.isLeader(player)) {
                return;
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        Location from = event.getFrom();
        Location to = event.getTo();
        SpoutPlayer sp = SpoutManager.getPlayer(event.getPlayer());

        if (from.equals(to)) {
            return;
        }

        Clan clanTo = plugin.getClanManager().getClanAt(to);
        Clan clanFrom = plugin.getClanManager().getClanAt(from);

        if (clanTo != null) {
            if (clanFrom != null) {
                if (!clanFrom.equals(clanTo)) {
                    if (sp.isSpoutCraftEnabled()) {
                        plugin.getSpoutPluginManager().enterClanRegion(sp, clanTo.getTag());
                    } else {
                        sp.sendMessage(ChatColor.DARK_GREEN + plugin.getLang("enter.clan.region"));
                    }
                }
            } else {
                if (sp.isSpoutCraftEnabled()) {
                    plugin.getSpoutPluginManager().enterClanRegion(sp, clanTo.getTag());
                } else {
                    sp.sendMessage(ChatColor.DARK_GREEN + plugin.getLang("enter.clan.region"));
                }
            }
        } else {
            if (clanFrom != null) {
                if (sp.isSpoutCraftEnabled()) {
                    plugin.getSpoutPluginManager().leaveClanRegion(sp);
                } else {
                    sp.sendMessage(ChatColor.DARK_GREEN + plugin.getLang("leave.clan.region"));
                }
            }
        }
    }

    @EventHandler
    public void onSpoutCraftCreate(SpoutCraftEnableEvent event)
    {
        SpoutPlayer sp = event.getPlayer();
        ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(sp.getName());

        if (cp != null) {
            Clan clan = cp.getClan();
            cp.setupClanView(sp);
            if (clan != null) {
                Location home = clan.getHomeChunkMiddle();
                double x = home.getX();
                double z = home.getZ();
                double y = home.getY();

                sp.addWaypoint("Homeblock", x, y, z);
            }
        }
    }
}
