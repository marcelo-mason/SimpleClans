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

import com.google.common.collect.Maps;
import java.util.Map;

/**
 *
 * @author Max
 */
public enum PermissionType
{

    ALLOW_ALLY_BUILD((byte) 0, "AllyBuild", Type.BUILD),
    ALLOW_ALLY_BREAK((byte) 1, "AllyBreak", Type.BREAK),
    ALLOW_ALLY_INTERACT((byte) 2, "AllyInteract", Type.INTERACT),
    ALLOW_OUTSIDER_BUILD((byte) 3, "OutsiderBuild", Type.BUILD),
    ALLOW_OUTSIDER_BREAK((byte) 4, "OutsiderBreak", Type.BREAK),
    ALLOW_OUTSIDER_INTERACT((byte) 5, "OutsiderInteract", Type.INTERACT),
    ALLOW_UNVERIFIED_BUILD((byte) 6, "UnverifiedBuild", Type.BUILD),
    ALLOW_UNVERIFIED_BREAK((byte) 7, "UnverifiedBreak", Type.BREAK),
    ALLOW_UNVERIFIED_INTERACT((byte) 8, "UnverifiedInteract", Type.INTERACT),
    DENY_MEMBER_BUILD((byte) 9, "MemberBuild", Type.BUILD),
    DENY_MEMBER_BREAK((byte) 10, "MemberBreak", Type.BREAK),
    DENY_MEMBER_INTERACT((byte) 11, "MemberInteract", Type.INTERACT);
    private static final PermissionType[] byId = new PermissionType[11];
    private final static Map<String, PermissionType> byName = Maps.newHashMap();
    private byte id;
    private String name;
    private PermissionType.Type type;

    static {
        for (PermissionType type : values()) {
            if (byId.length > type.id) {
                byId[type.id] = type;
            }
            byName.put(type.getName(), type);
        }
    }

    private PermissionType(byte id, String name, PermissionType.Type type)
    {
        this.id = id;
        this.name = name;
        this.type = type;
    }

//    public static void main(String[] args)
//    {
//        System.out.println(PermissionType.getPermissionByName("memberinteract"));
//    }
    public String getName()
    {
        return name;
    }

    public byte getId()
    {
        return id;
    }

    public Type getType()
    {
        return type;
    }

    public static PermissionType getPermissionByName(String name)
    {
        return byName.get(name);
    }

    public static PermissionType getPermissionById(final byte id)
    {
        if (byId.length > id) {
            return byId[id];
        } else {
            return null;
        }
    }

    public enum Type
    {

        BUILD,
        BREAK,
        INTERACT;
    }
}