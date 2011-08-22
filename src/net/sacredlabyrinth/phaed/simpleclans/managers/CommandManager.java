package net.sacredlabyrinth.phaed.simpleclans.managers;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 *
 * @author phaed
 */
public final class CommandManager
{
    private SimpleClans plugin;
    private HashMap<String, ChatBlock> chatBlocks = new HashMap<String, ChatBlock>();
    private NumberFormat formatter = new DecimalFormat("#.#");

    /**
     *
     * @param plugin
     */
    public CommandManager()
    {
        plugin = SimpleClans.getInstance();
    }

    /**
     * Processes a clan chat command
     * @param player
     * @param clan
     * @param msg
     */
    public void processClanChat(Player player, Clan clan, String msg)
    {
        if (!clan.isMember(player))
        {
            return;
        }

        String message = Helper.toColor(plugin.getSettingsManager().getClanChatBracketColor()) + plugin.getSettingsManager().getClanChatTagBracketLeft() + Helper.toColor(plugin.getSettingsManager().getTagDefaultColor()) + Helper.parseColors(clan.getColorTag()) + Helper.toColor(plugin.getSettingsManager().getClanChatBracketColor()) + plugin.getSettingsManager().getClanChatTagBracketRight() + " " + Helper.toColor(plugin.getSettingsManager().getClanChatNameColor()) + plugin.getSettingsManager().getClanChatPlayerBracketLeft() + player.getName() + plugin.getSettingsManager().getClanChatPlayerBracketRight() + " " + Helper.toColor(plugin.getSettingsManager().getClanChatMessageColor()) + msg;
        SimpleClans.log(Level.INFO, plugin.getSettingsManager().getClanChatTagBracketLeft() + clan.getTag() + plugin.getSettingsManager().getClanChatTagBracketRight() + " " + plugin.getSettingsManager().getClanChatPlayerBracketLeft() + player.getName() + plugin.getSettingsManager().getClanChatPlayerBracketRight() + " " + msg);

        List<ClanPlayer> tps = plugin.getClanManager().getAllMembers(clan);

        for (ClanPlayer cp : tps)
        {
            Player member = plugin.getServer().getPlayer(cp.getName());

            ChatBlock.sendMessage(member, message);
        }
    }

