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
 *
 * @author phaed
 */
public class ClanffCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public ClanffCommand(SimpleClans plugin)
    {
        super("Clanff");
        this.plugin = plugin;
        setArgumentRange(0, 0);
        setUsages(MessageFormat.format(plugin.getLang("usage.clanff"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("clanff.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (cp != null) {
            if (cp.isLeader() && plugin.getPermissionsManager().has(sender, "simpleclans.leader.ff")) {
                return MessageFormat.format(plugin.getLang("0.clanff.allow.block.1.toggle.clan.s.friendly.fire"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
            }
        }
        return null;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {

        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.ff")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isLeader(player)) {
                    if (args.length == 1) {
                        String action = args[0];

                        if (action.equalsIgnoreCase(plugin.getLang("allow"))) {
                            clan.addBb(player.getName(), ChatColor.AQUA + plugin.getLang("clan.wide.friendly.fire.is.allowed"));
                            clan.setFriendlyFire(true);
                            plugin.getStorageManager().updateClan(clan);
                        } else if (action.equalsIgnoreCase(plugin.getLang("block"))) {
                            clan.addBb(player.getName(), ChatColor.AQUA + plugin.getLang("clan.wide.friendly.fire.blocked"));
                            clan.setFriendlyFire(false);
                            plugin.getStorageManager().updateClan(clan);
                        } else {
                            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.clanff"), plugin.getSettingsManager().getCommandClan()));
                        }
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.clanff"), plugin.getSettingsManager().getCommandClan()));
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
    }
}
