package net.sacredlabyrinth.phaed.simpleclans.managers;

import java.util.ArrayList;
import java.util.List;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

import org.bukkit.util.config.Configuration;

/**
 *
 * @author phaed
 */
public final class SettingsManager
{
    private SimpleClans plugin;
    private boolean requireVerification;
    private List<String> bannedPlayers;
    private List<String> disallowedWords;
    private List<String> disallowedColors;
    private List<String> unRivableSimpleClans;
    private int rivalLimitPercent;
    private int combatTagSeconds;
    private String alertUrl;
    private boolean inGameTags;
    private boolean clanCapes;
    private String defaultCapeUrl;
    private String serverName;
    private boolean ffDefault;
    private boolean chatTags;
    private int purgeClan;
    private int purgeUnverified;
    private int purgePlayers;
    private int requestFreqencySecs;
    private String requestMessageColor;
    private int pageSize;
    private String pageSep;
    private String pageHeadingsColor;
    private String pageSubTitleColor;
    private String pageLeaderColor;
    private String pageMemberColor;
    private int bbSize;
    private String bbColor;
    private String bbAccentColor;
    private String bbNewClan;
    private String commandClan;
    private String commandMore;
    private String commandDeny;
    private String commandAccept;
    private int clanMinSizeToAlly;
    private int clanMinSizeToRival;
    private int clanMinLength;
    private int clanMaxLength;
    private String pageClanNameColor;
    private int tagMinLength;
    private int tagMaxLength;
    private String tagDefaultColor;
    private String tagSeparator;
    private String tagSeparatorColor;
    private String clanChatAnnouncementColor;
    private String clanChatMessageColor;
    private String clanChatNameColor;
    private String clanChatTagBracketLeft;
    private String clanChatTagBracketRight;
    private String clanChatBracketColor;
    private String clanChatPlayerBracketLeft;
    private String clanChatPlayerBracketRight;
    private int kwRival;
    private int kwNeutral;
    private int kwCivilian;
    private boolean useMysql;
    private String host;
    private String database;
    private String username;
    private String password;

    /**
     *
     * @param plugin
     */
    public SettingsManager()
    {
        plugin = SimpleClans.getInstance();
        loadConfiguration();
    }

