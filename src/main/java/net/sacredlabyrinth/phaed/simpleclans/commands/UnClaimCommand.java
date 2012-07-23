package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.api.events.SimpleClansChunkUnclaimEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public class UnClaimCommand
{

    public UnClaimCommand()
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

        if (arg.length == 0) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.admin.claim.unclaim")) {
                ClanPlayer cp = plugin.getClanManager().getCreateClanPlayer(player.getName());
                Location loc = player.getLocation();

                ChunkLocation chunk = new ChunkLocation(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockZ(), true);
                Clan clan = plugin.getClanManager().getClanAt(chunk);
                if (clan != null) {
                    switch (clan.unclaim(chunk)) {
                        case FAILED_HOMEBLOCK:
                            player.sendMessage(ChatColor.DARK_RED + plugin.getLang("remove.homeblock"));
                        case FAILED_NEAR:
                            player.sendMessage(ChatColor.DARK_RED + "not near");
                        case NO_CLAIM:
                            player.sendMessage(ChatColor.DARK_RED + plugin.getLang("error.no.claim"));
                        case SUCCESS:
                            clan.removeClaimedChunk(chunk);
                            plugin.getServer().getPluginManager().callEvent(new SimpleClansChunkUnclaimEvent(cp, clan, chunk));
                            player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("claim.removed"));
                            break;
                    }
                } else {
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            }
        } else {
            player.sendMessage(ChatColor.RED + MessageFormat.format(plugin.getLang("usage.unclaim"), plugin.getSettingsManager().getCommandClan()));
        }
    }
}
