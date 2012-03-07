package net.sacredlabyrinth.phaed.simpleclans.managers;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import in.mDev.MiracleM4n.mChatSuite.api.mChatAPI;
import in.mDev.MiracleM4n.mChatSuite.mChatSuite;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.sacredlabyrinth.Phaed.PreciousStones.FieldFlag;
import net.sacredlabyrinth.Phaed.PreciousStones.PreciousStones;
import net.sacredlabyrinth.Phaed.PreciousStones.vectors.Field;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Location;
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
    private PreciousStones ps;

    public static Permission permission = null;
    public static Economy economy = null;
    public static Chat chat = null;
    private mChatSuite mchat = null;


    /**
     *
     */
    public PermissionsManager()
    {
        plugin = SimpleClans.getInstance();
        detectPreciousStones();
        detectPermissions();
        detectMChat();
        detectPEX();

        try
        {
            Class.forName("net.milkbowl.vault.permission.Permission");

            setupChat();
            setupEconomy();
            setupPermissions();
        }
        catch (ClassNotFoundException e)
        {
            //SimpleClans.log("[PreciousStones] Vault.jar not found. No economy support.");
            //no need to spam everyone who doesnt use vault
        }
    }

    public mChatSuite getMChat()
    {
        return mchat;
    }

    /**
     * Whether exonomy plugin exists and is enabled
     *
     * @return
     */
    public boolean hasEconomy()
    {
        if (economy != null && economy.isEnabled())
        {
            return true;
        }
        return false;
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

    /**
     * Sets the mChat clan tag
     *
     * @param player
     * @param value
     */
    public void addSetMChatClanTag(Player player, String value)
    {
        if(mchat != null)
        {
            mChatAPI api = mchat.getAPI();

            api.addPlayerVar(player.getName(), "clan", value);
        }
    }

    /**
     * Clears the mChat clan tag
     *
     * @param player
     */
    public void clearSetMChatClanTag(Player player)
    {
        if(mchat != null)
        {
            mChatAPI api = mchat.getAPI();

            api.addPlayerVar(player.getName(), "clan", "");
        }
    }

    /**
     * Gives the player permissions linked to a clan
     *
     * @param cp
     */
    public void addClanPermissions(ClanPlayer cp)
    {
        if (!plugin.getSettingsManager().isEnableAutoGroups())
        {
            return;
        }

        if (permission != null)
        {
            if (cp != null && cp.toPlayer() != null)
            {
                if (cp.getClan() != null)
                {
                    if (!permission.playerInGroup(cp.toPlayer(), "Clan" + cp.getTag()))
                    {
                        permission.playerAddGroup(cp.toPlayer(), "Clan" + cp.getTag());
                    }

                    if (cp.isLeader())
                    {
                        if (!permission.playerInGroup(cp.toPlayer(), "SCLeader"))
                        {
                            permission.playerAddGroup(cp.toPlayer(), "SCLeader");
                        }
                        permission.playerRemoveGroup(cp.toPlayer(), "SCUntrusted");
                        permission.playerRemoveGroup(cp.toPlayer(), "SCTrusted");
                        return;
                    }

                    if (cp.isTrusted())
                    {
                        if (!permission.playerInGroup(cp.toPlayer(), "SCTrusted"))
                        {
                            permission.playerAddGroup(cp.toPlayer(), "SCTrusted");
                        }
                        permission.playerRemoveGroup(cp.toPlayer(), "SCUntrusted");
                        permission.playerRemoveGroup(cp.toPlayer(), "SCLeader");
                        return;
                    }

                    if (!cp.isTrusted() && !cp.isLeader())
                    {
                        if (!permission.playerInGroup(cp.toPlayer(), "SCUntrusted"))
                        {
                            permission.playerAddGroup(cp.toPlayer(), "SCUntrusted");
                        }
                        permission.playerRemoveGroup(cp.toPlayer(), "SCTrusted");
                        permission.playerRemoveGroup(cp.toPlayer(), "SCLeader");
                        return;
                    }
                }
                else
                {
                    permission.playerRemoveGroup(cp.toPlayer(), "SCUntrusted");
                    permission.playerRemoveGroup(cp.toPlayer(), "SCTrusted");
                    permission.playerRemoveGroup(cp.toPlayer(), "SCLeader");
                }
            }
            else
            {
                permission.playerRemoveGroup(cp.toPlayer(), "SCUntrusted");
                permission.playerRemoveGroup(cp.toPlayer(), "SCTrusted");
                permission.playerRemoveGroup(cp.toPlayer(), "SCLeader");
            }
        }
    }

    /**
     * Removes permissions linked to a clan from the player
     *
     * @param cp
     */
    public void removeClanPermissions(ClanPlayer cp)
    {
        if (!plugin.getSettingsManager().isEnableAutoGroups())
        {
            return;
        }

        if (permission != null)
        {
            if (cp.toPlayer() != null)
            {
                permission.playerRemoveGroup(cp.toPlayer(), "Clan" + cp.getTag());
                permission.playerRemoveGroup(cp.toPlayer(), "SCUntrusted");
                permission.playerRemoveGroup(cp.toPlayer(), "SCTrusted");
                permission.playerRemoveGroup(cp.toPlayer(), "SCLeader");
            }
        }
    }

    /**
     * Whether a player is allowed in the area
     *
     * @param player
     * @param location
     * @return
     */
    public boolean teleportAllowed(Player player, Location location)
    {
        if (ps != null)
        {
            Field field = ps.getForceFieldManager().getSourceField(location, FieldFlag.PREVENT_TELEPORT);

            if (field != null)
            {
                boolean allowed = ps.getForceFieldManager().isApplyToAllowed(field, player.getName());

                if (!allowed || field.hasFlag(FieldFlag.APPLY_TO_ALL))
                {
                    return false;
                }
            }
        }

        return true;
    }


    private void detectPreciousStones()
    {
        Plugin plug = plugin.getServer().getPluginManager().getPlugin("PreciousStones");

        if (plug != null)
        {
            ps = ((PreciousStones) plug);
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

    private void detectMChat()
    {
        Plugin test = plugin.getServer().getPluginManager().getPlugin("mChatSuite");

        if (test != null)
        {
            mchat = (mChatSuite) test;
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

        /*
        Plugin colorMe = plugin.getServer().getPluginManager().getPlugin("ColorMe");

        if (colorMe != null)
        {
            out += ((ColorMe) colorMe).getColor(p.getName());
        }
        */

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
