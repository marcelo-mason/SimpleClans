package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.util.logging.Level;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.AlliancesCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.AllyCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.BanCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.BbCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.CapeCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.ClanffCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.CoordsCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.CreateCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.DemoteCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.DisbandCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.FfCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.GlobalffCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.InviteCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.KickCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.LeaderboardCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.ListCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.LookupCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.MenuCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.ModtagCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.ProfileCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.PromoteCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.ReloadCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.ResignCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.RivalCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.RivalriesCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.RosterCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.StatsCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.TrustCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.UnbanCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.UntrustCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.VerifyCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.clan.VitalsCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public class ClanCommand implements CommandExecutor
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

    /**
     *
     */
    public ClanCommand()
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
    }

    /**
     *
     * @param sender
     * @param command
     * @param label
     * @param args
     * @return
     */
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
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
                    ChatBlock.sendMessage(player, ChatColor.RED + "You are banned from using clan commands");
                    return true;
                }

                if (args.length == 0)
                {
                    menuCommand.execute(player);
                }
                else
                {
                    String subcommand = args[0];
                    String[] subargs = Helper.removeFirst(args);

                    if (subcommand.equalsIgnoreCase("create"))
                    {
                        createCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("list"))
                    {
                        listCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("profile"))
                    {
                        profileCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("roster"))
                    {
                        rosterCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("lookup"))
                    {
                        lookupCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("leaderboard"))
                    {
                        leaderboardCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("alliances"))
                    {
                        alliancesCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("rivalries"))
                    {
                        rivalriesCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("vitals"))
                    {
                        vitalsCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("coords"))
                    {
                        coordsCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("stats"))
                    {
                        statsCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("ally"))
                    {
                        allyCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("rival"))
                    {
                        rivalCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("bb"))
                    {
                        bbCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("modtag"))
                    {
                        modtagCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("cape"))
                    {
                        capeCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("invite"))
                    {
                        inviteCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("kick"))
                    {
                        kickCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("trust"))
                    {
                        trustCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("untrust"))
                    {
                        untrustCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("promote"))
                    {
                        promoteCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("demote"))
                    {
                        demoteCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("clanff"))
                    {
                        clanffCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("ff"))
                    {
                        ffCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("resign"))
                    {
                        resignCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("disband"))
                    {
                        disbandCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("verify"))
                    {
                        verifyCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("ban"))
                    {
                        banCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("unban"))
                    {
                        unbanCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("reload"))
                    {
                        reloadCommand.execute(player, subargs);
                    }
                    else if (subcommand.equalsIgnoreCase("globalff"))
                    {
                        globalffCommand.execute(player, subargs);
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "Does not match a clan command");
                    }
                }
                return true;
            }
        }
        catch (Exception ex)
        {
            SimpleClans.log(Level.SEVERE, "SimpleClans command failure: {0}", ex.getMessage());
        }

        return false;
    }
}
