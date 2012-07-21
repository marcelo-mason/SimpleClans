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
package net.sacredlabyrinth.phaed.simpleclans;

/**
 *
 * @author Max
 */
public enum PermissionType
{

    ALLOW_ALLY_BUILD((byte) 0, "allybuild"),
    ALLOW_ALLY_BREAK((byte) 1, "allybreak"),
    ALLOW_ALLY_INTERACT((byte) 2, "allyinteract"),
    ALLOW_OUTSIDER_BUILD((byte) 3, "outsiderbuild"),
    ALLOW_OUTSIDER_BREAK((byte) 4, "outsiderbreak"),
    ALLOW_OUTSIDER_INTERACT((byte) 5, "outsiderinteract"),
    ALLOW_UNVERIFIED_BUILD((byte) 6, "unverifiedbuild"),
    ALLOW_UNVERIFIED_BREAK((byte) 7, "unverifiedbreak"),
    ALLOW_UNVERIFIED_INTERACT((byte) 8, "unverifiedinteract"),
    DENY_MEMBER_BUILD((byte) 9, "memberbuild"),
    DENY_MEMBER_BREAK((byte) 10, "memberbreak"),
    DENY_MEMBER_INTERACT((byte) 11, "memberinteract");

    private static final PermissionType[] byId = new PermissionType[8];
    private byte id;
    private String name;

    static {
        for (PermissionType type : values()) {
            if (byId.length > type.id) {
                byId[type.id] = type;
            }
        }
    }

    private PermissionType(byte id, String name)
    {
        this.id = id;
        this.name = name;
    }

//    public static void main(String[] args)
//    {
//        System.out.println(PermissionType.getPermissionById((byte) 5).getId());
//    }
    public String getName()
    {
        return name;
    }

    public byte getId()
    {
        return id;
    }

    public static PermissionType getPermissionById(final byte id)
    {
        if (byId.length > id) {
            return byId[id];
        } else {
            return null;
        }
    }
}