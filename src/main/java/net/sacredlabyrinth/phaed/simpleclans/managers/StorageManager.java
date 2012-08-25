package net.sacredlabyrinth.phaed.simpleclans.managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;

import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.storage.DBCore;
import net.sacredlabyrinth.phaed.simpleclans.storage.MySQLCore;
import net.sacredlabyrinth.phaed.simpleclans.storage.SQLiteCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author phaed
 */
public final class StorageManager {

    private SimpleClans plugin;
    private DBCore core;
    private HashMap<String, ChatBlock> chatBlocks = new HashMap<String, ChatBlock>();
    private PreparedStatement deleteClan, deleteClaim, deleteClanPlayer, deleteKills, deleteClaims,
            updateClan, updateClanPlayer,
            retrieveTotalDeathsPerPlayer, retrieveTotalKillsPerPlayer, retrieveTotalKillsPerClan,
            retrieveTotalDeathsPerClan, retrieveMostKilled, retrieveKillsPerPlayer, retrieveStrifes,
            retrieveClanStrifes, insertClan, insertClanPlayer, insertClaim, insertKill;

    /**
     *
     */
    public StorageManager(SimpleClans plugin)
    {
        this.plugin = plugin;
        initiateDB();
        prepareStatements();
        updateDatabase();
        importFromDatabase();

//        if (plugin.getSettingsManager().isClaimingEnabled()) {
//            plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new SaveThread(), 20L, 6000L);
//        }
    }

    /**
     * Saves all claims to the db
     */
    public void saveClaims()
    {
        for (Clan clan : plugin.getClanManager().getClans()) {
            for (ChunkLocation chunk : clan.getClaimedChunks()) {
                insertClaim(chunk, clan);
            }
        }
    }

    /**
     * Retrieve a player's pending chat lines
     *
     * @param sender
     * @return
     */
    public ChatBlock getChatBlock(CommandSender sender)
    {
        return chatBlocks.get(sender.getName());
    }

    /**
     * Store pending chat lines for a player
     *
     * @param sender
     * @param cb
     */
    public void addChatBlock(CommandSender sender, ChatBlock cb)
    {
        chatBlocks.put(sender.getName(), cb);
    }

