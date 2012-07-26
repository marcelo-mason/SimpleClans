package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.beta.GenericPlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author phaed
 */
public class LookupCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public LookupCommand(SimpleClans plugin)
    {
        super("Lookup");
        this.plugin = plugin;
        setArgumentRange(0, 1);
        setUsages(MessageFormat.format(plugin.getLang("usage.lookup"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("lookup.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        String out = "";
        if (plugin.getPermissionsManager().has(sender, "simpleclans.member.lookup")) {
            out = MessageFormat.format(plugin.getLang("0.lookup.1.lookup.your.info"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
        }
        if (plugin.getPermissionsManager().has(sender, "simpleclans.anyone.lookup")) {
            out += "\n   §b" + MessageFormat.format(plugin.getLang("0.lookup.player.1.lookup.a.player.s.info"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
        }
        return out.isEmpty() ? null : out;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {

        String headColor = plugin.getSettingsManager().getPageHeadingsColor();
        String subColor = plugin.getSettingsManager().getPageSubTitleColor();
        NumberFormat formatter = new DecimalFormat("#.#");

        String playerName = null;

        if (args.length == 0) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.lookup")) {
                playerName = player.getName();
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            }
        } else if (args.length == 1) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.lookup")) {
                playerName = args[0];
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.lookup.tag"), plugin.getSettingsManager().getCommandClan()));
        }

        if (playerName != null) {
            ClanPlayer targsetCp = plugin.getClanManager().getAnyClanPlayer(playerName);
            ClanPlayer myCp = plugin.getClanManager().getClanPlayer(player.getName());
            Clan myClan = myCp == null ? null : myCp.getClan();

            if (targsetCp != null) {
                Clan targsetClan = targsetCp.getClan();

                ChatBlock.sendBlank(player);
                ChatBlock.saySingle(player, MessageFormat.format(plugin.getLang("s.player.info"), plugin.getSettingsManager().getPageClanNameColor() + targsetCp.getName() + subColor) + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                ChatBlock.sendBlank(player);

                String clanName = ChatColor.WHITE + plugin.getLang("none");

                if (targsetClan != null) {
                    clanName = plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketLeft() + plugin.getSettingsManager().getTagDefaultColor() + targsetClan.getColorTag() + plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketRight() + " " + plugin.getSettingsManager().getPageClanNameColor() + targsetClan.getName();
                }

                String status = targsetClan == null ? ChatColor.WHITE + plugin.getLang("free.agent") : (targsetCp.isLeader() ? plugin.getSettingsManager().getPageLeaderColor() + plugin.getLang("leader") : (targsetCp.isTrusted() ? plugin.getSettingsManager().getPageTrustedColor() + plugin.getLang("trusted") : plugin.getSettingsManager().getPageUnTrustedColor() + plugin.getLang("untrusted")));
                String rank = ChatColor.WHITE + "" + Helper.parseColors(targsetCp.getRank());
                String joinDate = ChatColor.WHITE + "" + targsetCp.getJoinDateString();
                String lastSeen = ChatColor.WHITE + "" + targsetCp.getLastSeenString();
                String inactive = ChatColor.WHITE + "" + targsetCp.getInactiveDays() + subColor + "/" + ChatColor.WHITE + plugin.getSettingsManager().getPurgePlayers() + " days";
                String rival = ChatColor.WHITE + "" + targsetCp.getRivalKills();
                String neutral = ChatColor.WHITE + "" + targsetCp.getNeutralKills();
                String civilian = ChatColor.WHITE + "" + targsetCp.getCivilianKills();
                String deaths = ChatColor.WHITE + "" + targsetCp.getDeaths();
                String kdr = ChatColor.YELLOW + "" + formatter.format(targsetCp.getKDR());
                String power = ChatColor.YELLOW + "" + formatter.format(targsetCp.getPower());
                String pastClans = ChatColor.WHITE + "" + targsetCp.getPastClansString(headColor + ", ");

                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("clan.0"), clanName));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("rank.0"), rank));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("status.0"), status));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("kdr.0"), kdr));
                if (plugin.getSettingsManager().isClaimingEnabled()) {
                    ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("power.0"), power));
                }
                ChatBlock.sendMessage(player, "  " + subColor + plugin.getLang("kill.totals") + " " + headColor + "[" + plugin.getLang("rival") + ":" + rival + " " + headColor + "" + plugin.getLang("neutral") + ":" + neutral + " " + headColor + "" + plugin.getLang("civilian") + ":" + civilian + headColor + "]");
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("deaths.0"), deaths));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("join.date.0"), joinDate));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("last.seen.0"), lastSeen));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("past.clans.0"), pastClans));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("inactive.0"), inactive));

                if (args.length == 1 && targsetClan != null) {
                    if (!targsetCp.equals(myCp)) {
                        String killType = ChatColor.GRAY + plugin.getLang("neutral");

                        if (targsetClan == null) {
                            killType = ChatColor.DARK_GRAY + plugin.getLang("civilian");
                        } else if (myClan != null && myClan.isRival(targsetClan.getTag())) {
                            killType = ChatColor.WHITE + plugin.getLang("rival");
                        }

                        ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(plugin.getLang("kill.type.0"), killType));
                    }
                }

                ChatBlock.sendBlank(player);
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.player.data.found"));

                if (args.length == 1 && myClan != null) {
                    ChatBlock.sendBlank(player);
                    ChatBlock.sendMessage(player, MessageFormat.format(plugin.getLang("kill.type.civilian"), ChatColor.DARK_GRAY));
                }
            }
        }
    }
}
