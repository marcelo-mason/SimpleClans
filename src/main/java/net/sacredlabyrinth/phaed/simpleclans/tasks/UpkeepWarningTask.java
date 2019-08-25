package net.sacredlabyrinth.phaed.simpleclans.tasks;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author roinujnosde
 */
public class UpkeepWarningTask extends BukkitRunnable {

    /**
     * Starts the repetitive task
     *
     */
    public void start() {
        long delay = Helper.getDelayTo(12, 0);

        this.runTaskTimerAsynchronously(SimpleClans.getInstance(), delay * 20, 86400 * 20);
    }

    /**
     * (used internally)
     */
    @Override
    public void run() {
        final SimpleClans plugin = SimpleClans.getInstance();
        final SettingsManager sm = plugin.getSettingsManager();
        plugin.getClanManager().getClans().forEach((clan) -> {
            final double balance = clan.getBalance();
            double upkeep = sm.getClanUpkeep();
            if (sm.isMultiplyUpkeepBySize()) {
                upkeep = upkeep * clan.getSize();
            }
            if (balance < upkeep) {
                clan.addBb(MessageFormat.format(plugin.getLang("balance.is.not.enough.for.upkeep"), upkeep), false);
            }
        });
    }

}
