package net.sacredlabyrinth.phaed.simpleclans.uuid;

import java.util.UUID;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author NeT32
 */
public class UUIDMigration {

	private UUIDMigration() {}
	
    public static boolean canReturnUUID() {
        if(!SimpleClans.getInstance().getSettingsManager().isOnlineMode())
            return false;
        try {
            Bukkit.class.getDeclaredMethod("getPlayer", UUID.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public static UUID getForcedPlayerUUID(String playerDisplayName) {
        Player onlinePlayer = SimpleClans.getInstance().getServer().getPlayerExact(playerDisplayName);
        @SuppressWarnings("deprecation")
		OfflinePlayer offlinePlayer = SimpleClans.getInstance().getServer().getOfflinePlayer(playerDisplayName);

        if (onlinePlayer != null) {
            return onlinePlayer.getUniqueId();
        } else {
            for (ClanPlayer cp : SimpleClans.getInstance().getClanManager().getAllClanPlayers()) {
                if (cp.getName().equalsIgnoreCase(playerDisplayName)) {
                    return cp.getUniqueId();
                }
            }
            try {
                return UUIDFetcher.getUUIDOf(playerDisplayName);
            } catch (Exception ex) {
                if (offlinePlayer != null) {
                    return offlinePlayer.getUniqueId();
                } else {
                    return null;
                }
            }
        }
    }

}
