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
public final class ClanCommandExecutor implements CommandExecutor {
    private final SimpleClans plugin;
    private final CreateCommand createCommand;
    private final ListCommand listCommand;
    private final ProfileCommand profileCommand;
    private final RosterCommand rosterCommand;
    private final LookupCommand lookupCommand;
    private final LeaderboardCommand leaderboardCommand;
    private final AlliancesCommand alliancesCommand;
    private final RivalriesCommand rivalriesCommand;
    private final VitalsCommand vitalsCommand;
    private final CoordsCommand coordsCommand;
    private final StatsCommand statsCommand;
    private final AllyCommand allyCommand;
    private final RivalCommand rivalCommand;
    private final BbCommand bbCommand;
    private final ModtagCommand modtagCommand;
    private final ToggleCommand toggleCommand;
    private final InviteCommand inviteCommand;
    private final KickCommand kickCommand;
    private final TrustCommand trustCommand;
    private final UntrustCommand untrustCommand;
    private final PromoteCommand promoteCommand;
    private final DemoteCommand demoteCommand;
    private final ClanffCommand clanffCommand;
    private final FfCommand ffCommand;
    private final ResignCommand resignCommand;
    private final DisbandCommand disbandCommand;
    private final VerifyCommand verifyCommand;
    private final BanCommand banCommand;
    private final FeeCommand feeCommand;
    private final UnbanCommand unbanCommand;
    private final ReloadCommand reloadCommand;
    private final GlobalffCommand globalffCommand;
    private final MenuCommand menuCommand;
    private final WarCommand warCommand;
    private final HomeCommand homeCommand;
    private final KillsCommand killsCommand;
    private final MostKilledCommand mostKilledCommand;
    private final SetRankCommand setRankCommand;
    private final BankCommand bankCommand;
    private final PlaceCommand placeCommand;
    private final ResetKDRCommand resetKDRCommand;
    private final PurgeCommand purgeCommand;
    private final DescriptionCommand descriptionCommand;
    private final RankCommand rankCommand;

