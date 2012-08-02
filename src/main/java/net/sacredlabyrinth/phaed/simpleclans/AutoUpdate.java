package net.sacredlabyrinth.phaed.simpleclans;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.common.collect.Lists;

/**
 * @author V10lator
 * @version 1.0-Comphenix
 * @see Mod: <a href=
 *      "http://dev.bukkit.org/server-mods/experiencemod/"
 *      >ExperienceMod</a>
 */
public class AutoUpdate implements Runnable, Listener {
	
	/*
	 * Configuration:
	 * 
	 * delay = The delay this class checks for new updates. This time is in
	 * ticks (1 tick = 1/20 second). ymlPrefix = A prefix added to the version
	 * string from your plugin.yml. ymlSuffix = A suffix added to the version
	 * string from your plugin.yml. bukkitdevPrefix = A prefix added to the
	 * version string fetched from bukkitDev. bukkitdevSuffix = A suffix added
	 * to the version string fetched from bukkitDev. bukitdevSlug = The
	 * bukkitDev Slug. Leave empty for autodetection (uses
	 * plugin.getName().toLowerCase()). COLOR_INFO = The default text color.
	 * COLOR_OK = The text color for positive messages. COLOR_ERROR = The text
	 * color for error messages.
	 */
	private long delay = 216000L;
	private final String ymlPrefix = "";
	private final String ymlSuffix = "";
	private final String bukkitdevPrefix = "";
	private final String bukkitdevSuffix = "";
	
	private String bukkitdevSlug = "";
	private final ChatColor COLOR_INFO = ChatColor.BLUE;
	private final ChatColor COLOR_OK = ChatColor.GREEN;
	private final ChatColor COLOR_ERROR = ChatColor.RED;
	
	// No need to dump these values
	private final static String AUTO_UPDATE_SETTING = "auto update";
	private final static String SUPPORT_URL = "http://dev.bukkit.org/server-mods/experiencemod/";
	
	/*
	 * End of configuration.
	 * 
	 * !!! Don't change anything below if you don't know what you are doing !!!
	 * 
	 * WARNING: If you change anything below you loose support. Also you have to
	 * replace every
	 * "http://forums.bukkit.org/threads/autoupdate-update-your-plugins.84421/"
	 * with a link to your plugin and change the version to something unique
	 * (like adding -<yourName>).
	 */

	private final String version = "1.0-Comphenix";

	private final Plugin plugin;
	private final String bukget;
	private final String bukgetFallback;
	private int pid = -1;
	private final String av;
	
	@SuppressWarnings("unused")
	private Configuration config;

	boolean enabled = false;
	private final AtomicBoolean lock = new AtomicBoolean(false);
	private boolean needUpdate = false;
	private boolean updatePending = false;
	private String updateURL;
	private String updateVersion;
	private String pluginURL;
	private String type;

	/**
	 * This will use your main configuration (config.yml). Use this in
	 * onEnable().
	 * 
	 * @param plugin The instance of your plugins main class.
	 * @throws FileNotFoundException - configuration file could not be found.
	 */
	public AutoUpdate(Plugin plugin) throws FileNotFoundException {
		this(plugin, plugin.getConfig());
	}

