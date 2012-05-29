package net.sacredlabyrinth.phaed.simpleclans;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sacredlabyrinth.phaed.simpleclans.Metrics.Graph;
import net.sacredlabyrinth.phaed.simpleclans.listeners.SCEntityListener;
import net.sacredlabyrinth.phaed.simpleclans.listeners.SCPlayerListener;
import net.sacredlabyrinth.phaed.simpleclans.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Phaed
 */
public class SimpleClans extends JavaPlugin
{

    private static SimpleClans instance;
    private static final Logger logger = Logger.getLogger("Minecraft");
    private ClanManager clanManager;
    private RequestManager requestManager;
    private StorageManager storageManager;
    private SpoutPluginManager spoutPluginManager;
    private SettingsManager settingsManager;
    private PermissionsManager permissionsManager;
    private CommandManager commandManager;
    private TeleportManager teleportManager;
    private SCPlayerListener playerListener;
    private SCEntityListener entityListener;
    private ResourceBundle lang;

    /**
     * @return the logger
     */
    public static Logger getLog()
    {
        return logger;
    }

    /**
     * @return the logger
     */
    public static void debug(String msg)
    {
        if (getInstance().getSettingsManager().isDebugging()) {
            if (msg != null) {
                logger.log(Level.INFO, msg);
            }
        }
    }

    /**
     * @return the instance
     */
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
    public void onEnable()
    {
        instance = this;
        settingsManager = new SettingsManager();

        lang = PropertyResourceBundle.getBundle("languages.lang");

        logger.info(MessageFormat.format(lang.getString("version.loaded"), getDescription().getName(), getDescription().getVersion()));

        spoutPluginManager = new SpoutPluginManager();
        permissionsManager = new PermissionsManager();
        requestManager = new RequestManager();
        clanManager = new ClanManager();
        storageManager = new StorageManager();
        commandManager = new CommandManager();
        teleportManager = new TeleportManager();

        playerListener = new SCPlayerListener();
        entityListener = new SCEntityListener();

        getServer().getPluginManager().registerEvents(entityListener, this);
        getServer().getPluginManager().registerEvents(playerListener, this);

        spoutPluginManager.processAllPlayers();
        permissionsManager.loadPermissions();

        setupMetrics();

    }

    @Override
    public void onDisable()
    {
        getServer().getScheduler().cancelTasks(this);
        getStorageManager().closeConnection();
        getPermissionsManager().savePermissions();
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
        return lang.getString(msg);
    }

    public TeleportManager getTeleportManager()
    {
        return teleportManager;
    }
}
