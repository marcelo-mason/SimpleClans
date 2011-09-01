package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.List;


/**
 * @author phaed
 */
public class ListCommand
{
    public ListCommand()
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
        NumberFormat formatter = new DecimalFormat("#.#");

        if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.list"))
        {
            if (arg.length == 0)
            {
                List<Clan> clans = plugin.getClanManager().getClans();
                plugin.getClanManager().sortClansByKDR(clans);

                if (!clans.isEmpty())
                {
                    ChatBlock chatBlock = new ChatBlock();

                    ChatBlock.sendBlank(player);
                    ChatBlock.saySingle(player, plugin.getSettingsManager().getServerName() + subColor + " " + plugin.getLang().getString("clans.lower") + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                    ChatBlock.sendBlank(player);
                    ChatBlock.sendMessage(player, headColor + plugin.getLang().getString("total.clans") + " " + subColor + clans.size());
                    ChatBlock.sendBlank(player);

                    chatBlock.setAlignment("l", "c", "c");

                    chatBlock.addRow("  " + headColor + plugin.getLang().getString("name"), plugin.getLang().getString("kdr"), plugin.getLang().getString("members"));

                    for (Clan clan : clans)
                    {
                        if (!plugin.getSettingsManager().isShowUnverifiedOnList())
                        {
                            if (!clan.isVerified())
                            {
                                continue;
                            }
                        }

                        String tag = plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketLeft() + plugin.getSettingsManager().getTagDefaultColor() + clan.getColorTag() + plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketRight();
                        String name = (clan.isVerified() ? plugin.getSettingsManager().getPageClanNameColor() : ChatColor.GRAY) + clan.getName();
                        String fullname = tag + " " + name;
                        String size = ChatColor.WHITE + "" + clan.getSize();
                        String kdr = clan.isVerified() ? ChatColor.YELLOW + "" + formatter.format(clan.getTotalKDR()) : "";

                        chatBlock.addRow("  " + fullname, kdr, size);
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
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("no.clans.have.been.created"));
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang().getString("usage.list"), plugin.getSettingsManager().getCommandClan()));
            }
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang().getString("insufficient.permissions"));
        }
    }
}
