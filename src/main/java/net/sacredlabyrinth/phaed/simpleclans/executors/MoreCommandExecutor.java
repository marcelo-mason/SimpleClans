package net.sacredlabyrinth.phaed.simpleclans.executors;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

public class MoreCommandExecutor implements CommandExecutor {
    SimpleClans plugin;

    public MoreCommandExecutor() {
        plugin = SimpleClans.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;

        if (plugin.getSettingsManager().isBanned(player.getName())) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("banned"));
            return false;
        }

        ChatBlock chatBlock = plugin.getStorageManager().getChatBlock(player);

        if (chatBlock == null || chatBlock.size() <= 0) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("nothing.more.to.see"));
            return false;
        }

        chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

        if (chatBlock.size() > 0) {
            ChatBlock.sendBlank(player);
            ChatBlock.sendMessage(player, plugin.getSettingsManager().getPageHeadingsColor() + MessageFormat.format(plugin.getLang("view.next.page"), plugin.getSettingsManager().getCommandMore()));
        }
        ChatBlock.sendBlank(player);
        return true;
    }
}
