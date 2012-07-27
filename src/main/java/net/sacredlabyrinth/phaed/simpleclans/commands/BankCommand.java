package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.results.BankResult;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public class BankCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public BankCommand(SimpleClans plugin)
    {
        super("Bank");
        this.plugin = plugin;
        setArgumentRange(1, 2);
        setUsages(MessageFormat.format(plugin.getLang("usage.bank"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("bank.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (cp != null) {
            if (cp.isTrusted() && cp.getClan().isVerified()) {
                if (plugin.getPermissionsManager().has(sender, "simpleclans.member.bank")) {
                    return MessageFormat.format(plugin.getLang("bank.withdraw.deposit.status"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
                }
            }
        }
        return null;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {
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
                            if (args.length == 1) {
                                if (args[0].equalsIgnoreCase("status")) {
                                    player.sendMessage(ChatColor.AQUA + MessageFormat.format("Clan-Balance: {0}", clanbalance));
                                }
                            } else if (args.length == 2) {
                                if (args[1].matches("[0-9]+")) {
                                    money = Double.parseDouble(args[1]);
                                }

                                BankResult result = null;
                                double amount = 0;

                                if (args[0].equalsIgnoreCase("deposit")) {
                                    if (cp.getClan().isLeader(player) || clan.isAllowDeposit()) {
                                        if (args[1].equalsIgnoreCase("all")) {
                                            amount = plmoney;
                                            result = clan.deposit(plmoney, player);
                                        } else {
                                            amount = money;
                                            result = clan.deposit(money, player);
                                        }
                                    } else {
                                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                                    }
                                } else if (args[0].equalsIgnoreCase("withdraw")) {
                                    if (cp.getClan().isLeader(player) || clan.isAllowWithdraw()) {
                                        if (args[1].equalsIgnoreCase("all")) {
                                            amount = clanbalance;
                                            result = clan.withdraw(clanbalance, player);
                                        } else {
                                            amount = money;
                                            result = clan.withdraw(money, player);
                                        }
                                    } else {
                                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                                    }
                                } else {
                                    ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.bank"), plugin.getSettingsManager().getCommandClan()));
                                }

                                if (result != null) {

                                    switch (result) {
                                        case BANK_NOT_ENOUGH_MONEY:
                                            player.sendMessage(ChatColor.AQUA + plugin.getLang("clan.bank.not.enough.money"));
                                            break;
                                        case PLAYER_NOT_ENOUGH_MONEY:
                                            player.sendMessage(ChatColor.AQUA + plugin.getLang("not.sufficient.money"));
                                            break;
                                        case SUCCESS_DEPOSIT:
                                            player.sendMessage(ChatColor.AQUA + MessageFormat.format(plugin.getLang("player.clan.deposit"), amount));
                                            clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("bb.clan.deposit"), amount));
                                            break;
                                        case SUCCESS_WITHDRAW:
                                            player.sendMessage(ChatColor.AQUA + MessageFormat.format(plugin.getLang("player.clan.withdraw"), amount));
                                            clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("bb.clan.withdraw"), amount));
                                            break;
                                        case FAILED:
                                            player.sendMessage(ChatColor.DARK_RED + plugin.getLang("transaction.failed"));
                                            break;
                                    }

                                }
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
