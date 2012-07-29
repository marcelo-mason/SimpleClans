package net.sacredlabyrinth.phaed.simpleclans;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sacredlabyrinth.phaed.simpleclans.Metrics.Graph;
import net.sacredlabyrinth.phaed.simpleclans.commands.*;
import net.sacredlabyrinth.phaed.simpleclans.listeners.SCClaimingListener;
import net.sacredlabyrinth.phaed.simpleclans.listeners.SCEntityListener;
import net.sacredlabyrinth.phaed.simpleclans.listeners.SCPlayerListener;
import net.sacredlabyrinth.phaed.simpleclans.managers.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
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
    private AutoUpdate autoUpdate;
    private static ConsoleCommandSender log;
    private boolean spout;

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
            log.sendMessage(msg);
        } else {
            log.sendMessage(MessageFormat.format(msg, arg));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        commandManager.executeAll(null, sender, command.getName(), label, args);
        return true;
    }

    @Override
    public void onEnable()
    {
        long start = System.currentTimeMillis();
        logger = getLogger();
        log = getServer().getConsoleSender();
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
        commandManager = new CommandManager(this);
        commandManager.addCommand(new CreateCommand(this));
        commandManager.addCommand(new VerifyCommand(this));
        commandManager.addCommand(new ListCommand(this));
        commandManager.addCommand(new ProfileCommand(this));
        commandManager.addCommand(new LookupCommand(this));
        commandManager.addCommand(new LeaderboardCommand(this));
        commandManager.addCommand(new AlliancesCommand(this));
        commandManager.addCommand(new RivalriesCommand(this));
        commandManager.addCommand(new RosterCommand(this));
        commandManager.addCommand(new VitalsCommand(this));
        commandManager.addCommand(new CoordsCommand(this));
        commandManager.addCommand(new StatsCommand(this));
        commandManager.addCommand(new StrifesCommand(this));
        commandManager.addCommand(new KillsCommand(this));
        commandManager.addCommand(new BankCommand(this));
        commandManager.addCommand(new AllyCommand(this));
        commandManager.addCommand(new RivalCommand(this));
        commandManager.addCommand(new HomeCommand(this));
        commandManager.addCommand(new WarCommand(this));
        if (settingsManager.isClaimingEnabled()) {
            commandManager.addCommand(new ClaimCommand(this));
            commandManager.addCommand(new MapCommand(this));
            commandManager.addCommand(new UnClaimCommand(this));
        }
        commandManager.addCommand(new BbCommand(this));
        commandManager.addCommand(new ModtagCommand(this));
        commandManager.addCommand(new ToggleCommand(this));
        commandManager.addCommand(new InviteCommand(this));
        commandManager.addCommand(new KickCommand(this));
        commandManager.addCommand(new SetRankCommand(this));
        commandManager.addCommand(new TrustCommand(this));
        commandManager.addCommand(new UntrustCommand(this));
        commandManager.addCommand(new PromoteCommand(this));
        commandManager.addCommand(new DemoteCommand(this));
        commandManager.addCommand(new ClanffCommand(this));
        commandManager.addCommand(new FfCommand(this));
        commandManager.addCommand(new ResignCommand(this));
        commandManager.addCommand(new DisbandCommand(this));
        commandManager.addCommand(new VerifyModCommand(this));
        commandManager.addCommand(new MostKilledCommand(this));
        commandManager.addCommand(new DisbandCommand(this));
        commandManager.addCommand(new BanCommand(this));
        commandManager.addCommand(new GlobalffCommand(this));
        commandManager.addCommand(new ReloadCommand(this));
        commandManager.addCommand(new HelpCommand(this));
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
