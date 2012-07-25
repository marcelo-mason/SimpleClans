package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.beta.GenericPlayerCommand;
import org.bukkit.command.CommandSender;

/**
 * @author phaed
 */
public class CapeCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public CapeCommand(SimpleClans plugin)
    {
        super("Cape");
        this.plugin = plugin;
        setArgumentRange(1, 1);
        setUsages(String.format(plugin.getLang("usage.cape"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("cape.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (cp != null) {
            if (cp.getClan().isVerified() && cp.isLeader() && plugin.hasSpout() && plugin.getSettingsManager().isClanCapes() && plugin.getPermissionsManager().has(sender, "simpleclans.leader.cape")) {
                return MessageFormat.format(plugin.getLang("0.cape.url.1.change.your.clan.s.cape"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
            }
        }
        return null;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {

        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.cape")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isVerified()) {
                    if (clan.isLeader(player)) {
                        if (args.length == 1) {
                            String url = args[0];

                            if (url.substring(url.length() - 4, url.length()).equalsIgnoreCase(".png") && url.length() > 5 && url.length() < 255) {
                                if (Helper.testURL(url)) {
                                    clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("changed.the.clan.cape"), Helper.capitalize(player.getName())));
                                    clan.setClanCape(url);
                                } else {
                                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("url.error"));
                                }
                            } else {
                                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("cape.must.be.png"));
                            }
                        } else {
                            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.cape.url"), plugin.getSettingsManager().getCommandClan()));
                        }
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
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
}
