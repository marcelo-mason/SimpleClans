package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public class InviteCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public InviteCommand(SimpleClans plugin)
    {
        super("Invite");
        this.plugin = plugin;
        setArgumentRange(1, 1);
        setUsages(MessageFormat.format(plugin.getLang("usage.invite"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("invite.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (cp != null) {
            if (cp.isLeader() && plugin.getPermissionsManager().has(sender, "simpleclans.leader.invite")) {
                return MessageFormat.format(plugin.getLang("0.invite.player.1.invite.a.player"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
            }
        }
        return null;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {
        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.invite")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isLeader(player)) {
                    Player invited = Helper.matchOnePlayer(args[0]);

                    if (invited != null) {
                        if (plugin.getPermissionsManager().has(invited, "simpleclans.member.can-join")) {
                            if (!invited.getName().equals(player.getName())) {
                                if (!plugin.getSettingsManager().isBanned(player.getName())) {
                                    ClanPlayer cpInv = plugin.getClanManager().getClanPlayer(invited);

                                    if (cpInv == null) {
                                        if (plugin.getClanManager().purchaseInvite(player)) {
                                            plugin.getRequestManager().addInviteRequest(cp, invited.getName(), clan);
                                            ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(plugin.getLang("has.been.asked.to.join"), Helper.capitalize(invited.getName()), clan.getName()));
                                        }
                                    } else {
                                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("the.player.is.already.member.of.another.clan"));
                                    }
                                } else {
                                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("this.player.is.banned.from.using.clan.commands"));
                                }
                            } else {
                                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("you.cannot.invite.yourself"));
                            }
                        } else {
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("the.player.doesn.t.not.have.the.permissions.to.join.clans"));
                        }
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.player.matched"));
                    }
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
    }
}
