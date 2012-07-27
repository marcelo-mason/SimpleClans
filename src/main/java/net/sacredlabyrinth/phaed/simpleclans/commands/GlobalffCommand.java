package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author phaed
 */
public class GlobalffCommand extends GenericConsoleCommand
{

    private SimpleClans plugin;

    public GlobalffCommand(SimpleClans plugin)
    {
        super("Globalff");
        this.plugin = plugin;
        setArgumentRange(1, 1);
        setUsages(MessageFormat.format(plugin.getLang("usage.globalff"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("globalff.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (plugin.getPermissionsManager().has(sender, "simpleclans.mod.globalff")) {
            return ChatColor.DARK_RED + MessageFormat.format(plugin.getLang("0.globalff.allow.auto.1.set.global.friendly.fire"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
        }
        return null;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args)
    {
        String action = args[0];

        if (action.equalsIgnoreCase(plugin.getLang("allow"))) {
            if (plugin.getSettingsManager().isGlobalff()) {
                sender.sendMessage(ChatColor.AQUA + plugin.getLang("global.friendly.fire.is.already.being.allowed"));
            } else {
                plugin.getSettingsManager().setGlobalff(true);
                sender.sendMessage(ChatColor.AQUA + plugin.getLang("global.friendly.fire.is.set.to.allowed"));
            }
        } else if (action.equalsIgnoreCase(plugin.getLang("auto"))) {
            if (!plugin.getSettingsManager().isGlobalff()) {
                sender.sendMessage(ChatColor.AQUA + plugin.getLang("global.friendy.fire.is.already.being.managed.by.each.clan"));
            } else {
                plugin.getSettingsManager().setGlobalff(false);
                sender.sendMessage(ChatColor.AQUA + plugin.getLang("global.friendy.fire.is.now.managed.by.each.clan"));
            }
        } else {
            sender.sendMessage(ChatColor.RED + MessageFormat.format(plugin.getLang("usage.globalff"), plugin.getSettingsManager().getCommandClan()));
        }
    }
}
