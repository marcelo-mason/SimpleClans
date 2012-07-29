package net.sacredlabyrinth.phaed.simpleclans;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
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

/**
 * 
 * @author V10lator
 * @version 1.0
 * website: http://forums.bukkit.org/threads/autoupdate-update-your-plugins.84421/
 *
 */
public class AutoUpdate implements Runnable, Listener
{
  /*
   * Configuration:
   * 
   * delay = The delay this class checks for new updates. This time is in ticks (1 tick = 1/20 second).
   * ymlPrefix = A prefix added to the version string from your plugin.yml.
   * ymlSuffix = A suffix added to the version string from your plugin.yml.
   * bukkitdevPrefix = A prefix added to the version string fetched from bukkitDev.
   * bukkitdevSuffix = A suffix added to the version string fetched from bukkitDev.
   * bukitdevSlug = The bukkitDev Slug. Leave empty for autodetection (uses plugin.getName().toLowerCase()).
   * COLOR_INFO = The default text color.
   * COLOR_OK = The text color for positive messages.
   * COLOR_ERROR = The text color for error messages.
   */
  private long delay = 216000L;
  private final String ymlPrefix = "v";
  private final String ymlSuffix = "";
  private final String bukkitdevPrefix = "";
  private final String bukkitdevSuffix = "";
  private String bukkitdevSlug = "";
  private final ChatColor COLOR_INFO = ChatColor.BLUE;
  private final ChatColor COLOR_OK = ChatColor.GREEN;
  private final ChatColor COLOR_ERROR = ChatColor.RED;
  /*
   * End of configuration.
   * 
   * !!! Don't change anything below if you don't know what you are doing !!!
   * 
   * WARNING: If you change anything below you loose support.
   * Also you have to replace every "http://forums.bukkit.org/threads/autoupdate-update-your-plugins.84421/" with a link to your
   * plugin and change the version to something unique (like adding -<yourName>).
   */
  
  private final String version = "1.0";
  
  private final Plugin plugin;
  private final String bukget;
  private final String bukgetFallback;
  private int pid = -1;
  private final String av;
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
   * This will use your main configuration (config.yml).
   * Use this in onEnable().
   * @param plugin The instance of your plugins main class.
   * @throws Exception 
   */
  public AutoUpdate(Plugin plugin) throws Exception
  {
	this(plugin, plugin.getConfig());
  }
  
  /**
   * This will use a custom configuration.
   * Use this in onEnable().
   * @param plugin The instance of your plugins main class.
   * @param config The configuration to use.
   * @throws Exception 
   */
  public AutoUpdate(Plugin plugin, Configuration config) throws Exception
  {
	if(plugin == null)
	  throw new Exception("Plugin can not be null");
	this.plugin = plugin;
	av = ymlPrefix+plugin.getDescription().getVersion()+ymlSuffix;
	if(bukkitdevSlug == null || bukkitdevSlug.equals(""))
	  bukkitdevSlug = plugin.getName();
	bukkitdevSlug = bukkitdevSlug.toLowerCase();
	bukget = "http://bukget.v10lator.de/"+bukkitdevSlug;
	bukgetFallback = "http://bukget.org/api/plugin/"+bukkitdevSlug+"/latest";
	if(delay < 72000L)
	{
	  plugin.getLogger().info("[AutoUpdate] delay < 72000 ticks not supported. Setting delay to 72000.");
	  delay = 72000L;
	}
	setConfig(config);
	plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }
  
