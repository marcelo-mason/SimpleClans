package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.HashMap;
import java.util.List;

/**
 * @author phaed
 */
public final class PermissionsManager
{
    /**
     *
     */
    private SimpleClans plugin;

    public static Permission permission = null;
    public static Economy economy = null;
    public static Chat chat = null;

    private HashMap<String, List<String>> permissions = new HashMap<String, List<String>>();
    private HashMap<Player, PermissionAttachment> permAttaches = new HashMap<Player, PermissionAttachment>();


    /**
     *
     */
    public PermissionsManager()
    {
        plugin = SimpleClans.getInstance();

        try
        {
            Class.forName("net.milkbowl.vault.permission.Permission");

            setupChat();
            setupEconomy();
            setupPermissions();
        }
        catch (ClassNotFoundException e)
        {
            SimpleClans.log("[PreciousStones] Vault not found. No economy or extended Permissions support.");
        }
    }

    /**
     * Whether exonomy plugin exists and is enabled
     *
     * @return
     */
    public boolean hasEconomy()
    {
        return economy != null && economy.isEnabled();
    }

    /**
     * Loads the permissions for each clan from the config
     */
    public void loadPermissions()
    {
        SimpleClans.getInstance().getSettingsManager().load();
        permissions.clear();
        for (Clan clan : plugin.getClanManager().getClans())
        {
            permissions.put(clan.getTag(), SimpleClans.getInstance().getConfig().getStringList("permissions." + clan.getTag()));
        }
    }

    /**
     * Saves the permissions for earch clan from the config
     */
    public void savePermissions()
    {
        for (Clan clan : plugin.getClanManager().getClans())
        {
            if (permissions.containsKey(clan.getTag()))
            {
                SimpleClans.getInstance().getSettingsManager().getConfig().set("permissions." + clan.getTag(), getPermissions(clan));
            }
        }
        SimpleClans.getInstance().getSettingsManager().save();
    }

    /**
     * Adds all pemrissions for a clan
     *
     * @param clan
     */
    public void updateClanPermissions(Clan clan)
    {
        for (ClanPlayer cp : clan.getMembers())
        {
            addPlayerPermissions(cp);
        }
    }

    /**
     * Adds permissions for a player
     *
     * @param cp
     */
    public void addPlayerPermissions(ClanPlayer cp)
    {
        if (cp != null && cp.toPlayer() != null)
        {
            Player player = cp.toPlayer();
            if (permissions.containsKey(cp.getClan().getTag()))
            {
                if (!permAttaches.containsKey(cp.toPlayer()))
                {
                    permAttaches.put(cp.toPlayer(), cp.toPlayer().addAttachment(SimpleClans.getInstance()));
                }
                //Adds all permisisons from his clan
                for (String perm : getPermissions(cp.getClan()))
                {
                    permAttaches.get(cp.toPlayer()).setPermission(perm, true);
                }
                if (plugin.getSettingsManager().isAutoGroupGroupName())
                {
                    permAttaches.get(cp.toPlayer()).setPermission("group." + cp.getClan().getTag(), true);
                }
                player.recalculatePermissions();
            }
        }
    }

    /**
     * Removes permissions for a clan (when it gets disbanded for example)
     *
     * @param clan
     */
    public void removeClanPermissions(Clan clan)
    {
        for (ClanPlayer cp : clan.getMembers())
        {
            removeClanPlayerPermissions(cp);
        }
    }

    /**
     * Removes permissions for a player (when he gets kicked for example)
     *
     * @param cp
     */
    public void removeClanPlayerPermissions(ClanPlayer cp)
    {
        if (cp != null)
        {
            if (cp.getClan() != null)
            {
                if (cp.toPlayer() != null)
                {
                    Player player = cp.toPlayer();
                    if (player.isOnline())
                    {
                        if (permissions.containsKey(cp.getClan().getTag()))
                        {
                            if (permAttaches.containsKey(player))
                            {
                                permAttaches.get(player).remove();
                                permAttaches.remove(player);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @param clan
     * @return the permissions for a clan
     */
    public List<String> getPermissions(Clan clan)
    {
        return permissions.get(clan.getTag());
    }


    /**
     * @return the PermissionsAttachments for every player
     */
    public HashMap<Player, PermissionAttachment> getPermAttaches()
    {
        return permAttaches;
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
     * Grants a player some money
     *
     * @param player
     * @param money
     * @return
     */
    public boolean playerGrantMoney(Player player, double money)
    {
        return economy.depositPlayer(player.getName(), money).transactionSuccess();
    }

    /**
     * Grants a player some money
     *
     * @param player
     * @param money
     * @return
     */
    public boolean playerGrantMoney(String player, double money)
    {
        return economy.depositPlayer(player, money).transactionSuccess();
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
     * Returns the players money
     *
     * @param player
     * @return the players money
     */
    public double playerGetMoney(Player player)
    {
        return economy.getBalance(player.getName());
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
        else
        {
            return player.hasPermission(perm);
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
                    if (!permission.playerInGroup(cp.toPlayer(), "clan." + cp.getTag()))
                    {
                        permission.playerAddGroup(cp.toPlayer(), "clan." + cp.getTag());
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

                    if (!permission.playerInGroup(cp.toPlayer(), "SCUntrusted"))
                    {
                        permission.playerAddGroup(cp.toPlayer(), "SCUntrusted");
                    }
                    permission.playerRemoveGroup(cp.toPlayer(), "SCTrusted");
                    permission.playerRemoveGroup(cp.toPlayer(), "SCLeader");
                }
                else
                {
                    permission.playerRemoveGroup(cp.toPlayer(), "SCUntrusted");
                    permission.playerRemoveGroup(cp.toPlayer(), "SCTrusted");
                    permission.playerRemoveGroup(cp.toPlayer(), "SCLeader");
                }
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
                permission.playerRemoveGroup(cp.toPlayer(), "clan." + cp.getTag());
                permission.playerRemoveGroup(cp.toPlayer(), "SCUntrusted");
                permission.playerRemoveGroup(cp.toPlayer(), "SCTrusted");
                permission.playerRemoveGroup(cp.toPlayer(), "SCLeader");
            }
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

        if (permission != null && chat != null)
        {
            try
            {
                String world = p.getWorld().getName();
                String name = p.getName();
                String prefix = chat.getPlayerPrefix(name, world);
                if (prefix == null || prefix.isEmpty())
                {
                    String group = permission.getPrimaryGroup(world, name);
                    prefix = chat.getGroupPrefix(world, group);
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

        if (permission != null && chat != null)
        {
            try
            {
                String world = p.getWorld().getName();
                String name = p.getName();
                String suffix = chat.getPlayerSuffix(world, name);
                if (suffix == null || suffix.isEmpty())
                {
                    String group = permission.getPrimaryGroup(world, name);
                    suffix = chat.getPlayerSuffix(world, group);
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
