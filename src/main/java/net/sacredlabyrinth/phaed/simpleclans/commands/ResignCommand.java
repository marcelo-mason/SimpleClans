package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.util.HashMap;
import java.util.Map;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.conversation.ResignPrompt;
import org.bukkit.conversations.ConversationFactory;

/**
 * @author phaed
 */
public class ResignCommand {
    public ResignCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.member.resign")) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            return;
        }
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
            return;
        }
        
        new ConversationFactory(plugin)
                .withFirstPrompt(new ResignPrompt(plugin.getLang("resign.yes"), plugin.getLang("resign.no")))
                .withLocalEcho(false)
                .buildConversation(player).begin();
    }
}
