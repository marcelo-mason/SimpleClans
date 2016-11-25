package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.events.RequestEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.RequestFinishedEvent;
import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDMigration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.*;

/**
 * @author phaed
 */
public final class RequestManager {
    private SimpleClans plugin;
    private HashMap<String, Request> requests = new HashMap<>();

    /**
     *
     */
    public RequestManager() {
        plugin = SimpleClans.getInstance();
        askerTask();
    }

    /**
     * Check whether the clan has a pending request
     *
     * @param tag
     * @return
     */
    public boolean hasRequest(String tag) {
        return requests.containsKey(tag);
    }

    /**
     * Add a demotion request
     *
     * @param requester
     * @param demotedName
     * @param clan
     */
    public void addDemoteRequest(ClanPlayer requester, String demotedName, Clan clan) {
        String msg = MessageFormat.format(plugin.getLang("asking.for.the.demotion"), Helper.capitalize(requester.getName()), demotedName);

        ClanPlayer demotedTp = plugin.getClanManager().getClanPlayer(demotedName.toLowerCase());

        List<ClanPlayer> acceptors = Helper.stripOffLinePlayers(clan.getLeaders());
        acceptors.remove(demotedTp);

        Request req = new Request(plugin, ClanRequest.DEMOTE, acceptors, requester, demotedName, clan, msg);
        requests.put(clan.getTag(), req);
        ask(req);
    }

    /**
     * Add a promotion request
     *
     * @param requester
     * @param promotedName
     * @param clan
     */
    public void addPromoteRequest(ClanPlayer requester, String promotedName, Clan clan) {
        String msg = MessageFormat.format(plugin.getLang("asking.for.the.promotion"), Helper.capitalize(requester.getName()), promotedName);

        List<ClanPlayer> acceptors = Helper.stripOffLinePlayers(clan.getLeaders());
        acceptors.remove(requester);

        Request req = new Request(plugin, ClanRequest.PROMOTE, acceptors, requester, promotedName, clan, msg);
        requests.put(clan.getTag(), req);
        ask(req);
    }

    /**
     * Add a clan disband request
     *
     * @param requester
     * @param clan
     */
    public void addDisbandRequest(ClanPlayer requester, Clan clan) {
        String msg = MessageFormat.format(plugin.getLang("asking.for.the.deletion"), Helper.capitalize(requester.getName()));

        List<ClanPlayer> acceptors = Helper.stripOffLinePlayers(clan.getLeaders());
        acceptors.remove(requester);

        Request req = new Request(plugin, ClanRequest.DISBAND, acceptors, requester, clan.getTag(), clan, msg);
        requests.put(clan.getTag(), req);
        ask(req);
    }

    /**
     * Add a member invite request
     *
     * @param requester
     * @param invitedName
     * @param clan
     */
    public void addInviteRequest(ClanPlayer requester, String invitedName, Clan clan) {
        String msg = MessageFormat.format(plugin.getLang("inviting.you.to.join"), Helper.capitalize(requester.getName()), clan.getName());
        Request req = new Request(plugin, ClanRequest.INVITE, null, requester, invitedName, clan, msg);
        requests.put(invitedName.toLowerCase(), req);
        ask(req);
    }

    /**
     * Add an clan war request
     *
     * @param requester
     * @param warClan
     * @param requestingClan
     */
    public void addWarStartRequest(ClanPlayer requester, Clan warClan, Clan requestingClan) {
        String msg = MessageFormat.format(plugin.getLang("proposing.war"), Helper.capitalize(requestingClan.getName()), Helper.stripColors(warClan.getColorTag()));

        List<ClanPlayer> acceptors = Helper.stripOffLinePlayers(warClan.getLeaders());
        acceptors.remove(requester);

        Request req = new Request(plugin, ClanRequest.START_WAR, acceptors, requester, warClan.getTag(), requestingClan, msg);
        requests.put(warClan.getTag(), req);
        ask(req);
    }

    /**
     * Add an war end request
     *
     * @param requester
     * @param warClan
     * @param requestingClan
     */
    public void addWarEndRequest(ClanPlayer requester, Clan warClan, Clan requestingClan) {
        String msg = MessageFormat.format(plugin.getLang("proposing.to.end.the.war"), Helper.capitalize(requestingClan.getName()), Helper.stripColors(warClan.getColorTag()));

        List<ClanPlayer> acceptors = Helper.stripOffLinePlayers(warClan.getLeaders());
        acceptors.remove(requester);

        Request req = new Request(plugin, ClanRequest.END_WAR, acceptors, requester, warClan.getTag(), requestingClan, msg);
        requests.put(warClan.getTag(), req);
        ask(req);
    }

