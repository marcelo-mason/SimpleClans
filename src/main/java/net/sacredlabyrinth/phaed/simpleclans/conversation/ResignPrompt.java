package net.sacredlabyrinth.phaed.simpleclans.conversation;

import java.text.MessageFormat;
import java.util.Arrays;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
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
                cp.addResignTime(clan.getTag());
                clan.removePlayerFromClan(player.getUniqueId());
                
                return new MessagePromptImpl(ChatColor.AQUA + plugin.getLang("resign.success"));
            } else if (clan.isLeader(player) && clan.getLeaders().size() == 1) {
                clan.disband();
                String clanDisbanded = ChatColor.AQUA + MessageFormat.format(plugin.getLang("clan.has.been.disbanded"), clan.getName());
                //message for the server
                plugin.getClanManager().serverAnnounce(clanDisbanded);
                //message for the player
                return new MessagePromptImpl(clanDisbanded);
            } else {
                return new MessagePromptImpl(ChatColor.RED + plugin.getLang("last.leader.cannot.resign.you.must.appoint.another.leader.or.disband.the.clan"));
            }
        } else {
        	return new MessagePromptImpl(ChatColor.RED + plugin.getLang("resign.request.cancelled"));
        }
    }

    @Override
    public String getPromptText(ConversationContext cc) {
        final SimpleClans plugin = (SimpleClans) cc.getPlugin();

        return ChatColor.RED + MessageFormat.format(
                plugin.getLang("resign.confirmation"), Arrays.asList(
                    plugin.getLang("resign.yes"), plugin.getLang("resign.no")));
    }

}
