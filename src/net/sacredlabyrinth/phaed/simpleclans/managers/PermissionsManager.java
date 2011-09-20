package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.permissions.PermissionHandler;
import java.util.logging.Level;
import net.D3GN.MiracleM4n.mChat.mChat;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public final class PermissionsManager
{
    /**
     *
     */
    private boolean hasMChat;
    private PermissionHandler handler = null;
    private SimpleClans plugin;

    /**
     *
     */
    public PermissionsManager()
    {
        plugin = SimpleClans.getInstance();
        detectPermissions();
        detectMChat();
    }

    /**
     * Check if a player has permissions
     * @param player the player
     * @param permission the permission
     * @return whether he has the permission
     */
    public boolean has(Player player, String permission)
    {
        if (player == null)
        {
            return false;
        }

        if (handler != null)
        {
            return handler.has(player, permission);
        }
        else
        {
            return player.hasPermission(permission);
        }
    }

    private void detectMChat()
    {
        Plugin test = plugin.getServer().getPluginManager().getPlugin("mChat");

        if (test != null)
        {
            hasMChat = true;
        }
    }

    private void detectPermissions()
    {
        Plugin test = plugin.getServer().getPluginManager().getPlugin("Permissions");

        if (test != null)
        {
            this.handler = ((Permissions) test).getHandler();
        }
    }

    /**
     *
     * @param p
     * @return
     */
    @SuppressWarnings("deprecation")
    public String getGroup(Player p)
    {
        String world = p.getWorld().getName();
        String name = p.getName();
        return handler.getGroup(world, name);
    }

    /**
     *
     * @param p
     * @return
     */
    @SuppressWarnings({"deprecation", "deprecation"})
    public String getPrefix(Player p)
    {
        if (hasMChat)
        {
             return mChat.API.getPrefix(p);
        }

        if (handler != null)
        {
            try
            {
                String world = p.getWorld().getName();
                String name = p.getName();
                String prefix = handler.getUserPermissionString(world, name, "prefix");
                if (prefix == null || prefix.isEmpty())
                {
                    String group = handler.getGroup(world, name);
                    prefix = handler.getGroupPrefix(world, group);
                    if (prefix == null)
                    {
                        prefix = "";
                    }
                }
                return prefix.replace("&", "\u00a7").replace(String.valueOf((char) 194), "");
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
                return "";
            }
        }
        return "";
    }

    /**
     *
     * @param p
     * @return
     */
    @SuppressWarnings({"deprecation", "deprecation"})
    public String getSuffix(Player p)
    {
        if (hasMChat)
        {
            return mChat.API.getSuffix(p);
        }

        if (handler != null)
        {
            try
            {
                String world = p.getWorld().getName();
                String name = p.getName();
                String suffix = handler.getUserPermissionString(world, name, "suffix");
                if (suffix == null || suffix.isEmpty())
                {
                    String group = handler.getGroup(world, name);
                    suffix = handler.getGroupSuffix(world, group);
                    if (suffix == null)
                    {
                        suffix = "";
                    }
                }
                return suffix.replace("&", "\u00a7").replace(String.valueOf((char) 194), "");
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
                return "";
            }
        }
        return "";
    }
}
