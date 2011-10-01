package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author phaed
 */
public class VitalsCommand
{
    public VitalsCommand()
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
        String headColor = plugin.getSettingsManager().getPageHeadingsColor();
        String subColor = plugin.getSettingsManager().getPageSubTitleColor();

        if (plugin.getPermissionsManager().has(player, "simpleclans.member.vitals"))
        {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null)
            {
                Clan clan = cp.getClan();

                if (clan.isVerified())
                {
                    if (cp.isTrusted())
                    {
                        if (arg.length == 0)
                        {
                            ChatBlock chatBlock = new ChatBlock();
                            ChatBlock.sendBlank(player);
                            ChatBlock.saySingle(player, plugin.getSettingsManager().getPageClanNameColor() + Helper.capitalize(clan.getName()) + subColor+ " " + plugin.getLang().getString("vitals") + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                            ChatBlock.sendBlank(player);
                            ChatBlock.sendMessage(player, headColor + plugin.getLang().getString("weapons") + ": " + MessageFormat.format(plugin.getLang().getString("0.s.sword.1.2.b.bow.3.4.a.arrow"), ChatColor.WHITE, ChatColor.DARK_GRAY, ChatColor.WHITE, ChatColor.DARK_GRAY, ChatColor.WHITE));
                            ChatBlock.sendMessage(player, headColor + plugin.getLang().getString("materials") + ": " + ChatColor.AQUA + plugin.getLang().getString("diamond") +  ChatColor.DARK_GRAY + ", " + ChatColor.YELLOW + plugin.getLang().getString("gold") + ChatColor.DARK_GRAY + ", " +ChatColor.GRAY + plugin.getLang().getString("silver") + ChatColor.DARK_GRAY + ", " + ChatColor.GOLD + plugin.getLang().getString("wood"));
                            ChatBlock.sendBlank(player);

                            chatBlock.setFlexibility(true, false, false, false, false, false);
                            chatBlock.setAlignment("l", "l", "l", "c", "c", "c");

                            chatBlock.addRow("  " + headColor + plugin.getLang().getString("name"), plugin.getLang().getString("health"), plugin.getLang().getString("hunger"), plugin.getLang().getString("food"), plugin.getLang().getString("armor"), plugin.getLang().getString("weapons"));

                            List<ClanPlayer> members = Helper.stripOffLinePlayers(clan.getLeaders());
                            members.addAll(Helper.stripOffLinePlayers(clan.getNonLeaders()));

                            for (ClanPlayer cpm : members)
                            {
                                Player p = plugin.getServer().getPlayer(cpm.getName());

                                if (p != null)
                                {
                                    String name = (cpm.isLeader() ? plugin.getSettingsManager().getPageLeaderColor() : ((cpm.isTrusted() ? plugin.getSettingsManager().getPageTrustedColor() : plugin.getSettingsManager().getPageUnTrustedColor()))) + cpm.getName();
                                    String health = plugin.getClanManager().getHealthString(p.getHealth());
                                    String hunger = plugin.getClanManager().getHungerString(p.getFoodLevel());
                                    String armor = plugin.getClanManager().getArmorString(p.getInventory());
                                    String weapons = plugin.getClanManager().getWeaponString(p.getInventory());
                                    String food = plugin.getClanManager().getFoodString(p.getInventory());

                                    chatBlock.addRow("  " + name, ChatColor.RED + health, hunger, ChatColor.WHITE + food, armor, weapons);
                                }
                            }

                            boolean more = chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

                            if (more)
                            {
                                plugin.getStorageManager().addChatBlock(player, chatBlock);
                                ChatBlock.sendBlank(player);
                                ChatBlock.sendMessage(player, headColor + MessageFormat.format(plugin.getLang().getString("view.next.page"), plugin.getSettingsManager().getCommandMore()));
                            }

                            ChatBlock.sendBlank(player);
                        }
                        else
                        {
                            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang().getString("usage.0.vitals"), plugin.getSettingsManager().getCommandClan()));
                        }
                    }
                    else
                    {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("only.trusted.players.can.access.clan.vitals"));
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
