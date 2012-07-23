package net.sacredlabyrinth.phaed.simpleclans;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.*;
import net.sacredlabyrinth.phaed.simpleclans.api.events.SimpleClansChunkClaimEvent;
import net.sacredlabyrinth.phaed.simpleclans.api.events.SimpleClansJoinEvent;
import net.sacredlabyrinth.phaed.simpleclans.api.events.SimpleClansLeaveEvent;
import net.sacredlabyrinth.phaed.simpleclans.results.BankResult;
import net.sacredlabyrinth.phaed.simpleclans.results.ClaimResult;
import net.sacredlabyrinth.phaed.simpleclans.results.UnClaimResult;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * @author phaed
 */
public class Clan implements Serializable, Comparable<Clan>
{

    private SimpleClans plugin;
    private static final long serialVersionUID = 1L;
    private boolean verified;
    private String tag;
    private String colorTag;
    private String name;
    private double balance;
    private boolean friendlyFire;
    private long founded;
    private long lastUsed;
    private String capeUrl;
    private Set<String> allies = new HashSet<String>();
    private Set<String> rivals = new HashSet<String>();
    private List<String> bb = new ArrayList<String>();
    private Set<String> members = new HashSet<String>();
    private HashMap<String, Clan> warringClans = new HashMap<String, Clan>();
    private int homeX = 0;
    private int homeY = 0;
    private int homeZ = 0;
    private String homeWorld = "";
    private boolean allowWithdraw = false;
    private boolean allowDeposit = true;
    private Set<ChunkLocation> claimed = new HashSet<ChunkLocation>();
//    private boolean claimedChanged;
    private ChunkLocation homeChunk = null;
    //private Set<PermissionType> permissions = new HashSet(EnumSet.noneOf(PermissionType.class));
    private Set<Byte> permissions = new HashSet<Byte>();

    /**
     *
     */
    public Clan(SimpleClans plugin)
    {
        this.plugin = plugin;
        this.capeUrl = "";
        this.tag = "";
    }

    /**
     * @param tag
     * @param name
     * @param verified
     */
    public Clan(SimpleClans plugin, String tag, String name, boolean verified)
    {
        this.plugin = plugin;
        this.tag = Helper.cleanTag(tag);
        this.colorTag = Helper.parseColors(tag);
        this.name = name;
        this.founded = System.currentTimeMillis();
        this.lastUsed = System.currentTimeMillis();
        this.verified = verified;
        this.capeUrl = "";

        if (plugin.getSettingsManager().isClanFFOnByDefault()) {
            friendlyFire = true;
        }
    }

