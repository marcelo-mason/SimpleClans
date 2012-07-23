package net.sacredlabyrinth.phaed.simpleclans;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportState
{
    private String playerName;
    private Location playerLocation;
    private Location destination;
    private int counter;
    private String msg;
    private boolean processing;

    public TeleportState(Player player, Location dest, String msg)
    {
        this.destination = dest;
        this.playerLocation = player.getLocation();
        this.playerName = player.getName();
        this.msg = msg;
        this.counter = SimpleClans.getInstance().getSettingsManager().getWaitSecs();
    }

    /**
     * @return
     */
    public Location getLocation()
    {
        return playerLocation;
    }

    /**
     * Whether its time for teleport
     * @return
     */
    public boolean isTeleportTime()
    {
        if (counter > 1)
        {
            counter--;
            return false;
        }

        return true;
    }

    /**
     * The player that is waiting for teleport
     * @return
     */
    public Player getPlayer()
    {
        return SimpleClans.getInstance().getServer().getPlayer(playerName);
    }

    /**
     * Get seconds left before teleport
     * @return
     */
    public int getCounter()
    {
        return counter;
    }

    public void setCounter(int counter)
    {
        this.counter = counter;
    }

    public String getMessage()
    {
        return msg;
    }

    public Location getDestination()
    {
        return destination;
    }

    public boolean isProcessing()
    {
        return processing;
    }

    public void setProcessing(boolean processing)
    {
        this.processing = processing;
    }
}