    /**
     * Initiates the db
     */
    public void initiateDB()
    {
        if (plugin.getSettingsManager().isUseMysql()) {
            core = new MySQLCore(plugin.getSettingsManager().getHost(), plugin.getSettingsManager().getDatabase(), plugin.getSettingsManager().getUsername(), plugin.getSettingsManager().getPassword());

            if (core.checkConnection()) {
                SimpleClans.log("[SimpleClans] " + plugin.getLang("mysql.connection.successful"));

                if (!core.existsTable("sc_clans")) {
                    SimpleClans.log("Creating table: sc_clans");

                    String query = "CREATE TABLE IF NOT EXISTS `sc_clans` ( `id` bigint(20) NOT NULL auto_increment, `verified` tinyint(1) default '0', `tag` varchar(25) NOT NULL, `color_tag` varchar(25) NOT NULL, `name` varchar(100) NOT NULL, `friendly_fire` tinyint(1) default '0', `founded` bigint NOT NULL, `last_used` bigint NOT NULL, `packed_allies` text NOT NULL, `packed_rivals` text NOT NULL, `packed_bb` mediumtext NOT NULL, `cape_url` varchar(255) NOT NULL, `flags` text NOT NULL, `balance` double(64,2), PRIMARY KEY  (`id`), UNIQUE KEY `uq_simpleclans_1` (`tag`));";
                    core.execute(query);
                }

                if (!core.existsTable("sc_players")) {
                    SimpleClans.log("Creating table: sc_players");

                    String query = "CREATE TABLE IF NOT EXISTS `sc_players` ( `id` bigint(20) NOT NULL auto_increment, `name` varchar(16) NOT NULL, `leader` tinyint(1) default '0', `tag` varchar(25) NOT NULL, `friendly_fire` tinyint(1) default '0', `neutral_kills` int(11) default NULL, `rival_kills` int(11) default NULL, `civilian_kills` int(11) default NULL, `deaths` int(11) default NULL, `last_seen` bigint NOT NULL, `join_date` bigint NOT NULL, `trusted` tinyint(1) default '0', `flags` text NOT NULL, `packed_past_clans` text, `power` double(6,2) default 0.0, PRIMARY KEY  (`id`), UNIQUE KEY `uq_sc_players_1` (`name`));";
                    core.execute(query);
                }

                if (!core.existsTable("sc_kills")) {
                    SimpleClans.log("Creating table: sc_kills");

                    String query = "CREATE TABLE IF NOT EXISTS `sc_kills` ( `kill_id` bigint(20) NOT NULL auto_increment, `attacker` varchar(16) NOT NULL, `attacker_tag` varchar(16) NOT NULL, `victim` varchar(16) NOT NULL, `victim_tag` varchar(16) NOT NULL, `war` tinyint(1) default 0, `date` timestamp default CURRENT_TIMESTAMP, `kill_type` varchar(1) NOT NULL, PRIMARY KEY  (`kill_id`));";
                    core.execute(query);
                }

//                if (!core.existsTable("sc_strifes")) {
//                    SimpleClans.log("Creating table: sc_strifes");
//
//                    String query = "CREATE TABLE IF NOT EXISTS `sc_strifes` ( `strife_id` bigint(20) NOT NULL auto_increment, `attacker_clan` varchar(16) NOT NULL, `victim_clan` varchar(16) NOT NULL, PRIMARY KEY  (`strife_id`));";
//                    core.execute(query);
//                }

                if (!core.existsTable("sc_claims")) {
                    SimpleClans.log("Creating table: sc_claims");

                    String query = "CREATE TABLE IF NOT EXISTS `sc_claims` ( `id` bigint(20) NOT NULL auto_increment, `location` varchar(14) NOT NULL, `clan` varchar(16) NOT NULL, PRIMARY KEY  (`id`), UNIQUE KEY `uq_sc_claims_1` (`location`));";
                    core.execute(query);
                }
            } else {
                SimpleClans.log("[SimpleClans] " + ChatColor.RED + plugin.getLang("mysql.connection.failed"));
            }
        } else {

            SimpleClans.debug(Level.WARNING, "Using MySQL is highly recommended! (250x faster)");
            core = new SQLiteCore(plugin.getDataFolder().getPath());

            if (core.checkConnection()) {
                SimpleClans.log("[SimpleClans] " + plugin.getLang("sqlite.connection.successful"));

                if (!core.existsTable("sc_clans")) {
                    SimpleClans.log("Creating table: sc_clans");

                    String query = "CREATE TABLE IF NOT EXISTS `sc_clans` ( `id` bigint(20), `verified` tinyint(1) default '0', `tag` varchar(25) NOT NULL, `color_tag` varchar(25) NOT NULL, `name` varchar(100) NOT NULL, `friendly_fire` tinyint(1) default '0', `founded` bigint NOT NULL, `last_used` bigint NOT NULL, `packed_allies` text NOT NULL, `packed_rivals` text NOT NULL, `packed_bb` mediumtext NOT NULL, `cape_url` varchar(255) NOT NULL, `flags` text NOT NULL, `balance` double(64,2) default 0.0,  PRIMARY KEY  (`id`), UNIQUE (`tag`));";
                    core.execute(query);
                }

                if (!core.existsTable("sc_players")) {
                    SimpleClans.log("Creating table: sc_players");

                    String query = "CREATE TABLE IF NOT EXISTS `sc_players` ( `id` bigint(20), `name` varchar(16) NOT NULL, `leader` tinyint(1) default '0', `tag` varchar(25) NOT NULL, `friendly_fire` tinyint(1) default '0', `neutral_kills` int(11) default NULL, `rival_kills` int(11) default NULL, `civilian_kills` int(11) default NULL, `deaths` int(11) default NULL, `last_seen` bigint NOT NULL, `join_date` bigint NOT NULL, `trusted` tinyint(1) default '0', `flags` text NOT NULL, `packed_past_clans` text, `power` double(6,2) default 0.0, PRIMARY KEY  (`id`), UNIQUE (`name`));";
                    core.execute(query);
                }

                if (!core.existsTable("sc_kills")) {
                    SimpleClans.log("Creating table: sc_kills");

                    String query = "CREATE TABLE IF NOT EXISTS `sc_kills` ( `kill_id` bigint(20), `attacker` varchar(16) NOT NULL, `attacker_tag` varchar(16) NOT NULL, `victim` varchar(16) NOT NULL, `victim_tag` varchar(16) NOT NULL, `war` tinyint(1) default 0, `date` timestamp default CURRENT_TIMESTAMP, `kill_type` varchar(1) NOT NULL, PRIMARY KEY  (`kill_id`));";
                    core.execute(query);
                }

//                if (!core.existsTable("sc_strifes")) {
//                    SimpleClans.log("Creating table: sc_strifes");
//
//                    String query = "CREATE TABLE IF NOT EXISTS `sc_strifes` ( `strife_id` varchar(16) NOT NULL, `victim_clan` varchar(16) NOT NULL, PRIMARY KEY  (`strife_id`));";
//                    core.execute(query);
//                }

                if (!core.existsTable("sc_claims")) {
                    SimpleClans.log("Creating table: sc_claims");

                    String query = "CREATE TABLE IF NOT EXISTS `sc_claims` ( `id` bigint(20), `location` varchar(14) NOT NULL, `clan` varchar(16) NOT NULL, PRIMARY KEY  (`id`), UNIQUE (`location`));";
                    core.execute(query);
                }

            } else {
                SimpleClans.log("[SimpleClans] " + ChatColor.RED + plugin.getLang("sqlite.connection.failed"));
            }
        }
    }

