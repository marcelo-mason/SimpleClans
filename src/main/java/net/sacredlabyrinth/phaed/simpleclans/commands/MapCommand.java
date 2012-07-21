package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public class MapCommand
{

    private static final char northwest = '\\';
    private static final char north = 'N';
    private static final char northeast = '/';
    private static final char west = 'W';
    private static final char middle = '+';
    private static final char east = 'E';
    private static final char southwest = '/';
    private static final char southeast = '\\';
    private static final char south = 'S';
    private static final int rows = 8;
    private static final int cols = 41;
    private MapSenderThread senderThread;
    private SimpleClans plugin;

    public MapCommand()
    {
        plugin = SimpleClans.getInstance();
        senderThread = new MapSenderThread();
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, senderThread, 20L, 60L);
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg)
    {
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp != null) {

            Clan clan = cp.getClan();

            if (arg.length == 0) {
                if (plugin.getPermissionsManager().has(player, "simpleclans.claim.map")) {
                    player.sendMessage(getMap(player.getLocation(), clan));
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                }
            } else if (arg.length == 1) {
                if (arg[0].equalsIgnoreCase("auto")) {
                    if (senderThread.toggle(player.getName())) {
                        player.sendMessage("Auto");
                    } else {
                        player.sendMessage("remove");
                    }
                }
            } else {
                player.sendMessage(ChatColor.RED + MessageFormat.format(plugin.getLang("usage.map"), plugin.getSettingsManager().getCommandClan()));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
        }
    }

    public static BlockFace getDirection(Location loc)
    {
        int degrees = (Math.round(loc.getYaw()) + 270) % 360;
        if (degrees <= 22) {
            return BlockFace.NORTH;
        }
        if (degrees <= 67) {
            return BlockFace.NORTH_EAST;
        }
        if (degrees <= 112) {
            return BlockFace.EAST;
        }
        if (degrees <= 157) {
            return BlockFace.SOUTH_EAST;
        }
        if (degrees <= 202) {
            return BlockFace.SOUTH;
        }
        if (degrees <= 247) {
            return BlockFace.SOUTH_WEST;
        }
        if (degrees <= 292) {
            return BlockFace.WEST;
        }
        if (degrees <= 337) {
            return BlockFace.NORTH_WEST;
        }
        if (degrees <= 359) {
            return BlockFace.NORTH;
        }
        return null;
    }

    public static String getMap(Location loc, Clan playerClan)
    {

        SimpleClans plugin = SimpleClans.getInstance();

        int x = loc.getBlockX();
        int z = loc.getBlockZ();
        String world = loc.getWorld().getName();
        ChunkLocation playerChunk = new ChunkLocation(world, x, z, true);

        ArrayList<String> out = new ArrayList<String>();
        StringBuilder finalMap = new StringBuilder();

        //header above the map
        String header = ChatColor.GOLD + " __________________[ " + playerChunk.getX() + " " + playerChunk.getZ() + " " + (playerClan != null ? playerClan.getName() : "") + " ]__________________ \n";

        out.add(ChatColor.GOLD + header + ChatColor.GRAY);

        int x1 = x >> 4;
        int z1 = z >> 4;

        for (int xMap = 0; xMap < rows; xMap++) {
            StringBuilder row = new StringBuilder().append(' ');
            for (int zMap = cols; zMap > 0; zMap--) {
                //calculate map coordinates to real ones
                ChunkLocation chunk = new ChunkLocation(world, xMap + x1 - (rows >> 1), zMap + z1 - (cols >> 1), false);
                Clan clan = plugin.getClanManager().getClanAt(chunk);

                if (chunk.equals(playerChunk)) {
                    //- Nothing
                    row.append("P");
                } else if (clan == null) {
                    //is player location
                    row.append("-");
                } else if (playerClan.isClaimed(chunk)) {
                    //is own
                    if (playerClan.getHomeChunk().equals(chunk)) {
                        //is homeblock
                        row.append(ChatColor.GREEN).append("+").append(ChatColor.GRAY);
                    } else {
                        row.append(ChatColor.GREEN).append("/").append(ChatColor.GRAY);
                    }
                } else if (clan.getWarringClans().contains(playerClan)) {
                    //is enemy
                    if (clan.getHomeChunk().equals(chunk)) {
                        //is homeblock
                        row.append(ChatColor.DARK_RED).append("+").append(ChatColor.GRAY);
                    } else {
                        row.append(ChatColor.DARK_RED).append("/").append(ChatColor.GRAY);
                    }
                } else {
                    row.append("X");
                }

            }
            out.add(row.toString());
        }


        //int headerSize = header.length();
        //int line2 = headerSize + cols + 1 + 2;
        //int line3 = cols * 2 + 2 + headerSize + 2;

        out.set(1, getCompass(loc, 0) + out.get(1).substring(4));
        out.set(2, getCompass(loc, 1) + out.get(2).substring(4));
        out.set(3, getCompass(loc, 2) + out.get(3).substring(4));

        for (String string : out) {
            finalMap.append(string).append("\n");
        }

        return finalMap.toString();
    }

    public static String getCompass(Location loc, int index)
    {
        BlockFace dir = getDirection(loc);

        StringBuilder sb = new StringBuilder();

        sb.append(ChatColor.GOLD);
        if (index == 0) {
            sb.append(' ');
            sb.append(colorize(dir == BlockFace.NORTH_WEST, northwest));
            sb.append(colorize(dir == BlockFace.NORTH, north));
            sb.append(colorize(dir == BlockFace.NORTH_EAST, northeast));
        } else if (index == 1) {
            sb.append(' ');
            sb.append(colorize(dir == BlockFace.WEST, west)).append(middle);
            sb.append(colorize(dir == BlockFace.EAST, east));
        } else if (index == 2) {
            sb.append(' ');
            sb.append(colorize(dir == BlockFace.SOUTH_WEST, southwest));
            sb.append(colorize(dir == BlockFace.SOUTH, south));
            sb.append(colorize(dir == BlockFace.SOUTH_EAST, southeast));
        }
        sb.append(ChatColor.GRAY);

        return sb.toString();
    }

    public static String colorize(boolean active, char c)
    {
        return (active ? ChatColor.RED.toString() + c + ChatColor.GOLD.toString() : String.valueOf(c));
    }

    private class MapSenderThread implements Runnable
    {

        Set<String> players = new HashSet<String>();

        public void addPlayer(String player)
        {
            players.add(player);
        }

        public void removePlayer(String player)
        {
            players.remove(player);
        }

        public boolean toggle(String player)
        {
            if (players.contains(player)) {
                players.remove(player);
                return false;
            } else {
                players.add(player);
                return true;
            }
        }

        @Override
        public void run()
        {
            for (String name : players) {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(name);
                if (cp != null) {
                    Player player = plugin.getServer().getPlayer(name);
                    player.sendMessage(getMap(player.getLocation(), cp.getClan()));
                }
            }
        }
    }
}
