package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import java.util.List;
import java.util.Random;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.beta.GenericPlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author phaed
 */
public class HomeCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public HomeCommand(SimpleClans plugin)
    {
        super("Home");
        this.plugin = plugin;
        setArgumentRange(0, 2);
        setUsages(MessageFormat.format(plugin.getLang("usage.home"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("home.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (cp != null) {
            boolean isVerified = cp.getClan().isVerified();
            String out = "";
            if (isVerified && plugin.getPermissionsManager().has(sender, "simpleclans.member.home")) {
                out = MessageFormat.format(plugin.getLang("home-menu"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
            }
            if (isVerified && cp.isLeader() && plugin.getPermissionsManager().has(sender, "simpleclans.leader.home-set")) {
                out += "\n   §b" +MessageFormat.format(plugin.getLang("home-set-menu"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
            }
            return out.isEmpty() ? null : out;
        }
        return null;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {

        if (args.length == 2 && args[0].equalsIgnoreCase(plugin.getLang("home.command.set")) && plugin.getPermissionsManager().has(player, "simpleclans.mod.home")) {
            if (plugin.getClanManager().purchaseHomeTeleportSet(player)) {
                Location loc = player.getLocation();

                Clan clan = plugin.getClanManager().getClan(args[1]);

                if (clan != null) {
                    clan.setHomeLocation(loc);
                    ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang("hombase.mod.set"), clan.getName()) + " " + ChatColor.YELLOW + Helper.toLocationString(loc));
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("the.clan.does.not.exist"));
            }
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp != null) {
            Clan clan = cp.getClan();

            if (clan.isVerified()) {
                if (cp.isTrusted()) {
                    if (args.length == 0) {
                        if (plugin.getPermissionsManager().has(player, "simpleclans.member.home")) {
                            if (plugin.getClanManager().purchaseHomeTeleport(player)) {
                                Location loc = clan.getHomeLocation();

                                if (loc == null) {
                                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("hombase.not.set"));
                                    return;
                                }

                                plugin.getTeleportManager().addPlayer(player, clan.getHomeLocation(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("now.at.homebase"), clan.getName()));
                            }
                        } else {
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                        }
                    } else {
                        String ttag = args[0];

                        if (ttag.equalsIgnoreCase("set")) {
                            Location loc = player.getLocation();

                            if (cp.isLeader()) {
                                if (plugin.getPermissionsManager().has(player, "simpleclans.leader.home-set")) {
                                    if (plugin.getPermissionsManager().teleportAllowed(player, loc)) {
                                        if (plugin.getSettingsManager().isHomebaseSetOnce() && clan.getHomeLocation() != null && !plugin.getPermissionsManager().has(player, "simpleclans.mod.home")) {
                                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("home.base.only.once"));
                                            return;
                                        }

                                        clan.setHomeLocation(loc);
                                        ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang("hombase.set"), ChatColor.YELLOW + Helper.toLocationString(loc)));
                                    } else {
                                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.teleport"));
                                    }
                                } else {
                                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                                }
                            } else {
                                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                            }
                        } else if (ttag.equalsIgnoreCase("regroup")) {
                            Location loc = player.getLocation();

                            if (cp.isLeader()) {
                                if (plugin.getPermissionsManager().has(player, "simpleclans.leader.regroup")) {
                                    List<ClanPlayer> members = clan.getAllMembers();

                                    for (ClanPlayer ccp : members) {
                                        Player pl = ccp.toPlayer();

                                        if (pl == null || pl.equals(player)) {
                                            continue;
                                        }

                                        if (pl != null) {
                                            int x = loc.getBlockX();
                                            int z = loc.getBlockZ();

                                            player.sendBlockChange(new Location(loc.getWorld(), x + 1, loc.getBlockY(), z + 1), Material.GLASS, (byte) 0);
                                            player.sendBlockChange(new Location(loc.getWorld(), x - 1, loc.getBlockY(), z - 1), Material.GLASS, (byte) 0);
                                            player.sendBlockChange(new Location(loc.getWorld(), x + 1, loc.getBlockY(), z - 1), Material.GLASS, (byte) 0);
                                            player.sendBlockChange(new Location(loc.getWorld(), x - 1, loc.getBlockY(), z + 1), Material.GLASS, (byte) 0);

                                            Random r = new Random();

                                            int xx = r.nextInt(2) - 1;
                                            int zz = r.nextInt(2) - 1;

                                            if (xx == 0 && zz == 0) {
                                                xx = 1;
                                            }

                                            x = x + xx;
                                            z = z + zz;

                                            pl.teleport(new Location(loc.getWorld(), x + .5, loc.getBlockY(), z + .5));
                                        }
                                    }
                                    ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("hombase.set") + ChatColor.YELLOW + Helper.toLocationString(loc));
                                } else {
                                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                                }
                            } else {
                                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                            }
                        } else {
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                        }
                    }
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("only.trusted.players.can.access.clan.vitals"));
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clan.is.not.verified"));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
        }
    }
}