    /**
     * Add an clan alliance request
     *
     * @param requester
     * @param allyClan
     * @param requestingClan
     */
    public void addAllyRequest(ClanPlayer requester, Clan allyClan, Clan requestingClan) {
        String msg = MessageFormat.format(plugin.getLang("proposing.an.alliance"), Helper.capitalize(requestingClan.getName()), Helper.stripColors(allyClan.getColorTag()));

        List<ClanPlayer> acceptors = Helper.stripOffLinePlayers(allyClan.getLeaders());
        acceptors.remove(requester);

        Request req = new Request(plugin, ClanRequest.CREATE_ALLY, acceptors, requester, allyClan.getTag(), requestingClan, msg);
        requests.put(allyClan.getTag(), req);
        ask(req);
    }

    /**
     * Add an clan rivalry break request
     *
     * @param requester
     * @param rivalClan
     * @param requestingClan
     */
    public void addRivalryBreakRequest(ClanPlayer requester, Clan rivalClan, Clan requestingClan) {
        String msg = MessageFormat.format(plugin.getLang("proposing.to.end.the.rivalry"), Helper.capitalize(requestingClan.getName()), Helper.stripColors(rivalClan.getColorTag()));

        List<ClanPlayer> acceptors = Helper.stripOffLinePlayers(rivalClan.getLeaders());
        acceptors.remove(requester);

        Request req = new Request(plugin, ClanRequest.BREAK_RIVALRY, acceptors, requester, rivalClan.getTag(), requestingClan, msg);
        requests.put(rivalClan.getTag(), req);
        ask(req);
    }

    /**
     * Record one player's accept vote
     *
     * @param cp
     */
    public void accept(ClanPlayer cp) {
        Request req = requests.get(cp.getTag());

        if (req != null) {
            req.vote(cp.getName(), VoteResult.ACCEPT);
            processResults(req);
        } else {
            req = requests.get(cp.getCleanName());

            if (req != null) {
                processInvite(req, VoteResult.ACCEPT);
            }
        }
    }

    /**
     * Record one player's deny vote
     *
     * @param cp
     */
    public void deny(ClanPlayer cp) {
        Request req = requests.get(cp.getTag());

        if (req != null) {
            req.vote(cp.getName(), VoteResult.DENY);
            processResults(req);
        } else {
            req = requests.get(cp.getCleanName());

            if (req != null) {
                processInvite(req, VoteResult.DENY);
            }
        }
    }

    /**
     * Process the answer from an invite and add the player to the clan if accepted
     *
     * @param req
     * @param vote
     */
    public void processInvite(Request req, VoteResult vote) {
        Clan clan = req.getClan();
        String invited = req.getTarget();

        if (vote.equals(VoteResult.ACCEPT)) {
            ClanPlayer cp = plugin.getClanManager().getCreateClanPlayerUUID(invited);
            if (cp == null) {
                return;
            }

            clan.addBb(ChatColor.AQUA + MessageFormat.format(plugin.getLang("joined.the.clan"), Helper.capitalize(invited)));
            plugin.getClanManager().serverAnnounce(MessageFormat.format(plugin.getLang("has.joined"), Helper.capitalize(invited), clan.getName()));
            clan.addPlayerToClan(cp);
        } else {
            clan.leaderAnnounce(ChatColor.RED + MessageFormat.format(plugin.getLang("membership.invitation"), Helper.capitalize(invited)));
        }

        requests.remove(req.getTarget().toLowerCase());
    }

