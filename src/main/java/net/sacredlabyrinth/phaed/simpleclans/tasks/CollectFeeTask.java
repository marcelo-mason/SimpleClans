package net.sacredlabyrinth.phaed.simpleclans.tasks;

import java.text.MessageFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

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
                for (ClanPlayer cp : clan.getFeePayers()) {
                	                	
					final boolean success = plugin.getPermissionsManager()
							.playerChargeMoney(Bukkit.getOfflinePlayer(cp.getUniqueId()), memberFee);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (success) {
                                ChatBlock.sendMessage(cp.toPlayer(), ChatColor.AQUA + 
                                        MessageFormat.format(plugin.getLang("fee.collected"), memberFee));
                                clan.setBalance(clan.getBalance() + memberFee);
                                plugin.getStorageManager().updateClanAsync(clan);
                            } else {
                            	clan.removePlayerFromClan(cp.getUniqueId());
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
