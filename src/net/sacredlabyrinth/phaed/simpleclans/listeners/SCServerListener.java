package net.sacredlabyrinth.phaed.simpleclans.listeners;

import net.sacredlabyrinth.phaed.register.payment.Methods;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

/**
 *
 * @author phaed
 */
public class SCServerListener extends ServerListener
{
    private final SimpleClans plugin;
    private Methods Methods;

    /**
     *
     */
    public SCServerListener()
    {
        plugin = SimpleClans.getInstance();
        Methods = new Methods();
    }

    /**
     *
     * @param event the event
     */
    @Override
    public void onPluginDisable(PluginDisableEvent event)
    {
        if (Methods != null && Methods.hasMethod())
        {
            Boolean check = Methods.checkDisabled(event.getPlugin());

            if (check)
            {
                plugin.setMethod(null);
            }
        }
    }

    /**
     *
     * @param event the events
     */
    @Override
    public void onPluginEnable(PluginEnableEvent event)
    {
        if (!Methods.hasMethod())
        {
            if (Methods.setMethod(plugin.getServer().getPluginManager()))
            {
                plugin.setMethod(Methods.getMethod());
                SimpleClans.log("Payment method: {0} v{1}", plugin.getMethod().getName(), plugin.getMethod().getVersion());
            }
        }
    }
}
