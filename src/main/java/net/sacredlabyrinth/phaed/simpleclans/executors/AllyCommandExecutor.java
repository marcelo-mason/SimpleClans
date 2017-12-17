package net.sacredlabyrinth.phaed.simpleclans.executors;

import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AllyCommandExecutor implements CommandExecutor {

    SimpleClans plugin;

    public AllyCommandExecutor() {
        plugin = SimpleClans.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;

        if (!plugin.getSettingsManager().isAllyChatEnable()) {
            return false;
        }
      
        plugin.getClanManager().processAllyChat(player, Helper.toMessage(strings));
      
        return false;
    }
}