    public void prepareStatements()
    {
        deleteClan = core.prepareStatement("DELETE FROM `sc_clans` WHERE tag = ?;");
        deleteClaim = core.prepareStatement("DELETE FROM `sc_claims` WHERE clan = ? AND location = ?;");
        deleteClanPlayer = core.prepareStatement("DELETE FROM `sc_players` WHERE name = ?;");
        deleteKills = core.prepareStatement("DELETE FROM `sc_kills` WHERE `attacker` = ?;");
        deleteClaims = core.prepareStatement("DELETE FROM `sc_claims` WHERE clan = ?;");
        updateClan = core.prepareStatement("UPDATE `sc_clans` SET verified = ?, tag = ?, color_tag = ?, name = ?, friendly_fire = ?, founded = ?, last_used = ?, packed_allies = ?, packed_rivals = ?, packed_bb = ?, cape_url = ?, balance = ?, flags = ? WHERE tag = ?;");
        updateClanPlayer = core.prepareStatement("UPDATE `sc_players` SET leader = ?, tag = ? , friendly_fire = ?, neutral_kills = ?, rival_kills = ?, civilian_kills = ?, deaths = ?, last_seen = ?, packed_past_clans = ?, trusted = ?, flags = ?, power = ? WHERE name = ?;");
        insertClan = core.prepareStatement("INSERT INTO `sc_clans` (  `verified`, `tag`, `color_tag`, `name`, `friendly_fire`, `founded`, `last_used`, `packed_allies`, `packed_rivals`, `packed_bb`, `cape_url`, `flags`, `balance`) VALUES ( ?, ?, ?, ?, ?, ?, 0, '', '', '', '', '', 0);");
        insertClanPlayer = core.prepareStatement("INSERT INTO `sc_players` (  `name`, `leader`, `tag`, `friendly_fire`, `neutral_kills`, `rival_kills`, `civilian_kills`, `deaths`, `last_seen`, `join_date`, `packed_past_clans`, `flags`) VALUES ( ? , 0, '', 0, 0, 0, 0, 0, ?, ?, '', '');");
        retrieveTotalDeathsPerPlayer = core.prepareStatement("SELECT victim, count(victim) AS kills FROM `sc_kills` GROUP BY victim ORDER BY 2 DESC;");
        retrieveTotalKillsPerPlayer = core.prepareStatement("SELECT attacker, count(attacker) AS kills FROM `sc_kills` GROUP BY attacker ORDER BY 2 DESC;");
        retrieveTotalKillsPerClan = core.prepareStatement("SELECT attacker_tag, count(attacker_tag) AS kills FROM `sc_kills` GROUP BY attacker_tag ORDER BY 2 DESC;");
        retrieveTotalDeathsPerClan = core.prepareStatement("SELECT victim_tag, count(victim_tag) AS kills FROM `sc_kills` GROUP BY victim_tag ORDER BY 2 DESC;");
        retrieveMostKilled = core.prepareStatement("SELECT attacker, victim, count(victim) AS kills FROM `sc_kills` GROUP BY attacker, victim ORDER BY 3 DESC;");
        retrieveKillsPerPlayer = core.prepareStatement("SELECT victim, count(victim) AS kills FROM `sc_kills` WHERE attacker = ? GROUP BY victim ORDER BY count(victim) DESC;");
        if (core instanceof MySQLCore) {
            insertClaim = core.prepareStatement("INSERT INTO sc_claims (location,clan) VALUES ( ?, ?) ON DUPLICATE KEY UPDATE location = ?, clan = ?;");
        } else {
            insertClaim = core.prepareStatement("INSERT OR IGNORE INTO sc_claims (location,clan) VALUES (?, ?); UPDATE sc_claims SET location = ?, clan = ? WHERE location LIKE ?;");
        }
        insertKill = core.prepareStatement("INSERT INTO `sc_kills` (  `attacker`, `attacker_tag`, `victim`, `victim_tag`, `war`, `kill_type`, `date`) VALUES ( ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);");
        //insertStrife = core.prepareStatement("INSERT INTO `sc_strifes` (  `attacker_clan`, `victim_clan`) VALUES ( ?, ?);");
        retrieveStrifes = core.prepareStatement("SELECT * FROM `sc_kills` WHERE ((`attacker_tag` = ? AND `victim_tag` = ?) OR (`victim_tag` = ? AND `attacker_tag` = ?)) AND `war` = 0;");
        retrieveClanStrifes = core.prepareStatement("SELECT * FROM `sc_kills` WHERE (`attacker_tag` = ? OR `victim_tag` = ?) AND `war` = 0;");
    }

    /**
     * Closes DB connection
     */
    public void closeConnection()
    {
        core.close();
    }

    /**
     * Import all data from database to memory
     */
    public void importFromDatabase()
    {
        plugin.getClanManager().cleanData();

        Set<Clan> clans = retrieveClans();

        purgeClans(clans);

        for (Clan clan : clans) {
            plugin.getClanManager().importClan(clan);
        }

        for (Clan clan : clans) {
            clan.validateWarring();
        }

        if (clans.size() > 0) {
            SimpleClans.log(MessageFormat.format("[SimpleClans] " + plugin.getLang("clans"), clans.size()));
        }

        Set<ClanPlayer> cps = retrieveClanPlayers();
        purgeClanPlayers(cps);

        for (ClanPlayer cp : cps) {
            Clan tm = cp.getClan();

            if (tm != null) {
                tm.importMember(cp);
            }
            plugin.getClanManager().importClanPlayer(cp);
        }

        if (cps.size() > 0) {
            SimpleClans.log(MessageFormat.format("[SimpleClans] " + plugin.getLang("clan.players"), cps.size()));
        }
    }

