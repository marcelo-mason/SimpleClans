package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.beta.GenericPlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author phaed
 */
public class FfCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public FfCommand(SimpleClans plugin)
    {
        super("FfCommand");
        this.plugin = plugin;
        setArgumentRange(1, 1);
        setUsages(String.format(plugin.getLang("usage.ff"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("ff.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (cp != null) {
            if (plugin.getPermissionsManager().has(sender, "simpleclans.member.ff")) {
                return MessageFormat.format(plugin.getLang("0.ff.allow.auto.1.toggle.personal.friendly.fire"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
            }
        }
        return null;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {

        if (plugin.getPermissionsManager().has(player, "simpleclans.member.ff")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                String action = args[0];

                if (action.equalsIgnoreCase(plugin.getLang("allow"))) {
                    cp.setFriendlyFire(true);
                    plugin.getStorageManager().updateClanPlayer(cp);
                    ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("personal.friendly.fire.is.set.to.allowed"));
                } else if (action.equalsIgnoreCase(plugin.getLang("auto"))) {
                    cp.setFriendlyFire(false);
                    plugin.getStorageManager().updateClanPlayer(cp);
                    ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("friendy.fire.is.now.managed.by.your.clan"));
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.0.ff.allow.auto"), plugin.getSettingsManager().getCommandClan()));
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
    }
}
