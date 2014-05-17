package net.sacredlabyrinth.phaed.simpleclans.events;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author NeT32
 */
public class CreateClanEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Clan clan;

    public CreateClanEvent(Clan clan) {
        this.clan = clan;
    }

    public Clan getClan() {
        return this.clan;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
