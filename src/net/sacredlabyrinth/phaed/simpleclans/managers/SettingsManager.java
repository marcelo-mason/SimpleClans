package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.util.config.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author phaed
 */
public final class SettingsManager
{
    private SimpleClans plugin;
    private String language;
    private boolean globalff;
    private boolean showUnverifiedOnList;
    private boolean requireVerification;
    private List<String> blacklistedWorlds;
    private List<String> bannedPlayers;
    private List<String> disallowedWords;
    private List<String> disallowedColors;
    private List<String> unRivableClans;
    private int rivalLimitPercent;
    private boolean ePurchaseCreation;
    private boolean ePurchaseVerification;
    private int eCreationPrice;
    private int eVerificationPrice;
    private String alertUrl;
    private boolean inGameTags;
    private boolean inGameTagsColored;
    private boolean clanCapes;
    private String defaultCapeUrl;
    private String serverName;
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
    private String pageTrustedColor;
    private String pageUnTrustedColor;
    private boolean bbShowOnLogin;
    private int bbSize;
    private String bbColor;
    private String bbAccentColor;
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
    private String tagBracketLeft;
    private String tagBracketRight;
    private String tagBracketColor;
    private boolean clanTrustByDefault;
    private boolean clanChatEnable;
    private String clanChatAnnouncementColor;
    private String clanChatMessageColor;
    private String clanChatNameColor;
    private String clanChatTagBracketLeft;
    private String clanChatTagBracketRight;
    private String clanChatBracketColor;
    private String clanChatPlayerBracketLeft;
    private String clanChatPlayerBracketRight;
    private double kwRival;
    private double kwNeutral;
    private double kwCivilian;
    private boolean useMysql;
    private String host;
    private String database;
    private String username;
    private String password;
    private boolean safeCivilians;

    /**
     *
     */
    public SettingsManager()
    {
        plugin = SimpleClans.getInstance();
        load();
    }

