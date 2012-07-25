package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.List;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.beta.GenericConsoleCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author phaed
 */
public class ListCommand extends GenericConsoleCommand
{

    private SimpleClans plugin;

    public ListCommand(SimpleClans plugin)
    {
        super("List");
        this.plugin = plugin;
        setArgumentRange(0, 0);
        setUsages(String.format(plugin.getLang("usage.list"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("list.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (plugin.getPermissionsManager().has(sender, "simpleclans.anyone.list")) {
            return MessageFormat.format(plugin.getLang("0.list.1.lists.all.clans"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
        }
        return null;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args)
    {
        String headColor = plugin.getSettingsManager().getPageHeadingsColor();
        String subColor = plugin.getSettingsManager().getPageSubTitleColor();
        NumberFormat formatter = new DecimalFormat("#.#");

        if (plugin.getPermissionsManager().has(sender, "simpleclans.anyone.list")) {

            List<Clan> clans = plugin.getClanManager().getClans();
            plugin.getClanManager().sortClansByKDR(clans);

            if (!clans.isEmpty()) {
                ChatBlock chatBlock = new ChatBlock();

                ChatBlock.sendBlank(sender);
                ChatBlock.saySingle(sender, plugin.getSettingsManager().getServerName() + subColor + " " + plugin.getLang("clans.lower") + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                ChatBlock.sendBlank(sender);
                ChatBlock.sendMessage(sender, headColor + plugin.getLang("total.clans") + " " + subColor + clans.size());
                ChatBlock.sendBlank(sender);

                chatBlock.setAlignment("c", "l", "c", "c");
                chatBlock.setFlexibility(false, true, false, false);

                chatBlock.addRow("  " + headColor + plugin.getLang("rank"), plugin.getLang("name"), plugin.getLang("kdr"), plugin.getLang("members"));

                int rank = 1;

                for (Clan clan : clans) {
                    if (!plugin.getSettingsManager().isShowUnverifiedOnList()) {
                        if (!clan.isVerified()) {
                            continue;
                        }
                    }

                    String tag = plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketLeft() + plugin.getSettingsManager().getTagDefaultColor() + clan.getColorTag() + plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketRight();
                    String name = (clan.isVerified() ? plugin.getSettingsManager().getPageClanNameColor() : ChatColor.GRAY) + clan.getName();
                    String fullname = tag + " " + name;
                    String size = ChatColor.WHITE + "" + clan.getSize();
                    String kdr = clan.isVerified() ? ChatColor.YELLOW + "" + formatter.format(clan.getTotalKDR()) : "";

                    chatBlock.addRow("  " + rank, fullname, kdr, size);
                    rank++;
                }

                boolean more = chatBlock.sendBlock(sender, plugin.getSettingsManager().getPageSize());

                if (more) {
                    plugin.getStorageManager().addChatBlock(sender, chatBlock);
                    ChatBlock.sendBlank(sender);
                    ChatBlock.sendMessage(sender, headColor + MessageFormat.format(plugin.getLang("view.next.page"), plugin.getSettingsManager().getCommandMore()));
                }

                ChatBlock.sendBlank(sender);
            } else {
                ChatBlock.sendMessage(sender, ChatColor.RED + plugin.getLang("no.clans.have.been.created"));
            }
        } else {
            ChatBlock.sendMessage(sender, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
    }
}
