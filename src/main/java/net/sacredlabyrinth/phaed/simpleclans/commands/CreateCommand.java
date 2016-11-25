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
public class CreateCommand {
    public CreateCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.create")) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            return;
        }
        if (arg.length < 2) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.create.tag"), plugin.getSettingsManager().getCommandClan()));
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("example.clan.create"));
            return;
        }

        String tag = arg[0];
        String cleanTag = Helper.cleanTag(arg[0]);

        String name = Helper.toMessage(Helper.removeFirst(arg));

        boolean bypass = plugin.getPermissionsManager().has(player, "simpleclans.mod.bypass");

        if (!bypass) {
            if (cleanTag.length() > plugin.getSettingsManager().getTagMaxLength()) {
                ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("your.clan.tag.cannot.be.longer.than.characters"), plugin.getSettingsManager().getTagMaxLength()));
                return;
            }
            if (cleanTag.length() <= plugin.getSettingsManager().getTagMinLength()) {
                ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("your.clan.tag.must.be.longer.than.characters"), plugin.getSettingsManager().getTagMinLength()));
                return;
            }
            if (plugin.getSettingsManager().hasDisallowedColor(tag)) {
                ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("your.tag.cannot.contain.the.following.colors"), plugin.getSettingsManager().getDisallowedColorString()));
                return;
            }
            if (Helper.stripColors(name).length() > plugin.getSettingsManager().getClanMaxLength()) {
                ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("your.clan.name.cannot.be.longer.than.characters"), plugin.getSettingsManager().getClanMaxLength()));
                return;
            }
            if (Helper.stripColors(name).length() <= plugin.getSettingsManager().getClanMinLength()) {
                ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("your.clan.name.must.be.longer.than.characters"), plugin.getSettingsManager().getClanMinLength()));
                return;
            }
            if (plugin.getSettingsManager().isDisallowedWord(cleanTag.toLowerCase())) {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("that.tag.name.is.disallowed"));
                return;
            }
        }

        if (!cleanTag.matches("[0-9a-zA-Z]*")) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("your.clan.tag.can.only.contain.letters.numbers.and.color.codes"));
            return;
        }
        if (name.contains("&")) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("your.clan.name.cannot.contain.color.codes"));
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp != null) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("you.must.first.resign"), cp.getClan().getName()));
            return;
        }
        if (plugin.getClanManager().isClan(cleanTag)) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clan.with.this.tag.already.exists"));
            return;
        }
        if (plugin.getClanManager().purchaseCreation(player)) {
            plugin.getClanManager().createClan(player, tag, name);

            Clan clan = plugin.getClanManager().getClan(tag);
            clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("clan.created"), name));
            plugin.getStorageManager().updateClan(clan);

            if (plugin.getSettingsManager().isRequireVerification()) {
                boolean verified = !plugin.getSettingsManager().isRequireVerification() || plugin.getPermissionsManager().has(player, "simpleclans.mod.verify");

                if (!verified) {
                    ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("get.your.clan.verified.to.access.advanced.features"));
                }
            }
        }
    }
}