    /**
     * Check to see if votes are complete and process the result
     *
     * @param req
     */
    public void processResults(Request req) {
        if (req.getType().equals(ClanRequest.START_WAR)) {
            Clan clan = req.getClan();
            Clan war = plugin.getClanManager().getClan(req.getTarget());
            ClanPlayer cp = req.getRequester();

            if (war != null && clan != null) {
                List<String> accepts = req.getAccepts();
                List<String> denies = req.getDenies();

                if (!accepts.isEmpty()) {
                    clan.addWarringClan(war);
                    war.addWarringClan(clan);

                    war.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("you.are.at.war"), Helper.capitalize(war.getName()), clan.getColorTag()));
                    clan.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("you.are.at.war"), Helper.capitalize(clan.getName()), war.getColorTag()));
                } else {
                    war.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("denied.war.req"), Helper.capitalize(denies.get(0)), clan.getName()));
                    clan.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("end.war.denied"), Helper.capitalize(war.getName())));
                }
            }
        } else if (req.getType().equals(ClanRequest.END_WAR)) {
            Clan clan = req.getClan();
            Clan war = plugin.getClanManager().getClan(req.getTarget());
            ClanPlayer cp = req.getRequester();

            if (war != null && clan != null) {
                List<String> accepts = req.getAccepts();
                List<String> denies = req.getDenies();

                if (!accepts.isEmpty()) {
                    clan.removeWarringClan(war);
                    war.removeWarringClan(clan);

                    war.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("you.are.no.longer.at.war"), Helper.capitalize(accepts.get(0)), clan.getColorTag()));
                    clan.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("you.are.no.longer.at.war"), Helper.capitalize(clan.getName()), Helper.capitalize(war.getColorTag())));
                } else {
                    war.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("denied.war.end"), Helper.capitalize(denies.get(0)), clan.getName()));
                    clan.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("end.war.denied"), Helper.capitalize(war.getName())));
                }
            }
        } else if (req.getType().equals(ClanRequest.CREATE_ALLY)) {
            Clan clan = req.getClan();
            Clan ally = plugin.getClanManager().getClan(req.getTarget());
            ClanPlayer cp = req.getRequester();

            if (ally != null && clan != null) {
                List<String> accepts = req.getAccepts();
                List<String> denies = req.getDenies();

                if (!accepts.isEmpty()) {
                    clan.addAlly(ally);

                    ally.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("accepted.an.alliance"), Helper.capitalize(accepts.get(0)), clan.getName()));
                    clan.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("created.an.alliance"), Helper.capitalize(cp.getName()), Helper.capitalize(ally.getName())));
                } else {
                    ally.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("denied.an.alliance"), Helper.capitalize(denies.get(0)), clan.getName()));
                    clan.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("the.alliance.was.denied"), Helper.capitalize(ally.getName())));
                }
            }
        } else if (req.getType().equals(ClanRequest.BREAK_RIVALRY)) {
            Clan clan = req.getClan();
            Clan rival = plugin.getClanManager().getClan(req.getTarget());
            ClanPlayer cp = req.getRequester();

            if (rival != null && clan != null) {
                List<String> accepts = req.getAccepts();
                List<String> denies = req.getDenies();

                if (!accepts.isEmpty()) {
                    clan.removeRival(rival);
                    rival.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("broken.the.rivalry"), Helper.capitalize(accepts.get(0)), clan.getName()));
                    clan.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("broken.the.rivalry.with"), Helper.capitalize(cp.getName()), Helper.capitalize(rival.getName())));
                } else {
                    rival.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("denied.to.make.peace"), Helper.capitalize(denies.get(0)), clan.getName()));
                    clan.addBb(cp.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("peace.agreement.denied"), Helper.capitalize(rival.getName())));
                }
            }
        } else if (req.votingFinished()) {
            List<String> denies = req.getDenies();

            if (req.getType().equals(ClanRequest.DEMOTE)) {
                Clan clan = req.getClan();
                String demoted = req.getTarget();
                UUID demotedUniqueId = UUIDMigration.getForcedPlayerUUID(demoted);

                if (SimpleClans.getInstance().hasUUID() && demotedUniqueId != null) {
                    return;
                }

                if (denies.isEmpty()) {
                    clan.addBb(plugin.getLang("leaders"), ChatColor.AQUA + MessageFormat.format(plugin.getLang("demoted.back.to.member"), Helper.capitalize(demoted)));
                    if (SimpleClans.getInstance().hasUUID()) {
                        clan.demote(demotedUniqueId);
                    } else {
                        clan.demote(demoted);
                    }
                } else {
                    String deniers = Helper.capitalize(Helper.toMessage(Helper.toArray(denies), ", "));
                    clan.leaderAnnounce(ChatColor.RED + MessageFormat.format(plugin.getLang("denied.demotion"), deniers, demoted));
                }
            } else if (req.getType().equals(ClanRequest.PROMOTE)) {
                Clan clan = req.getClan();
                String promoted = req.getTarget();
                UUID promotedUniqueId = UUIDMigration.getForcedPlayerUUID(promoted);

                if (SimpleClans.getInstance().hasUUID() && promotedUniqueId == null) {
                    return;
                }
                if (denies.isEmpty()) {
                    clan.addBb(plugin.getLang("leaders"), ChatColor.AQUA + MessageFormat.format(plugin.getLang("promoted.to.leader"), Helper.capitalize(promoted)));
                    if (SimpleClans.getInstance().hasUUID()) {
                        clan.promote(promotedUniqueId);
                    } else {
                        clan.promote(promoted);
                    }
                } else {
                    String deniers = Helper.capitalize(Helper.toMessage(Helper.toArray(denies), ", "));
                    clan.leaderAnnounce(ChatColor.RED + MessageFormat.format(plugin.getLang("denied.the.promotion"), deniers, promoted));
                }
            } else if (req.getType().equals(ClanRequest.DISBAND)) {
                Clan clan = req.getClan();

                if (denies.isEmpty()) {
                    clan.addBb(plugin.getLang("leaders"), ChatColor.AQUA + MessageFormat.format(plugin.getLang("has.been.disbanded"), clan.getName()));
                    clan.disband();
                } else {
                    String deniers = Helper.capitalize(Helper.toMessage(Helper.toArray(denies), ", "));
                    clan.leaderAnnounce(ChatColor.RED + MessageFormat.format(plugin.getLang("clan.deletion"), deniers));
                }
            }

            req.cleanVotes();
        }

        requests.remove(req.getTarget());
        SimpleClans.getInstance().getServer().getPluginManager().callEvent(new RequestFinishedEvent(req));
    }

    /**
     * End a pending request prematurely
     *
     * @param playerName
     * @return
     */
    public boolean endPendingRequest(String playerName) {
        for (Request req : new LinkedList<Request>(requests.values())) {
            for (ClanPlayer cp : req.getAcceptors()) {
                if (cp.getName().equalsIgnoreCase(playerName)) {
                    req.getClan().leaderAnnounce(MessageFormat.format(plugin.getLang("signed.off.request.cancelled"), ChatColor.RED + Helper.capitalize(playerName), req.getType()));
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
    public void askerTask() {
        plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                for (Iterator<Map.Entry<String, Request>> iter = requests.entrySet().iterator(); iter.hasNext(); ) {
                    Request req = iter.next().getValue();

                    if (req == null) {
                        continue;
                    }

                    if (req.reachedRequestLimit()) {
                        iter.remove();
                    }

                    ask(req);
                    req.incrementAskCount();
                }
            }
        }, 0, plugin.getSettingsManager().getRequestFreqencySecs() * 20L);
    }

    /**
     * Asks a request to players for votes
     *
     * @param req
     */
    public void ask(Request req) {
        final String tag = plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketLeft() + plugin.getSettingsManager().getTagDefaultColor() + req.getClan().getColorTag() + plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketRight();
        final String message = tag + " " + plugin.getSettingsManager().getRequestMessageColor() + req.getMsg();
        final String options = MessageFormat.format(plugin.getLang("accept.or.deny"), ChatBlock.makeEmpty(Helper.stripColors(tag)) + " " + ChatColor.DARK_GREEN + "/" + plugin.getSettingsManager().getCommandAccept() + plugin.getSettingsManager().getPageHeadingsColor(), ChatColor.DARK_RED + "/" + plugin.getSettingsManager().getCommandDeny());

        if (req.getType().equals(ClanRequest.INVITE)) {
            Player player = Helper.getPlayer(req.getTarget());

            if (player != null) {
                ChatBlock.sendBlank(player);
                ChatBlock.sendMessage(player, message);
                ChatBlock.sendMessage(player, options);
                ChatBlock.sendBlank(player);
            }
        } else {
            for (ClanPlayer cp : req.getAcceptors()) {
                if (cp.getVote() == null) {
                    Player player = cp.toPlayer();

                    if (player != null) {
                        ChatBlock.sendBlank(player);
                        ChatBlock.sendMessage(player, message);
                        ChatBlock.sendMessage(player, options);
                        ChatBlock.sendBlank(player);
                    }
                }
            }
        }

        SimpleClans.getInstance().getServer().getPluginManager().callEvent(new RequestEvent(req));
    }
}
