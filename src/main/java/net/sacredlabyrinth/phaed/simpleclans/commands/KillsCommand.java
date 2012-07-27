package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillsCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public KillsCommand(SimpleClans plugin)
    {
        super("Kills");
        this.plugin = plugin;
        setArgumentRange(0, 0);
        setUsages(MessageFormat.format(plugin.getLang("usage.kills"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("kills.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (cp != null) {
            if (cp.isTrusted() && cp.getClan().isVerified()) {
                String out = "";
                if (plugin.getPermissionsManager().has(sender, "simpleclans.member.kills")) {
                    out = MessageFormat.format(plugin.getLang("0.kills"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
                }
                if (plugin.getPermissionsManager().has(sender, "simpleclans.member.kills")) {
                    out += "\n   §b" + MessageFormat.format(plugin.getLang("0.killsplayer"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
                }
                return out.isEmpty() ? null : out;
            }
        }
        return null;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {
        String headColor = plugin.getSettingsManager().getPageHeadingsColor();
        String subColor = plugin.getSettingsManager().getPageSubTitleColor();

        if (plugin.getPermissionsManager().has(player, "simpleclans.member.kills")) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isVerified()) {
                    if (cp.isTrusted()) {
                        String polledPlayerName = player.getName();

                        if (args.length == 1) {
                            polledPlayerName = args[0];
                        }

                        ChatBlock chatBlock = new ChatBlock();

                        chatBlock.setFlexibility(true, false);
                        chatBlock.setAlignment("l", "c");

                        chatBlock.addRow("  " + headColor + plugin.getLang("victim"), plugin.getLang("killcount"));

                        HashMap<String, Integer> killsPerPlayerUnordered = plugin.getStorageManager().getKillsPerPlayer(polledPlayerName);

                        if (killsPerPlayerUnordered.isEmpty()) {
                            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("nokillsfound"));
                            return;
                        }

                        Map<String, Integer> killsPerPlayer = Helper.sortByValue(killsPerPlayerUnordered);

                        for (String playerName : killsPerPlayer.keySet()) {
                            int count = killsPerPlayer.get(playerName);

                            chatBlock.addRow("  " + playerName, ChatColor.AQUA + "" + count);
                        }

                        ChatBlock.saySingle(player, plugin.getSettingsManager().getPageClanNameColor() + Helper.capitalize(polledPlayerName) + subColor + " " + plugin.getLang("kills") + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
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
