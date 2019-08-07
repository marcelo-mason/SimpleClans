package net.sacredlabyrinth.phaed.simpleclans.conversation;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import static org.bukkit.conversations.Prompt.END_OF_CONVERSATION;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

/**
 *
 * @author roinujnosde
 */
public class ResignPrompt extends StringPrompt {

    @Override
    public Prompt acceptInput(ConversationContext cc, String input) {
        final SimpleClans plugin = (SimpleClans) cc.getPlugin();

        String yes = plugin.getLang("resign.yes");
        Player player = (Player) cc.getForWhom();
        ClanManager cm = plugin.getClanManager();
        ClanPlayer cp = cm.getClanPlayer(player);
        Clan clan = cp.getClan();

        if (yes.equalsIgnoreCase(input)) {
            if (!clan.isLeader(player) || clan.getLeaders().size() > 1) {
                clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("0.has.resigned"), Helper.capitalize(player.getName())));
                if (SimpleClans.getInstance().hasUUID()) {
                    clan.removePlayerFromClan(player.getUniqueId());
                } else {
                    clan.removePlayerFromClan(player.getName());
                }
                player.sendMessage(ChatColor.AQUA + plugin.getLang("resign.success"));
            } else if (clan.isLeader(player) && clan.getLeaders().size() == 1) {
                plugin.getClanManager().serverAnnounce(ChatColor.AQUA + MessageFormat.format(plugin.getLang("clan.has.been.disbanded"), clan.getName()));
                clan.disband();
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("last.leader.cannot.resign.you.must.appoint.another.leader.or.disband.the.clan"));
            }
        }

        return END_OF_CONVERSATION;
    }

    @Override
    public String getPromptText(ConversationContext cc) {
        final SimpleClans plugin = (SimpleClans) cc.getPlugin();

        return ChatColor.RED + MessageFormat.format(
                plugin.getLang("resign.confirmation"), (Object) new String[] {
                    plugin.getLang("resign.yes"), plugin.getLang("resign.no")});
    }

}
