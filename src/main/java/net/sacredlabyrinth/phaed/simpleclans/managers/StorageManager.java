package net.sacredlabyrinth.phaed.simpleclans.managers;

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
import org.bukkit.entity.Player;

/**
 * @author phaed
 */
public final class StorageManager
{

    private SimpleClans plugin;
    private DBCore core;
    private HashMap<String, ChatBlock> chatBlocks = new HashMap<String, ChatBlock>();

    /**
     *
     */
    public StorageManager(SimpleClans plugin)
    {
        this.plugin = plugin;
        initiateDB();
        updateDatabase();
        importFromDatabase();
//        if (plugin.getSettingsManager().isClaimingEnabled()) {
//            plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new SaveThread(), 20L, 6000L);
//        }
    }

//    /**
//     * AutoSave thread
//     *
//     */
//    private class SaveThread implements Runnable
//    {
//        
//        @Override
//        public void run()
//        {
//            saveClaims();
//        }
//    }
    /**
     * Saves all claims to the db
     *
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
     * @param player
     * @return
     */
    public ChatBlock getChatBlock(Player player)
    {
        return chatBlocks.get(player.getName());
    }

    /**
     * Store pending chat lines for a player
     *
     * @param player
     * @param cb
     */
    public void addChatBlock(Player player, ChatBlock cb)
    {
        chatBlocks.put(player.getName(), cb);
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

                    String query = "CREATE TABLE IF NOT EXISTS `sc_kills` ( `kill_id` bigint(20) NOT NULL auto_increment, `attacker` varchar(16) NOT NULL, `attacker_tag` varchar(16) NOT NULL, `victim` varchar(16) NOT NULL, `victim_tag` varchar(16) NOT NULL, `kill_type` varchar(1) NOT NULL, PRIMARY KEY  (`kill_id`));";
                    core.execute(query);
                }

                if (!core.existsTable("sc_strifes")) {
                    SimpleClans.log("Creating table: sc_strifes");

                    String query = "CREATE TABLE IF NOT EXISTS `sc_strifes` ( `clan` varchar(16) NOT NULL, `opponent_clan` varchar(16) NOT NULL, `strifes` int(11) default NULL, PRIMARY KEY  (`clan`));";
                    core.execute(query);
                }

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

