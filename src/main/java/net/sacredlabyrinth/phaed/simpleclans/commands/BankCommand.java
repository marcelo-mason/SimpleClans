package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

/**
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

        if (!plugin.getPermissionsManager().has(player, "simpleclans.member.bank")) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            return;
        }
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
        double plmoney = plugin.getPermissionsManager().playerGetMoney(player);
        double money = 0;
        Clan clan = cp.getClan();

        if (clan == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
            return;
        }

        double clanbalance = clan.getBalance();

        if (clan.isMember(player)) {
            if (!clan.isVerified()) {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clan.is.not.verified"));
                return;
            }
            if (!cp.isTrusted()) {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("only.trusted.players.can.access.clan.stats"));
                return;
            }

            if (arg.length == 1) {
                if (arg[0].equalsIgnoreCase("status")) {
                    player.sendMessage(ChatColor.AQUA + MessageFormat.format("Clan-Balance: {0}", clanbalance));
                }
            } else if (arg.length == 2) {
                if (arg[1].matches("[0-9]+")) {
                    money = Double.parseDouble(arg[1]);
                }
                if (arg[0].equalsIgnoreCase("deposit")) {
                    if (!cp.getClan().isLeader(player) && !clan.isAllowDeposit()) {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                        return;
                    }
                    if (arg[1].equalsIgnoreCase("all")) {
                        clan.deposit(plmoney, player);
                    } else {
                        clan.deposit(money, player);
                    }
                } else if (arg[0].equalsIgnoreCase("withdraw")) {
                    if (!cp.getClan().isLeader(player) && !clan.isAllowWithdraw()) {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                        return;
                    }
                    if (arg[1].equalsIgnoreCase("all")) {
                        clan.withdraw(clanbalance, player);
                    } else {
                        clan.withdraw(money, player);
                    }
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.bank"), plugin.getSettingsManager().getCommandClan()));
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.bank"), plugin.getSettingsManager().getCommandClan()));
            }
        }

    }
}
