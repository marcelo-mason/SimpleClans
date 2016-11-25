package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class KillsCommand {
    public KillsCommand() {

    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();
        String headColor = plugin.getSettingsManager().getPageHeadingsColor();
        String subColor = plugin.getSettingsManager().getPageSubTitleColor();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.member.kills")) {
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
        if (!cp.isTrusted()) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("only.trusted.players.can.access.clan.stats"));
            return;
        }

        String polledPlayerName = player.getName();

        if (arg.length == 1) {
            polledPlayerName = arg[0];
        }

        ChatBlock chatBlock = new ChatBlock();
        chatBlock.setFlexibility(true, false);
        chatBlock.setAlignment("l", "c");
        chatBlock.addRow("  " + headColor + plugin.getLang("victim"), plugin.getLang("killcount"));

        Map<String, Integer> killsPerPlayerUnordered = plugin.getStorageManager().getKillsPerPlayer(polledPlayerName);

        if (killsPerPlayerUnordered.isEmpty()) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("nokillsfound"));
            return;
        }

        Map<String, Integer> killsPerPlayer = Helper.sortByValue(killsPerPlayerUnordered);

        for (Entry<String, Integer> playerKills : killsPerPlayer.entrySet()) {
            int count = playerKills.getValue();
            chatBlock.addRow("  " + playerKills.getKey(), ChatColor.AQUA + "" + count);
        }

        ChatBlock.saySingle(player, plugin.getSettingsManager().getPageClanNameColor() + Helper.capitalize(polledPlayerName) + subColor + " " + plugin.getLang("kills") + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
        ChatBlock.sendBlank(player);

        boolean more = chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

        if (more) {
            plugin.getStorageManager().addChatBlock(player, chatBlock);
            ChatBlock.sendBlank(player);
            ChatBlock.sendMessage(player, headColor + MessageFormat.format(plugin.getLang("view.next.page"), plugin.getSettingsManager().getCommandMore()));
        }

        ChatBlock.sendBlank(player);
    }
}
