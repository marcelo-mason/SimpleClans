package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author phaed
 */
public final class SettingsManager
{
    private boolean teleportOnSpawn;
    private boolean dropOnHome;
    private boolean keepOnHome;
    private boolean debugging;
    private SimpleClans plugin;
    private boolean mChatIntegration;
    private boolean pvpOnlywhileInWar;
    private boolean useColorCodeFromPrefix;
    private boolean confirmationForPromote;
    private boolean confirmationForDemote;
    private boolean globalff;
    private boolean showUnverifiedOnList;
    private boolean requireVerification;
    private List<Object> itemsList;
    private List<Object> blacklistedWorlds;
    private List<Object> bannedPlayers;
    private List<Object> disallowedWords;
    private List<Object> disallowedColors;
    private List<Object> unRivableClans;
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
    private String commandAlly;
    private String commandGlobal;
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
    private boolean allyChatEnable;
    private String allyChatMessageColor;
    private String allyChatNameColor;
    private String allyChatTagColor;
    private String allyChatTagBracketLeft;
    private String allyChatTagBracketRight;
    private String allyChatBracketColor;
    private String allyChatPlayerBracketLeft;
    private String allyChatPlayerBracketRight;
    private boolean clanChatEnable;
    private String clanChatAnnouncementColor;
    private String clanChatMessageColor;
    private String clanChatNameColor;
    private String clanChatTagBracketLeft;
    private String clanChatTagBracketRight;
    private String clanChatBracketColor;
    private String clanChatPlayerBracketLeft;
    private String clanChatPlayerBracketRight;
    private boolean clanFFOnByDefault;
    private double kwRival;
    private double kwNeutral;
    private double kwCivilian;
    private boolean useMysql;
    private String host;
    private String database;
    private String username;
    private String password;
    private boolean safeCivilians;
    private File main;
    private FileConfiguration config;
    private boolean compatMode;
    private boolean homebaseSetOnce;
    private int waitSecs;
    private boolean enableAutoGroups;

    /**
     *
     */
    public SettingsManager()
    {
        plugin = SimpleClans.getInstance();
        config = plugin.getConfig();
        main = new File(plugin.getDataFolder() + File.separator + "config.yml");
        load();
    }