    private void purgeClans(Set<Clan> clans)
    {
        List<Clan> purge = new ArrayList<Clan>();

        for (Clan clan : clans) {
            if (clan.isVerified()) {
                if (clan.getInactiveDays() > plugin.getSettingsManager().getPurgeClan()) {
                    purge.add(clan);
                }
            } else {
                if (clan.getInactiveDays() > plugin.getSettingsManager().getPurgeUnverified()) {
                    purge.add(clan);
                }
            }
        }

        for (Clan clan : purge) {
            SimpleClans.log("[SimpleClans] " + MessageFormat.format(plugin.getLang("purging.clan"), clan.getName()));
            deleteClan(clan);
            clans.remove(clan);
        }
    }

    private void purgeClanPlayers(Set<ClanPlayer> cps)
    {
        List<ClanPlayer> purge = new ArrayList<ClanPlayer>();

        for (ClanPlayer cp : cps) {
            if (cp.getInactiveDays() > plugin.getSettingsManager().getPurgePlayers()) {
                if (!cp.isLeader()) {
                    purge.add(cp);
                }
            }
        }

        for (ClanPlayer cp : purge) {
            SimpleClans.log("[SimpleClans] " + MessageFormat.format(plugin.getLang("purging.player.data"), cp.getName()));
            deleteClanPlayer(cp);
            cps.remove(cp);
        }
    }

    public void deleteClaim(ChunkLocation chunk, Clan clan)
    {
        try {
            deleteClaim.setString(1, clan.getTag());
            deleteClaim.setString(2, chunk.toLocationString());
            deleteClaim.executeUpdate();
        } catch (SQLException ex) {
            SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
        }
    }

    public void insertClaim(ChunkLocation chunk, Clan clan)
    {
        String loc = chunk.toLocationString();
        String ownerClan = clan.getTag();
        try {
            if (core instanceof MySQLCore) {
                insertClaim.setString(1, loc);
                insertClaim.setString(2, ownerClan);
                insertClaim.setString(3, loc);
                insertClaim.setString(4, ownerClan);
            } else {
                insertClaim.setString(1, loc);
                insertClaim.setString(2, ownerClan);
                insertClaim.setString(3, loc);
                insertClaim.setString(4, ownerClan);
                insertClaim.setString(5, loc);
            }
            insertClaim.executeUpdate();
        } catch (SQLException ex) {
            SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
        }
    }

    /**
     * Retrieves all claims of a Clan
     *
     * @return
     */
    public Set<ChunkLocation> retrieveClaims(String tag)
    {
        Set<ChunkLocation> out = new HashSet<ChunkLocation>();

        String query = "SELECT * FROM `sc_claims` WHERE clan = '" + tag + "';";
        ResultSet res = core.select(query);

        if (res != null) {
            try {
                while (res.next()) {
                    try {
                        String location = res.getString("location");
                        String[] splitted = location.split(",");
                        String world = SettingsManager.getWorldByNumber(Integer.parseInt(splitted[0]));
                        int x = Integer.parseInt(splitted[1]);
                        int z = Integer.parseInt(splitted[2]);

                        ChunkLocation chunk = new ChunkLocation(world, x, z, false);

                        out.add(chunk);
                    } catch (Exception ex) {
                        SimpleClans.debug(null, ex);
                    }
                }
            } catch (SQLException ex) {
                SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
            }
        }

        return out;
    }

    /**
     * Retrieves all simple clans from the database
     *
     * @return
     */
    public Set<Clan> retrieveClans()
    {
        Set<Clan> out = new HashSet<Clan>();

        String query = "SELECT * FROM  `sc_clans`;";
        ResultSet res = core.select(query);

        if (res != null) {
            try {
                while (res.next()) {
                    try {
                        boolean verified = res.getBoolean("verified");
                        boolean friendly_fire = res.getBoolean("friendly_fire");
                        String tag = res.getString("tag");
                        String color_tag = Helper.parseColors(res.getString("color_tag"));
                        String name = res.getString("name");
                        String packed_allies = res.getString("packed_allies");
                        String packed_rivals = res.getString("packed_rivals");
                        String packed_bb = res.getString("packed_bb");
                        String cape_url = res.getString("cape_url");
                        String flags = res.getString("flags");
                        long founded = res.getLong("founded");
                        long last_used = res.getLong("last_used");
                        double balance = res.getDouble("balance");

                        if (founded == 0) {
                            founded = (new Date()).getTime();
                        }

                        if (last_used == 0) {
                            last_used = (new Date()).getTime();
                        }

                        Clan clan = new Clan(plugin);
                        clan.setFlags(flags);
                        clan.setVerified(verified);
                        clan.setFriendlyFire(friendly_fire);
                        clan.setTag(tag);
                        clan.setColorTag(color_tag);
                        clan.setName(name);
                        clan.setPackedAllies(packed_allies);
                        clan.setPackedRivals(packed_rivals);
                        clan.setPackedBb(packed_bb);
                        clan.setCapeUrl(cape_url);
                        clan.setFounded(founded);
                        clan.setLastUsed(last_used);
                        clan.setBalance(balance);
                        if (plugin.getSettingsManager().isClaimingEnabled()) {
                            clan.setClaimedChunks(retrieveClaims(tag));
                        }

                        out.add(clan);
                    } catch (Exception ex) {
                        SimpleClans.debug(null, ex);
                    }
                }
            } catch (SQLException ex) {
                SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
            }
        }

        return out;
    }

