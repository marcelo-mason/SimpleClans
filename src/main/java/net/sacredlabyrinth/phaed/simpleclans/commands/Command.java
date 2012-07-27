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

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Max
 */
public interface Command
{

    //public void execute(org.bukkit.command.CommandSender sender, String label, String[] args);
    //public void execute(org.bukkit.entity.Player player, String label, String[] args);

    public String getName();

//    public boolean isNoClanCommand();
//
//    public void setNoClanCommand();
//
//    public boolean isLeaderCommand();
//
//    public void setLeaderCommand();
//
//    public boolean isTrustedCommand();
//
//    public void setTrustedCommand();
//
//    public boolean isSpoutCommand();
//
//    public void setSpoutCommand();
//
//    public boolean isVerifiedCommand();
//
//    public void setVerifiedCommand();
//
//    public boolean isNonVerifiedCommand();
//
//    public void setNonVerifiedCommand();

//    public boolean hasPermission(org.bukkit.command.CommandSender sender);
//
//    public void setPermissions(String... perm);

    public String[] getUsages();

    public void setIdentifiers(String... identifiers);

    public boolean isIdentifier(String cmd);

    public void setUsages(String... text);

//    public void setMenu(String... text);

    public String getMenu(ClanPlayer cp, CommandSender sender);

    public int getMaxArguments();

    public int getMinArguments();

    public void setArgumentRange(int min, int max);
}
