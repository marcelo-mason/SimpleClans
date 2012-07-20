package net.sacredlabyrinth.phaed.simpleclans.beta;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author p000ison
 */
public interface Command
{

    public void cancelInteraction(CommandSender executor);

    public boolean execute(CommandSender executor, String identifier, String[] args);

    public String getDescription();

    public String[] getIdentifiers();

    public int getMaxArguments();

    public int getMinArguments();

    public String getName();

    public String[] getNotes();

    public String getPermission();

    public String getUsage();

    public boolean isIdentifier(CommandSender executor, String input);

    public boolean isInProgress(CommandSender executor);

    public boolean isInteractive();

    public boolean isShownOnHelpMenu();

    public boolean needsPlayer();

    public Player getPlayer();

    public void setPlayer(Player player);
}