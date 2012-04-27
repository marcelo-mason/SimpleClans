package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author phaed
 */
public class ToggleCommand
{
    public ToggleCommand()
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

        if (arg.length == 0)
        {
            return;
        }

        String cmd = arg[0];

        if (cmd.equalsIgnoreCase("cape"))
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.cape-toggle"))
            {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                if (cp != null)
                {
                    Clan clan = cp.getClan();

                    if (clan.isVerified())
                    {
                        if (cp.isCapeEnabled())
                        {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("capeoff"));
                            cp.setCapeEnabled(false);
                            plugin.getSpoutPluginManager().clearCape(player);
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("capeon"));
                            cp.setCapeEnabled(true);
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("clan.is.not.verified"));
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            }
        }

        if (cmd.equalsIgnoreCase("bb"))
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.bb-toggle"))
            {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                if (cp != null)
                {
                    Clan clan = cp.getClan();

                    if (clan.isVerified())
                    {
                        if (cp.isBbEnabled())
                        {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("bboff"));
                            cp.setBbEnabled(false);
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("bbon"));
                            cp.setBbEnabled(true);
                        }
                        plugin.getStorageManager().updateClanPlayer(cp);
                    }
                }
            }
        }

        if (cmd.equalsIgnoreCase("tag"))
        {
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.tag-toggle"))
            {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                if (cp != null)
                {
                    Clan clan = cp.getClan();

                    if (clan.isVerified())
                    {
                        if (cp.isTagEnabled())
                        {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("tagoff"));
                            cp.setTagEnabled(false);
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("tagon"));
                            cp.setTagEnabled(true);
                        }
                        plugin.getStorageManager().updateClanPlayer(cp);
                    }
                }
            }
        }
    }
}
