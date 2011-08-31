package net.sacredlabyrinth.phaed.simpleclans.managers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import net.sacredlabyrinth.phaed.simpleclans.DamageRecord;
import net.sacredlabyrinth.phaed.simpleclans.Dates;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

/**
 *
 * @author phaed
 */
public final class DeathManager
{
    private SimpleClans plugin;
    private HashMap<String, DamageRecord> damages = new HashMap<String, DamageRecord>(); // victim, Record

    /**
     *
     */
    public DeathManager()
    {
        plugin = SimpleClans.getInstance();
        cleanupTask();
    }

    /**
     *
     * @param victim
     * @param attacker
     */
    public void addDamager(String victim, String attacker)
    {
        damages.put(victim, new DamageRecord(victim, attacker));
    }

    /**
     *
     * @param victim
     * @return
     */
    public String pollLastAttacker(String victim)
    {
        if (damages.containsKey(victim))
        {
            String attacker = damages.get(victim).getAttacker();
            damages.remove(victim);
            return attacker;
        }

        return null;
    }

    /**
     * Clean up aged combat tags
     */
    public void cleanupTask()
    {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
        {
            public void run()
            {
                List<String> old = new ArrayList<String>();

                for (String victim : damages.keySet())
                {
                    DamageRecord rec = damages.get(victim);

                    Date now = new Date();

                    if (Dates.differenceInSeconds(now, rec.getTime()) >= plugin.getSettingsManager().getCombatTagSeconds())
                    {
                        old.add(victim);
                    }
                }

                for (String v : old)
                {
                    damages.remove(v);
                }
            }
        }, 0, 20L * 5);
    }
}
