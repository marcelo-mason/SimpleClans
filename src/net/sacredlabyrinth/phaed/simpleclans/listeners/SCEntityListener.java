package net.sacredlabyrinth.phaed.simpleclans.listeners;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

/**
 *
 * @author phaed
 */
public class SCEntityListener extends EntityListener
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
     *
     * @param event
     */
    @Override
    public void onEntityDeath(EntityDeathEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            Player victim = (Player) event.getEntity();

            String attackerName = plugin.getDeathManager().pollLastAttacker(victim.getName());

            if (attackerName != null)
            {
                Player attacker = plugin.getServer().getPlayer(attackerName);

                if (attacker != null)
                {
                    ClanPlayer acp = plugin.getClanManager().getClanPlayer(attacker);
                    ClanPlayer vcp = plugin.getClanManager().getClanPlayer(victim);

                    // record kill for attacker

                    if (acp != null && acp.getClan().isVerified())
                    {
                        if (vcp == null || !acp.getClan().isVerified())
                        {
                            acp.addCivilianKill();
                        }
                        else
                        {
                            if (acp.getClan().isRival(vcp.getClan().getTag()))
                            {
                                acp.addRivalKill();
                                plugin.getClanManager().serverAnnounce(acp.getClan().getColorTag() + ChatColor.AQUA + acp.getName() + " killed rival " + vcp.getClan().getColorTag() + ChatColor.AQUA + plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketRight() + " " + plugin.getSettingsManager().getClanChatNameColor() + plugin.getSettingsManager().getClanChatPlayerBracketLeft() + vcp.getName() + plugin.getSettingsManager().getClanChatPlayerBracketRight());
                            }
                            else if (acp.getClan().isAlly(vcp.getClan().getTag()))
                            {
                                // do not reacord ally kills
                            }
                            else if (acp.getClan().equals(vcp.getClan()))
                            {
                                // do not record same clan kills
                            }
                            else
                            {
                                acp.addNeutralKill();
                                plugin.getClanManager().serverAnnounce(acp.getClan().getColorTag() + ChatColor.AQUA + acp.getName() + " killed " + vcp.getClan().getColorTag() + ChatColor.AQUA + plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketRight() + " " + plugin.getSettingsManager().getClanChatNameColor() + plugin.getSettingsManager().getClanChatPlayerBracketLeft() + vcp.getName() + plugin.getSettingsManager().getClanChatPlayerBracketRight());
                            }
                        }

                        plugin.getStorageManager().updateClanPlayer(acp);
                    }

                    // record death for victim

                    if (vcp != null && vcp.getClan().isVerified())
                    {
                        vcp.addDeath();
                        plugin.getStorageManager().updateClanPlayer(vcp);
                    }
                }
            }
        }
    }

    /**
     *
     * @param event
     */
    @Override
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

        if (attacker != null && victim != null)
        {
            ClanPlayer acp = plugin.getClanManager().getClanPlayer(attacker);
            ClanPlayer vcp = plugin.getClanManager().getClanPlayer(victim);

            Clan vclan = vcp == null ? null : vcp.getClan();
            Clan aclan = acp == null ? null : acp.getClan();

            if (vclan != null)
            {
                if (aclan != null)
                {
                    if (vcp != null)
                    {
                        // personal ff enabled, allow damage

                        if (vcp.isFriendlyFire())
                        {
                            return;
                        }
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
                        return;
                    }
                }
            }

            plugin.getDeathManager().addDamager(victim.getName(), attacker.getName());
        }
    }
}
