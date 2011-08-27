package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public final class ClanManager
{
    private SimpleClans plugin;
    private HashMap<String, Clan> clans = new HashMap<String, Clan>();
    private HashMap<String, ClanPlayer> clanPlayers = new HashMap<String, ClanPlayer>();

    /**
     *
     */
    public ClanManager()
    {
        plugin = SimpleClans.getInstance();
    }

    /**
     * Deletes all clans and clan players in memory
     */
    public void cleanData()
    {
        clans.clear();
        clanPlayers.clear();
    }

    /**
     * Import a clan into the in-memory store
     * @param clan
     */
    public void importClan(Clan clan)
    {
        this.clans.put(clan.getTag(), clan);
    }

    /**
     * Import a clan player into the in-memory store
     * @param cp
     */
    public void importClanPlayer(ClanPlayer cp)
    {
        this.clanPlayers.put(cp.getCleanName(), cp);
    }

    /**
     * Create a new clan
     * @param player
     * @param colorTag
     * @param name
     */
    public void createClan(Player player, String colorTag, String name)
    {
        ClanPlayer cp = getCreateClanPlayer(player.getName());

        boolean verified = !plugin.getSettingsManager().isRequireVerification() || plugin.getPermissionsManager().has(player, "simpleclans.mod.verify");

        Clan clan = new Clan(cp, colorTag, name, verified);
        clan.addPlayerToClan(cp);
        cp.setLeader(true);

        plugin.getStorageManager().insertClan(clan);
        importClan(clan);

        plugin.getStorageManager().updateClanPlayer(cp);
        plugin.getSpoutPluginManager().processPlayer(player.getName());
    }

    /**
     * Delete a players data file
     * @param cp
     */
    public void deleteClanPlayer(ClanPlayer cp)
    {
        clanPlayers.remove(cp.getCleanName());
        plugin.getStorageManager().deleteClanPlayer(cp);
    }

    /**
     * Whether the tag belongs to a clan
     * @param tag
     * @return
     */
    public boolean isClan(String tag)
    {
        return clans.containsKey(Helper.cleanTag(tag));

    }

    /**
     * Returns the clan the tag belongs to
     * @param tag
     * @return
     */
    public Clan getClan(String tag)
    {
        return clans.get(Helper.cleanTag(tag));
    }

    /**
     * @return the clans
     */
    public List<Clan> getClans()
    {
        return new ArrayList<Clan>(clans.values());
    }

    /**
     * Returns the collection of all clan players, including the disabled ones
     * @return
     */
    public List<ClanPlayer> getAllClanPlayers()
    {
        return new ArrayList<ClanPlayer>(clanPlayers.values());
    }

    /**
     * Gets the ClanPlayer data object if a player is currently in a clan, null if he's not in a clan
     * @param player
     * @return
     */
    public ClanPlayer getClanPlayer(Player player)
    {
        return getClanPlayer(player.getName());
    }

    /**
     * Gets the ClanPlayer data object if a player is currently in a clan, null if he's not in a clan
     * @param playerName
     * @return
     */
    public ClanPlayer getClanPlayer(String playerName)
    {
        ClanPlayer cp = clanPlayers.get(playerName.toLowerCase());

        if (cp == null)
        {
            return null;
        }

        if (cp.getClan() == null)
        {
            return null;
        }

        return cp;
    }

    /**
     * Gets the ClanPlayer data object for the player, will retrieve disabled clan players as well, these are players who used to be in a clan but are not currently in one, their data file persists and can be accessed. their clan will be null though.
     * @param playerName
     * @return
     */
    public ClanPlayer getAnyClanPlayer(String playerName)
    {
        return clanPlayers.get(playerName.toLowerCase());
    }

    /**
     * Gets the ClanPlayer object for the player, creates one if not found
     * @param playerName
     * @return
     */
    public ClanPlayer getCreateClanPlayer(String playerName)
    {
        if (clanPlayers.containsKey(playerName.toLowerCase()))
        {
            return clanPlayers.get(playerName.toLowerCase());
        }

        ClanPlayer cp = new ClanPlayer(playerName);

        plugin.getStorageManager().insertClanPlayer(cp);
        importClanPlayer(cp);

        return cp;
    }

    /**
     * Announce message to the server
     * @param msg
     */
    public void serverAnnounce(String msg)
    {
        Player[] players = plugin.getServer().getOnlinePlayers();

        for (Player player : players)
        {
            ChatBlock.sendMessage(player, ChatColor.DARK_GRAY + " * " + ChatColor.AQUA + msg);
        }

        SimpleClans.log(Level.INFO, "[Server Announce] {0}", msg);
    }

    /**
     * Update the players display name with his clan's tag
     * @param player
     */
    public void updateDisplayName(Player player)
    {
        if (plugin.getSettingsManager().isChatTags())
        {
            String prefix = plugin.getPermissionsManager().getPrefix(player);
            String lastColor = Helper.getLastColorCode(prefix);
            String fullname = player.getName();

            ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(player.getName());

            if (cp == null)
            {
                return;
            }

            Clan clan = cp.getClan();

            if (clan != null)
            {
                String tag = plugin.getSettingsManager().getTagDefaultColor() + clan.getColorTag();
                String tagLabel = tag + plugin.getSettingsManager().getTagSeparatorColor() + plugin.getSettingsManager().getTagSeparator().trim();

                fullname = tagLabel + lastColor + fullname;
            }

            player.setDisplayName(fullname);
        }
    }

    /**
     * Process a player and his clan's last seen date
     * @param player
     */
    public void updateLastSeen(Player player)
    {
        ClanPlayer cp = getAnyClanPlayer(player.getName());

        if (cp != null)
        {
            cp.updateLastSeen();
            plugin.getStorageManager().updateClanPlayer(cp);

            Clan clan = cp.getClan();

            if (clan != null)
            {
                clan.updateLastUsed();
                plugin.getStorageManager().updateClan(clan);
            }
        }
    }

    /**
     *
     * @param playerName
     */
    public void ban(String playerName)
    {
        ClanPlayer cp = getClanPlayer(playerName);
        Clan clan = cp.getClan();

        if (clan != null)
        {
            if (clan.getSize() == 1)
            {
                clan.disband();
            }
            else
            {
                cp.setClan(null);
                cp.addPastClan(clan.getColorTag() + (cp.isLeader() ? ChatColor.DARK_RED + "*" : ""));
                cp.setLeader(false);
                clan.removeMember(playerName);

                plugin.getStorageManager().updateClanPlayer(cp);
                plugin.getStorageManager().updateClan(clan);
            }
        }

        plugin.getSettingsManager().addBanned(playerName);
    }

    /**
     * Get a count of rivable clans
     * @return
     */
    public int getRivableClanCount()
    {
        int clanCount = 0;

        for (Clan tm : clans.values())
        {
            if (!SimpleClans.getInstance().getSettingsManager().isUnrivable(tm.getTag()))
            {
                clanCount++;
            }
        }

        return clanCount;
    }
}