    /**
     * Retrieves all clan players from the database
     *
     * @return
     */
    public Set<ClanPlayer> retrieveClanPlayers()
    {
        Set<ClanPlayer> out = new HashSet<ClanPlayer>();

        String query = "SELECT * FROM  `sc_players`;";
        ResultSet res = core.select(query);

        if (res != null) {
            try {
                while (res.next()) {
                    try {
                        String name = res.getString("name");
                        String tag = res.getString("tag");
                        boolean leader = res.getBoolean("leader");
                        boolean friendly_fire = res.getBoolean("friendly_fire");
                        boolean trusted = res.getBoolean("trusted");
                        int neutral_kills = res.getInt("neutral_kills");
                        int rival_kills = res.getInt("rival_kills");
                        int civilian_kills = res.getInt("civilian_kills");
                        int deaths = res.getInt("deaths");
                        long last_seen = res.getLong("last_seen");
                        long join_date = res.getLong("join_date");
                        String flags = res.getString("flags");
                        String packed_past_clans = Helper.parseColors(res.getString("packed_past_clans"));
                        double power = res.getDouble("power");

                        if (last_seen == 0) {
                            last_seen = (new Date()).getTime();
                        }

                        if (join_date == 0) {
                            join_date = (new Date()).getTime();
                        }

                        ClanPlayer cp = new ClanPlayer(plugin);
                        cp.setFlags(flags);
                        cp.setName(name);
                        cp.setLeader(leader);
                        cp.setFriendlyFire(friendly_fire);
                        cp.setNeutralKills(neutral_kills);
                        cp.setRivalKills(rival_kills);
                        cp.setCivilianKills(civilian_kills);
                        cp.setDeaths(deaths);
                        cp.setLastSeen(last_seen);
                        cp.setJoinDate(join_date);
                        cp.setPackedPastClans(packed_past_clans);
                        cp.setTrusted(leader || trusted);
                        cp.setPower(power);

                        if (!tag.isEmpty()) {
                            Clan clan = plugin.getClanManager().getClan(tag);

                            if (clan != null) {
                                cp.setClan(clan);
                            } else {
                                SimpleClans.debug("The clan for " + name + " doesnt exist!");
                            }
                        }

                        out.add(cp);
                    } catch (Exception ex) {

                        SimpleClans.debug(null, ex);

                    }
                }
            } catch (SQLException ex) {
                SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
            }
        }

        return out;
    }

    /**
     * Insert a clan into the database
     *
     * @param clan
     */
    public void insertClan(Clan clan)
    {
        try {
            insertClan.setInt(1, (clan.isVerified() ? 1 : 0));
            insertClan.setString(2, clan.getTag());
            insertClan.setString(3, clan.getColorTag());
            insertClan.setString(4, clan.getName());
            insertClan.setInt(5, (clan.isFriendlyFire() ? 1 : 0));
            insertClan.setLong(6, clan.getFounded());

            insertClan.executeUpdate();
        } catch (SQLException ex) {
            SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
        }
    }

    /**
     * Update a clan to the database
     *
     * @param clan
     */
    public void updateClan(Clan clan)
    {
        clan.updateLastUsed();
        try {
            updateClan.setInt(1, (clan.isVerified() ? 1 : 0));
            updateClan.setString(2, clan.getTag());
            updateClan.setString(3, clan.getColorTag());
            updateClan.setString(4, clan.getName());
            updateClan.setInt(5, (clan.isFriendlyFire() ? 1 : 0));
            updateClan.setLong(6, clan.getFounded());
            updateClan.setLong(7, clan.getLastUsed());
            updateClan.setString(8, clan.getPackedAllies());
            updateClan.setString(9, clan.getPackedRivals());
            updateClan.setString(10, clan.getPackedBb());
            updateClan.setString(11, clan.getCapeUrl());
            updateClan.setDouble(12, clan.getBalance());
            updateClan.setString(13, clan.getFlags());
            updateClan.setString(14, clan.getTag());
            updateClan.executeUpdate();
        } catch (SQLException ex) {
            SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
        }
    }

    /**
     * Delete a clan from the database
     *
     * @param clan
     */
    public void deleteClan(Clan clan)
    {
        try {
            deleteClan.setString(1, clan.getTag());
            deleteClan.executeUpdate();
            deleteClaims.setString(1, clan.getTag());
            deleteClaims.executeUpdate();
        } catch (SQLException ex) {
            SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
        }
    }

    /**
     * Insert a clan player into the database
     *
     * @param cp
     */
    public void insertClanPlayer(ClanPlayer cp)
    {
        try {
//            `name`, `leader`, `tag`, `friendly_fire`, `neutral_kills`, `rival_kills`, `civilian_kills`, `deaths`, `last_seen`, `join_date`, `packed_past_clans`, `flags`) 
//            ( ? , 0, '', ?, 0, 0, 0, 0, ?, ?, '', '');"
            insertClanPlayer.setString(1, cp.getName());
            insertClanPlayer.setLong(2, cp.getLastSeen());
            insertClanPlayer.setLong(3, cp.getJoinDate());
            insertClanPlayer.executeUpdate();
        } catch (SQLException ex) {
            SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
        }
//        String query = "INSERT INTO `sc_players` (  `name`, `leader`, `tag`, `friendly_fire`, `neutral_kills`, `rival_kills`, `civilian_kills`, `deaths`, `last_seen`, `join_date`, `packed_past_clans`, `flags`) ";
//        String values = "VALUES ( '" + cp.getName() + "'," + (cp.isLeader() ? 1 : 0) + ",'" + Helper.escapeQuotes(cp.getTag()) + "'," + (cp.isFriendlyFire() ? 1 : 0) + "," + cp.getNeutralKills() + "," + cp.getRivalKills() + "," + cp.getCivilianKills() + "," + cp.getDeaths() + ",'" + cp.getLastSeen() + "',' " + cp.getJoinDate() + "','" + Helper.escapeQuotes(cp.getPackedPastClans()) + "','" + Helper.escapeQuotes(cp.getFlags()) + "');";
//        core.insert(query + values);
    }

