package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import java.util.List;
import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author phaed
 */
public class RivalriesCommand extends GenericConsoleCommand
{

    private SimpleClans plugin;

    public RivalriesCommand(SimpleClans plugin)
    {
        super("Rivalries");
        this.plugin = plugin;
        setArgumentRange(0, 0);
        setUsages(MessageFormat.format(plugin.getLang("usage.rivalries"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("rivalries.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (plugin.getPermissionsManager().has(sender, "simpleclans.anyone.rivalries")) {
            return MessageFormat.format(plugin.getLang("0.rivalries.1.view.all.clan.rivalries"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
        }
        return null;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args)
    {
        String headColor = plugin.getSettingsManager().getPageHeadingsColor();
        String subColor = plugin.getSettingsManager().getPageSubTitleColor();

        if (plugin.getPermissionsManager().has(sender, "simpleclans.anyone.rivalries")) {
            List<Clan> clans = plugin.getClanManager().getClans();
            plugin.getClanManager().sortClansByKDR(clans);

            ChatBlock chatBlock = new ChatBlock();

            ChatBlock.sendBlank(sender);
            ChatBlock.saySingle(sender, plugin.getSettingsManager().getServerName() + subColor + " " + plugin.getLang("rivalries") + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
            ChatBlock.sendBlank(sender);
            ChatBlock.sendMessage(sender, headColor + plugin.getLang("legend") + ChatColor.DARK_RED + " [" + plugin.getLang("war") + "]");
            ChatBlock.sendBlank(sender);

            chatBlock.setAlignment("l", "l");
            chatBlock.addRow(plugin.getLang("clan"), plugin.getLang("rivals"));

            for (Clan clan : clans) {
                if (!clan.isVerified()) {
                    continue;
                }

                chatBlock.addRow("  " + ChatColor.AQUA + clan.getName(), clan.getRivalString(ChatColor.DARK_GRAY + ", "));
            }

            boolean more = chatBlock.sendBlock(sender, plugin.getSettingsManager().getPageSize());

            if (more) {
                plugin.getStorageManager().addChatBlock(sender, chatBlock);
                ChatBlock.sendBlank(sender);
                ChatBlock.sendMessage(sender, headColor + MessageFormat.format(plugin.getLang("view.next.page"), plugin.getSettingsManager().getCommandMore()));
            }

            ChatBlock.sendBlank(sender);
        } else {
            ChatBlock.sendMessage(sender, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
    }
}
