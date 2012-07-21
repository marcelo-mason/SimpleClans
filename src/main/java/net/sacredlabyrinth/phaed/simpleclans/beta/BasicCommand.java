package net.sacredlabyrinth.phaed.simpleclans.beta;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author p000ison
 */
public abstract class BasicCommand implements Command
{

    private String name;
    private String description = "";
    private String usage = "";
    private String permission = null;
    private String[] notes = new String[0];
    private String[] identifiers = new String[0];
    private int minArguments = 0;
    private int maxArguments = 0;
    private boolean needsPlayer;
    private Player player;

    public BasicCommand(String name)
    {
        this.name = name;
    }

    @Override
    public void cancelInteraction(CommandSender executor)
    {
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public String[] getIdentifiers()
    {
        return identifiers;
    }

    @Override
    public int getMaxArguments()
    {
        return maxArguments;
    }

    @Override
    public int getMinArguments()
    {
        return minArguments;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String[] getNotes()
    {
        return notes;
    }

    @Override
    public String getPermission()
    {
        return permission;
    }

    @Override
    public String getUsage()
    {
        return usage;
    }

    @Override
    public boolean isIdentifier(CommandSender executor, String input)
    {
        for (String identifier : identifiers) {
            if (input.equalsIgnoreCase(identifier)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isInProgress(CommandSender executor)
    {
        return false;
    }

    @Override
    public boolean isInteractive()
    {
        return false;
    }

    @Override
    public boolean isShownOnHelpMenu()
    {
        return true;
    }

    public void setArgumentRange(int min, int max)
    {
        this.minArguments = min;
        this.maxArguments = max;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setIdentifiers(String... identifiers)
    {
        this.identifiers = identifiers;
    }

    public void setNotes(String... notes)
    {
        this.notes = notes;
    }

    public void setPermission(String permission)
    {
        this.permission = permission;
    }

    public void setUsage(String usage)
    {
        this.usage = usage;
    }

    @Override
    public void setPlayer(Player player)
    {
        this.player = player;
        needsPlayer = true;
    }

    @Override
    public Player getPlayer()
    {
        if (player == null) {
            throw new IllegalArgumentException("You need to set a Player first!");
        }
        return player;
    }

    @Override
    public boolean needsPlayer()
    {
        return needsPlayer;
    }
}