package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

/**
 * @author phaed
 */
public class VerifyCommand
{
    public VerifyCommand()
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

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
        Clan clan = cp == null ? null : cp.getClan();

        boolean isNonVerified = clan != null && !clan.isVerified();
        boolean isBuyer = isNonVerified && plugin.getSettingsManager().isRequireVerification() && plugin.getSettingsManager().isePurchaseVerification();

        if (isBuyer)
        {
            if (arg.length == 0)
            {
                if (plugin.getClanManager().purchaseVerification(player))
                {
                    clan.verifyClan();
                    clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang().getString("clan.0.has.been.verified"), clan.getName()));
                    ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang().getString("the.clan.has.been.verified"));
                }
            }
        }
        else if (plugin.getPermissionsManager().has(player, "simpleclans.mod.verify"))
        {
            if (arg.length == 1)
            {
                Clan cclan = plugin.getClanManager().getClan(arg[0]);

                if (cclan != null)
                {
                    if (!cclan.isVerified())
                    {
                        cclan.verifyClan();
                        cclan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang().getString("clan.0.has.been.verified"), cclan.getName()));
                        ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang().getString("the.clan.has.been.verified"));
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("the.clan.is.already.verified"));
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED +plugin.getLang().getString("the.clan.does.not.exist"));
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang().getString("usage.0.verify.tag"), plugin.getSettingsManager().getCommandClan()));
            }
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("insufficient.permissions"));
        }
    }
}
