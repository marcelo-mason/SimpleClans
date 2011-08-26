package net.sacredlabyrinth.phaed.simpleclans;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import org.bukkit.ChatColor;

/**
 *
 * @author phaed
 */
public class ClanPlayer implements Serializable, Comparable<ClanPlayer>
{
    private static final long serialVersionUID = 1L;
    private String name;
    private boolean leader;
    private boolean trusted;
    private String tag;
    private Clan clan;
    private boolean friendlyFire;
    private int neutralKills;
    private int rivalKills;
    private int civilianKills;
    private int deaths;
    private long lastSeen;
    private long joinDate;
    private HashSet<String> pastClans = new HashSet<String>();
    private VoteResult vote;

    /**
     *
     */
    public ClanPlayer()
    {
        this.tag = "";
    }

    /**
     *
     * @param playerName
     */
    public ClanPlayer(String playerName)
    {
        this.name = playerName;
        this.lastSeen = (new Date()).getTime();
        this.joinDate = (new Date()).getTime();
        this.neutralKills = 0;
        this.rivalKills = 0;
        this.civilianKills = 0;
    }

    @Override
    public int hashCode()
    {
        return getName().hashCode() >> 13;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof ClanPlayer))
        {
            return false;
        }

        ClanPlayer other = (ClanPlayer) obj;
        return other.getName().equals(this.getName());
    }

    @Override
    public int compareTo(ClanPlayer other)
    {
        return this.getName().compareToIgnoreCase(other.getName());
    }

    @Override
    public String toString()
    {
        return name;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the name
     */
    public String getCleanName()
    {
        return name.toLowerCase();
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the leader
     */
    public boolean isLeader()
    {
        return leader;
    }

    /**
     * @param leader the leader to set
     */
    public void setLeader(boolean leader)
    {
        if (leader)
        {
            trusted = true;
        }

        this.leader = leader;
    }

    /**
     * @return the lastSeen
     */
    public long getLastSeen()
    {
        return lastSeen;
    }

    /**
     * @param lastSeen the lastSeen to set
     */
    public void setLastSeen(long lastSeen)
    {
        this.lastSeen = lastSeen;
    }

    /**
     * Updates last seen date
     */
    public void updateLastSeen()
    {
        this.lastSeen = (new Date()).getTime();
    }

    /**
     * Returns a verbal representation of how many days ago a player was last seen
     * @return
     */
    public String getLastSeenDaysString()
    {
        double days = Dates.differenceInDays(new Timestamp(lastSeen), new Timestamp((new Date()).getTime()));

        if (days < 1)
        {
            return "Today";
        }
        else if (Math.round(days) == 1)
        {
            return "1 " + ChatColor.GRAY + "day";
        }
        else
        {
            return Math.round(days) + "" + ChatColor.GRAY + " days";
        }
    }

    /**
     * Returns number of days since the player was last seen
     * @return
     */
    public double getLastSeenDays()
    {
        return Dates.differenceInDays(new Timestamp(lastSeen), new Timestamp((new Date()).getTime()));
    }

    /**
     * @return the rivalKills
     */
    public int getRivalKills()
    {
        return rivalKills;
    }

    /**
     * @param rivalKills the rivalKills to set
     */
    public void setRivalKills(int rivalKills)
    {
        this.rivalKills = rivalKills;
    }

    /**
     * Adds one rival kill
     */
    public void addRivalKill()
    {
        setRivalKills(getRivalKills() + 1);
    }

    /**
     * @return the civilianKills
     */
    public int getCivilianKills()
    {
        return civilianKills;
    }

    /**
     * @param civilianKills the civilianKills to set
     */
    public void setCivilianKills(int civilianKills)
    {
        this.civilianKills = civilianKills;
    }

    /**
     * Adds one civilian kill
     */
    public void addCivilianKill()
    {
        setCivilianKills(getCivilianKills() + 1);
    }

    /**
     * @return the neutralKills
     */
    public int getNeutralKills()
    {
        return neutralKills;
    }

    /**
     * @param neutralKills the neutralKills to set
     */
    public void setNeutralKills(int neutralKills)
    {
        this.neutralKills = neutralKills;
    }

    /**
     * Adds one civilian kill
     */
    public void addNeutralKill()
    {
        setNeutralKills(getNeutralKills() + 1);
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
     * @return the vote
     */
    public VoteResult getVote()
    {
        return vote;
    }

    /**
     * @param vote the vote to set
     */
    public void setVote(VoteResult vote)
    {
        this.vote = vote;
    }

    /**
     * @return the deaths
     */
    public int getDeaths()
    {
        return deaths;
    }

    /**
     * @param deaths the deaths to set
     */
    public void setDeaths(int deaths)
    {
        this.deaths = deaths;
    }

    /**
     * Adds one death
     */
    public void addDeath()
    {
        setDeaths(getDeaths() + 1);
    }

    /**
     * Returns weighted kill score
     * @return
     */
    public double getWeightedKills()
    {
        SimpleClans plugin = SimpleClans.getInstance();
        return (((double) rivalKills * plugin.getSettingsManager().getKwRival()) + ((double) neutralKills * plugin.getSettingsManager().getKwNeutral()) + ((double) civilianKills * plugin.getSettingsManager().getKwCivilian()));
    }

    /**
     * Returns kill / death ratio
     * @return
     */
    public float getKDR()
    {
        if (deaths == 0)
        {
            return 0;
        }

        return ((float) getWeightedKills()) / ((float) deaths);
    }

    /**
     * @return the joinDate
     */
    public long getJoinDate()
    {
        return joinDate;
    }

    /**
     * @param joinDate the joinDate to set
     */
    public void setJoinDate(long joinDate)
    {
        this.joinDate = joinDate;
    }

    /**
     * @return the string representation of the join date
     */
    public String getJoinDateString()
    {
        return new java.text.SimpleDateFormat("MMM dd, ''yy h:mm a").format(new Date(this.joinDate));
    }

    /**
     * @return the string representation of the last seen date
     */
    public String getLastSeenString()
    {
        return new java.text.SimpleDateFormat("MMM dd, ''yy h:mm a").format(new Date(this.lastSeen));
    }

    /**
     * @return number of days the player has been inactive
     */
    public int getInactiveDays()
    {
        Timestamp now = new Timestamp((new Date()).getTime());
        return (int) Math.floor(Dates.differenceInDays(new Timestamp(getLastSeen()), now));
    }

    /**
     * @return the PackedPastClans
     */
    public String getPackedPastClans()
    {
        String PackedPastClans = "";

        HashSet<String> pt = getPastClans();

        for (String pastClan : pt)
        {
            PackedPastClans += pastClan + "|";
        }

        return Helper.stripTrailing(PackedPastClans, "|");
    }

    /**
     * @param PackedPastClans the PackedPastClans to set
     */
    public void setPackedPastClans(String PackedPastClans)
    {
        this.pastClans = Helper.fromArray2(PackedPastClans.split("[|]"));
    }

    /**
     *
     * @param tag
     */
    public void addPastClan(String tag)
    {
        this.getPastClans().add(tag);
    }

    /**
     *
     * @param tag
     */
    public void removePastClan(String tag)
    {
        this.getPastClans().remove(tag);
    }

    /**
     *
     * @param sep
     * @return
     */
    public String getPastClansString(String sep)
    {
        String out = "";

        for (String pastClan : getPastClans())
        {
            out += pastClan + sep;
        }

        out = Helper.stripTrailing(out, sep);

        if (out.trim().isEmpty())
        {
            return "None";
        }

        return out;
    }

    /**
     * @return the pastClans
     */
    public HashSet<String> getPastClans()
    {
        HashSet<String> pc = new HashSet<String>();
        pc.addAll(pastClans);
        return pc;
    }

    /**
     * @return the clan
     */
    public Clan getClan()
    {
        return clan;
    }

    /**
     * @param clan the clan to set
     */
    public void setClan(Clan clan)
    {
        if (clan == null)
        {
            this.tag = "";
        }
        else
        {
            this.tag = clan.getTag();
        }

        this.clan = clan;
    }

    /**
     * @return the tag
     */
    public String getTag()
    {
        return tag;
    }

    /**
     * @return the trusted
     */
    public boolean isTrusted()
    {
        return trusted;
    }

    /**
     * @param trusted the trusted to set
     */
    public void setTrusted(boolean trusted)
    {
        this.trusted = trusted;
    }
}
