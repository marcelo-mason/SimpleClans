package net.sacredlabyrinth.phaed.simpleclans;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sacredlabyrinth.phaed.simpleclans.listeners.SCEntityListener;
import net.sacredlabyrinth.phaed.simpleclans.listeners.SCPlayerListener;
import net.sacredlabyrinth.phaed.simpleclans.managers.CommandManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.DeathManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.RequestManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SpoutManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.StorageManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Phaed
 */
public class SimpleClans extends JavaPlugin
{
    private static SimpleClans instance;
    private static Logger logger = Logger.getLogger("Minecraft");

    /**
     * @return the logger
     */
    public static Logger getLogger()
    {
        return logger;
    }
    private ClanManager clanManager;
    private CommandManager commandManager;
    private RequestManager requestManager;
    private DeathManager deathManager;
    private StorageManager storageManager;
    private SpoutManager spoutManager;
    private SettingsManager settingsManager;
    private PermissionsManager permissionsManager;

    private SCPlayerListener playerListener;
    private SCEntityListener entityListener;

    /**
     * @return the instance
     */
    public static SimpleClans getInstance()
    {
        return instance;
    }

    /**
     * Parameterized logger
     * @param level
     * @param msg the message
     * @param arg the arguments
     */
    public static void log(Level level, String msg, Object... arg)
    {
        getLogger().log(level, new StringBuilder().append("[SimpleClans] ").append(MessageFormat.format(msg, arg)).toString());
    }

    @Override
    public void onEnable()
    {
        getLogger().info("[" + getDescription().getName() + "] version " + getDescription().getVersion() + " loaded");

        instance = this;
        spoutManager = new SpoutManager();
        settingsManager = new SettingsManager();
        permissionsManager = new PermissionsManager();

        requestManager = new RequestManager();
        clanManager = new ClanManager();
        commandManager = new CommandManager();
        deathManager = new DeathManager();
        storageManager = new StorageManager();

        playerListener = new SCPlayerListener();
        entityListener = new SCEntityListener();

        registerEvents();

        getSpoutManager().processAllPlayers();
    }

    private void registerEvents()
    {
        getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Low, this);
        getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Low, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Lowest, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Lowest, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_KICK, playerListener, Priority.Normal, this);
    }

    @Override
    public void onDisable()
    {
    }

    /**
     * @return the clanManager
     */
    public ClanManager getClanManager()
    {
        return clanManager;
    }

    /**
     * @return the commandManager
     */
    public CommandManager getCommandManager()
    {
        return commandManager;
    }

    /**
     * @return the requestManager
     */
    public RequestManager getRequestManager()
    {
        return requestManager;
    }

    /**
     * @return the deathManager
     */
    public DeathManager getDeathManager()
    {
        return deathManager;
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
    public SpoutManager getSpoutManager()
    {
        return spoutManager;
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
}
