package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author roinujnosde
 */
public class FeeCommand {

    /**
     * Executes the command
     *
     * @param player
     * @param args
     */
    public void execute(Player player, String[] args) {
        SimpleClans plugin = SimpleClans.getInstance();
        if (!plugin.getSettingsManager().isMemberFee()) {
            return;
        }

        if (args.length >= 1) {
            ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(player.getUniqueId());
            Clan clan = cp.getClan();
            if (!clan.isVerified()) {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clan.is.not.verified"));
                return;
            }

            if (args[0].equalsIgnoreCase("check")) {
                if (!plugin.getPermissionsManager().has(player, "simpleclans.member.fee-check")) {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                    return;
                }
                ChatBlock.sendMessage(player, ChatColor.AQUA
                        + MessageFormat.format(
                                plugin.getLang("the.fee.is.0.and.its.current.value.is.1"),
                                clan.isMemberFeeEnabled() ? plugin.getLang("fee.enabled") : plugin.getLang("fee.disabled"),
                                clan.getMemberFee()
                        ));
                return;
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
                if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.fee")) {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                    return;
                }
                if (!cp.isLeader()) {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                    return;
                }
                double newFee = 0;
                try {
                    newFee = Double.parseDouble(args[1]);
                } catch (NumberFormatException ignored) {
                    player.sendMessage(ChatColor.RED + plugin.getLang("invalid.fee"));
                    return;
                }
                    double maxFee = plugin.getSettingsManager().getMaxMemberFee();
                    if (newFee > maxFee) {
                        ChatBlock.sendMessage(player, ChatColor.RED
                                + MessageFormat.format(plugin.getLang("max.fee.allowed.is.0"), maxFee));
                        return;
                    }

                    clan.setMemberFee(newFee);
                    clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("bb.fee.set"), newFee));
                    ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("fee.set"));
                    plugin.getStorageManager().updateClanAsync(clan);
                    return;
                
            }
        }
        ChatBlock.sendMessage(player, ChatColor.RED
                + MessageFormat.format(plugin.getLang("usage.fee"), plugin.getSettingsManager().getCommandClan()));
    }
}
