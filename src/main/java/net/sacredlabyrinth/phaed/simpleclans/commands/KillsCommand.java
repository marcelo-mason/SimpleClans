package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.managers.StorageManager.DataCallback;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.MessageFormat;
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
        if (!plugin.getPermissionsManager().has(player, RankPermission.KILLS, PermissionLevel.TRUSTED, true)) {
        	return;
        }

        String polledPlayerName = player.getName();

        if (arg.length == 1) {
            polledPlayerName = arg[0];
        }
        
        final String name = polledPlayerName;
        
        plugin.getStorageManager().getKillsPerPlayer(polledPlayerName, new DataCallback<Map<String, Integer>>() {
			@Override
			public void onResultReady(Map<String, Integer> data) {
				
				new BukkitRunnable() {
					@Override
					public void run() {

				        ChatBlock chatBlock = new ChatBlock();
				        chatBlock.setFlexibility(true, false);
				        chatBlock.setAlignment("l", "c");
				        chatBlock.addRow("  " + headColor + plugin.getLang("victim"), plugin.getLang("killcount"));				        

				        if (data.isEmpty()) {
				            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("nokillsfound"));
				            return;
				        }

				        Map<String, Integer> killsPerPlayer = Helper.sortByValue(data);

				        for (Entry<String, Integer> playerKills : killsPerPlayer.entrySet()) {
				            int count = playerKills.getValue();
				            chatBlock.addRow("  " + playerKills.getKey(), ChatColor.AQUA + "" + count);
				        }

				        ChatBlock.saySingle(player, plugin.getSettingsManager().getPageClanNameColor() + Helper.capitalize(name) + subColor + " " + plugin.getLang("kills") + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
				        ChatBlock.sendBlank(player);

				        boolean more = chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

				        if (more) {
				            plugin.getStorageManager().addChatBlock(player, chatBlock);
				            ChatBlock.sendBlank(player);
				            ChatBlock.sendMessage(player, headColor + MessageFormat.format(plugin.getLang("view.next.page"), plugin.getSettingsManager().getCommandMore()));
				        }

				        ChatBlock.sendBlank(player);						
					}
				}.runTask(plugin);
			}
		});
    }
}