  /**
   * Use this to restart the main task.
   * This is useful after scheduler.cancelTasks(plugin); for example.
   */
  public boolean restartMainTask()
  {
	try
	{
	  ResetTask rt = new ResetTask(enabled);
	  rt.setPid(plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, rt, 0L, 1L));
	  return enabled;
	}
	catch(Throwable t)
	{
	  printStackTraceSync(t, false);
	  return false;
	}
  }
  
  private boolean checkState(boolean newState, boolean restart)
  {
	if(enabled != newState)
	{
	  enabled = newState;
	  plugin.getLogger().info("[AutoUpdate] v"+version+(enabled ? " enabled" : " disabled")+"!");
	  if(restart)
		return restartMainTask();
	}
	return enabled;
  }
  
  private class ResetTask implements Runnable
  {
	private int pid;
	private final boolean restart;
	
	private ResetTask(boolean restart)
	{
	  this.restart = restart;
	}
	
	private void setPid(int pid)
	{
	  this.pid = pid;
	}
	
	public void run()
	{
	  try
	  {
		if(!lock.compareAndSet(false, true))
		  return;
		BukkitScheduler bs = plugin.getServer().getScheduler();
		if(bs.isQueued(AutoUpdate.this.pid) || bs.isCurrentlyRunning(AutoUpdate.this.pid))
		  bs.cancelTask(AutoUpdate.this.pid);
		if(restart)
		  AutoUpdate.this.pid = bs.scheduleAsyncRepeatingTask(plugin, AutoUpdate.this, 5L, delay);
		else
		  AutoUpdate.this.pid = -1;
		lock.set(false);
		bs.cancelTask(pid);
	  }
	  catch(Throwable t)
	  {
		printStackTraceSync(t, false);
	  }
	}
  }
  
  /**
   * This will overwrite the pre-saved configuration.
   * use this after reloadConfig(), for example.
   * This will use your main configuration (config.yml).
   * This will call {@link #restartMainTask()} internally.
   * @throws FileNotFoundException 
   */
  public void resetConfig() throws FileNotFoundException
  {
	setConfig(plugin.getConfig());
  }
  
  /**
   * This will overwrite the pre-saved configuration.
   * use this after config.load(file), for example.
   * This will use a custom configuration.
   * This will call {@link #restartMainTask()} internally.
   * @param config The new configuration to use.
   * @throws FileNotFoundException 
   */
  public void setConfig(Configuration config) throws FileNotFoundException
  {
	if(config == null)
	  throw new FileNotFoundException("Config can not be null");
	try
	{
	  while(!lock.compareAndSet(false, true))
		continue; //TODO: This blocks the main thread...
	  this.config = config;
	  if(!config.isSet("settings.auto-update"))
		config.set("settings.auto-update", true);
	  checkState(config.getBoolean("settings.auto-update"), true);
	  lock.set(false);
	}
	catch(Throwable t)
	{
	  printStackTraceSync(t, false);
	}
  }
  
  /**
   * This is internal stuff.
   * Don't call this directly!
   */
  public void run()
  {
	if(!plugin.isEnabled())
	{
	  plugin.getServer().getScheduler().cancelTask(pid);
	  return;
	}
	try
	{
	  while(!lock.compareAndSet(false, true))
	  {
		try
		{
		  Thread.sleep(1L);
		}
		catch(InterruptedException e)
		{
		}
		continue;
	  }
	  try
	  {
		InputStreamReader ir;
		try
		{
		  URL url = new URL(bukget);
		  ir = new InputStreamReader(url.openStream());
		}
		catch(Exception e)
		{
		  URL url = new URL(bukgetFallback);
		  ir = new InputStreamReader(url.openStream());
		}
		
		String nv;
		try
		{
		  JSONObject jo = new JSONObject(new JSONTokener(ir));
		  JSONArray ja = jo.getJSONArray("versions");
		  pluginURL = jo.getString("bukkitdev_link");
		  jo = ja.getJSONObject(0);
		  nv = bukkitdevPrefix+jo.getString("name")+bukkitdevSuffix;
		  if(av.equals(nv) || (updateVersion != null && updateVersion.equals(nv)))
		  {
			lock.set(false);
			return;
		  }
		  updateURL = jo.getString("dl_link");
		  updateVersion = nv;
		  type = jo.getString("type");
		  needUpdate = true;
		  ir.close();
		}
		catch(JSONException e)
		{
		  lock.set(false);
		  printStackTraceSync(e, true);
		  ir.close();
		  return;
		}
		final String[] out = new String[] {
				"["+plugin.getName()+"] New "+type+" available!",
				"If you want to update from "+av+" to "+updateVersion+" use /update "+plugin.getName(),
				"See "+pluginURL+" for more information."
		};
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new SyncMessageDelayer(null, out));
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
		{
		  public void run()
		  {
			String[] rout = new String[3];
			for(int i = 0; i < 3; i++)
			  rout[i] = COLOR_INFO+out[i];
			for(Player p: plugin.getServer().getOnlinePlayers())
			  if(hasPermission(p, "autoupdate.announce"))
				p.sendMessage(rout);
		  }
		});
	  }
	  catch(Exception e)
	  {
		printStackTraceSync(e, true);
	  }
	  lock.set(false);
	}
	catch(Throwable t)
	{
	  printStackTraceSync(t, false);
	}
  }
  
  /**
   * This is internal stuff.
   * Don't call this directly!
   */
  @EventHandler(priority = EventPriority.MONITOR)
  public void adminJoin(PlayerJoinEvent event)
  {
	try
	{
	  if(!enabled || !lock.compareAndSet(false, true))
		return;
	  Player p = event.getPlayer();
	  String[] out;
	  if(needUpdate)
	  {
		if(hasPermission(p, "autoupdate.announce"))
		{
		  out = new String[] {
				  COLOR_INFO+"["+plugin.getName()+"] New "+type+" available!",
				  COLOR_INFO+"If you want to update from "+av+" to "+updateVersion+" use /update "+plugin.getName(),
				  COLOR_INFO+"See "+pluginURL+" for more information."
		  };
		}
		else
		  out = null;
	  }
	  else if(updatePending)
	  {
		if(hasPermission(p, "autoupdate.announce"))
		{
		  out = new String[] {
				  COLOR_INFO+"Please restart the server to finish the update of "+plugin.getName(),
				  COLOR_INFO+"See "+pluginURL+" for more information."
		  };
		}
		else
		  out = null;
	  }
	  else
		out = null;
	  lock.set(false);
	  if(out != null)
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new SyncMessageDelayer(p.getName(), out));
	}
	catch(Throwable t)
	{
	  printStackTraceSync(t, false);
	}
  }
  
  private class SyncMessageDelayer implements Runnable
  {
	private final String p;
	private final String[] msgs;
	
	private SyncMessageDelayer(String p, String[] msgs)
	{
	  this.p = p;
	  this.msgs = msgs;
	}
	
	public void run()
	{
	  try
	  {
		CommandSender cs;
		if(p != null)
		  cs = plugin.getServer().getPlayerExact(p);
		else
		  cs = plugin.getServer().getConsoleSender();
		if(cs != null)
		  for(String msg: msgs)
			if(msg != null)
			  cs.sendMessage(msg);
	  }
	  catch(Throwable t)
	  {
		printStackTraceSync(t, false);
	  }
	}
  }
  
  //TODO: Find a better way for dynamic command handling
  /**
   * This is internal stuff.
   * Don't call this directly!
   */
  @EventHandler(ignoreCancelled = false)
  public void updateCmd(PlayerCommandPreprocessEvent event)
  {
	try
	{
	  String[] split = event.getMessage().split(" ");
	  if(!split[0].equalsIgnoreCase("/update"))
		return;
	  event.setCancelled(true);
	  if(!enabled || !needUpdate)
		return;
	  if(split.length > 1 && !plugin.getName().equalsIgnoreCase(split[1]))
		return;
	  update(event.getPlayer());
	}
	catch(Throwable t)
	{
	  printStackTraceSync(t, false);
	}
  }
  
  private void update(CommandSender sender)
  {
	if(!hasPermission(sender, "autoupdate.update."+plugin.getName()))
	{
	  sender.sendMessage(COLOR_ERROR+plugin.getName()+": You are not allowed to update me!");
	  return;
	}
	final BukkitScheduler bs = plugin.getServer().getScheduler();
	final String pn = sender instanceof Player ? ((Player)sender).getName() : null;
	bs.scheduleAsyncDelayedTask(plugin, new Runnable()
	{
	  public void run()
	  {
		try
		{
		  while(!lock.compareAndSet(false, true))
		  {
			try
			{
			  Thread.sleep(1L);
			}
			catch(InterruptedException e)
			{
			}
			continue;
		  }
		  String out;
		  try
		  {
			File to = new File(plugin.getServer().getUpdateFolderFile(), updateURL.substring(updateURL.lastIndexOf('/')+1, updateURL.length()));
			File tmp = new File(to.getAbsolutePath()+".au");
			if(!tmp.exists())
			{
			  plugin.getServer().getUpdateFolderFile().mkdirs();
			  tmp.createNewFile();
			}
			URL url = new URL(updateURL);
			InputStream is = url.openStream();
			OutputStream os = new FileOutputStream(tmp);
			byte[] buffer = new byte[4096];
			int fetched;
			while((fetched = is.read(buffer)) != -1)
			  os.write(buffer, 0, fetched);
			is.close();
			os.flush();
			os.close();
			if(to.exists())
			  to.delete();
			if(tmp.renameTo(to))
			{
			  out = COLOR_OK+plugin.getName()+" ready! Restart server to finish the update.";
			  needUpdate = false;
			  updatePending = true;
			  updateURL = type = null;
			}
			else
			{
			  out = COLOR_ERROR+plugin.getName()+" failed to update!";
			  if(tmp.exists())
				tmp.delete();
			  if(to.exists())
				to.delete();
			}
		  }
		  catch(Exception e)
		  {
			out = COLOR_ERROR+plugin.getName()+" failed to update!";
			printStackTraceSync(e, true);
		  }
		  bs.scheduleSyncDelayedTask(plugin, new SyncMessageDelayer(pn, new String[] {out}));
		  lock.set(false);
		}
		catch(Throwable t)
		{
		  printStackTraceSync(t, false);
		}
	  }
	});
  }
  
  private void printStackTraceSync(Throwable t, boolean expected)
  {
	BukkitScheduler bs = plugin.getServer().getScheduler();
	try
	{
	  String prefix = plugin.getName()+" [AutoUpdate] ";
	  StringWriter sw = new StringWriter();
	  PrintWriter pw = new PrintWriter(sw);
	  t.printStackTrace(pw);
	  String[] sts = sw.toString().replace("\r", "").split("\n");
	  String[] out;
	  if(expected)
		out = new String[sts.length+1];
	  else
		out = new String[sts.length+3];
	 
          out[0] = "AutoUpdater failed to connect!";
	  if(!expected)
	  {
		out[1] = prefix+"DISABLING UPDATER!";
		out[2] = prefix;
	  }
	  bs.scheduleSyncDelayedTask(plugin, new SyncMessageDelayer(null, out));
	}
	catch(Throwable e) //This prevents endless loops.
	{
	  e.printStackTrace();
	}
	if(!expected)
	{
	  bs.cancelTask(pid);
	  bs.scheduleAsyncDelayedTask(plugin, new Runnable()
	  {
		public void run()
		{
		  while(!lock.compareAndSet(false, true))
		  {
			try
			{
			  Thread.sleep(1L);
			}
			  catch(InterruptedException e)
			{
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
  
  private boolean hasPermission(Permissible player, String node)
  {
	if(player.isPermissionSet(node))
	  return player.hasPermission(node);
	while(node.contains("."))
	{
	  node = node.substring(0, node.lastIndexOf("."));
	  if(player.isPermissionSet(node))
	    return player.hasPermission(node);
	  if(player.isPermissionSet(node+".*"))
	    return player.hasPermission(node+".*");
	}
	if(player.isPermissionSet("*"))
	  return player.hasPermission("*");
	return player.isOp();
  }
  
  // We use a in-lined stripped-down version of the JSON lib to avoid dependencies.
  /**
   * This is a stipped down version of JSONArray from JSON.org
   * allowing only creation (from a JSONTokener) and  basic reading.
   *
   * @author V10lator
   * @author JSON.org
   * @version 0.1 based from JSON.org 2012-04-20
   */
  private class JSONArray {
  	private final ArrayList<Object> myArrayList;
  	
  	private JSONArray(JSONTokener x) throws JSONException {
  		this.myArrayList = new ArrayList<Object>();
        if (x.nextClean() != '[') {
            throw x.syntaxError("A JSONArray text must start with '['");
        }
        if (x.nextClean() != ']') {
            x.back();
            for (;;) {
                if (x.nextClean() == ',') {
                    x.back();
                    this.myArrayList.add(null);
                } else {
                    x.back();
                    this.myArrayList.add(x.nextValue());
                }
                switch (x.nextClean()) {
                case ';':
                case ',':
                    if (x.nextClean() == ']') {
                        return;
                    }
                    x.back();
                    break;
                case ']':
                    return;
                default:
                    throw x.syntaxError("Expected a ',' or ']'");
                }
            }
        }
    }
      
      private Object get(int index) throws JSONException {
      	if(index < 0 || index >= this.length())
      		throw new JSONException("JSONArray[" + index + "] out of range.");
          Object object = this.myArrayList.get(index);
          if (object == null) {
              throw new JSONException("JSONArray[" + index + "] not found.");
          }
          return object;
      }
      
      private JSONObject getJSONObject(int index) throws JSONException {
          Object object = this.get(index);
          if (object instanceof JSONObject) {
              return (JSONObject)object;
          }
          throw new JSONException("JSONArray[" + index +
              "] is not a JSONObject.");
      }
      
      private int length() {
          return this.myArrayList.size();
      }
  }
  
  /**
   * This is a stipped down version of JSONObject from JSON.org
   * allowing only creation (from a JSONTokener) and  basic reading.
   *
   * @author V10lator
   * @author JSON.org
   * @version 0.1 based from JSON.org 2012-05-29
   */
  private class JSONObject {
  	private final Map<String, Object> map;
  	
  	private JSONObject(JSONTokener x) throws JSONException {
      	this.map = new HashMap<String, Object>();
          char c;
          String key;

          if (x.nextClean() != '{') {
              throw x.syntaxError("A JSONObject text must begin with '{'");
          }
          for (;;) {
              c = x.nextClean();
              switch (c) {
              case 0:
                  throw x.syntaxError("A JSONObject text must end with '}'");
              case '}':
                  return;
              default:
                  x.back();
                  key = x.nextValue().toString();
              }

  // The key is followed by ':'. We will also tolerate '=' or '=>'.

              c = x.nextClean();
              if (c == '=') {
                  if (x.next() != '>') {
                      x.back();
                  }
              } else if (c != ':') {
                  throw x.syntaxError("Expected a ':' after a key");
              }
              if (key == null)
              	throw new JSONException("Null key.");
              
              Object value = x.nextValue();
              
              if (value != null) {
              	if (this.has(key)) {
              		throw new JSONException("Duplicate key \"" + key + "\"");
              	}
              	if (value instanceof Double) {
                      if (((Double)value).isInfinite() || ((Double)value).isNaN()) {
                          throw new JSONException(
                              "JSON does not allow non-finite numbers.");
                      }
                  } else if (value instanceof Float) {
                      if (((Float)value).isInfinite() || ((Float)value).isNaN()) {
                          throw new JSONException(
                              "JSON does not allow non-finite numbers.");
                      }
                  }
                  this.map.put(key, value);
              }

  // Pairs are separated by ','. We will also tolerate ';'.

              switch (x.nextClean()) {
              case ';':
              case ',':
                  if (x.nextClean() == '}') {
                      return;
                  }
                  x.back();
                  break;
              case '}':
                  return;
              default:
                  throw x.syntaxError("Expected a ',' or '}'");
              }
          }
      }
  	
  	private Object get(String key) throws JSONException {
          if (key == null) {
              throw new JSONException("Null key.");
          }
          Object object = this.map.get(key);
          if (object == null) {
              throw new JSONException("JSONObject[" + quote(key) +
                      "] not found.");
          }
          return object;
      }
  	
  	private JSONArray getJSONArray(String key) throws JSONException {
          Object object = this.get(key);
          if (object instanceof JSONArray) {
              return (JSONArray)object;
          }
          throw new JSONException("JSONObject[" + quote(key) +
                  "] is not a JSONArray.");
      }
  	
  	private String getString(String key) throws JSONException {
          Object object = this.get(key);
          if (object instanceof String) {
              return (String)object;
          }
          throw new JSONException("JSONObject[" + quote(key) +
              "] not a string.");
      }
  	
  	private boolean has(String key) {
          return this.map.containsKey(key);
      }
  	
  	private String quote(String string) {
          StringWriter sw = new StringWriter();
          synchronized (sw.getBuffer()) {
              if (string == null || string.length() == 0) {
                  sw.write("\"\"");
              }
              else
              {
                  char b;
                  char c = 0;
                  String hhhh;
                  int i;
                  int len = string.length();

                  sw.write('"');
                  for (i = 0; i < len; i += 1) {
                      b = c;
                      c = string.charAt(i);
                      switch (c) {
                      case '\\':
                      case '"':
                          sw.write('\\');
                          sw.write(c);
                          break;
                      case '/':
                          if (b == '<') {
                              sw.write('\\');
                          }
                          sw.write(c);
                          break;
                      case '\b':
                          sw.write("\\b");
                          break;
                      case '\t':
                          sw.write("\\t");
                          break;
                      case '\n':
                          sw.write("\\n");
                          break;
                      case '\f':
                          sw.write("\\f");
                          break;
                      case '\r':
                          sw.write("\\r");
                          break;
                      default:
                          if (c < ' ' || (c >= '\u0080' && c < '\u00a0')
                                  || (c >= '\u2000' && c < '\u2100')) {
                              hhhh = "000" + Integer.toHexString(c);
                              sw.write("\\u" + hhhh.substring(hhhh.length() - 4));
                          } else {
                              sw.write(c);
                          }
                      }
                  }
                  sw.write('"');
              }
              return sw.toString();
          }
      }
  }
  
  /**
   * A JSONTokener takes a source string and extracts characters and tokens from
   * it. It is used by the JSONObject and JSONArray constructors to parse
   * JSON source strings.
   * @author JSON.org
   * @version 2012-02-16
   */
  private class JSONTokener {

      private boolean eof;
      private long    index;
      private char    previous;
      private Reader  reader;
      private boolean usePrevious;


      private JSONTokener(Reader reader) {
          this.reader = reader.markSupported()
              ? reader
              : new BufferedReader(reader);
          this.eof = false;
          this.usePrevious = false;
          this.previous = 0;
          this.index = 0;
      }


      /**
       * Back up one character. This provides a sort of lookahead capability,
       * so that you can test for a digit or letter before attempting to parse
       * the next number or identifier.
       */
      private void back() throws JSONException {
          if (this.usePrevious || this.index <= 0) {
              throw new JSONException("Stepping back two steps is not supported");
          }
          this.index -= 1;
          this.usePrevious = true;
          this.eof = false;
      }

      private boolean end() {
          return this.eof && !this.usePrevious;
      }


      /**
       * Get the next character in the source string.
       *
       * @return The next character, or 0 if past the end of the source string.
       */
      private char next() throws JSONException {
          int c;
          if (this.usePrevious) {
              this.usePrevious = false;
              c = this.previous;
          } else {
              try {
                  c = this.reader.read();
              } catch (IOException exception) {
                  throw new JSONException(exception);
              }

              if (c <= 0) { // End of stream
                  this.eof = true;
                  c = 0;
              }
          }
          this.index += 1;
          this.previous = (char) c;
          return this.previous;
      }


      /**
       * Get the next n characters.
       *
       * @param n     The number of characters to take.
       * @return      A string of n characters.
       * @throws JSONException
       *   Substring bounds error if there are not
       *   n characters remaining in the source string.
       */
       private String next(int n) throws JSONException {
           if (n == 0) {
               return "";
           }

           char[] chars = new char[n];
           int pos = 0;

           while (pos < n) {
               chars[pos] = this.next();
               if (this.end()) {
                   throw this.syntaxError("Substring bounds error");
               }
               pos += 1;
           }
           return new String(chars);
       }


      /**
       * Get the next char in the string, skipping whitespace.
       * @throws JSONException
       * @return  A character, or 0 if there are no more characters.
       */
      private char nextClean() throws JSONException {
          while (true) { // V10: for to while.
              char c = this.next();
              if (c == 0 || c > ' ') {
                  return c;
              }
          }
      }


      /**
       * Return the characters up to the next close quote character.
       * Backslash processing is done. The formal JSON format does not
       * allow strings in single quotes, but an implementation is allowed to
       * accept them.
       * @param quote The quoting character, either
       *      <code>"</code>&nbsp;<small>(double quote)</small> or
       *      <code>'</code>&nbsp;<small>(single quote)</small>.
       * @return      A String.
       * @throws JSONException Unterminated string.
       */
      private String nextString(char quote) throws JSONException {
          char c;
          StringBuffer sb = new StringBuffer();
          while (true) { // V10: for to while.
              c = this.next();
              switch (c) {
              case 0:
              case '\n':
              case '\r':
                  throw this.syntaxError("Unterminated string");
              case '\\':
                  c = this.next();
                  switch (c) {
                  case 'b':
                      sb.append('\b');
                      break;
                  case 't':
                      sb.append('\t');
                      break;
                  case 'n':
                      sb.append('\n');
                      break;
                  case 'f':
                      sb.append('\f');
                      break;
                  case 'r':
                      sb.append('\r');
                      break;
                  case 'u':
                      sb.append((char)Integer.parseInt(this.next(4), 16));
                      break;
                  case '"':
                  case '\'':
                  case '\\':
                  case '/':
                      sb.append(c);
                      break;
                  default:
                      throw this.syntaxError("Illegal escape.");
                  }
                  break;
              default:
                  if (c == quote) {
                      return sb.toString();
                  }
                  sb.append(c);
              }
          }
      }


      /**
       * Get the next value. The value can be a Boolean, Double, Integer,
       * JSONArray, JSONObject, Long, or String, or the JSONObject.NULL object.
       * @throws JSONException If syntax error.
       *
       * @return An object.
       */
      private Object nextValue() throws JSONException {
          char c = this.nextClean();

          switch (c) {
              case '"':
              case '\'':
                  return this.nextString(c);
              case '{':
                  this.back();
                  return new JSONObject(this);
              case '[':
                  this.back();
                  return new JSONArray(this);
          }

          /*
           * While the original JSON does more here we just assume if it's not a JSON* it's a String...
           */

          StringBuffer sb = new StringBuffer();
          while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
              sb.append(c);
              c = this.next();
          }
          this.back();

          String string = sb.toString().trim();
          if ("".equals(string))
              throw this.syntaxError("Missing value");
          return string;
      }


      /**
       * Make a JSONException to signal a syntax error.
       *
       * @param message The error message.
       * @return  A JSONException object, suitable for throwing
       */
      private JSONException syntaxError(String message) {
          return new JSONException(message + this.toString());
      }
  }
  
  /**
   * The JSONException is thrown by the JSON.org classes when things are amiss.
   * @author JSON.org
   * @version 2010-12-24
   */
  private class JSONException extends Exception {
      private static final long serialVersionUID = 0;
      private Throwable cause;

      /**
       * Constructs a JSONException with an explanatory message.
       * @param message Detail about the reason for the exception.
       */
      private JSONException(String message) {
          super(message);
      }

      private JSONException(Throwable cause) {
          super(cause.getMessage());
          this.cause = cause;
      }

      public Throwable getCause() {
          return this.cause;
      }
  }
}