    /**
     * Load the configuration
     */
    @SuppressWarnings("unchecked")
    public void load()
    {
        Configuration config = getPlugin().getConfiguration();
        config.load();

        List<String> standardDisallowed = new ArrayList<String>();
        standardDisallowed.add("vip");

        List<String> unRivableClansDefault = new ArrayList<String>();
        unRivableClansDefault.add("admin");
        unRivableClansDefault.add("staff");
        unRivableClansDefault.add("mod");

        List<String> disallowedColorsDefault = new ArrayList<String>();
        disallowedColorsDefault.add("4");

        bannedPlayers = config.getStringList("settings.banned-players", new ArrayList<String>());
        disallowedColors = config.getStringList("settings.disallowed-tag-colors", disallowedColorsDefault);
        blacklistedWorlds = config.getStringList("settings.blacklisted-worlds", new ArrayList<String>());
        disallowedWords = config.getStringList("settings.disallowed-tags", standardDisallowed);
        unRivableClans = config.getStringList("settings.unrivable-clans", unRivableClansDefault);
        showUnverifiedOnList = config.getBoolean("settings.show-unverified-on-list", false);
        requireVerification = config.getBoolean("settings.new-clan-verification-required", true);
        language = config.getString("settings.language", "en");
        serverName = config.getString("settings.server-name", "&9MinecraftServer");
        chatTags = config.getBoolean("settings.display-chat-tags", true);
        rivalLimitPercent = config.getInt("settings.rival-limit-percent", 50);
        ePurchaseCreation = config.getBoolean("economy.purchase-clan-create", false);
        ePurchaseVerification = config.getBoolean("economy.purchase-clan-verify", false);
        eCreationPrice = config.getInt("economy.creation-price", 100);
        eVerificationPrice = config.getInt("economy.verification-price", 1000);
        alertUrl = config.getString("spout.alert-url", "http://sacredlabyrinth.net/siren.wav");
        inGameTags = config.getBoolean("spout.in-game-tags", true);
        inGameTagsColored = config.getBoolean("spout.in-game-tags-colored", false);
        clanCapes = config.getBoolean("spout.enable-clan-capes", true);
        defaultCapeUrl = config.getString("spout.default-cape-url", "http://i.imgur.com/pjqV6.png");
        purgeClan = config.getInt("purge.inactive-clan-days", 7);
        purgeUnverified = config.getInt("purge.unverified-clan-days", 2);
        purgePlayers = config.getInt("purge.inactive-player-data-days", 30);
        requestFreqencySecs = config.getInt("request.ask-frequency-secs", 60);
        requestMessageColor = config.getString("request.message-color", "b");
        pageSize = config.getInt("page.size", 13);
        pageSep = config.getString("page.separator", "-");
        pageSubTitleColor = config.getString("page.subtitle-color", "7");
        pageHeadingsColor = config.getString("page.headings-color", "8");
        pageLeaderColor = config.getString("page.leader-color", "4");
        pageTrustedColor = config.getString("page.trusted-color", "f");
        pageUnTrustedColor = config.getString("page.untrusted-color", "8");
        pageClanNameColor = config.getString("page.clan-name-color", "b");
        bbShowOnLogin = config.getBoolean("bb.show-on-login", true);
        bbSize = config.getInt("bb.size", 6);
        bbColor = config.getString("bb.color", "e");
        bbAccentColor = config.getString("bb.accent-color", "8");
        commandClan = config.getString("commands.clan", "clan");
        commandMore = config.getString("commands.more", "more");
        commandDeny = config.getString("commands.deny", "deny");
        commandAccept = config.getString("commands.accept", "accept");
        clanTrustByDefault = config.getBoolean("clan.trust-members-by-default", false);
        clanMinSizeToAlly = config.getInt("clan.min-size-to-set-ally", 3);
        clanMinSizeToRival = config.getInt("clan.min-size-to-set-rival", 3);
        clanMinLength = config.getInt("clan.min-length", 2);
        clanMaxLength = config.getInt("clan.max-length", 25);
        tagMinLength = config.getInt("tag.min-length", 2);
        tagMaxLength = config.getInt("tag.max-length", 5);
        tagDefaultColor = config.getString("tag.default-color", "8");
        tagSeparator = config.getString("tag.separator.char", " .");
        tagSeparatorColor = config.getString("tag.separator.color", "8");
        tagBracketColor = config.getString("tag.bracket.color", "8");
        tagBracketLeft = config.getString("tag.bracket.left", "");
        tagBracketRight = config.getString("tag.bracket.right", "");
        clanChatEnable = config.getBoolean("clanchat.enable", true);
        clanChatAnnouncementColor = config.getString("clanchat.announcement-color", "e");
        clanChatMessageColor = config.getString("clanchat.message-color", "b");
        clanChatNameColor = config.getString("clanchat.name-color", "e");
        clanChatBracketColor = config.getString("clanchat.tag-bracket.color", "8");
        clanChatTagBracketLeft = config.getString("clanchat.tag-bracket.left", "[");
        clanChatTagBracketRight = config.getString("clanchat.tag-bracket.right", "]");
        clanChatPlayerBracketLeft = config.getString("clanchat.player-bracket.left", "<");
        clanChatPlayerBracketRight = config.getString("clanchat.player-bracket.right", ">");
        kwRival = config.getDouble("kill-weights.rival", 1.5);
        kwNeutral = config.getDouble("kill-weights.neutral", 1);
        kwCivilian = config.getDouble("kill-weights.civilian", .5);
        useMysql = config.getBoolean("mysql.enable", false);
        host = config.getString("mysql.host", "localhost");
        database = config.getString("mysql.database", "");
        username = config.getString("mysql.username", "");
        password = config.getString("mysql.password", "");
        safeCivilians = config.getBoolean("safe-civilians", false);
        save();
    }

