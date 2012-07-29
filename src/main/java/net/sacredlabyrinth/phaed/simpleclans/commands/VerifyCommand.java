package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author phaed
 */
public class VerifyCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public VerifyCommand(SimpleClans plugin)
    {
        super("Verify");
        this.plugin = plugin;
        setArgumentRange(0, 0);
        setUsages(MessageFormat.format(plugin.getLang("usage.verify"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("verify.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (cp != null) {
            if (!cp.getClan().isVerified() && plugin.getSettingsManager().isRequireVerification() && plugin.getSettingsManager().isePurchaseVerification()) {
                return MessageFormat.format(plugin.getLang("0.verify.1.purchase.verification.of.your.clan"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
            }
        }
        return null;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
        Clan clan = cp == null ? null : cp.getClan();

        boolean isNonVerified = clan != null && !clan.isVerified();
        boolean isBuy = plugin.getSettingsManager().isePurchaseVerification();

        if (plugin.getSettingsManager().isRequireVerification()) {
            if (isNonVerified) {
                if (isBuy) {
                    if (!plugin.getClanManager().purchaseVerification(player)) {
                        player.sendMessage(ChatColor.AQUA + plugin.getLang("not.sufficient.money"));
                        return;
                    }
                }
                clan.verifyClan();
                clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("clan.0.has.been.verified"), clan.getName()));
                ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("the.clan.has.been.verified"));
            } else {
                player.sendMessage(ChatColor.GRAY + plugin.getLang("your.clan.is.already.verified"));
            }
        } else {
            player.sendMessage(ChatColor.GRAY + plugin.getLang("you.dont.need.to.verify"));
        }
    }
}
