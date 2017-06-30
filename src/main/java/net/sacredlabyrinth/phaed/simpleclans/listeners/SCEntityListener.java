package net.sacredlabyrinth.phaed.simpleclans.listeners;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.text.MessageFormat;

/**
 * @author phaed
 */
public class SCEntityListener implements Listener
{

    private SimpleClans plugin;

    /**
     *
     */
    public SCEntityListener()
    {
        plugin = SimpleClans.getInstance();
    }

    /**
     * @param event
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDeath(EntityDeathEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            Player victim = (Player) event.getEntity();

            if (plugin.getSettingsManager().isBlacklistedWorld(victim.getLocation().getWorld().getName()))
            {
                return;
            }

            if (victim.hasMetadata("NPC"))
            {
                return;
            }

            Player attacker = null;

            // find attacker

            EntityDamageEvent lastDamageCause = victim.getLastDamageCause();

            if (lastDamageCause instanceof EntityDamageByEntityEvent)
            {
                EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) lastDamageCause;

                if (entityEvent.getDamager() instanceof Player)
                {
                    attacker = (Player) entityEvent.getDamager();
                } else if (entityEvent.getDamager() instanceof Arrow)
                {
                    Arrow arrow = (Arrow) entityEvent.getDamager();

                    if (arrow.getShooter() instanceof Player)
                    {
                        attacker = (Player) arrow.getShooter();
                    }
                }
            }

            if (attacker != null && victim != null)
            {
                ClanPlayer acp;
                ClanPlayer vcp;
                if (SimpleClans.getInstance().hasUUID())
                {
                    acp = plugin.getClanManager().getCreateClanPlayer(attacker.getUniqueId());
                    vcp = plugin.getClanManager().getCreateClanPlayer(victim.getUniqueId());
                } else
                {
                    acp = plugin.getClanManager().getCreateClanPlayer(attacker.getName());
                    vcp = plugin.getClanManager().getCreateClanPlayer(victim.getName());
                }

                double reward = 0;
                double multipier = plugin.getSettingsManager().getKDRMultipliesPerKill();
                float kdr = acp.getKDR();

                if (vcp.getClan() == null || acp.getClan() == null || !vcp.getClan().isVerified() || !acp.getClan().isVerified())
                {
                    acp.addCivilianKill();
                    plugin.getStorageManager().insertKill(attacker, acp.getTag(), victim, "", "c");
                } else if (acp.getClan().isRival(vcp.getTag()))
                {
                    if (acp.getClan().isWarring(vcp.getClan()))
                    {
                        reward = (double) kdr * multipier * 4;
                    } else
                    {
                        reward = (double) kdr * multipier * 2;
                    }
                    acp.addRivalKill();
                    plugin.getStorageManager().insertKill(attacker, acp.getTag(), victim, vcp.getTag(), "r");
                } else if (acp.getClan().isAlly(vcp.getTag()))
                {
                    reward = (double) kdr * multipier * -1;
                } else
                {
                    reward = (double) kdr * multipier;
                    acp.addNeutralKill();
                    plugin.getStorageManager().insertKill(attacker, acp.getTag(), victim, vcp.getTag(), "n");
                }

                if (reward != 0 && plugin.getSettingsManager().isMoneyPerKill())
                {
                    for (ClanPlayer cp : acp.getClan().getOnlineMembers())
                    {
                        double money = Math.round((reward / acp.getClan().getOnlineMembers().size()) * 100D) / 100D;
                        cp.toPlayer().sendMessage(ChatColor.AQUA + MessageFormat.format(plugin.getLang("player.got.money"), money, victim.getName(), kdr));
                        plugin.getPermissionsManager().playerGrantMoney(cp.getName(), money);
                    }
                }

                // record death for victim
                vcp.addDeath();
                plugin.getStorageManager().updateClanPlayerAsync(vcp);
            }
        }
    }

    /**
     * @param event
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEntityEvent event)
    {
        if (event.isCancelled())
        {
            return;
        }

        if (plugin.getSettingsManager().isTamableMobsSharing() && event.getRightClicked() instanceof Tameable)
        {
        	Entity entity = event.getRightClicked();
            Player player = event.getPlayer();
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
            Tameable tamed = (Tameable) entity;

            if (tamed.isTamed())
            {
                if(entity instanceof Wolf && !((Wolf) entity).isSitting())
                {
                	return;
                }
                if (cp.getClan().isMember((Player) tamed.getOwner()))
                {
                    tamed.setOwner(player);
                }
            }
        }
    }

    /**
     * @param event
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onEntityTarget(EntityTargetLivingEntityEvent event)
    {
        if (plugin.getSettingsManager().isTamableMobsSharing())
        {
            if (event.getEntity() instanceof Tameable && event.getTarget() instanceof Player)
            {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer((Player) event.getTarget());
                Tameable wolf = (Tameable) event.getEntity();

                if (wolf.isTamed() && cp.getClan().isMember((Player) wolf.getOwner()))
                {
                	// cancels the event if the attacker is one out of his clan
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * @param event
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamage(EntityDamageEvent event)
    {
        if (event.isCancelled())
        {
            return;
        }

        Player attacker = null;
        Player victim = null;


        if (event instanceof EntityDamageByEntityEvent)
        {
            EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent) event;

            if (sub.getEntity() instanceof Player && sub.getDamager() instanceof Player)
            {
                attacker = (Player) sub.getDamager();
                victim = (Player) sub.getEntity();
            }

            if (plugin.getSettingsManager().isTamableMobsSharing())
            {
                if (sub.getEntity() instanceof Wolf && sub.getDamager() instanceof Player)
                {
                    attacker = (Player) sub.getDamager();
                    Wolf wolf = (Wolf) sub.getEntity();
                    ClanPlayer cp = plugin.getClanManager().getClanPlayer(attacker);
                    if (wolf.isTamed() && cp.getClan().isMember((Player) wolf.getOwner()))
                    {
                    	// Sets the wolf to friendly if the attacker is one out of his clan
                        wolf.setAngry(false);
                    }
                }
            }

            if (sub.getEntity() instanceof Player && sub.getDamager() instanceof Arrow)
            {
                Arrow arrow = (Arrow) sub.getDamager();

                if (arrow.getShooter() instanceof Player)
                {
                    attacker = (Player) arrow.getShooter();
                    victim = (Player) sub.getEntity();
                }
            }
        }

        if (victim != null && plugin.getSettingsManager().isBlacklistedWorld(victim.getLocation().getWorld().getName()))
        {
        	return;
        }

        if (attacker != null && victim != null)
        {
            ClanPlayer acp = plugin.getClanManager().getClanPlayer(attacker);
            ClanPlayer vcp = plugin.getClanManager().getClanPlayer(victim);


            Clan vclan = vcp == null ? null : vcp.getClan();
            Clan aclan = acp == null ? null : acp.getClan();


            if (plugin.getSettingsManager().isPvpOnlywhileInWar())
            {
                // if one doesn't have clan then they cant be at war

                if (aclan == null || vclan == null)
                {
                    event.setCancelled(true);
                    return;
                }

                if (plugin.getPermissionsManager().has(victim, "simpleclans.mod.nopvpinwar")
                        && attacker != null && victim != null)
                {
                    event.setCancelled(true);
                    return;
                }

                // if not warring no pvp

                if (!aclan.isWarring(vclan))
                {
                    event.setCancelled(true);
                    return;
                }
            }

            if (vclan != null)
            {
                if (aclan != null)
                {
                    // personal ff enabled, allow damage

                    if (vcp.isFriendlyFire())
                    {
                        return;
                    }

                    // clan ff enabled, allow damage

                    if (vclan.isFriendlyFire())
                    {
                        return;
                    }

                    // same clan, deny damage

                    if (vclan.equals(aclan))
                    {
                        event.setCancelled(true);
                        return;
                    }

                    // ally clan, deny damage

                    if (vclan.isAlly(aclan.getTag()))
                    {
                        event.setCancelled(true);
                    }
                } else
                {
                    // not part of a clan - check if safeCivilians is set

                    if (plugin.getSettingsManager().getSafeCivilians())
                    {
                        event.setCancelled(true);
                    }
                }
            } else
            {
                // not part of a clan - check if safeCivilians is set

                if (plugin.getSettingsManager().getSafeCivilians())
                {
                    event.setCancelled(true);
                }
            }
        }
    }
}