    private void save()
    {
        Configuration config = getPlugin().getConfiguration();
        config.load();

        config.setProperty("settings.language", language);
        config.setProperty("settings.banned-players", bannedPlayers);
        config.setProperty("settings.blacklisted-worlds", blacklistedWorlds);
        config.setProperty("settings.disallowed-tags", disallowedWords);
        config.setProperty("settings.disallowed-tag-colors", disallowedColors);
        config.setProperty("settings.show-unverified-on-list", showUnverifiedOnList);
        config.setProperty("settings.unrivable-clans", unRivableClans);
        config.setProperty("settings.new-clan-verification-required", requireVerification);
        config.setProperty("settings.server-name", serverName);
        config.setProperty("settings.display-chat-tags", chatTags);
        config.setProperty("settings.rival-limit-percent", rivalLimitPercent);
        config.setProperty("spout.alert-url", alertUrl);
        config.setProperty("economy.purchase-clan-create", ePurchaseCreation);
        config.setProperty("economy.purchase-clan-verify", ePurchaseVerification);
        config.setProperty("economy.creation-price", eCreationPrice);
        config.setProperty("economy.verification-price", eVerificationPrice);
        config.setProperty("spout.in-game-tags", inGameTags);
        config.setProperty("spout.in-game-tags-colored", inGameTagsColored);
        config.setProperty("spout.enable-clan-capes", clanCapes);
        config.setProperty("spout.default-cape-url", defaultCapeUrl);
        config.setProperty("purge.inactive-clan-days", purgeClan);
        config.setProperty("purge.unverified-clan-days", purgeUnverified);
        config.setProperty("purge.inactive-player-data-days", purgePlayers);
        config.setProperty("request.ask-frequency-secs", requestFreqencySecs);
        config.setProperty("request.message-color", requestMessageColor);
        config.setProperty("page.size", pageSize);
        config.setProperty("page.separator", pageSep);
        config.setProperty("page.subtitle-color", pageSubTitleColor);
        config.setProperty("page.headings-color", pageHeadingsColor);
        config.setProperty("page.leader-color", pageLeaderColor);
        config.setProperty("page.trusted-color", pageTrustedColor);
        config.setProperty("page.untrusted-color", pageUnTrustedColor);
        config.setProperty("page.clan-name-color", pageClanNameColor);
        config.setProperty("bb.size", bbSize);
        config.setProperty("bb.show-on-login", bbShowOnLogin);
        config.setProperty("bb.color", bbColor);
        config.setProperty("bb.accent-color", bbAccentColor);
        config.setProperty("commands.clan", commandClan);
        config.setProperty("commands.more", commandMore);
        config.setProperty("commands.deny", commandDeny);
        config.setProperty("commands.accept", commandAccept);
        config.setProperty("clan.trust-members-by-default", clanTrustByDefault);
        config.setProperty("clan.min-size-to-set-ally", clanMinSizeToAlly);
        config.setProperty("clan.min-size-to-set-rival", clanMinSizeToRival);
        config.setProperty("clan.min-length", clanMinLength);
        config.setProperty("clan.max-length", clanMaxLength);
        config.setProperty("tag.min-length", tagMinLength);
        config.setProperty("tag.max-length", tagMaxLength);
        config.setProperty("tag.default-color", tagDefaultColor);
        config.setProperty("tag.separator.char", tagSeparator);
        config.setProperty("tag.separator.color", tagSeparatorColor);
        config.setProperty("tag.bracket.color", tagBracketColor);
        config.setProperty("tag.bracket.left", tagBracketLeft);
        config.setProperty("tag.bracket.right", tagBracketRight);
        config.setProperty("clanchat.enable", clanChatEnable);
        config.setProperty("clanchat.announcement-color", clanChatAnnouncementColor);
        config.setProperty("clanchat.message-color", clanChatMessageColor);
        config.setProperty("clanchat.name-color", clanChatNameColor);
        config.setProperty("clanchat.tag-bracket.color", clanChatBracketColor);
        config.setProperty("clanchat.tag-bracket.left", clanChatTagBracketLeft);
        config.setProperty("clanchat.tag-bracket.right", clanChatTagBracketRight);
        config.setProperty("clanchat.player-bracket.left", clanChatPlayerBracketLeft);
        config.setProperty("clanchat.player-bracket.right", clanChatPlayerBracketRight);
        config.setProperty("kill-weights.rival", kwRival);
        config.setProperty("kill-weights.neutral", kwNeutral);
        config.setProperty("kill-weights.civilian", kwCivilian);
        config.setProperty("mysql.enable", useMysql);
        config.setProperty("mysql.host", host);
        config.setProperty("mysql.database", database);
        config.setProperty("mysql.username", username);
        config.setProperty("mysql.password", password);
        config.setProperty("safe-civilians", safeCivilians);
        config.save();
    }

