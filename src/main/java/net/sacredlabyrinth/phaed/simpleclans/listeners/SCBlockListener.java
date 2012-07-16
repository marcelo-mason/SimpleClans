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
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Max
 */
public class SCBlockListener implements Listener
{

    private SimpleClans plugin;

    public SCBlockListener(SimpleClans plugin)
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
        ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(player.getName());
        Clan clan = cp.getClan();

        if (clan == null) {
            return;
        }

        for (Clan clans : plugin.getClanManager().getClans()) {
            if (clans.isClaimed(block.getLocation()) && !clans.equals(clan)) {
                player.sendMessage("Here is a plot from " + clans.getName());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        Player player = event.getPlayer();

        if (plugin.getSettingsManager().isBlacklistedWorld(player.getLocation().getWorld().getName())) {
            return;
        }

        Block block = event.getBlock();
        ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(player.getName());
        Clan clan = cp.getClan();

        if (clan == null) {
            return;
        }

        for (Clan clans : plugin.getClanManager().getClans()) {
            if (clans.isClaimed(block.getLocation()) && !clans.equals(clan)) {
                player.sendMessage("Here is a plot from " + clans.getName());
                event.setCancelled(true);
            }
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
        ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(player.getName());
        Clan clan = cp.getClan();

        if (clan == null || block == null) {
            return;
        }

        for (Clan clans : plugin.getClanManager().getClans()) {
            if (clans.isClaimed(block.getLocation()) && !clans.equals(clan)) {
                player.sendMessage("Here is a plot from " + clans.getName());
                event.setCancelled(true);
            }
        }
    }
}
