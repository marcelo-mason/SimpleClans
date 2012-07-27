package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import org.bukkit.command.CommandSender;

/**
 * @author phaed
 */
public class ProfileCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public ProfileCommand(SimpleClans plugin)
    {
        super("Profile");
        this.plugin = plugin;
        setArgumentRange(0, 1);
        setUsages(MessageFormat.format(plugin.getLang("usage.profile"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("profile.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        String out = "";
        if (cp != null) {
            if (cp.getClan().isVerified() && plugin.getPermissionsManager().has(sender, "simpleclans.member.profile")) {
                out = MessageFormat.format(plugin.getLang("0.profile.1.view.your.clan.s.profile"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
            }
        }
        if (plugin.getPermissionsManager().has(sender, "simpleclans.anyone.profile")) {
            out += "\n   §b" + MessageFormat.format(plugin.getLang("0.profile.tag.1.view.a.clan.s.profile"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
        }
        return out.isEmpty() ? null : out;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {
        String headColor = plugin.getSettingsManager().getPageHeadingsColor();
        String subColor = plugin.getSettingsManager().getPageSubTitleColor();
        NumberFormat formatter = new DecimalFormat("#.#");

        Clan clan = null;

        if (args.length == 0) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.profile")) {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                if (cp == null) {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
                } else {
                    if (cp.getClan().isVerified()) {
                        clan = cp.getClan();
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clan.is.not.verified"));
                    }
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            }
        } else if (args.length == 1) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.profile")) {
                clan = plugin.getClanManager().getClan(args[0]);

                if (clan == null) {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.clan.matched"));
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.0.profile.tag"), plugin.getSettingsManager().getCommandClan()));
        }

        if (clan != null) {
            if (clan.isVerified()) {
                ChatBlock.sendBlank(player);
                ChatBlock.saySingle(player, plugin.getSettingsManager().getPageClanNameColor() + Helper.capitalize(clan.getName()) + subColor + " " + plugin.getLang("profile") + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                ChatBlock.sendBlank(player);

                String name = plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketLeft() + plugin.getSettingsManager().getTagDefaultColor() + clan.getColorTag() + plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketRight() + " " + plugin.getSettingsManager().getPageClanNameColor() + clan.getName();
                String leaders = clan.getLeadersString(plugin.getSettingsManager().getPageLeaderColor(), subColor + ", ");
                String onlineCount = ChatColor.WHITE + "" + Helper.stripOffLinePlayers(clan.getMembers()).size();
                String membersOnline = onlineCount + subColor + "/" + ChatColor.WHITE + clan.getSize();
                String inactive = ChatColor.WHITE + "" + clan.getInactiveDays() + subColor + "/" + ChatColor.WHITE + (clan.isVerified() ? plugin.getSettingsManager().getPurgeClan() : plugin.getSettingsManager().getPurgeUnverified()) + " " + plugin.getLang("days");
                String founded = ChatColor.WHITE + "" + clan.getFoundedString();
                String allies = ChatColor.WHITE + "" + clan.getAllyString(subColor + ", ");
                String rivals = ChatColor.WHITE + "" + clan.getRivalString(subColor + ", ");
                String kdr = ChatColor.YELLOW + "" + formatter.format(clan.getTotalKDR());
                String deaths = ChatColor.WHITE + "" + clan.getTotalDeaths();
                String rival = ChatColor.WHITE + "" + clan.getTotalRival();
                String neutral = ChatColor.WHITE + "" + clan.getTotalNeutral();
                String civ = ChatColor.WHITE + "" + clan.getTotalCivilian();
                String status = ChatColor.WHITE + "" + (clan.isVerified() ? plugin.getSettingsManager().getPageTrustedColor() + plugin.getLang("verified") : plugin.getSettingsManager().getPageUnTrustedColor() + plugin.getLang("unverified"));
                String claims = ChatColor.WHITE + "" + clan.getClaimCount();

                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("name.0"), name));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("status.0"), status));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("leaders.0"), leaders));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("members.online.0"), membersOnline));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("kdr.0"), kdr));
                ChatBlock.sendMessage(player, "  " + subColor + plugin.getLang("kill.totals") + " " + headColor + "[" + plugin.getLang("rival") + ":" + rival + " " + headColor + "" + plugin.getLang("neutral") + ":" + neutral + " " + headColor + "" + plugin.getLang("civilian") + ":" + civ + headColor + "]");
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("deaths.0"), deaths));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("allies.0"), allies));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("rivals.0"), rivals));
                if (plugin.getSettingsManager().isClaimingEnabled()) {
                    ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format("Claims: {0}", claims));
                }
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("founded.0"), founded));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("inactive.0"), inactive));

                ChatBlock.sendBlank(player);
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clan.is.not.verified"));
            }
        }
    }
}
