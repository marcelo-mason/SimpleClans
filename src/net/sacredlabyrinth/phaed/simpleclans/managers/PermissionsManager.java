package net.sacredlabyrinth.phaed.simpleclans.managers;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import de.xghostkillerx.colorme.ColorMe;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * @author phaed
 */
public final class PermissionsManager
{
    /**
     *
     */
    private boolean hasPEX;
    private PermissionHandler handler = null;
    private SimpleClans plugin;

    public static Permission permission = null;
    public static Economy economy = null;
    public static Chat chat = null;

    /**
     *
     */
    public PermissionsManager()
    {
        plugin = SimpleClans.getInstance();
        detectPermissions();
        detectPEX();

        try
        {
            Class.forName("net.milkbowl.vault.permission.Permission");

            setupPermissions();
            setupEconomy();
            setupChat();
        }
        catch (ClassNotFoundException e)
        {
            SimpleClans.log("[SimpleClans] Vault.jar not found. No economy support.");
            //my class isn't there!
        }
    }

    /**
     * Check if an economy plugin is installed
     *
     * @return
     */
    public boolean hasEconomy()
    {
        return economy != null;
    }

    /**
     * Charge a player some money
     *
     * @param player
     * @param money
     * @return
     */
    public boolean playerChargeMoney(Player player, double money)
    {
        return economy.withdrawPlayer(player.getName(), money).transactionSuccess();
    }

    /**
     * Check if a user has the money
     *
     * @param player
     * @param money
     * @return whether he has the money
     */
    public boolean playerHasMoney(Player player, double money)
    {
        return economy.has(player.getName(), money);
    }

    /**
     * Check if a player has permissions
     *
     * @param player the player
     * @param perm   the permission
     * @return whether he has the permission
     */
    public boolean has(Player player, String perm)
    {
        if (player == null)
        {
            return false;
        }

        if (permission != null)
        {
            return permission.has(player, perm);
        }
        else if (handler != null)
        {
            return handler.has(player, perm);
        }
        else
        {
            return player.hasPermission(perm);
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

    private void detectPEX()
    {
        Plugin test = plugin.getServer().getPluginManager().getPlugin("PermissionsEx");

        if (test != null)
        {
            hasPEX = true;
        }
    }

    private Boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null)
        {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    private Boolean setupChat()
    {
        RegisteredServiceProvider<Chat> chatProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null)
        {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }

    private Boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null)
        {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    /**
     * @param p
     * @return
     */
    @SuppressWarnings({"deprecation", "deprecation"})
    public String getPrefix(Player p)
    {
        String out = "";

        try
        {
            if (chat != null)
            {
                out = chat.getPlayerPrefix(p);
            }
        }
        catch (Exception ex)
        {
            // yea vault kinda sucks like that
        }

        if (hasPEX)
        {
            out = PermissionsEx.getUser(p).getPrefix(p.getWorld().getName());
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

                out = prefix.replace("&", "\u00a7").replace(String.valueOf((char) 194), "");
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }
        }

        // add in colorMe color

        Plugin colorMe = plugin.getServer().getPluginManager().getPlugin("ColorMe");

        if (colorMe != null)
        {
            out += ((ColorMe)colorMe).getColor(p.getName());
        }

        return out;
    }

    /**
     * @param p
     * @return
     */
    @SuppressWarnings({"deprecation", "deprecation"})
    public String getSuffix(Player p)
    {
        try
        {
            if (chat != null)
            {
                return chat.getPlayerSuffix(p);
            }
        }
        catch (Exception ex)
        {
            // yea vault kinda sucks like that
        }

        if (hasPEX)
        {
            return PermissionsEx.getUser(p).getSuffix(p.getWorld().getName());
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
