package net.sacredlabyrinth.phaed.simpleclans.api;

import java.util.UUID;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author NeT32
 */
public class UUIDMigration {

    public static boolean canReturnUUID() {
        try {
            Bukkit.class.getDeclaredMethod("getPlayer", UUID.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Converts a String to a UUID
     *
     * @param uuid The string to be converted
     * @return The result
     */
    public static UUID getUUID(String uuid) {
        return UUID.fromString(uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20, 32));
    }

    public static Object getForcedPlayer(String playerDisplayName) {
        Player OnlinePlayer = Helper.matchOnePlayer(playerDisplayName);
        if (OnlinePlayer != null) {
            return OnlinePlayer;
        } else {
            OfflinePlayer OfflinePlayer = SimpleClans.getInstance().getServer().getOfflinePlayer(playerDisplayName);
            return OfflinePlayer;
        }
    }

    public static UUID getForcedPlayerUUID(String playerDisplayName) {
        Player OnlinePlayer = Helper.matchOnePlayer(playerDisplayName);
        OfflinePlayer OfflinePlayer = SimpleClans.getInstance().getServer().getOfflinePlayer(playerDisplayName);
        if (OnlinePlayer != null) {
            return OnlinePlayer.getUniqueId();
        } else if (OfflinePlayer != null) {
            return OfflinePlayer.getUniqueId();
        } else {
            for (ClanPlayer cp : SimpleClans.getInstance().getClanManager().getAllClanPlayers()) {
                if (cp.getName().equalsIgnoreCase(playerDisplayName)) {
                    return cp.getUniqueId();
                }
            }
            try {
                return UUIDFetcher.getUUIDOf(playerDisplayName);
            } catch (Exception ex) {
                return null;
            }
        }
    }

}
