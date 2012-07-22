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
public interface Command
{

    public void execute(CommandSender sender, String label, String[] args);

    public String getName();

    public boolean isLeaderOnly();

    public void setLeaderOnly();

    public String getPermission();

    public void setPermission(String perm);

    public void setMenuName(String name);

    public String getMenuName();

    public String getUsage();

    public void setIdentifiers(String... identifiers);

    public boolean isIdentifier(String cmd);

    public void setUsage(String text);

    public int getMaxArguments();

    public int getMinArguments();

    public void setArgumentRange(int min, int max);
}
