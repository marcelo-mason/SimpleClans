package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.PermissionLevel;
import net.sacredlabyrinth.phaed.simpleclans.RankPermission;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

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
                            ChatBlock.sendMessage(player, ChatColor.AQUA + lang("capeoff"));
                            cp.setCapeEnabled(false);
                        } else {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + lang("capeon"));
                            cp.setCapeEnabled(true);
                        }
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + lang("clan.is.not.verified"));
                    }
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + lang("not.a.member.of.any.clan"));
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions"));
            }
        }

        if (cmd.equalsIgnoreCase("bb")) {
            if (!plugin.getPermissionsManager().has(player, "simpleclans.member.bb-toggle")) {
                lang(ChatColor.RED + "insufficient.permissions");
                return;
            }
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isVerified()) {
                    if (cp.isBbEnabled()) {
                        ChatBlock.sendMessage(player, ChatColor.AQUA + lang("bboff"));
                        cp.setBbEnabled(false);
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.AQUA + lang("bbon"));
                        cp.setBbEnabled(true);
                    }
                    plugin.getStorageManager().updateClanPlayer(cp);
                }
            }
        }

        if (cmd.equalsIgnoreCase("tag")) {
            if (!plugin.getPermissionsManager().has(player, "simpleclans.member.tag-toggle")) {
                lang(ChatColor.RED + "insufficient.permissions");
                return;
            }
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isVerified()) {
                    if (cp.isTagEnabled()) {
                        ChatBlock.sendMessage(player, ChatColor.AQUA + lang("tagoff"));
                        cp.setTagEnabled(false);
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.AQUA + lang("tagon"));
                        cp.setTagEnabled(true);
                    }
                    plugin.getStorageManager().updateClanPlayer(cp);
                }
            }
        }

        if (cmd.equalsIgnoreCase("deposit")) {
            if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.deposit-toggle")) {
                lang(ChatColor.RED + "insufficient.permissions");
                return;
            }
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();
                if (clan.isLeader(player)) {
                    if (clan.isVerified()) {
                        if (clan.isAllowDeposit()) {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + lang("depositoff"));
                            clan.setAllowDeposit(false);
                        } else {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + lang("depositon"));
                            clan.setAllowDeposit(true);
                        }
                    }
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + lang("no.leader.permissions"));
                }
            }
        }

        if (cmd.equalsIgnoreCase("fee") && plugin.getPermissionsManager().has(player, RankPermission.FEE_ENABLE,true)) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
            if (cp != null) {
                Clan clan = cp.getClan();
                if (clan.isVerified()) {
                	if (clan.isMemberFeeEnabled()) {
                		ChatBlock.sendMessage(player, ChatColor.AQUA + lang("feeoff"));
                		clan.setMemberFeeEnabled(false);
                	} else {
                		ChatBlock.sendMessage(player, ChatColor.AQUA + lang("feeon"));
                		clan.setMemberFeeEnabled(true);
                	}
                	plugin.getStorageManager().updateClan(clan);
                }
            }
        }

        if (cmd.equalsIgnoreCase("withdraw")) {
            if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.withdraw-toggle")) {
                lang(ChatColor.RED + "insufficient.permissions");
                return;
            }
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
            if (cp != null) {
                Clan clan = cp.getClan();
                if (clan.isVerified()) {
                    if (clan.isLeader(player)) {
                        if (clan.isAllowWithdraw()) {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + lang("withdrawoff"));
                            clan.setAllowWithdraw(false);
                        } else {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + lang("withdrawon"));
                            clan.setAllowWithdraw(true);
                        }
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + lang("no.leader.permissions"));
                    }
                }
            }
        }
    }
}
