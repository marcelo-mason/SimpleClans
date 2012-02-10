package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

/**
 *
 * @author phaed
 */
public class ModtagCommand
{
    public ModtagCommand()
    {
    }

    /**
     * Execute the command
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg)
    {
        SimpleClans plugin = SimpleClans.getInstance();

        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.modtag"))
        {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null)
            {
                Clan clan = cp.getClan();

                if (clan.isVerified())
                {
                    if (clan.isLeader(player))
                    {
                        if (arg.length == 1)
                        {
                            String newtag = arg[0];
                            String cleantag = Helper.cleanTag(newtag);

                            if (Helper.stripColors(newtag).length() <= plugin.getSettingsManager().getTagMaxLength())
                            {
                                if (!plugin.getSettingsManager().hasDisallowedColor(newtag))
                                {
                                    if (Helper.stripColors(newtag).matches("[0-9a-zA-Z]*"))
                                    {
                                        if (cleantag.equals(clan.getTag()))
                                        {
                                            clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang().getString("tag.changed.to.0"), Helper.parseColors(newtag)));
                                            clan.changeClanTag(newtag);
                                            plugin.getClanManager().updateDisplayName(player.getPlayer());
                                        }
                                        else
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("you.can.only.modify.the.color.and.case.of.the.tag"));
                                        }
                                    }
                                    else
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("your.clan.tag.can.only.contain.letters.numbers.and.color.codes"));
                                    }
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang().getString("your.tag.cannot.contain.the.following.colors"), plugin.getSettingsManager().getDisallowedColorString()));
                                }

                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang().getString("your.clan.tag.cannot.be.longer.than.characters"), plugin.getSettingsManager().getTagMaxLength()));
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang().getString("usage.0.modtag.tag"), plugin.getSettingsManager().getCommandClan()));
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("example.clan.modtag.4kfo.4l"));
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("no.leader.permissions"));
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("clan.is.not.verified"));
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("not.a.member.of.any.clan"));
            }
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("insufficient.permissions"));
        }
    }
}
