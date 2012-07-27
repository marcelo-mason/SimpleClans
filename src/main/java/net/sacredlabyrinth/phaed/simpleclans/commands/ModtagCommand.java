package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public class ModtagCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public ModtagCommand(SimpleClans plugin)
    {
        super("Modtag");
        this.plugin = plugin;
        setArgumentRange(1, 1);
        setUsages(MessageFormat.format(plugin.getLang("usage.modtag"), plugin.getSettingsManager().getCommandClan()), ChatColor.RED + plugin.getLang("example.clan.modtag.4kfo.4l"));
        setIdentifiers(plugin.getLang("modtag.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (cp != null) {
            if (cp.isLeader() && cp.getClan().isVerified() && plugin.getPermissionsManager().has(sender, "simpleclans.leader.modtag")) {
                return MessageFormat.format(plugin.getLang("0.modtag.tag.1.modify.the.clan.s.tag"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
            }
        }
        return null;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {

        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.modtag")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isVerified()) {
                    if (clan.isLeader(player)) {

                        String newtag = args[0];
                        String cleantag = Helper.cleanTag(newtag);

                        if (Helper.stripColors(newtag).length() <= plugin.getSettingsManager().getTagMaxLength()) {
                            if (!plugin.getSettingsManager().hasDisallowedColor(newtag)) {
                                if (Helper.stripColors(newtag).matches("[0-9a-zA-Z]*")) {
                                    if (cleantag.equals(clan.getTag())) {
                                        clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("tag.changed.to.0"), Helper.parseColors(newtag)));
                                        clan.changeClanTag(newtag);
                                        plugin.getClanManager().updateDisplayName(player.getPlayer());
                                    } else {
                                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("you.can.only.modify.the.color.and.case.of.the.tag"));
                                    }
                                } else {
                                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("your.clan.tag.can.only.contain.letters.numbers.and.color.codes"));
                                }
                            } else {
                                ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("your.tag.cannot.contain.the.following.colors"), plugin.getSettingsManager().getDisallowedColorString()));
                            }

                        } else {
                            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("your.clan.tag.cannot.be.longer.than.characters"), plugin.getSettingsManager().getTagMaxLength()));
                        }

                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
                    }
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clan.is.not.verified"));
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
    }
}
