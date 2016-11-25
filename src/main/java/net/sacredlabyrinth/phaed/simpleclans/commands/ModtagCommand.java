package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

/**
 * @author phaed
 */
public class ModtagCommand {
    public ModtagCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.modtag")) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
            return;
        }

        Clan clan = cp.getClan();

        if (!clan.isVerified()) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clan.is.not.verified"));
            return;
        }
        if (!clan.isLeader(player)) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
            return;
        }
        if (arg.length != 1) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.0.modtag.tag"), plugin.getSettingsManager().getCommandClan()));
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("example.clan.modtag.4kfo.4l"));
            return;
        }

        String newtag = arg[0];
        String cleantag = Helper.cleanTag(newtag);

        if (Helper.stripColors(newtag).length() > plugin.getSettingsManager().getTagMaxLength()) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("your.clan.tag.cannot.be.longer.than.characters"), plugin.getSettingsManager().getTagMaxLength()));
            return;
        }
        if (plugin.getSettingsManager().hasDisallowedColor(newtag)) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("your.tag.cannot.contain.the.following.colors"), plugin.getSettingsManager().getDisallowedColorString()));
            return;
        }
        if (!Helper.stripColors(newtag).matches("[0-9a-zA-Z]*")) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("your.clan.tag.can.only.contain.letters.numbers.and.color.codes"));
            return;
        }
        if (!cleantag.equals(clan.getTag())) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("you.can.only.modify.the.color.and.case.of.the.tag"));
            return;
        }

        clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("tag.changed.to.0"), Helper.parseColors(newtag)));
        clan.changeClanTag(newtag);
        plugin.getClanManager().updateDisplayName(player.getPlayer());
    }
}
