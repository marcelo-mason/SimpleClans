package net.sacredlabyrinth.phaed.simpleclans.listeners;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
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

/**
 * @author phaed
 */
public class SCEntityListener implements Listener
{

    private SimpleClans plugin;

    /**
     *
     */
    public SCEntityListener(SimpleClans plugin)
    {
        this.plugin = plugin;
    }

    /**
     * @param event
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDeath(EntityDeathEvent event)
    {
        if (event.getEntity() instanceof Player) {
            Player victim = (Player) event.getEntity();

            if (plugin.getSettingsManager().isBlacklistedWorld(victim.getLocation().getWorld().getName())) {
                return;
            }

            Player attacker = null;

            // find attacker

            EntityDamageEvent lastDamageCause = victim.getLastDamageCause();

            if (lastDamageCause instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) lastDamageCause;

                if (entityEvent.getDamager() instanceof Player) {
                    attacker = (Player) entityEvent.getDamager();
                } else if (entityEvent.getDamager() instanceof Arrow) {
                    Arrow arrow = (Arrow) entityEvent.getDamager();

                    if (arrow.getShooter() instanceof Player) {
                        attacker = (Player) arrow.getShooter();
                    }
                }
            }
            if (victim != null) {
                ClanPlayer vcp = plugin.getClanManager().getCreateClanPlayer(victim.getName());

                //removes power
                double minpower = plugin.getSettingsManager().getPowerLossPerDeath();
                double min = plugin.getSettingsManager().getMinPower();
                if ((vcp.getPower() - minpower) > min) {
                    vcp.setPower(min);
                } else {
                    vcp.lossPower(minpower);
                }

                if (attacker != null) {
                    ClanPlayer acp = plugin.getClanManager().getCreateClanPlayer(attacker.getName());

                    int strifemax = plugin.getSettingsManager().getStrifeLimit();

                    if (plugin.getSettingsManager().isAutoWar()) {
                        if (acp.getClan() != null && vcp.getClan() != null) {
                            if (!acp.getClan().equals(vcp.getClan()) && !acp.getClan().isWarring(vcp.getClan()) && !vcp.getClan().isWarring(acp.getClan())) {
                                plugin.getStorageManager().addStrife(acp.getClan(), vcp.getClan(), 1);
                                if (plugin.getStorageManager().retrieveStrifes(acp.getClan(), vcp.getClan()) >= strifemax) {
                                    acp.getClan().addWarringClan(vcp.getClan());
                                    vcp.getClan().addWarringClan(acp.getClan());
                                    acp.getClan().addBb(acp.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("you.are.at.war"), Helper.capitalize(acp.getClan().getName()), vcp.getClan().getColorTag()));
                                    vcp.getClan().addBb(vcp.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("you.are.at.war"), Helper.capitalize(vcp.getClan().getName()), acp.getClan().getColorTag()));
                                    plugin.getStorageManager().addStrife(acp.getClan(), vcp.getClan(), -strifemax);
                                }
                            }
                        }
                    }

                    // record attacker kill

                    // if victim doesn't have a clan or attacker doesn't have a clan, then the kill is civilian
                    // if both have verified clans, check for rival or default to neutral

                    double reward = 0;
                    double multipier = plugin.getSettingsManager().getKDRMultipliesPerKill();
                    float kdr = acp.getKDR();

                    if (vcp.getClan() == null || acp.getClan() == null || !vcp.getClan().isVerified() || !acp.getClan().isVerified()) {
                        acp.addCivilianKill();
                        plugin.getStorageManager().insertKill(attacker, acp.getTag(), victim, "", "c");
                    } else if (acp.getClan().isRival(vcp.getTag())) {

                        if (acp.getClan().isWarring(vcp.getClan())) {
                            reward = (double) kdr * multipier * 4;
                        } else {
                            reward = (double) kdr * multipier * 2;
                        }

                        acp.addRivalKill();
                        plugin.getStorageManager().insertKill(attacker, acp.getTag(), victim, vcp.getTag(), "r");
                    } else if (acp.getClan().isAlly(vcp.getTag())) {

                        reward = (double) kdr * multipier * -1;
                        acp.addNeutralKill();

                        plugin.getStorageManager().insertKill(attacker, acp.getTag(), victim, vcp.getTag(), "n");
                    } else {
                        reward = (double) kdr * multipier;
                        acp.addNeutralKill();
                        plugin.getStorageManager().insertKill(attacker, acp.getTag(), victim, vcp.getTag(), "n");
                    }

                    if (reward != 0 && plugin.getSettingsManager().isMoneyPerKill()) {
                        for (ClanPlayer cp : acp.getClan().getOnlineMembers()) {
                            double money = Math.round((reward / acp.getClan().getOnlineMembers().size()) * 100D) / 100D;
                            cp.toPlayer().sendMessage(ChatColor.AQUA + MessageFormat.format(plugin.getLang("player.got.money"), money, victim.getName(), kdr));
                            plugin.getPermissionsManager().playerGrantMoney(cp.getName(), money);
                        }
                    }

                    //adds power
                    double maxpower = plugin.getSettingsManager().getPowerPlusPerKill();
                    double max = plugin.getSettingsManager().getMaxPower();
                    if ((acp.getPower() + maxpower) > max) {
                        acp.setPower(max);
                    } else {
                        acp.addPower(maxpower);
                    }

                    // record death for victim
                    vcp.addDeath();

                    plugin.getStorageManager().updateClanPlayer(acp);
                }
                plugin.getStorageManager().updateClanPlayer(vcp);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEntityEvent event)
    {
        if (event.isCancelled()) {
            return;
        }

        if (plugin.getSettingsManager().isTamableMobsSharing()) {
            if (event.getRightClicked() instanceof Tameable) {

                Entity entity = event.getRightClicked();
                Player player = event.getPlayer();
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
                Tameable tamed = (Tameable) entity;

                if (tamed.isTamed() && ((Wolf) entity).isSitting()) {
                    if (cp.getClan().isMember((Player) tamed.getOwner())) {
                        tamed.setOwner(player);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityTarget(EntityTargetLivingEntityEvent event)
    {
        if (plugin.getSettingsManager().isTamableMobsSharing()) {
            if (event.getEntity() instanceof Wolf && event.getTarget() instanceof Player) {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer((Player) event.getTarget());
                Tameable wolf = (Tameable) event.getEntity();
                if (wolf.isTamed()) {
                    if (cp.getClan().isMember((Player) wolf.getOwner())) {
                        // cancels the event if the attacker is one out of his clan
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event)
    {
        Player attacker = null;
        Player victim = null;


        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent) event;

            if (sub.getEntity() instanceof Player && sub.getDamager() instanceof Player) {
                attacker = (Player) sub.getDamager();
                victim = (Player) sub.getEntity();
            }

            if (plugin.getSettingsManager().isTamableMobsSharing()) {
                if (sub.getEntity() instanceof Wolf && sub.getDamager() instanceof Player) {
                    attacker = (Player) sub.getDamager();
                    Wolf wolf = (Wolf) sub.getEntity();
                    ClanPlayer cp = plugin.getClanManager().getClanPlayer(attacker);
                    if (wolf.isTamed()) {
                        if (cp.getClan().isMember((Player) wolf.getOwner())) {
                            // Sets the wolf to friendly if the attacker is one out of his clan
                            wolf.setAngry(false);
                        }
                    }
                }
            }

            if (sub.getEntity() instanceof Player && sub.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) sub.getDamager();

                if (arrow.getShooter() instanceof Player) {
                    attacker = (Player) arrow.getShooter();
                    victim = (Player) sub.getEntity();
                }
            }
        }

        if (victim != null) {
            if (plugin.getSettingsManager().isBlacklistedWorld(victim.getLocation().getWorld().getName())) {
                return;
            }
        }

        if (attacker != null && victim != null) {
            ClanPlayer acp = plugin.getClanManager().getClanPlayer(attacker);
            ClanPlayer vcp = plugin.getClanManager().getClanPlayer(victim);


            Clan vclan = vcp == null ? null : vcp.getClan();
            Clan aclan = acp == null ? null : acp.getClan();


            if (plugin.getSettingsManager().isPvpOnlywhileInWar()) {
                // if one doesn't have clan then they cant be at war

                if (aclan == null || vclan == null) {
                    event.setCancelled(true);
                    return;
                }

                if (plugin.getPermissionsManager().has(victim, "simpleclans.mod.nopvpinwar")
                        && attacker != null && victim != null) {
                    event.setCancelled(true);
                    return;
                }

                // if not warring no pvp

                if (!aclan.isWarring(vclan)) {
                    event.setCancelled(true);
                    return;
                }
            }

            if (vclan != null) {
                if (aclan != null) {
                    // personal ff enabled, allow damage

                    if (vcp.isFriendlyFire()) {
                        return;
                    }

                    // clan ff enabled, allow damage

                    if (vclan.isFriendlyFire()) {
                        return;
                    }

                    // same clan, deny damage

                    if (vclan.equals(aclan)) {
                        event.setCancelled(true);
                        return;
                    }

                    // ally clan, deny damage

                    if (vclan.isAlly(aclan.getTag())) {
                        event.setCancelled(true);
                    }
                } else {
                    // not part of a clan - check if safeCivilians is set
                    // ignore setting if he has a specific permissions
                    if (plugin.getSettingsManager().getSafeCivilians() && !plugin.getPermissionsManager().has(victim, "simpleclans.ignore-safe-civilians")) {
                        event.setCancelled(true);
                    }
                }
            } else {
                // not part of a clan - check if safeCivilians is set
                // ignore setting if he has a specific permissions
                if (plugin.getSettingsManager().getSafeCivilians() && !plugin.getPermissionsManager().has(victim, "simpleclans.ignore-safe-civilians")) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
