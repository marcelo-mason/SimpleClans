package net.sacredlabyrinth.phaed.simpleclans;

/**
 * 
 * @author RoinujNosde
 *
 */
public enum RankPermission {

	ALLY_ADD("simpleclans.leader.ally"), ALLY_CHAT("simpleclans.member.ally"), ALLY_REMOVE("simpleclans.leader.ally"),
	BANK_BALANCE("simpleclans.member.bank"), BANK_DEPOSIT("simpleclans.member.bank"),
	BANK_WITHDRAW("simpleclans.member.bank"), BB_ADD("simpleclans.member.bb-add"),
	BB_CLEAR("simpleclans.leader.bb-clear"), COORDS("simpleclans.member.coords"),
	FEE_ENABLE("simpleclans.leader.fee"), FEE_SET("simpleclans.leader.fee"),
	HOME_REGROUP("simpleclans.leader.home-regroup"), HOME_SET("simpleclans.leader.home-set"),
	HOME_TP("simpleclans.member.home"), INVITE("simpleclans.leader.invite"), KICK("simpleclans.leader.kick"),
	MODTAG("simpleclans.leader.modtag"), RANK_DISPLAYNAME("simpleclans.leader.rank.setdisplayname"),
	RANK_LIST("simpleclans.leader.rank.list"), RIVAL_ADD("simpleclans.leader.rival"),
	RIVAL_REMOVE("simpleclans.leader.rival"), WAR_END("simpleclans.leader.war"), WAR_START("simpleclans.leader.war"),
	STATS("simpleclans.member.stats"), VITALS("simpleclans.member.vitals"), KILLS("simpleclans.member.kills"),
	DESCRIPTION("simpleclans.leader.description"), MOSTKILLED("simpleclans.mod.mostkilled");

	private String bukkitPermission;

	private RankPermission(String bukkitPermission) {
		this.bukkitPermission = bukkitPermission;
	}

	/**
	 * Returns the Bukkit equivalent to this rank permission
	 * 
	 * @return
	 */
	public String getBukkitPermission() {
		return bukkitPermission;
	}

	@Override
	public String toString() {
		return super.toString().replace("_", ".").toLowerCase();
	}

	/**
	 * Checks if this is a valid rank permission.
	 * 
	 * @param permission
	 * @return
	 */
	public static boolean isValid(String permission) {
		for (RankPermission p : values()) {
			if (p.toString().equalsIgnoreCase(permission)) {
				return true;
			}
		}
		return false;
	}
}
