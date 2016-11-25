package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.List;


/**
 * @author phaed
 */
public class ListCommand {
    public ListCommand() {
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
        NumberFormat formatter = new DecimalFormat("#.#");

        if (!plugin.getPermissionsManager().has(player, "simpleclans.anyone.list")) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            return;
        }
        if (arg.length != 0) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.list"), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        List<Clan> clans = plugin.getClanManager().getClans();
        plugin.getClanManager().sortClansByKDR(clans);

        if (clans.isEmpty()) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.clans.have.been.created"));
            return;
        }

        ChatBlock chatBlock = new ChatBlock();
        ChatBlock.sendBlank(player);
        ChatBlock.saySingle(player, plugin.getSettingsManager().getServerName() + subColor + " " + plugin.getLang("clans.lower") + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
        ChatBlock.sendBlank(player);
        ChatBlock.sendMessage(player, headColor + plugin.getLang("total.clans") + " " + subColor + clans.size());
        ChatBlock.sendBlank(player);
        chatBlock.setAlignment("c", "l", "c", "c");
        chatBlock.setFlexibility(false, true, false, false);
        chatBlock.addRow("  " + headColor + plugin.getLang("rank"), plugin.getLang("name"), plugin.getLang("kdr"), plugin.getLang("members"));

        int rank = 1;

        for (Clan clan : clans) {
            if (!plugin.getSettingsManager().isShowUnverifiedOnList() && !clan.isVerified()) {
                continue;
            }

            String tag = plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketLeft() + plugin.getSettingsManager().getTagDefaultColor() + clan.getColorTag() + plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketRight();
            String name = (clan.isVerified() ? plugin.getSettingsManager().getPageClanNameColor() : ChatColor.GRAY) + clan.getName();
            String fullname = tag + " " + name;
            String size = ChatColor.WHITE + "" + clan.getSize();
            String kdr = clan.isVerified() ? ChatColor.YELLOW + "" + formatter.format(clan.getTotalKDR()) : "";

            chatBlock.addRow("  " + rank, fullname, kdr, size);
            rank++;
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



