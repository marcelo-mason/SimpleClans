/*
 * Copyright (C) 2012 p000ison
 * 
 * This work is licensed under the Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of
 * this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send
 * a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco,
 * California, 94105, USA.
 * 
 */
package net.sacredlabyrinth.phaed.simpleclans.commands;

import org.bukkit.command.CommandSender;

/**
 *
 * @author Max
 */
public abstract class GenericCommand implements Command
{

    private String name;
    private String usage;
    private int minArgs;
    private int maxArgs;
    private String[] identifiers;

    public GenericCommand(String name)
    {
        this.name = name;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public abstract void execute(CommandSender sender, String label, String[] args);

    @Override
    public void setUsage(String text)
    {
        this.usage = text;
    }

    @Override
    public void setIdentifiers(String... identifiers)
    {
        this.identifiers = identifiers;
    }

    @Override
    public boolean isIdentifier(String cmd)
    {
        for (String identifier : identifiers) {
            if (cmd.equalsIgnoreCase(identifier)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getUsage()
    {
        return usage;
    }

    @Override
    public int getMaxArguments()
    {
        return minArgs;
    }

    @Override
    public int getMinArguments()
    {
        return maxArgs;
    }

    @Override
    public void setArgumentRange(int min, int max)
    {
        minArgs = min;
        maxArgs = max;
    }
}
