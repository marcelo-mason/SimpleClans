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
public class BanCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public BanCommand(SimpleClans plugin)
    {
        super("Ban");
        this.plugin = plugin;
        setArgumentRange(1, 1);
        setUsages(MessageFormat.format(plugin.getLang("usage.ban"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE));
        setIdentifiers(plugin.getLang("ban.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (plugin.getPermissionsManager().has(sender, "simpleclans.mod.ban")) {
            return ChatColor.DARK_RED + MessageFormat.format(plugin.getLang("0.ban.unban.player.1.ban.unban.a.player"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
        }
        return null;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {

        if (plugin.getPermissionsManager().has(player, "simpleclans.mod.ban")) {

            String banned = args[0];

            if (!plugin.getSettingsManager().isBanned(banned)) {
                Player pl = Helper.matchOnePlayer(banned);

                if (pl != null) {
                    ChatBlock.sendMessage(pl, ChatColor.AQUA + plugin.getLang("you.banned"));
                }

                plugin.getClanManager().ban(banned);
                ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("player.added.to.banned.list"));
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("this.player.is.already.banned"));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
    }
}