    /**
     * Load the configuration
     */
    @SuppressWarnings("unchecked")
    public void loadConfiguration()
    {
        if (!plugin.getDataFolder().exists())
        {
            getPlugin().getDataFolder().mkdir();
        }

        Configuration config = getPlugin().getConfiguration();
        config.load();

        List<String> standardDisallowed = new ArrayList<String>();
        standardDisallowed.add("vip");

        List<String> unRivableSimpleClansDefault = new ArrayList<String>();
        unRivableSimpleClansDefault.add("admin");
        unRivableSimpleClansDefault.add("mod");

        List<String> disallowedColorsDefault = new ArrayList<String>();
        disallowedColorsDefault.add("4");

        bannedPlayers = config.getStringList("settings.banned-players", new ArrayList<String>());
        disallowedColors = config.getStringList("settings.disallowed-tag-colors", disallowedColorsDefault);
        disallowedWords = config.getStringList("settings.disallowed-tags", standardDisallowed);
        unRivableSimpleClans = config.getStringList("settings.unrivable-simpleclans", unRivableSimpleClansDefault);
        requireVerification = config.getBoolean("settings.new-clan-verification-required", true);
        serverName = config.getString("settings.server-name", "&9MinecraftServer");
        ffDefault = config.getBoolean("settings.ff-prevent-by-default", true);
        chatTags = config.getBoolean("settings.display-chat-tags", true);
        combatTagSeconds = config.getInt("settings.combat-tag-seconds", 5);
        rivalLimitPercent = config.getInt("settings.rival-limit-percent", 50);

        alertUrl = config.getString("spout.alert-url", "http://sacredlabyrinth.net/siren.wav");
        inGameTags = config.getBoolean("spout.in-game-tags", true);
        clanCapes = config.getBoolean("spout.enable-clan-capes", true);
        defaultCapeUrl = config.getString("spout.default-cape-url", "http://sacredlabyrinth.net/leader.png");

        purgeClan = config.getInt("purge.inactive-clan-days", 7);
        purgeUnverified = config.getInt("purge.unverified-clan-days", 2);
        purgePlayers = config.getInt("purge.inactive-player-data-days", 30);

        setRequestFreqencySecs(config.getInt("request.ask-frequency-secs", 60));
        requestMessageColor = config.getString("request.message-color", "b");

        pageSize = config.getInt("page.size", 11);
        pageSep = config.getString("page.separator", "-");
        pageSubTitleColor = config.getString("page.subtitle-color", "7");
        pageHeadingsColor = config.getString("page.headings-color", "8");
        pageLeaderColor = config.getString("page.leader-color", "4");
        pageMemberColor = config.getString("page.member-color", "f");
        pageClanNameColor = config.getString("page.clan-name-color", "b");

        bbSize = config.getInt("bb.size", 10);
        bbColor = config.getString("bb.color", "f");
        bbAccentColor = config.getString("bb.accent-color", "8");

        commandClan = config.getString("commands.clan", "clan");
        commandMore = config.getString("commands.more", "more");
        commandDeny = config.getString("commands.deny", "deny");
        commandAccept = config.getString("commands.accept", "accept");

        clanMinSizeToAlly = config.getInt("clan.min-size-to-set-ally", 3);
        clanMinSizeToRival = config.getInt("clan.min-size-to-set-rival", 3);
        clanMinLength = config.getInt("clan.min-length", 2);
        clanMaxLength = config.getInt("clan.max-length", 25);

        tagMinLength = config.getInt("tag.min-length", 2);
        tagMaxLength = config.getInt("tag.max-length", 5);
        tagDefaultColor = config.getString("tag.default-color", "8");
        tagSeparator = config.getString("tag.separator.char", " .");
        tagSeparatorColor = config.getString("tag.separator.color", "8");

        clanChatAnnouncementColor = config.getString("clanchat.announcement-color", "e");
        clanChatMessageColor = config.getString("clanchat.message-color", "b");
        clanChatNameColor = config.getString("clanchat.name-color", "e");
        clanChatBracketColor = config.getString("clanchat.tag-bracket.color", "8");
        clanChatTagBracketLeft = config.getString("clanchat.tag-bracket.left", "[");
        clanChatTagBracketRight = config.getString("clanchat.tag-bracket.right", "]");
        clanChatPlayerBracketLeft = config.getString("clanchat.player-bracket.left", "<");
        clanChatPlayerBracketRight = config.getString("clanchat.player-bracket.right", ">");

        kwRival = config.getInt("kill-weights.rival", 3);
        kwNeutral = config.getInt("kill-weights.neutral", 2);
        kwCivilian = config.getInt("kill-weights.civilian", 1);

        useMysql = config.getBoolean("mysql.enable", false);
        host = config.getString("mysql.host", "localhost");
        database = config.getString("mysql.database", "minecraft");
        username = config.getString("mysql.username", "");
        password = config.getString("mysql.password", "");
        save();
    }

