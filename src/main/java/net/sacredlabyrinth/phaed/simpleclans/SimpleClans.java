package net.sacredlabyrinth.phaed.simpleclans;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sacredlabyrinth.phaed.simpleclans.Metrics.Graph;
import net.sacredlabyrinth.phaed.simpleclans.beta.BetaCommandManager;
import net.sacredlabyrinth.phaed.simpleclans.beta.HelpCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.*;
import net.sacredlabyrinth.phaed.simpleclans.listeners.SCClaimingListener;
import net.sacredlabyrinth.phaed.simpleclans.listeners.SCEntityListener;
import net.sacredlabyrinth.phaed.simpleclans.listeners.SCPlayerListener;
import net.sacredlabyrinth.phaed.simpleclans.managers.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Phaed
 */
public class SimpleClans extends JavaPlugin
{

    private static SimpleClans instance;
    private static Logger logger;
    private ClanManager clanManager;
    private RequestManager requestManager;
    private StorageManager storageManager;
    private SpoutPluginManager spoutPluginManager;
    private SettingsManager settingsManager;
    private PermissionsManager permissionsManager;
    private CommandManager commandManager;
    private TeleportManager teleportManager;
    private ResourceBundle lang;
    private BetaCommandManager betaCommandManager;
    private AutoUpdate autoUpdate;
    private boolean spout;

    /**
     * @return the logger
     */
    public static void debug(String msg)
    {
        if (getInstance().getSettingsManager().isDebugging()) {
            logger.log(Level.INFO, msg);
        }
    }

    public static void debug(Level level, String msg)
    {
        if (getInstance().getSettingsManager().isDebugging()) {
            logger.log(level, msg);
        }
    }

    public static void debug(String msg, Throwable ex)
    {
        logger.log(Level.SEVERE, msg, ex);
    }

    public static SimpleClans getInstance()
    {
        return instance;
    }