    /**
     * Update a clan player to the database
     *
     * @param cp
     */
    public void updateClanPlayer(ClanPlayer cp)
    {
        try {
            cp.updateLastSeen();
            updateClanPlayer.setInt(1, (cp.isLeader() ? 1 : 0));
            updateClanPlayer.setString(2, cp.getTag());
            updateClanPlayer.setInt(3, (cp.isFriendlyFire() ? 1 : 0));
            updateClanPlayer.setInt(4, cp.getNeutralKills());
            updateClanPlayer.setInt(5, cp.getRivalKills());
            updateClanPlayer.setInt(6, cp.getCivilianKills());
            updateClanPlayer.setInt(7, cp.getDeaths());
            updateClanPlayer.setLong(8, cp.getLastSeen());
            updateClanPlayer.setString(9, cp.getPackedPastClans());
            updateClanPlayer.setInt(10, (cp.isTrusted() ? 1 : 0));
            updateClanPlayer.setString(11, cp.getFlags());
            updateClanPlayer.setDouble(12, cp.getPower());
            updateClanPlayer.setString(13, cp.getName());
            updateClanPlayer.executeUpdate();
        } catch (SQLException ex) {
            SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
        }
    }

    /**
     * Delete a clan player from the database
     *
     * @param cp
     */
    public void deleteClanPlayer(ClanPlayer cp)
    {

        try {
            deleteClanPlayer.setString(1, cp.getName());
            deleteClanPlayer.executeUpdate();
        } catch (SQLException ex) {
            SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
        }

        deleteKills(cp.getName());
    }

//    /**
//     * Insert a strife into the database
//     *
//     * @param attacker
//     * @param victim
//     * @param type
//     */
//    public void insertStrife(String attackerTag, Player victim, String victimTag)
//    {
//        try {
//            insertKill.setString(1, attackerTag);
//            insertKill.setString(2, victimTag);
//            insertKill.executeQuery();
//        } catch (SQLException ex) {
//            SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
//        }
//
////        String query = "INSERT INTO `sc_kills` (  `attacker`, `attacker_tag`, `victim`, `victim_tag`, `kill_type`) ";
////        String values = "VALUES ( '" + attacker.getName() + "','" + attackerTag + "','" + victim.getName() + "','" + victimTag + "','" + type + "');";
////
////        core.insert(query
////                + values);
//    }

    /**
     * Insert a kill into the database
     *
     * @param attacker
     * @param victim
     * @param type
     */
    public void insertKill(Player attacker, String attackerTag, Player victim, String victimTag, String type, boolean war)
    {
        try {
            insertKill.setString(1, attacker.getName());
            insertKill.setString(2, attackerTag);
            insertKill.setString(3, victim.getName());
            insertKill.setString(4, victimTag);
            if (war) {
                insertKill.setInt(5, 1);
            } else {
                insertKill.setInt(5, 0);
            }
            insertKill.setString(6, type);
            insertKill.executeUpdate();
        } catch (SQLException ex) {
            SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
        }

//        String query = "INSERT INTO `sc_kills` (  `attacker`, `attacker_tag`, `victim`, `victim_tag`, `kill_type`) ";
//        String values = "VALUES ( '" + attacker.getName() + "','" + attackerTag + "','" + victim.getName() + "','" + victimTag + "','" + type + "');";
//
//        core.insert(query
//                + values);
    }

    /**
     * Delete a player's kill record form the database
     *
     * @param playerName
     */
    public void deleteKills(String playerName)
    {
        try {
            deleteKills.setString(1, playerName);
            deleteKills.executeUpdate();
        } catch (SQLException ex) {
            SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
        }
//        String query = "DELETE FROM `sc_kills` WHERE `attacker` = '" + playerName + "'";
//        core.delete(query);
    }

    /**
     * Returns a map of victim->count of all kills that specific player did
     *
     * @param playerName
     * @return
     */
    public HashMap<String, Integer> getKillsPerPlayer(String playerName)
    {
        HashMap<String, Integer> out = new HashMap<String, Integer>();
        try {
            retrieveKillsPerPlayer.setString(1, playerName);
            ResultSet res = retrieveKillsPerPlayer.executeQuery();

            if (res != null) {

                while (res.next()) {
                    try {
                        String victim = res.getString("victim");
                        int kills = res.getInt("kills");
                        out.put(victim, kills);
                    } catch (Exception ex) {
                        SimpleClans.debug(ex.getMessage(), ex);
                    }
                }

            }
        } catch (SQLException ex) {
            SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
        }

        return out;
    }