    private void save()
    {
        Configuration config = getPlugin().getConfiguration();
        config.load();

        config.setProperty("settings.banned-players", getBannedPlayers());
        config.setProperty("settings.disallowed-tags", getDisallowedWords());
        config.setProperty("settings.disallowed-tag-colors", getDisallowedColors());
        config.setProperty("settings.unrivable-simpleclans", getUnRivableSimpleClans());
        config.setProperty("settings.new-clan-verification-required", isRequireVerification());
        config.setProperty("settings.server-name", getServerName());
        config.setProperty("settings.ff-prevent-by-default", isFfDefault());
        config.setProperty("settings.display-chat-tags", isChatTags());
        config.setProperty("settings.combat-tag-seconds", getCombatTagSeconds());
        config.setProperty("settings.rival-limit-percent", getRivalLimitPercent());

        config.setProperty("spout.alert-url", getAlertUrl());
        config.setProperty("spout.in-game-tags", isInGameTags());
        config.setProperty("spout.enable-clan-capes", isClanCapes());
        config.setProperty("spout.default-cape-url", getDefaultCapeUrl());

        config.setProperty("purge.inactive-clan-days", getPurgeClan());
        config.setProperty("purge.unverified-clan-days", getPurgeUnverified());
        config.setProperty("purge.inactive-player-data-days", getPurgePlayers());

        config.setProperty("request.ask-frequency-secs", getRequestFreqencySecs());
        config.setProperty("request.message-color", getRequestMessageColor());

        config.setProperty("page.size", getPageSize());
        config.setProperty("page.separator", getPageSep());
        config.setProperty("page.subtitle-color", getPageSubTitleColor());
        config.setProperty("page.headings-color", getPageHeadingsColor());
        config.setProperty("page.leader-color", getPageLeaderColor());
        config.setProperty("page.member-color", getPageMemberColor());
        config.setProperty("page.clan-name-color", getPageClanNameColor());

        config.setProperty("bb.size", getBbSize());
        config.setProperty("bb.color", getBbColor());
        config.setProperty("bb.accent-color", getBbAccentColor());

        config.setProperty("commands.clan", getCommandClan());
        config.setProperty("commands.more", getCommandMore());
        config.setProperty("commands.deny", getCommandDeny());
        config.setProperty("commands.accept", getCommandAccept());

        config.setProperty("clan.min-size-to-set-ally", getClanMinSizeToAlly());
        config.setProperty("clan.min-size-to-set-rival", getClanMinSizeToRival());
        config.setProperty("clan.min-length", getClanMinLength());
        config.setProperty("clan.max-length", getClanMaxLength());

        config.setProperty("tag.min-length", getTagMinLength());
        config.setProperty("tag.max-length", getTagMaxLength());
        config.setProperty("tag.default-color", getTagDefaultColor());
        config.setProperty("tag.separator.char", getTagSeparator());
        config.setProperty("tag.separator.color", getTagSeparatorColor());

        config.setProperty("clanchat.announcement-color", getClanChatAnnouncementColor());
        config.setProperty("clanchat.message-color", getClanChatMessageColor());
        config.setProperty("clanchat.name-color", getClanChatNameColor());
        config.setProperty("clanchat.tag-bracket.left", getClanChatTagBracketLeft());
        config.setProperty("clanchat.tag-bracket.right", getClanChatTagBracketRight());
        config.setProperty("clanchat.tag-bracket.color", getClanChatBracketColor());
        config.setProperty("clanchat.player-bracket.left", getClanChatPlayerBracketLeft());
        config.setProperty("clanchat.player-bracket.right", getClanChatPlayerBracketRight());

        config.setProperty("kill-weights.civilian", getKwCivilian());
        config.setProperty("kill-weights.rival", getKwRival());
        config.setProperty("kill-weights.neutral", getKwNeutral());

        config.setProperty("mysql.enable", isUseMysql());
        config.setProperty("mysql.host", getHost());
        config.setProperty("mysql.database", getDatabase());
        config.setProperty("mysql.username", getUsername());
        config.setProperty("mysql.password", getPassword());

        config.save();
    }

