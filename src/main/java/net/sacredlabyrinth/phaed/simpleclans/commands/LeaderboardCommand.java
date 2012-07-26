package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.List;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.beta.GenericPlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author phaed
 */
public class LeaderboardCommand extends GenericPlayerCommand
{

    private SimpleClans plugin;

    public LeaderboardCommand(SimpleClans plugin)
    {
        super("Leaderboard");
        this.plugin = plugin;
        setArgumentRange(0, 0);
        setUsages(MessageFormat.format(plugin.getLang("usage.leaderboard"), plugin.getSettingsManager().getCommandClan()));
        setIdentifiers(plugin.getLang("leaderboard.command"));
    }

    @Override
    public String getMenu(ClanPlayer cp, CommandSender sender)
    {
        if (plugin.getPermissionsManager().has(sender, "simpleclans.anyone.leaderboard")) {
            return MessageFormat.format(plugin.getLang("0.leaderboard.1.view.leaderboard"), plugin.getSettingsManager().getCommandClan(), ChatColor.WHITE);
        }
        return null;
    }

    @Override
    public void execute(Player player, String label, String[] args)
    {

        String headColor = plugin.getSettingsManager().getPageHeadingsColor();
        String subColor = plugin.getSettingsManager().getPageSubTitleColor();
        NumberFormat formatter = new DecimalFormat("#.#");


        if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.leaderboard")) {
            List<ClanPlayer> clanPlayers = plugin.getClanManager().getAllClanPlayers();
            plugin.getClanManager().sortClanPlayersByKDR(clanPlayers);

            ChatBlock chatBlock = new ChatBlock();

            ChatBlock.sendBlank(player);
            ChatBlock.saySingle(player, plugin.getSettingsManager().getServerName() + subColor + " " + plugin.getLang("leaderboard.command") + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
            ChatBlock.sendBlank(player);
            ChatBlock.sendMessage(player, headColor + MessageFormat.format(plugin.getLang("total.clan.players.0"), subColor + clanPlayers.size()));
            ChatBlock.sendBlank(player);

            chatBlock.setAlignment("c", "l", "c", "c", "c", "c");
            chatBlock.addRow("  " + headColor + plugin.getLang("rank"), plugin.getLang("player"), plugin.getLang("kdr"), plugin.getLang("clan"), plugin.getLang("seen"));

            int rank = 1;

            for (ClanPlayer cp : clanPlayers) {
                Player p = plugin.getServer().getPlayer(cp.getName());

                boolean isOnline = false;

                if (p != null) {
                    isOnline = true;
                }


                String name = (cp.isLeader() ? plugin.getSettingsManager().getPageLeaderColor() : ((cp.isTrusted() ? plugin.getSettingsManager().getPageTrustedColor() : plugin.getSettingsManager().getPageUnTrustedColor()))) + cp.getName();
                String lastSeen = (isOnline ? ChatColor.GREEN + plugin.getLang("online") : ChatColor.WHITE + cp.getLastSeenDaysString());

                String clanTag = ChatColor.WHITE + plugin.getLang("free.agent");

                if (cp.getClan() != null) {
                    clanTag = cp.getClan().getColorTag();
                }

                chatBlock.addRow("  " + rank, name, ChatColor.YELLOW + "" + formatter.format(cp.getKDR()), ChatColor.WHITE + clanTag, lastSeen);
                rank++;
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