    /**
     * Returns a map of tag->count of all kills
     *
     * @return
     */
    public HashMap<String, Integer> getMostKilled()
    {
        HashMap<String, Integer> out = new HashMap<String, Integer>();
        try {
            ResultSet res = retrieveMostKilled.executeQuery();

            if (res != null) {

                while (res.next()) {
                    try {
                        String attacker = res.getString("attacker");
                        String victim = res.getString("victim");
                        int kills = res.getInt("kills");
                        out.put(attacker + " " + victim, kills);
                    } catch (Exception ex) {
                        SimpleClans.debug(ex.getMessage(), ex);
                    }
                }
            }
        } catch (SQLException ex) {
            SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
        }

        return out;
    }

    /**
     * Returns a map of tag->count of all deaths by each clan
     *
     * @return
     */
    public HashMap<String, Integer> getTotalDeathsPerClan()
    {
        HashMap<String, Integer> out = new HashMap<String, Integer>();
        try {
            ResultSet res = retrieveTotalDeathsPerClan.executeQuery();

            if (res != null) {

                while (res.next()) {
                    try {
                        String victimTag = res.getString("victim_tag");
                        int kills = res.getInt("kills");
                        out.put(victimTag, kills);
                    } catch (Exception ex) {
                        SimpleClans.debug(ex.getMessage(), ex);
                    }
                }
            }
        } catch (SQLException ex) {
            SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
        }

        return out;
    }

    /**
     * Returns a map of tag->count of all kills by each clan
     *
     * @return
     */
    public HashMap<String, Integer> getTotalKillsPerClan()
    {
        HashMap<String, Integer> out = new HashMap<String, Integer>();
        try {
            ResultSet res = retrieveTotalKillsPerClan.executeQuery();

            if (res != null) {

                while (res.next()) {
                    try {
                        String victimTag = res.getString("attacker_tag");
                        int kills = res.getInt("kills");
                        out.put(victimTag, kills);
                    } catch (Exception ex) {
                        SimpleClans.debug(ex.getMessage(), ex);
                    }
                }
            }
        } catch (SQLException ex) {
            SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
        }

        return out;
    }

    /**
     * Returns a map of playerName->count of all kills by each player
     *
     * @return
     */
    public HashMap<String, Integer> getTotalKillsPerPlayer()
    {
        HashMap<String, Integer> out = new HashMap<String, Integer>();
        try {
            ResultSet res = retrieveTotalKillsPerPlayer.executeQuery();

            if (res != null) {
                while (res.next()) {
                    try {
                        String attacker = res.getString("attacker");
                        int kills = res.getInt("kills");
                        out.put(attacker, kills);
                    } catch (Exception ex) {
                        SimpleClans.debug(ex.getMessage(), ex);
                    }
                }
            }
        } catch (SQLException ex) {
            SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
        }

        return out;
    }

    /**
     * Returns a map of playerName->count of all kills by each player
     *
     * @return
     */
    public HashMap<String, Integer> getTotalDeathsPerPlayer()
    {
        HashMap<String, Integer> out = new HashMap<String, Integer>();
        try {
            ResultSet res = retrieveTotalDeathsPerPlayer.executeQuery();

            if (res != null) {

                while (res.next()) {
                    try {
                        String victim = res.getString("victim");
                        int kills = res.getInt("kills");
                        out.put(victim, kills);
                    } catch (Exception ex) {
                        SimpleClans.debug(ex.getMessage(), ex);
                    }
                }
            }
        } catch (SQLException ex) {
            SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
        }

        return out;
    }

    public HashMap<String, Integer> getStrifesOfClan(String clan)
    {
        HashMap<String, Integer> out = new HashMap<String, Integer>();
        try {
            retrieveClanStrifes.setString(1, clan);
            retrieveClanStrifes.setString(2, clan);
            ResultSet res = retrieveClanStrifes.executeQuery();

            if (res != null) {

                while (res.next()) {
                    try {

                        String attacker = res.getString("attacker_tag");
                        String victim = res.getString("victim_tag");
                        if (!clan.equals(attacker)) {
                            Integer strifes = out.get(attacker);
                            out.put(attacker, (strifes == null ? 0 : strifes) + 1);
                        }
                        if (!clan.equals(victim)) {
                            Integer strifes = out.get(victim);
                            out.put(victim, (strifes == null ? 0 : strifes) + 1);
                        }
                    } catch (Exception ex) {
                        SimpleClans.debug(ex.getMessage(), ex);
                    }
                }
            }
        } catch (SQLException ex) {
            SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
        }

        return out;
    }

    public int getStrifes(Clan clan, Clan opponenClan)
    {
        try {
            retrieveStrifes.setString(1, clan.getTag());
            retrieveStrifes.setString(2, opponenClan.getTag());
            retrieveStrifes.setString(3, opponenClan.getTag());
            retrieveStrifes.setString(4, clan.getTag());
            ResultSet res = retrieveStrifes.executeQuery();
            res.last();
            int rowCount = res.getRow();
            return rowCount;
        } catch (SQLException ex) {
            SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
        }
        return 0;
    }

