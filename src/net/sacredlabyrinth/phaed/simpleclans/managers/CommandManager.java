package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.commands.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

/**
 * @author phaed
 */
public final class CommandManager
{
    private SimpleClans plugin;
    private CreateCommand createCommand;
    private ListCommand listCommand;
    private ProfileCommand profileCommand;
    private RosterCommand rosterCommand;
    private LookupCommand lookupCommand;
    private LeaderboardCommand leaderboardCommand;
    private AlliancesCommand alliancesCommand;
    private RivalriesCommand rivalriesCommand;
    private VitalsCommand vitalsCommand;
    private CoordsCommand coordsCommand;
    private StatsCommand statsCommand;
    private AllyCommand allyCommand;
    private RivalCommand rivalCommand;
    private BbCommand bbCommand;
    private ModtagCommand modtagCommand;
    private CapeCommand capeCommand;
    private InviteCommand inviteCommand;
    private KickCommand kickCommand;
    private TrustCommand trustCommand;
    private UntrustCommand untrustCommand;
    private PromoteCommand promoteCommand;
    private DemoteCommand demoteCommand;
    private ClanffCommand clanffCommand;
    private FfCommand ffCommand;
    private ResignCommand resignCommand;
    private DisbandCommand disbandCommand;
    private VerifyCommand verifyCommand;
    private BanCommand banCommand;
    private UnbanCommand unbanCommand;
    private ReloadCommand reloadCommand;
    private GlobalffCommand globalffCommand;
    private MenuCommand menuCommand;
    private WarCommand warCommand;
    private HomeCommand homeCommand;

    /**
     *
     */
    public CommandManager()
    {
        plugin = SimpleClans.getInstance();
        menuCommand = new MenuCommand();
        createCommand = new CreateCommand();
        listCommand = new ListCommand();
        profileCommand = new ProfileCommand();
        rosterCommand = new RosterCommand();
        lookupCommand = new LookupCommand();
        leaderboardCommand = new LeaderboardCommand();
        alliancesCommand = new AlliancesCommand();
        rivalriesCommand = new RivalriesCommand();
        vitalsCommand = new VitalsCommand();
        coordsCommand = new CoordsCommand();
        statsCommand = new StatsCommand();
        allyCommand = new AllyCommand();
        rivalCommand = new RivalCommand();
        bbCommand = new BbCommand();
        modtagCommand = new ModtagCommand();
        capeCommand = new CapeCommand();
        inviteCommand = new InviteCommand();
        kickCommand = new KickCommand();
        trustCommand = new TrustCommand();
        untrustCommand = new UntrustCommand();
        promoteCommand = new PromoteCommand();
        demoteCommand = new DemoteCommand();
        clanffCommand = new ClanffCommand();
        ffCommand = new FfCommand();
        resignCommand = new ResignCommand();
        disbandCommand = new DisbandCommand();
        verifyCommand = new VerifyCommand();
        banCommand = new BanCommand();
        unbanCommand = new UnbanCommand();
        reloadCommand = new ReloadCommand();
        globalffCommand = new GlobalffCommand();
        warCommand = new WarCommand();
        homeCommand = new HomeCommand();
    }

