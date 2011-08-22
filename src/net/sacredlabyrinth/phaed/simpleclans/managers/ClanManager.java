package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import java.util.ArrayList;
import java.util.Collections;
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
    private HashMap<String, Clan> simpleclans = new HashMap<String, Clan>();
    private HashMap<String, ClanPlayer> clanPlayers = new HashMap<String, ClanPlayer>();

    /**
     *
     * @param plugin
     */
    public ClanManager()
    {
        plugin = SimpleClans.getInstance();
    }

    /**
     * Deletes all simpleclans and clan players in memory
     */
    public void cleanData()
    {
        simpleclans.clear();
        clanPlayers.clear();
    }

    /**
     * Import a clan into the in-memory store
     * @param clan
     */
    public void importClan(Clan clan)
    {
        this.simpleclans.put(clan.getTag(), clan);
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
        clan.addMember(cp);
        cp.setLeader(true);
        cp.setTag(Helper.cleanTag(colorTag));

        plugin.getStorageManager().updateClanPlayer(cp);
        plugin.getStorageManager().insertClan(clan);
        importClan(clan);

        plugin.getSpoutManager().processPlayer(player.getName());
    }

    /**
     * Delete a clan
     * @param clan
     */
    public void deleteClan(Clan clan)
    {
        for (ClanPlayer cp : clanPlayers.values())
        {
            if (cp.getTag().equals(clan.getTag()))
            {
                cp.setTag("");

                if (plugin.getClanManager().isVerified(clan))
                {
                    cp.addPastClan(clan.getColorTag() + (cp.isLeader() ? ChatColor.DARK_RED + "*" : ""));
                }

                cp.setLeader(false);

                plugin.getStorageManager().updateClanPlayer(cp);

                plugin.getSpoutManager().processPlayer(cp.getName());
            }
        }

        simpleclans.remove(clan.getTag());

        for (Clan tm : simpleclans.values())
        {
            if (tm.removeRival(clan.getTag()))
            {
                plugin.getClanManager().addBb("Clan Deleted", tm, ChatColor.AQUA + Helper.capitalize(clan.getName()) + " has been deleted.  Rivalry has ended.");
            }

            if (tm.removeAlly(clan.getTag()))
            {
                plugin.getClanManager().addBb("Clan Deleted", tm, ChatColor.AQUA + Helper.capitalize(clan.getName()) + " has been deleted.  Alliance has ended.");
            }
        }

        plugin.getStorageManager().deleteClan(clan);
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
        return simpleclans.containsKey(Helper.cleanTag(tag));

    }

    /**
     * Returns the clan the tag belongs to
     * @param tag
     * @return
     */
    public Clan getClan(String tag)
    {
        return simpleclans.get(Helper.cleanTag(tag));
    }

    /**
     * Return all simpleclans a player belongs to
     * @param player
     * @return
     */
    public Clan getClan(Player player)
    {
        ClanPlayer cp = clanPlayers.get(player.getName().toLowerCase());

        if (cp != null && cp.getTag() != null)
        {
            return getClan(cp.getTag());
        }

        return null;
    }

    /**
     * Returns the clan the player belongs to
     * @param playerName
     * @return
     */
    public Clan getClanByPlayerName(String playerName)
    {
        if (playerName == null)
        {
            return null;
        }

        ClanPlayer cp = clanPlayers.get(playerName.toLowerCase());

        if (cp != null && cp.getTag() != null)
        {
            return getClan(cp.getTag());
        }

        return null;
    }

    /**
     * @return the simpleclans
     */
    public ArrayList<Clan> getSimpleClans()
    {
        return new ArrayList(simpleclans.values());
    }

    /**
     * Gets the ClanPlayer object for the player, null if not found
     * @param player
     * @return
     */
    public ClanPlayer getClanPlayer(Player player)
    {
        return clanPlayers.get(player.getName().toLowerCase());
    }

    /**
     * Gets the ClanPlayer object for the player, null if not found
     * @param playerName
     * @return
     */
    public ClanPlayer getClanPlayer(String playerName)
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
     * Check if the player is registered (has a ClanPlayer entry)
     * @param player
     * @return
     */
    public boolean isRegistered(Player player)
    {
        if (clanPlayers.containsKey(player.getName().toLowerCase()))
        {
            return true;
        }
        return false;
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
     * Announce message to a whole clan
     * @param playerName
     * @param clan
     * @param msg
     */
    public void clanAnnounce(String playerName, Clan clan, String msg)
    {
        String message = Helper.toColor(plugin.getSettingsManager().getClanChatBracketColor()) + plugin.getSettingsManager().getClanChatTagBracketLeft() + Helper.toColor(plugin.getSettingsManager().getTagDefaultColor()) + Helper.parseColors(clan.getColorTag()) + Helper.toColor(plugin.getSettingsManager().getClanChatBracketColor()) + plugin.getSettingsManager().getClanChatTagBracketRight() + " " + Helper.toColor(plugin.getSettingsManager().getClanChatAnnouncementColor()) + msg;

        List<ClanPlayer> allMembers = getAllMembers(clan);

        for (ClanPlayer cp : allMembers)
        {
            Player pl = plugin.getServer().getPlayer(cp.getName());

            if (pl != null)
            {
                ChatBlock.sendMessage(pl, message);
            }
        }
        SimpleClans.log(Level.INFO, "[Clan Announce] [{0}] {1}", playerName, Helper.stripColors(message));
    }

    /**
     * Announce message to a all the leaders of a clan
     * @param playerName
     * @param clan
     * @param msg
     */
    public void leaderAnnounce(String playerName, Clan clan, String msg)
    {
        String message = Helper.toColor(plugin.getSettingsManager().getClanChatBracketColor()) + plugin.getSettingsManager().getClanChatTagBracketLeft() + Helper.toColor(plugin.getSettingsManager().getTagDefaultColor()) + Helper.parseColors(clan.getColorTag()) + Helper.toColor(plugin.getSettingsManager().getClanChatBracketColor()) + plugin.getSettingsManager().getClanChatTagBracketRight() + " " + Helper.toColor(plugin.getSettingsManager().getClanChatAnnouncementColor()) + msg;

        List<ClanPlayer> leaders = getLeaders(clan);

        for (ClanPlayer cp : leaders)
        {
            Player pl = plugin.getServer().getPlayer(cp.getName());

            if (pl != null)
            {
                ChatBlock.sendMessage(pl, message);
            }
        }
        SimpleClans.log(Level.INFO, "[Leader Announce] [{0}] " + Helper.stripColors(message), playerName);
    }

    /**
     * Announce message to a whole clan plus audio alert
     * @param playerName
     * @param clan
     * @param msg
     */
    public void audioAnnounce(String playerName, Clan clan, String msg)
    {
        clanAnnounce(playerName, clan, msg);

        List<String> members = clan.getMembers();

        for (String member : members)
        {
            Player pl = plugin.getServer().getPlayer(member);

            if (pl != null)
            {
                plugin.getSpoutManager().playAlert(pl);
            }
        }
    }

    /**
     * Add a new bb message and announce it to all online members of a clan
     * @param announcerName
     * @param clan
     * @param msg
     */
    public void addBb(String announcerName, Clan clan, String msg)
    {
        if (isVerified(clan))
        {
            clan.addBb(msg);
            clanAnnounce(announcerName, clan, Helper.toColor(plugin.getSettingsManager().getBbAccentColor()) + "* " + Helper.toColor(plugin.getSettingsManager().getBbColor()) + Helper.parseColors(msg));
            plugin.getStorageManager().updateClan(clan);
        }
    }

    /**
     * Displays a clan's bb to a player
     * @param player
     */
    public void displayBb(Player player)
    {
        Clan clan = getClan(player);

        if (clan != null && isVerified(clan))
        {
            List<String> bb = clan.getBb();
            List<String> chunk = bb.subList(Math.max(bb.size() - plugin.getSettingsManager().getBbSize(), 0), bb.size());

            ChatBlock.sendBlank(player);
            ChatBlock.saySingle(player, Helper.toColor(plugin.getSettingsManager().getBbAccentColor()) + "* " + Helper.toColor(plugin.getSettingsManager().getPageHeadingsColor()) + Helper.capitalize(clan.getName()) + " bulletin board");

            for (String msg : chunk)
            {
                ChatBlock.saySingle(player, Helper.toColor(plugin.getSettingsManager().getBbAccentColor()) + "* " + Helper.toColor(plugin.getSettingsManager().getBbColor()) + Helper.parseColors(msg));
            }
            ChatBlock.sendBlank(player);
        }
    }

    /**
     * Change a clan's tag
     * @param clan
     * @param tag
     */
    public void changeClanTag(Clan clan, String tag)
    {
        clan.setColorTag(tag);
        plugin.getStorageManager().updateClan(clan);
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

            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp == null)
            {
                return;
            }

            Clan clan = plugin.getClanManager().getClan(player);

            if (clan != null)
            {
                String tag = Helper.toColor(plugin.getSettingsManager().getTagDefaultColor()) + Helper.parseColors(clan.getColorTag());
                String tagLabel = tag + Helper.toColor(plugin.getSettingsManager().getTagSeparatorColor()) + plugin.getSettingsManager().getTagSeparator().trim();

                fullname = tagLabel + lastColor + fullname;
            }

            player.setDisplayName(fullname);
        }
    }


    /**
     * Add a player to a clan
     * @param cp
     * @param clan
     */
    public void addMemberToClan(ClanPlayer cp, Clan clan)
    {
        cp.removePastClan(clan.getColorTag());
        cp.setTag(clan.getTag());
        cp.setLeader(false);
        clan.addMember(cp);

        plugin.getStorageManager().updateClanPlayer(cp);
        plugin.getStorageManager().updateClan(clan);

        plugin.getSpoutManager().processPlayer(cp.getName());
    }

    /**
     * Remove a player from a clan
     * @param player
     * @param clan
     */
    public void removePlayer(Player player, Clan clan)
    {
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
        cp.setTag("");
        cp.addPastClan(clan.getColorTag() + (cp.isLeader() ? ChatColor.DARK_RED + "*" : ""));
        cp.setLeader(false);
        clan.removeMember(player);

        plugin.getStorageManager().updateClanPlayer(cp);
        plugin.getStorageManager().updateClan(clan);

        plugin.getSpoutManager().processPlayer(cp.getName());
    }

    /**
     * Promote a member to a leader of a clan
     * @param playerName
     * @param clan
     */
    public void promote(String playerName, Clan clan)
    {
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(playerName);
        cp.setLeader(true);

        plugin.getStorageManager().updateClanPlayer(cp);
        plugin.getStorageManager().updateClan(clan);

        plugin.getSpoutManager().processPlayer(cp.getName());
    }

    /**
     * Demote a leader back to a member of a clan
     * @param playerName
     * @param clan
     */
    public void demote(String playerName, Clan clan)
    {
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(playerName);
        cp.setLeader(false);

        plugin.getStorageManager().updateClanPlayer(cp);
        plugin.getStorageManager().updateClan(clan);

        plugin.getSpoutManager().processPlayer(cp.getName());
    }

    /**
     * Demote a leader back to a member of a clan
     * @param playerName
     * @param clan
     */
    public void invite(String playerName, Clan clan)
    {
        ClanPlayer cp = plugin.getClanManager().getCreateClanPlayer(playerName);
        cp.setTag(clan.getTag());
        cp.setLeader(false);
        clan.addMember(cp);

        plugin.getStorageManager().updateClanPlayer(cp);
        plugin.getStorageManager().updateClan(clan);
    }

    /**
     * Add an ally to a clan
     * @param clan
     * @param ally
     */
    public void addAlly(Clan clan, Clan ally)
    {
        clan.removeRival(ally.getTag());
        clan.addAlly(ally.getTag());

        ally.removeRival(clan.getTag());
        ally.addAlly(clan.getTag());

        plugin.getStorageManager().updateClan(clan);
        plugin.getStorageManager().updateClan(ally);
    }

    /**
     *
     * @param clan
     * @param ally
     */
    public void removeAlly(Clan clan, Clan ally)
    {
        clan.removeAlly(ally.getTag());
        ally.removeAlly(clan.getTag());

        plugin.getStorageManager().updateClan(clan);
        plugin.getStorageManager().updateClan(ally);
    }

    /**
     * Remove an ally from a clan
     * @param clan
     * @param rival
     */
    public void addRival(Clan clan, Clan rival)
    {
        clan.removeAlly(rival.getTag());
        clan.addRival(rival.getTag());

        rival.removeAlly(clan.getTag());
        rival.addRival(clan.getTag());

        plugin.getStorageManager().updateClan(clan);
        plugin.getStorageManager().updateClan(rival);
    }

    /**
     *
     * @param clan
     * @param rival
     */
    public void removeRival(Clan clan, Clan rival)
    {
        clan.removeRival(rival.getTag());
        rival.removeRival(clan.getTag());

        plugin.getStorageManager().updateClan(clan);
        plugin.getStorageManager().updateClan(rival);
    }

    /**
     * Verify a clan
     * @param clan
     */
    public void verifyClan(Clan clan)
    {
        clan.setVerified(true);
        plugin.getStorageManager().updateClan(clan);
    }

    /**
     * Tells you if the clan is verified, always returns true if no verification is required
     * @param clan
     * @return
     */
    public boolean isVerified(Clan clan)
    {
        if (clan != null && plugin.getSettingsManager().isRequireVerification())
        {
            return clan.isVerified();
        }

        return true;
    }

    /**
     * Check whether any clan member is online
     * @param clan
     * @return
     */
    public boolean isAnyOnline(Clan clan)
    {
        List<String> members = clan.getMembers();

        for (String member : members)
        {
            if (Helper.isOnline(member))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Check whether all leaders of a clan are online
     * @param clan
     * @return
     */
    public boolean allLeadersOnline(Clan clan)
    {
        List<ClanPlayer> leaders = getLeaders(clan);

        for (ClanPlayer leader : leaders)
        {
            if (!Helper.isOnline(leader.getName()))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Check whether all leaders, except for the one passed in, are online
     * @param clan
     * @param playerName
     * @return
     */
    public boolean allOtherLeadersOnline(Clan clan, String playerName)
    {
        List<ClanPlayer> leaders = getLeaders(clan);

        for (ClanPlayer leader : leaders)
        {
            if (leader.getName().equalsIgnoreCase(playerName))
            {
                continue;
            }

            if (!Helper.isOnline(leader.getName()))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Process a player login
     * @param player
     */
    public void processPlayerLogin(Player player)
    {
        ClanPlayer cp = getClanPlayer(player.getName());

        if (cp != null)
        {
            cp.updateLastSeen();
            Clan clan = getClan(cp.getTag());

            if (clan != null)
            {
                clan.updateLastUsed();
                plugin.getStorageManager().updateClan(clan);
            }
            plugin.getSpoutManager().processPlayer(cp.getName());
        }
    }

    /**
     * Process a player's logoff
     * @param player
     */
    public void processPlayerLogOff(Player player)
    {
        ClanPlayer cp = getClanPlayer(player.getName());

        if (cp != null)
        {
            cp.updateLastSeen();
        }
    }

    /**
     * Returns a string with all ally tags
     * @param clan
     * @param sep
     * @return
     */
    public String getAllyString(Clan clan, String sep)
    {
        String out = "";

        for (String allyTag : clan.getAllies())
        {
            Clan ally = plugin.getClanManager().getClan(allyTag);

            if (ally != null)
            {
                out += ally.getColorTag() + sep;
            }
        }

        out = Helper.stripTrailing(out, sep);

        if (out.trim().isEmpty())
        {
            return "None";
        }

        return out;
    }

    /**
     * Returns a string with all rival tags
     * @param clan
     * @param sep
     * @return
     */
    public String getRivalString(Clan clan, String sep)
    {
        String out = "";

        for (String rivalTag : clan.getRivals())
        {
            Clan rival = plugin.getClanManager().getClan(rivalTag);

            if (rival != null)
            {
                out += rival.getColorTag() + sep;
            }
        }

        out = Helper.stripTrailing(out, sep);

        if (out.trim().isEmpty())
        {
            return "None";
        }

        return out;
    }

    /**
     * @param clan
     * @param prefix
     * @param sep
     * @return the formatted leaders string
     */
    public String getLeadersString(Clan clan, String prefix, String sep)
    {
        String out = "";

        for (String member : clan.getMembers())
        {
            ClanPlayer cp = clanPlayers.get(member.toLowerCase());

            if (cp.isLeader())
            {
                out += prefix + cp.getName() + sep;
            }
        }

        return Helper.stripTrailing(out, sep);
    }

    /**
     * Check if a player is a leader of a clan
     * @param clan
     * @param player
     * @return the leaders
     */
    public boolean isLeader(Clan clan, Player player)
    {
        if (clan.isMember(player))
        {
            ClanPlayer cp = clanPlayers.get(player.getName().toLowerCase());

            if (cp.isLeader())
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if a player is a leader of a clan
     * @param clan
     * @param playerName
     * @return the leaders
     */
    public boolean isLeader(Clan clan, String playerName)
    {
        if (clan.isMember(playerName))
        {
            ClanPlayer cp = clanPlayers.get(playerName.toLowerCase());

            if (cp.isLeader())
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @param clan
     * @return the leaders
     */
    public List<ClanPlayer> getLeaders(Clan clan)
    {
        List<ClanPlayer> out = new ArrayList<ClanPlayer>();

        for (String member : clan.getMembers())
        {
            ClanPlayer cp = clanPlayers.get(member.toLowerCase());

            if (cp.isLeader())
            {
                out.add(cp);
            }
        }

        return out;
    }

    /**
     * @param clan
     * @return non leaders
     */
    public List<ClanPlayer> getNonLeaders(Clan clan)
    {
        List<ClanPlayer> out = new ArrayList<ClanPlayer>();

        for (String member : clan.getMembers())
        {
            ClanPlayer cp = clanPlayers.get(member.toLowerCase());

            if (!cp.isLeader())
            {
                out.add(cp);
            }
        }

        Collections.sort(out);

        return out;
    }

    /**
     * @param clan
     * @return all members
     */
    public List<ClanPlayer> getAllMembers(Clan clan)
    {
        List<ClanPlayer> out = new ArrayList<ClanPlayer>();
        out.addAll(getLeaders(clan));
        out.addAll(getNonLeaders(clan));
        return out;
    }

    /**
     * Gets averaged clan ratio form the entire clan
     * @param clan
     * @return
     */
    public float getAverageKDR(Clan clan)
    {
        float total = 0;

        if (clan.getMembers().isEmpty())
        {
            return total;
        }

        for (String member : clan.getMembers())
        {
            ClanPlayer cp = clanPlayers.get(member.toLowerCase());
            total += cp.getKillDeathRatio();
        }

        return ((float) total) / ((float) clan.getSize());
    }

    /**
     * Gets average weighted kills for the clan
     * @param clan
     * @return
     */
    public int getAverageWK(Clan clan)
    {
        int total = 0;

        if (clan.getMembers().isEmpty())
        {
            return total;
        }

        for (String member : clan.getMembers())
        {
            ClanPlayer cp = clanPlayers.get(member.toLowerCase());
            total += cp.getWeightedKills();
        }

        return total / clan.getSize();
    }

    /**
     * Gets total rival kills for the clan
     * @param clan
     * @return
     */
    public int getTotalRival(Clan clan)
    {
        int total = 0;

        if (clan.getMembers().isEmpty())
        {
            return total;
        }

        for (String member : clan.getMembers())
        {
            ClanPlayer cp = clanPlayers.get(member.toLowerCase());
            total += cp.getRivalKills();
        }

        return total;
    }

    /**
     * Gets total neutral kills for the clan
     * @param clan
     * @return
     */
    public int getTotalNeutral(Clan clan)
    {
        int total = 0;

        if (clan.getMembers().isEmpty())
        {
            return total;
        }

        for (String member : clan.getMembers())
        {
            ClanPlayer cp = clanPlayers.get(member.toLowerCase());
            total += cp.getNeutralKills();
        }

        return total;
    }

    /**
     * Gets total civilian kills for the clan
     * @param clan
     * @return
     */
    public int getTotalCivilian(Clan clan)
    {
        int total = 0;

        if (clan.getMembers().isEmpty())
        {
            return total;
        }

        for (String member : clan.getMembers())
        {
            ClanPlayer cp = clanPlayers.get(member.toLowerCase());
            total += cp.getCivilianKills();
        }

        return total;
    }

    /**
     *
     * @param playerName
     */
    public void ban(String playerName)
    {
        ClanPlayer cp = getClanPlayer(playerName);
        Clan clan = getClan(cp.getTag());

        if (clan != null)
        {
            if (clan.getSize() == 1)
            {
                deleteClan(clan);
            }
            else
            {
                cp.setTag("");
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
     * SEt a clan's cape url
     * @param url
     * @param clan
     */
    public void setClanCape(String url, Clan clan)
    {
        clan.setCapeUrl(url);
        plugin.getStorageManager().updateClan(clan);

        for (String member : clan.getMembers())
        {
            plugin.getSpoutManager().processPlayer(member);
        }
    }

    /**
     * @return the clanPlayers
     */
    public HashMap<String, ClanPlayer> getClanPlayers()
    {
        return clanPlayers;
    }

    /**
     * @param clanPlayers the clanPlayers to set
     */
    public void setClanPlayers(HashMap<String, ClanPlayer> clanPlayers)
    {
        this.clanPlayers = clanPlayers;
    }

    /**
     * Check whether the clan has crossed the rival limit
     * @param clan
     * @return
     */
    public boolean reachedRivalLimit(Clan clan)
    {
        List<String> rivals = clan.getRivals();

        int rivalCount = rivals.size();

        int clanCount = 0;

        for (Clan tm : simpleclans.values())
        {
            if (tm.equals(clan))
            {
                continue;
            }

            if (!plugin.getSettingsManager().isUnrivable(tm.getTag()))
            {
                clanCount++;
            }
        }

        double limit = ((double) clanCount) * (((double) plugin.getSettingsManager().getRivalLimitPercent()) / ((double) 100));

        return rivalCount > limit;
    }
}