	/**
	 * This will use a custom configuration. Use this in onEnable().
	 * 
	 * @param plugin The instance of your plugins main class.
	 * @param config The configuration to use.
	 * @throws FileNotFoundException - configuration file could not be found.
	 */
	public AutoUpdate(Plugin plugin, Configuration config) throws FileNotFoundException {
		if (plugin == null)
			throw new NullArgumentException("Plugin can not be null");
		this.plugin = plugin;
		av = ymlPrefix + plugin.getDescription().getVersion() + ymlSuffix;
		if (bukkitdevSlug == null || bukkitdevSlug.equals(""))
			bukkitdevSlug = plugin.getName();
		bukkitdevSlug = bukkitdevSlug.toLowerCase();
		bukget = "http://bukget.v10lator.de/" + bukkitdevSlug;
		bukgetFallback = "http://bukget.org/api/plugin/" + bukkitdevSlug
				+ "/latest";
		if (delay < 72000L) {
			plugin.getLogger()
					.info("[AutoUpdate] delay < 72000 ticks not supported. Setting delay to 72000.");
			delay = 72000L;
		}
		setConfig(config);
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	/**
	 * Use this to restart the main task. This is useful after
	 * scheduler.cancelTasks(plugin); for example.
	 */
	public boolean restartMainTask() {
		try {
			ResetTask rt = new ResetTask(enabled);
			rt.setPid(plugin.getServer().getScheduler()
					.scheduleSyncRepeatingTask(plugin, rt, 0L, 1L));
			return enabled;
		} catch (Throwable t) {
			printStackTraceSync(t, false);
			return false;
		}
	}

	private boolean checkState(boolean newState, boolean restart) {
		if (enabled != newState) {
			enabled = newState;
			plugin.getLogger().info(
					"[AutoUpdate] v" + version
							+ (enabled ? " enabled" : " disabled") + "!");
			if (restart)
				return restartMainTask();
		}
		return enabled;
	}

	private class ResetTask implements Runnable {
		private int pid;
		private final boolean restart;

		private ResetTask(boolean restart) {
			this.restart = restart;
		}

		private void setPid(int pid) {
			this.pid = pid;
		}

		public void run() {
			try {
				if (!lock.compareAndSet(false, true))
					return;
				BukkitScheduler bs = plugin.getServer().getScheduler();
				if (bs.isQueued(AutoUpdate.this.pid) || bs.isCurrentlyRunning(AutoUpdate.this.pid))
					bs.cancelTask(AutoUpdate.this.pid);
				if (restart)
					AutoUpdate.this.pid = bs.scheduleAsyncRepeatingTask(plugin,
							AutoUpdate.this, 5L, delay);
				else
					AutoUpdate.this.pid = -1;
				lock.set(false);
				bs.cancelTask(pid);
			} catch (Throwable t) {
				printStackTraceSync(t, false);
			}
		}
	}

	/**
	 * This will overwrite the pre-saved configuration. use this after
	 * reloadConfig(), for example. This will use your main configuration
	 * (config.yml). This will call {@link #restartMainTask()} internally.
	 * 
	 * @throws FileNotFoundException
	 */
	public void resetConfig() throws FileNotFoundException {
		setConfig(plugin.getConfig());
	}

	/**
	 * This will overwrite the pre-saved configuration. use this after
	 * config.load(file), for example. This will use a custom configuration.
	 * This will call {@link #restartMainTask()} internally.
	 * 
	 * @param config The new configuration to use.
	 * @throws FileNotFoundException
	 */
	public final void setConfig(Configuration config) throws FileNotFoundException {
		if (config == null)
			throw new FileNotFoundException("Config can not be null");
		try {
			while (!lock.compareAndSet(false, true))
				continue; // This blocks the main thread...
			this.config = config;
			if (!config.isSet(AUTO_UPDATE_SETTING))
				config.set(AUTO_UPDATE_SETTING, true);
			checkState(config.getBoolean(AUTO_UPDATE_SETTING), true);
			lock.set(false);
		} catch (Throwable t) {
			printStackTraceSync(t, false);
		}
	}

	/**
	 * This is internal stuff. Don't call this directly!
	 */
	public void run() {
		if (!plugin.isEnabled()) {
			plugin.getServer().getScheduler().cancelTask(pid);
			return;
		}
		try {
			while (!lock.compareAndSet(false, true)) {
				try {
					Thread.sleep(1L);
				} catch (InterruptedException e) {
				}
				continue;
			}
			try {
				InputStreamReader ir;
				try {
					URL url = new URL(bukget);
					ir = new InputStreamReader(url.openStream());
				} catch (Exception e) {
					URL url = new URL(bukgetFallback);
					ir = new InputStreamReader(url.openStream());
				}

				String nv;
				try {
					
					JSONParser parser = new JSONParser();
					Object result = parser.parse(ir);
					
					if (!(result instanceof JSONObject)) {
						ir.close();
						throw new Exception("No data recieved.");
					}

					JSONObject jo = (JSONObject) result;
					JSONArray ja = (JSONArray) jo.get("versions");
					pluginURL = (String) jo.get("bukkitdev_link");
					
					jo = (JSONObject) ja.get(0);
					nv = bukkitdevPrefix + jo.get("name") + bukkitdevSuffix;
					
                                        if (av.contains("UNKNOWN") || av.substring(9).contains("b")) {
                                            return;
                                        }
                                        
					if (av.equals(nv) || (updateVersion != null && updateVersion.equals(nv))) {
						lock.set(false);
						return;
					}
					
					updateURL = (String) jo.get("dl_link");
					updateVersion = nv;
					type = (String) jo.get("type");
					needUpdate = true;
					ir.close();
					
				} catch (ParseException e) {
					lock.set(false);
					printStackTraceSync(e, true);
					ir.close();
					return;
				}
				
				final String[] out = new String[] {
						"[" + plugin.getName() + "] New " + type
								+ " available!",
						"If you want to update from " + av + " to "
								+ updateVersion + " use /update "
								+ plugin.getName(),
						"See " + pluginURL + " for more information." };
				plugin.getServer()
						.getScheduler()
						.scheduleSyncDelayedTask(plugin,
								new SyncMessageDelayer(null, out));
				plugin.getServer().getScheduler()
						.scheduleSyncDelayedTask(plugin, new Runnable() {
							public void run() {
								String[] rout = new String[3];
								for (int i = 0; i < 3; i++)
									rout[i] = COLOR_INFO + out[i];
								for (Player p : plugin.getServer()
										.getOnlinePlayers())
									if (hasPermission(p, "autoupdate.announce"))
										p.sendMessage(rout);
							}
						});
			} catch (Exception e) {
				printStackTraceSync(e, true);
			}
			lock.set(false);
		} catch (Throwable t) {
			printStackTraceSync(t, false);
		}
	}

	/**
	 * This is internal stuff. Don't call this directly!
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void adminJoin(PlayerJoinEvent event) {
		try {
			if (!enabled || !lock.compareAndSet(false, true))
				return;
			Player p = event.getPlayer();
			String[] out;
			if (needUpdate) {
				if (hasPermission(p, "autoupdate.announce")) {
					out = new String[] {
							COLOR_INFO + "[" + plugin.getName() + "] New "
									+ type + " available!",
							COLOR_INFO + "If you want to update from " + av
									+ " to " + updateVersion + " use /update "
									+ plugin.getName(),
							COLOR_INFO + "See " + pluginURL
									+ " for more information." };
				} else
					out = null;
			} else if (updatePending) {
				if (hasPermission(p, "autoupdate.announce")) {
					out = new String[] {
							COLOR_INFO
									+ "Please restart the server to finish the update of "
									+ plugin.getName(),
							COLOR_INFO + "See " + pluginURL
									+ " for more information." };
				} else
					out = null;
			} else
				out = null;
			lock.set(false);
			if (out != null)
				plugin.getServer()
						.getScheduler()
						.scheduleSyncDelayedTask(plugin,
								new SyncMessageDelayer(p.getName(), out));
		} catch (Throwable t) {
			printStackTraceSync(t, false);
		}
	}

	private class SyncMessageDelayer implements Runnable {
		private final String player;
		private final String prefix;
		private final List<String> msgs;
		
		private SyncMessageDelayer(String player, String[] msgs) {
			this(player, "", Lists.newArrayList(msgs));
		}
		
		private SyncMessageDelayer(String player, String prefix, List<String> list) {
			this.player = player;
			this.prefix = prefix;
			this.msgs = list;
		}

		public void run() {
			try {
				CommandSender cs;
				if (player != null)
					cs = plugin.getServer().getPlayerExact(player);
				else
					cs = plugin.getServer().getConsoleSender();
				if (cs != null)
					for (String msg : msgs)
						if (msg != null)
							cs.sendMessage(prefix + msg);
			} catch (Throwable t) {
				printStackTraceSync(t, false);
			}
		}
	}

	// Find a better way for dynamic command handling
	/**
	 * This is internal stuff. Don't call this directly!
	 */
	@EventHandler(ignoreCancelled = false)
	public void updateCmd(PlayerCommandPreprocessEvent event) {
		try {
			String[] split = event.getMessage().split(" ");
			if (!split[0].equalsIgnoreCase("/update"))
				return;
			event.setCancelled(true);
			if (split.length > 1
					&& !plugin.getName().equalsIgnoreCase(split[1]))
				return;
			updatePlugin(event.getPlayer());
		} catch (Throwable t) {
			printStackTraceSync(t, false);
		}
	}
	
	/**
	 * Called by a player or the console to initiate updates.
	 * <p>
	 * Note that the return value only indicates that the update request was accepted and the download
	 * process is scheduled to be executed. The update itself may still fail.
	 * 
	 * @param sender - the player or console that is attempting to initiate an update.
	 * @return TRUE if the update was initiated and the download process has begun, FALSE otherwise.
	 */
	public boolean updatePlugin(CommandSender sender) {
		if (enabled && needUpdate) {
			return update(sender);
		} else {
			return false;
		}
	}

	private boolean update(CommandSender sender) {
		if (!hasPermission(sender, "autoupdate.update." + plugin.getName())) {
			sender.sendMessage(COLOR_ERROR + plugin.getName()
					+ ": You are not allowed to update me!");
			return false;
		}
		final BukkitScheduler bs = plugin.getServer().getScheduler();
		final String pn = sender instanceof Player ? ((Player) sender)
				.getName() : null;
		bs.scheduleAsyncDelayedTask(plugin, new Runnable() {
			public void run() {
				try {
					while (!lock.compareAndSet(false, true)) {
						try {
							Thread.sleep(1L);
						} catch (InterruptedException e) {
						}
						continue;
					}
					String out;
					try {
						File to = new File(plugin.getServer()
								.getUpdateFolderFile(), updateURL.substring(
								updateURL.lastIndexOf('/') + 1,
								updateURL.length()));
						File tmp = new File(to.getAbsolutePath() + ".au");
						if (!tmp.exists()) {
							plugin.getServer().getUpdateFolderFile().mkdirs();
							tmp.createNewFile();
						}
						URL url = new URL(updateURL);
						InputStream is = url.openStream();
						OutputStream os = new FileOutputStream(tmp);
						byte[] buffer = new byte[4096];
						int fetched;
						while ((fetched = is.read(buffer)) != -1)
							os.write(buffer, 0, fetched);
						is.close();
						os.flush();
						os.close();
						if (to.exists())
							to.delete();
						if (tmp.renameTo(to)) {
							out = COLOR_OK
									+ plugin.getName()
									+ " ready! Restart server to finish the update.";
							needUpdate = false;
							updatePending = true;
							updateURL = type = null;
						} else {
							out = COLOR_ERROR + plugin.getName()
									+ " failed to update!";
							if (tmp.exists())
								tmp.delete();
							if (to.exists())
								to.delete();
						}
					} catch (Exception e) {
						out = COLOR_ERROR + plugin.getName()
								+ " failed to update!";
						printStackTraceSync(e, true);
					}
					bs.scheduleSyncDelayedTask(plugin, new SyncMessageDelayer(
							pn, new String[] { out }));
					lock.set(false);
				} catch (Throwable t) {
					printStackTraceSync(t, false);
				}
			}
		});

		// The task was successfully created. It may still fail, though.
		return true;
	}

	private void printStackTraceSync(Throwable t, boolean expected) {
		BukkitScheduler bs = plugin.getServer().getScheduler();
		try {
		    List<String> lines = new ArrayList<String>();
			String prefix = "  ";

			lines.add(String.format("[%] [AutoUpdate]:", plugin.getName()));
			lines.add("Internal error!");
			lines.add("");
			lines.add("If this bug hasn't been reported please open a ticket at " + SUPPORT_URL);
			lines.add("Include the following into your bug report:");
			
			lines.add("          ======= SNIP HERE =======");
			addMultiString(lines, ExceptionUtils.getFullStackTrace(t));
			lines.add("          ======= DUMP =======");
			addMultiString(lines, ToStringBuilder.
					reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE).
					replace(ChatColor.COLOR_CHAR, '&'));
			
			if (!expected) {
				lines.add("DISABLING UPDATER!\n");
				lines.add("");
			}

			lines.add("          ======= SNIP HERE =======");
			lines.add("");
			
			bs.scheduleSyncDelayedTask(plugin,
					new SyncMessageDelayer(null, prefix, lines));
			
		} catch (Throwable e) // This prevents endless loops.
		{
			e.printStackTrace();
		}
		if (!expected) {
			bs.cancelTask(pid);
			bs.scheduleAsyncDelayedTask(plugin, new Runnable() {
				public void run() {
					while (!lock.compareAndSet(false, true)) {
						try {
							Thread.sleep(1L);
						} catch (InterruptedException e) {
						}
					}
					pid = -1;
					config = null;
					needUpdate = updatePending = false;
					updateURL = updateVersion = pluginURL = type = null;
				}
			});
		}
	}

	private void addMultiString(List<String> lines, String text) {
		for (String line : text.split("\\r?\\n"))
			lines.add(line);
	}
	
	private boolean hasPermission(Permissible player, String node) {
		if (player.isPermissionSet(node))
			return player.hasPermission(node);
		while (node.contains(".")) {
			node = node.substring(0, node.lastIndexOf("."));
			if (player.isPermissionSet(node))
				return player.hasPermission(node);
			if (player.isPermissionSet(node + ".*"))
				return player.hasPermission(node + ".*");
		}
		if (player.isPermissionSet("*"))
			return player.hasPermission("*");
		return player.isOp();
	}
}