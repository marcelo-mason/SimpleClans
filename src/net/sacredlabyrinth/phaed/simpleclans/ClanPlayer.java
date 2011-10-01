package net.sacredlabyrinth.phaed.simpleclans;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.stringtree.json.JSONReader;
import org.stringtree.json.JSONValidatingReader;
import org.stringtree.json.JSONWriter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.*;

/**
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
    private Channel channel;
    private boolean globalChat = true;
    private boolean allyChat = true;
    private boolean clanChat = true;

    /**
     *
     */
    public ClanPlayer()
    {
        this.tag = "";
        this.channel = Channel.NONE;
    }

    /**
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
        this.tag = "";
        this.channel = Channel.NONE;
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
     * (used internally)
     *
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the clean name for this player (lowercase)
     *
     * @return the name
     */
    public String getCleanName()
    {
        return name.toLowerCase();
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
     * Whether this player is a leader or not
     *
     * @return the leader
     */
    public boolean isLeader()
    {
        return leader;
    }

    /**
     * Sets this player as a leader (does not update clanplayer to db)
     *
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
     * Check whether the player is an ally with another player
     *
     * @param player
     * @return
     */
    public boolean isAlly(Player player)
    {
        ClanPlayer allycp = SimpleClans.getInstance().getClanManager().getClanPlayer(player);

        if (allycp != null)
        {
            return allycp.getClan().isAlly(tag);
        }

        return false;
    }


    /**
     * Returns the last seen date for this player in milliseconds
     *
     * @return the lastSeen
     */
    public long getLastSeen()
    {
        return lastSeen;
    }

    /**
     * (used internally)
     *
     * @param lastSeen the lastSeen to set
     */
    public void setLastSeen(long lastSeen)
    {
        this.lastSeen = lastSeen;
    }

    /**
     * Updates last seen date to today
     */
    public void updateLastSeen()
    {
        this.lastSeen = (new Date()).getTime();
    }

    /**
     * Returns a verbal representation of how many days ago a player was last seen
     *
     * @return
     */
    public String getLastSeenDaysString()
    {
        double days = Dates.differenceInDays(new Timestamp(lastSeen), new Timestamp((new Date()).getTime()));

        if (days < 1)
        {
            return SimpleClans.getInstance().getLang().getString("today");
        }
        else if (Math.round(days) == 1)
        {
            return MessageFormat.format(SimpleClans.getInstance().getLang().getString("1.color.day"), ChatColor.GRAY);
        }
        else
        {
            return MessageFormat.format(SimpleClans.getInstance().getLang().getString("many.color.days"), Math.round(days), ChatColor.GRAY);
        }
    }

    /**
     * Returns number of days since the player was last seen
     *
     * @return
     */
    public double getLastSeenDays()
    {
        return Dates.differenceInDays(new Timestamp(lastSeen), new Timestamp((new Date()).getTime()));
    }

    /**
     * Returns the number of rival kills this player has
     *
     * @return the rivalKills
     */
    public int getRivalKills()
    {
        return rivalKills;
    }

    /**
     * (used internally)
     *
     * @param rivalKills the rivalKills to set
     */
    public void setRivalKills(int rivalKills)
    {
        this.rivalKills = rivalKills;
    }

    /**
     * Adds one rival kill to this player (does not update clanplayer to db)
     */
    public void addRivalKill()
    {
        setRivalKills(getRivalKills() + 1);
    }

    /**
     * Returns the number of civilian kills this player has
     *
     * @return the civilianKills
     */
    public int getCivilianKills()
    {
        return civilianKills;
    }

    /**
     * (used internally)
     *
     * @param civilianKills the civilianKills to set
     */
    public void setCivilianKills(int civilianKills)
    {
        this.civilianKills = civilianKills;
    }

    /**
     * Adds one civilian kill to this player (does not update clanplayer to db)
     */
    public void addCivilianKill()
    {
        setCivilianKills(getCivilianKills() + 1);
    }

    /**
     * Returns the number of neutral kills this player has
     *
     * @return the neutralKills
     */
    public int getNeutralKills()
    {
        return neutralKills;
    }

    /**
     * (used internally)
     *
     * @param neutralKills the neutralKills to set
     */
    public void setNeutralKills(int neutralKills)
    {
        this.neutralKills = neutralKills;
    }

    /**
     * Adds one civilian kill to this player (does not update clanplayer to db)
     */
    public void addNeutralKill()
    {
        setNeutralKills(getNeutralKills() + 1);
    }

    /**
     * Whether this player is allowing friendly fire
     *
     * @return the friendlyFire
     */
    public boolean isFriendlyFire()
    {
        return friendlyFire;
    }

    /**
     * Sets whether this player is allowing friendly fire (does not update clanplayer to db)
     *
     * @param friendlyFire the friendlyFire to set
     */
    public void setFriendlyFire(boolean friendlyFire)
    {
        this.friendlyFire = friendlyFire;
    }

    /**
     * (used internally)
     *
     * @return the vote
     */
    public VoteResult getVote()
    {
        return vote;
    }

    /**
     * (used internally)
     *
     * @param vote the vote to set
     */
    public void setVote(VoteResult vote)
    {
        this.vote = vote;
    }

    /**
     * Returns the number of deaths this player has
     *
     * @return the deaths
     */
    public int getDeaths()
    {
        return deaths;
    }

    /**
     * (used internally)
     *
     * @param deaths the deaths to set
     */
    public void setDeaths(int deaths)
    {
        this.deaths = deaths;
    }

    /**
     * Adds one death to this player  (does not update clanplayer to db)
     */
    public void addDeath()
    {
        setDeaths(getDeaths() + 1);
    }

    /**
     * Returns weighted kill score for this player (kills multiplied by the different weights)
     *
     * @return
     */
    public double getWeightedKills()
    {
        SimpleClans plugin = SimpleClans.getInstance();
        return (((double) rivalKills * plugin.getSettingsManager().getKwRival()) + ((double) neutralKills * plugin.getSettingsManager().getKwNeutral()) + ((double) civilianKills * plugin.getSettingsManager().getKwCivilian()));
    }

    /**
     * Returns weighted-kill/death ratio
     *
     * @return
     */
    public float getKDR()
    {
        int totalDeaths = getDeaths();

        if (totalDeaths == 0)
        {
            totalDeaths = 1;
        }

        return ((float) getWeightedKills()) / ((float) totalDeaths);
    }

    /**
     * Returns the player's join date to his current clan in milliseconds, 0 if not in a clan
     *
     * @return the joinDate
     */
    public long getJoinDate()
    {
        return joinDate;
    }

    /**
     * (used internally)
     *
     * @param joinDate the joinDate to set
     */
    public void setJoinDate(long joinDate)
    {
        this.joinDate = joinDate;
    }

    /**
     * Returns a string representation of the join date, blank if not in a clan
     *
     * @return
     */
    public String getJoinDateString()
    {
        if (joinDate == 0)
        {
            return "";
        }

        return new java.text.SimpleDateFormat("MMM dd, ''yy h:mm a").format(new Date(this.joinDate));
    }

    /**
     * Returns a string representation of the last seen date
     *
     * @return
     */
    public String getLastSeenString()
    {
        return new java.text.SimpleDateFormat("MMM dd, ''yy h:mm a").format(new Date(this.lastSeen));
    }

    /**
     * Returns the number of days the player has been inactive
     *
     * @return
     */
    public int getInactiveDays()
    {
        Timestamp now = new Timestamp((new Date()).getTime());
        return (int) Math.floor(Dates.differenceInDays(new Timestamp(getLastSeen()), now));
    }

    /**
     * (used internally)
     *
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
     * (used internally)
     *
     * @param PackedPastClans the PackedPastClans to set
     */
    public void setPackedPastClans(String PackedPastClans)
    {
        this.pastClans = Helper.fromArray2(PackedPastClans.split("[|]"));
    }

    /**
     * Adds a past clan to the player (does not update the clanplayer to db)
     *
     * @param tag
     */
    public void addPastClan(String tag)
    {
        this.getPastClans().add(tag);
    }

    /**
     * Removes a past clan from the player (does not update the clanplayer to db)
     *
     * @param tag is the clan's colored tag
     */
    public void removePastClan(String tag)
    {
        this.getPastClans().remove(tag);
    }

    /**
     * Returns a separator delimited string with the color tags for all past clans this player has been in
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
            return SimpleClans.getInstance().getLang().getString("none");
        }

        return out;
    }

    /**
     * Returns a list with all past clans color tags this player has been in
     *
     * @return the pastClans
     */
    public HashSet<String> getPastClans()
    {
        HashSet<String> pc = new HashSet<String>();
        pc.addAll(pastClans);
        return pc;
    }

    /**
     * Returns this player's clan
     *
     * @return the clan
     */
    public Clan getClan()
    {
        return clan;
    }

    /**
     * (used internally)
     *
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
     * Returns this player's clan's tag.  Empty string if he's not in a clan.
     *
     * @return the tag
     */
    public String getTag()
    {
        return tag;
    }

    /**
     * Returns this player's trusted status
     *
     * @return the trusted
     */
    public boolean isTrusted()
    {
        return trusted;
    }

    /**
     * Sets this player's trusted status (does not update the clanplayer to db)
     *
     * @param trusted the trusted to set
     */
    public void setTrusted(boolean trusted)
    {
        this.trusted = trusted;
    }

    /**
     * Return the list of flags and their data as a json string
     *
     * @return the flags
     */
    public String getFlags()
    {
        HashMap<String, Object> flags = new HashMap<String, Object>();

        // writing the list of flags to json

        flags.put("channel", channel);

        // writing the channel state settings flags

        List<Boolean> settings = new LinkedList<Boolean>();
        settings.add(globalChat);
        settings.add(allyChat);
        settings.add(clanChat);

        flags.put("channel-state", settings);

        return (new JSONWriter()).write(flags);
    }

    /**
     * Read the list of flags in from a json string
     *
     * @param flagString the flags to set
     */
    public void setFlags(String flagString)
    {
        if (flagString != null && !flagString.isEmpty())
        {
            JSONReader reader = new JSONValidatingReader();
            HashMap<String, Object> flags = (HashMap<String, Object>) reader.read(flagString);

            if (flags != null)
            {
                for (String flag : flags.keySet())
                {
                    // reading the list of flags from json

                    if (flag.equals("channel"))
                    {
                        String chn = (String) flags.get(flag);

                        if (chn != null && !chn.isEmpty())
                        {
                            if (chn.equalsIgnoreCase("clan"))
                            {
                                channel = Channel.CLAN;
                            }
                            else if (chn.equalsIgnoreCase("ally"))
                            {
                                channel = Channel.ALLY;
                            }
                            else
                            {
                                channel = Channel.NONE;
                            }
                        }
                    }

                    if (flag.equals("channel-state"))
                    {
                        List<Boolean> settings = (List<Boolean>) flags.get(flag);

                        if (settings != null && !settings.isEmpty())
                        {
                            globalChat = settings.get(0);
                            allyChat = settings.get(1);
                            clanChat = settings.get(2);
                        }
                    }
                }
            }
        }
    }

    public Channel getChannel()
    {
        return channel;
    }

    public boolean isGlobalChat()
    {
        return globalChat;
    }

    public boolean isAllyChat()
    {
        return allyChat;
    }

    public boolean isClanChat()
    {
        return clanChat;
    }

    public void setGlobalChat(boolean globalChat)
    {
        this.globalChat = globalChat;
    }

    public void setAllyChat(boolean allyChat)
    {
        this.allyChat = allyChat;
    }

    public void setClanChat(boolean clanChat)
    {
        this.clanChat = clanChat;
    }

    public void setChannel(Channel channel)
    {
        this.channel = channel;
    }

    public enum Channel
    {
        CLAN,
        ALLY,
        NONE
    }
}

