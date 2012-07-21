package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author phaed
 */
public class ResetCommand
{

    public ResetCommand()
    {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg)
    {
        SimpleClans plugin = SimpleClans.getInstance();

        if (arg.length == 1) {
            if (arg[0].equalsIgnoreCase("allkdr")) {
                if (plugin.getPermissionsManager().has(player, "simpleclans.admin.resetkdr")) {
                    plugin.getClanManager().resetKDRs();
                    player.sendMessage(ChatColor.GRAY + plugin.getLang("allkdr.reset"));
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            }
        } else if (arg.length == 2) {
            if (arg[0].equalsIgnoreCase("kdr")) {
                if (plugin.getPermissionsManager().has(player, "simpleclans.admin.resetkdr")) {
                    Player otherPlayer = plugin.getServer().getPlayer(arg[1]);
                    ClanPlayer cp;

                    if (otherPlayer != null) {
                        cp = plugin.getClanManager().getAnyClanPlayer(otherPlayer.getName());
                    } else {
                        cp = plugin.getClanManager().getAnyClanPlayer(arg[1]);
                    }

                    if (cp != null) {
                        plugin.getClanManager().resetKDR(cp);
                        player.sendMessage(ChatColor.GRAY + MessageFormat.format(plugin.getLang("dr.reset"), cp.getName()));
                    } else {
                        player.sendMessage(ChatColor.DARK_RED + plugin.getLang("clanplayer.not.exist"));
                    }
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.0.verify.tag"), plugin.getSettingsManager().getCommandClan()));
        }
    }
}
