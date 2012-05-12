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
            double plmoney = plugin.getPermissionsManager().playerGetMoney(player);
            double money = 0;
            Clan clan = cp.getClan();
            double clanbalance = clan.getBalance();
            
            if (cp != null) {
                
                if (clan.isMember(player)) {
                    if (clan.isVerified()) {
                        if (cp.isTrusted()) {
                            if (arg.length == 1) {
                                if (arg[0].equalsIgnoreCase("status")) {
                                    player.sendMessage(ChatColor.AQUA + MessageFormat.format("Clan-Balance: {0}", clanbalance));
                                }
                            } else if (arg.length == 2) {
                                if (arg[1].matches("[0-9]+")) {
                                    money = Double.parseDouble(arg[1]);
                                }
                                if (arg[0].equalsIgnoreCase("deposit")) {
                                    if (arg[1].equalsIgnoreCase("all")) {
                                        clan.deposit(plmoney, player);
                                    } else {
                                        clan.deposit(money, player);
                                    }
                                } else if (arg[0].equalsIgnoreCase("withdraw")) {
                                    if (cp.getClan().isLeader(player)) {
                                        if (arg[1].equalsIgnoreCase("all")) {
                                            clan.withdraw(clanbalance, player);
                                        } else {
                                            clan.withdraw(money, player);
                                        }
                                    }
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
