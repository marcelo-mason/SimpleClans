package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author phaed
 */
public class RivalriesCommand {
    public RivalriesCommand() {
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

        if (arg.length != 0) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.0.rivalries"), plugin.getSettingsManager().getCommandClan()));
            return;
        }
        if (!plugin.getPermissionsManager().has(player, "simpleclans.anyone.rivalries")) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            return;
        }
        List<Clan> clans = plugin.getClanManager().getClans();
        plugin.getClanManager().sortClansByKDR(clans);

        ChatBlock chatBlock = new ChatBlock();

        ChatBlock.sendBlank(player);
        ChatBlock.saySingle(player, plugin.getSettingsManager().getServerName() + subColor + " " + plugin.getLang("rivalries") + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
        ChatBlock.sendBlank(player);
        ChatBlock.sendMessage(player, headColor + plugin.getLang("legend") + ChatColor.DARK_RED + " [" + plugin.getLang("war") + "]");
        ChatBlock.sendBlank(player);

        chatBlock.setAlignment("l", "l");
        chatBlock.addRow(plugin.getLang("clan"), plugin.getLang("rivals"));

        for (Clan clan : clans) {
            if (!clan.isVerified()) {
                continue;
            }

            chatBlock.addRow("  " + ChatColor.AQUA + clan.getName(), clan.getRivalString(ChatColor.DARK_GRAY + ", "));
        }

        boolean more = chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

        if (more) {
            plugin.getStorageManager().addChatBlock(player, chatBlock);
            ChatBlock.sendBlank(player);
            ChatBlock.sendMessage(player, headColor + MessageFormat.format(plugin.getLang("view.next.page"), plugin.getSettingsManager().getCommandMore()));
        }

        ChatBlock.sendBlank(player);
    }
}

