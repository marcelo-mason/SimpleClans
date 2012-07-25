package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.beta.GenericPlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MostKilledCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public MostKilledCommand(SimpleClans plugin)
    {
        super("MostKilled");
        this.plugin = plugin;
        setArgumentRange(0, 0);
        setUsages(String.format(plugin.getLang("usage.mostkilled"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("mostkilled.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (cp != null) {
            if (cp.isTrusted() && cp.getClan().isVerified() && plugin.getPermissionsManager().has(sender, "simpleclans.mod.mostkilled")) {
                return MessageFormat.format(plugin.getLang("0.mostkilled"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
            }
        }
        return null;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {
        String headColor = plugin.getSettingsManager().getPageHeadingsColor();
        String subColor = plugin.getSettingsManager().getPageSubTitleColor();

        if (plugin.getPermissionsManager().has(player, "simpleclans.mod.mostkilled")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isVerified()) {
                    if (cp.isTrusted()) {
                        ChatBlock chatBlock = new ChatBlock();

                        chatBlock.setFlexibility(true, false, false);
                        chatBlock.setAlignment("l", "c", "l");

                        chatBlock.addRow("  " + headColor + plugin.getLang("victim"), headColor + plugin.getLang("killcount"), headColor + plugin.getLang("attacker"));

                        HashMap<String, Integer> killsPerPlayerUnordered = plugin.getStorageManager().getMostKilled();

                        if (killsPerPlayerUnordered.isEmpty()) {
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("nokillsfound"));
                            return;
                        }

                        Map<String, Integer> killsPerPlayer = Helper.sortByValue(killsPerPlayerUnordered);

                        for (String attackerVictim : killsPerPlayer.keySet()) {
                            String[] split = attackerVictim.split(" ");

                            if (split.length < 2) {
                                continue;
                            }

                            int count = killsPerPlayer.get(attackerVictim);
                            String attacker = split[0];
                            String victim = split[1];

                            chatBlock.addRow("  " + ChatColor.WHITE + victim, ChatColor.AQUA + "" + count, ChatColor.YELLOW + attacker);
                        }

                        ChatBlock.saySingle(player, plugin.getSettingsManager().getServerName() + subColor + " " + plugin.getLang("mostkilled") + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
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
