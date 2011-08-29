package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


/**
 *
 * @author phaed
 */
public class ListCommand
{
    public ListCommand()
    {
    }

    /**
     * Run the command
     * @param player
     * @param arg
     */
    public void run(Player player, String[] arg)
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
                    ChatBlock.saySingle(player, plugin.getSettingsManager().getServerName() + subColor + " clans " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                    ChatBlock.sendBlank(player);
                    ChatBlock.sendMessage(player, headColor + "Total clans: " + subColor + clans.size());
                    ChatBlock.sendBlank(player);

                    chatBlock.setAlignment("l", "c", "c");

                    chatBlock.addRow("  " + headColor + "Name", "KDR", "Members");

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
                        String name = clan.isVerified() ? plugin.getSettingsManager().getPageClanNameColor() + clan.getName() : ChatColor.GRAY + "unverified";
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
                        ChatBlock.sendMessage(player, headColor + "Type /" + plugin.getSettingsManager().getCommandMore() + " to view next page.");
                    }

                    ChatBlock.sendBlank(player);
                }
                else
                {
                    ChatBlock.sendMessage(player, ChatColor.RED + "No clans have been created");
                }
            }
            else
            {
                ChatBlock.sendMessage(player, ChatColor.RED + "Usage: /" + plugin.getSettingsManager().getCommandClan() + " list");
            }
        }
        else
        {
            ChatBlock.sendMessage(player, ChatColor.RED + "Insufficient permissions");
        }
    }
}
