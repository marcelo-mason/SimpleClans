package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import java.util.List;
import java.util.Random;
import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author phaed
 */
public class RallyCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public RallyCommand(SimpleClans plugin)
    {
        super("Rally");
        this.plugin = plugin;
        setArgumentRange(0, 1);
        setUsages(MessageFormat.format(plugin.getLang("usage.rally"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("rally.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (cp != null) {
            if (cp.isTrusted() && cp.getClan().isVerified() && plugin.getPermissionsManager().has(sender, "simpleclans.member.rally")) {
                return MessageFormat.format(plugin.getLang("usage.rally"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
            }
        }
        return null;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp != null) {
            Clan clan = cp.getClan();

            if (clan.isVerified()) {
                if (cp.isTrusted()) {

                    if (args.length == 0) {
                        if (plugin.getPermissionsManager().has(player, "simpleclans.member.rally")) {
                            Location loc = clan.getRallyPoint();
                            if (loc == null) {
                                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("rally.point.not.set.or.expired"));
                                return;
                            }
                            if (plugin.getClanManager().purchaseRallyPointTeleport(player)) {
                                plugin.getTeleportManager().addPlayer(player, loc, ChatColor.AQUA + plugin.getLang("now.at.rally.point"));
                            }
                        } else {
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                        }
                    } else if (args.length == 1) {

                        if (args[0].equalsIgnoreCase("set")) {

                            if (cp.isLeader()) {
                                if (plugin.getPermissionsManager().has(player, "simpleclans.leader.rally-point-set")) {
                                    Location loc = player.getLocation();
                                    if (plugin.getPermissionsManager().teleportAllowed(player, loc)) {
                                        if (plugin.getClanManager().purchaseRallyPointTeleportSet(player)) {

                                            clan.setRallyPoint(loc);
                                            clan.updateRallyDate();
                                            ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang("rally.point.set"), clan.getName()) + " " + ChatColor.YELLOW + Helper.toLocationString(loc));

                                        } else {
                                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                                        }
                                    } else {
                                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.teleport"));
                                    }
                                } else {
                                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                                }
                            } else {
                                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                            }
                        } else if (args[0].equalsIgnoreCase("group")) {

                            if (cp.isLeader()) {
                                if (plugin.getPermissionsManager().has(player, "simpleclans.leader.rally-point.group")) {
                                    List<ClanPlayer> members = clan.getAllMembers();
                                    Location loc = clan.getRallyPoint();

                                    if (loc == null) {
                                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("rally.point.not.set.or.expired"));
                                        return;
                                    }
                                    for (ClanPlayer ccp : members) {
                                        Player pl = ccp.toPlayer();

                                        if (pl == null || pl.equals(player)) {
                                            continue;
                                        }

                                        int x = loc.getBlockX();
                                        int z = loc.getBlockZ();

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
