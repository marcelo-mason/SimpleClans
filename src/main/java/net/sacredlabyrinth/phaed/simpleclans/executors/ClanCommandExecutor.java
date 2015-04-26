package net.sacredlabyrinth.phaed.simpleclans.executors;

import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.commands.*;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

/**
 * @author phaed
 */
public final class ClanCommandExecutor implements CommandExecutor
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
    private ToggleCommand toggleCommand;
    private InviteCommand inviteCommand;
    private KickCommand kickCommand;
    private TrustCommand trustCommand;
    private UntrustCommand untrustCommand;
    private PromoteCommand promoteCommand;
    private CapeCommand capeCommand;
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
    private KillsCommand killsCommand;
    private MostKilledCommand mostKilledCommand;
    private SetRankCommand setRankCommand;
    private BankCommand bankCommand;
    private PlaceCommand placeCommand;

    /**
     *
     */
    public ClanCommandExecutor()
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
        toggleCommand = new ToggleCommand();
        inviteCommand = new InviteCommand();
        kickCommand = new KickCommand();
        trustCommand = new TrustCommand();
        untrustCommand = new UntrustCommand();
        promoteCommand = new PromoteCommand();
        capeCommand = new CapeCommand();
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
        killsCommand = new KillsCommand();
        mostKilledCommand = new MostKilledCommand();
        setRankCommand = new SetRankCommand();
        bankCommand = new BankCommand();
        placeCommand = new PlaceCommand();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args)
    {
        try
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;

                if (plugin.getSettingsManager().isBlacklistedWorld(player.getLocation().getWorld().getName()))
                {
                    return false;
                }

                if (plugin.getSettingsManager().isBanned(player.getName()))
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("banned"));
                    return false;
                }

                if (args.length == 0)
                {
                    menuCommand.execute(player);
                }
                else
                {
                    String subcommand = args[0];
                    String[] subargs = Helper.removeFirst(args);

                    if (subcommand.equalsIgnoreCase(plugin.getLang("create.command")))
                    {
                        createCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("list.command")))
                    {
                        listCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("bank.command")))
                    {
                        bankCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("profile.command")))
                    {
                        profileCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("roster.command")))
                    {
                        rosterCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("lookup.command")))
                    {
                        lookupCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("home.command")))
                    {
                        homeCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("leaderboard.command")))
                    {
                        leaderboardCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("alliances.command")))
                    {
                        alliancesCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("rivalries.command")))
                    {
                        rivalriesCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("vitals.command")))
                    {
                        vitalsCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("coords.command")))
                    {
                        coordsCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("stats.command")))
                    {
                        statsCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("ally.command")))
                    {
                        allyCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("rival.command")))
                    {
                        rivalCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("bb.command")))
                    {
                        bbCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("modtag.command")))
                    {
                        modtagCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("toggle.command")))
                    {
                        toggleCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("cape.command")))
                    {
                        capeCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("invite.command")))
                    {
                        inviteCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("kick.command")))
                    {
                        kickCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("trust.command")))
                    {
                        trustCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("untrust.command")))
                    {
                        untrustCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("promote.command")))
                    {
                        promoteCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("demote.command")))
                    {
                        demoteCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("clanff.command")))
                    {
                        clanffCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("ff.command")))
                    {
                        ffCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("resign.command")))
                    {
                        resignCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("disband.command")))
                    {
                        disbandCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("verify.command")))
                    {
                        verifyCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("ban.command")))
                    {
                        banCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("unban.command")))
                    {
                        unbanCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("reload.command")))
                    {
                        reloadCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("globalff.command")))
                    {
                        globalffCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("war.command")))
                    {
                        warCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("kills.command")))
                    {
                        killsCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("mostkilled.command")))
                    {
                        mostKilledCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("setrank.command")))
                    {
                        setRankCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("place.command")))
                    {
                        placeCommand.execute(player, subargs);
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("does.not.match"));
                    }
                }
            }
            else
            {
                if (args.length == 0)
                {
                    menuCommand.executeSender(sender);
                }
                else
                {
                    String subcommand = args[0];
                    String[] subargs = Helper.removeFirst(args);

                    if (subcommand.equalsIgnoreCase(plugin.getLang("verify.command")))
                    {
                        verifyCommand.execute(sender, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("reload.command")))
                    {
                        reloadCommand.execute(sender, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase(plugin.getLang("place.command")))
                    {
                        placeCommand.execute(sender, subargs);
                    }
                    else
                    {
                        ChatBlock.sendMessage(sender, ChatColor.RED + plugin.getLang("does.not.match"));
                    }
                }
            }
        }
        catch (Exception ex)
        {
            SimpleClans.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.RED + MessageFormat.format(plugin.getLang("simpleclans.command.failure"), ex.getMessage()));
            for (StackTraceElement el : ex.getStackTrace())
            {
                System.out.print(el.toString());
            }
        }

        return false;
    }
}