    /**
     * @param args
     * @return
     */
    public void processClan(Player player, String[] args)
    {
        try
        {
            if (plugin.getSettingsManager().isBlacklistedWorld(player.getLocation().getWorld().getName()))
            {
                return;
            }

            if (plugin.getSettingsManager().isBanned(player.getName()))
            {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("banned"));
                return;
            }

            if (args.length == 0)
            {
                menuCommand.execute(player);
            }
            else
            {
                String subcommand = args[0];
                String[] subargs = Helper.removeFirst(args);

                if (subcommand.equalsIgnoreCase(plugin.getLang().getString("create.command")))
                {
                    createCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("list.command")))
                {
                    listCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("profile.command")))
                {
                    profileCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("roster.command")))
                {
                    rosterCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("lookup.command")))
                {
                    lookupCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("home.command")))
                {
                    homeCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("leaderboard.command")))
                {
                    leaderboardCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("alliances.command")))
                {
                    alliancesCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("rivalries.command")))
                {
                    rivalriesCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("vitals.command")))
                {
                    vitalsCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("coords.command")))
                {
                    coordsCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("stats.command")))
                {
                    statsCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("ally.command")))
                {
                    allyCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("rival.command")))
                {
                    rivalCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("bb.command")))
                {
                    bbCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("modtag.command")))
                {
                    modtagCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("cape.command")))
                {
                    capeCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("invite.command")))
                {
                    inviteCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("kick.command")))
                {
                    kickCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("trust.command")))
                {
                    trustCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("untrust.command")))
                {
                    untrustCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("promote.command")))
                {
                    promoteCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("demote.command")))
                {
                    demoteCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("clanff.command")))
                {
                    clanffCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("ff.command")))
                {
                    ffCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("resign.command")))
                {
                    resignCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("disband.command")))
                {
                    disbandCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("verify.command")))
                {
                    verifyCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("ban.command")))
                {
                    banCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("unban.command")))
                {
                    unbanCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("reload.command")))
                {
                    reloadCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("globalff.command")))
                {
                    globalffCommand.execute(player, subargs);
                }
                else if (subcommand.equalsIgnoreCase(plugin.getLang().getString("war.command")))
                {
                    warCommand.execute(player, subargs);
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("does.not.match"));
                }
            }
        }
        catch (Exception ex)
        {
            SimpleClans.log(ChatColor.RED + MessageFormat.format(plugin.getLang().getString("simpleclans.command.failure"), ex.getMessage()));
            for(StackTraceElement el : ex.getStackTrace())
            {
                System.out.print(el.toString());
            }
        }
    }

    /**
     * Process the accept command
     *
     * @param player
     */
    public void processAccept(Player player)
    {
        if (plugin.getSettingsManager().isBanned(player.getName()))
        {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("banned"));
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp != null)
        {
            Clan clan = cp.getClan();

            if (clan.isLeader(player))
            {
                if (plugin.getRequestManager().hasRequest(clan.getTag()))
                {
                    if (cp.getVote() == null)
                    {
                        plugin.getRequestManager().accept(cp);
                        clan.leaderAnnounce(ChatColor.GREEN + MessageFormat.format(plugin.getLang().getString("voted.to.accept"), Helper.capitalize(player.getName())));
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("you.have.already.voted"));
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("nothing.to.accept"));
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("no.leader.permissions"));
            }
        }
        else
        {
            if (plugin.getRequestManager().hasRequest(player.getName().toLowerCase()))
            {
                cp = plugin.getClanManager().getCreateClanPlayer(player.getName());
                plugin.getRequestManager().accept(cp);
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("nothing.to.accept"));
            }
        }
    }

    /**
     * Process the deny command
     *
     * @param player
     */
    public void processDeny(Player player)
    {
        if (plugin.getSettingsManager().isBanned(player.getName()))
        {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("banned"));
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp != null)
        {
            Clan clan = cp.getClan();

            if (clan.isLeader(player))
            {
                if (plugin.getRequestManager().hasRequest(clan.getTag()))
                {
                    if (cp.getVote() == null)
                    {
                        plugin.getRequestManager().deny(cp);
                        clan.leaderAnnounce(ChatColor.RED + MessageFormat.format(plugin.getLang().getString("has.voted.to.deny"), Helper.capitalize(player.getName())));
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("you.have.already.voted"));
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("nothing.to.deny"));
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("no.leader.permissions"));
            }
        }
        else
        {
            if (plugin.getRequestManager().hasRequest(player.getName().toLowerCase()))
            {
                cp = plugin.getClanManager().getCreateClanPlayer(player.getName());
                plugin.getRequestManager().deny(cp);
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("nothing.to.deny"));
            }
        }
    }

    /**
     * Process the more command
     *
     * @param player
     */
    public void processMore(Player player)
    {
        if (plugin.getSettingsManager().isBanned(player.getName()))
        {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("banned"));
            return;
        }

        ChatBlock chatBlock = plugin.getStorageManager().getChatBlock(player);

        if (chatBlock != null && chatBlock.size() > 0)
        {
            chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

            if (chatBlock.size() > 0)
            {
                ChatBlock.sendBlank(player);
                ChatBlock.sendMessage(player, plugin.getSettingsManager().getPageHeadingsColor() + MessageFormat.format(plugin.getLang().getString("view.next.page"), plugin.getSettingsManager().getCommandMore()));
            }
            ChatBlock.sendBlank(player);
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("nothing.more.to.see"));
        }
    }

    public CreateCommand getCreateCommand()
    {
        return createCommand;
    }

    public ListCommand getListCommand()
    {
        return listCommand;
    }

    public ProfileCommand getProfileCommand()
    {
        return profileCommand;
    }

    public RosterCommand getRosterCommand()
    {
        return rosterCommand;
    }

    public LookupCommand getLookupCommand()
    {
        return lookupCommand;
    }

    public LeaderboardCommand getLeaderboardCommand()
    {
        return leaderboardCommand;
    }

    public AlliancesCommand getAlliancesCommand()
    {
        return alliancesCommand;
    }

    public RivalriesCommand getRivalriesCommand()
    {
        return rivalriesCommand;
    }

    public VitalsCommand getVitalsCommand()
    {
        return vitalsCommand;
    }

    public CoordsCommand getCoordsCommand()
    {
        return coordsCommand;
    }

    public StatsCommand getStatsCommand()
    {
        return statsCommand;
    }

    public AllyCommand getAllyCommand()
    {
        return allyCommand;
    }

    public RivalCommand getRivalCommand()
    {
        return rivalCommand;
    }

    public BbCommand getBbCommand()
    {
        return bbCommand;
    }

    public ModtagCommand getModtagCommand()
    {
        return modtagCommand;
    }

    public CapeCommand getCapeCommand()
    {
        return capeCommand;
    }

    public InviteCommand getInviteCommand()
    {
        return inviteCommand;
    }

    public KickCommand getKickCommand()
    {
        return kickCommand;
    }

    public TrustCommand getTrustCommand()
    {
        return trustCommand;
    }

    public UntrustCommand getUntrustCommand()
    {
        return untrustCommand;
    }

    public PromoteCommand getPromoteCommand()
    {
        return promoteCommand;
    }

    public DemoteCommand getDemoteCommand()
    {
        return demoteCommand;
    }

    public ClanffCommand getClanffCommand()
    {
        return clanffCommand;
    }

    public FfCommand getFfCommand()
    {
        return ffCommand;
    }

    public ResignCommand getResignCommand()
    {
        return resignCommand;
    }

    public DisbandCommand getDisbandCommand()
    {
        return disbandCommand;
    }

    public VerifyCommand getVerifyCommand()
    {
        return verifyCommand;
    }

    public BanCommand getBanCommand()
    {
        return banCommand;
    }

    public UnbanCommand getUnbanCommand()
    {
        return unbanCommand;
    }

    public ReloadCommand getReloadCommand()
    {
        return reloadCommand;
    }

    public GlobalffCommand getGlobalffCommand()
    {
        return globalffCommand;
    }

    public MenuCommand getMenuCommand()
    {
        return menuCommand;
    }

    public WarCommand getWarCommand()
    {
        return warCommand;
    }
}