    /**
     * Shows the help menu
     * @param player
     */
    public void processMenu(Player player)
    {
        String headColor = Helper.toColor(plugin.getSettingsManager().getPageHeadingsColor());
        String subColor = Helper.toColor(plugin.getSettingsManager().getPageSubTitleColor());

        Clan clan = plugin.getClanManager().getClan(player);
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
        boolean isLeader = cp != null && cp.isLeader();

        ChatBlock chatBlock = new ChatBlock();

        if (plugin.getPermissionsManager().has(player, "simpleclans.mod.verify") && plugin.getSettingsManager().isRequireVerification())
        {
            chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " verify [tag]" + ChatColor.WHITE + " - Verify an unverified clan");
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.mod.ban"))
        {
            chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " ban/unban [player]" + ChatColor.WHITE + " - Ban/unban a player");
        }
        if (clan == null)
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.leader.create"))
            {
                chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " create [tag] [name]" + ChatColor.WHITE + " - Create a new clan");
            }
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.list"))
        {
            chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " list" + ChatColor.WHITE + " - Lists all simpleclans");
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.profile"))
        {
            chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " profile [tag]" + ChatColor.WHITE + " - View a clan's profile");
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.roster"))
        {
            chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " roster [tag]" + ChatColor.WHITE + " - View a clan's member list");
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.lookup"))
        {
            chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " lookup [player]" + ChatColor.WHITE + " - Lookup a player's info");
        }
        if (clan != null)
        {
            if (plugin.getClanManager().isVerified(clan))
            {
                if (plugin.getPermissionsManager().has(player, "simpleclans.member.profile"))
                {
                    chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " profile" + ChatColor.WHITE + " - View your clan's profile");
                }
                if (plugin.getPermissionsManager().has(player, "simpleclans.member.roster"))
                {
                    chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " roster" + ChatColor.WHITE + " - View your clan's member list");
                }
            }
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.lookup"))
            {
                chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " lookup" + ChatColor.WHITE + " - Lookup your info");
            }
            if (plugin.getClanManager().isVerified(clan))
            {
                if (plugin.getPermissionsManager().has(player, "simpleclans.member.vitals"))
                {
                    chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " vitals" + ChatColor.WHITE + " - View your clan member's vitals");
                }
                if (plugin.getPermissionsManager().has(player, "simpleclans.member.coords"))
                {
                    chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " coords" + ChatColor.WHITE + " - View your clan member's coordinates");
                }
                if (plugin.getPermissionsManager().has(player, "simpleclans.member.stats"))
                {
                    chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " stats" + ChatColor.WHITE + " - View your clan member's stats");
                }
                if (isLeader)
                {
                    if (plugin.getPermissionsManager().has(player, "simpleclans.leader.ally"))
                    {
                        chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " ally add/remove [tag]" + ChatColor.WHITE + " - Add/remove an ally clan");
                    }
                    if (plugin.getPermissionsManager().has(player, "simpleclans.leader.rival"))
                    {
                        chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " rival add/remove [tag]" + ChatColor.WHITE + " - Add/remove a rival clan");
                    }
                }
                if (plugin.getPermissionsManager().has(player, "simpleclans.member.bb"))
                {
                    chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " bb" + ChatColor.WHITE + " - Display bulletin board");
                }
                if (plugin.getPermissionsManager().has(player, "simpleclans.member.historybb"))
                {
                    chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " bb history" + ChatColor.WHITE + " - Display bulletin board history");
                }
                if (isLeader)
                {
                    if (plugin.getPermissionsManager().has(player, "simpleclans.leader.bb"))
                    {
                        chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " bb [msg]" + ChatColor.WHITE + " - Add a message to the bulletin board");
                    }
                    if (plugin.getPermissionsManager().has(player, "simpleclans.leader.modtag"))
                    {
                        chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " modtag [tag]" + ChatColor.WHITE + " - Modify the clan's tag");
                    }
                    if (plugin.getPermissionsManager().has(player, "simpleclans.leader.cape") && plugin.getSettingsManager().isClanCapes())
                    {
                        chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " cape [url]" + ChatColor.WHITE + " - Change your clan's cape");
                    }
                }
            }
            if (isLeader)
            {
                if (plugin.getPermissionsManager().has(player, "simpleclans.leader.invite"))
                {
                    chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " invite [player]" + ChatColor.WHITE + " - Invite a player");
                }
                if (plugin.getPermissionsManager().has(player, "simpleclans.leader.kick"))
                {
                    chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " kick [player]" + ChatColor.WHITE + " - Kick a player from the clan");
                }
                if (plugin.getPermissionsManager().has(player, "simpleclans.leader.promote"))
                {
                    chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " promote <member>" + ChatColor.WHITE + " - Promote a member to leader");
                }
                if (plugin.getPermissionsManager().has(player, "simpleclans.leader.demote"))
                {
                    chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " demote [leader]" + ChatColor.WHITE + " - Demote a leader to member");
                }
                if (plugin.getPermissionsManager().has(player, "simpleclans.leader.ff"))
                {
                    chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " clanff allow/block" + ChatColor.WHITE + " - Toggle clan's friendly fire");
                }
            }
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.ff"))
            {
                chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " ff allow/auto" + ChatColor.WHITE + " - Toggle personal friendly fire");
            }
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.resign"))
            {
                chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " resign" + ChatColor.WHITE + " - Resign from the clan");
            }
            if (plugin.getPermissionsManager().has(player, "simpleclans.mod.delete"))
            {
                chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " delete [tag]" + ChatColor.WHITE + " - Delete a clan");
            }
            if (isLeader)
            {
                if (plugin.getPermissionsManager().has(player, "simpleclans.leader.delete"))
                {
                    chatBlock.addRow(ChatColor.AQUA + "/" + plugin.getSettingsManager().getCommandClan() + " delete " + ChatColor.WHITE + " - Delete your clan");
                }
            }
        }

        if (chatBlock.isEmpty())
        {
            ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
            return;
        }

        ChatBlock.sendBlank(player);
        ChatBlock.saySingle(player, Helper.parseColors(plugin.getSettingsManager().getServerName()) + subColor + " clan commands " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
        ChatBlock.sendBlank(player);

        boolean more = chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

        if (more)
        {
            chatBlocks.put(player.getName(), chatBlock);
            ChatBlock.sendBlank(player);
            ChatBlock.sendMessage(player, headColor + "Type /" + plugin.getSettingsManager().getCommandMore() + " to view next page.");
        }

        ChatBlock.sendBlank(player);
    }

    /**
     * Processes the clan command
     * @param player
     * @param command
     * @param arg
     */
    public void processClan(Player player, String command, String[] arg)
    {
        String headColor = Helper.toColor(plugin.getSettingsManager().getPageHeadingsColor());
        String subColor = Helper.toColor(plugin.getSettingsManager().getPageSubTitleColor());

        if (plugin.getSettingsManager().isBanned(player.getName()))
        {
            ChatBlock.sendMessage(player, ChatColor.RED + "You are banned from using " + plugin.getSettingsManager().getCommandClan() + " commands");
            return;
        }

        if (command.equalsIgnoreCase("verify"))
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.mod.verify"))
            {
                if (arg.length == 1)
                {
                    Clan clan = plugin.getClanManager().getClan(arg[0]);

                    if (clan != null)
                    {
                        if (!clan.isVerified())
                        {
                            plugin.getClanManager().verifyClan(clan);
                            plugin.getClanManager().addBb(player.getName(), clan, ChatColor.AQUA + "Clan " + clan.getName() + " has been verified!");
                            ChatBlock.sendMessage(player, ChatColor.AQUA + "The clan has been verified");
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + "The clan is already verified");
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "The clan does not exist");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " verify [tag]");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
            }
        }
        else if (command.equalsIgnoreCase("ban"))
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.mod.ban"))
            {
                if (arg.length == 1)
                {
                    String banned = arg[0];

                    if (!plugin.getSettingsManager().isBanned(banned))
                    {
                        Player pl = Helper.matchOnePlayer(banned);

                        if (pl != null)
                        {
                            ChatBlock.sendMessage(pl, ChatColor.AQUA + "You have been banned from " + plugin.getSettingsManager().getCommandClan() + " commands");
                        }

                        plugin.getClanManager().ban(banned);
                        ChatBlock.sendMessage(player, ChatColor.AQUA + "Player added to banned list");
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "This player is already banned");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " ban/unban [player]");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
            }
        }
        else if (command.equalsIgnoreCase("unban"))
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.mod.ban"))
            {
                if (arg.length == 1)
                {
                    String banned = arg[0];

                    if (plugin.getSettingsManager().isBanned(banned))
                    {
                        Player pl = Helper.matchOnePlayer(banned);

                        if (pl != null)
                        {
                            ChatBlock.sendMessage(pl, ChatColor.AQUA + "You have been unbanned from " + plugin.getSettingsManager().getCommandClan() + " commands");
                        }

                        plugin.getSettingsManager().removeBanned(banned);
                        ChatBlock.sendMessage(player, ChatColor.AQUA + "Player removed from the banned list");
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "This player is not banned");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " ban/unban [player]");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
            }
        }
        else if (command.equalsIgnoreCase("create"))
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.leader.create"))
            {
                if (arg.length >= 2)
                {
                    String tag = arg[0];
                    String cleanTag = Helper.cleanTag(arg[0]);

                    String name = Helper.toMessage(Helper.removeFirst(arg));

                    if (cleanTag.length() <= plugin.getSettingsManager().getTagMaxLength())
                    {
                        if (cleanTag.length() > plugin.getSettingsManager().getTagMinLength())
                        {
                            if (!plugin.getSettingsManager().hasDisallowedColor(tag))
                            {
                                if (Helper.stripColors(name).length() <= plugin.getSettingsManager().getClanMaxLength())
                                {
                                    if (Helper.stripColors(name).length() > plugin.getSettingsManager().getClanMinLength())
                                    {
                                        if (cleanTag.matches("[0-9a-zA-Z]*"))
                                        {
                                            if (name.indexOf("&") < 0)
                                            {
                                                if (!plugin.getSettingsManager().isDisallowedWord(cleanTag.toLowerCase()))
                                                {
                                                    Clan currentClan = plugin.getClanManager().getClan(player);

                                                    if (currentClan == null)
                                                    {
                                                        if (!plugin.getClanManager().isClan(cleanTag))
                                                        {
                                                            plugin.getClanManager().createClan(player, tag, name);

                                                            Clan clan = plugin.getClanManager().getClan(tag);
                                                            plugin.getClanManager().addBb(player.getName(), clan, ChatColor.AQUA + "Clan " + name + " created");
                                                            plugin.getStorageManager().updateClan(clan);

                                                            if (plugin.getSettingsManager().isRequireVerification())
                                                            {
                                                                boolean verified = !plugin.getSettingsManager().isRequireVerification() || plugin.getPermissionsManager().has(player, "simpleclans.mod.verify");

                                                                if (!verified)
                                                                {
                                                                    ChatBlock.sendMessage(player, ChatColor.AQUA + "Get your clan verified to access advanced features.");
                                                                }
                                                            }
                                                        }
                                                        else
                                                        {
                                                            ChatBlock.sendMessage(player, ChatColor.RED + "A clan with this tag already exists");
                                                        }
                                                    }
                                                    else
                                                    {
                                                        ChatBlock.sendMessage(player, ChatColor.RED + "You must first resign from " + currentClan.getName());
                                                    }
                                                }
                                                else
                                                {
                                                    ChatBlock.sendMessage(player, ChatColor.RED + "That tag name is disallowed");
                                                }
                                            }
                                            else
                                            {
                                                ChatBlock.sendMessage(player, ChatColor.RED + "Your clan's name cannot contain color codes");
                                            }
                                        }
                                        else
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.RED + "Your clan's tag can only contain letters, numbers, and color codes");
                                        }
                                    }
                                    else
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.RED + "Your clan's name must be longer than " + plugin.getSettingsManager().getClanMinLength() + " characters");
                                    }
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + "Your clan's name cannot be longer than " + plugin.getSettingsManager().getClanMaxLength() + " characters");
                                }
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + "Your tag cannot contain the following colors: " + plugin.getSettingsManager().getDisallowedColorString());
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + "Your clan's tag must be longer than " + plugin.getSettingsManager().getTagMinLength() + " characters");
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "Your clan's tag cannot be longer than " + plugin.getSettingsManager().getTagMaxLength() + " characters");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " create [tag] [name]");
                    ChatBlock.sendMessage(player, ChatColor.RED + "Example: /" + plugin.getSettingsManager().getCommandClan() + " create &4Kol Knights of the Labyrinth");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
            }
        }
        else if (command.equalsIgnoreCase("list"))
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.list"))
            {
                if (arg.length == 0)
                {
                    List<Clan> simpleclans = plugin.getClanManager().getSimpleClans();
                    Collections.sort(simpleclans);

                    if (!simpleclans.isEmpty())
                    {
                        ChatBlock chatBlock = new ChatBlock();

                        ChatBlock.sendBlank(player);
                        ChatBlock.saySingle(player, Helper.parseColors(plugin.getSettingsManager().getServerName()) + subColor + " simpleclans " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                        ChatBlock.sendBlank(player);

                        chatBlock.setAlignment("l", "c", "c", "c");

                        chatBlock.addRow("  " + headColor + "Name", "Size", "AVG-KDR", "AVG-WK");

                        for (Clan clan : simpleclans)
                        {
                            String tag = Helper.toColor(plugin.getSettingsManager().getClanChatBracketColor()) + plugin.getSettingsManager().getClanChatTagBracketLeft() + Helper.toColor(plugin.getSettingsManager().getTagDefaultColor()) + Helper.parseColors(clan.getColorTag()) + Helper.toColor(plugin.getSettingsManager().getClanChatBracketColor()) + plugin.getSettingsManager().getClanChatTagBracketRight();
                            String name = plugin.getClanManager().isVerified(clan) ? Helper.toColor(plugin.getSettingsManager().getPageClanNameColor()) + clan.getName() : ChatColor.GRAY + "unverified";
                            String fullname = tag + " " + name;
                            String size = ChatColor.WHITE + "" + clan.getSize();
                            String kdr = plugin.getClanManager().isVerified(clan) ? ChatColor.WHITE + "" + formatter.format(plugin.getClanManager().getAverageKDR(clan)) : "";
                            String wk = plugin.getClanManager().isVerified(clan) ? ChatColor.WHITE + "" + formatter.format(plugin.getClanManager().getAverageWK(clan)) : "";

                            chatBlock.addRow("  " + fullname, size, kdr, wk);
                        }

                        boolean more = chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

                        if (more)
                        {
                            chatBlocks.put(player.getName(), chatBlock);
                            ChatBlock.sendBlank(player);
                            ChatBlock.sendMessage(player, headColor + "Type /" + plugin.getSettingsManager().getCommandMore() + " to view next page.");
                        }

                        ChatBlock.sendBlank(player);
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "No simpleclans have been created");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " simpleclans");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
            }
        }
        else if (command.equalsIgnoreCase("profile"))
        {
            Clan clan = null;

            if (arg.length == 0)
            {
                if (plugin.getPermissionsManager().has(player, "simpleclans.member.profile"))
                {
                    clan = plugin.getClanManager().getClan(player);

                    if (clan == null)
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "You are not a member of any clan");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
                }
            }
            else if (arg.length == 1)
            {
                if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.profile"))
                {
                    clan = plugin.getClanManager().getClan(arg[0]);

                    if (clan == null)
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "No clan matched");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " profile [tag]");
            }

            if (clan != null)
            {
                if (plugin.getClanManager().isVerified(clan))
                {
                    ChatBlock.sendBlank(player);
                    ChatBlock.saySingle(player, Helper.toColor(plugin.getSettingsManager().getPageClanNameColor()) + clan.getName() + subColor + " profile " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                    ChatBlock.sendBlank(player);

                    String name = Helper.toColor(plugin.getSettingsManager().getClanChatBracketColor()) + plugin.getSettingsManager().getClanChatTagBracketLeft() + Helper.toColor(plugin.getSettingsManager().getTagDefaultColor()) + Helper.parseColors(clan.getColorTag()) + Helper.toColor(plugin.getSettingsManager().getClanChatBracketColor()) + plugin.getSettingsManager().getClanChatTagBracketRight() + " " + Helper.toColor(plugin.getSettingsManager().getPageClanNameColor()) + clan.getName();
                    String leaders = plugin.getClanManager().getLeadersString(clan, Helper.toColor(plugin.getSettingsManager().getPageLeaderColor()), subColor + ", ");
                    String onlineCount = ChatColor.WHITE + "" + Helper.stripOffLinePlayers(plugin.getClanManager().getAllMembers(clan)).size();
                    String membersOnline = onlineCount + subColor + "/" + ChatColor.WHITE + clan.getSize();
                    String inactive = ChatColor.WHITE + "" + clan.getInactiveDays() + subColor + "/" + ChatColor.WHITE + (clan.isVerified() ? plugin.getSettingsManager().getPurgeClan() : plugin.getSettingsManager().getPurgeUnverified()) + " days";
                    String founded = ChatColor.WHITE + "" + clan.getFoundedString();
                    String allies = ChatColor.WHITE + "" + Helper.parseColors(plugin.getClanManager().getAllyString(clan, subColor + ", "));
                    String rivals = ChatColor.WHITE + "" + Helper.parseColors(plugin.getClanManager().getRivalString(clan, subColor + ", "));
                    String kdr = ChatColor.WHITE + "" + formatter.format(plugin.getClanManager().getAverageKDR(clan));
                    String wk = ChatColor.WHITE + "" + plugin.getClanManager().getAverageWK(clan);
                    String rival = ChatColor.WHITE + "" + formatter.format(plugin.getClanManager().getTotalRival(clan));
                    String neutral = ChatColor.WHITE + "" + plugin.getClanManager().getTotalNeutral(clan);
                    String civ = ChatColor.WHITE + "" + plugin.getClanManager().getTotalCivilian(clan);

                    ChatBlock.sendMessage(player, "  " + subColor + "Name: " + name);
                    ChatBlock.sendMessage(player, "  " + subColor + "Leaders: " + leaders);
                    ChatBlock.sendMessage(player, "  " + subColor + "Members Online: " + membersOnline);
                    ChatBlock.sendMessage(player, "  " + subColor + "AVG-KDR: " + kdr);
                    ChatBlock.sendMessage(player, "  " + subColor + "AVG-WK: " + wk);
                    ChatBlock.sendMessage(player, "  " + subColor + "Kill Totals: " + headColor + "[Rival:" + rival + " " + headColor + "Neutral:" + neutral + " " + headColor + "Civilian:" + civ + headColor + "]");
                    ChatBlock.sendMessage(player, "  " + subColor + "Allies: " + allies);
                    ChatBlock.sendMessage(player, "  " + subColor + "Rivals: " + rivals);
                    ChatBlock.sendMessage(player, "  " + subColor + "Founded: " + founded);
                    ChatBlock.sendMessage(player, "  " + subColor + "Inactive: " + inactive);
                    ChatBlock.sendBlank(player);
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "Clan is not verified");
                }
            }
        }
        else if (command.equalsIgnoreCase("roster"))
        {
            Clan clan = null;

            if (arg.length == 0)
            {
                if (plugin.getPermissionsManager().has(player, "simpleclans.member.roster"))
                {
                    clan = plugin.getClanManager().getClan(player);

                    if (clan == null)
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "You are not a member of any clan");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
                }
            }
            else if (arg.length == 1)
            {
                if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.roster"))
                {
                    clan = plugin.getClanManager().getClan(arg[0]);

                    if (clan == null)
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "No clan matched");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " roster [tag]");
            }

            if (clan != null)
            {
                if (plugin.getClanManager().isVerified(clan))
                {
                    ChatBlock chatBlock = new ChatBlock();

                    ChatBlock.sendBlank(player);
                    ChatBlock.saySingle(player, Helper.toColor(plugin.getSettingsManager().getPageClanNameColor()) + clan.getName() + subColor + " roster " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                    ChatBlock.sendBlank(player);
                    ChatBlock.sendMessage(player, headColor + "(" + ChatColor.YELLOW + "KDR" + headColor + ") = " + subColor + "Kill/Death ratio" + headColor + ", " + ChatColor.GREEN + "*" + headColor + " = " + subColor + "online");
                    ChatBlock.sendBlank(player);

                    chatBlock.setFlexibility(false, true, false, true);
                    chatBlock.addRow("  " + headColor + "Player", "Seen", "Player", "Seen");

                    List<String> row = new ArrayList<String>();

                    List<ClanPlayer> members = plugin.getClanManager().getAllMembers(clan);

                    for (ClanPlayer cp : members)
                    {
                        Player p = plugin.getServer().getPlayer(cp.getCleanName());

                        boolean isOnline = false;

                        if (p != null)
                        {
                            isOnline = true;
                        }

                        String name = (cp.isLeader() ? Helper.toColor(plugin.getSettingsManager().getPageLeaderColor()) : Helper.toColor(plugin.getSettingsManager().getPageMemberColor())) + cp.getCleanName();
                        String kdr = ChatColor.DARK_GRAY + "(" + ChatColor.YELLOW + formatter.format(cp.getKillDeathRatio()) + ChatColor.DARK_GRAY + ")";
                        String online = isOnline ? ChatColor.GREEN + "*" : "";
                        String lastSeen = ChatColor.WHITE + cp.getLastSeenDaysString();

                        row.add(name + kdr + online);
                        row.add(lastSeen);

                        if (row.size() == 4)
                        {
                            chatBlock.addRow("  " + row.get(0), row.get(1), row.get(2), row.get(3));
                            row.clear();
                        }
                    }

                    if (!row.isEmpty())
                    {
                        chatBlock.addRow("  " + row.get(0), row.size() > 1 ? row.get(1) : "", row.size() > 2 ? row.get(2) : "", row.size() > 4 ? row.get(3) : "");
                    }

                    boolean more = chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

                    if (more)
                    {
                        chatBlocks.put(player.getName(), chatBlock);
                        ChatBlock.sendBlank(player);
                        ChatBlock.sendMessage(player, headColor + "Type /" + plugin.getSettingsManager().getCommandMore() + " to view next page.");
                    }

                    ChatBlock.sendBlank(player);
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "Clan is not verified");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " profile [tag]");
            }
        }
        else if (command.equalsIgnoreCase("lookup"))
        {
            String playerName = null;

            if (arg.length == 0)
            {
                if (plugin.getPermissionsManager().has(player, "simpleclans.member.lookup"))
                {
                    playerName = player.getName();
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
                }
            }
            else if (arg.length == 1)
            {
                if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.lookup"))
                {
                    playerName = arg[0];
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " lookup [tag]");
            }

            if (playerName != null)
            {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(playerName);
                Clan myClan = plugin.getClanManager().getClan(player.getName());

                if (cp != null)
                {
                    Clan clan = plugin.getClanManager().getClan(cp.getTag());

                    ChatBlock.sendBlank(player);
                    ChatBlock.saySingle(player, Helper.toColor(plugin.getSettingsManager().getPageClanNameColor()) + cp.getName() + subColor + " lookup " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                    ChatBlock.sendBlank(player);

                    String clanName = ChatColor.WHITE + "None";

                    if (clan != null)
                    {
                        clanName = Helper.toColor(plugin.getSettingsManager().getClanChatBracketColor()) + plugin.getSettingsManager().getClanChatTagBracketLeft() + Helper.toColor(plugin.getSettingsManager().getTagDefaultColor()) + Helper.parseColors(clan.getColorTag()) + Helper.toColor(plugin.getSettingsManager().getClanChatBracketColor()) + plugin.getSettingsManager().getClanChatTagBracketRight() + " " + Helper.toColor(plugin.getSettingsManager().getPageClanNameColor()) + clan.getName();
                    }

                    String status = clan == null ? ChatColor.WHITE + "Free Agent" : (cp.isLeader() ? Helper.toColor(plugin.getSettingsManager().getPageLeaderColor()) + "Leader" : ChatColor.WHITE + "Member");
                    String joinDate = ChatColor.WHITE + "" + cp.getJoinDateString();
                    String lastSeen = ChatColor.WHITE + "" + cp.getLastSeenString();
                    String inactive = ChatColor.WHITE + "" + cp.getInactiveDays() + subColor + "/" + ChatColor.WHITE + plugin.getSettingsManager().getPurgePlayers() + " days";
                    String rival = ChatColor.WHITE + "" + cp.getRivalKills();
                    String neutral = ChatColor.WHITE + "" + cp.getNeutralKills();
                    String civilian = ChatColor.WHITE + "" + cp.getCivilianKills();
                    String wk = ChatColor.WHITE + "" + cp.getWeightedKills();
                    String deaths = ChatColor.WHITE + "" + cp.getDeaths();
                    String kdr = ChatColor.WHITE + "" + formatter.format(cp.getKillDeathRatio());
                    String pastClans = ChatColor.WHITE + "" + cp.getPastClansString(headColor + ", ");

                    ChatBlock.sendMessage(player, "  " + subColor + "Clan: " + clanName);
                    ChatBlock.sendMessage(player, "  " + subColor + "Status: " + status);
                    ChatBlock.sendMessage(player, "  " + subColor + "KDR: " + kdr);
                    ChatBlock.sendMessage(player, "  " + subColor + "WK: " + wk);
                    ChatBlock.sendMessage(player, "  " + subColor + "Deaths: " + deaths);
                    ChatBlock.sendMessage(player, "  " + subColor + "Kill Totals: " + headColor + "[Rival:" + rival + " " + headColor + "Neutral:" + neutral + " " + headColor + "Civilian:" + civilian + headColor + "]");
                    ChatBlock.sendMessage(player, "  " + subColor + "Join Date: " + joinDate);
                    ChatBlock.sendMessage(player, "  " + subColor + "Last Seen: " + lastSeen);
                    ChatBlock.sendMessage(player, "  " + subColor + "Past SimpleClans: " + pastClans);
                    ChatBlock.sendMessage(player, "  " + subColor + "Inactive: " + inactive);

                    if (arg.length == 1 && myClan != null)
                    {
                        String killType = ChatColor.GRAY + "Neutral";

                        if (clan == null)
                        {
                            killType = ChatColor.DARK_GRAY + "Civilian";
                        }
                        else if (myClan.isRival(clan.getTag()))
                        {
                            killType = ChatColor.WHITE + "Rival";
                        }

                        ChatBlock.sendMessage(player, "  " + subColor + "Kill Type: " + killType);
                    }

                    ChatBlock.sendBlank(player);
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "No player data found");

                    if (arg.length == 1 && myClan != null)
                    {
                        ChatBlock.sendBlank(player);
                        ChatBlock.sendMessage(player, "  " + subColor + "Kill Type: " + ChatColor.DARK_GRAY + "Civilian");
                    }
                }
            }
        }
        else if (command.equalsIgnoreCase("vitals"))
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.vitals"))
            {
                Clan clan = plugin.getClanManager().getClan(player);

                if (clan != null)
                {
                    if (plugin.getClanManager().isVerified(clan))
                    {
                        if (arg.length == 0)
                        {
                            ChatBlock chatBlock = new ChatBlock();

                            ChatBlock.sendBlank(player);
                            ChatBlock.saySingle(player, Helper.toColor(plugin.getSettingsManager().getPageClanNameColor()) + clan.getName() + subColor + " vitals " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                            ChatBlock.sendBlank(player);

                            chatBlock.setFlexibility(true, false, false, false, false);
                            chatBlock.setAlignment("l", "l", "c", "c", "c");

                            chatBlock.addRow("  " + headColor + "Name", "Health", "Armor", "Weapons", "Food");

                            List<ClanPlayer> members = Helper.stripOffLinePlayers(plugin.getClanManager().getLeaders(clan));
                            members.addAll(Helper.stripOffLinePlayers(plugin.getClanManager().getNonLeaders(clan)));

                            for (ClanPlayer cp : members)
                            {
                                Player p = plugin.getServer().getPlayer(cp.getCleanName());

                                if (p != null)
                                {
                                    String name = Helper.toColor(cp.isLeader() ? plugin.getSettingsManager().getPageLeaderColor() : plugin.getSettingsManager().getPageMemberColor()) + cp.getCleanName();
                                    String health = getHealthString(p.getHealth());
                                    String armor = getArmorString(p.getInventory());
                                    String weapons = getWeaponString(p.getInventory());
                                    String food = getFoodString(p.getInventory());

                                    chatBlock.addRow("  " + name, ChatColor.RED + health, armor, weapons, ChatColor.WHITE + food);
                                }
                            }

                            boolean more = chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

                            if (more)
                            {
                                chatBlocks.put(player.getName(), chatBlock);
                                ChatBlock.sendBlank(player);
                                ChatBlock.sendMessage(player, headColor + "Type /" + plugin.getSettingsManager().getCommandMore() + " to view next page.");
                            }

                            ChatBlock.sendBlank(player);
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " vitals");
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "Clan is not verified");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "You are not a member of any clan");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
            }
        }
        else if (command.equalsIgnoreCase("coords"))
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.coords"))
            {
                Clan clan = plugin.getClanManager().getClan(player);

                if (clan != null)
                {
                    if (plugin.getClanManager().isVerified(clan))
                    {
                        if (arg.length == 0)
                        {
                            ChatBlock chatBlock = new ChatBlock();

                            chatBlock.setFlexibility(true, false, false, false);
                            chatBlock.setAlignment("l", "c", "c", "c");

                            chatBlock.addRow("  " + headColor + "Name", "Distance", "Coords", "World");

                            List<ClanPlayer> members = Helper.stripOffLinePlayers(plugin.getClanManager().getAllMembers(clan));

                            Map<Integer, List<String>> rows = new TreeMap<Integer, List<String>>();

                            for (ClanPlayer cp : members)
                            {
                                Player p = plugin.getServer().getPlayer(cp.getCleanName());

                                if (p != null)
                                {
                                    String name = Helper.toColor(cp.isLeader() ? plugin.getSettingsManager().getPageLeaderColor() : plugin.getSettingsManager().getPageMemberColor()) + cp.getCleanName();

                                    Location loc = p.getLocation();
                                    int distance = (int) Math.ceil(loc.toVector().distance(player.getLocation().toVector()));
                                    String coords = loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();
                                    String world = loc.getWorld().getName();

                                    List<String> cols = new ArrayList<String>();
                                    cols.add("  " + name);
                                    cols.add(ChatColor.AQUA + "" + distance);
                                    cols.add(ChatColor.WHITE + "" + coords);
                                    cols.add(world);
                                    rows.put(distance, cols);
                                }
                            }

                            if (!rows.isEmpty())
                            {
                                for (List<String> col : rows.values())
                                {
                                    chatBlock.addRow(col.get(0), col.get(1), col.get(2), col.get(3));
                                }

                                ChatBlock.sendBlank(player);
                                ChatBlock.saySingle(player, Helper.toColor(plugin.getSettingsManager().getPageClanNameColor()) + clan.getName() + subColor + " coords " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                                ChatBlock.sendBlank(player);

                                boolean more = chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

                                if (more)
                                {
                                    chatBlocks.put(player.getName(), chatBlock);
                                    ChatBlock.sendBlank(player);
                                    ChatBlock.sendMessage(player, headColor + "Type /" + plugin.getSettingsManager().getCommandMore() + " to view next page.");
                                }

                                ChatBlock.sendBlank(player);
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + "You are the only member online");
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " coords");
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "Clan is not verified");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "You are not a member of any clan");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
            }
        }
        else if (command.equalsIgnoreCase("stats"))
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.stats"))
            {
                Clan clan = plugin.getClanManager().getClan(player);

                if (clan != null)
                {
                    if (plugin.getClanManager().isVerified(clan))
                    {
                        if (arg.length == 0)
                        {
                            ChatBlock chatBlock = new ChatBlock();

                            ChatBlock.sendBlank(player);
                            ChatBlock.saySingle(player, Helper.toColor(plugin.getSettingsManager().getPageClanNameColor()) + clan.getName() + subColor + " stats " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                            ChatBlock.sendBlank(player);
                            ChatBlock.sendMessage(player, headColor + "KDR = " + subColor + "Kill/Death Ratio" + headColor + ", WK = " + subColor + "Weighted Kills");
                            ChatBlock.sendMessage(player, headColor + "Weights = Rival: " + subColor + plugin.getSettingsManager().getKwRival() + headColor + " Neutral: " + subColor + plugin.getSettingsManager().getKwNeutral() + headColor + " Civilian: " + subColor + plugin.getSettingsManager().getKwCivilian());
                            ChatBlock.sendBlank(player);

                            chatBlock.setFlexibility(true, false, false, false, false, false, false);
                            chatBlock.setAlignment("l", "c", "c", "c", "c", "c", "c");

                            chatBlock.addRow("  " + headColor + "Name", "KDR", "WK", "Rival", "Neutral", "Civ", "Deaths");

                            List<ClanPlayer> members = plugin.getClanManager().getAllMembers(clan);

                            for (ClanPlayer cp : members)
                            {
                                String name = Helper.toColor(cp.isLeader() ? plugin.getSettingsManager().getPageLeaderColor() : plugin.getSettingsManager().getPageMemberColor()) + cp.getCleanName();
                                String rival = NumberFormat.getInstance().format(cp.getRivalKills());
                                String neutral = NumberFormat.getInstance().format(cp.getNeutralKills());
                                String civilian = NumberFormat.getInstance().format(cp.getCivilianKills());
                                String wk = NumberFormat.getInstance().format(cp.getWeightedKills());
                                String deaths = NumberFormat.getInstance().format(cp.getDeaths());
                                String kdr = formatter.format(cp.getKillDeathRatio());

                                chatBlock.addRow("  " + name, ChatColor.YELLOW + kdr, ChatColor.AQUA + wk, ChatColor.WHITE + rival, ChatColor.GRAY + neutral, ChatColor.DARK_GRAY + civilian, ChatColor.DARK_RED + deaths);
                            }

                            boolean more = chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

                            if (more)
                            {
                                chatBlocks.put(player.getName(), chatBlock);
                                ChatBlock.sendBlank(player);
                                ChatBlock.sendMessage(player, headColor + "Type /" + plugin.getSettingsManager().getCommandMore() + " to view next page.");
                            }

                            ChatBlock.sendBlank(player);
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " kills");
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "Clan is not verified");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "You are not a member of any clan");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
            }
        }
        else if (command.equalsIgnoreCase("ally"))
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.leader.ally"))
            {
                Clan clan = plugin.getClanManager().getClan(player);

                if (clan != null)
                {
                    if (plugin.getClanManager().isVerified(clan))
                    {
                        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                        if (plugin.getClanManager().isLeader(clan, player))
                        {
                            if (arg.length == 2)
                            {
                                if (clan.getSize() >= plugin.getSettingsManager().getClanMinSizeToAlly())
                                {
                                    String action = arg[0];
                                    Clan ally = plugin.getClanManager().getClan(arg[1]);

                                    if (ally != null)
                                    {
                                        if (plugin.getClanManager().isVerified(ally))
                                        {
                                            if (action.equals("add"))
                                            {
                                                if (!clan.isAlly(ally.getTag()))
                                                {
                                                    List<ClanPlayer> onlineLeaders = Helper.stripOffLinePlayers(plugin.getClanManager().getLeaders(clan));

                                                    if (!onlineLeaders.isEmpty())
                                                    {
                                                        plugin.getRequestManager().addAllyRequest(plugin, cp, ally, clan);
                                                        ChatBlock.sendMessage(player, ChatColor.AQUA + Helper.capitalize(ally.getName()) + " leaders have been asked for an alliance");
                                                    }
                                                    else
                                                    {
                                                        ChatBlock.sendMessage(player, ChatColor.RED + "At least one leader of the allied must be online to accept the alliance");
                                                    }
                                                }
                                                else
                                                {
                                                    ChatBlock.sendMessage(player, ChatColor.RED + "Your simpleclans are already allies");
                                                }
                                            }
                                            else if (action.equals("remove"))
                                            {
                                                if (clan.isAlly(ally.getTag()))
                                                {
                                                    plugin.getClanManager().removeAlly(clan, ally);
                                                    plugin.getClanManager().addBb(cp.getName(), ally, ChatColor.AQUA + Helper.capitalize(clan.getName()) + " has broken the alliance with " + ally.getName());
                                                    plugin.getClanManager().addBb(cp.getName(), clan, ChatColor.AQUA + Helper.capitalize(cp.getName()) + " has broken the alliance with " + Helper.capitalize(ally.getName()));
                                                }
                                                else
                                                {
                                                    ChatBlock.sendMessage(player, ChatColor.RED + "Your simpleclans are not allies");
                                                }
                                            }
                                            else
                                            {
                                                ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " ally add/remove [tag]");
                                            }
                                        }
                                        else
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.RED + "You cannot ally with an unverified clan");
                                        }
                                    }
                                    else
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.RED + "No clan matched");
                                    }
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + "Your clan must have at least " + plugin.getSettingsManager().getClanMinSizeToAlly() + " players in order to make alliances");
                                }
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " ally add/remove [tag]");
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + "You do not have leader permissions");
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "Clan is not verified");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "You are not a member of any clan");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
            }
        }
        else if (command.equalsIgnoreCase("rival"))
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.leader.rival"))
            {
                Clan clan = plugin.getClanManager().getClan(player);

                if (clan != null)
                {
                    if (plugin.getClanManager().isVerified(clan))
                    {
                        if (!plugin.getSettingsManager().isUnrivable(clan.getTag()))
                        {
                            if (!plugin.getClanManager().reachedRivalLimit(clan))
                            {
                                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                                if (plugin.getClanManager().isLeader(clan, player))
                                {
                                    if (arg.length == 2)
                                    {
                                        if (clan.getSize() >= plugin.getSettingsManager().getClanMinSizeToRival())
                                        {
                                            String action = arg[0];
                                            Clan rival = plugin.getClanManager().getClan(arg[1]);

                                            if (rival != null)
                                            {
                                                if (!plugin.getSettingsManager().isUnrivable(rival.getTag()))
                                                {
                                                    if (plugin.getClanManager().isVerified(rival))
                                                    {
                                                        if (action.equals("add"))
                                                        {
                                                            if (!clan.isRival(rival.getTag()))
                                                            {
                                                                plugin.getClanManager().addRival(clan, rival);
                                                                plugin.getClanManager().addBb(player.getName(), rival, ChatColor.AQUA + Helper.capitalize(clan.getName()) + " has initiated a rivalry with " + rival.getName());
                                                                plugin.getClanManager().addBb(player.getName(), clan, ChatColor.AQUA + Helper.capitalize(player.getName()) + " has initiated a rivalry with " + Helper.capitalize(rival.getName()));
                                                            }
                                                            else
                                                            {
                                                                ChatBlock.sendMessage(player, ChatColor.RED + "Your simpleclans are already rivals");
                                                            }
                                                        }
                                                        else if (action.equals("remove"))
                                                        {
                                                            if (clan.isRival(rival.getTag()))
                                                            {
                                                                plugin.getRequestManager().addRivalryBreakRequest(plugin, cp, rival, clan);
                                                                ChatBlock.sendMessage(player, ChatColor.AQUA + Helper.capitalize(rival.getName()) + " leaders have been asked to end the rivalry");
                                                            }
                                                            else
                                                            {
                                                                ChatBlock.sendMessage(player, ChatColor.RED + "Your simpleclans are not rivals");
                                                            }
                                                        }
                                                        else
                                                        {
                                                            ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " ally add/remove [tag]");
                                                        }
                                                    }
                                                    else
                                                    {
                                                        ChatBlock.sendMessage(player, ChatColor.RED + "You cannot rival an unverified clan");
                                                    }
                                                }
                                                else
                                                {
                                                    ChatBlock.sendMessage(player, ChatColor.RED + "No clan matched");
                                                }
                                            }
                                            else
                                            {
                                                ChatBlock.sendMessage(player, ChatColor.RED + "No clan matched");
                                            }
                                        }
                                        else
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.RED + "Your clan must have at least " + plugin.getSettingsManager().getClanMinSizeToRival() + " players in order to make rivalries");
                                        }
                                    }
                                    else
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " rival add/remove [tag]");
                                    }
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + "You do not have leader permissions");
                                }
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + "Your clan has reached the rival limit of " + plugin.getSettingsManager().getRivalLimitPercent() + "% of all simpleclans.  You canno initiate any more rivalries.");
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + "Your clan cannot create rivals");
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "Clan is not verified");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "You are not a member of any clan");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
            }
        }
        else if (command.equalsIgnoreCase("bb"))
        {
            Clan clan = plugin.getClanManager().getClan(player);

            if (clan != null)
            {
                if (plugin.getClanManager().isVerified(clan))
                {
                    if (arg.length == 0)
                    {
                        if (plugin.getPermissionsManager().has(player, "simpleclans.member.bb"))
                        {
                            plugin.getClanManager().displayBb(player);
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
                        }
                    }
                    else
                    {
                        if (arg[0].equalsIgnoreCase("history"))
                        {
                            if (plugin.getPermissionsManager().has(player, "simpleclans.member.bbhistory"))
                            {
                                ChatBlock chatBlock = new ChatBlock();

                                ChatBlock.sendBlank(player);
                                ChatBlock.saySingle(player, Helper.toColor(plugin.getSettingsManager().getPageClanNameColor()) + clan.getName() + subColor + " full bulletin board history " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                                ChatBlock.sendBlank(player);

                                List<String> bb = clan.getBb();

                                for (String msg : bb)
                                {
                                    chatBlock.addRow("  " + Helper.toColor(plugin.getSettingsManager().getBbAccentColor()) + "* " + Helper.toColor(plugin.getSettingsManager().getBbColor()) + Helper.parseColors(msg));
                                }

                                boolean more = chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

                                if (more)
                                {
                                    chatBlocks.put(player.getName(), chatBlock);
                                    ChatBlock.sendBlank(player);
                                    ChatBlock.sendMessage(player, headColor + "Type /" + plugin.getSettingsManager().getCommandMore() + " to view next page.");
                                }

                                ChatBlock.sendBlank(player);
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
                            }
                        }
                        else
                        {
                            if (plugin.getPermissionsManager().has(player, "simpleclans.leader.bb"))
                            {
                                if (plugin.getClanManager().isLeader(clan, player))
                                {
                                    String msg = Helper.toMessage(arg);
                                    plugin.getClanManager().addBb(player.getName(), clan, "(" + player.getName() + ") " + msg);
                                    plugin.getStorageManager().updateClan(clan);
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + "You do not have leader permissions");
                                }
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
                            }
                        }
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "Clan is not verified");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "You are not a member of any clan");
            }
        }
        else if (command.equalsIgnoreCase("modtag"))
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.leader.modtag"))
            {
                Clan clan = plugin.getClanManager().getClan(player);

                if (clan != null)
                {
                    if (plugin.getClanManager().isVerified(clan))
                    {
                        if (plugin.getClanManager().isLeader(clan, player))
                        {
                            if (arg.length == 1)
                            {
                                String newtag = arg[0];
                                String cleantag = Helper.cleanTag(newtag);

                                if (Helper.stripColors(newtag).length() <= plugin.getSettingsManager().getTagMaxLength())
                                {
                                    if (!plugin.getSettingsManager().hasDisallowedColor(newtag))
                                    {
                                        if (Helper.stripColors(newtag).matches("[0-9a-zA-Z]*"))
                                        {
                                            if (cleantag.equals(clan.getTag()))
                                            {
                                                plugin.getClanManager().addBb(player.getName(), clan, ChatColor.AQUA + "Tag changed to " + Helper.parseColors(newtag));
                                                plugin.getClanManager().changeClanTag(clan, newtag);
                                            }
                                            else
                                            {
                                                ChatBlock.sendMessage(player, ChatColor.RED + "You can only modify the color and case of the tag");
                                            }
                                        }
                                        else
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.RED + "Your clan's tag can only contain letters, numbers, and color codes");
                                        }
                                    }
                                    else
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.RED + "Your tag cannot contain the following colors: " + plugin.getSettingsManager().getDisallowedColorString());
                                    }

                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + "Your clan's tag cannot be longer than " + plugin.getSettingsManager().getTagMaxLength() + " characters");
                                }
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " modtag [tag]");
                                ChatBlock.sendMessage(player, ChatColor.RED + "Example: /" + plugin.getSettingsManager().getCommandClan() + " modtag &4K&fo&4L");
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + "You do not have leader permissions");
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "Clan is not verified");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "You are not a member of any clan");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
            }
        }
        else if (command.equalsIgnoreCase("cape"))
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.leader.cape"))
            {
                Clan clan = plugin.getClanManager().getClan(player);

                if (clan != null)
                {
                    if (plugin.getClanManager().isVerified(clan))
                    {
                        if (plugin.getClanManager().isLeader(clan, player))
                        {
                            if (arg.length == 1)
                            {
                                String url = arg[0];

                                if (url.indexOf(".png") >= 0)
                                {
                                    if (Helper.testURL(url))
                                    {
                                        plugin.getClanManager().addBb(player.getName(), clan, ChatColor.AQUA + Helper.capitalize(player.getName()) + " has changed the clan's cape");
                                        plugin.getClanManager().setClanCape(url, clan);
                                    }
                                    else
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.RED + "The URL is retuning an error.  Please verify that it is working.");
                                    }
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + "The cape url must point to a png image");
                                }
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " cape [url]");
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + "You do not have leader permissions");
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "Clan is not verified");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "You are not a member of any clan");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
            }
        }
        else if (command.equalsIgnoreCase("invite"))
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.leader.invite"))
            {
                Clan clan = plugin.getClanManager().getClan(player);

                if (clan != null)
                {
                    ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                    if (plugin.getClanManager().isLeader(clan, player))
                    {
                        if (arg.length == 1)
                        {
                            Player invited = Helper.matchOnePlayer(arg[0]);

                            if (invited != null)
                            {
                                if (plugin.getPermissionsManager().has(player, "simpleclans.member.can-join"))
                                {
                                    if (!invited.getName().equals(player.getName()))
                                    {
                                        if (!plugin.getSettingsManager().isBanned(player.getName()))
                                        {
                                            Clan iclan = plugin.getClanManager().getClan(invited);

                                            if (iclan == null)
                                            {
                                                plugin.getRequestManager().addInviteRequest(plugin, cp, invited.getName(), clan);
                                                ChatBlock.sendMessage(player, ChatColor.AQUA + Helper.capitalize(invited.getName()) + " has been asked to join " + clan.getName());
                                            }
                                            else
                                            {
                                                ChatBlock.sendMessage(player, ChatColor.RED + "The player is already member of another clan");
                                            }
                                        }
                                        else
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.RED + "This player is banned from using " + plugin.getSettingsManager().getCommandClan() + " commands");
                                        }
                                    }
                                    else
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.RED + "You cannot invite yourself");
                                    }
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + "The player doesn't not have the permissions to join simpleclans");
                                }

                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + "No player matched");
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " invite [player]");
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "You do not have leader permissions");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "You are not a member of any clan");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
            }
        }
        else if (command.equalsIgnoreCase("kick"))
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.leader.kick"))
            {
                Clan clan = plugin.getClanManager().getClan(player);

                if (clan != null)
                {
                    if (plugin.getClanManager().isLeader(clan, player))
                    {
                        if (arg.length == 1)
                        {
                            Player kicked = Helper.matchOnePlayer(arg[0]);

                            if (kicked != null)
                            {
                                if (!kicked.getName().equals(player.getName()))
                                {
                                    if (clan.isMember(kicked))
                                    {
                                        if (!plugin.getClanManager().isLeader(clan, kicked))
                                        {
                                            plugin.getClanManager().addBb(player.getName(), clan, ChatColor.AQUA + Helper.capitalize(kicked.getName()) + " has been kicked by " + player.getName());
                                            plugin.getClanManager().removePlayer(kicked, clan);
                                        }
                                        else
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.RED + "You cannot kick another leader");
                                        }
                                    }
                                    else
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.RED + "The player is not a member of your clan");
                                    }
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + "You cannot kick yourself");
                                }
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + "No player matched");
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " kick [player]");
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "You do not have leader permissions");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "You are not a member of any clan");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
            }
        }
        else if (command.equalsIgnoreCase("promote"))
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.leader.promote"))
            {
                Clan clan = plugin.getClanManager().getClan(player);

                if (clan != null)
                {
                    ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                    if (plugin.getClanManager().isLeader(clan, player))
                    {
                        if (arg.length == 1)
                        {
                            Player promoted = Helper.matchOnePlayer(arg[0]);

                            if (promoted != null)
                            {
                                if (plugin.getPermissionsManager().has(promoted, "simpleclans.leader.promotable"))
                                {
                                    if (!promoted.getName().equals(player.getName()))
                                    {
                                        if (plugin.getClanManager().allLeadersOnline(clan))
                                        {
                                            if (clan.isMember(promoted))
                                            {
                                                if (!plugin.getClanManager().isLeader(clan, promoted))
                                                {
                                                    if (plugin.getClanManager().getLeaders(clan).size() == 1)
                                                    {
                                                        plugin.getClanManager().addBb(player.getName(), clan, ChatColor.AQUA + Helper.capitalize(promoted.getName()) + " has been promoted to leader");
                                                        plugin.getClanManager().promote(promoted.getName(), clan);
                                                    }
                                                    else
                                                    {
                                                        plugin.getRequestManager().addPromoteRequest(plugin, cp, promoted.getName(), clan);
                                                        ChatBlock.sendMessage(player, ChatColor.AQUA + "Promotion vote has been requested from all leaders");
                                                    }
                                                }
                                                else
                                                {
                                                    ChatBlock.sendMessage(player, ChatColor.RED + "The player is already a leader");
                                                }
                                            }
                                            else
                                            {
                                                ChatBlock.sendMessage(player, ChatColor.RED + "The player is not a member of your clan");
                                            }
                                        }
                                        else
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.RED + "All leaders must be online to vote on this promotion");
                                        }
                                    }
                                    else
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.RED + "You cannot promote yourself");
                                    }
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + "The player does not have the permissions to lead a clan");
                                }
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + "The member to be promoted must be online");
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " promote <member>");
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "You do not have leader permissions");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "You are not a member of any clan");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
            }
        }
        else if (command.equalsIgnoreCase("demote"))
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.leader.demote"))
            {
                Clan clan = plugin.getClanManager().getClan(player);

                if (clan != null)
                {
                    ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                    if (plugin.getClanManager().isLeader(clan, player))
                    {
                        if (arg.length == 1)
                        {
                            String demotedName = arg[0];

                            if (plugin.getClanManager().allOtherLeadersOnline(clan, demotedName))
                            {
                                if (plugin.getClanManager().isLeader(clan, demotedName))
                                {
                                    if (plugin.getClanManager().getLeaders(clan).size() == 1)
                                    {
                                        plugin.getClanManager().addBb(player.getName(), clan, ChatColor.AQUA + Helper.capitalize(demotedName) + " has been demoted back to member");
                                        plugin.getClanManager().demote(demotedName, clan);
                                    }
                                    else
                                    {
                                        plugin.getRequestManager().addDemoteRequest(plugin, cp, demotedName, clan);
                                        ChatBlock.sendMessage(player, ChatColor.AQUA + "Demotion vote has been requested from all leaders");
                                    }
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + "The player is not a leader of your clan");
                                }
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + "All leaders other than the demoted must be online to vote on this demotion");
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " demote [leader]");
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "You do not have leader permissions");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "You are not a member of any clan");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
            }
        }
        else if (command.equalsIgnoreCase("clanff"))
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.leader.clanff"))
            {
                Clan clan = plugin.getClanManager().getClan(player);

                if (clan != null)
                {
                    if (plugin.getClanManager().isLeader(clan, player))
                    {
                        if (arg.length == 1)
                        {
                            String action = arg[0];

                            if (action.equalsIgnoreCase("allow"))
                            {
                                plugin.getClanManager().addBb(player.getName(), clan, ChatColor.AQUA + "Clan-wide friendly-fire is now allowed");
                                clan.setFriendlyFire(true);
                                plugin.getStorageManager().updateClan(clan);
                            }
                            else if (action.equalsIgnoreCase("block"))
                            {
                                plugin.getClanManager().addBb(player.getName(), clan, ChatColor.AQUA + "Clan-wide friendly-fire blocked");
                                clan.setFriendlyFire(false);
                                plugin.getStorageManager().updateClan(clan);
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " clanff allow/block");
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " clanff allow/block");
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "You do not have leader permissions");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "You are not a member of any clan");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
            }
        }
        else if (command.equalsIgnoreCase("ff"))
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.ff"))
            {
                Clan clan = plugin.getClanManager().getClan(player);

                if (clan != null)
                {
                    if (arg.length == 1)
                    {
                        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
                        String action = arg[0];

                        if (action.equalsIgnoreCase("allow"))
                        {
                            cp.setFriendlyFire(true);
                            plugin.getStorageManager().updateClanPlayer(cp);
                            ChatBlock.sendMessage(player, ChatColor.AQUA + "Personal friendly-fire is set to allowed");
                        }
                        else if (action.equalsIgnoreCase("auto"))
                        {
                            cp.setFriendlyFire(false);
                            plugin.getStorageManager().updateClanPlayer(cp);
                            ChatBlock.sendMessage(player, ChatColor.AQUA + "Friendy-fire is now managed by your clan");
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " ff allow/auto");
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " ff allow/auto");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "You are not a member of any clan");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
            }
        }
        else if (command.equalsIgnoreCase("resign"))
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.resign"))
            {
                Clan clan = plugin.getClanManager().getClan(player);

                if (clan != null)
                {
                    if (!plugin.getClanManager().isLeader(clan, player) || plugin.getClanManager().getLeaders(clan).size() > 1)
                    {
                        plugin.getClanManager().addBb(player.getName(), clan, ChatColor.AQUA + Helper.capitalize(player.getName()) + " has resigned");
                        plugin.getClanManager().removePlayer(player, clan);
                    }
                    else if(plugin.getClanManager().isLeader(clan, player) && plugin.getClanManager().getLeaders(clan).size() == 1)
                    {
                        plugin.getClanManager().serverAnnounce(ChatColor.AQUA + "Clan " + clan.getName() + " has been deleted");
                        plugin.getClanManager().deleteClan(clan);
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "Last leader cannot resign.  You must appoint another leader or delete the clan");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "You are not a member of any clan");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
            }
        }
        else if (command.equalsIgnoreCase("delete"))
        {
            if (arg.length == 0)
            {
                if (plugin.getPermissionsManager().has(player, "simpleclans.leader.delete"))
                {
                    Clan clan = plugin.getClanManager().getClan(player);

                    if (clan != null)
                    {
                        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                        if (plugin.getClanManager().isLeader(clan, player))
                        {
                            if (plugin.getClanManager().getLeaders(clan).size() == 1)
                            {
                                plugin.getClanManager().clanAnnounce(player.getName(), clan, ChatColor.AQUA + "Clan " + clan.getName() + " has been deleted");
                                plugin.getClanManager().deleteClan(clan);
                            }
                            else
                            {
                                plugin.getRequestManager().addDeleteRequest(plugin, cp, clan);
                                ChatBlock.sendMessage(player, ChatColor.AQUA + "Clan deletion vote has been requested from all leaders");
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + "You do not have leader permissions");
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "You are not a member of any clan");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
                }
            }
            else if (arg.length == 1)
            {
                if (plugin.getPermissionsManager().has(player, "simpleclans.mod.delete"))
                {
                    Clan clan = plugin.getClanManager().getClan(arg[0]);

                    if (clan != null)
                    {
                        plugin.getClanManager().serverAnnounce(ChatColor.AQUA + "Clan " + clan.getName() + " has been deleted");
                        plugin.getClanManager().deleteClan(clan);
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "No clan matched");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " delete");
            }

        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + "Does not match a " + plugin.getSettingsManager().getCommandClan() + " command");
        }
    }

    /**
     * Process the more command
     * @param player
     */
    public void processMore(Player player)
    {
        ChatBlock chatBlock = chatBlocks.get(player.getName());

        if (chatBlock != null && chatBlock.size() > 0)
        {
            chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

            if (chatBlock.size() > 0)
            {
                ChatBlock.sendBlank(player);
                ChatBlock.sendMessage(player, Helper.toColor(plugin.getSettingsManager().getPageHeadingsColor()) + "Type /" + plugin.getSettingsManager().getCommandMore() + " to view next page.");
            }
            ChatBlock.sendBlank(player);
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + "Nothing more to see.");
        }
    }

    /**
     * Process the accept command
     * @param player
     */
    public void processAccept(Player player)
    {
        Clan clan = plugin.getClanManager().getClan(player);

        if (clan != null)
        {
            if (plugin.getClanManager().isLeader(clan, player))
            {
                if (plugin.getRequestManager().hasRequest(clan.getTag()))
                {
                    ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                    if (cp.getVote() == null)
                    {
                        plugin.getRequestManager().accept(cp);
                        plugin.getClanManager().leaderAnnounce(player.getName(), clan, ChatColor.GREEN + Helper.capitalize(player.getName()) + " voted to accept");
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "You have already voted");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "Nothing to accept");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "You do not have leader permissions");
            }
        }
        else
        {
            if (plugin.getRequestManager().hasRequest(player.getName().toLowerCase()))
            {
                ClanPlayer cp = plugin.getClanManager().getCreateClanPlayer(player.getName());
                plugin.getRequestManager().accept(cp);
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Nothing to accept");
            }
        }
    }

    /**
     * Process the deny command
     * @param player
     */
    public void processDeny(Player player)
    {
        Clan clan = plugin.getClanManager().getClan(player);

        if (clan != null)
        {
            if (plugin.getClanManager().isLeader(clan, player))
            {
                if (plugin.getRequestManager().hasRequest(clan.getTag()))
                {
                    ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                    if (cp.getVote() == null)
                    {
                        plugin.getRequestManager().deny(plugin.getClanManager().getClanPlayer(player));
                        plugin.getClanManager().leaderAnnounce(player.getName(), clan, ChatColor.RED + Helper.capitalize(player.getName()) + " has voted to deny");
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "You have already voted");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "Nothing to deny");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "You do not have leader permissions");
            }
        }
        else
        {
            if (plugin.getRequestManager().hasRequest(player.getName().toLowerCase()))
            {
                ClanPlayer cp = plugin.getClanManager().getCreateClanPlayer(player.getName());
                plugin.getRequestManager().deny(cp);
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Nothing to deny");
            }
        }
    }

    /**
     * Returns a formatted string detailing the players armor
     * @param inv
     * @return
     */
    public static String getArmorString(PlayerInventory inv)
    {
        String out = "";

        ItemStack h = inv.getHelmet();

        if (h.getType().equals(Material.CHAINMAIL_HELMET))
        {
            out += ChatColor.WHITE + "H";
        }
        else if (h.getType().equals(Material.DIAMOND_HELMET))
        {
            out += ChatColor.AQUA + "H";
        }
        else if (h.getType().equals(Material.GOLD_HELMET))
        {
            out += ChatColor.YELLOW + "H";
        }
        else if (h.getType().equals(Material.IRON_HELMET))
        {
            out += ChatColor.GRAY + "H";
        }
        else if (h.getType().equals(Material.LEATHER_HELMET))
        {
            out += ChatColor.GOLD + "H";
        }
        else if (h.getType().equals(Material.AIR))
        {
            out += ChatColor.BLACK + "H";
        }
        else
        {
            out += ChatColor.RED + "H";
        }

        ItemStack c = inv.getChestplate();

        if (c.getType().equals(Material.CHAINMAIL_CHESTPLATE))
        {
            out += ChatColor.WHITE + "C";
        }
        else if (c.getType().equals(Material.DIAMOND_CHESTPLATE))
        {
            out += ChatColor.AQUA + "C";
        }
        else if (c.getType().equals(Material.GOLD_CHESTPLATE))
        {
            out += ChatColor.YELLOW + "C";
        }
        else if (c.getType().equals(Material.IRON_CHESTPLATE))
        {
            out += ChatColor.GRAY + "C";
        }
        else if (c.getType().equals(Material.LEATHER_CHESTPLATE))
        {
            out += ChatColor.GOLD + "C";
        }
        else if (c.getType().equals(Material.AIR))
        {
            out += ChatColor.BLACK + "C";
        }
        else
        {
            out += ChatColor.RED + "C";
        }

        ItemStack l = inv.getLeggings();

        if (l.getType().equals(Material.CHAINMAIL_LEGGINGS))
        {
            out += ChatColor.WHITE + "L";
        }
        else if (l.getType().equals(Material.DIAMOND_LEGGINGS))
        {
            out += ChatColor.AQUA + "L";
        }
        else if (l.getType().equals(Material.GOLD_LEGGINGS))
        {
            out += ChatColor.YELLOW + "L";
        }
        else if (l.getType().equals(Material.IRON_LEGGINGS))
        {
            out += ChatColor.GRAY + "L";
        }
        else if (l.getType().equals(Material.LEATHER_LEGGINGS))
        {
            out += ChatColor.GOLD + "L";
        }
        else if (l.getType().equals(Material.AIR))
        {
            out += ChatColor.BLACK + "L";
        }
        else
        {
            out += ChatColor.RED + "L";
        }

        ItemStack b = inv.getBoots();

        if (b.getType().equals(Material.CHAINMAIL_BOOTS))
        {
            out += ChatColor.WHITE + "B";
        }
        else if (b.getType().equals(Material.DIAMOND_BOOTS))
        {
            out += ChatColor.AQUA + "B";
        }
        else if (b.getType().equals(Material.GOLD_BOOTS))
        {
            out += ChatColor.YELLOW + "B";
        }
        else if (b.getType().equals(Material.IRON_BOOTS))
        {
            out += ChatColor.GRAY + "B";
        }
        else if (b.getType().equals(Material.LEATHER_BOOTS))
        {
            out += ChatColor.GOLD + "B";
        }
        else if (b.getType().equals(Material.AIR))
        {
            out += ChatColor.BLACK + "B";
        }
        else
        {
            out += ChatColor.RED + "B";
        }

        return out;
    }

    /**
     * Returns a formatted string detailing the players weapons
     * @param inv
     * @return
     */
    public String getWeaponString(PlayerInventory inv)
    {
        String headColor = Helper.toColor(plugin.getSettingsManager().getPageHeadingsColor());

        String out = "";

        int count = getItemCount(inv.all(Material.DIAMOND_SWORD));

        if (count > 0)
        {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.AQUA + "S" + headColor + countString;
        }

        count = getItemCount(inv.all(Material.GOLD_SWORD));

        if (count > 0)
        {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.YELLOW + "S" + headColor + countString;
        }

        count = getItemCount(inv.all(Material.IRON_SWORD));

        if (count > 0)
        {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.GRAY + "S" + headColor + countString;
        }

        count = getItemCount(inv.all(Material.WOOD_SWORD));

        if (count > 0)
        {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.GOLD + "S" + headColor + countString;
        }

        count = getItemCount(inv.all(Material.BOW));

        if (count > 0)
        {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.GOLD + "B" + headColor + countString;
        }

        count = getItemCount(inv.all(Material.ARROW));

        if (count > 0)
        {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.WHITE + "A" + headColor + countString;
        }

        if (out.length() == 0)
        {
            out = ChatColor.BLACK + "None";
        }

        return out;
    }

    private int getItemCount(HashMap<Integer, ? extends ItemStack> all)
    {
        int count = 0;

        for (ItemStack is : all.values())
        {
            count += is.getAmount();
        }

        return count;
    }

    /**
     * Returns a formatted string detailing the players food
     * @param inv
     * @return
     */
    public String getFoodString(PlayerInventory inv)
    {
        double out = 0;

        int count = getItemCount(inv.all(Material.GRILLED_PORK));

        if (count > 0)
        {
            out += count * 4;
        }

        count = getItemCount(inv.all(Material.COOKED_FISH));

        if (count > 0)
        {
            out += count * 2.5;
        }

        count = getItemCount(inv.all(Material.COOKIE));

        if (count > 0)
        {
            out += count * .5;
        }

        count = getItemCount(inv.all(Material.CAKE));

        if (count > 0)
        {
            out += count * 9;
        }

        count = getItemCount(inv.all(Material.CAKE_BLOCK));

        if (count > 0)
        {
            out += count * 9;
        }

        count = getItemCount(inv.all(Material.MUSHROOM_SOUP));

        if (count > 0)
        {
            out += count * 5;
        }

        count = getItemCount(inv.all(Material.BREAD));

        if (count > 0)
        {
            out += count * 2.5;
        }

        count = getItemCount(inv.all(Material.APPLE));

        if (count > 0)
        {
            out += count * 2;
        }

        count = getItemCount(inv.all(Material.GOLDEN_APPLE));

        if (count > 0)
        {
            out += count * 10;
        }

        if (out == 0)
        {
            return ChatColor.BLACK + "None";
        }
        else
        {
            return formatter.format(out) + "" + ChatColor.RED + "h";
        }
    }

    /**
     * Returns a formatted string detailing the players health
     * @param health
     * @return
     */
    public String getHealthString(int health)
    {
        String out = "";

        if (health >= 16)
        {
            out += ChatColor.GREEN;
        }
        else if (health >= 8)
        {
            out += ChatColor.GOLD;
        }
        else
        {
            out += ChatColor.RED;
        }

        for (int i = 0; i < health; i++)
        {
            out += '|';
        }

        return out;
    }
}
