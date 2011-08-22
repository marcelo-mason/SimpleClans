package net.sacredlabyrinth.phaed.simpleclans;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public class Clan implements Serializable, Comparable<Clan>
{
    private boolean verified;
    private String tag;
    private String colorTag;
    private String name;
    private boolean friendlyFire;
    private long founded;
    private long lastUsed;
    private String capeUrl;
    private List<String> allies = new ArrayList<String>();
    private List<String> rivals = new ArrayList<String>();
    private List<String> bb = new ArrayList<String>();
    private List<String> members = new ArrayList<String>();

    /**
     *
     */
    public Clan()
    {
        this.capeUrl = "";
    }

    /**
     *
     * @param cp
     * @param tag
     * @param name
     * @param verified
     */
    public Clan(ClanPlayer cp, String tag, String name, boolean verified)
    {
        this.tag = Helper.cleanTag(tag);
        this.colorTag = tag;
        this.name = name;
        this.founded = (new Date()).getTime();
        this.lastUsed = (new Date()).getTime();
        this.verified = verified;
        this.capeUrl = "";

        cp.setTag(Helper.cleanTag(tag));
        cp.setLeader(true);
        this.members.add(cp.getCleanName());
    }

    @Override
    public int hashCode()
    {
        return getTag().hashCode() >> 13;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Clan))
        {
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

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the tag
     */
    public String getTag()
    {
        return tag;
    }

    /**
     * @param tag the tag to set
     */
    public void setTag(String tag)
    {
        this.tag = tag;
    }

    /**
     * @return the lastUsed
     */
    public long getLastUsed()
    {
        return lastUsed;
    }

    /**
     * Updates last used date
     */
    public void updateLastUsed()
    {
        setLastUsed((new Date()).getTime());
    }

    /**
     * @return number of days the clan has been inactive
     */
    public int getInactiveDays()
    {
        Timestamp now = new Timestamp((new Date()).getTime());
        return (int) Math.floor(Dates.differenceInDays(new Timestamp(getLastUsed()), now));
    }

    /**
     * @param lastUsed the lastUsed to set
     */
    public void setLastUsed(long lastUsed)
    {
        this.lastUsed = lastUsed;
    }

    /**
     * @return the friendlyFire
     */
    public boolean isFriendlyFire()
    {
        return friendlyFire;
    }

    /**
     * @param friendlyFire the friendlyFire to set
     */
    public void setFriendlyFire(boolean friendlyFire)
    {
        this.friendlyFire = friendlyFire;
    }

    /**
     * Check if the player is a member of this clan
     * @param player
     * @return confirmation
     */
    public boolean isMember(Player player)
    {
        return this.members.contains(player.getName().toLowerCase());
    }

    /**
     * Check if the player is a member of this clan
     * @param playerName
     * @return confirmation
     */
    public boolean isMember(String playerName)
    {
        return this.members.contains(playerName.toLowerCase());
    }

    /**
     * @return the bb
     */
    public List<String> getBb()
    {
        return bb;
    }

    /**
     * @return the allies
     */
    public List<String> getAllies()
    {
        return allies;
    }

    /**
     * @param allies the allies to set
     */
    public void setAllies(List<String> allies)
    {
        this.allies = allies;
    }

    /**
     *
     * @param tag
     */
    public void addAlly(String tag)
    {
        allies.add(tag);
    }

    /**
     *
     * @param ally
     * @return
     */
    public boolean removeAlly(String ally)
    {
        if (!allies.contains(ally))
        {
            return false;
        }

        allies.remove(ally);
        return true;
    }

    /**
     * @param bb the bb to set
     */
    public void setBb(List<String> bb)
    {
        this.bb = bb;
    }

    /**
     * @return the founded
     */
    public long getFounded()
    {
        return founded;
    }

    /**
     * @return the string representation of the founded date
     */
    public String getFoundedString()
    {
        return new java.text.SimpleDateFormat("MMM dd, ''yy h:mm a").format(new Date(this.founded));
    }

    /**
     * @param founded the founded to set
     */
    public void setFounded(long founded)
    {
        this.founded = founded;
    }

    /**
     * @return the colorTag
     */
    public String getColorTag()
    {
        return colorTag;
    }

    /**
     * @param colorTag the colorTag to set
     */
    public void setColorTag(String colorTag)
    {
        this.colorTag = colorTag;
    }

    /**
     *
     * @param msg
     */
    public void addBb(String msg)
    {
        bb.add(msg);
    }

    /**
     *
     * @param cp
     */
    public void addMember(ClanPlayer cp)
    {
        if (!this.members.contains(cp.getCleanName()))
        {
            this.members.add(cp.getCleanName());
        }
    }

    /**
     *
     * @param player
     */
    public void removeMember(Player player)
    {
        this.members.remove(player.getName().toLowerCase());
    }

    /**
     *
     * @param playerName
     */
    public void removeMember(String playerName)
    {
        this.members.remove(playerName.toLowerCase());
    }

    /**
     * Get total clan size
     * @return
     */
    public int getSize()
    {
        return this.members.size();
    }

    /**
     * @return the rivals
     */
    public List<String> getRivals()
    {
        return rivals;
    }

    /**
     *
     * @param tag
     */
    public void addRival(String tag)
    {
        rivals.add(tag);
    }

    /**
     *
     * @param rival
     * @return
     */
    public boolean removeRival(String rival)
    {
        if (!rivals.contains(rival))
        {
            return false;
        }

        rivals.remove(rival);
        return true;
    }

    /**
     * @param rivals the rivals to set
     */
    public void setRivals(List<String> rivals)
    {
        this.rivals = rivals;
    }

    /**
     * Check if the tag is a rival
     * @param tag
     * @return
     */
    public boolean isRival(String tag)
    {
        return rivals.contains(tag);
    }

    /**
     * Check if the tag is an ally
     * @param tag
     * @return
     */
    public boolean isAlly(String tag)
    {
        return allies.contains(tag);
    }

    /**
     * @return the members
     */
    public List<String> getMembers()
    {
        return this.members;
    }

    /**
     * @param members the members to set
     */
    public void setMembers(List<String> members)
    {
        this.members = members;
    }

    /**
     * @return the verified
     */
    public boolean isVerified()
    {
        return verified;
    }

    /**
     * @param verified the verified to set
     */
    public void setVerified(boolean verified)
    {
        this.verified = verified;
    }

    /**
     * @return the capeUrl
     */
    public String getCapeUrl()
    {
        return capeUrl;
    }

    /**
     * @param capeUrl the capeUrl to set
     */
    public void setCapeUrl(String capeUrl)
    {
        this.capeUrl = capeUrl;
    }

    /**
     * @return the packedBb
     */
    public String getPackedBb()
    {
        return Helper.toMessage(bb, "|");
    }

    /**
     * @param packedBb the packedBb to set
     */
    public void setPackedBb(String packedBb)
    {
        this.bb = Helper.fromArray(packedBb.split("[|]"));
    }

    /**
     * @return the packedAllies
     */
    public String getPackedAllies()
    {
        return Helper.toMessage(allies, "|");
    }

    /**
     * @param packedAllies the packedAllies to set
     */
    public void setPackedAllies(String packedAllies)
    {
        this.allies = Helper.fromArray(packedAllies.split("[|]"));
    }

    /**
     * @return the packedRivals
     */
    public String getPackedRivals()
    {
        return Helper.toMessage(rivals, "|");
    }

    /**
     * @param packedRivals the packedRivals to set
     */
    public void setPackedRivals(String packedRivals)
    {
        this.rivals = Helper.fromArray(packedRivals.split("[|]"));
    }
}
