package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.PermissionLevel;
import net.sacredlabyrinth.phaed.simpleclans.RankPermission;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author phaed
 */
public class ToggleCommand {

    public ToggleCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (arg.length == 0) {
            return;
        }

        String cmd = arg[0];

        if (cmd.equalsIgnoreCase("cape")) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.cape-toggle")) {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                if (cp != null) {
                    Clan clan = cp.getClan();

                    if (clan.isVerified()) {
                        if (cp.isCapeEnabled()) {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("capeoff"));
                            cp.setCapeEnabled(false);
                        } else {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("capeon"));
                            cp.setCapeEnabled(true);
                        }
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clan.is.not.verified"));
                    }
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            }
        }

        if (cmd.equalsIgnoreCase("bb") && plugin.getPermissionsManager().has(player, "simpleclans.member.bb-toggle")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isVerified()) {
                    if (cp.isBbEnabled()) {
                        ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("bboff"));
                        cp.setBbEnabled(false);
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("bbon"));
                        cp.setBbEnabled(true);
                    }
                    plugin.getStorageManager().updateClanPlayer(cp);
                }
            }
        }

        if (cmd.equalsIgnoreCase("tag") && plugin.getPermissionsManager().has(player, "simpleclans.member.tag-toggle")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isVerified()) {
                    if (cp.isTagEnabled()) {
                        ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("tagoff"));
                        cp.setTagEnabled(false);
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("tagon"));
                        cp.setTagEnabled(true);
                    }
                    plugin.getStorageManager().updateClanPlayer(cp);
                }
            }
        }

        if (cmd.equalsIgnoreCase("deposit") && plugin.getPermissionsManager().has(player, "simpleclans.leader.deposit-toggle")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();
                if (clan.isLeader(player)) {
                    if (clan.isVerified()) {
                        if (clan.isAllowDeposit()) {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("depositoff"));
                            clan.setAllowDeposit(false);
                        } else {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("depositon"));
                            clan.setAllowDeposit(true);
                        }
                    }
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                }
            }
        }

        if (cmd.equalsIgnoreCase("fee") && plugin.getPermissionsManager().has(player, RankPermission.FEE_ENABLE, PermissionLevel.LEADER, true)) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
            if (cp != null) {
                Clan clan = cp.getClan();
                if (clan.isVerified()) {
                	if (clan.isMemberFeeEnabled()) {
                		ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("feeoff"));
                		clan.setMemberFeeEnabled(false);
                	} else {
                		ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("feeon"));
                		clan.setMemberFeeEnabled(true);
                	}
                	plugin.getStorageManager().updateClanAsync(clan);
                }
            }
        }

        if (cmd.equalsIgnoreCase("withdraw") && plugin.getPermissionsManager().has(player, "simpleclans.leader.withdraw-toggle")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
            if (cp != null) {
                Clan clan = cp.getClan();
                if (clan.isVerified()) {
                    if (clan.isLeader(player)) {
                        if (clan.isAllowWithdraw()) {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("withdrawoff"));
                            clan.setAllowWithdraw(false);
                        } else {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("withdrawon"));
                            clan.setAllowWithdraw(true);
                        }
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                    }
                }
            }
        }
    }
}
