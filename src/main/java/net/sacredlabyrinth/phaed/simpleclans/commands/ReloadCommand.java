package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.io.FileNotFoundException;
import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author phaed
 */
public class ReloadCommand extends GenericConsoleCommand
{

    private SimpleClans plugin;

    public ReloadCommand(SimpleClans plugin)
    {
        super("Reload");
        this.plugin = plugin;
        setArgumentRange(0, 0);
        setUsages(MessageFormat.format(plugin.getLang("usage.reload"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("reload.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (plugin.getPermissionsManager().has(sender, "simpleclans.admin.reload")) {
            return ChatColor.DARK_RED + MessageFormat.format(plugin.getLang("0.reload.1.reload.configuration"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
        }
        return null;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args)
    {
        if (sender.hasPermission("simpleclans.admin.reload")) {
            long start = System.currentTimeMillis();
            plugin.getSettingsManager().reload();

            try {
                plugin.getAutoUpdater().resetConfig();
            } catch (FileNotFoundException ex) {
                SimpleClans.debug(null, ex);
            }

            plugin.getStorageManager().importFromDatabase();
            for (Clan clan : plugin.getClanManager().getClans()) {
                plugin.getPermissionsManager().updateClanPermissions(clan);
            }
            long end = System.currentTimeMillis();
            sender.sendMessage(ChatColor.AQUA + MessageFormat.format(plugin.getLang("configuration.reloaded"), end - start));
        } else {
            sender.sendMessage(ChatColor.RED + "Think you're slick don't ya");
        }
    }
}
