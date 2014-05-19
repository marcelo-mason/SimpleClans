package net.sacredlabyrinth.phaed.simpleclans.events;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author NeT32
 */
public class PlayerHomeSetEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Clan clan;
    private final ClanPlayer cp;
    private final Location loc;

    public PlayerHomeSetEvent(Clan clan, ClanPlayer cp, Location loc) {
        this.clan = clan;
        this.cp = cp;
        this.loc = loc;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Clan getClan() {
        return this.clan;
    }

    public ClanPlayer getClanPlayer() {
        return this.cp;
    }

    public Location getLocation() {
        return this.loc;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
