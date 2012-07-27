package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author phaed
 */
public class ToggleCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public ToggleCommand(SimpleClans plugin)
    {
        super("Command");
        this.plugin = plugin;
        setArgumentRange(1, 2);
        setUsages(MessageFormat.format(plugin.getLang("usage.toggle"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("toggle.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (cp != null) {
            StringBuilder toggles = new StringBuilder();

            if (cp.isLeader() && plugin.getPermissionsManager().has(sender, "simpleclans.member.tag-toggle")) {
                toggles.append("tag/");
            }
            if (cp.getClan().isVerified()) {
                if (plugin.hasSpout() && plugin.getSettingsManager().isClanCapes() && plugin.getPermissionsManager().has(sender, "simpleclans.member.cape-toggle")) {
                    toggles.append("cape/");
                }
                if (cp.isTrusted()) {
                    if (plugin.getPermissionsManager().has(sender, "simpleclans.member.bb-toggle")) {
                        toggles.append("bb/");
                    }
                    if (plugin.getPermissionsManager().has(sender, "simpleclans.member.tag-toggle")) {
                        toggles.append("tag/");
                    }
                }
            }
            return toggles.length() == 0 ? null : MessageFormat.format(plugin.getLang("0.toggle.command"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE, Helper.stripTrailing(toggles.toString(), "/"));
        }

        return null;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {

        String cmd = args[0];

        if (cmd.equalsIgnoreCase("cape")) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.cape-toggle")) {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                if (cp != null) {
                    Clan clan = cp.getClan();

                    if (clan.isVerified()) {
                        if (cp.isCapeEnabled()) {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("capeoff"));
                            cp.setCapeEnabled(false);
                            if (plugin.hasSpout()) {
                                plugin.getSpoutPluginManager().clearCape(player);
                            }
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

//        if (cmd.equalsIgnoreCase("all-seeing-eye") || cmd.equalsIgnoreCase("ase")) {
//            if (plugin.getPermissionsManager().has(sender, "simpleclans.admin.all-seeing-eye-toggle")) {
//                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
//
//                if (cp != null) {
//                    cp.setAllSeeingEyeEnabled(!cp.isAllSeeingEyeEnabled());
//                } else {
//                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
//                }
//            }
//        }

        if (cmd.equalsIgnoreCase("perms")) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.toggle.claim.perms")) {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                if (cp != null) {
                    Clan clan = cp.getClan();
                    if (cp.isLeader()) {
                        if (args[1].equalsIgnoreCase("show")) {
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
                            return;
                        }

                        for (PermissionType type : PermissionType.values()) {
                            if (args[1].equalsIgnoreCase(type.getName())) {
                                if (clan.toggle(type)) {
                                    player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle." + type.getName() + ".true"));
                                } else {
                                    player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle." + type.getName() + ".false"));
                                }
                                return;
                            }
                        }

                        player.sendMessage(ChatColor.DARK_RED + plugin.getLang("toggle.not.exist"));
//                        if (args[1].equalsIgnoreCase("allybreak")) {
//                            if (clan.toggle(PermissionType.ALLOW_ALLY_BREAK)) {
//                                player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.ally.break.true"));
//                            } else {
//                                player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.ally.break.false"));
//                            }
//                            plugin.getStorageManager().updateClan(clan);
//                        } else if (args[1].equalsIgnoreCase("allybuild")) {
//                            if (clan.toggle(PermissionType.ALLOW_ALLY_BUILD)) {
//                                player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.ally.build.true"));
//                            } else {
//                                player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.ally.build.false"));
//                            }
//                            plugin.getStorageManager().updateClan(clan);
//                        } else if (args[1].equalsIgnoreCase("outsiderbreak")) {
//                            if (clan.toggle(PermissionType.ALLOW_OUTSIDER_BREAK)) {
//                                player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.outsider.break.true"));
//                            } else {
//                                player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.outsider.break.false"));
//                            }
//                            plugin.getStorageManager().updateClan(clan);
//                        } else if (args[1].equalsIgnoreCase("outsiderbuild")) {
//                            if (clan.toggle(PermissionType.ALLOW_OUTSIDER_BUILD)) {
//                                player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.outsider.build.true"));
//                            } else {
//                                player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.outsider.build.false"));
//                            }
//                            plugin.getStorageManager().updateClan(clan);
//                        } else if (args[1].equalsIgnoreCase("unverifiedbuild")) {
//                            if (clan.toggle(PermissionType.ALLOW_UNVERIFIED_BUILD)) {
//                                player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.unverified.build.true"));
//                            } else {
//                                player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.unverified.build.false"));
//                            }
//                            plugin.getStorageManager().updateClan(clan);
//                        } else if (args[1].equalsIgnoreCase("unverifiedbreak")) {
//                            if (clan.toggle(PermissionType.ALLOW_UNVERIFIED_BREAK)) {
//                                player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.unverified.break.true"));
//                            } else {
//                                player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.unverified.break.false"));
//                            }
//                            plugin.getStorageManager().updateClan(clan);
//                        } else if (args[1].equalsIgnoreCase("memberbuild")) {
//                            if (clan.toggle(PermissionType.DENY_MEMBER_BUILD)) {
//                                player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.member.build.false"));
//                            } else {
//                                player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.member.build.true"));
//                            }
//                            plugin.getStorageManager().updateClan(clan);
//                        } else if (args[1].equalsIgnoreCase("memberbreak")) {
//                            if (clan.toggle(PermissionType.DENY_MEMBER_BREAK)) {
//                                player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.member.break.false"));
//                            } else {
//                                player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("toggle.member.break.true"));
//                            }
//                            plugin.getStorageManager().updateClan(clan);
//                        } else 

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
