package net.sacredlabyrinth.phaed.simpleclans.utils;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author RoinujNosde
 */
@SuppressWarnings("deprecation")
public class ChatFormatMigration {

    SimpleClans plugin = SimpleClans.getInstance();

    
    public void migrateClanChat() {
        SettingsManager sm = plugin.getSettingsManager();
        FileConfiguration c = sm.getConfig();

        if (c.getString("clanchat.name-color") == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        sb.append('&');
        sb.append(sm.getClanChatBracketColor());
        sb.append(sm.getClanChatTagBracketLeft());
        sb.append("%clan%");
        sb.append("&");
        sb.append(sm.getClanChatBracketColor());
        sb.append(sm.getClanChatTagBracketRight());
        sb.append(" ");
        sb.append('&');
        sb.append(sm.getClanChatNameColor());
        sb.append(sm.getClanChatPlayerBracketLeft());
        sb.append("%nick-color%");
        sb.append("%player%");
        sb.append('&');
        sb.append(sm.getClanChatNameColor());
        sb.append(sm.getClanChatPlayerBracketRight());
        sb.append(" %rank%: ");
        sb.append('&');
        sb.append(sm.getClanChatMessageColor());
        sb.append("%message%");

        c.set("clanchat.format", sb.toString());
        c.set("clanchat.rank", "&f[%rank%&f]");
        c.set("clanchat.rank.color", null);
        c.set("clanchat.name-color", null);
        c.set("clanchat.player-bracket", null);
        c.set("clanchat.message-color", null);
        c.set("clanchat.tag-bracket", null);
        sm.save();
    }

    public void migrateAllyChat() {
        SettingsManager sm = SimpleClans.getInstance().getSettingsManager();
        FileConfiguration c = sm.getConfig();

        //Checks if the old format is still in use
        if (c.getString("allychat.tag-color") == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append('&');
        sb.append(sm.getAllyChatBracketColor());
        sb.append(sm.getAllyChatTagBracketLeft());
        sb.append('&');
        sb.append(sm.getAllyChatTagColor());
        sb.append(sm.getCommandAlly());
        sb.append('&');
        sb.append(sm.getAllyChatBracketColor());
        sb.append(sm.getAllyChatTagBracketRight());
        sb.append(" ");
        sb.append("&4<%clan%&4> ");
        sb.append('&');
        sb.append(sm.getAllyChatBracketColor());
        sb.append(sm.getAllyChatPlayerBracketLeft());
        sb.append("%nick-color%");
        sb.append("%player%");
        sb.append('&');
        sb.append(sm.getAllyChatBracketColor());
        sb.append(sm.getAllyChatPlayerBracketRight());
        sb.append(" %rank%: ");
        sb.append('&');
        sb.append(sm.getAllyChatMessageColor());
        sb.append("%message%");

        c.set("allychat.format", sb.toString());
        c.set("allychat.rank", "&f[%rank%&f]");
        c.set("allychat.tag-color", null);
        c.set("allychat.name-color", null);
        c.set("allychat.player-bracket", null);
        c.set("allychat.message-color", null);
        c.set("allychat.tag-bracket", null);
        sm.save();
    }
}
