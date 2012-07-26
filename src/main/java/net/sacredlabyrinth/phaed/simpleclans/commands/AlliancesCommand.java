package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import java.util.List;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.beta.GenericConsoleCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author phaed
 */
public class AlliancesCommand extends GenericConsoleCommand
{

    private SimpleClans plugin;

    public AlliancesCommand(SimpleClans plugin)
    {
        super("Alliances");
        this.plugin = plugin;
        setArgumentRange(0, 0);
        setUsages(MessageFormat.format(plugin.getLang("usage.alliances"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("alliances.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (plugin.getPermissionsManager().has(sender, "simpleclans.anyone.alliances")) {
            return MessageFormat.format(plugin.getLang("0.alliances.1.view.all.clan.alliances"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
        }
        return null;
    }

    @Override
    public void execute(CommandSender player, String label, String[] arg)
    {
        String headColor = plugin.getSettingsManager().getPageHeadingsColor();
        String subColor = plugin.getSettingsManager().getPageSubTitleColor();

        if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.alliances")) {
            List<Clan> clans = plugin.getClanManager().getClans();
            plugin.getClanManager().sortClansByKDR(clans);

            ChatBlock chatBlock = new ChatBlock();

            ChatBlock.sendBlank(player);
            ChatBlock.saySingle(player, plugin.getSettingsManager().getServerName() + subColor + " " + plugin.getLang("alliances") + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
            ChatBlock.sendBlank(player);

            chatBlock.setAlignment("l", "l");
            chatBlock.addRow("  " + headColor + plugin.getLang("clan"), plugin.getLang("allies"));

            for (Clan clan : clans) {
                if (!clan.isVerified()) {
                    continue;
                }

                chatBlock.addRow("  " + ChatColor.AQUA + clan.getName(), clan.getAllyString(ChatColor.DARK_GRAY + ", "));
            }

            boolean more = chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

            if (more) {
                plugin.getStorageManager().addChatBlock(player, chatBlock);
                ChatBlock.sendBlank(player);
                ChatBlock.sendMessage(player, headColor + MessageFormat.format(plugin.getLang("view.next.page"), plugin.getSettingsManager().getCommandMore()));
            }

            ChatBlock.sendBlank(player);
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
    }
}