                    String query = "CREATE TABLE IF NOT EXISTS `sc_kills` ( `kill_id` bigint(20), `attacker` varchar(16) NOT NULL, `attacker_tag` varchar(16) NOT NULL, `victim` varchar(16) NOT NULL, `victim_tag` varchar(16) NOT NULL, `kill_type` varchar(1) NOT NULL, PRIMARY KEY  (`kill_id`));";
                    core.execute(query);
                }

                if (!core.existsTable("sc_strifes")) {
                    SimpleClans.log("Creating table: sc_strifes");

                    String query = "CREATE TABLE IF NOT EXISTS `sc_strifes` ( `clan` varchar(16) NOT NULL, `opponent_clan` varchar(16) NOT NULL, `strifes` int(11) default NULL, PRIMARY KEY  (`clan`));";
                    core.execute(query);
                }

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
        String query = "DELETE FROM `sc_claims` WHERE clan = '" + clan.getTag() + "' AND location = '" + chunk.toLocationString() + "';";

        core.delete(query);
    }

    public void insertClaim(ChunkLocation chunk, Clan clan)
    {
        String query;
        String loc = chunk.toLocationString();
        String ownerClan = clan.getTag();
        if (core instanceof MySQLCore) {
            query = "INSERT INTO sc_claims (location,clan) VALUES ('" + loc + "','" + ownerClan + "') ON DUPLICATE KEY UPDATE location = '" + loc + "', clan = '" + ownerClan + "';";
        } else {
            query = "INSERT OR IGNORE INTO sc_claims (location,clan) VALUES ('" + loc + "','" + ownerClan + "'); UPDATE sc_claims SET location = '" + loc + "', clan = '" + ownerClan + "' WHERE location LIKE '" + loc + "';";
        }

        core.execute(query);
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
                        clan.setClaimedChunks(retrieveClaims(tag));

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
     * Retrieves the strifes relative to another clan
     *
     * @param attackerclan
     * @param victimclan
     * @return
     */
    public Integer retrieveStrifes(Clan attackerclan, Clan victimclan)
    {
        String query = null;
        ResultSet res = null;
        try {
            if (Helper.existsEntry(core, "sc_strifes", "clan", attackerclan.getTag()) && Helper.existsEntry(core, "sc_strifes", "opponent_clan", victimclan.getTag())) {
                query = "SELECT `strifes` FROM `sc_strifes` WHERE `clan` = '" + attackerclan.getTag() + "' AND `opponent_clan` = '" + victimclan.getTag() + "';";
            } else if (Helper.existsEntry(core, "sc_strifes", "clan", victimclan.getTag()) && Helper.existsEntry(core, "sc_strifes", "opponent_clan", attackerclan.getTag())) {
                query = "SELECT `strifes` FROM `sc_strifes` WHERE `clan` = '" + victimclan.getTag() + "' AND `opponent_clan` = '" + attackerclan.getTag() + "';";
            }

        } catch (SQLException ex) {
            SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);

        }

        if (query != null) {
            res = core.select(query);
        }

        if (res != null) {
            try {
                while (res.next()) {
                    try {
                        return res.getInt("strifes");
                    } catch (Exception ex) {
                        SimpleClans.debug(null, ex);
                    }
                }
            } catch (SQLException ex) {
                SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
            }
        }
        return 0;
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
        String query = "INSERT INTO `sc_clans` (  `verified`, `tag`, `color_tag`, `name`, `friendly_fire`, `founded`, `last_used`, `packed_allies`, `packed_rivals`, `packed_bb`, `cape_url`, `flags`, `balance`) ";
        String values = "VALUES ( " + (clan.isVerified() ? 1 : 0) + ",'" + Helper.escapeQuotes(clan.getTag()) + "','" + Helper.escapeQuotes(clan.getColorTag()) + "','" + Helper.escapeQuotes(clan.getName()) + "'," + (clan.isFriendlyFire() ? 1 : 0) + ",'" + clan.getFounded() + "','" + clan.getLastUsed() + "','" + Helper.escapeQuotes(clan.getPackedAllies()) + "','" + Helper.escapeQuotes(clan.getPackedRivals()) + "','" + Helper.escapeQuotes(clan.getPackedBb()) + "','" + Helper.escapeQuotes(clan.getCapeUrl()) + "', '" + Helper.escapeQuotes(clan.getFlags()) + "','" + Helper.escapeQuotes(String.valueOf(clan.getBalance())) + "');";
        core.insert(query + values);
    }

    /**
     * Insert a strife to a clan
     *
     * @param attackerclan
     * @param victimclan
     */
    public void addStrife(Clan attackerclan, Clan victimclan, int amount)
    {
        try {
            String query = null;

            if (!Helper.existsEntry(core, "sc_strifes", "clan", attackerclan.getTag()) && !Helper.existsEntry(core, "sc_strifes", "opponent_clan", victimclan.getTag())) {
                query = "INSERT INTO  `sc_strifes` (`clan` ,`opponent_clan` ,`strifes`)VALUES ('" + attackerclan.getTag() + "',  '" + victimclan.getTag() + "',  '" + 1 + "');";
            } else if (!Helper.existsEntry(core, "sc_strifes", "clan", victimclan.getTag()) && !Helper.existsEntry(core, "sc_strifes", "opponent_clan", attackerclan.getTag())) {
                query = "INSERT INTO  `sc_strifes` (`clan` ,`opponent_clan` ,`strifes`)VALUES ('" + victimclan.getTag() + "',  '" + attackerclan.getTag() + "',  '" + 1 + "');";
            }
            if (Helper.existsEntry(core, "sc_strifes", "clan", attackerclan.getTag()) && Helper.existsEntry(core, "sc_strifes", "opponent_clan", victimclan.getTag())) {
                query = "UPDATE `sc_strifes` SET strifes = " + (retrieveStrifes(attackerclan, victimclan) + amount) + " WHERE `clan` = '" + attackerclan.getTag() + "' AND `opponent_clan` = '" + victimclan.getTag() + "';";
            } else if (Helper.existsEntry(core, "sc_strifes", "clan", victimclan.getTag()) && Helper.existsEntry(core, "sc_strifes", "opponent_clan", attackerclan.getTag())) {
                query = "UPDATE `sc_strifes` SET strifes = " + (retrieveStrifes(attackerclan, victimclan) + amount) + " WHERE `clan` = '" + victimclan.getTag() + "' AND `opponent_clan` = '" + attackerclan.getTag() + "';";
            }

            if (query != null) {
                core.execute(query);
            }
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
        String query = "UPDATE `sc_clans` SET verified = " + (clan.isVerified() ? 1 : 0) + ", tag = '" + Helper.escapeQuotes(clan.getTag()) + "', color_tag = '" + Helper.escapeQuotes(clan.getColorTag()) + "', name = '" + Helper.escapeQuotes(clan.getName()) + "', friendly_fire = " + (clan.isFriendlyFire() ? 1 : 0) + ", founded = '" + clan.getFounded() + "', last_used = '" + clan.getLastUsed() + "', packed_allies = '" + Helper.escapeQuotes(clan.getPackedAllies()) + "', packed_rivals = '" + Helper.escapeQuotes(clan.getPackedRivals()) + "', packed_bb = '" + Helper.escapeQuotes(clan.getPackedBb()) + "', cape_url = '" + Helper.escapeQuotes(clan.getCapeUrl()) + "', cape_url = '" + Helper.escapeQuotes(String.valueOf(clan.getCapeUrl())) + "', balance = '" + clan.getBalance() + "', flags = '" + Helper.escapeQuotes(clan.getFlags()) + "' WHERE tag = '" + Helper.escapeQuotes(clan.getTag()) + "';";
        core.update(query);
    }
//    
//    public void updateLastUsedClan(Clan clan) {
//        clan.updateLastUsed();
//        String query = "UPDATE `sc_clans` SET last_used = '" + clan.getLastUsed() + "' WHERE tag = '" + Helper.escapeQuotes(clan.getTag()) + "';";
//        core.update(query);
//    }
//    
//    public void updateLastSeenClanPlayer(ClanPlayer cp) {
//        cp.updateLastSeen();
//        String query = "UPDATE `sc_players` SET last_seen = '" + cp.getLastSeen() + "' WHERE name = '" + cp.getName() + "';";
//        core.update(query);
//    }

    /**
     * Delete a clan from the database
     *
     * @param clan
     */
    public void deleteClan(Clan clan)
    {
        String query = "DELETE FROM `sc_clans` WHERE tag = '" + clan.getTag() + "';";
        String strifes = "DELETE FROM `sc_strifes` WHERE clan = '" + clan.getTag() + "' AND opponent_clan = '" + clan.getTag() + "';";
        String claims = "DELETE FROM `sc_claims` WHERE clan = '" + clan.getTag() + "';";
        core.delete(query);
        core.delete(strifes);
        core.delete(claims);
    }

    /**
     * Insert a clan player into the database
     *
     * @param cp
     */
    public void insertClanPlayer(ClanPlayer cp)
    {
        String query = "INSERT INTO `sc_players` (  `name`, `leader`, `tag`, `friendly_fire`, `neutral_kills`, `rival_kills`, `civilian_kills`, `deaths`, `last_seen`, `join_date`, `packed_past_clans`, `flags`) ";
        String values = "VALUES ( '" + cp.getName() + "'," + (cp.isLeader() ? 1 : 0) + ",'" + Helper.escapeQuotes(cp.getTag()) + "'," + (cp.isFriendlyFire() ? 1 : 0) + "," + cp.getNeutralKills() + "," + cp.getRivalKills() + "," + cp.getCivilianKills() + "," + cp.getDeaths() + ",'" + cp.getLastSeen() + "',' " + cp.getJoinDate() + "','" + Helper.escapeQuotes(cp.getPackedPastClans()) + "','" + Helper.escapeQuotes(cp.getFlags()) + "');";
        core.insert(query + values);
    }

    /**
     * Update a clan player to the database
     *
     * @param cp
     */
    public void updateClanPlayer(ClanPlayer cp)
    {
        cp.updateLastSeen();
        String query = "UPDATE `sc_players` SET leader = " + (cp.isLeader() ? 1 : 0) + ", tag = '" + Helper.escapeQuotes(cp.getTag()) + "' , friendly_fire = " + (cp.isFriendlyFire() ? 1 : 0) + ", neutral_kills = " + cp.getNeutralKills() + ", rival_kills = " + cp.getRivalKills() + ", civilian_kills = " + cp.getCivilianKills() + ", deaths = " + cp.getDeaths() + ", last_seen = '" + cp.getLastSeen() + "', packed_past_clans = '" + Helper.escapeQuotes(cp.getPackedPastClans()) + "', trusted = " + (cp.isTrusted() ? 1 : 0) + ", flags='" + Helper.escapeQuotes(cp.getFlags()) + "', power = " + Helper.escapeQuotes(String.valueOf(cp.getPower())) + " WHERE name = '" + cp.getName() + "';";
        core.update(query);
    }

    /**
     * Delete a clan player from the database
     *
     * @param cp
     */
    public void deleteClanPlayer(ClanPlayer cp)
    {
        String query = "DELETE FROM `sc_players` WHERE name = '" + cp.getName() + "';";
        core.delete(query);

        deleteKills(cp.getName());
    }

    /**
     * Insert a kill into the database
     *
     * @param attacker
     * @param victim
     * @param type
     */
    public void insertKill(Player attacker, String attackerTag, Player victim, String victimTag, String type)
    {
        String query = "INSERT INTO `sc_kills` (  `attacker`, `attacker_tag`, `victim`, `victim_tag`, `kill_type`) ";
        String values = "VALUES ( '" + attacker.getName() + "','" + attackerTag + "','" + victim.getName() + "','" + victimTag + "','" + type + "');";
        core.insert(query + values);
    }

    /**
     * Delete a player's kill record form the database
     *
     * @param playerName
     */
    public void deleteKills(String playerName)
    {
        String query = "DELETE FROM `sc_kills` WHERE `attacker` = '" + playerName + "'";
        core.delete(query);
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

        String query = "SELECT victim, count(victim) AS kills FROM `sc_kills` WHERE attacker = '" + playerName + "' GROUP BY victim ORDER BY count(victim) DESC;";
        ResultSet res = core.select(query);

        if (res != null) {
            try {
                while (res.next()) {
                    try {
                        String victim = res.getString("victim");
                        int kills = res.getInt("kills");
                        out.put(victim, kills);
                    } catch (Exception ex) {
                        SimpleClans.debug(ex.getMessage(), ex);
                    }
                }
            } catch (SQLException ex) {
                SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
            }
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

        String query = "SELECT attacker, victim, count(victim) AS kills FROM `sc_kills` GROUP BY attacker, victim ORDER BY 3 DESC;";
        ResultSet res = core.select(query);

        if (res != null) {
            try {
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
            } catch (SQLException ex) {
                SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
            }
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

        String query = "SELECT victim_tag, count(victim_tag) AS kills FROM `sc_kills` GROUP BY victim_tag ORDER BY 2 DESC;";
        ResultSet res = core.select(query);

        if (res != null) {
            try {
                while (res.next()) {
                    try {
                        String victimTag = res.getString("victim_tag");
                        int kills = res.getInt("kills");
                        out.put(victimTag, kills);
                    } catch (Exception ex) {
                        SimpleClans.debug(ex.getMessage(), ex);
                    }
                }
            } catch (SQLException ex) {
                SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
            }
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

        String query = "SELECT attacker_tag, count(attacker_tag) AS kills FROM `sc_kills` GROUP BY attacker_tag ORDER BY 2 DESC;";
        ResultSet res = core.select(query);

        if (res != null) {
            try {
                while (res.next()) {
                    try {
                        String victimTag = res.getString("attacker_tag");
                        int kills = res.getInt("kills");
                        out.put(victimTag, kills);
                    } catch (Exception ex) {
                        SimpleClans.debug(ex.getMessage(), ex);
                    }
                }
            } catch (SQLException ex) {
                SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
            }
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

        String query = "SELECT attacker, count(attacker) AS kills FROM `sc_kills` GROUP BY attacker ORDER BY 2 DESC;";
        ResultSet res = core.select(query);

        if (res != null) {
            try {
                while (res.next()) {
                    try {
                        String attacker = res.getString("attacker");
                        int kills = res.getInt("kills");
                        out.put(attacker, kills);
                    } catch (Exception ex) {
                        SimpleClans.debug(ex.getMessage(), ex);
                    }
                }
            } catch (SQLException ex) {
                SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
            }
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

        String query = "SELECT victim, count(victim) AS kills FROM `sc_kills` GROUP BY victim ORDER BY 2 DESC;";
        ResultSet res = core.select(query);

        if (res != null) {
            try {
                while (res.next()) {
                    try {
                        String victim = res.getString("victim");
                        int kills = res.getInt("kills");
                        out.put(victim, kills);
                    } catch (Exception ex) {
                        SimpleClans.debug(ex.getMessage(), ex);


                    }
                }
            } catch (SQLException ex) {
                SimpleClans.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
            }
        }

        return out;
    }

    /**
     * Updates the database to the latest version
     *
     * @param
     */
    private void updateDatabase()
    {
        String query = null;

        //From 2.2.6.3 to 2.3
        if (!core.existsColumn("sc_clans", "balance")) {
            query = "ALTER TABLE sc_clans ADD COLUMN `balance` double(64,2);";
        }

        //From to 2.4
        if (!core.existsColumn("sc_players", "power")) {
            query = "ALTER TABLE sc_players ADD COLUMN `power` double(6,2);";
        }

        if (query != null) {
            core.execute(query);
        }
    }
}
