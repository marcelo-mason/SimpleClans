package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author phaed
 */
public class ToggleCommand
{

    public ToggleCommand()
    {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg)
    {
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
                            plugin.getSpoutPluginManager().clearCape(player);
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

        if (cmd.equalsIgnoreCase("bb")) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.bb-toggle")) {
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
        }

        if (cmd.equalsIgnoreCase("tag")) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.tag-toggle")) {
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
        }

        if (cmd.equalsIgnoreCase("deposit")) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.leader.deposit-toggle")) {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                if (cp != null) {
                    Clan clan = cp.getClan();
                    if (clan.isLeader(player)) {
                        if (clan.isVerified()) {
                            clan.setAllowDeposit(!clan.isAllowDeposit());
                        }
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                    }
                }
            }
        }

        if (cmd.equalsIgnoreCase("withdraw")) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.leader.withdraw-toggle")) {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
                if (cp != null) {
                    Clan clan = cp.getClan();
                    if (clan.isVerified()) {
                        if (clan.isLeader(player)) {
                            clan.setAllowWithdraw(!clan.isAllowWithdraw());
                        } else {
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                        }
                    }
                }
            }
        }

        if (cmd.equalsIgnoreCase("all-seeing-eye") || cmd.equalsIgnoreCase("ase")) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.admin.all-seeing-eye-toggle")) {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                if (cp != null) {
                    cp.setAllSeeingEyeEnabled(!cp.isAllSeeingEyeEnabled());
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                }
            }
        }

        if (cmd.equalsIgnoreCase("perms")) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.toggle.claim.perms")) {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                if (cp != null) {
                    Clan clan = cp.getClan();
                    if (arg[1].equalsIgnoreCase("allybreak")) {
                        if (clan.toggle(PermissionType.ALLOW_ALLY_BREAK)) {
                            player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.ally.break.true"));
                        } else {
                            player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.ally.break.false"));
                        }
                        plugin.getStorageManager().updateClan(clan);
                    } else if (arg[1].equalsIgnoreCase("allybuild")) {
                        if (clan.toggle(PermissionType.ALLOW_ALLY_BUILD)) {
                            player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.ally.build.true"));
                        } else {
                            player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.ally.build.false"));
                        }
                        plugin.getStorageManager().updateClan(clan);
                    } else if (arg[1].equalsIgnoreCase("outsiderbreak")) {
                        if (clan.toggle(PermissionType.ALLOW_OUTSIDER_BREAK)) {
                            player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.outsider.break.true"));
                        } else {
                            player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.outsider.break.false"));
                        }
                        plugin.getStorageManager().updateClan(clan);
                    } else if (arg[1].equalsIgnoreCase("outsiderbuild")) {
                        if (clan.toggle(PermissionType.ALLOW_OUTSIDER_BUILD)) {
                            player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.outsider.build.true"));
                        } else {
                            player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.outsider.build.false"));
                        }
                        plugin.getStorageManager().updateClan(clan);
                    } else if (arg[1].equalsIgnoreCase("unverifiedbuild")) {
                        if (clan.toggle(PermissionType.ALLOW_UNVERIFIED_BUILD)) {
                            player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.unverified.build.true"));
                        } else {
                            player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.unverified.build.false"));
                        }
                        plugin.getStorageManager().updateClan(clan);
                    } else if (arg[1].equalsIgnoreCase("unverifiedbreak")) {
                        if (clan.toggle(PermissionType.ALLOW_UNVERIFIED_BREAK)) {
                            player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.unverified.break.true"));
                        } else {
                            player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.unverified.break.false"));
                        }
                        plugin.getStorageManager().updateClan(clan);
                    } else if (arg[1].equalsIgnoreCase("memberbuild")) {
                        if (clan.toggle(PermissionType.DENY_MEMBER_BUILD)) {
                            player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.member.build.false"));
                        } else {
                            player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.member.build.true"));
                        }
                        plugin.getStorageManager().updateClan(clan);
                    } else if (arg[1].equalsIgnoreCase("memberbreak")) {
                        if (clan.toggle(PermissionType.DENY_MEMBER_BREAK)) {
                            player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.member.break.false"));
                        } else {
                            player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.member.break.true"));
                        }
                        plugin.getStorageManager().updateClan(clan);
                    } else if (arg[1].equalsIgnoreCase("show")) {
                        player.sendMessage("Enabled:");
                        for (PermissionType types : PermissionType.values()) {
                            if (clan.hasPermission(types)) {
                                player.sendMessage(ChatColor.GREEN + types.getName());
                            }
                        }
                        
                        player.sendMessage("Disabled:");
                        for (PermissionType types : PermissionType.values()) {
                            if (!clan.hasPermission(types)) {
                                player.sendMessage(ChatColor.RED + types.getName());
                            }
                        }
                    } else {
                        player.sendMessage(ChatColor.DARK_RED + plugin.getLang("toggle.not.exist"));
                    }

                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            }
        }
    }
}