    /**
     * Updates the database to the latest version
     *
     * @param
     */
    private void updateDatabase()
    {
        String query;

        //From 2.2.6.3 to 2.3
        if (!core.existsColumn("sc_clans", "balance")) {
            query = "ALTER TABLE sc_clans ADD COLUMN `balance` double(64,2) default 0.00;";
            core.execute(query);
        }

        //To 2.4
        if (!core.existsColumn("sc_players", "power")) {
            query = "ALTER TABLE sc_players ADD COLUMN `power` double(6,2) default 0.00;";
            core.execute(query);
        }
        //To 2.4
        if (!core.existsColumn("sc_kills", "war")) {
            query = "ALTER TABLE sc_kills ADD COLUMN `war` tinyint(1) default 0;";
            core.execute(query);
        }

        if (!core.existsColumn("sc_kills", "date")) {
            query = "ALTER TABLE sc_kills ADD COLUMN `date` timestamp;";
            core.execute(query);
        }

        if (core.existsColumn("sc_clans", "claims")) {
            if (core instanceof MySQLCore) {
                query = "ALTER TABLE sc_clans DROP COLUMN claims";
                core.execute(query);
            } else {
                query = "CREATE TABLE IF NOT EXISTS `sc_clans_tmp` ( `id` bigint(20), `verified` tinyint(1) default '0', `tag` varchar(25) NOT NULL, `color_tag` varchar(25) NOT NULL, `name` varchar(100) NOT NULL, `friendly_fire` tinyint(1) default '0', `founded` bigint NOT NULL, `last_used` bigint NOT NULL, `packed_allies` text NOT NULL, `packed_rivals` text NOT NULL, `packed_bb` mediumtext NOT NULL, `cape_url` varchar(255) NOT NULL, `flags` text NOT NULL, `balance` double(64,2) default 0.0,  PRIMARY KEY  (`id`), UNIQUE (`tag`));";
                core.execute(query);

                for (Clan clan : retrieveClans()) {

                    try {
                        query = "INSERT INTO `sc_clans_tmp` (  `verified`, `tag`, `color_tag`, `name`, `friendly_fire`, `founded`, `last_used`, `packed_allies`, `packed_rivals`, `packed_bb`, `cape_url`, `flags`, `balance`) VALUES (  " + (clan.isVerified() ? 1 : 0) + ", " + clan.getTag() + ", " + clan.getColorTag() + ", " + clan.getName() + ", " + (clan.isFriendlyFire() ? 1 : 0) + ", " + clan.getFounded() + ", 0, '', '', '', '', '', 0);";
                        core.getConnection().createStatement().execute(query);
                    } catch (SQLException e) {
                        SimpleClans.debug(null, e);
                        return;
                    }
                }


                try {

                    String old = "ALTER TABLE sc_clans RENAME TO sc_clans_backup;";
                    core.execute(old);
                    String rename = "ALTER TABLE sc_clans_tmp RENAME TO sc_clans;";
                    core.getConnection().createStatement().executeUpdate(rename);
                } catch (SQLException e) {
                    SimpleClans.debug(null, e);
                }
            }
        }


        boolean existsWar = core.existsColumn("sc_players", "war");
        boolean existsDate = core.existsColumn("sc_players", "date");

        //fail fixes
        if (existsDate || existsWar) {
            if (core instanceof MySQLCore) {
                if (existsWar) {
                    query = "ALTER TABLE sc_players DROP COLUMN `war`;";
                    core.execute(query);
                }

                if (existsDate) {
                    query = "ALTER TABLE sc_players DROP COLUMN `date`;";
                    core.execute(query);
                }
            } else {
                query = "CREATE TABLE IF NOT EXISTS `sc_players_tmp` ( `id` bigint(20), `name` varchar(16) NOT NULL, `leader` tinyint(1) default '0', `tag` varchar(25) NOT NULL, `friendly_fire` tinyint(1) default '0', `neutral_kills` int(11) default NULL, `rival_kills` int(11) default NULL, `civilian_kills` int(11) default NULL, `deaths` int(11) default NULL, `last_seen` bigint NOT NULL, `join_date` bigint NOT NULL, `trusted` tinyint(1) default '0', `flags` text NOT NULL, `packed_past_clans` text, `power` double(6,2) default 0.0, PRIMARY KEY  (`id`), UNIQUE (`name`));";
                core.execute(query);

                for (ClanPlayer cp : retrieveClanPlayers()) {

                    try {
                        query = "INSERT INTO `sc_players_tmp` (  `name`, `leader`, `tag`, `friendly_fire`, `neutral_kills`, `rival_kills`, `civilian_kills`, `deaths`, `last_seen`, `join_date`, `packed_past_clans`, `flags`) VALUES ( '" + cp.getName() + "' , 0, '', 0, 0, 0, 0, 0, " + cp.getLastSeen() + ", " + cp.getJoinDate() + ", '', '');";

                        core.getConnection().createStatement().execute(query);
                    } catch (SQLException e) {
                        SimpleClans.debug(null, e);
                        return;
                    }
                }


                try {
                    String old = "ALTER TABLE sc_players RENAME TO sc_players_backup;";
                    core.execute(old);
                    String rename = "ALTER TABLE sc_players_tmp RENAME TO sc_players;";
                    core.getConnection().createStatement().executeUpdate(rename);
                } catch (SQLException e) {
                    SimpleClans.debug(null, e);
                }
            }

        }
    }
}
