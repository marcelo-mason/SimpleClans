package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.beta.GenericPlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StrifesCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public StrifesCommand(SimpleClans plugin)
    {
        super("Command");
        this.plugin = plugin;
        setArgumentRange(0, 0);
        setUsages(MessageFormat.format(plugin.getLang("usage.strifes"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("strifes.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (cp != null) {
            if (plugin.getPermissionsManager().has(sender, "simpleclans.member.strifes")) {
                return MessageFormat.format(plugin.getLang("usage.menu.strifes"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
            }
        }
        return null;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {
        String headColor = plugin.getSettingsManager().getPageHeadingsColor();
        String subColor = plugin.getSettingsManager().getPageSubTitleColor();

        if (plugin.getPermissionsManager().has(player, "simpleclans.member.strifes")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isVerified()) {
                    if (cp.isTrusted()) {

                        ChatBlock chatBlock = new ChatBlock();

                        chatBlock.setFlexibility(true, false);
                        chatBlock.setAlignment("l", "c");

                        chatBlock.addRow("  " + headColor + plugin.getLang("clan"), plugin.getLang("strifes"));
                        Map<String, Integer> unordered = new HashMap<String, Integer>();

                        for (Clan clans : plugin.getClanManager().getClans()) {
                            int strifes = plugin.getStorageManager().getStrifes(clan, clans);
                            if (strifes != 0) {
                                unordered.put(clans.getTag(), strifes);
                            }
                        }

                        Map<String, Integer> ordered = Helper.sortByValue(unordered);

                        for (String clanTag : ordered.keySet()) {
                            chatBlock.addRow("  " + clanTag, ChatColor.AQUA + "" + ordered.get(clanTag));
                        }

                        ChatBlock.saySingle(player, plugin.getSettingsManager().getPageClanNameColor() + subColor + " " + plugin.getLang("strifes") + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                        ChatBlock.sendBlank(player);

                        boolean more = chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

                        if (more) {
                            plugin.getStorageManager().addChatBlock(player, chatBlock);
                            ChatBlock.sendBlank(player);
                            ChatBlock.sendMessage(player, headColor + MessageFormat.format(plugin.getLang("view.next.page"), plugin.getSettingsManager().getCommandMore()));
                        }

                        ChatBlock.sendBlank(player);


                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("only.trusted.players.can.access.clan.stats"));
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