    /**
     *
     */
    public ClanCommandExecutor() {
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
        resetKDRCommand = new ResetKDRCommand();
        feeCommand = new FeeCommand();
        purgeCommand = new PurgeCommand();
        descriptionCommand = new DescriptionCommand();
        rankCommand = new RankCommand();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        try {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (plugin.getSettingsManager().isBlacklistedWorld(player.getLocation().getWorld().getName())) {
                    return false;
                }

                if (plugin.getSettingsManager().isBanned(player.getUniqueId())) {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("banned"));
                    return false;
                }

                if (args.length == 0) {
                    menuCommand.execute(player);
                } else {
                    String subcommand = args[0];
                    // TODO if
                    String[] subargs = Helper.removeFirst(args);

                    if (subcommand.equalsIgnoreCase(plugin.getLang("create.command"))) {
                        createCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("list.command")) || subcommand.equalsIgnoreCase("list")) {
                        listCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("bank.command")) || subcommand.equalsIgnoreCase("bank")) {
                        bankCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("profile.command")) || subcommand.equalsIgnoreCase("profile")) {
                        profileCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("roster.command")) || subcommand.equalsIgnoreCase("roster")) {
                        rosterCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("lookup.command")) || subcommand.equalsIgnoreCase("lookup")) {
                        lookupCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("home.command")) || subcommand.equalsIgnoreCase("home")) {
                        homeCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("leaderboard.command")) || subcommand.equalsIgnoreCase("leaderboard")) {
                        leaderboardCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("alliances.command")) || subcommand.equalsIgnoreCase("alliances")) {
                        alliancesCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("rivalries.command")) || subcommand.equalsIgnoreCase("rivalries")) {
                        rivalriesCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("vitals.command")) || subcommand.equalsIgnoreCase("vitals")) {
                        vitalsCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("coords.command")) || subcommand.equalsIgnoreCase("coords")) {
                        coordsCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("stats.command")) || subcommand.equalsIgnoreCase("stats")) {
                        statsCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("ally.command")) || subcommand.equalsIgnoreCase("ally")) {
                        allyCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("rival.command")) || subcommand.equalsIgnoreCase("rival")) {
                        rivalCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("bb.command")) || subcommand.equalsIgnoreCase("bb")) {
                        bbCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("modtag.command")) || subcommand.equalsIgnoreCase("modtag")) {
                        modtagCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("toggle.command")) || subcommand.equalsIgnoreCase("toggle")) {
                        toggleCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("invite.command")) || subcommand.equalsIgnoreCase("invite")) {
                        inviteCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("kick.command")) || subcommand.equalsIgnoreCase("kick")) {
                        kickCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("trust.command")) || subcommand.equalsIgnoreCase("trust")) {
                        trustCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("untrust.command")) || subcommand.equalsIgnoreCase("unstrust")) {
                        untrustCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("promote.command")) || subcommand.equalsIgnoreCase("promote")) {
                        promoteCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("demote.command")) || subcommand.equalsIgnoreCase("demote")) {
                        demoteCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("clanff.command")) || subcommand.equalsIgnoreCase("canff")) {
                        clanffCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("ff.command")) || subcommand.equalsIgnoreCase("ff")) {
                        ffCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("resign.command")) || subcommand.equalsIgnoreCase("resign")) {
                        resignCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("disband.command")) || subcommand.equalsIgnoreCase("disband")) {
                        disbandCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("verify.command")) || subcommand.equalsIgnoreCase("verify")) {
                        verifyCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("ban.command")) || subcommand.equalsIgnoreCase("ban")) {
                        banCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("unban.command")) || subcommand.equalsIgnoreCase("unban")) {
                        unbanCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("reload.command")) || subcommand.equalsIgnoreCase("reload")) {
                        reloadCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("globalff.command")) || subcommand.equalsIgnoreCase("globalff")) {
                        globalffCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("war.command")) || subcommand.equalsIgnoreCase("war")) {
                        warCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("kills.command")) || subcommand.equalsIgnoreCase("kills")) {
                        killsCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("mostkilled.command")) || subcommand.equalsIgnoreCase("mostkilled")) {
                        mostKilledCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("setrank.command")) || subcommand.equalsIgnoreCase("setrank")) {
                        setRankCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("place.command")) || subcommand.equalsIgnoreCase("place")) {
                        placeCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("resetkdr.command")) || subcommand.equalsIgnoreCase("resetkdr")) {
                        resetKDRCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("fee.command")) || subcommand.equalsIgnoreCase("fee")) {
                        feeCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("purge.command")) || subcommand.equalsIgnoreCase("purge")){
                        purgeCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("description.command")) || subcommand.equalsIgnoreCase("description")) {
                    	descriptionCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("rank.command")) || subcommand.equalsIgnoreCase("rank")) {
                    	rankCommand.execute(player, subargs);
                    } else {   
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("does.not.match"));
                    }
                }
            } else {
                if (args.length == 0) {
                    menuCommand.executeSender(sender);
                } else {
                    String subcommand = args[0];
                    String[] subargs = Helper.removeFirst(args);

                    if (subcommand.equalsIgnoreCase(plugin.getLang("verify.command")) || subcommand.equalsIgnoreCase("verify")) {
                        verifyCommand.execute(sender, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("reload.command")) || subcommand.equalsIgnoreCase("reload")) {
                        reloadCommand.execute(sender, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("place.command")) || subcommand.equalsIgnoreCase("place")) {
                        placeCommand.execute(sender, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("globalff.command")) || subcommand.equalsIgnoreCase("globalff")) {
                        globalffCommand.execute(sender, subargs);
                    } else if (subcommand.equalsIgnoreCase(plugin.getLang("purge.command")) || subcommand.equalsIgnoreCase("purge")) {
                        purgeCommand.execute(sender, subargs);
                    } else {
                        ChatBlock.sendMessage(sender, ChatColor.RED + plugin.getLang("does.not.match"));
                    }
                }
            }
        } catch (Exception ex) {
            SimpleClans.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.RED + MessageFormat.format(plugin.getLang("simpleclans.command.failure"), ex.getMessage()));
            for (StackTraceElement el : ex.getStackTrace()) {
                System.out.print(el.toString());
            }
        }

        return false;
    }
}
