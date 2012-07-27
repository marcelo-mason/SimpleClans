package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author phaed
 */
public class DisbandCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public DisbandCommand(SimpleClans plugin)
    {
        super("Disband");
        this.plugin = plugin;
        setArgumentRange(0, 1);
        setUsages(MessageFormat.format(plugin.getLang("usage.disband"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("disband.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (cp != null) {
            if (cp.isLeader() && plugin.getPermissionsManager().has(sender, "simpleclans.leader.disband")) {
                return ChatColor.DARK_RED + MessageFormat.format(plugin.getLang("0.disband.1.disband.your.clan"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
            }
        }
        return null;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {

        if (args.length == 0) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.leader.disband")) {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                if (cp != null) {
                    Clan clan = cp.getClan();

                    if (clan.isLeader(player)) {
                        if (clan.getLeaders().size() == 1) {
                            clan.clanAnnounce(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("clan.has.been.disbanded"), clan.getName()));
                            clan.disband();
                        } else {
                            plugin.getRequestManager().addDisbandRequest(cp, clan);
                            ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("clan.disband.vote.has.been.requested.from.all.leaders"));
                        }
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                    }
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            }
        } else if (args.length == 1) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.mod.disband")) {
                Clan clan = plugin.getClanManager().getClan(args[0]);

                if (clan != null) {
                    plugin.getClanManager().serverAnnounce(ChatColor.AQUA + MessageFormat.format(plugin.getLang("clan.has.been.disbanded"), clan.getName()));
                    clan.disband();
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.clan.matched"));
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.0.disband"), plugin.getSettingsManager().getCommandClan()));
        }
    }
}
