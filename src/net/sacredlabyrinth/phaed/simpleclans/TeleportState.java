package net.sacredlabyrinth.phaed.simpleclans;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportState
{
    private String playerName;
    private Location playerLocation;
    private int counter;
    private String clanName;

    public TeleportState(Player player, Location dest, String clanName)
    {
        this.playerLocation = dest;
        this.playerName = player.getName();
        this.clanName = clanName;
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
        if (counter > 0)
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

    public String getClanName()
    {
        return clanName;
    }
}