    /**
     * Check whether a worlds is blacklisted
     * @param world the world
     * @return whether the world is blacklisted
     */
    public boolean isBlacklistedWorld(String world)
    {
        for (String w : blacklistedWorlds)
        {
            if (w.equalsIgnoreCase(world))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Check whether a word is disallowed
     * @param word the world
     * @return whether its a disallowed word
     */
    public boolean isDisallowedWord(String word)
    {
        for (String w : disallowedWords)
        {
            if (w.equalsIgnoreCase(word))
            {
                return true;
            }
        }

        return word.equalsIgnoreCase("clan") || word.equalsIgnoreCase(commandMore) || word.equalsIgnoreCase(commandDeny) || word.equalsIgnoreCase(commandAccept);

    }

    /**
     * Check whether a string has a disallowed color
     * @param str the string
     * @return whether the string contains the color code
     */
    public boolean hasDisallowedColor(String str)
    {
        for (String c : getDisallowedColors())
        {
            if (str.contains("&" + c))
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
     * @param tag the tag
     * @return whether the clan is unrivable
     */
    public boolean isUnrivable(String tag)
    {
        for (String t : getunRivableClans())
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
     * @param playerName the player's name
     * @return whether player is banned
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
     * @param playerName the player's name
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
     * @param playerName the player's name
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
        return Collections.unmodifiableList(bannedPlayers);
    }

    /**
     * @return the disallowedColors
     */
    public List<String> getDisallowedColors()
    {
        return Collections.unmodifiableList(disallowedColors);
    }

    /**
     * @return the unRivableClans
     */
    public List<String> getunRivableClans()
    {
        return Collections.unmodifiableList(unRivableClans);
    }

    /**
     * @return the rivalLimitPercent
     */
    public int getRivalLimitPercent()
    {
        return rivalLimitPercent;
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
        return Helper.parseColors(serverName);
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
     * @return the requestMessageColor
     */
    public String getRequestMessageColor()
    {
        return Helper.toColor(requestMessageColor);
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
        return Helper.toColor(pageHeadingsColor);
    }

    /**
     * @return the pageSubTitleColor
     */
    public String getPageSubTitleColor()
    {
        return Helper.toColor(pageSubTitleColor);
    }

    /**
     * @return the pageLeaderColor
     */
    public String getPageLeaderColor()
    {
        return Helper.toColor(pageLeaderColor);
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
        return Helper.toColor(bbColor);
    }

    /**
     * @return the bbAccentColor
     */
    public String getBbAccentColor()
    {
        return Helper.toColor(bbAccentColor);
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
        return Helper.toColor(pageClanNameColor);
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
        return Helper.toColor(tagDefaultColor);
    }

    /**
     * @return the tagSeparator
     */
    public String getTagSeparator()
    {
        if (tagSeparator.equals(" ."))
        {
            return ".";
        }

        return tagSeparator;
    }

    /**
     * @return the tagSeparatorColor
     */
    public String getTagSeparatorColor()
    {
        return Helper.toColor(tagSeparatorColor);
    }

    /**
     * @return the clanChatAnnouncementColor
     */
    public String getClanChatAnnouncementColor()
    {
        return Helper.toColor(clanChatAnnouncementColor);
    }

    /**
     * @return the clanChatMessageColor
     */
    public String getClanChatMessageColor()
    {
        return Helper.toColor(clanChatMessageColor);
    }

    /**
     * @return the clanChatNameColor
     */
    public String getClanChatNameColor()
    {
        return Helper.toColor(clanChatNameColor);
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
        return Helper.toColor(clanChatBracketColor);
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
    public double getKwRival()
    {
        return kwRival;
    }

    /**
     * @return the kwNeutral
     */
    public double getKwNeutral()
    {
        return kwNeutral;
    }

    /**
     * @return the kwCivilian
     */
    public double getKwCivilian()
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

    /**
     * @return the inGameTagsColored
     */
    public boolean isInGameTagsColored()
    {
        return inGameTagsColored;
    }

    /**
     * @return the showUnverifiedOnList
     */
    public boolean isShowUnverifiedOnList()
    {
        return showUnverifiedOnList;
    }

    /**
     * @return the clanTrustByDefault
     */
    public boolean isClanTrustByDefault()
    {
        return clanTrustByDefault;
    }

    /**
     * @return the pageTrustedColor
     */
    public String getPageTrustedColor()
    {
        return Helper.toColor(pageTrustedColor);
    }

    /**
     * @return the pageUnTrustedColor
     */
    public String getPageUnTrustedColor()
    {
        return Helper.toColor(pageUnTrustedColor);
    }

    /**
     * @return the globalff
     */
    public boolean isGlobalff()
    {
        return globalff;
    }

    /**
     * @param globalff the globalff to set
     */
    public void setGlobalff(boolean globalff)
    {
        this.globalff = globalff;
    }

    /**
     * @return the clanChatEnable
     */
    public boolean getClanChatEnable()
    {
        return clanChatEnable;
    }

    /**
     * @return the tagBracketLeft
     */
    public String getTagBracketLeft()
    {
        return tagBracketLeft;
    }

    /**
     * @return the tagBracketRight
     */
    public String getTagBracketRight()
    {
        return tagBracketRight;
    }

    /**
     * @return the tagBracketColor
     */
    public String getTagBracketColor()
    {
        return Helper.toColor(tagBracketColor);
    }

    /**
     * @return the ePurchaseCreation
     */
    public boolean isePurchaseCreation()
    {
        return ePurchaseCreation;
    }

    /**
     * @return the ePurchaseVerification
     */
    public boolean isePurchaseVerification()
    {
        return ePurchaseVerification;
    }

    /**
     * @return the eCreationPrice
     */
    public int geteCreationPrice()
    {
        return eCreationPrice;
    }

    /**
     * @return the eVerificationPrice
     */
    public int geteVerificationPrice()
    {
        return eVerificationPrice;
    }

    public String getLanguage()
    {
        return language;
    }

    public boolean isBbShowOnLogin()
    {
        return bbShowOnLogin;
    }

    public boolean getSafeCivilians()
    {
        return safeCivilians;
    }


}
