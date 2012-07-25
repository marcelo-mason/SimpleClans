package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.beta.GenericPlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetRankCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public SetRankCommand(SimpleClans plugin)
    {
        super("SetRank");
        this.plugin = plugin;
        setArgumentRange(1, 50);
        setUsages(String.format(plugin.getLang("usage.setrank"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("setrank.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (cp != null && cp.isLeader() && cp.getClan().isVerified()) {
            if (plugin.getPermissionsManager().has(sender, "simpleclans.leader.setrank")) {
                return MessageFormat.format(plugin.getLang("0.trust.setrank"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
            }
        }
        return null;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {

        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.setrank")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isVerified()) {
                    if (clan.isLeader(player)) {

                        String playerName = args[0];
                        String rank = Helper.toMessage(Helper.removeFirst(args));

                        if (clan.isMember(playerName) || clan.isLeader(playerName)) {
                            ClanPlayer cpm = plugin.getClanManager().getClanPlayer(playerName);
                            cpm.setRank(rank);
                            plugin.getStorageManager().updateClanPlayer(cpm);

                            ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("player.rank.changed"));
                        } else {
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.player.matched"));
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
