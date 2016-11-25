package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MostKilledCommand {
    public MostKilledCommand() {

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

        if (!plugin.getPermissionsManager().has(player, "simpleclans.mod.mostkilled")) {
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

        ChatBlock chatBlock = new ChatBlock();
        chatBlock.setFlexibility(true, false, false);
        chatBlock.setAlignment("l", "c", "l");
        chatBlock.addRow("  " + headColor + plugin.getLang("victim"), headColor + plugin.getLang("killcount"), headColor + plugin.getLang("attacker"));

        Map<String, Integer> killsPerPlayerUnordered = plugin.getStorageManager().getMostKilled();

        if (killsPerPlayerUnordered.isEmpty()) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("nokillsfound"));
            return;
        }

        Map<String, Integer> killsPerPlayer = Helper.sortByValue(killsPerPlayerUnordered);

        for (Entry<String, Integer> attackerVictim : killsPerPlayer.entrySet()) {
            String[] split = attackerVictim.getKey().split(" ");

            if (split.length < 2) {
                continue;
            }

            int count = attackerVictim.getValue();
            String attacker = split[0];
            String victim = split[1];

            chatBlock.addRow("  " + ChatColor.WHITE + victim, ChatColor.AQUA + "" + count, ChatColor.YELLOW + attacker);
        }

        ChatBlock.saySingle(player, plugin.getSettingsManager().getServerName() + subColor + " " + plugin.getLang("mostkilled") + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
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
