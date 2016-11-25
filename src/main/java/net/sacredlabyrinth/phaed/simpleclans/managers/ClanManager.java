package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDMigration;
import net.sacredlabyrinth.phaed.simpleclans.events.CreateClanEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.*;

/**
 * @author phaed
 */
public final class ClanManager {

    private SimpleClans plugin;
    private HashMap<String, Clan> clans = new HashMap<>();
    private HashMap<String, ClanPlayer> clanPlayers = new HashMap<>();

    /**
     *
     */
    public ClanManager() {
        plugin = SimpleClans.getInstance();
    }

    /**
     * Deletes all clans and clan players in memory
     */
    public void cleanData() {
        clans.clear();
        clanPlayers.clear();
    }

    /**
     * Import a clan into the in-memory store
     *
     * @param clan
     */
    public void importClan(Clan clan) {
        this.clans.put(clan.getTag(), clan);
    }

    /**
     * Import a clan player into the in-memory store
     *
     * @param cp
     */
    public void importClanPlayer(ClanPlayer cp) {
        if (SimpleClans.getInstance().hasUUID()) {
            if (cp.getUniqueId() != null) {
                this.clanPlayers.put(cp.getUniqueId().toString(), cp);
            }
        } else {
            this.clanPlayers.put(cp.getCleanName(), cp);
        }
    }

    /**
     * Create a new clan
     *
     * @param player
     * @param colorTag
     * @param name
     */
    public void createClan(Player player, String colorTag, String name) {
        ClanPlayer cp;
        if (SimpleClans.getInstance().hasUUID()) {
            cp = getCreateClanPlayer(player.getUniqueId());
        } else {
            cp = getCreateClanPlayer(player.getName());
        }

        boolean verified = !plugin.getSettingsManager().isRequireVerification() || plugin.getPermissionsManager().has(player, "simpleclans.mod.verify");

        Clan clan = new Clan(colorTag, name, verified);
        clan.addPlayerToClan(cp);
        cp.setLeader(true);

        plugin.getStorageManager().insertClan(clan);
        importClan(clan);
        plugin.getStorageManager().updateClanPlayer(cp);

        SimpleClans.getInstance().getPermissionsManager().updateClanPermissions(clan);

        if (SimpleClans.getInstance().hasUUID()) {
            SimpleClans.getInstance().getSpoutPluginManager().processPlayer(cp.getUniqueId());
        } else {
            SimpleClans.getInstance().getSpoutPluginManager().processPlayer(cp.getName());
        }

        SimpleClans.getInstance().getServer().getPluginManager().callEvent(new CreateClanEvent(clan));
    }

    /**
     * Delete a players data file
     *
     * @param cp
     */
    public void deleteClanPlayer(ClanPlayer cp) {
        clanPlayers.remove(cp.getCleanName());
        plugin.getStorageManager().deleteClanPlayer(cp);
    }

    /**
     * Delete a player data from memory
     *
     * @param playerUniqueId
     */
    public void deleteClanPlayerFromMemory(UUID playerUniqueId) {
        clanPlayers.remove(playerUniqueId.toString());
    }

    /**
     * Remove a clan from memory
     *
     * @param tag
     */
    public void removeClan(String tag) {
        clans.remove(tag);
    }

    /**
     * Whether the tag belongs to a clan
     *
     * @param tag
     * @return
     */
    public boolean isClan(String tag) {
        return clans.containsKey(Helper.cleanTag(tag));

    }

    /**
     * Returns the clan the tag belongs to
     *
     * @param tag
     * @return
     */
    public Clan getClan(String tag) {
        return clans.get(Helper.cleanTag(tag));
    }

    /**
     * Get a player's clan
     *
     * @param playerName
     * @return null if not in a clan
     */
    @Deprecated
    public Clan getClanByPlayerName(String playerName) {
        ClanPlayer cp = getClanPlayer(playerName);

        if (cp != null) {
            return cp.getClan();
        }

        return null;
    }

    /**
     * Get a player's clan
     *
     * @param playerUniqueId
     * @return null if not in a clan
     */
    public Clan getClanByPlayerUniqueId(UUID playerUniqueId) {
        ClanPlayer cp = getClanPlayer(playerUniqueId);

        if (cp != null) {
            return cp.getClan();
        }

        return null;
    }

    /**
     * @return the clans
     */
    public List<Clan> getClans() {
        return new ArrayList<>(clans.values());
    }

