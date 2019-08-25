package net.sacredlabyrinth.phaed.simpleclans.tasks;

import java.text.MessageFormat;
import net.md_5.bungee.api.ChatColor;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author roinujnosde
 */
public class CollectFeeTask extends BukkitRunnable {
    
    /**
     * Starts the repetitive task
     */
    public void start() {
        long delay = Helper.getDelayTo(1, 0);
        
        this.runTaskTimerAsynchronously(SimpleClans.getInstance(), delay * 20, 86400 * 20);
    }
    
    /**
     * (used internally)
     */
    @Override
    public void run() {
        final SimpleClans plugin = SimpleClans.getInstance();
        
        for (Clan clan : plugin.getClanManager().getClans()) {
            final double memberFee = clan.getMemberFee();
            
            if (clan.isMemberFeeEnabled() && memberFee > 0) {
                for (ClanPlayer cp : clan.getNonLeaders()) {
                    final boolean success = plugin.getPermissionsManager().playerChargeMoney(cp.getName(), memberFee);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (success) {
                                ChatBlock.sendMessage(cp.toPlayer(), ChatColor.AQUA + 
                                        MessageFormat.format(plugin.getLang("fee.collected"), memberFee));
                                clan.setBalance(clan.getBalance() + memberFee);
                                plugin.getStorageManager().updateClanAsync(clan);
                            } else {
                                if (plugin.hasUUID()) {
                                    clan.removePlayerFromClan(cp.getUniqueId());
                                } else {
                                    clan.removePlayerFromClan(cp.getName());
                                }
                                clan.addBb(ChatColor.AQUA + 
                                        MessageFormat.format(plugin.getLang("bb.fee.player.kicked"), cp.getName()));
                            }
                        }
                    }.runTask(plugin);   
                }
            }
        } 
    }
}