    /**
     * Check whether a word is disallowed
     * @param word
     * @return
     */
    public boolean isDisallowedWord(String word)
    {
        for (String w : getDisallowedWords())
        {
            if (w.equalsIgnoreCase(word))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Check whether a color is disallowed
     * @param color
     * @return
     */
    public boolean isDisallowedColor(String color)
    {
        for (String c : getDisallowedColors())
        {
            if (c.equalsIgnoreCase(color))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Check whether a string has a disallowed color
     * @param str
     * @return
     */
    public boolean hasDisallowedColor(String str)
    {
        for (String c : getDisallowedColors())
        {
            if (str.contains("&"+c))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @return a comma delimited string with all disallowed colors
     */
    public String getDisallowedColorString()
    {
        String out = "";

        for (String c : getDisallowedColors())
        {
            out += c + ", ";
        }

        return Helper.stripTrailing(out, ", ");
    }

    /**
     * Check whether a clan is un-rivable
     * @param tag
     * @return
     */
    public boolean isUnrivable(String tag)
    {
        for (String t : getUnRivableSimpleClans())
        {
            if (t.equalsIgnoreCase(tag))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Check whether a player is banned
     * @param playerName
     * @return
     */
    public boolean isBanned(String playerName)
    {
        for (String pl : getBannedPlayers())
        {
            if (pl.equalsIgnoreCase(playerName))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Add a player to the banned list
     * @param playerName
     */
    public void addBanned(String playerName)
    {
        if (!bannedPlayers.contains(playerName))
        {
            getBannedPlayers().add(playerName);
        }

        save();
    }

    /**
     * Remove a player from the banned list
     * @param playerName
     */
    public void removeBanned(String playerName)
    {
        if (getBannedPlayers().contains(playerName))
        {
            getBannedPlayers().remove(playerName);
        }

        save();
    }

    /**
     * @return the plugin
     */
    public SimpleClans getPlugin()
    {
        return plugin;
    }

    /**
     * @return the requireVerification
     */
    public boolean isRequireVerification()
    {
        return requireVerification;
    }

    /**
     * @return the bannedPlayers
     */
    public List<String> getBannedPlayers()
    {
        return bannedPlayers;
    }

    /**
     * @return the disallowedWords
     */
    public List<String> getDisallowedWords()
    {
        return disallowedWords;
    }

    /**
     * @return the disallowedColors
     */
    public List<String> getDisallowedColors()
    {
        return disallowedColors;
    }

    /**
     * @return the unRivableSimpleClans
     */
    public List<String> getUnRivableSimpleClans()
    {
        return unRivableSimpleClans;
    }

    /**
     * @return the rivalLimitPercent
     */
    public int getRivalLimitPercent()
    {
        return rivalLimitPercent;
    }

    /**
     * @return the combatTagSeconds
     */
    public int getCombatTagSeconds()
    {
        return combatTagSeconds;
    }

    /**
     * @return the alertUrl
     */
    public String getAlertUrl()
    {
        return alertUrl;
    }

    /**
     * @return the inGameTags
     */
    public boolean isInGameTags()
    {
        return inGameTags;
    }

    /**
     * @return the clanCapes
     */
    public boolean isClanCapes()
    {
        return clanCapes;
    }

    /**
     * @return the defaultCapeUrl
     */
    public String getDefaultCapeUrl()
    {
        return defaultCapeUrl;
    }

    /**
     * @return the serverName
     */
    public String getServerName()
    {
        return serverName;
    }

    /**
     * @return the ffDefault
     */
    public boolean isFfDefault()
    {
        return ffDefault;
    }

    /**
     * @return the chatTags
     */
    public boolean isChatTags()
    {
        return chatTags;
    }

    /**
     * @return the purgeClan
     */
    public int getPurgeClan()
    {
        return purgeClan;
    }

    /**
     * @return the purgeUnverified
     */
    public int getPurgeUnverified()
    {
        return purgeUnverified;
    }

    /**
     * @return the purgePlayers
     */
    public int getPurgePlayers()
    {
        return purgePlayers;
    }

    /**
     * @return the requestFreqencySecs
     */
    public int getRequestFreqencySecs()
    {
        return requestFreqencySecs;
    }

    /**
     * @param requestFreqencySecs the requestFreqencySecs to set
     */
    public void setRequestFreqencySecs(int requestFreqencySecs)
    {
        this.requestFreqencySecs = requestFreqencySecs;
    }

    /**
     * @return the requestMessageColor
     */
    public String getRequestMessageColor()
    {
        return requestMessageColor;
    }

    /**
     * @return the pageSize
     */
    public int getPageSize()
    {
        return pageSize;
    }

    /**
     * @return the pageSep
     */
    public String getPageSep()
    {
        return pageSep;
    }

    /**
     * @return the pageHeadingsColor
     */
    public String getPageHeadingsColor()
    {
        return pageHeadingsColor;
    }

    /**
     * @return the pageSubTitleColor
     */
    public String getPageSubTitleColor()
    {
        return pageSubTitleColor;
    }

    /**
     * @return the pageLeaderColor
     */
    public String getPageLeaderColor()
    {
        return pageLeaderColor;
    }

    /**
     * @return the pageMemberColor
     */
    public String getPageMemberColor()
    {
        return pageMemberColor;
    }

    /**
     * @return the bbSize
     */
    public int getBbSize()
    {
        return bbSize;
    }

    /**
     * @return the bbColor
     */
    public String getBbColor()
    {
        return bbColor;
    }

    /**
     * @return the bbAccentColor
     */
    public String getBbAccentColor()
    {
        return bbAccentColor;
    }

    /**
     * @return the bbNewClan
     */
    public String getBbNewClan()
    {
        return bbNewClan;
    }

    /**
     * @return the commandClan
     */
    public String getCommandClan()
    {
        return commandClan;
    }

    /**
     * @return the commandMore
     */
    public String getCommandMore()
    {
        return commandMore;
    }

    /**
     * @return the commandDeny
     */
    public String getCommandDeny()
    {
        return commandDeny;
    }

    /**
     * @return the commandAccept
     */
    public String getCommandAccept()
    {
        return commandAccept;
    }

    /**
     * @return the clanMinSizeToAlly
     */
    public int getClanMinSizeToAlly()
    {
        return clanMinSizeToAlly;
    }

    /**
     * @return the clanMinSizeToRival
     */
    public int getClanMinSizeToRival()
    {
        return clanMinSizeToRival;
    }

    /**
     * @return the clanMinLength
     */
    public int getClanMinLength()
    {
        return clanMinLength;
    }

    /**
     * @return the clanMaxLength
     */
    public int getClanMaxLength()
    {
        return clanMaxLength;
    }

    /**
     * @return the pageClanNameColor
     */
    public String getPageClanNameColor()
    {
        return pageClanNameColor;
    }

    /**
     * @return the tagMinLength
     */
    public int getTagMinLength()
    {
        return tagMinLength;
    }

    /**
     * @return the tagMaxLength
     */
    public int getTagMaxLength()
    {
        return tagMaxLength;
    }

    /**
     * @return the tagDefaultColor
     */
    public String getTagDefaultColor()
    {
        return tagDefaultColor;
    }

    /**
     * @return the tagSeparator
     */
    public String getTagSeparator()
    {
        return tagSeparator;
    }

    /**
     * @return the tagSeparatorColor
     */
    public String getTagSeparatorColor()
    {
        return tagSeparatorColor;
    }

    /**
     * @return the clanChatAnnouncementColor
     */
    public String getClanChatAnnouncementColor()
    {
        return clanChatAnnouncementColor;
    }

    /**
     * @return the clanChatMessageColor
     */
    public String getClanChatMessageColor()
    {
        return clanChatMessageColor;
    }

    /**
     * @return the clanChatNameColor
     */
    public String getClanChatNameColor()
    {
        return clanChatNameColor;
    }

    /**
     * @return the clanChatTagBracketLeft
     */
    public String getClanChatTagBracketLeft()
    {
        return clanChatTagBracketLeft;
    }

    /**
     * @return the clanChatTagBracketRight
     */
    public String getClanChatTagBracketRight()
    {
        return clanChatTagBracketRight;
    }

    /**
     * @return the clanChatBracketColor
     */
    public String getClanChatBracketColor()
    {
        return clanChatBracketColor;
    }

    /**
     * @return the clanChatPlayerBracketLeft
     */
    public String getClanChatPlayerBracketLeft()
    {
        return clanChatPlayerBracketLeft;
    }

    /**
     * @return the clanChatPlayerBracketRight
     */
    public String getClanChatPlayerBracketRight()
    {
        return clanChatPlayerBracketRight;
    }

    /**
     * @return the kwRival
     */
    public int getKwRival()
    {
        return kwRival;
    }

    /**
     * @return the kwNeutral
     */
    public int getKwNeutral()
    {
        return kwNeutral;
    }

    /**
     * @return the kwCivilian
     */
    public int getKwCivilian()
    {
        return kwCivilian;
    }

    /**
     * @return the useMysql
     */
    public boolean isUseMysql()
    {
        return useMysql;
    }

    /**
     * @return the host
     */
    public String getHost()
    {
        return host;
    }

    /**
     * @return the database
     */
    public String getDatabase()
    {
        return database;
    }

    /**
     * @return the username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }
}