    /**
     * Returns the collection of all clan players, including the disabled ones
     *
     * @return
     */
    public List<ClanPlayer> getAllClanPlayers() {
        return new ArrayList<>(clanPlayers.values());
    }

    /**
     * Gets the ClanPlayer data object if a player is currently in a clan, null
     * if he's not in a clan
     * Used for BungeeCord Reload ClanPlayer and your Clan
     *
     * @param player
     * @return
     */
    public ClanPlayer getClanPlayerJoinEvent(Player player) {
        SimpleClans.getInstance().getStorageManager().importFromDatabaseOnePlayer(player);
        if (SimpleClans.getInstance().hasUUID()) {
            return getClanPlayer(player.getUniqueId());
        } else {
            return getClanPlayer(player.getName());
        }
    }

    /**
     * Gets the ClanPlayer data object if a player is currently in a clan, null
     * if he's not in a clan
     *
     * @param player
     * @return
     */
    public ClanPlayer getClanPlayer(Player player) {
        if (SimpleClans.getInstance().hasUUID()) {
            return getClanPlayer(player.getUniqueId());
        } else {
            return getClanPlayer(player.getName());
        }
    }

    /**
     * Gets the ClanPlayer data object if a player is currently in a clan, null
     * if he's not in a clan
     *
     * @param playerName
     * @return
     */
    @Deprecated
    public ClanPlayer getClanPlayer(String playerName) {
        ClanPlayer cp;
        if (SimpleClans.getInstance().hasUUID()) {
            cp = getClanPlayerName(playerName);
        } else {
            cp = clanPlayers.get(playerName.toLowerCase());
        }

        if (cp == null) {
            return null;
        }

        if (cp.getClan() == null) {
            return null;
        }

        return cp;
    }

    /**
     * Gets the ClanPlayer data object if a player is currently in a clan, null
     * if he's not in a clan
     *
     * @param playerUniqueId
     * @return
     */
    public ClanPlayer getClanPlayer(UUID playerUniqueId) {
        ClanPlayer cp = clanPlayers.get(playerUniqueId.toString());

        if (cp == null) {
            return null;
        }

        if (cp.getClan() == null) {
            return null;
        }

        return cp;
    }

    /**
     * Gets the ClanPlayer data object if a player is currently in a clan, null
     * if he's not in a clan
     *
     * @param playerDisplayName
     * @return
     */
    public ClanPlayer getClanPlayerName(String playerDisplayName) {
        UUID uuid = UUIDMigration.getForcedPlayerUUID(playerDisplayName);

        if (uuid == null) {
            return null;
        }

        ClanPlayer cp = clanPlayers.get(uuid.toString());

        if (cp == null) {
            return null;
        }

        if (cp.getClan() == null) {
            return null;
        }

        return cp;
    }

    /**
     * Gets the ClanPlayer data object for the player, will retrieve disabled
     * clan players as well, these are players who used to be in a clan but are
     * not currently in one, their data file persists and can be accessed. their
     * clan will be null though.
     *
     * @param playerName
     * @return
     */
    @Deprecated
    public ClanPlayer getAnyClanPlayer(String playerName) {
        if (SimpleClans.getInstance().hasUUID()) {
            return getClanPlayerName(playerName);
        } else {
            return clanPlayers.get(playerName.toLowerCase());
        }
    }

    /**
     * Gets the ClanPlayer data object for the player, will retrieve disabled
     * clan players as well, these are players who used to be in a clan but are
     * not currently in one, their data file persists and can be accessed. their
     * clan will be null though.
     *
     * @param playerUniqueId
     * @return
     */
    public ClanPlayer getAnyClanPlayer(UUID playerUniqueId) {
        return clanPlayers.get(playerUniqueId.toString());
    }

    /**
     * Gets the ClanPlayer object for the player, creates one if not found
     *
     * @param playerName
     * @return
     */
    @Deprecated
    public ClanPlayer getCreateClanPlayer(String playerName) {
        if (clanPlayers.containsKey(playerName.toLowerCase())) {
            return clanPlayers.get(playerName.toLowerCase());
        }

        ClanPlayer cp = new ClanPlayer(playerName);

        plugin.getStorageManager().insertClanPlayer(cp);
        importClanPlayer(cp);

        return cp;
    }

