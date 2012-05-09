package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.HashSet;

/**
 *
 * @author phaed
 */
public class BankCommand {

    public BankCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.kick")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();
                if (arg.length == 1) {
                } else if (arg.length == 2) {
                    
                    double money = Double.parseDouble(arg[1]);
                    double clanbalance = clan.getBalance();
                    
                    if (arg[0].equalsIgnoreCase("deposit")) {
                        plugin.getPermissionsManager().playerChargeMoney(player, money);
                        player.sendMessage("You deposited " + money);
                        clan.setBalance(clanbalance + money);
                    } else if (arg[0].equalsIgnoreCase("withdraw")) {
                        if (arg[1].equalsIgnoreCase("all")) {
                                plugin.getPermissionsManager().playerGrantMoney(player, clanbalance);
                                player.sendMessage("You withdraw " + clanbalance);
                                clan.setBalance(0);
                        } else {
                            if ((clanbalance - money) >= 0) {
                                plugin.getPermissionsManager().playerGrantMoney(player, money);
                                player.sendMessage("You withdraw " + money);
                                clan.setBalance(clanbalance - money);
                            }
                        }
                    }
                    SimpleClans.getInstance().getStorageManager().updateClan(clan);
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.kick.player"), plugin.getSettingsManager().getCommandClan()));
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
    }
}
