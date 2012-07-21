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
package net.sacredlabyrinth.phaed.simpleclans;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 *
 * @author Max
 */
public class ChunkLocation implements Cloneable
{

    private int x;
    private int z;
    private String world;
    private boolean changed = false;

    public ChunkLocation()
    {
    }

    public ChunkLocation(String world, int x, int z, boolean normal)
    {
        this.x = x;
        this.z = z;
        this.world = world;

        if (normal) {
            this.x = x >> 4;
            this.z = z >> 4;
        }
    }

    public boolean isChanged()
    {
        return changed;
    }

    public void setChanged(boolean changed)
    {
        this.changed = changed;
    }

    public int getX()
    {
        return x;
    }

    public int getZ()
    {
        return z;
    }

    public String getWorld()
    {
        return world;
    }

    public World getNormalWorld()
    {
        return Bukkit.getWorld(world);
    }

    public void setWorld(String world)
    {
        this.world = world;
    }

    public void setZ(int z)
    {
        this.z = z;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getNormalX()
    {
        return x << 4;
    }

    public int getNormalZ()
    {
        return z << 4;
    }

    /**
     * Attempts to clone this object
     *
     * @return
     */
    @Override
    protected ChunkLocation clone()
    {
        try {
            return (ChunkLocation) super.clone();
        } catch (CloneNotSupportedException ex) {
            SimpleClans.debug(null, ex);
        }
        return null;
    }

    @Override
    public int hashCode()
    {
        int result = 17;
        result = 31 * result + x;
        result = 31 * result + z;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        ChunkLocation chunkLoc;
        if (obj instanceof ChunkLocation) {
            chunkLoc = (ChunkLocation) obj;
        } else {
            return false;
        }

        if (world != null) {
            if (!world.equals(chunkLoc.getWorld())) {
                return false;
            }
        }

        if (x != chunkLoc.x) {
            return false;
        }
        if (z != chunkLoc.z) {
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        return world + "," + x + "," + z;
    }

    public String toLocationString()
    {
        return SettingsManager.getWorldNumber(world) + "," + x + "," + z;
    }
}
