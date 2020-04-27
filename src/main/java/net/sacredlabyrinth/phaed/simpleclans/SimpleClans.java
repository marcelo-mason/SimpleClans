package net.sacredlabyrinth.phaed.simpleclans;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bstats.bukkit.Metrics;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.sacredlabyrinth.phaed.simpleclans.executors.AcceptCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.executors.AllyCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.executors.ClanCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.executors.DenyCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.executors.GlobalCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.executors.MoreCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.language.LanguageMigration;
import net.sacredlabyrinth.phaed.simpleclans.language.LanguageResource;
import net.sacredlabyrinth.phaed.simpleclans.listeners.SCEntityListener;
import net.sacredlabyrinth.phaed.simpleclans.listeners.SCPlayerListener;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.LanguageManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.RequestManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.StorageManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.TeleportManager;
import net.sacredlabyrinth.phaed.simpleclans.tasks.CollectFeeTask;
import net.sacredlabyrinth.phaed.simpleclans.tasks.CollectUpkeepTask;
import net.sacredlabyrinth.phaed.simpleclans.tasks.UpkeepWarningTask;
import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDMigration;

/**
 * @author Phaed
 */
public class SimpleClans extends JavaPlugin {

    private ArrayList<String> messages = new ArrayList<>();
    private static SimpleClans instance;
    private static final Logger logger = Logger.getLogger("Minecraft");
    private ClanManager clanManager;
    private RequestManager requestManager;
    private StorageManager storageManager;
    private SettingsManager settingsManager;
    private PermissionsManager permissionsManager;
    private TeleportManager teleportManager;
    private LanguageManager languageManager;
    private ChatFormatMigration chatFormatMigration;
    private boolean hasUUID;

    /**
     * @return the logger
     */
    public static Logger getLog() {
        return logger;
    }

    /**
     * @param msg
     */
    public static void debug(String msg) {
        if (getInstance().getSettingsManager().isDebugging()) {
            logger.log(Level.INFO, msg);
        }
    }

    /**
     * @return the instance
     */
    public static SimpleClans getInstance() {
        return instance;
    }

