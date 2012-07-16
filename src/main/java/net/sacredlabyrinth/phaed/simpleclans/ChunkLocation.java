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

import org.bukkit.Location;
import org.bukkit.World;

/**
 *
 * @author Max
 */
public class ChunkLocation
{

    private int x;
    private int z;
    private String world;

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

    public boolean containsBlockLocation(Location loc)
    {
        return containsBlockLocation(loc.getWorld(), loc.getBlockX(), loc.getBlockZ());
    }

    public boolean containsBlockLocation(World locWorld, int locX, int locZ)
    {
        int ChunkX = locX >> 4;
        int ChunkZ = locZ >> 4;
        return locWorld.getName().equals(world) && ChunkX == x && ChunkZ == z;
    }

    public boolean isChunkLocation(World locWorld, int locX, int locZ)
    {
        return locWorld.getName().equals(world) && locX == x && locZ == z;
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

        if (world == null) {
            return false;
        }

        if (!world.equals(chunkLoc.getWorld())) {
            return false;
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
}
