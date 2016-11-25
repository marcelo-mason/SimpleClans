package net.sacredlabyrinth.phaed.simpleclans;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeleportState {
    private UUID playerUniqueId;
    private String playerName;
    private Location playerLocation;
    private Location destination;
    private int counter;
    private String clanName;
    private boolean processing;

    public TeleportState(Player player, Location dest, String clanName) {
        this.destination = dest;
        this.playerLocation = player.getLocation();
        this.playerName = player.getName();
        this.clanName = clanName;
        this.counter = SimpleClans.getInstance().getSettingsManager().getWaitSecs();
        this.playerUniqueId = player.getUniqueId();
    }

    /**
     * @return
     */
    public Location getLocation() {
        return this.playerLocation;
    }

    /**
     * Whether its time for teleport
     *
     * @return
     */
    public boolean isTeleportTime() {
        if (this.counter > 1) {
            this.counter--;
            return false;
        }

        return true;
    }

    /**
     * The player that is waiting for teleport
     *
     * @return
     */
    public Player getPlayer() {
        if (SimpleClans.getInstance().hasUUID()) {
            return SimpleClans.getInstance().getServer().getPlayer(this.playerUniqueId);
        } else {
            return SimpleClans.getInstance().getServer().getPlayer(this.playerName);
        }
    }

    /**
     * Get seconds left before teleport
     *
     * @return
     */
    public int getCounter() {
        return this.counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public String getClanName() {
        return this.clanName;
    }

    public Location getDestination() {
        return this.destination;
    }

    public boolean isProcessing() {
        return this.processing;
    }

    public void setProcessing(boolean processing) {
        this.processing = processing;
    }

    public UUID getUniqueId() {
        return this.playerUniqueId;
    }
}
