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
package net.sacredlabyrinth.phaed.simpleclans.beta;

/**
 *
 * @author Max
 */
public abstract class GenericConsoleCommand extends GenericCommand
{

    public GenericConsoleCommand(String name)
    {
        super(name);
    }

    public abstract void execute(org.bukkit.command.CommandSender sender, String label, String[] args);
}
