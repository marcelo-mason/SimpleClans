package net.sacredlabyrinth.phaed.simpleclans.tasks;

import java.text.MessageFormat;
import net.md_5.bungee.api.ChatColor;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author roinujnosde
 */
public class CollectUpkeepTask extends BukkitRunnable {

    /**
     * Starts the repetitive task
     */
    public void start() {
        long delay = Helper.getDelayTo(1, 30);

        this.runTaskTimerAsynchronously(SimpleClans.getInstance(), delay * 20, 86400 * 20);
    }

    /**
     * (used internally)
     */
    @Override
    public void run() {
        final SimpleClans plugin = SimpleClans.getInstance();
        final SettingsManager settingsManager = plugin.getSettingsManager();
        plugin.getClanManager().getClans().forEach((clan) -> {
            double upkeep = settingsManager.getClanUpkeep();
            if (settingsManager.isMultiplyUpkeepBySize()) {
                upkeep = upkeep * clan.getSize();
            }
            final double balance = clan.getBalance();
            if (balance >= upkeep) {
                clan.setBalance(balance - upkeep);
                clan.addBb(ChatColor.AQUA + MessageFormat.format(plugin.getLang("upkeep.collected"), upkeep), false);
            } else {
                clan.disband();
            }
        });
    }

}
