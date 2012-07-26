package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.beta.GenericConsoleCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author phaed
 */
public class VerifyModCommand extends GenericConsoleCommand
{

    private SimpleClans plugin;

    public VerifyModCommand(SimpleClans plugin)
    {
        super("VerifyMod");
        this.plugin = plugin;
        setArgumentRange(1, 1);
        setUsages(MessageFormat.format(plugin.getLang("usage.verifyclan"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("verifyclan.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (plugin.getPermissionsManager().has(sender, "simpleclans.mod.verify") && plugin.getSettingsManager().isRequireVerification()) {
            return ChatColor.DARK_RED + MessageFormat.format(plugin.getLang("usage.menu.verifyclan"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
        }
        return null;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args)
    {

        if (plugin.getPermissionsManager().has(sender, "simpleclans.mod.verify")) {

            Clan cclan = plugin.getClanManager().getClan(args[0]);

            if (cclan != null) {
                if (!cclan.isVerified()) {
                    cclan.verifyClan();
                    cclan.addBb(sender.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("clan.0.has.been.verified"), cclan.getName()));
                    ChatBlock.sendMessage(sender, ChatColor.AQUA + plugin.getLang("the.clan.has.been.verified"));
                } else {
                    ChatBlock.sendMessage(sender, ChatColor.RED + plugin.getLang("the.clan.is.already.verified"));
                }
            } else {
                ChatBlock.sendMessage(sender, ChatColor.RED + plugin.getLang("the.clan.does.not.exist"));
            }

        } else {
            ChatBlock.sendMessage(sender, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
    }
}
