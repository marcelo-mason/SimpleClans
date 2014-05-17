package net.sacredlabyrinth.phaed.simpleclans.events;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author NeT32
 */
public class PlayerKickedClanEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Clan clan;
    private final ClanPlayer target;

    public PlayerKickedClanEvent(Clan clan, ClanPlayer target) {
        this.clan = clan;
        this.target = target;
    }

    public Clan getClan() {
        return this.clan;
    }

    public ClanPlayer getClanPlayer() {
        return this.target;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