    /**
     * Load the configuration
     */
    @SuppressWarnings("unchecked")
    public void load()
    {
        boolean exists = (main).exists();

        if (exists)
        {
            try
            {
                config.options().copyDefaults(true);
                config.load(main);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            config.options().copyDefaults(true);
        }

        teleportOnSpawn = config.getBoolean("settings.teleport-home-on-spawn");
        dropOnHome = config.getBoolean("settings.drop-items-on-clan-home");
        keepOnHome = config.getBoolean("settings.keep-items-on-clan-home");
        itemsList = config.getList("settings.item-list");
        debugging = config.getBoolean("settings.show-debug-info");
        mChatIntegration = config.getBoolean("settings.mchat-integration");
        pvpOnlywhileInWar = config.getBoolean("settings.pvp-only-while-at-war");
        enableAutoGroups = config.getBoolean("settings.enable-auto-groups");
        useColorCodeFromPrefix = config.getBoolean("settings.use-colorcode-from-prefix-for-name");
        bannedPlayers = config.getList("settings.banned-players");
        compatMode = config.getBoolean("settings.chat-compatibility-mode");
        disallowedColors = config.getList("settings.disallowed-tag-colors");
        blacklistedWorlds = config.getList("settings.blacklisted-worlds");
        disallowedWords = config.getList("settings.disallowed-tags");
        unRivableClans = config.getList("settings.unrivable-clans");
        showUnverifiedOnList = config.getBoolean("settings.show-unverified-on-list");
        requireVerification = config.getBoolean("settings.new-clan-verification-required");
        serverName = config.getString("settings.server-name");
        chatTags = config.getBoolean("settings.display-chat-tags");
        rivalLimitPercent = config.getInt("settings.rival-limit-percent");
        ePurchaseCreation = config.getBoolean("economy.purchase-clan-create");
        ePurchaseVerification = config.getBoolean("economy.purchase-clan-verify");
        eCreationPrice = config.getInt("economy.creation-price");
        eVerificationPrice = config.getInt("economy.verification-price");
        alertUrl = config.getString("spout.alert-url");
        inGameTags = config.getBoolean("spout.in-game-tags");
        inGameTagsColored = config.getBoolean("spout.in-game-tags-colored");
        clanCapes = config.getBoolean("spout.enable-clan-capes");
        defaultCapeUrl = config.getString("spout.default-cape-url");
        purgeClan = config.getInt("purge.inactive-clan-days");
        purgeUnverified = config.getInt("purge.unverified-clan-days");
        purgePlayers = config.getInt("purge.inactive-player-data-days");
        requestFreqencySecs = config.getInt("request.ask-frequency-secs");
        requestMessageColor = config.getString("request.message-color");
        pageSize = config.getInt("page.size");
        pageSep = config.getString("page.separator");
        pageSubTitleColor = config.getString("page.subtitle-color");
        pageHeadingsColor = config.getString("page.headings-color");
        pageLeaderColor = config.getString("page.leader-color");
        pageTrustedColor = config.getString("page.trusted-color");
        pageUnTrustedColor = config.getString("page.untrusted-color");
        pageClanNameColor = config.getString("page.clan-name-color");
        bbShowOnLogin = config.getBoolean("bb.show-on-login");
        bbSize = config.getInt("bb.size");
        bbColor = config.getString("bb.color");
        bbAccentColor = config.getString("bb.accent-color");
        commandClan = config.getString("commands.clan");
        commandAlly = config.getString("commands.ally");
        commandGlobal = config.getString("commands.global");
        commandMore = config.getString("commands.more");
        commandDeny = config.getString("commands.deny");
        commandAccept = config.getString("commands.accept");
        homebaseSetOnce = config.getBoolean("clan.homebase-can-be-set-only-once");
        waitSecs = config.getInt("clan.homebase-teleport-wait-secs");
        confirmationForPromote = config.getBoolean("clan.confirmation-for-demote");
        confirmationForDemote = config.getBoolean("clan.confirmation-for-promote");
        clanTrustByDefault = config.getBoolean("clan.trust-members-by-default");
        clanMinSizeToAlly = config.getInt("clan.min-size-to-set-ally");
        clanMinSizeToRival = config.getInt("clan.min-size-to-set-rival");
        clanMinLength = config.getInt("clan.min-length");
        clanMaxLength = config.getInt("clan.max-length");
        clanFFOnByDefault = config.getBoolean("clan.ff-on-by-default");
        tagMinLength = config.getInt("tag.min-length");
        tagMaxLength = config.getInt("tag.max-length");
        tagDefaultColor = config.getString("tag.default-color");
        tagSeparator = config.getString("tag.separator.char");
        tagSeparatorColor = config.getString("tag.separator.color");
        tagBracketColor = config.getString("tag.bracket.color");
        tagBracketLeft = config.getString("tag.bracket.left");
        tagBracketRight = config.getString("tag.bracket.right");
        allyChatEnable = config.getBoolean("allychat.enable");
        allyChatMessageColor = config.getString("allychat.message-color");
        allyChatTagColor = config.getString("allychat.tag-color");
        allyChatNameColor = config.getString("allychat.name-color");
        allyChatBracketColor = config.getString("allychat.tag-bracket.color");
        allyChatTagBracketLeft = config.getString("allychat.tag-bracket.left");
        allyChatTagBracketRight = config.getString("allychat.tag-bracket.right");
        allyChatPlayerBracketLeft = config.getString("allychat.player-bracket.left");
        allyChatPlayerBracketRight = config.getString("allychat.player-bracket.right");
        clanChatEnable = config.getBoolean("clanchat.enable");
        clanChatAnnouncementColor = config.getString("clanchat.announcement-color");
        clanChatMessageColor = config.getString("clanchat.message-color");
        clanChatNameColor = config.getString("clanchat.name-color");
        clanChatBracketColor = config.getString("clanchat.tag-bracket.color");
        clanChatTagBracketLeft = config.getString("clanchat.tag-bracket.left");
        clanChatTagBracketRight = config.getString("clanchat.tag-bracket.right");
        clanChatPlayerBracketLeft = config.getString("clanchat.player-bracket.left");
        clanChatPlayerBracketRight = config.getString("clanchat.player-bracket.right");
        kwRival = config.getDouble("kill-weights.rival");
        kwNeutral = config.getDouble("kill-weights.neutral");
        kwCivilian = config.getDouble("kill-weights.civilian");
        useMysql = config.getBoolean("mysql.enable");
        host = config.getString("mysql.host");
        database = config.getString("mysql.database");
        username = config.getString("mysql.username");
        password = config.getString("mysql.password");
        safeCivilians = config.getBoolean("safe-civilians");

        save();
    }

    private void save()
    {
        try
        {
            config.save(main);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Check whether an item is in the list
     *
     * @param typeId the type
     * @return whether the world is blacklisted
     */
    public boolean isItemInList(int typeId)
    {
        return itemsList.contains(typeId);
    }


    /**
     * Check whether a worlds is blacklisted
     *
     * @param world the world
     * @return whether the world is blacklisted
     */
    public boolean isBlacklistedWorld(String world)
    {
        for (Object w : blacklistedWorlds)
        {
            if (((String) w).equalsIgnoreCase(world))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Check whether a word is disallowed
     *
     * @param word the world
     * @return whether its a disallowed word
     */
    public boolean isDisallowedWord(String word)
    {
        for (Object w : disallowedWords)
        {
            if (((String) w).equalsIgnoreCase(word))
            {
                return true;
            }
        }

        return word.equalsIgnoreCase("clan") || word.equalsIgnoreCase(commandMore) || word.equalsIgnoreCase(commandDeny) || word.equalsIgnoreCase(commandAccept);

    }

    /**
     * Check whether a string has a disallowed color
     *
     * @param str the string
     * @return whether the string contains the color code
     */
    public boolean hasDisallowedColor(String str)
    {
        for (Object c : getDisallowedColors())
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

        for (Object c : getDisallowedColors())
        {
            out += c + ", ";
        }

        return Helper.stripTrailing(out, ", ");
    }

    /**
     * Check whether a clan is un-rivable
     *
     * @param tag the tag
     * @return whether the clan is unrivable
     */
    public boolean isUnrivable(String tag)
    {
        for (Object t : getunRivableClans())
        {
            if (((String) t).equalsIgnoreCase(tag))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Check whether a player is banned
     *
     * @param playerName the player's name
     * @return whether player is banned
     */
    public boolean isBanned(String playerName)
    {
        for (Object pl : getBannedPlayers())
        {
            if (((String) pl).equalsIgnoreCase(playerName))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Add a player to the banned list
     *
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
     *
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
    public List<Object> getBannedPlayers()
    {
        return Collections.unmodifiableList(bannedPlayers);
    }

    /**
     * @return the disallowedColors
     */
    public List<Object> getDisallowedColors()
    {
        return Collections.unmodifiableList(disallowedColors);
    }

    /**
     * @return the unRivableClans
     */
    public List<Object> getunRivableClans()
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

        if (tagSeparator == null)
        {
            return "";
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
    public int getCreationPrice()
    {
        return eCreationPrice;
    }

    /**
     * @return the eVerificationPrice
     */
    public int getVerificationPrice()
    {
        return eVerificationPrice;
    }

    public boolean isBbShowOnLogin()
    {
        return bbShowOnLogin;
    }

    public boolean getSafeCivilians()
    {
        return safeCivilians;
    }

    public boolean isConfirmationForPromote()
    {
        return confirmationForPromote;
    }

    public boolean isConfirmationForDemote()
    {
        return confirmationForDemote;
    }

    public boolean isUseColorCodeFromPrefix()
    {
        return useColorCodeFromPrefix;
    }

    public String getCommandAlly()
    {
        return commandAlly;
    }

    public boolean isAllyChatEnable()
    {
        return allyChatEnable;
    }

    public String getAllyChatMessageColor()
    {
        return Helper.toColor(allyChatMessageColor);
    }

    public String getAllyChatNameColor()
    {
        return Helper.toColor(allyChatNameColor);
    }

    public String getAllyChatTagBracketLeft()
    {
        return allyChatTagBracketLeft;
    }

    public String getAllyChatTagBracketRight()
    {
        return allyChatTagBracketRight;
    }

    public String getAllyChatBracketColor()
    {
        return Helper.toColor(allyChatBracketColor);
    }

    public String getAllyChatPlayerBracketLeft()
    {
        return allyChatPlayerBracketLeft;
    }

    public String getAllyChatPlayerBracketRight()
    {
        return allyChatPlayerBracketRight;
    }

    public String getCommandGlobal()
    {
        return commandGlobal;
    }

    public String getAllyChatTagColor()
    {
        return Helper.toColor(allyChatTagColor);
    }

    public boolean isClanFFOnByDefault()
    {
        return clanFFOnByDefault;
    }

    public boolean isCompatMode()
    {
        return compatMode;
    }

    public void setCompatMode(boolean compatMode)
    {
        this.compatMode = compatMode;
    }

    public boolean isHomebaseSetOnce()
    {
        return homebaseSetOnce;
    }

    public int getWaitSecs()
    {
        return waitSecs;
    }

    public void setWaitSecs(int waitSecs)
    {
        this.waitSecs = waitSecs;
    }

    public boolean isEnableAutoGroups()
    {
        return enableAutoGroups;
    }

    public boolean isPvpOnlywhileInWar()
    {
        return pvpOnlywhileInWar;
    }

    public boolean ismChatIntegration()
    {
        return mChatIntegration;
    }

    public boolean isDebugging()
    {
        return debugging;
    }

    public boolean isKeepOnHome()
    {
        return keepOnHome;
    }

    public boolean isDropOnHome()
    {
        return dropOnHome;
    }

    public List<Object> getItemsList()
    {
        return Collections.unmodifiableList(itemsList);
    }

    public boolean isTeleportOnSpawn()
    {
        return teleportOnSpawn;
    }
}