    /**
     * Gets the ClanPlayer object for the player, creates one if not found
     *
     * @param playerDisplayName
     * @return
     */
    public ClanPlayer getCreateClanPlayerUUID(String playerDisplayName) {
        if (SimpleClans.getInstance().hasUUID()) {
            UUID playerUniqueId = UUIDMigration.getForcedPlayerUUID(playerDisplayName);
            if (playerUniqueId != null) {
                return getCreateClanPlayer(playerUniqueId);
            } else {
                return null;
            }
        } else {
            return getCreateClanPlayer(playerDisplayName);
        }
    }

    /**
     * Gets the ClanPlayer object for the player, creates one if not found
     *
     * @param playerUniqueId
     * @return
     */
    public ClanPlayer getCreateClanPlayer(UUID playerUniqueId) {
        if (clanPlayers.containsKey(playerUniqueId.toString())) {
            return clanPlayers.get(playerUniqueId.toString());
        }

        ClanPlayer cp = new ClanPlayer(playerUniqueId);

        plugin.getStorageManager().insertClanPlayer(cp);
        importClanPlayer(cp);

        return cp;
    }

    /**
     * Announce message to the server
     *
     * @param msg
     */
    public void serverAnnounce(String msg) {
        Collection<Player> players = Helper.getOnlinePlayers();

        for (Player player : players) {
            ChatBlock.sendMessage(player, ChatColor.DARK_GRAY + "* " + ChatColor.AQUA + msg);
        }

        SimpleClans.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "[" + plugin.getLang("server.announce") + "] " + ChatColor.WHITE + msg);
    }

    /**
     * Update the players display name with his clan's tag
     *
     * @param player
     */
    public void updateDisplayName(Player player) {
        // do not update displayname if in compat mode

        if (plugin.getSettingsManager().isCompatMode()) {
            return;
        }

        if (player == null) {
            return;
        }

        if (plugin.getSettingsManager().isChatTags()) {
            String prefix = plugin.getPermissionsManager().getPrefix(player);
            String suffix = plugin.getPermissionsManager().getSuffix(player);
            String lastColor = plugin.getSettingsManager().isUseColorCodeFromPrefix() ? Helper.getLastColorCode(prefix) : ChatColor.WHITE + "";
            String fullName = player.getName();

            ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(player.getName());

            if (cp == null) {
                return;
            }

            if (cp.isTagEnabled()) {
                Clan clan = cp.getClan();

                if (clan != null) {
                    fullName = clan.getTagLabel(cp.isLeader()) + lastColor + fullName + ChatColor.WHITE;
                }

                player.setDisplayName(fullName);
            } else {
                player.setDisplayName(lastColor + fullName + ChatColor.WHITE);
            }
        }
    }

    /**
     * Process a player and his clan's last seen date
     *
     * @param player
     */
    public void updateLastSeen(Player player) {
        ClanPlayer cp = getAnyClanPlayer(player.getName());

        if (cp != null) {
            cp.updateLastSeen();
            plugin.getStorageManager().updateClanPlayerAsync(cp);

            Clan clan = cp.getClan();

            if (clan != null) {
                clan.updateLastUsed();
                plugin.getStorageManager().updateClanAsync(clan);
            }
        }
    }

    /**
     * @param playerName
     */
    public void ban(String playerName) {
        ClanPlayer cp = getClanPlayer(playerName);
        Clan clan = cp.getClan();

        if (clan != null) {
            if (clan.getSize() == 1) {
                clan.disband();
            } else {
                cp.setClan(null);
                cp.addPastClan(clan.getColorTag() + (cp.isLeader() ? ChatColor.DARK_RED + "*" : ""));
                cp.setLeader(false);
                cp.setJoinDate(0);
                clan.removeMember(playerName);

                plugin.getStorageManager().updateClanPlayer(cp);
                plugin.getStorageManager().updateClan(clan);
            }
        }

        plugin.getSettingsManager().addBanned(playerName);
    }

    /**
     * Get a count of rivable clans
     *
     * @return
     */
    public int getRivableClanCount() {
        int clanCount = 0;

        for (Clan tm : clans.values()) {
            if (!SimpleClans.getInstance().getSettingsManager().isUnrivable(tm.getTag())) {
                clanCount++;
            }
        }

        return clanCount;
    }

    /**
     * Returns a formatted string detailing the players armor
     *
     * @param inv
     * @return
     */
    public String getArmorString(PlayerInventory inv) {
        String out = "";

        ItemStack h = inv.getHelmet();

        if (h != null) {
            if (h.getType().equals(Material.CHAINMAIL_HELMET)) {
                out += ChatColor.WHITE + plugin.getLang("armor.h");
            } else if (h.getType().equals(Material.DIAMOND_HELMET)) {
                out += ChatColor.AQUA + plugin.getLang("armor.h");
            } else if (h.getType().equals(Material.GOLD_HELMET)) {
                out += ChatColor.YELLOW + plugin.getLang("armor.h");
            } else if (h.getType().equals(Material.IRON_HELMET)) {
                out += ChatColor.GRAY + plugin.getLang("armor.h");
            } else if (h.getType().equals(Material.LEATHER_HELMET)) {
                out += ChatColor.GOLD + plugin.getLang("armor.h");
            } else if (h.getType().equals(Material.AIR)) {
                out += ChatColor.BLACK + plugin.getLang("armor.h");
            } else {
                out += ChatColor.RED + plugin.getLang("armor.h");
            }
        }
        ItemStack c = inv.getChestplate();

        if (c != null) {
            if (c.getType().equals(Material.CHAINMAIL_CHESTPLATE)) {
                out += ChatColor.WHITE + plugin.getLang("armor.c");
            } else if (c.getType().equals(Material.DIAMOND_CHESTPLATE)) {
                out += ChatColor.AQUA + plugin.getLang("armor.c");
            } else if (c.getType().equals(Material.GOLD_CHESTPLATE)) {
                out += ChatColor.YELLOW + plugin.getLang("armor.c");
            } else if (c.getType().equals(Material.IRON_CHESTPLATE)) {
                out += ChatColor.GRAY + plugin.getLang("armor.c");
            } else if (c.getType().equals(Material.LEATHER_CHESTPLATE)) {
                out += ChatColor.GOLD + plugin.getLang("armor.c");
            } else if (c.getType().equals(Material.AIR)) {
                out += ChatColor.BLACK + plugin.getLang("armor.c");
            } else {
                out += ChatColor.RED + plugin.getLang("armor.c");
            }
        }
        ItemStack l = inv.getLeggings();

        if (l != null) {
            if (l.getType().equals(Material.CHAINMAIL_LEGGINGS)) {
                out += ChatColor.WHITE + plugin.getLang("armor.l");
            } else if (l.getType().equals(Material.DIAMOND_LEGGINGS)) {
                out += plugin.getLang("armor.l");
            } else if (l.getType().equals(Material.GOLD_LEGGINGS)) {
                out += plugin.getLang("armor.l");
            } else if (l.getType().equals(Material.IRON_LEGGINGS)) {
                out += plugin.getLang("armor.l");
            } else if (l.getType().equals(Material.LEATHER_LEGGINGS)) {
                out += plugin.getLang("armor.l");
            } else if (l.getType().equals(Material.AIR)) {
                out += plugin.getLang("armor.l");
            } else {
                out += plugin.getLang("armor.l");
            }
        }
        ItemStack b = inv.getBoots();

        if (b != null) {
            if (b.getType().equals(Material.CHAINMAIL_BOOTS)) {
                out += ChatColor.WHITE + plugin.getLang("armor.B");
            } else if (b.getType().equals(Material.DIAMOND_BOOTS)) {
                out += ChatColor.AQUA + plugin.getLang("armor.B");
            } else if (b.getType().equals(Material.GOLD_BOOTS)) {
                out += ChatColor.YELLOW + plugin.getLang("armor.B");
            } else if (b.getType().equals(Material.IRON_BOOTS)) {
                out += ChatColor.WHITE + plugin.getLang("armor.B");
            } else if (b.getType().equals(Material.LEATHER_BOOTS)) {
                out += ChatColor.GOLD + plugin.getLang("armor.B");
            } else if (b.getType().equals(Material.AIR)) {
                out += ChatColor.BLACK + plugin.getLang("armor.B");
            } else {
                out += ChatColor.RED + plugin.getLang("armor.B");
            }
        }

        if (out.length() == 0) {
            out = ChatColor.BLACK + "None";
        }

        return out;
    }

    /**
     * Returns a formatted string detailing the players weapons
     *
     * @param inv
     * @return
     */
    public String getWeaponString(PlayerInventory inv) {
        String headColor = plugin.getSettingsManager().getPageHeadingsColor();

        String out = "";

        int count = getItemCount(inv.all(Material.DIAMOND_SWORD));

        if (count > 0) {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.AQUA + plugin.getLang("weapon.S") + headColor + countString;
        }

        count = getItemCount(inv.all(Material.GOLD_SWORD));

        if (count > 0) {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.YELLOW + plugin.getLang("weapon.S") + headColor + countString;
        }

        count = getItemCount(inv.all(Material.IRON_SWORD));

        if (count > 0) {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.WHITE + plugin.getLang("weapon.S") + headColor + countString;
        }

        count = getItemCount(inv.all(Material.STONE_SWORD));

        if (count > 0) {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.GRAY + plugin.getLang("weapon.S") + headColor + countString;
        }

        count = getItemCount(inv.all(Material.WOOD_SWORD));

        if (count > 0) {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.GOLD + plugin.getLang("weapon.S") + headColor + countString;
        }

        count = getItemCount(inv.all(Material.BOW));

        if (count > 0) {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.GOLD + plugin.getLang("weapon.B") + headColor + countString;
        }

        count = getItemCount(inv.all(Material.ARROW));

        if (count > 0) {
            out += ChatColor.GOLD + plugin.getLang("weapon.A") + headColor + count;
        }

        if (out.length() == 0) {
            out = ChatColor.BLACK + "None";
        }

        return out;
    }

    private int getItemCount(HashMap<Integer, ? extends ItemStack> all) {
        int count = 0;

        for (ItemStack is : all.values()) {
            count += is.getAmount();
        }

        return count;
    }

    /**
     * Returns a formatted string detailing the players food
     *
     * @param inv
     * @return
     */
    public String getFoodString(PlayerInventory inv) {
        double out = 0;

        int count = getItemCount(inv.all(320)); // cooked porkchop

        if (count > 0) {
            out += count * 4;
        }

        count = getItemCount(inv.all(Material.COOKED_FISH));

        if (count > 0) {
            out += count * 3;
        }

        count = getItemCount(inv.all(Material.COOKIE));

        if (count > 0) {
            out += count * 1;
        }

        count = getItemCount(inv.all(Material.CAKE));

        if (count > 0) {
            out += count * 6;
        }

        count = getItemCount(inv.all(Material.CAKE_BLOCK));

        if (count > 0) {
            out += count * 9;
        }

        count = getItemCount(inv.all(Material.MUSHROOM_SOUP));

        if (count > 0) {
            out += count * 4;
        }

        count = getItemCount(inv.all(Material.BREAD));

        if (count > 0) {
            out += count * 3;
        }

        count = getItemCount(inv.all(Material.APPLE));

        if (count > 0) {
            out += count * 2;
        }

        count = getItemCount(inv.all(Material.GOLDEN_APPLE));

        if (count > 0) {
            out += count * 5;
        }

        count = getItemCount(inv.all(Material.RAW_BEEF));

        if (count > 0) {
            out += count * 2;
        }

        count = getItemCount(inv.all(364));  // steak

        if (count > 0) {
            out += count * 4;
        }

        count = getItemCount(inv.all(319)); // raw porkchop

        if (count > 0) {
            out += count * 2;
        }

        count = getItemCount(inv.all(Material.RAW_CHICKEN));

        if (count > 0) {
            out += count * 1;
        }

        count = getItemCount(inv.all(Material.COOKED_CHICKEN));

        if (count > 0) {
            out += count * 3;
        }

        count = getItemCount(inv.all(Material.ROTTEN_FLESH));

        if (count > 0) {
            out += count * 2;
        }

        count = getItemCount(inv.all(360));  // melon slice

        if (count > 0) {
            out += count * 2;
        }

        if (out == 0) {
            return ChatColor.BLACK + plugin.getLang("none");
        } else {
            return new DecimalFormat("#.#").format(out) + "" + ChatColor.GOLD + "h";
        }
    }

    /**
     * Returns a formatted string detailing the players health
     *
     * @param health
     * @return
     */
    public String getHealthString(double health) {
        String out = "";

        if (health >= 16) {
            out += ChatColor.GREEN;
        } else if (health >= 8) {
            out += ChatColor.GOLD;
        } else {
            out += ChatColor.RED;
        }

        for (int i = 0; i < health; i++) {
            out += '|';
        }

        return out;
    }

    /**
     * Returns a formatted string detailing the players hunger
     *
     * @param health
     * @return
     */
    public String getHungerString(int health) {
        String out = "";

        if (health >= 16) {
            out += ChatColor.GREEN;
        } else if (health >= 8) {
            out += ChatColor.GOLD;
        } else {
            out += ChatColor.RED;
        }

        for (int i = 0; i < health; i++) {
            out += '|';
        }

        return out;
    }

    /**
     * Sort clans by KDR
     *
     * @param clans
     * @return
     */
    public void sortClansByKDR(List<Clan> clans) {
        Collections.sort(clans, new Comparator<Clan>() {

            @Override
            public int compare(Clan c1, Clan c2) {
                Float o1 = c1.getTotalKDR();
                Float o2 = c2.getTotalKDR();

                return o2.compareTo(o1);
            }
        });
    }

    /**
     * Sort clans by KDR
     *
     * @param clans
     * @return
     */
    public void sortClansBySize(List<Clan> clans) {
        Collections.sort(clans, new Comparator<Clan>() {

            @Override
            public int compare(Clan c1, Clan c2) {
                Integer o1 = c1.getAllMembers().size();
                Integer o2 = c2.getAllMembers().size();

                return o2.compareTo(o1);
            }
        });
    }

    /**
     * Sort clan players by KDR
     *
     * @param cps
     * @return
     */
    public void sortClanPlayersByKDR(List<ClanPlayer> cps) {
        Collections.sort(cps, new Comparator<ClanPlayer>() {

            @Override
            public int compare(ClanPlayer c1, ClanPlayer c2) {
                Float o1 = c1.getKDR();
                Float o2 = c2.getKDR();

                return o2.compareTo(o1);
            }
        });
    }

    /**
     * Sort clan players by last seen days
     *
     * @param cps
     * @return
     */
    public void sortClanPlayersByLastSeen(List<ClanPlayer> cps) {
        Collections.sort(cps, new Comparator<ClanPlayer>() {

            @Override
            public int compare(ClanPlayer c1, ClanPlayer c2) {
                Double o1 = c1.getLastSeenDays();
                Double o2 = c2.getLastSeenDays();

                return o1.compareTo(o2);
            }
        });
    }

    /**
     * Purchase clan creation
     *
     * @param player
     * @return
     */
    public boolean purchaseCreation(Player player) {
        if (!plugin.getSettingsManager().isePurchaseCreation()) {
            return true;
        }

        double price = plugin.getSettingsManager().getCreationPrice();

        if (plugin.getPermissionsManager().hasEconomy()) {
            if (plugin.getPermissionsManager().playerHasMoney(player, price)) {
                plugin.getPermissionsManager().playerChargeMoney(player, price);
                player.sendMessage(ChatColor.RED + MessageFormat.format(plugin.getLang("account.has.been.debited"), price));
            } else {
                player.sendMessage(ChatColor.RED + plugin.getLang("not.sufficient.money"));
                return false;
            }
        }

        return true;
    }

    /**
     * Purchase invite
     *
     * @param player
     * @return
     */
    public boolean purchaseInvite(Player player) {
        if (!plugin.getSettingsManager().isePurchaseInvite()) {
            return true;
        }

        double price = plugin.getSettingsManager().getInvitePrice();

        if (plugin.getPermissionsManager().hasEconomy()) {
            if (plugin.getPermissionsManager().playerHasMoney(player, price)) {
                plugin.getPermissionsManager().playerChargeMoney(player, price);
                player.sendMessage(ChatColor.RED + MessageFormat.format(plugin.getLang("account.has.been.debited"), price));
            } else {
                player.sendMessage(ChatColor.RED + plugin.getLang("not.sufficient.money"));
                return false;
            }
        }

        return true;
    }

    /**
     * Purchase Home Teleport
     *
     * @param player
     * @return
     */
    public boolean purchaseHomeTeleport(Player player) {
        if (!plugin.getSettingsManager().isePurchaseHomeTeleport()) {
            return true;
        }

        double price = plugin.getSettingsManager().getHomeTeleportPrice();

        if (plugin.getPermissionsManager().hasEconomy()) {
            if (plugin.getPermissionsManager().playerHasMoney(player, price)) {
                plugin.getPermissionsManager().playerChargeMoney(player, price);
                player.sendMessage(ChatColor.RED + MessageFormat.format(plugin.getLang("account.has.been.debited"), price));
            } else {
                player.sendMessage(ChatColor.RED + plugin.getLang("not.sufficient.money"));
                return false;
            }
        }

        return true;
    }

    /**
     * Purchase Home Teleport Set
     *
     * @param player
     * @return
     */
    public boolean purchaseHomeTeleportSet(Player player) {
        if (!plugin.getSettingsManager().isePurchaseHomeTeleportSet()) {
            return true;
        }

        double price = plugin.getSettingsManager().getHomeTeleportPriceSet();

        if (plugin.getPermissionsManager().hasEconomy()) {
            if (plugin.getPermissionsManager().playerHasMoney(player, price)) {
                plugin.getPermissionsManager().playerChargeMoney(player, price);
                player.sendMessage(ChatColor.RED + MessageFormat.format(plugin.getLang("account.has.been.debited"), price));
            } else {
                player.sendMessage(ChatColor.RED + plugin.getLang("not.sufficient.money"));
                return false;
            }
        }

        return true;
    }

    /**
     * Purchase clan verification
     *
     * @param player
     * @return
     */
    public boolean purchaseVerification(Player player) {
        if (!plugin.getSettingsManager().isePurchaseVerification()) {
            return true;
        }

        double price = plugin.getSettingsManager().getVerificationPrice();

        if (plugin.getPermissionsManager().hasEconomy()) {
            if (plugin.getPermissionsManager().playerHasMoney(player, price)) {
                plugin.getPermissionsManager().playerChargeMoney(player, price);
                player.sendMessage(ChatColor.RED + MessageFormat.format(plugin.getLang("account.has.been.debited"), price));
            } else {
                player.sendMessage(ChatColor.RED + plugin.getLang("not.sufficient.money"));
                return false;
            }
        }

        return true;
    }

    /**
     * Processes a clan chat command
     *
     * @param player
     * @param msg
     */
    public void processClanChat(Player player, String tag, String msg) {
        Clan clan = plugin.getClanManager().getClan(tag);

        if (clan == null || !clan.isMember(player)) {
            return;
        }

        processClanChat(player, msg);
    }

    /**
     * Processes a clan chat command
     *
     * @param player
     * @param msg
     */
    public void processClanChat(Player player, String msg) {
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player.getName());

        if (cp == null) {
            return;
        }

        String[] split = msg.split(" ");

        if (split.length == 0) {
            return;
        }

        String command = split[0];

        if (command.equals(plugin.getLang("on"))) {
            cp.setClanChat(true);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have enabled clan chat");
        } else if (command.equals(plugin.getLang("off"))) {
            cp.setClanChat(false);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have disabled clan chat");
        } else if (command.equals(plugin.getLang("join"))) {
            cp.setChannel(ClanPlayer.Channel.CLAN);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have joined clan chat");
        } else if (command.equals(plugin.getLang("leave"))) {
            cp.setChannel(ClanPlayer.Channel.NONE);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have left clan chat");
        } else if (command.equals(plugin.getLang("mute"))) {
            if (cp.isMuted()) {
                cp.setMuted(true);
                ChatBlock.sendMessage(player, ChatColor.AQUA + "You have muted clan chat");
            } else {
                cp.setMuted(false);
                ChatBlock.sendMessage(player, ChatColor.AQUA + "You have unmuted clan chat");
            }
        } else {
            String code = "" + ChatColor.RED + ChatColor.WHITE + ChatColor.RED + ChatColor.BLACK;
            String tag;

            if (cp.getRank() != null && !cp.getRank().isEmpty()) {
                tag = plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketLeft() + plugin.getSettingsManager().getClanChatRankColor() + cp.getRank() + plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketRight() + " ";
            } else {
                tag = plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketLeft() + plugin.getSettingsManager().getTagDefaultColor() + cp.getClan().getColorTag() + plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketRight() + " ";
            }

            String message = code + Helper.parseColors(tag) + plugin.getSettingsManager().getClanChatNameColor() + plugin.getSettingsManager().getClanChatPlayerBracketLeft() + player.getName() + plugin.getSettingsManager().getClanChatPlayerBracketRight() + " " + plugin.getSettingsManager().getClanChatMessageColor() + msg;
            String eyeMessage = code + plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketLeft() + plugin.getSettingsManager().getTagDefaultColor() + cp.getClan().getColorTag() + plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketRight() + " " + plugin.getSettingsManager().getClanChatNameColor() + plugin.getSettingsManager().getClanChatPlayerBracketLeft() + player.getName() + plugin.getSettingsManager().getClanChatPlayerBracketRight() + " " + plugin.getSettingsManager().getClanChatMessageColor() + msg;

            plugin.getServer().getConsoleSender().sendMessage(eyeMessage);

            List<ClanPlayer> cps = cp.getClan().getMembers();

            for (ClanPlayer cpp : cps) {
                Player member = cpp.toPlayer();
                if (cpp.isMuted()) {
                    continue;
                }
                ChatBlock.sendMessage(member, message);
            }

            sendToAllSeeing(eyeMessage, cps);
        }
    }

    public void sendToAllSeeing(String msg, List<ClanPlayer> cps) {
        Collection<Player> players = Helper.getOnlinePlayers();

        for (Player player : players) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.admin.all-seeing-eye")) {
                boolean alreadySent = false;

                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                if (cp != null && cp.isMuted()) {
                    continue;
                }

                for (ClanPlayer cpp : cps) {
                    if (cpp.getName().equalsIgnoreCase(player.getName())) {
                        alreadySent = true;
                    }
                }

                if (!alreadySent) {
                    ChatBlock.sendMessage(player, ChatColor.DARK_GRAY + Helper.stripColors(msg));
                }
            }
        }
    }

    /**
     * Processes a ally chat command
     *
     * @param player
     * @param msg
     */
    public void processAllyChat(Player player, String msg) {
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp == null) {
            return;
        }

        String[] split = msg.split(" ");

        if (split.length == 0) {
            return;
        }

        String command = split[0];

        if (command.equals(plugin.getLang("on"))) {
            cp.setAllyChat(true);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have enabled ally chat");
        } else if (command.equals(plugin.getLang("off"))) {
            cp.setAllyChat(false);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have disabled ally chat");
        } else if (command.equals(plugin.getLang("join"))) {
            cp.setChannel(ClanPlayer.Channel.ALLY);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have joined ally chat");
        } else if (command.equals(plugin.getLang("leave"))) {
            cp.setChannel(ClanPlayer.Channel.NONE);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have left ally chat");
        } else if (command.equals(plugin.getLang("mute"))) {
            if (!cp.isMutedAlly()) {
                cp.setMutedAlly(true);
                ChatBlock.sendMessage(player, ChatColor.AQUA + "You have muted ally chat");
            } else {
                cp.setMutedAlly(false);
                ChatBlock.sendMessage(player, ChatColor.AQUA + "You have unmuted ally chat");
            }
        } else {
            String code = "" + ChatColor.AQUA + ChatColor.WHITE + ChatColor.AQUA + ChatColor.BLACK;
            String message = code + plugin.getSettingsManager().getAllyChatBracketColor() + plugin.getSettingsManager().getAllyChatTagBracketLeft() + plugin.getSettingsManager().getAllyChatTagColor() + plugin.getSettingsManager().getCommandAlly() + plugin.getSettingsManager().getAllyChatBracketColor() + plugin.getSettingsManager().getAllyChatTagBracketRight() + " " + plugin.getSettingsManager().getAllyChatNameColor() + plugin.getSettingsManager().getAllyChatPlayerBracketLeft() + player.getName() + plugin.getSettingsManager().getAllyChatPlayerBracketRight() + " " + plugin.getSettingsManager().getAllyChatMessageColor() + msg;
            SimpleClans.log(message);

            Player self = cp.toPlayer();
            ChatBlock.sendMessage(self, message);

            Set<ClanPlayer> allies = cp.getClan().getAllAllyMembers();
            allies.addAll(cp.getClan().getMembers());

            for (ClanPlayer ally : allies) {
                if (ally.isMutedAlly()) {
                    continue;
                }
                Player member = ally.toPlayer();
                if (SimpleClans.getInstance().hasUUID()) {
                    if (player.getUniqueId().equals(ally.getUniqueId())) {
                        continue;
                    }
                } else {
                    if (player.getName().equalsIgnoreCase(ally.getName())) {
                        continue;
                    }
                }
                ChatBlock.sendMessage(member, message);
            }
        }
    }

    /**
     * Processes a global chat command
     *
     * @param player
     * @param msg
     * @return boolean
     */
    public boolean processGlobalChat(Player player, String msg) {
        ClanPlayer cp;
        if (SimpleClans.getInstance().hasUUID()) {
            cp = plugin.getClanManager().getClanPlayer(player.getUniqueId());
        } else {
            cp = plugin.getClanManager().getClanPlayer(player.getName());
        }

        if (cp == null) {
            return false;
        }

        String[] split = msg.split(" ");

        if (split.length == 0) {
            return false;
        }

        String command = split[0];

        if (command.equals(plugin.getLang("on"))) {
            cp.setGlobalChat(true);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have enabled global chat");
        } else if (command.equals(plugin.getLang("off"))) {
            cp.setGlobalChat(false);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have disabled global chat");
        } else {
            return true;
        }

        return false;
    }
}