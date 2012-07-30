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
package net.sacredlabyrinth.phaed.simpleclans.api.events;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;

/**
 *
 * @author Max
 */
public class SimpleClansBankBalanceChangeEvent extends ClanEvent
{

    private double before, after;

    public SimpleClansBankBalanceChangeEvent(ClanPlayer cp, Clan clan, double before, double after)
    {

        super(cp, clan);
        this.after = after;
        this.before = before;
    }

    public double getBalanceAfter()
    {
        return after;
    }

    public double getBalanceBefore()
    {
        return before;
    }
}