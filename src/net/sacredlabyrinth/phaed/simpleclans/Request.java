package net.sacredlabyrinth.phaed.simpleclans;

import java.util.ArrayList;
import java.util.List;
import net.sacredlabyrinth.phaed.simpleclans.managers.RequestManager.RequestType;
import net.sacredlabyrinth.phaed.simpleclans.managers.RequestManager.Vote;

/**
 *
 * @author phaed
 */
public final class Request
{
    private SimpleClans plugin;
    private List<ClanPlayer> acceptors = new ArrayList<ClanPlayer>();
    private Clan clan;
    private String msg;
    private String target;
    private RequestType type;
    private ClanPlayer requester;

    /**
     *
     * @param plugin
     * @param type
     * @param acceptors
     * @param msg
     * @param requester
     * @param clan
     * @param target
     */
    public Request(SimpleClans plugin, RequestType type, List<ClanPlayer> acceptors, ClanPlayer requester, String target, Clan clan, String msg)
    {
        plugin = SimpleClans.getInstance();
        this.type = type;
        this.target = target;
        this.clan = clan;
        this.msg = msg;
        if (acceptors != null)
        {
            this.acceptors = acceptors;
        }
        this.requester = requester;

        cleanVotes();
    }

    /**
     * @return the type
     */
    public RequestType getType()
    {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(RequestType type)
    {
        this.type = type;
    }

    /**
     * @return the plugin
     */
    public SimpleClans getPlugin()
    {
        return plugin;
    }

    /**
     * @param plugin the plugin to set
     */
    public void setPlugin()
    {
        plugin = SimpleClans.getInstance();
    }

    /**
     * @return the acceptors
     */
    public List<ClanPlayer> getAcceptors()
    {
        return acceptors;
    }

    /**
     * @param acceptors the acceptors to set
     */
    public void setAcceptors(List<ClanPlayer> acceptors)
    {
        this.setAcceptors(acceptors);
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
        this.clan = clan;
    }

    /**
     * @return the msg
     */
    public String getMsg()
    {
        return msg;
    }

    /**
     * @param msg the msg to set
     */
    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    /**
     * @return the target
     */
    public String getTarget()
    {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(String target)
    {
        this.target = target;
    }

    /**
     * Used for leader voting
     * @param playerNAme
     * @param vote
     */
    public void vote(String playerNAme, Vote vote)
    {
        for (ClanPlayer cp : acceptors)
        {
            if (cp.getName().equalsIgnoreCase(playerNAme))
            {
                cp.setVote(vote);
            }
        }
    }

    /**
     * Check whether all leaders have voted
     * @return
     */
    public boolean votingFinished()
    {
        for (ClanPlayer cp : acceptors)
        {
            if (cp.getVote() == null)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the players who have denied the request
     * @return
     */
    public List<String> getDenies()
    {
        List<String> out = new ArrayList<String>();

        for (ClanPlayer cp : acceptors)
        {
            if (cp.getVote().equals(Vote.DENY))
            {
                out.add(cp.getName());
            }
        }

        return out;
    }

    /**
     * Returns the players who have denied the request
     * @return
     */
    public List<String> getAccepts()
    {
        List<String> out = new ArrayList<String>();

        for (ClanPlayer cp : acceptors)
        {
            if (cp.getVote().equals(Vote.ACCEPT))
            {
                out.add(cp.getName());
            }
        }

        return out;
    }

    /**
     * Cleans votes
     */
    public void cleanVotes()
    {
        for (ClanPlayer cp : acceptors)
        {
            cp.setVote(null);
        }
    }

    /**
     * @return the requester
     */
    public ClanPlayer getRequester()
    {
        return requester;
    }

    /**
     * @param requester the requester to set
     */
    public void setRequester(ClanPlayer requester)
    {
        this.requester = requester;
    }
}