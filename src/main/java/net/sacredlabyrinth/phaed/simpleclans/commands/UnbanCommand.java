package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public class UnbanCommand extends GenericConsoleCommand
{

    private SimpleClans plugin;

    public UnbanCommand(SimpleClans plugin)
    {
        super("Unban");
        this.plugin = plugin;
        setArgumentRange(1, 1);
        setUsages(MessageFormat.format(plugin.getLang("usage.Command"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("Command.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (plugin.getPermissionsManager().has(sender, "simpleclans.mod.ban")) {
            return MessageFormat.format(plugin.getLang("0.ban.unban.player.1.ban.unban.a.player"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
        }
        return null;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args)
    {

        if (plugin.getPermissionsManager().has(sender, "simpleclans.mod.ban")) {
            String banned = args[0];

            if (plugin.getSettingsManager().isBanned(banned)) {
                Player pl = Helper.matchOnePlayer(banned);

                if (pl != null) {
                    ChatBlock.sendMessage(pl, ChatColor.AQUA + plugin.getLang("you.have.been.unbanned.from.clan.commands"));
                }

                plugin.getSettingsManager().removeBanned(banned);
                ChatBlock.sendMessage(sender, ChatColor.AQUA + plugin.getLang("player.removed.from.the.banned.list"));
            } else {
                ChatBlock.sendMessage(sender, ChatColor.RED + plugin.getLang("this.player.is.not.banned"));
            }
        } else {
            ChatBlock.sendMessage(sender, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
    }
}
