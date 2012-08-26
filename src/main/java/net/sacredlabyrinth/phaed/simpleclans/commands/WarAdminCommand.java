package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author phaed
 */
public class WarAdminCommand extends GenericConsoleCommand {

    private SimpleClans plugin;

    public WarAdminCommand(SimpleClans plugin)
    {
        super("WarAdmin");
        this.plugin = plugin;
        setArgumentRange(3, 3);
        setUsages(MessageFormat.format(plugin.getLang("usage.waradmin"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("waradmin.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (sender.hasPermission("simpleclans.admin.warcontrol")) {
            return ChatColor.DARK_RED + MessageFormat.format(plugin.getLang("waradmin"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
        }
        return null;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args)
    {

        if (plugin.getPermissionsManager().has(sender, "simpleclans.admin.warcontrol")) {

            Clan clan1 = plugin.getClanManager().getClan(args[1]);
            Clan clan2 = plugin.getClanManager().getClan(args[2]);

            if (clan1 == null || clan2 == null) {
                ChatBlock.sendMessage(sender, ChatColor.RED + plugin.getLang("no.clan.matched"));
                return;
            }

            if (args[0].equalsIgnoreCase("cancel")) {

                boolean clan1War = clan1.isWarring(clan2);
                boolean clan2War = clan2.isWarring(clan1);

                if (clan1War || clan1War) {
                    sender.sendMessage("The war was cancelled!");

                    if (clan1War) {
                        clan1.removeWarringClan(clan2);
                    }

                    if (clan2War) {
                        clan2.removeWarringClan(clan1);
                    }

                } else {
                    sender.sendMessage("The clans are not in war!");
                }
            }

        }
    }
}