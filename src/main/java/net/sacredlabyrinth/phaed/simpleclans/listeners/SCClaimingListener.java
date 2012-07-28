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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.getspout.spoutapi.SpoutManager;
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
            //System.out.println("backlist");
            return;
        }

        Block block = event.getBlock();

        if (plugin.getSettingsManager().isClaimedBlockAllowed(block.getType())) {
            //System.out.println("allowed");
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(player.getName());

        Clan clan = null;

        if (cp != null) {
            clan = cp.getClan();
        }

        Location loc = player.getLocation();

//        long start = System.currentTimeMillis();
//        long startns = System.nanoTime();
        Clan clanHere = plugin.getClanManager().getClanAt(loc);

        if (!isBreakAllowed(cp, clanHere, clan)) {
            event.setCancelled(true);
        }
//        long endns = System.nanoTime();
//        long end = System.currentTimeMillis();
//        System.out.println("Checking(break) took: " + (endns - startns) + "ns " + (end - start) + "ms");
    }

    public boolean isBreakAllowed(ClanPlayer cp, Clan clanHere, Clan clan)
    {
        if (clanHere != null) {

            String name = cp.getName();

            if (clan != null) {

                if (plugin.getSettingsManager().isAllowedDestroyInWar() && clan.isWarring(clanHere)) {
                    return true;
                }

                if (clanHere.hasPermission(PermissionType.ALLOW_ALLY_BREAK) && clanHere.isAlly(clan.getTag())) {
                    //System.out.println("ALLOW_ALLY_BREAK");
                    return true;
                }

                if (clanHere.hasPermission(PermissionType.ALLOW_UNVERIFIED_BREAK) && clanHere.isMember(name) && !cp.isTrusted()) {
                    //System.out.println("ALLOW_UNVERIFIED_BREAK");
                    return true;
                }
            }

            if (clanHere.hasPermission(PermissionType.ALLOW_OUTSIDER_BREAK)) {
                //System.out.println("ALLOW_OUTSIDER_BREAK");
                if (!clanHere.equals(clan)) {
                    return true;
                }
            }

            if (!clanHere.hasPermission(PermissionType.DENY_MEMBER_BREAK) && clanHere.isMember(name)) {
                //System.out.println("DENY_MEMBER_BREAK");
                return true;
            }

            //return always if the player is the leader of the clan
            if (clanHere.isLeader(name) || (clanHere.isMember(name) || cp.isTrusted())) {
                //System.out.println("leader");
                return true;
            }

            return false;
        }
        return true;
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

        Clan clan = null;

        if (cp != null) {
            clan = cp.getClan();
        }

        Location loc = player.getLocation();

        Clan clanHere = plugin.getClanManager().getClanAt(loc);

        if (!isPlaceAllowed(cp, clanHere, clan)) {
            event.setCancelled(true);
        }
    }

    public boolean isPlaceAllowed(ClanPlayer cp, Clan clanHere, Clan clan)
    {
        if (clanHere != null) {

            String name = cp.getName();


            if (clan != null) {

                if (plugin.getSettingsManager().isAllowedDestroyInWar() && clan.isWarring(clanHere)) {
                    return true;
                }


                if (clanHere.hasPermission(PermissionType.ALLOW_ALLY_BUILD) && clanHere.isAlly(clan.getTag())) {
                    //System.out.println("ALLOW_ALLY_BUILD");
                    return true;
                }

                if (clanHere.hasPermission(PermissionType.ALLOW_UNVERIFIED_BUILD) && !cp.isTrusted()) {
                    //System.out.println("ALLOW_UNVERIFIED_BUILD");
                    return true;
                }
            }

            if (clanHere.hasPermission(PermissionType.ALLOW_OUTSIDER_BUILD)) {
                //System.out.println("ALLOW_OUTSIDER_BUILD");
                if (clanHere == null) {
                    return true;
                }
                if (!clanHere.equals(clan)) {
                    return true;
                }
            }

            if (!clanHere.hasPermission(PermissionType.DENY_MEMBER_BUILD) && clanHere.isMember(name) && cp.isTrusted()) {
                //System.out.println("DENY_MEMBER_BUILD");
                return true;
            }

            //return always if the player is the leader of the clan
            if (clanHere.isLeader(name)) {
                //System.out.println("leader");
                return true;
            }

            return false;
        }
        return true;
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

        Material type = block.getType();

        if (!(type == Material.WOODEN_DOOR || type == Material.LEVER || type == Material.STONE_BUTTON || type == Material.WOOD_PLATE || type == Material.STONE_PLATE || type == Material.CROPS)) {
            return;
        }

        if (plugin.getSettingsManager().isClaimedBlockAllowed(block.getType())) {
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(player.getName());

        Clan clan = null;

        if (cp != null) {
            clan = cp.getClan();
        }

        Location loc = player.getLocation();

        Clan clanHere = plugin.getClanManager().getClanAt(loc);

        if (!isInteractAllowed(cp, clanHere, clan)) {
            event.setCancelled(true);
        }
    }

    public boolean isInteractAllowed(ClanPlayer cp, Clan clanHere, Clan clan)
    {
        if (clanHere != null) {

            String name = cp.getName();

            if (clan != null) {

                if (plugin.getSettingsManager().isAllowedDestroyInWar() && clan.isWarring(clanHere)) {
                    return true;
                }

                if (clanHere.hasPermission(PermissionType.ALLOW_ALLY_INTERACT) && clanHere.isAlly(clan.getTag())) {
                    //System.out.println("ALLOW_ALLY_INTERACT");
                    return true;
                }

                if (clanHere.hasPermission(PermissionType.ALLOW_UNVERIFIED_INTERACT) && !cp.isTrusted()) {
                    //System.out.println("ALLOW_UNVERIFIED_INTERACT");
                    return true;
                }
            }

            if (clanHere.hasPermission(PermissionType.ALLOW_OUTSIDER_INTERACT)) {
                //System.out.println("ALLOW_OUTSIDER_INTERACT");
                if (clanHere == null) {
                    return true;
                }
                if (!clanHere.equals(clan)) {
                    return true;
                }
            }

            if (!clanHere.hasPermission(PermissionType.DENY_MEMBER_INTERACT) && clanHere.isMember(name) && cp.isTrusted()) {
                //System.out.println("DENY_MEMBER_INTERACT");
                return true;
            }

            //return always if the player is the leader of the clan
            if (clanHere.isLeader(name)) {
                //System.out.println("leader");
                return true;
            }

            return false;
        }
        return true;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        Location from = event.getFrom();
        Location to = event.getTo();
        Player player = event.getPlayer();

        if (from.equals(to)) {
            return;
        }

        Clan clanTo = plugin.getClanManager().getClanAt(to);
        Clan clanFrom = plugin.getClanManager().getClanAt(from);

        if (clanTo != null) {
            if (clanFrom != null) {
                if (!clanFrom.equals(clanTo)) {
                    if (plugin.hasSpout()) {
                        SpoutPlayer sp = SpoutManager.getPlayer(player);
                        if (sp.isSpoutCraftEnabled()) {
                            plugin.getSpoutPluginManager().enterClanRegion(sp, clanTo.getTag());
                        } else {
                            player.sendMessage(ChatColor.GRAY + clanTo.getTag());
                        }
                    } else {
                        player.sendMessage(ChatColor.GRAY + clanTo.getTag());
                    }
                }
            } else {
                if (plugin.hasSpout()) {
                    SpoutPlayer sp = SpoutManager.getPlayer(player);
                    if (sp.isSpoutCraftEnabled()) {

                        plugin.getSpoutPluginManager().enterClanRegion(sp, clanTo.getTag());
                    } else {
                        player.sendMessage(ChatColor.GRAY + clanTo.getTag());
                    }
                } else {
                    player.sendMessage(ChatColor.GRAY + clanTo.getTag());
                }
            }
        } else {
            if (clanFrom != null) {
                if (plugin.hasSpout()) {
                    SpoutPlayer sp = SpoutManager.getPlayer(player);
                    if (sp.isSpoutCraftEnabled()) {


                        plugin.getSpoutPluginManager().leaveClanRegion(sp);
                    } else {
                        player.sendMessage(ChatColor.DARK_GREEN + plugin.getLang("wilderness"));
                    }
                }
            }
        }
    }
}
