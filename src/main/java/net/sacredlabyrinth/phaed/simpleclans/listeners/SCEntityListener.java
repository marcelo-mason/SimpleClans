package net.sacredlabyrinth.phaed.simpleclans.listeners;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

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
            boolean updateVictim = false;

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
                if (plugin.getSettingsManager().isClaimingEnabled()) {
                    double minpower = plugin.getSettingsManager().getPowerLossPerDeath();
                    double min = plugin.getSettingsManager().getMinPower();
                    if ((vcp.getPower() - minpower) < min) {
                        vcp.setPower(min);
                    } else {
                        vcp.lossPower(minpower);
                    }
                    updateVictim = true;
                }

                if (attacker != null) {
                    ClanPlayer acp = plugin.getClanManager().getCreateClanPlayer(attacker.getName());
                    Clan acpC = acp.getClan();
                    Clan vcpC = vcp.getClan();

                    if (plugin.getSettingsManager().isAutoWar()) {
                        int strifemax = plugin.getSettingsManager().getStrifeLimit();
                        if (acpC != null && vcpC != null) {
                            if (plugin.getStorageManager().getStrifes(acpC, vcpC) % strifemax == 0) {
                                acpC.addWarringClan(vcpC);
                                vcpC.addWarringClan(acpC);
                                acpC.addBb(acp.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("you.are.at.war"), Helper.capitalize(acpC.getName()), vcpC.getColorTag()));
                                vcpC.addBb(vcp.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("you.are.at.war"), Helper.capitalize(vcpC.getName()), acpC.getColorTag()));
                            }
                        }
                    }

                    // record attacker kill

                    // if victim doesn't have a clan or attacker doesn't have a clan, then the kill is civilian
                    // if both have verified clans, check for rival or default to neutral

                    double reward = 0;
                    double multipier = plugin.getSettingsManager().getKDRMultipliesPerKill();
                    float kdr = acp.getKDR();

                    if (vcpC == null || acpC == null || !vcpC.isVerified() || !acpC.isVerified()) {
                        acp.addCivilianKill();
                        plugin.getStorageManager().insertKill(attacker, acp.getTag(), victim, "", "c", false);
                    } else if (acpC.isRival(vcp.getTag())) {
                        boolean war = false;
                        if (acpC.isWarring(vcpC)) {
                            war = true;
                            reward = (double) kdr * multipier * 4;
                        } else {
                            reward = (double) kdr * multipier * 2;
                        }

                        acp.addRivalKill();
                        plugin.getStorageManager().insertKill(attacker, acp.getTag(), victim, vcp.getTag(), "r", war);
                    } else if (acpC.isAlly(vcp.getTag())) {

                        reward = (double) kdr * multipier * -1;
                        acp.addNeutralKill();

                        plugin.getStorageManager().insertKill(attacker, acp.getTag(), victim, vcp.getTag(), "n", false);
                    } else {
                        reward = (double) kdr * multipier;
                        acp.addNeutralKill();
                        plugin.getStorageManager().insertKill(attacker, acp.getTag(), victim, vcp.getTag(), "n", false);
                    }

                    if (reward != 0 && plugin.getSettingsManager().isMoneyPerKill()) {
                        for (ClanPlayer cp : acpC.getOnlineMembers()) {
                            double money = Math.round((reward / acpC.getOnlineMembers().size()) * 100D) / 100D;
                            cp.toPlayer().sendMessage(ChatColor.AQUA + MessageFormat.format(plugin.getLang("player.got.money"), money, victim.getName(), kdr));
                            plugin.getPermissionsManager().playerGrantMoney(cp.getName(), money);
                        }
                    }

                    //adds power
                    if (plugin.getSettingsManager().isClaimingEnabled()) {
                        double maxpower = plugin.getSettingsManager().getPowerPlusPerKill();
                        double max = plugin.getSettingsManager().getMaxPower();
                        if ((acp.getPower() + maxpower) > max) {
                            acp.setPower(max);
                        } else {
                            acp.addPower(maxpower);
                        }
                    }

                    // record death for victim
                    vcp.addDeath();
                    updateVictim = true;

                    plugin.getStorageManager().updateClanPlayer(acp);
                }
                if (updateVictim) {
                    plugin.getStorageManager().updateClanPlayer(vcp);
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
                    //skip if globalff is on
                    if (plugin.getSettingsManager().isGlobalff() || vcp.isFriendlyFire()) {
                        return;
                    }

                    // clan ff enabled, allow damage

                    if (plugin.getSettingsManager().isGlobalff() || vclan.isFriendlyFire()) {
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
