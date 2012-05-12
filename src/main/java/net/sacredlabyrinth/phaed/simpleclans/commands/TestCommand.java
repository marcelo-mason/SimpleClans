package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public class TestCommand {

    public TestCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        plugin.getStorageManager().addStrife(cp.getClan(), plugin.getClanManager().getClan("asd"), 1);
        System.out.println(plugin.getStorageManager().retrieveStrifes(cp.getClan(), plugin.getClanManager().getClan("asd")));
    }
}