    public static void log(String msg, Object... arg) {
        if (arg == null || arg.length == 0) {
            logger.log(Level.INFO, msg);
        } else {
            logger.log(Level.INFO, MessageFormat.format(msg, arg));
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        new LanguageMigration(this).migrate();        
        settingsManager = new SettingsManager();
        this.hasUUID = UUIDMigration.canReturnUUID();
        
        languageManager = new LanguageManager();
        permissionsManager = new PermissionsManager();
        requestManager = new RequestManager();
        clanManager = new ClanManager();
        storageManager = new StorageManager();
        teleportManager = new TeleportManager();
        chatFormatMigration = new ChatFormatMigration();

        chatFormatMigration.migrateAllyChat();
        chatFormatMigration.migrateClanChat();

        logger.info(MessageFormat.format(getLang("version.loaded"), getDescription().getName(), getDescription().getVersion()));

        getServer().getPluginManager().registerEvents(new SCEntityListener(), this);
        getServer().getPluginManager().registerEvents(new SCPlayerListener(), this);

        permissionsManager.loadPermissions();

        CommandHelper.registerCommand(getSettingsManager().getCommandClan());
        CommandHelper.registerCommand(getSettingsManager().getCommandAccept());
        CommandHelper.registerCommand(getSettingsManager().getCommandDeny());
        CommandHelper.registerCommand(getSettingsManager().getCommandMore());
        CommandHelper.registerCommand(getSettingsManager().getCommandAlly());
        CommandHelper.registerCommand(getSettingsManager().getCommandGlobal());

        getCommand(getSettingsManager().getCommandClan()).setExecutor(new ClanCommandExecutor());
        getCommand(getSettingsManager().getCommandAccept()).setExecutor(new AcceptCommandExecutor());
        getCommand(getSettingsManager().getCommandDeny()).setExecutor(new DenyCommandExecutor());
        getCommand(getSettingsManager().getCommandMore()).setExecutor(new MoreCommandExecutor());
        getCommand(getSettingsManager().getCommandAlly()).setExecutor(new AllyCommandExecutor());
        getCommand(getSettingsManager().getCommandGlobal()).setExecutor(new GlobalCommandExecutor());

        getCommand(getSettingsManager().getCommandClan()).setTabCompleter(new PlayerNameTabCompleter());
        logger.info("[SimpleClans] Modo Multithreading: " + SimpleClans.getInstance().getSettingsManager().getUseThreads());
        logger.info("[SimpleClans] Modo BungeeCord: " + SimpleClans.getInstance().getSettingsManager().getUseBungeeCord());
        if (!Locale.getDefault().getLanguage().equals("en")) {
        	logger.info("[SimpleClans] Help us translate SimpleClans to your language! Access https://crowdin.com/project/simpleclans/");
        }
        
        startTasks();
        startMetrics();
    }
    
    private void startMetrics() {
    	Metrics metrics = new Metrics(this, 7131);
    	SettingsManager sm = getSettingsManager();
    	ClanManager cm = getClanManager();
    	metrics.addCustomChart(new Metrics.SingleLineChart("clans", () -> cm.getClans().size()));
    	metrics.addCustomChart(new Metrics.SingleLineChart("clan_players", () -> cm.getAllClanPlayers().size()));
    	metrics.addCustomChart(new Metrics.SimplePie("database", () -> sm.isUseMysql() ? "MySQL" : "SQLite"));
    	metrics.addCustomChart(new Metrics.SimplePie("upkeep", () -> sm.isClanUpkeep() ? "enabled" : "disabled"));
    	metrics.addCustomChart(new Metrics.SimplePie("member_fee", () -> sm.isMemberFee() ? "enabled" : "disabled"));
    	metrics.addCustomChart(new Metrics.SimplePie("rejoin_cooldown", () -> sm.isRejoinCooldown() ? "enabled" : "disabled"));
    	metrics.addCustomChart(new Metrics.SimplePie("clan_verification", () -> sm.isRequireVerification() ? "enabled" : "disabled"));
    	metrics.addCustomChart(new Metrics.SimplePie("money_per_kill", () -> sm.isMoneyPerKill() ? "enabled" : "disabled"));
    	metrics.addCustomChart(new Metrics.SimplePie("threads", () -> sm.getUseThreads() ? "enabled" : "disabled"));
    	metrics.addCustomChart(new Metrics.SimplePie("bungeecord", () -> sm.getUseBungeeCord() ? "enabled" : "disabled"));
    }

    private void startTasks() {
        if (getSettingsManager().isMemberFee()) {
            new CollectFeeTask().start();
        }
        if (getSettingsManager().isClanUpkeep()) {
            new CollectUpkeepTask().start();
            new UpkeepWarningTask().start();
        }
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        getStorageManager().closeConnection();
        getPermissionsManager().savePermissions();
    }

    /**
     * @return the clanManager
     */
    public ClanManager getClanManager() {
        return clanManager;
    }

    /**
     * @return the requestManager
     */
    public RequestManager getRequestManager() {
        return requestManager;
    }

    /**
     * @return the storageManager
     */
    public StorageManager getStorageManager() {
        return storageManager;
    }

    /**
     * @return the settingsManager
     */
    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    /**
     * @return the permissionsManager
     */
    public PermissionsManager getPermissionsManager() {
        return permissionsManager;
    }

    /**
     * @param key the path within the language file
     * @return the lang
     */
    public String getLang(String key) {
        return getLang(key, null);
    }
    
    public String getLang(String key, Player player) {
    	Locale locale;
    	if (player == null) {
    		locale = getSettingsManager().getLanguage();
    	} else {
    		locale = Helper.getLocale(player);
    	}
    	
    	return new LanguageResource().getLang(key, locale);
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public List<String> getMessages() {
        return messages;
    }

    /**
     * @return the hasUUID
     */
    @Deprecated
    public boolean hasUUID() {
        return this.hasUUID;
    }

    /**
     * @param trueOrFalse
     */
    public void setUUID(boolean trueOrFalse) {
        this.hasUUID = trueOrFalse;
    }

    @Deprecated
    public LanguageManager getLanguageManager() {
        return languageManager;
    }
}