    @Override
    public int hashCode()
    {
        return getTag().hashCode() >> 13;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Clan)) {
            return false;
        }

        Clan other = (Clan) obj;
        return other.getTag().equals(this.getTag());
    }

    @Override
    public int compareTo(Clan other)
    {
        return this.getTag().compareToIgnoreCase(other.getTag());
    }

    @Override
    public String toString()
    {
        return tag;
    }

    public boolean hasPermission(PermissionType type)
    {
        return permissions.contains(type.getId());
    }

    public void removePermission(PermissionType type)
    {
        permissions.remove(type.getId());
    }

    public void addPermission(PermissionType type)
    {
        permissions.add(type.getId());
    }

    /**
     * Returns false if the permission was removed else true
     *
     * @param type
     * @return
     */
    public boolean toggle(PermissionType type)
    {
        byte id = type.getId();
        if (permissions.contains(id)) {
            permissions.remove(id);
            return false;
        } else {
            permissions.add(id);
            return true;
        }
    }

    public Location getHomeChunkMiddle()
    {
        int cx = homeChunk.getX();
        int cz = homeChunk.getZ();
        int x = cx << 4 + 8;
        int z = cz << 4 + 8;
        World world = homeChunk.getNormalWorld();

        return new Location(world, x, world.getHighestBlockYAt(x, z), z);
    }

    public boolean isClaimed(Location loc)
    {
        return claimed.contains(new ChunkLocation(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockZ(), true));
    }

    public boolean isClaimed(World world, int x, int z)
    {
        return claimed.contains(new ChunkLocation(world.getName(), x, z, true));
    }

    public boolean isClaimed(ChunkLocation chunk)
    {
        return claimed.contains(chunk);
    }

    public boolean canClaim()
    {
        return getClaimedChunks().size() < getAllowedClaims();
    }

    public int getAllowedClaims()
    {
        if (plugin.getSettingsManager().isClanSizeBased()) {
            int clanSize = getSize();
//
//            if (clanSize == 1) {
//                return 0;
//            }

            int claimsseBasedOnMembers = 0;

            for (int i = 0; i < clanSize; i++) {
                claimsseBasedOnMembers += 2;
            }

            return claimsseBasedOnMembers;
        } else if (plugin.getSettingsManager().isPowerBased()) {
            return getPower() * plugin.getSettingsManager().getClaimsPerPower();
        }
        return 0;
    }

    public boolean isClaimedNear(ChunkLocation chunk)
    {
        return isClaimedNear(chunk, null);
    }

    public boolean isClaimedNear(ChunkLocation checkChunk, ChunkLocation excluded)
    {
        String world = checkChunk.getWorld();
        int x = checkChunk.getX();
        int z = checkChunk.getZ();
        Set<ChunkLocation> near = new HashSet<ChunkLocation>();

        near.add(checkChunk);
        near.add(new ChunkLocation(world, x + 1, z, false));
        near.add(new ChunkLocation(world, x, z + 1, false));
        near.add(new ChunkLocation(world, x + 1, z + 1, false));
        near.add(new ChunkLocation(world, x - 1, z, false));
        near.add(new ChunkLocation(world, x, z - 1, false));
        near.add(new ChunkLocation(world, x - 1, z - 1, false));

        for (ChunkLocation chunk : claimed) {
            if (!chunk.equals(excluded)) {
                if (near.contains(chunk)) {
                    return true;
                }
            }
        }

        if (claimed.isEmpty()) {
            return true;
        }

        return false;
    }

    public void setHomeChunk(ChunkLocation chunk)
    {
        homeChunk = chunk;
    }

    public ChunkLocation getHomeChunk()
    {
        return homeChunk;
    }

    public void addClaimedChunk(ChunkLocation chunk)
    {
        plugin.getStorageManager().insertClaim(chunk, this);
        claimed.add(chunk);
    }

    public ClaimResult canClaim(ChunkLocation chunk)
    {
        Clan clanHere = plugin.getClanManager().getClanAt(chunk);

        if (clanHere == null) {
            if (isClaimedNear(chunk)) {
                return ClaimResult.SUCCESS;
            } else {
                return ClaimResult.NO_CLAIM_NEAR;
            }
        } else {
            if (clanHere.equals(this)) {
                return ClaimResult.ALREADY_OWN;
            } else {
                return ClaimResult.ALREADY_OTHER;
            }
        }
    }

    public UnClaimResult unclaim(ChunkLocation chunk)
    {
        Clan clanHere = plugin.getClanManager().getClanAt(chunk);

        if (clanHere != null) {
            if (!chunk.equals(homeChunk)) {
                if (isClaimedNear(chunk, chunk)) {
                    return UnClaimResult.SUCCESS;
                } else {
                    return UnClaimResult.FAILED_NEAR;
                }
            } else {
                return UnClaimResult.FAILED_HOMEBLOCK;
            }
        } else {
            return UnClaimResult.NO_CLAIM;
        }
    }

    public void removeClaimedChunk(ChunkLocation chunk)
    {
        plugin.getStorageManager().deleteClaim(chunk, this);
        claimed.remove(chunk);
    }

    public Set<ChunkLocation> getClaimedChunks()
    {
        return claimed;
    }

    /**
     * deposits money to the clan
     *
     * @param amount
     * @param player
     */
    public BankResult deposit(double amount, Player player)
    {
        if (plugin.getPermissionsManager().playerHasMoney(player, amount)) {
            if (plugin.getPermissionsManager().playerChargeMoney(player, amount)) {
                setBalance(getBalance() + amount);
                plugin.getStorageManager().updateClan(this);
                return BankResult.SUCCESS_DEPOSIT;
            } else {
                return BankResult.PLAYER_NOT_ENOUGH_MONEY;
            }
        } else {
            return BankResult.PLAYER_NOT_ENOUGH_MONEY;
        }
    }

    /**
     * withdraws money from the clan
     *
     * @param amount
     * @param player
     */
    public BankResult withdraw(double amount, Player player)
    {
        if (getBalance() >= amount) {
            if (plugin.getPermissionsManager().playerGrantMoney(player, amount)) {
                setBalance(getBalance() - amount);
                plugin.getStorageManager().updateClan(this);

                return BankResult.SUCCESS_WITHDRAW;
            } else {
                return BankResult.FAILED;
            }

        } else {
            return BankResult.BANK_NOT_ENOUGH_MONEY;
        }
    }

    /**
     * Returns the clan's name
     *
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * (used internally)
     *
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the clan's balance
     *
     * @return the balance
     */
    public double getBalance()
    {
        return balance;
    }

    /**
     * (used internally)
     *
     * @param balance the balance to set
     */
    public void setBalance(double balance)
    {
        this.balance = balance;
    }

    /**
     * Returns the clan's tag clean (no colors)
     *
     * @return the tag
     */
    public String getTag()
    {
        return tag;
    }

    /**
     * (used internally)
     *
     * @param tag the tag to set
     */
    public void setTag(String tag)
    {
        this.tag = tag;
    }

    /**
     * Returns the last used date in milliseconds
     *
     * @return the lastUsed
     */
    public long getLastUsed()
    {
        return lastUsed;
    }

    /**
     * Updates last used date to today (does not update clan on db)
     */
    public void updateLastUsed()
    {
        setLastUsed(System.currentTimeMillis());
    }

    /**
     * Returns the number of days the clan has been inactive
     *
     * @return
     */
    public int getInactiveDays()
    {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return (int) Math.floor(Dates.differenceInDays(new Timestamp(getLastUsed()), now));
    }

    /**
     * (used internally)
     *
     * @param lastUsed the lastUsed to set
     */
    public void setLastUsed(long lastUsed)
    {
        this.lastUsed = lastUsed;
    }

    /**
     * Check whether this clan allows friendly fire
     *
     * @return the friendlyFire
     */
    public boolean isFriendlyFire()
    {
        return friendlyFire;
    }

    /**
     * Sets the friendly fire status of this clan (does not update clan on db)
     *
     * @param friendlyFire the friendlyFire to set
     */
    public void setFriendlyFire(boolean friendlyFire)
    {
        this.friendlyFire = friendlyFire;
    }

    /**
     * Check if the player is a member of this clan
     *
     * @param player
     * @return confirmation
     */
    public boolean isMember(Player player)
    {
        return this.members.contains(player.getName().toLowerCase());
    }

    /**
     * Check if the player is a member of this clan
     *
     * @param playerName
     * @return confirmation
     */
    public boolean isMember(String playerName)
    {
        return this.members.contains(playerName.toLowerCase());
    }

    /**
     * Returns a list with the contents of the bulletin board
     *
     * @return the bb
     */
    public List<String> getBb()
    {
        return Collections.unmodifiableList(bb);
    }

    /**
     * Return a list of all the allies' tags clean (no colors)
     *
     * @return the allies
     */
    public Set<String> getAllies()
    {
        return Collections.unmodifiableSet(allies);
    }

    private void addAlly(String tag)
    {
        allies.add(tag);
    }

    private boolean removeAlly(String ally)
    {
        if (!allies.contains(ally)) {
            return false;
        }

        allies.remove(ally);
        return true;
    }

    /**
     * The founded date in milliseconds
     *
     * @return the founded
     */
    public long getFounded()
    {
        return founded;
    }

    /**
     * The string representation of the founded date
     *
     * @return
     */
    public String getFoundedString()
    {
        return new java.text.SimpleDateFormat("MMM dd, ''yy h:mm a").format(new Date(this.founded));
    }

    /**
     * (used internally)
     *
     * @param founded the founded to set
     */
    public void setFounded(long founded)
    {
        this.founded = founded;
    }

    /**
     * Returns the color tag for this clan
     *
     * @return the colorTag
     */
    public String getColorTag()
    {
        return colorTag;
    }

    /**
     * (used internally)
     *
     * @param colorTag the colorTag to set
     */
    public void setColorTag(String colorTag)
    {
        this.colorTag = Helper.parseColors(colorTag);
    }

    /**
     * Adds a bulletin board message without announcer
     *
     * @param msg
     */
    public void addBb(String msg)
    {
        while (bb.size() > plugin.getSettingsManager().getBbSize()) {
            bb.remove(0);
        }

        bb.add(msg);
        plugin.getStorageManager().updateClan(this);
    }

    /**
     * Clears the bulletin board
     *
     * @param msg
     */
    public void clearBb()
    {
        bb.clear();
        plugin.getStorageManager().updateClan(this);
    }

    /**
     * (used internally)
     *
     * @param cp
     */
    public void importMember(ClanPlayer cp)
    {
        if (!this.members.contains(cp.getCleanName())) {
            this.members.add(cp.getCleanName());
        }
    }

    /**
     * (used internally)
     *
     * @param playerName
     */
    public void removeMember(String playerName)
    {
        this.members.remove(playerName.toLowerCase());
    }

    /**
     * Get total clan size
     *
     * @return
     */
    public int getSize()
    {
        return this.members.size();
    }

    /**
     * Returns a list of all rival tags clean (no colors)
     *
     * @return the rivals
     */
    public Set<String> getRivals()
    {
        return Collections.unmodifiableSet(rivals);
    }

    private void addRival(String tag)
    {
        rivals.add(tag);
    }

    private boolean removeRival(String rival)
    {
        if (!rivals.contains(rival)) {
            return false;
        }

        rivals.remove(rival);
        return true;
    }

    /**
     * Check if the tag is a rival
     *
     * @param tag
     * @return
     */
    public boolean isRival(String tag)
    {
        return rivals.contains(tag);
    }

    /**
     * Check if the tag is an ally
     *
     * @param tag
     * @return
     */
    public boolean isAlly(String tag)
    {
        return allies.contains(tag);
    }

    /**
     * Check if the clanplayer is an ally
     *
     * @param tag
     * @return
     */
    public boolean isAlly(ClanPlayer cp)
    {

        if (cp == null) {
            return false;
        }

        Clan clan = cp.getClan();

        if (clan == null) {
            return false;
        }

        return allies.contains(clan.getTag());
    }

    /**
     * Tells you if the clan is verified, always returns true if no verification
     * is required
     *
     * @return
     */
    public boolean isVerified()
    {
        return !plugin.getSettingsManager().isRequireVerification() || verified;

    }

    /**
     * Returns the power of this clan
     *
     * @return
     */
    public int getPower()
    {
        int out = 0;

        for (ClanPlayer cp : getMembers()) {
            out += cp.getPower();
        }

        return out;
    }

    /**
     * (used internally)
     *
     * @param verified the verified to set
     */
    public void setVerified(boolean verified)
    {
        this.verified = verified;
    }

    /**
     * Returns the cape url for this clan
     *
     * @return the capeUrl
     */
    public String getCapeUrl()
    {
        return capeUrl;
    }

    /**
     * (used internally)
     *
     * @param capeUrl the capeUrl to set
     */
    public void setCapeUrl(String capeUrl)
    {
        this.capeUrl = capeUrl;
    }

    /**
     * (used internally)
     *
     * @return the packedBb
     */
    public String getPackedBb()
    {
        return Helper.toMessage(bb, "|");
    }

    /**
     * (used internally)
     *
     * @param packedBb the packedBb to set
     */
    public void setPackedBb(String packedBb)
    {
        this.bb = new ArrayList<String>(Helper.fromArray(packedBb.split("[|]")));
    }

    /**
     * (used internally)
     *
     * @return the packedAllies
     */
    public String getPackedAllies()
    {
        return Helper.toMessage(allies, "|");
    }

    /**
     * (used internally)
     *
     * @param packedAllies the packedAllies to set
     */
    public void setPackedAllies(String packedAllies)
    {
        this.allies = new HashSet<String>(Helper.fromArray(packedAllies.split("[|]")));
    }

    /**
     * (used internally)
     *
     * @return the packedRivals
     */
    public String getPackedRivals()
    {
        return Helper.toMessage(rivals, "|");
    }

    /**
     * (used internally)
     *
     * @param packedRivals the packedRivals to set
     */
    public void setPackedRivals(String packedRivals)
    {
        this.rivals = new HashSet<String>(Helper.fromArray(packedRivals.split("[|]")));
    }

    /**
     * Returns a separator delimited string with all the ally clan's colored
     * tags
     *
     * @param sep
     * @return
     */
    public String getAllyString(String sep)
    {
        String out = "";

        for (String allyTag : getAllies()) {
            Clan ally = plugin.getClanManager().getClan(allyTag);

            if (ally != null) {
                out += ally.getColorTag() + sep;
            }
        }

        out = Helper.stripTrailing(out, sep);

        if (out.trim().isEmpty()) {
            return ChatColor.BLACK + "None";
        }

        return Helper.parseColors(out);
    }

    /**
     * Returns a separator delimited string with all the rival clan's colored
     * tags
     *
     * @param sep
     * @return
     */
    public String getRivalString(String sep)
    {
        String out = "";

        for (String rivalTag : getRivals()) {
            Clan rival = plugin.getClanManager().getClan(rivalTag);

            if (rival != null) {
                if (isWarring(rivalTag)) {
                    out += ChatColor.DARK_RED + "[" + Helper.stripColors(rival.getColorTag()) + "]" + sep;
                } else {
                    out += rival.getColorTag() + sep;
                }

            }
        }

        out = Helper.stripTrailing(out, sep);

        if (out.trim().isEmpty()) {
            return ChatColor.BLACK + "None";
        }

        return Helper.parseColors(out);
    }

    /**
     * Returns a separator delimited string with all the leaders
     *
     * @param prefix
     * @param sep
     * @return the formatted leaders string
     */
    public String getLeadersString(String prefix, String sep)
    {
        String out = "";

        for (String member : members) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(member.toLowerCase());

            if (cp.isLeader()) {
                out += prefix + cp.getName() + sep;
            }
        }

        return Helper.stripTrailing(out, sep);
    }

    /**
     * Check if a player is a leader of a clan
     *
     * @param player
     * @return the leaders
     */
    public boolean isLeader(Player player)
    {
        return isLeader(player.getName());
    }

    /**
     * Check if a player is a leader of a clan
     *
     * @param playerName
     * @return the leaders
     */
    public boolean isLeader(String playerName)
    {
        if (isMember(playerName)) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(playerName.toLowerCase());

            if (cp.isLeader()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get all members (leaders, and non-leaders) in the clan
     *
     * @return the members
     */
    public List<ClanPlayer> getMembers()
    {
        List<ClanPlayer> out = new ArrayList<ClanPlayer>();

        for (String member : members) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(member.toLowerCase());
            out.add(cp);
        }

        return out;
    }

    /**
     * Get all online members (leaders, and non-leaders) in the clan
     *
     * @return the members
     */
    public List<ClanPlayer> getOnlineMembers()
    {
        List<ClanPlayer> out = new ArrayList<ClanPlayer>();

        for (String member : members) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(member.toLowerCase());
            if (cp.toPlayer() != null) {
                out.add(cp);
            }
        }

        return out;
    }

    /**
     * Get all leaders in the clan
     *
     * @return the leaders
     */
    public List<ClanPlayer> getLeaders()
    {
        List<ClanPlayer> out = new ArrayList<ClanPlayer>();

        for (String member : members) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(member.toLowerCase());

            if (cp != null && cp.isLeader()) {
                out.add(cp);
            }
        }

        return out;
    }

    /**
     * Get all non-leader players in the clan
     *
     * @return non leaders
     */
    public List<ClanPlayer> getNonLeaders()
    {
        List<ClanPlayer> out = new ArrayList<ClanPlayer>();

        for (String member : members) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(member.toLowerCase());

            if (!cp.isLeader()) {
                out.add(cp);
            }
        }

        Collections.sort(out);

        return out;
    }

    /**
     * Get all clan's members
     *
     * @return
     */
    public List<ClanPlayer> getAllMembers()
    {
        List<ClanPlayer> out = new ArrayList<ClanPlayer>();

        for (String member : members) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(member.toLowerCase());
            out.add(cp);
        }

        Collections.sort(out);

        return out;
    }

    /**
     * Get all the ally clan's members
     *
     * @return
     */
    public Set<ClanPlayer> getAllAllyMembers()
    {
        Set<ClanPlayer> out = new HashSet<ClanPlayer>();

        for (String tag1 : allies) {
            Clan ally = plugin.getClanManager().getClan(tag1);

            if (ally != null) {
                out.addAll(ally.getMembers());
            }
        }

        return out;
    }

    /**
     * Gets the clan's total KDR
     *
     * @return
     */
    public float getTotalKDR()
    {
        if (members.isEmpty()) {
            return 0;
        }

        double totalWeightedKills = 0;
        int totalDeaths = 0;

        for (String member : members) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(member.toLowerCase());
            totalWeightedKills += cp.getWeightedKills();
            totalDeaths += cp.getDeaths();
        }

        if (totalDeaths == 0) {
            totalDeaths = 1;
        }

        return ((float) totalWeightedKills) / ((float) totalDeaths);
    }

    /**
     * Gets the clan's total KDR
     *
     * @return
     */
    public int getTotalDeaths()
    {
        int totalDeaths = 0;

        if (members.isEmpty()) {
            return totalDeaths;
        }

        for (String member : members) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(member.toLowerCase());
            totalDeaths += cp.getDeaths();
        }

        return totalDeaths;
    }

    /**
     * Gets average weighted kills for the clan
     *
     * @return
     */
    public int getAverageWK()
    {
        int total = 0;

        if (members.isEmpty()) {
            return total;
        }

        for (String member : members) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(member.toLowerCase());
            total += cp.getWeightedKills();
        }

        return total / getSize();
    }

    /**
     * Gets total rival kills for the clan
     *
     * @return
     */
    public int getTotalRival()
    {
        int total = 0;

        if (members.isEmpty()) {
            return total;
        }

        for (String member : members) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(member.toLowerCase());
            total += cp.getRivalKills();
        }

        return total;
    }

    /**
     * Gets total neutral kills for the clan
     *
     * @return
     */
    public int getTotalNeutral()
    {
        int total = 0;

        if (members.isEmpty()) {
            return total;
        }

        for (String member : members) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(member.toLowerCase());
            total += cp.getNeutralKills();
        }

        return total;
    }

    /**
     * Gets total civilian kills for the clan
     *
     * @return
     */
    public int getTotalCivilian()
    {
        int total = 0;

        if (members.isEmpty()) {
            return total;
        }

        for (String member : members) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(member.toLowerCase());
            total += cp.getCivilianKills();
        }

        return total;
    }

    /**
     * Set a clan's cape url
     *
     * @param url
     */
    public void setClanCape(String url)
    {
        setCapeUrl(url);

        plugin.getStorageManager().updateClan(this);

        for (String member : members) {
            plugin.getSpoutPluginManager().processPlayer(member);
        }
    }

    /**
     * Check whether the clan has crossed the rival limit
     *
     * @return
     */
    public boolean reachedRivalLimit()
    {
        int rivalCount = rivals.size();
        int clanCount = plugin.getClanManager().getRivableClanCount() - 1;
        int rivalPercent = plugin.getSettingsManager().getRivalLimitPercent();

        double limit = ((double) clanCount) * (((double) rivalPercent) / ((double) 100));

        return rivalCount > limit;
    }

    /**
     * Add a new player to the clan
     *
     * @param cp
     */
    public void addPlayerToClan(ClanPlayer cp)
    {
        cp.removePastClan(getColorTag());
        cp.setClan(this);
        cp.setLeader(false);
        cp.setTrusted(plugin.getSettingsManager().isClanTrustByDefault());

        importMember(cp);

        plugin.getStorageManager().updateClanPlayer(cp);
        plugin.getStorageManager().updateClan(this);

        // add clan permission
//        plugin.getPermissionsManager().addClanPermissions(cp);

        plugin.getPermissionsManager().setupPlayerPermissions(cp);

        plugin.getServer().getPluginManager().callEvent(new SimpleClansJoinEvent(cp, this));

        if (plugin.hasSpout()) {
            plugin.getSpoutPluginManager().processPlayer(cp.getName());
            plugin.getSpoutPluginManager().setupClaimView(cp);
        }

        Player player = Helper.matchOnePlayer(cp.getName());

        if (player != null) {
            plugin.getClanManager().updateDisplayName(player);
        }
    }

    /**
     * Remove a player from a clan
     *
     * @param playerName
     */
    public void removePlayerFromClan(String playerName)
    {
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(playerName);

        // remove clan group-permission
        //plugin.getPermissionsManager().removeClanPermissions(cp);

        // remove permissions
        plugin.getPermissionsManager().removeClanPlayerPermissions(cp);

        cp.setClan(null);
        cp.addPastClan(getColorTag() + (cp.isLeader() ? ChatColor.DARK_RED + "*" : ""));
        cp.setLeader(false);
        cp.setTrusted(false);
        cp.setJoinDate(0);
        removeMember(playerName);

        if (plugin.hasSpout()) {
            plugin.getSpoutPluginManager().removeClaimView(cp.toPlayer());
            plugin.getSpoutPluginManager().processPlayer(cp.getName());
        }

        plugin.getStorageManager().updateClanPlayer(cp);
        plugin.getStorageManager().updateClan(this);

        plugin.getServer().getPluginManager().callEvent(new SimpleClansLeaveEvent(cp, this));

        Player matched = Helper.matchOnePlayer(playerName);

        if (matched != null) {
            plugin.getClanManager().updateDisplayName(matched);
        }
    }

    /**
     * Promote a member to a leader of a clan
     *
     * @param playerName
     */
    public void promote(String playerName)
    {
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(playerName);

        cp.setLeader(true);
        cp.setTrusted(true);

        plugin.getStorageManager().updateClanPlayer(cp);
        plugin.getStorageManager().updateClan(this);
        plugin.getSpoutPluginManager().processPlayer(cp.getName());

        plugin.getPermissionsManager().updatePlayerPermissions(cp);
        // add clan permission
//        plugin.getPermissionsManager().addClanPermissions(cp);
    }

    /**
     * Demote a leader back to a member of a clan
     *
     * @param playerName
     */
    public void demote(String playerName)
    {
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(playerName);

        cp.setLeader(false);

        plugin.getStorageManager().updateClanPlayer(cp);
        plugin.getStorageManager().updateClan(this);
        plugin.getSpoutPluginManager().processPlayer(cp.getName());

        plugin.getPermissionsManager().updatePlayerPermissions(cp);

        // add clan permission
//        plugin.getPermissionsManager().addClanPermissions(cp);
    }

    /**
     * Add an ally to a clan, and the clan to the ally
     *
     * @param ally
     */
    public void addAlly(Clan ally)
    {
        removeRival(ally.getTag());
        addAlly(ally.getTag());

        ally.removeRival(getTag());
        ally.addAlly(getTag());

        plugin.getStorageManager().updateClan(this);
        plugin.getStorageManager().updateClan(ally);
    }

    /**
     * Remove an ally form the clan, and the clan from the ally
     *
     * @param ally
     */
    public void removeAlly(Clan ally)
    {
        removeAlly(ally.getTag());
        ally.removeAlly(getTag());

        plugin.getStorageManager().updateClan(this);
        plugin.getStorageManager().updateClan(ally);
    }

    /**
     * Add a rival to the clan, and the clan to the rival
     *
     * @param rival
     */
    public void addRival(Clan rival)
    {
        removeAlly(rival.getTag());
        addRival(rival.getTag());

        rival.removeAlly(getTag());
        rival.addRival(getTag());

        plugin.getStorageManager().updateClan(this);
        plugin.getStorageManager().updateClan(rival);
    }

    /**
     * Removes a rival from the clan, the clan from the rival
     *
     * @param rival
     */
    public void removeRival(Clan rival)
    {
        removeRival(rival.getTag());
        rival.removeRival(getTag());

        plugin.getStorageManager().updateClan(this);
        plugin.getStorageManager().updateClan(rival);
    }

    /**
     * Verify a clan
     */
    public void verifyClan()
    {
        setVerified(true);
        plugin.getStorageManager().updateClan(this);
    }

    /**
     * Check whether any clan member is online
     *
     * @return
     */
    public boolean isAnyOnline()
    {
        for (String member : members) {
            if (Helper.isOnline(member)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check whether all leaders of a clan are online
     *
     * @return
     */
    public boolean allLeadersOnline()
    {
        List<ClanPlayer> leaders = getLeaders();

        for (ClanPlayer leader : leaders) {
            if (!Helper.isOnline(leader.getName())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check whether all leaders, except for the one passed in, are online
     *
     * @param playerName
     * @return
     */
    public boolean allOtherLeadersOnline(String playerName)
    {
        List<ClanPlayer> leaders = getLeaders();

        for (ClanPlayer leader : leaders) {
            if (leader.getName().equalsIgnoreCase(playerName)) {
                continue;
            }

            if (!Helper.isOnline(leader.getName())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Change a clan's tag
     *
     * @param tag
     */
    public void changeClanTag(String tag)
    {
        setColorTag(tag);
        plugin.getStorageManager().updateClan(this);
    }

    /**
     * Announce message to a whole clan
     *
     * @param playerName
     * @param msg
     */
    public void clanAnnounce(String playerName, String msg)
    {
        String message = plugin.getSettingsManager().getClanChatAnnouncementColor() + msg;

        for (ClanPlayer cp : getMembers()) {
            Player pl = plugin.getServer().getPlayer(cp.getName());

            if (pl != null) {
                ChatBlock.sendMessage(pl, message);
            }
        }

        SimpleClans.log(ChatColor.AQUA + "[" + plugin.getLang("clan.announce") + ChatColor.AQUA + "] " + ChatColor.AQUA + "[" + Helper.getColorName(playerName) + ChatColor.WHITE + "] " + message);
    }

    /**
     * Announce message to a all the leaders of a clan
     *
     * @param msg
     */
    public void leaderAnnounce(String msg)
    {
        String message = plugin.getSettingsManager().getClanChatAnnouncementColor() + msg;

        List<ClanPlayer> leaders = getLeaders();

        for (ClanPlayer cp : leaders) {
            Player pl = plugin.getServer().getPlayer(cp.getName());

            if (pl != null) {
                ChatBlock.sendMessage(pl, message);
            }
        }
        SimpleClans.log(ChatColor.AQUA + "[" + plugin.getLang("leader.announce") + ChatColor.AQUA + "] " + ChatColor.WHITE + message);
    }

    /**
     * Announce message to a whole clan plus audio alert
     *
     * @param playerName
     * @param msg
     */
    public void audioAnnounce(String playerName, String msg)
    {
        clanAnnounce(playerName, msg);

        for (String member : members) {
            Player pl = plugin.getServer().getPlayer(member);

            if (pl != null) {
                plugin.getSpoutPluginManager().playAlert(pl);
            }
        }
    }

    /**
     * Add a new bb message and announce it to all online members of a clan
     *
     * @param announcerName
     * @param msg
     */
    public void addBb(String announcerName, String msg)
    {
        if (isVerified()) {
            addBb(plugin.getSettingsManager().getBbColor() + msg);
            clanAnnounce(announcerName, plugin.getSettingsManager().getBbAccentColor() + "* " + plugin.getSettingsManager().getBbColor() + Helper.parseColors(msg));
        }
    }

    /**
     * Displays bb to a player
     *
     * @param player
     */
    public void displayBb(Player player)
    {
        if (isVerified()) {
            ChatBlock.sendBlank(player);
            ChatBlock.saySingle(player, MessageFormat.format(plugin.getLang("bulletin.board.header"), plugin.getSettingsManager().getBbAccentColor(), plugin.getSettingsManager().getPageHeadingsColor(), Helper.capitalize(getName())));

            int maxSize = plugin.getSettingsManager().getBbSize();

            while (bb.size() > maxSize) {
                bb.remove(0);
            }

            for (String msg : bb) {
                ChatBlock.sendMessage(player, plugin.getSettingsManager().getBbAccentColor() + "* " + plugin.getSettingsManager().getBbColor() + Helper.parseColors(msg));
            }
            ChatBlock.sendBlank(player);
        }
    }

    /**
     * Disband a clan
     */
    public void disband()
    {
        Collection<ClanPlayer> clanPlayers = plugin.getClanManager().getAllClanPlayers();
        List<Clan> clans = plugin.getClanManager().getClans();

        for (ClanPlayer cp : clanPlayers) {
            if (cp.getTag().equals(getTag())) {
                plugin.getPermissionsManager().removeClanPermissions(this);
                cp.setClan(null);


                if (isVerified()) {
                    cp.addPastClan(getColorTag() + (cp.isLeader() ? ChatColor.DARK_RED + "*" : ""));
                }

                cp.setLeader(false);

                plugin.getStorageManager().updateClanPlayer(cp);
                if (plugin.hasSpout()) {
                    plugin.getSpoutPluginManager().processPlayer(cp.getName());
                }
            }
        }

        clans.remove(this);

        for (Clan c : clans) {
            String disbanded = plugin.getLang("clan.disbanded");

            if (c.removeWarringClan(this)) {
                c.addBb(disbanded, ChatColor.AQUA + MessageFormat.format(plugin.getLang("you.are.no.longer.at.war"), Helper.capitalize(c.getName()), getColorTag()));
            }

            if (c.removeRival(getTag())) {
                c.addBb(disbanded, ChatColor.AQUA + MessageFormat.format(plugin.getLang("has.been.disbanded.rivalry.ended"), Helper.capitalize(getName())));
            }

            if (c.removeAlly(getTag())) {
                c.addBb(disbanded, ChatColor.AQUA + MessageFormat.format(plugin.getLang("has.been.disbanded.alliance.ended"), Helper.capitalize(getName())));
            }
        }

        final Clan thisOne = this;

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {

            @Override
            public void run()
            {
                plugin.getClanManager().removeClan(thisOne.getTag());
                plugin.getStorageManager().deleteClan(thisOne);
            }
        }, 1);
    }

    /**
     * Whether this clan can be rivaled
     *
     * @return
     */
    public boolean isUnrivable()
    {
        return plugin.getSettingsManager().isUnrivable(getTag());
    }

    /**
     * Returns whether this clan is warring with another clan
     *
     * @param tag the tag of the clan we are at war with
     * @return
     */
    public boolean isWarring(String tag)
    {
        return warringClans.containsKey(tag);
    }

    /**
     * Returns whether this clan is warring with another clan
     *
     * @param clan the clan we are testing against
     * @return
     */
    public boolean isWarring(Clan clan)
    {
        return warringClans.containsKey(clan.getTag());
    }

    /**
     * Add a clan to be at war with
     *
     * @param clan
     */
    public void addWarringClan(Clan clan)
    {
        if (!warringClans.containsKey(clan.getTag())) {
            warringClans.put(clan.getTag(), clan);
        }
        plugin.getStorageManager().updateClan(this);
    }

    /**
     * Remove a warring clan
     *
     * @param clan
     * @return
     */
    public boolean removeWarringClan(Clan clan)
    {
        Clan warring = warringClans.remove(clan.getTag());

        if (warring != null) {
            plugin.getStorageManager().updateClan(this);
            return true;
        }

        return false;
    }

    public int getClaimCount()
    {
        return claimed.size();
    }

    /**
     * Return a collection of all the warring clans
     *
     * @return the clan list
     */
    public List<Clan> getWarringClans()
    {
        return new ArrayList<Clan>(warringClans.values());
    }

    /**
     * Return the list of flags and their data as a json string
     *
     * @return the flags
     */
    public String getFlags()
    {
        JSONObject json = new JSONObject();

        // writing the list of flags to json

        JSONArray warring = new JSONArray();
        warring.addAll(warringClans.keySet());

        JSONArray perms = new JSONArray();
        perms.addAll(permissions);

        json.put("warring", warring);
        json.put("homeX", homeX);
        json.put("homeY", homeY);
        json.put("homeZ", homeZ);
        json.put("homeWorld", homeWorld == null ? "" : homeWorld);
        if (homeChunk != null) {
            json.put("homeChunkX", homeChunk.getX());
            json.put("homeChunkZ", homeChunk.getZ());
            json.put("homeChunkWorld", homeChunk.getWorld());
        }
        if (allowWithdraw) {
            json.put("withdraw", 1);
        }
        if (!allowDeposit) {
            json.put("deposit", 0);
        }
        json.put("perms", perms);
        return json.toString();
    }

    public void setClaimedChunks(Set<ChunkLocation> claimed)
    {
        this.claimed = claimed;
    }

    /**
     * Read the list of flags in from a json string
     *
     * @param flagString the flags to set
     */
    public void setFlags(String flagString)
    {
        if (flagString != null && !flagString.isEmpty()) {
            JSONObject flags = (JSONObject) JSONValue.parse(flagString);

            if (flags != null) {
                for (Object flag : flags.keySet()) {
                    // reading the list of flags from json

                    try {
                        if (flag.equals("warring")) {
                            JSONArray clans = (JSONArray) flags.get(flag);

                            if (clans != null) {
                                for (Object ctag : clans) {
                                    SimpleClans.debug("warring added: " + ctag.toString());
                                    warringClans.put(ctag.toString(), null);
                                }
                            }
                        }

                        if (flag.equals("perms")) {
                            JSONArray perms = (JSONArray) flags.get(flag);

                            if (perms != null) {
                                for (Object perm : perms) {
                                    permissions.add(Byte.valueOf(perm.toString()));
                                }
                            }
                        }

                        if (flag.equals("deposit")) {
                            allowDeposit = ((Long) flags.get(flag)).intValue() == 0 ? false : true;
                        }

                        if (flag.equals("withdraw")) {
                            allowWithdraw = ((Long) flags.get(flag)).intValue() == 0 ? false : true;
                        }

                        if (flag.equals("homeX")) {
                            homeX = ((Long) flags.get(flag)).intValue();
                        }

                        if (flag.equals("homeY")) {
                            homeY = ((Long) flags.get(flag)).intValue();
                        }

                        if (flag != null && flag.equals("homeZ")) {
                            homeZ = ((Long) flags.get(flag)).intValue();
                        }

                        if (flag.equals("homeWorld")) {
                            homeWorld = (String) flags.get(flag);
                        }

                        if (flag.equals("homeChunkX")) {
                            if (homeChunk == null) {
                                homeChunk = new ChunkLocation();
                            }
                            homeChunk.setX(((Long) flags.get(flag)).intValue());
                        }

                        if (flag.equals("homeChunkZ")) {
                            if (homeChunk == null) {
                                homeChunk = new ChunkLocation();
                            }
                            homeChunk.setZ(((Long) flags.get(flag)).intValue());
                        }

                        if (flag.equals("homeChunkWorld")) {
                            if (homeChunk == null) {
                                homeChunk = new ChunkLocation();
                            }
                            homeChunk.setWorld((String) flags.get(flag));
                        }

                    } catch (Exception ex) {
                        for (StackTraceElement el : ex.getStackTrace()) {
                            SimpleClans.debug(String.format("Failed reading flag: %s", flag));
                            SimpleClans.debug(String.format("Value: %s", flags.get(flag)));
                            SimpleClans.debug(el.toString());
                        }
                    }
                }
            }
        }
    }

    public void validateWarring()
    {
        for (Iterator iter = warringClans.keySet().iterator(); iter.hasNext();) {
            String clanName = (String) iter.next();

            Clan clan = plugin.getClanManager().getClan(clanName);

            if (clan == null) {
                iter.remove();
            } else {
                SimpleClans.debug("validated: " + clanName);
                warringClans.put(clanName, clan);
            }
        }
    }

    public void setHomeLocation(Location home)
    {
        Location above = home;
        above.setY(home.getBlockY() + 1);

        homeX = home.getBlockX();
        homeY = home.getBlockY();
        homeZ = home.getBlockZ();
        homeWorld = home.getWorld().getName();

        plugin.getStorageManager().updateClan(this);
    }

    public Location getHomeLocation()
    {
        World world = plugin.getServer().getWorld(homeWorld);

        if (world != null) {
            if (world.getBlockAt(homeX, homeY, homeZ).getTypeId() != 0 || world.getBlockAt(homeX, homeY + 1, homeZ).getTypeId() != 0 || homeY == 0) {
                return new Location(world, homeX, world.getHighestBlockYAt(homeX, homeZ), homeZ);
            } else {
                return new Location(world, homeX, homeY, homeZ);
            }
        }

        return null;
    }

    public String getTagLabel()
    {
        return plugin.getSettingsManager().getTagBracketColor() + plugin.getSettingsManager().getTagBracketLeft() + plugin.getSettingsManager().getTagDefaultColor() + getColorTag() + plugin.getSettingsManager().getTagBracketColor() + plugin.getSettingsManager().getTagBracketRight() + plugin.getSettingsManager().getTagSeparatorColor() + plugin.getSettingsManager().getTagSeparator();
    }

    /**
     * @return the allowWithdraw
     */
    public boolean isAllowWithdraw()
    {
        return allowWithdraw;
    }

    /**
     * @param allowWithdraw the allowWithdraw to set
     */
    public void setAllowWithdraw(boolean allowWithdraw)
    {
        this.allowWithdraw = allowWithdraw;
    }

    /**
     * @return the allowDeposit
     */
    public boolean isAllowDeposit()
    {
        return allowDeposit;
    }

    /**
     * @param allowDeposit the allowDeposit to set
     */
    public void setAllowDeposit(boolean allowDeposit)
    {
        this.allowDeposit = allowDeposit;
    }
}