    public static void log(String msg, Object... arg)
    {
        if (arg == null || arg.length == 0) {
            logger.log(Level.INFO, msg);
        } else {
            logger.log(Level.INFO, new StringBuilder().append(MessageFormat.format(msg, arg)).toString());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        betaCommandManager.executeAll(null, sender, command.getName(), label, args);
        return true;
    }

    @Override
    public void onEnable()
    {
        long start = System.currentTimeMillis();
        logger = getLogger();
        instance = this;
        settingsManager = new SettingsManager();

        spout = checkSpout();

        lang = PropertyResourceBundle.getBundle("languages.lang");

        debug(MessageFormat.format(lang.getString("version.loaded"), getDescription().getName(), getDescription().getVersion()));


        if (hasSpout()) {
            spoutPluginManager = new SpoutPluginManager(this);
        }
        permissionsManager = new PermissionsManager(this);
        requestManager = new RequestManager(this);
        clanManager = new ClanManager(this);
        storageManager = new StorageManager(this);
        commandManager = new CommandManager(this);
        teleportManager = new TeleportManager(this);

        try {
            autoUpdate = new AutoUpdate(this, getConfig());
        } catch (Exception ex) {
            debug(null, ex);
        }

        PluginManager pm = getServer().getPluginManager();

        if (settingsManager.isClaimingEnabled()) {
            SCClaimingListener claimingListener = new SCClaimingListener(this);
            pm.registerEvents(claimingListener, this);
        }

        pm.registerEvents(new SCEntityListener(this), this);
        pm.registerEvents(new SCPlayerListener(this), this);

//        if (hasSpout()) {
//            spoutPluginManager.processAllPlayers();
//        }

        setupMetrics();
        setupBetaCommandManager();

        long end = System.currentTimeMillis();

        debug("Enabling took " + (end - start) + "ms.");
    }

    private void setupBetaCommandManager()
    {
        betaCommandManager = new BetaCommandManager(this);
        betaCommandManager.addCommand(new CreateCommand(this));
        betaCommandManager.addCommand(new VerifyCommand(this));
        betaCommandManager.addCommand(new ListCommand(this));
        betaCommandManager.addCommand(new ProfileCommand(this));
        betaCommandManager.addCommand(new LookupCommand(this));
        betaCommandManager.addCommand(new LeaderboardCommand(this));
        betaCommandManager.addCommand(new AlliancesCommand(this));
        betaCommandManager.addCommand(new RivalriesCommand(this));
        betaCommandManager.addCommand(new RosterCommand(this));
        betaCommandManager.addCommand(new VitalsCommand(this));
        betaCommandManager.addCommand(new CoordsCommand(this));
        betaCommandManager.addCommand(new StatsCommand(this));
        betaCommandManager.addCommand(new KillsCommand(this));
        betaCommandManager.addCommand(new AllyCommand(this));
        betaCommandManager.addCommand(new RivalCommand(this));
        betaCommandManager.addCommand(new HomeCommand(this));
        betaCommandManager.addCommand(new WarCommand(this));
        if (settingsManager.isClaimingEnabled()) {
            betaCommandManager.addCommand(new ClaimCommand(this));
            betaCommandManager.addCommand(new MapCommand(this));
            betaCommandManager.addCommand(new UnClaimCommand(this));
        }
        betaCommandManager.addCommand(new BbCommand(this));
        betaCommandManager.addCommand(new ModtagCommand(this));
        betaCommandManager.addCommand(new ToggleCommand(this));
        betaCommandManager.addCommand(new InviteCommand(this));
        betaCommandManager.addCommand(new KickCommand(this));
        betaCommandManager.addCommand(new SetRankCommand(this));
        betaCommandManager.addCommand(new TrustCommand(this));
        betaCommandManager.addCommand(new UntrustCommand(this));
        betaCommandManager.addCommand(new PromoteCommand(this));
        betaCommandManager.addCommand(new DemoteCommand(this));
        betaCommandManager.addCommand(new ClanffCommand(this));
        betaCommandManager.addCommand(new FfCommand(this));
        betaCommandManager.addCommand(new ResignCommand(this));
        betaCommandManager.addCommand(new DisbandCommand(this));
        betaCommandManager.addCommand(new VerifyModCommand(this));
        betaCommandManager.addCommand(new MostKilledCommand(this));
        betaCommandManager.addCommand(new DisbandCommand(this));
        betaCommandManager.addCommand(new BanCommand(this));
        betaCommandManager.addCommand(new GlobalffCommand(this));
        betaCommandManager.addCommand(new ReloadCommand(this));
        betaCommandManager.addCommand(new HelpCommand(this));
    }

    @Override
    public void onDisable()
    {
        getStorageManager().saveClaims();
        getServer().getScheduler().cancelTasks(this);
        getStorageManager().closeConnection();
    }

    public AutoUpdate getAutoUpdater()
    {
        return autoUpdate;
    }

    public void setupMetrics()
    {
        try {
            Metrics metrics = new Metrics(this);
            Graph clanGraph = metrics.createGraph("Clan Graph");
            Graph clanPlayerGraph = metrics.createGraph("Clan-Player Graph");

            clanGraph.addPlotter(new Metrics.Plotter("Total created clans")
            {

                @Override
                public int getValue()
                {
                    return getClanManager().getClans().size();
                }
            });

            clanPlayerGraph.addPlotter(new Metrics.Plotter("Total clan players")
            {

                @Override
                public int getValue()
                {
                    int cp = 0;
                    for (Clan clan : getClanManager().getClans()) {
                        cp += clan.getMembers().size();
                    }
                    return cp;
                }
            });

            metrics.start();
        } catch (IOException e) {
            log(e.getMessage());
        }
    }

    private boolean checkSpout()
    {
        Plugin spoutpl = getServer().getPluginManager().getPlugin("Spout");

        if (spoutpl != null) {
            SimpleClans.debug(String.format("Hooked Spout %s", spoutpl.getDescription().getVersion()));
            return true;
        }
        return false;
    }

    public boolean hasSpout()
    {
        return spout;
    }

    /**
     * @return the clanManager
     */
    public ClanManager getClanManager()
    {
        return clanManager;
    }

    /**
     * @return the requestManager
     */
    public RequestManager getRequestManager()
    {
        return requestManager;
    }

    /**
     * @return the storageManager
     */
    public StorageManager getStorageManager()
    {
        return storageManager;
    }

    public BetaCommandManager getBetaCommandManager()
    {
        return betaCommandManager;
    }

    /**
     * Retruns the SpoutManager of if spout isnt installed null
     *
     * @return the spoutManager
     */
    public SpoutPluginManager getSpoutPluginManager()
    {
        return spoutPluginManager;
    }

    /**
     * @return the settingsManager
     */
    public SettingsManager getSettingsManager()
    {
        return settingsManager;
    }

    /**
     * @return the permissionsManager
     */
    public PermissionsManager getPermissionsManager()
    {
        return permissionsManager;
    }

    /**
     * @return the commandManager
     */
    public CommandManager getCommandManager()
    {
        return commandManager;
    }

    /**
     * @return the lang
     */
    public String getLang(String msg)
    {
        return Helper.parseColors(lang.getString(msg));
    }

    public TeleportManager getTeleportManager()
    {
        return teleportManager;
    }
}
