package net.sacredlabyrinth.phaed.simpleclans.events;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author NeT32
 */
public class DisbandClanEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Clan clan;

    public DisbandClanEvent(Clan clan) {
        this.clan = clan;
    }

    public Clan getClan() {
        return this.clan;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
