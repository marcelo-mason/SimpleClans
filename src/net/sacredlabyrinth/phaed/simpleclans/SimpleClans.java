package net.sacredlabyrinth.phaed.simpleclans;

import net.sacredlabyrinth.phaed.simpleclans.listeners.SCEntityListener;
import net.sacredlabyrinth.phaed.simpleclans.listeners.SCPlayerListener;
import net.sacredlabyrinth.phaed.simpleclans.managers.*;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Phaed
 */
public class SimpleClans extends JavaPlugin
{
    private static SimpleClans instance;
    private static Logger logger = Logger.getLogger("Minecraft");
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
    public static Logger getLogger()
    {
        return logger;
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
        logger.log(Level.INFO, new StringBuilder().append(MessageFormat.format(msg, arg)).toString());
    }

    public void onEnable()
    {
        instance = this;
        settingsManager = new SettingsManager();

        if (lang == null)
        {
            lang = PropertyResourceBundle.getBundle("languages.lang", new Locale(settingsManager.getLanguage()));
        }

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

        registerEvents();

        spoutPluginManager.processAllPlayers();
    }

    private void registerEvents()
    {
        getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Low, this);
        getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Low, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Lowest, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Highest, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_KICK, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_TOGGLE_SNEAK, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Normal, this);
    }

    public void onDisable()
    {
        getServer().getScheduler().cancelTasks(this);
        getStorageManager().closeConnection();
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
    public ResourceBundle getLang()
    {
        return lang;
    }

    public TeleportManager getTeleportManager()
    {
        return teleportManager;
    }
}
