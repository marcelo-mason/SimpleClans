package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public class MenuCommand
{
    public MenuCommand()
    {
    }

    /**
     * Execute the command
     * @param player
     * @param arg
     */
    public void execute(Player player)
    {
        SimpleClans plugin = SimpleClans.getInstance();

        String headColor = plugin.getSettingsManager().getPageHeadingsColor();
        String subColor = plugin.getSettingsManager().getPageSubTitleColor();

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
        Clan clan = cp == null ? null : cp.getClan();

        boolean isLeader = cp != null && cp.isLeader();
        boolean isTrusted = cp != null && cp.isTrusted();
        boolean isVerified = clan != null && clan.isVerified();
        boolean isNonVerified = clan != null && !clan.isVerified();

        ChatBlock chatBlock = new ChatBlock();

        if (clan == null && plugin.getPermissionsManager().has(player, "simpleclans.leader.create"))
        {
            if (plugin.getSettingsManager().isePurchaseCreation())
            {
                chatBlock.addRow(ChatColor.AQUA + "  /clan create [tag] [name]" + ChatColor.WHITE + " - Purchase a new clan");
            }
            else
            {
                chatBlock.addRow(ChatColor.AQUA + "  /clan create [tag] [name]" + ChatColor.WHITE + " - Create a new clan");
            }
        }
        if (isNonVerified && plugin.getSettingsManager().isRequireVerification() && plugin.getSettingsManager().isePurchaseVerification())
        {
            chatBlock.addRow(ChatColor.DARK_RED + "  /clan verify" + ChatColor.WHITE + " - Purchase verification of your clan");
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.list"))
        {
            chatBlock.addRow(ChatColor.AQUA + "  /clan list" + ChatColor.WHITE + " - Lists all clans");
        }
        if (isVerified && plugin.getPermissionsManager().has(player, "simpleclans.member.profile"))
        {
            chatBlock.addRow(ChatColor.AQUA + "  /clan profile" + ChatColor.WHITE + " - View your clan's profile");
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.profile"))
        {
            chatBlock.addRow(ChatColor.AQUA + "  /clan profile [tag]" + ChatColor.WHITE + " - View a clan's profile");
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.member.lookup"))
        {
            chatBlock.addRow(ChatColor.AQUA + "  /clan lookup" + ChatColor.WHITE + " - Lookup your info");
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.lookup"))
        {
            chatBlock.addRow(ChatColor.AQUA + "  /clan lookup [player]" + ChatColor.WHITE + " - Lookup a player's info");
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.leaderboard"))
        {
            chatBlock.addRow(ChatColor.AQUA + "  /clan leaderboard" + ChatColor.WHITE + " - View leaderboard");
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.alliances"))
        {
            chatBlock.addRow(ChatColor.AQUA + "  /clan alliances" + ChatColor.WHITE + " - View all clan alliances");
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.rivalries"))
        {
            chatBlock.addRow(ChatColor.AQUA + "  /clan rivalries" + ChatColor.WHITE + " - View all clan rivalries");
        }
        if (isVerified && plugin.getPermissionsManager().has(player, "simpleclans.member.roster"))
        {
            chatBlock.addRow(ChatColor.AQUA + "  /clan roster" + ChatColor.WHITE + " - View your clan's member list");
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.roster"))
        {
            chatBlock.addRow(ChatColor.AQUA + "  /clan roster [tag]" + ChatColor.WHITE + " - View a clan's member list");
        }
        if (isVerified && isTrusted && plugin.getPermissionsManager().has(player, "simpleclans.member.vitals"))
        {
            chatBlock.addRow(ChatColor.AQUA + "  /clan vitals" + ChatColor.WHITE + " - View your clan member's vitals");
        }
        if (isVerified && isTrusted && plugin.getPermissionsManager().has(player, "simpleclans.member.coords"))
        {
            chatBlock.addRow(ChatColor.AQUA + "  /clan coords" + ChatColor.WHITE + " - View your clan member's coordinates");
        }
        if (isVerified && isTrusted && plugin.getPermissionsManager().has(player, "simpleclans.member.stats"))
        {
            chatBlock.addRow(ChatColor.AQUA + "  /clan stats" + ChatColor.WHITE + " - View your clan member's stats");
        }
        if (isVerified && isLeader && plugin.getPermissionsManager().has(player, "simpleclans.leader.ally"))
        {
            chatBlock.addRow(ChatColor.AQUA + "  /clan ally add/remove [tag]" + ChatColor.WHITE + " - Add/remove an ally clan");
        }
        if (isVerified && isLeader && plugin.getPermissionsManager().has(player, "simpleclans.leader.rival"))
        {
            chatBlock.addRow(ChatColor.AQUA + "  /clan rival add/remove [tag]" + ChatColor.WHITE + " - Add/remove a rival clan");
        }
        if (isVerified && plugin.getPermissionsManager().has(player, "simpleclans.member.bb"))
        {
            chatBlock.addRow(ChatColor.AQUA + "  /clan bb" + ChatColor.WHITE + " - Display bulletin board");
        }
        if (isVerified && isLeader && plugin.getPermissionsManager().has(player, "simpleclans.leader.bb"))
        {
            chatBlock.addRow(ChatColor.AQUA + "  /clan bb [msg]" + ChatColor.WHITE + " - Add a message to the bulletin board");
        }
        if (isVerified && isLeader && plugin.getPermissionsManager().has(player, "simpleclans.leader.modtag"))
        {
            chatBlock.addRow(ChatColor.AQUA + "  /clan modtag [tag]" + ChatColor.WHITE + " - Modify the clan's tag");
        }
        if (isVerified && isLeader && plugin.getSpoutPluginManager().isHasSpout() && plugin.getSettingsManager().isClanCapes() && plugin.getPermissionsManager().has(player, "simpleclans.leader.cape"))
        {
            chatBlock.addRow(ChatColor.AQUA + "  /clan cape [url]" + ChatColor.WHITE + " - Change your clan's cape");
        }
        if (isLeader && plugin.getPermissionsManager().has(player, "simpleclans.leader.invite"))
        {
            chatBlock.addRow(ChatColor.AQUA + "  /clan invite [player]" + ChatColor.WHITE + " - Invite a player");
        }
        if (isLeader && plugin.getPermissionsManager().has(player, "simpleclans.leader.kick"))
        {
            chatBlock.addRow(ChatColor.AQUA + "  /clan kick [player]" + ChatColor.WHITE + " - Kick a player from the clan");
        }
        if (isVerified && isLeader && plugin.getPermissionsManager().has(player, "simpleclans.leader.settrust"))
        {
            chatBlock.addRow(ChatColor.AQUA + "  /clan trust/untrust [player]" + ChatColor.WHITE + " - Set trust level");
        }
        if (isLeader && plugin.getPermissionsManager().has(player, "simpleclans.leader.promote"))
        {
            chatBlock.addRow(ChatColor.AQUA + "  /clan promote [member]" + ChatColor.WHITE + " - Promote a member to leader");
        }
        if (isLeader && plugin.getPermissionsManager().has(player, "simpleclans.leader.demote"))
        {
            chatBlock.addRow(ChatColor.AQUA + "  /clan demote [leader]" + ChatColor.WHITE + " - Demote a leader to member");
        }
        if (isLeader && plugin.getPermissionsManager().has(player, "simpleclans.leader.ff"))
        {
            chatBlock.addRow(ChatColor.AQUA + "  /clan clanff allow/block" + ChatColor.WHITE + " - Toggle clan's friendly fire");
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.member.ff"))
        {
            chatBlock.addRow(ChatColor.AQUA + "  /clan ff allow/auto" + ChatColor.WHITE + " - Toggle personal friendly fire");
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.member.resign"))
        {
            chatBlock.addRow(ChatColor.AQUA + "  /clan resign" + ChatColor.WHITE + " - Resign from the clan");
        }
        if (isLeader && plugin.getPermissionsManager().has(player, "simpleclans.leader.disband"))
        {
            chatBlock.addRow(ChatColor.AQUA + "  /clan disband " + ChatColor.WHITE + " - Disband your clan");
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.mod.verify") && plugin.getSettingsManager().isRequireVerification())
        {
            chatBlock.addRow(ChatColor.DARK_RED + "  /clan verify [tag]" + ChatColor.WHITE + " - Verify an unverified clan");
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.mod.disband"))
        {
            chatBlock.addRow(ChatColor.DARK_RED + "  /clan disband [tag]" + ChatColor.WHITE + " - Disband a clan");
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.mod.ban"))
        {
            chatBlock.addRow(ChatColor.DARK_RED + "  /clan ban/unban [player]" + ChatColor.WHITE + " - Ban/unban a player");
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.mod.globalff"))
        {
            chatBlock.addRow(ChatColor.DARK_RED + "  /clan globalff allow/auto " + ChatColor.WHITE + " - Set global friendly fire");
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.admin.reload"))
        {
            chatBlock.addRow(ChatColor.DARK_RED + "  /clan reload" + ChatColor.WHITE + " - Reload configuration");
        }
        if (chatBlock.isEmpty())
        {
            ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
            return;
        }

        ChatBlock.sendBlank(player);
        ChatBlock.saySingle(player, plugin.getSettingsManager().getServerName() + subColor + " clan commands " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
        ChatBlock.sendBlank(player);

        boolean more = chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

        if (more)
        {
            plugin.getStorageManager().addChatBlock(player, chatBlock);
            ChatBlock.sendBlank(player);
            ChatBlock.sendMessage(player, headColor + "Type /" + plugin.getSettingsManager().getCommandMore() + " to view next page.");
        }

        ChatBlock.sendBlank(player);
    }
}
