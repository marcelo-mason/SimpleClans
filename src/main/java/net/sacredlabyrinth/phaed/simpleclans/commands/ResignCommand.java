package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public class ResignCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public ResignCommand(SimpleClans plugin)
    {
        super("Resign");
        this.plugin = plugin;
        setArgumentRange(0, 0);
        setUsages(MessageFormat.format(plugin.getLang("usage.resign"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("resign.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (cp != null) {
            if (plugin.getPermissionsManager().has(sender, "simpleclans.member.resign")) {
                return MessageFormat.format(plugin.getLang("0.resign.1.resign.from.the.clan"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
            }
        }
        return null;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {

        if (plugin.getPermissionsManager().has(player, "simpleclans.member.resign")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (!clan.isLeader(player) || clan.getLeaders().size() > 1) {
                    clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("0.has.resigned"), Helper.capitalize(player.getName())));
                    clan.removePlayerFromClan(player.getName());
                } else if (clan.isLeader(player) && clan.getLeaders().size() == 1) {
                    plugin.getClanManager().serverAnnounce(ChatColor.AQUA + MessageFormat.format(plugin.getLang("clan.has.been.disbanded"), clan.getName()));
                    clan.disband();
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("last.leader.cannot.resign.you.must.appoint.another.leader.or.disband.the.clan"));
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
    }
}
