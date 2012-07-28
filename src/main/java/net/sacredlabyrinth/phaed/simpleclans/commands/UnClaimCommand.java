package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.api.events.SimpleClansChunkUnclaimEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public class UnClaimCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public UnClaimCommand(SimpleClans plugin)
    {
        super("UnClaim");
        this.plugin = plugin;
        setArgumentRange(0, 0);
        setUsages(MessageFormat.format(plugin.getLang("usage.unclaim"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("unclaim.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (plugin.getPermissionsManager().has(sender, "simpleclans.admin.claim.unclaim")) {
            return MessageFormat.format(plugin.getLang("usage.menu.unclaim"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
        }
        return null;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {

        if (plugin.getPermissionsManager().has(player, "simpleclans.admin.claim.unclaim")) {
            ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(player.getName());
            Location loc = player.getLocation();

            ChunkLocation chunk = new ChunkLocation(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockZ(), true);
            Clan clan = plugin.getClanManager().getClanAt(chunk);
            if (clan != null) {
                switch (clan.unclaim(chunk)) {
                    case FAILED_HOMEBLOCK:
                        player.sendMessage(ChatColor.DARK_RED + plugin.getLang("remove.homeblock"));
                        break;
                    case NO_CLAIM:
                        player.sendMessage(ChatColor.DARK_RED + plugin.getLang("error.no.claim"));
                        break;
                    case SUCCESS:
                        clan.removeClaimedChunk(chunk);
                        plugin.getServer().getPluginManager().callEvent(new SimpleClansChunkUnclaimEvent(cp, clan, chunk));
                        player.sendMessage(ChatColor.DARK_GRAY + plugin.getLang("claim.removed"));
                        break;
                }
            } else {
                player.sendMessage(ChatColor.DARK_RED + plugin.getLang("error.no.claim"));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
    }
}
