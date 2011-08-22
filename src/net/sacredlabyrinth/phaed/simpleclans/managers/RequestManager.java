package net.sacredlabyrinth.phaed.simpleclans.managers;

import java.util.HashMap;
import java.util.List;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.Request;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public final class RequestManager
{
    private SimpleClans plugin;
    private HashMap<String, Request> requests = new HashMap<String, Request>();

    /**
     *
     * @param plugin
     */
    public RequestManager()
    {
        plugin = SimpleClans.getInstance();
        askerTask();
    }

    /**
     * Request Types
     */
    public enum RequestType
    {
        DEMOTE,
        PROMOTE,
        DELETE,
        INVITE,
        CREATE_ALLY,
        BREAK_RIVALRY
    }

    /**
     * Possible vote values
     */
    public enum Vote
    {
        ACCEPT,
        DENY
    }

    /**
     * Check whether the clan has a pending request
     * @param tag
     * @return
     */
    public boolean hasRequest(String tag)
    {
        return requests.containsKey(tag);
    }

    /**
     * Add a demotion request
     * @param plugin
     * @param requester
     * @param demotedName
     * @param clan
     */
    public void addDemoteRequest(SimpleClans plugin, ClanPlayer requester, String demotedName, Clan clan)
    {
        String msg = Helper.capitalize(requester.getName()) + " is asking for the demotion of " + demotedName;

        ClanPlayer demotedTp = plugin.getClanManager().getClanPlayer(demotedName.toLowerCase());

        List<ClanPlayer> acceptors = Helper.stripOffLinePlayers(plugin.getClanManager().getLeaders(clan));
        acceptors.remove(plugin.getClanManager().getClanPlayer(demotedName.toLowerCase()));

        Request req = new Request(plugin, RequestType.DEMOTE, acceptors, requester, demotedName, clan, msg);
        requests.put(clan.getTag(), req);
        ask(req);
    }

    /**
     * Add a promotion request
     * @param plugin
     * @param requester
     * @param promotedName
     * @param clan
     */
    public void addPromoteRequest(SimpleClans plugin, ClanPlayer requester, String promotedName, Clan clan)
    {
        String msg = Helper.capitalize(requester.getName()) + " is asking for the promotion of " + promotedName;

        List<ClanPlayer> acceptors = Helper.stripOffLinePlayers(plugin.getClanManager().getLeaders(clan));
        acceptors.remove(requester);

        Request req = new Request(plugin, RequestType.PROMOTE, acceptors, requester, promotedName, clan, msg);
        requests.put(clan.getTag(), req);
        ask(req);
    }

    /**
     * Add a clan delete request
     * @param plugin
     * @param requester
     * @param clan
     */
    public void addDeleteRequest(SimpleClans plugin, ClanPlayer requester, Clan clan)
    {
        String msg = Helper.capitalize(requester.getName()) + " is asking for the deletion of the clan";

        List<ClanPlayer> acceptors = Helper.stripOffLinePlayers(plugin.getClanManager().getLeaders(clan));
        acceptors.remove(requester);

        Request req = new Request(plugin, RequestType.DELETE, acceptors, requester, null, clan, msg);
        requests.put(clan.getTag(), req);
        ask(req);
    }

    /**
     * Add a member invite request
     * @param plugin
     * @param requester
     * @param invitedName
     * @param clan
     */
    public void addInviteRequest(SimpleClans plugin, ClanPlayer requester, String invitedName, Clan clan)
    {
        String msg = Helper.capitalize(requester.getName()) + " is inviting you to join " + clan.getName();
        Request req = new Request(plugin, RequestType.INVITE, null, requester, invitedName, clan, msg);
        requests.put(invitedName.toLowerCase(), req);
        ask(req);
    }

    /**
     * Add an clan alliance request
     * @param plugin
     * @param requester
     * @param allyClan
     * @param requestingClan
     */
    public void addAllyRequest(SimpleClans plugin, ClanPlayer requester, Clan allyClan, Clan requestingClan)
    {
        String msg = Helper.capitalize(requestingClan.getName()) + " is proposing an alliance with " + Helper.stripColors(allyClan.getColorTag());

        List<ClanPlayer> acceptors = Helper.stripOffLinePlayers(plugin.getClanManager().getLeaders(allyClan));
        acceptors.remove(requester);

        Request req = new Request(plugin, RequestType.CREATE_ALLY, acceptors, requester, allyClan.getTag(), requestingClan, msg);
        requests.put(allyClan.getTag(), req);
        ask(req);
    }

    /**
     * Add an clan rivalry break request
     * @param plugin
     * @param requester
     * @param rivalClan
     * @param requestingClan
     */
    public void addRivalryBreakRequest(SimpleClans plugin, ClanPlayer requester, Clan rivalClan, Clan requestingClan)
    {
        String msg = Helper.capitalize(requestingClan.getName()) + " is proposing to end the rivalry with " + Helper.stripColors(rivalClan.getColorTag());

        List<ClanPlayer> acceptors = Helper.stripOffLinePlayers(plugin.getClanManager().getLeaders(rivalClan));
        acceptors.remove(requester);

        Request req = new Request(plugin, RequestType.BREAK_RIVALRY, acceptors, requester, rivalClan.getTag(), requestingClan, msg);
        requests.put(rivalClan.getTag(), req);
        ask(req);
    }

    /**
     * Record one player's accept vote
     * @param cp
     */
    public void accept(ClanPlayer cp)
    {
        Request req = requests.get(cp.getTag());

        if (req != null)
        {
            req.vote(cp.getName(), Vote.ACCEPT);
            processResults(req);
        }
        else
        {
            req = requests.get(cp.getCleanName());

            if (req != null)
            {
                processInvite(req, Vote.ACCEPT);
            }
        }
    }

    /**
     * Record one player's deny vote
     * @param cp
     */
    public void deny(ClanPlayer cp)
    {
        Request req = requests.get(cp.getTag());

        if (req != null)
        {
            req.vote(cp.getName(), Vote.DENY);
            processResults(req);
        }
        else
        {
            req = requests.get(cp.getCleanName());

            if (req != null)
            {
                processInvite(req, Vote.DENY);
            }
        }
    }

    /**
     *
     * @param req
     * @param vote
     */
    public void processInvite(Request req, Vote vote)
    {
        Clan clan = req.getClan();
        String invited = req.getTarget();

        if (vote.equals(Vote.ACCEPT))
        {
            ClanPlayer cp = plugin.getClanManager().getCreateClanPlayer(invited);

            clan.addBb(ChatColor.AQUA + Helper.capitalize(invited) + " has joined the clan");
            plugin.getClanManager().serverAnnounce(Helper.capitalize(invited) + " has joined " + clan.getName());
            plugin.getClanManager().addMemberToClan(cp, clan);
        }
        else
        {
            plugin.getClanManager().leaderAnnounce("Leaders", clan, ChatColor.RED + Helper.capitalize(invited) + " has denied the membership invitation");
        }

        requests.remove(req.getTarget().toLowerCase());
    }

    /**
     * Check to see if votes are complete and process the result
     * @param req
     */
    public void processResults(Request req)
    {
        if (req.getType().equals(RequestType.CREATE_ALLY))
        {
            Clan clan = req.getClan();
            Clan ally = plugin.getClanManager().getClan(req.getTarget());
            ClanPlayer cp = req.getRequester();

            if (ally != null && clan != null)
            {
                List<String> accepts = req.getAccepts();
                List<String> denies = req.getDenies();

                if (!accepts.isEmpty())
                {
                    plugin.getClanManager().addAlly(clan, ally);
                    plugin.getClanManager().addBb(cp.getName(), ally, ChatColor.AQUA + Helper.capitalize(accepts.get(0)) + " has accepted an alliance with " + clan.getName());
                    plugin.getClanManager().addBb(cp.getName(), clan, ChatColor.AQUA + Helper.capitalize(cp.getName()) + " has created an alliance with " + Helper.capitalize(ally.getName()));
                }
                else
                {
                    plugin.getClanManager().addBb(cp.getName(), ally, ChatColor.AQUA + Helper.capitalize(denies.get(0)) + " has denied an alliance with " + clan.getName());
                    plugin.getClanManager().addBb(cp.getName(), clan, ChatColor.AQUA + "The alliance with " + Helper.capitalize(ally.getName()) + " was denied");
                }
            }
        }
        else if (req.getType().equals(RequestType.BREAK_RIVALRY))
        {
            Clan clan = req.getClan();
            Clan rival = plugin.getClanManager().getClan(req.getTarget());
            ClanPlayer cp = req.getRequester();

            if (rival != null && clan != null)
            {
                List<String> accepts = req.getAccepts();
                List<String> denies = req.getDenies();

                if (!accepts.isEmpty())
                {
                    plugin.getClanManager().removeRival(clan, rival);
                    plugin.getClanManager().addBb(cp.getName(), rival, ChatColor.AQUA + Helper.capitalize(accepts.get(0)) + " has broken the rivalry with " + clan.getName());
                    plugin.getClanManager().addBb(cp.getName(), clan, ChatColor.AQUA + Helper.capitalize(cp.getName()) + " has broken the rivalry with " + Helper.capitalize(rival.getName()));
                }
                else
                {
                    plugin.getClanManager().addBb(cp.getName(), rival, ChatColor.AQUA + Helper.capitalize(denies.get(0)) + " has denied to make peace with " + clan.getName());
                    plugin.getClanManager().addBb(cp.getName(), clan, ChatColor.AQUA + "The peace agreement with " + Helper.capitalize(rival.getName()) + " was denied");
                }
            }
        }
        else if (req.votingFinished())
        {
            List<String> denies = req.getDenies();

            if (req.getType().equals(RequestType.DEMOTE))
            {
                Clan clan = req.getClan();
                String demoted = req.getTarget();

                if (denies.isEmpty())
                {
                    plugin.getClanManager().addBb("Leaders", clan, ChatColor.AQUA + Helper.capitalize(demoted) + " has been demoted back to member");
                    plugin.getClanManager().demote(demoted, clan);
                }
                else
                {
                    String deniers = Helper.capitalize(Helper.toMessage(Helper.toArray(denies), ", "));
                    plugin.getClanManager().leaderAnnounce("Leaders", clan, ChatColor.RED + deniers + " " + (denies.size() == 1 ? "has" : "have") + " denied the demotion of " + demoted + ".  Consensus failed.");
                }
            }
            else if (req.getType().equals(RequestType.PROMOTE))
            {
                Clan clan = req.getClan();
                String promoted = req.getTarget();

                if (denies.isEmpty())
                {
                    plugin.getClanManager().addBb("Leaders", clan, ChatColor.AQUA + Helper.capitalize(promoted) + " has been promoted to leader");
                    plugin.getClanManager().promote(promoted, clan);
                }
                else
                {
                    String deniers = Helper.capitalize(Helper.toMessage(Helper.toArray(denies), ", "));
                    plugin.getClanManager().leaderAnnounce("Leaders", clan, ChatColor.RED + deniers + " " + (denies.size() == 1 ? "has" : "have") + " denied the promotion of " + promoted + ".  Consensus failed.");
                }
            }
            else if (req.getType().equals(RequestType.DELETE))
            {
                Clan clan = req.getClan();

                if (denies.isEmpty())
                {
                    plugin.getClanManager().addBb("Leaders", clan, ChatColor.AQUA + "Clan " + clan.getName() + " has been deleted");
                    plugin.getClanManager().deleteClan(clan);
                }
                else
                {
                    String deniers = Helper.capitalize(Helper.toMessage(Helper.toArray(denies), ", "));
                    plugin.getClanManager().leaderAnnounce("Leaders", clan, ChatColor.RED + deniers + " " + (denies.size() == 1 ? "has" : "have") + " denied the clan deletion.  Consensus failed.");
                }
            }

            req.cleanVotes();
            requests.remove(req.getClan().getTag());
        }
    }

    /**
     * End a pending request prematurely
     * @param playerName
     * @return
     */
    public boolean endPendingRequest(String playerName)
    {
        for (Request req : requests.values())
        {
            for (ClanPlayer cp : req.getAcceptors())
            {
                if (cp.getName().equalsIgnoreCase(playerName))
                {
                    plugin.getClanManager().leaderAnnounce("Leaders", req.getClan(), ChatColor.RED + Helper.capitalize(playerName) + " has signed off. " + req.getType() + " request cancelled.");
                    requests.remove(req.getClan().getTag());
                    break;
                }
            }
        }

        return false;
    }

    /**
     * Starts the task that asks for the votes of all requests
     */
    public void askerTask()
    {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                for (Request req : requests.values())
                {
                    ask(req);
                }
            }
        }, 0, plugin.getSettingsManager().getRequestFreqencySecs() * 20L);
    }

    /**
     * Asks a request's players for votes
     * @param req
     */
    public void ask(Request req)
    {
        final String tag = Helper.toColor(plugin.getSettingsManager().getClanChatBracketColor()) + plugin.getSettingsManager().getClanChatTagBracketLeft() + Helper.toColor(plugin.getSettingsManager().getTagDefaultColor()) + Helper.parseColors(req.getClan().getColorTag()) + Helper.toColor(plugin.getSettingsManager().getClanChatBracketColor()) + plugin.getSettingsManager().getClanChatTagBracketRight();
        final String message = tag + " " + Helper.toColor(plugin.getSettingsManager().getRequestMessageColor()) + req.getMsg();
        final String options = ChatBlock.makeEmpty(Helper.stripColors(tag)) + " " + ChatColor.DARK_GREEN + "/accept" + Helper.toColor(plugin.getSettingsManager().getPageHeadingsColor()) + " or " + ChatColor.DARK_RED + "/deny";

        if (req.getType().equals(RequestType.INVITE))
        {
            Player player = plugin.getServer().getPlayer(req.getTarget());

            if (player != null)
            {
                ChatBlock.sendBlank(player);
                ChatBlock.sendMessage(player, message);
                ChatBlock.sendMessage(player, options);
                ChatBlock.sendBlank(player);
            }
        }
        else
        {
            for (ClanPlayer cp : req.getAcceptors())
            {
                if (cp.getVote() == null)
                {
                    Player player = plugin.getServer().getPlayer(cp.getName());

                    if (player != null)
                    {
                        ChatBlock.sendBlank(player);
                        ChatBlock.sendMessage(player, message);
                        ChatBlock.sendMessage(player, options);
                        ChatBlock.sendBlank(player);
                    }
                }
            }
        }
    }
}
