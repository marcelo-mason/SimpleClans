package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author phaed
 */
public class CreateCommand
{
    public CreateCommand()
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

        if (plugin.getPermissionsManager().has(player, "simpleclans.leader.create"))
        {
            if (arg.length >= 2)
            {
                String tag = arg[0];
                String cleanTag = Helper.cleanTag(arg[0]);

                String name = Helper.toMessage(Helper.removeFirst(arg));

                if (cleanTag.length() <= plugin.getSettingsManager().getTagMaxLength())
                {
                    if (cleanTag.length() > plugin.getSettingsManager().getTagMinLength())
                    {
                        if (!plugin.getSettingsManager().hasDisallowedColor(tag))
                        {
                            if (Helper.stripColors(name).length() <= plugin.getSettingsManager().getClanMaxLength())
                            {
                                if (Helper.stripColors(name).length() > plugin.getSettingsManager().getClanMinLength())
                                {
                                    if (cleanTag.matches("[0-9a-zA-Z]*"))
                                    {
                                        if (!name.contains("&"))
                                        {
                                            if (!plugin.getSettingsManager().isDisallowedWord(cleanTag.toLowerCase()))
                                            {
                                                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                                                if (cp == null)
                                                {
                                                    if (!plugin.getClanManager().isClan(cleanTag))
                                                    {
                                                        if (plugin.getClanManager().purchaseCreation(player))
                                                        {
                                                            plugin.getClanManager().createClan(player, tag, name);

                                                            Clan clan = plugin.getClanManager().getClan(tag);
                                                            clan.addBb(player.getName(), ChatColor.AQUA + "Clan " + name + " created");
                                                            plugin.getStorageManager().updateClan(clan);

                                                            if (plugin.getSettingsManager().isRequireVerification())
                                                            {
                                                                boolean verified = !plugin.getSettingsManager().isRequireVerification() || plugin.getPermissionsManager().has(player, "simpleclans.mod.verify");

                                                                if (!verified)
                                                                {
                                                                    ChatBlock.sendMessage(player, ChatColor.AQUA + "Get your clan verified to access advanced features.");
                                                                }
                                                            }
                                                        }
                                                    }
                                                    else
                                                    {
                                                        ChatBlock.sendMessage(player, ChatColor.RED + "A clan with this tag already exists");
                                                    }
                                                }
                                                else
                                                {
                                                    ChatBlock.sendMessage(player, ChatColor.RED + "You must first resign from " + cp.getClan().getName());
                                                }
                                            }
                                            else
                                            {
                                                ChatBlock.sendMessage(player, ChatColor.RED + "That tag name is disallowed");
                                            }
                                        }
                                        else
                                        {
                                            ChatBlock.sendMessage(player, ChatColor.RED + "Your clan's name cannot contain color codes");
                                        }
                                    }
                                    else
                                    {
                                        ChatBlock.sendMessage(player, ChatColor.RED + "Your clan's tag can only contain letters, numbers, and color codes");
                                    }
                                }
                                else
                                {
                                    ChatBlock.sendMessage(player, ChatColor.RED + "Your clan's name must be longer than " + plugin.getSettingsManager().getClanMinLength() + " characters");
                                }
                            }
                            else
                            {
                                ChatBlock.sendMessage(player, ChatColor.RED + "Your clan's name cannot be longer than " + plugin.getSettingsManager().getClanMaxLength() + " characters");
                            }
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + "Your tag cannot contain the following colors: " + plugin.getSettingsManager().getDisallowedColorString());
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + "Your clan's tag must be longer than " + plugin.getSettingsManager().getTagMinLength() + " characters");
                    }
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "Your clan's tag cannot be longer than " + plugin.getSettingsManager().getTagMaxLength() + " characters");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /clan create [tag] [name]");
                ChatBlock.sendMessage(player, ChatColor.RED + "Example: /clan create &4Kol Knights of the Labyrinth");
            }
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
        }
    }
}
