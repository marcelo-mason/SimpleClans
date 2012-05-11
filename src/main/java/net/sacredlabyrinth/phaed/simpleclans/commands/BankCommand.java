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

        if (plugin.getPermissionsManager().has(player, "simpleclans.member.bank")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();
                if (clan.isMember(player)) {
                    if (clan.isVerified()) {
                        if (cp.isTrusted()) {
                            double clanbalance = clan.getBalance();
                            if (arg.length == 1) {
                                if (arg[0].equalsIgnoreCase("status")) {
                                    player.sendMessage(MessageFormat.format("Clan-Balance: {0}", clanbalance));
                                }
                            } else if (arg.length == 2) {
                                if (arg[1].matches("[0-9]+")) {
                                    double money = Double.parseDouble(arg[1]);
                                    double plmoney = plugin.getPermissionsManager().playerGetMoney(player);
                                    if (arg[0].equalsIgnoreCase("deposit")) {
                                        if (arg[1].equalsIgnoreCase("all")) {
                                            plugin.getPermissionsManager().playerChargeMoney(player, plmoney);
                                            player.sendMessage(MessageFormat.format("You withdraw {0}", plmoney));
                                            clan.addBb(player.getName(), MessageFormat.format("{0} were withdrawn.", plmoney));
                                            clan.setBalance(plmoney);
                                        }
                                        if (plugin.getPermissionsManager().playerHasMoney(player, money)) {
                                            plugin.getPermissionsManager().playerChargeMoney(player, money);
                                            player.sendMessage(MessageFormat.format("You deposited {0}", money));
                                            clan.addBb(player.getName(), MessageFormat.format("{0} were deposited.", clanbalance));
                                            clan.setBalance(clanbalance + money);
                                        }
                                    } else if (arg[0].equalsIgnoreCase("withdraw")) {
                                        if (arg[1].equalsIgnoreCase("all")) {
                                            if (clanbalance <= money) {
                                                plugin.getPermissionsManager().playerGrantMoney(player, clanbalance);
                                                player.sendMessage(MessageFormat.format("You withdraw {0}", clanbalance));
                                                clan.addBb(player.getName(), MessageFormat.format("{0} were withdrawn.", clanbalance));
                                                clan.setBalance(0);
                                            }
                                        } else {
                                            if ((clanbalance - money) >= 0) {
                                                plugin.getPermissionsManager().playerGrantMoney(player, money);
                                                clan.addBb(player.getName(), MessageFormat.format("{0} were withdrawn.", money));
                                                player.sendMessage(MessageFormat.format("You withdraw {0}.", money));
                                                clan.setBalance(clanbalance - money);
                                            }
                                        }
                                    }
                                    SimpleClans.getInstance().getStorageManager().updateClan(clan);
                                } else {
                                    ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.bank"), plugin.getSettingsManager().getCommandClan()));
                                }
                            } else {
                                ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.bank"), plugin.getSettingsManager().getCommandClan()));
                            }

                        } else {
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("only.trusted.players.can.access.clan.stats"));
                        }
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clan.is.not.verified"));
                    }
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
    }
}
