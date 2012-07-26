package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.beta.GenericPlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author phaed
 */
public class BbCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public BbCommand(SimpleClans plugin)
    {
        super("Bb");
        this.plugin = plugin;
        setArgumentRange(0, 1);
        setUsages(MessageFormat.format(plugin.getLang("usage.bb"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("bb.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (cp != null) {
            if (cp.getClan().isVerified()) {
                String display = null;
                if (plugin.getPermissionsManager().has(sender, "simpleclans.member.bb")) {
                    display = MessageFormat.format(plugin.getLang("0.bb.1.display.bulletin.board"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE) + "\n   §b";
                }
                if (plugin.getPermissionsManager().has(sender, "simpleclans.member.bb-add")) {
                    display += MessageFormat.format(plugin.getLang("0.bb.msg.1.add.a.message.to.the.bulletin.board"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
                }
                return display;
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
                if (args.length == 0) {
                    if (plugin.getPermissionsManager().has(player, "simpleclans.member.bb")) {
                        clan.displayBb(player);
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                    }
                } else if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {
                    if (plugin.getPermissionsManager().has(player, "simpleclans.leader.bb-clear")) {
                        if (cp.isTrusted() && cp.isLeader()) {
                            cp.getClan().clearBb();
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("cleared.bb"));
                        } else {
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                        }
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                    }
                } else if (plugin.getPermissionsManager().has(player, "simpleclans.member.bb-add")) {
                    if (cp.isTrusted()) {
                        String msg = Helper.toMessage(args);
                        clan.addBb(player.getName(), ChatColor.AQUA + player.getName() + ": " + ChatColor.WHITE + msg);
                        plugin.getStorageManager().updateClan(clan);
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                    }
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clan.is.not.verified"));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
        }
    }
}