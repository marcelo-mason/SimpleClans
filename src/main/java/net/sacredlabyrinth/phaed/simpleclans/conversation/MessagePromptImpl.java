package net.sacredlabyrinth.phaed.simpleclans.conversation;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

/**
 *
 * @author roinujnosde
 */
public class MessagePromptImpl extends org.bukkit.conversations.MessagePrompt {
    
    private final String message;

    public MessagePromptImpl(String message) {
        this.message = message;
    }

    @Override
    protected Prompt getNextPrompt(ConversationContext cc) {
        return END_OF_CONVERSATION;
    }

    @Override
    public String getPromptText(ConversationContext cc) {
        return message;
    }
    
}
