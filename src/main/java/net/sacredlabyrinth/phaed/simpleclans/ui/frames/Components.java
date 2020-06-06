package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import net.sacredlabyrinth.phaed.simpleclans.utils.Paginator;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.SkullMeta;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.ui.InventoryDrawer;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCComponent;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCComponentImpl;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCFrame;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class Components {

	private Components() {
	}

	public static SCComponent getPlayerComponent(SCFrame frame, Player viewer, OfflinePlayer subject, int slot,
                                                 boolean openDetails) {
        ClanPlayer cp = SimpleClans.getInstance().getClanManager().getCreateClanPlayer(subject.getUniqueId());

        return getPlayerComponent(frame, viewer, cp, slot, openDetails);
    }

    public static SCComponent getPlayerComponent(SCFrame frame, Player viewer, ClanPlayer cp, int slot,
                                                 boolean openDetails) {
        SimpleClans pl = SimpleClans.getInstance();

        String status = cp.getClan() == null ? lang("free.agent")
                : (cp.isLeader() ? lang("leader")
                : (cp.isTrusted() ? lang("trusted") : lang("untrusted")));
        SCComponent c = new SCComponentImpl(lang("gui.playerdetails.player.title", cp.getName()),
                Arrays.asList(
                        cp.getClan() == null ? lang("gui.playerdetails.player.lore.noclan")
                                : lang("gui.playerdetails.player.lore.clan", cp.getClan().getColorTag(),
                                cp.getClan().getName()),
                        lang("gui.playerdetails.player.lore.rank",
                                Helper.parseColors(cp.getRankDisplayName())),
                        lang("gui.playerdetails.player.lore.status", status),
                        lang("gui.playerdetails.player.lore.kdr",
                                new DecimalFormat("#.#").format(cp.getKDR())),
                        lang("gui.playerdetails.player.lore.kill.totals", cp.getRivalKills(),
                                cp.getNeutralKills(), cp.getCivilianKills()),
                        lang("gui.playerdetails.player.lore.deaths", cp.getDeaths()),
                        lang("gui.playerdetails.player.lore.join.date", cp.getJoinDateString()),
                        lang("gui.playerdetails.player.lore.last.seen", cp.getLastSeenString()),
                        lang("gui.playerdetails.player.lore.past.clans", cp.getPastClansString(
                                lang("gui.playerdetails.player.lore.past.clans.separator"))),
                        lang("gui.playerdetails.player.lore.inactive", cp.getInactiveDays(),
                                pl.getSettingsManager().getPurgePlayers())),
                Material.SKULL_ITEM, (byte) 3, slot);
        SkullMeta itemMeta = (SkullMeta) c.getItemMeta();
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(cp.getUniqueId());
        if (itemMeta != null) {
            itemMeta.setOwner(offlinePlayer.getName());
            c.setItemMeta(itemMeta);
        }
        if (viewer.getUniqueId().equals(cp.getUniqueId())) {
            c.setLorePermission("simpleclans.member.lookup");
        } else {
            c.setLorePermission("simpleclans.anyone.lookup");
        }
        if (openDetails) {
            c.setListener(ClickType.LEFT,
                    () -> InventoryDrawer.open(new PlayerDetailsFrame(viewer, frame, offlinePlayer)));
        }
        return c;
    }

    public static SCComponent getClanComponent(@NotNull SCFrame frame, @NotNull Player viewer,
                                               @Nullable Clan clan, int slot, boolean openDetails) {
        SimpleClans pl = SimpleClans.getInstance();
        String name;
        List<String> lore;
        if (clan != null) {
            name = lang("gui.clandetails.clan.title", clan.getColorTag(), clan.getName());
            lore = Arrays.asList(
                    lang("gui.clandetails.clan.lore.description",
                            clan.getDescription() != null && !clan.getDescription().isEmpty() ? clan.getDescription() : lang("no.description")),
                    lang("gui.clandetails.clan.lore.status", clan.isVerified() ? lang("verified") : lang("unverified")),
                    lang("gui.clandetails.clan.lore.leaders", clan.getLeadersString("", ", ")),
                    lang("gui.clandetails.clan.lore.online.members", clan.getOnlineMembers().size(), clan.getMembers().size()),
                    lang("gui.clandetails.clan.lore.kdr", Helper.formatKDR(clan.getTotalKDR())),
                    lang("gui.clandetails.clan.lore.kill.totals", clan.getTotalRival(), clan.getTotalNeutral(), clan.getTotalCivilian()),
                    lang("gui.clandetails.clan.lore.deaths", clan.getTotalDeaths()),
                    lang("gui.clandetails.clan.lore.fee", clan.isMemberFeeEnabled()
                            ? lang("fee.enabled") : lang("fee.disabled"), clan.getMemberFee()),
                    lang("gui.clandetails.clan.lore.allies", clan.getAllies().isEmpty() ? lang("none") : clan.getAllyString(lang("gui.clandetails.clan.lore.allies.separator"))),
                    lang("gui.clandetails.clan.lore.rivals", clan.getRivals().isEmpty() ? lang("none") : clan.getRivalString(lang("gui.clandetails.clan.lore.rivals.separator"))),
                    lang("gui.clandetails.clan.lore.founded", clan.getFoundedString()),
                    lang("gui.clandetails.clan.lore.inactive", clan.getInactiveDays(), (clan.isVerified() ?
                            pl.getSettingsManager().getPurgeClan() : pl.getSettingsManager().getPurgeUnverified())
                    ));
        } else {
            name = lang("gui.clandetails.free.agent.title");
            lore = null;
        }

        SCComponent c = new SCComponentImpl(name, lore, Material.BANNER, slot);
        BannerMeta itemMeta = ((BannerMeta) c.getItemMeta());
        Objects.requireNonNull(itemMeta).setBaseColor(DyeColor.GREEN);
        c.setItemMeta(itemMeta);
        if (openDetails && clan != null) {
            c.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new ClanDetailsFrame(frame, viewer, clan)));
        }

        if (clan != null && clan.isMember(viewer)) {
            c.setLorePermission("simpleclans.member.profile");
        } else {
            c.setLorePermission("simpleclans.anyone.profile");
        }

        return c;
    }

    public static SCComponent getBackComponent(SCFrame parent, int slot) {
        SCComponent back = new SCComponentImpl(lang("gui.back.title"), null,
                Material.ARROW, slot);
        back.setListener(ClickType.LEFT, () -> InventoryDrawer.open(parent));
        return back;
    }

    @SuppressWarnings("deprecation")
    public static SCComponent getPanelComponent(int slot) {
        return new SCComponentImpl(" ", null, Material.STAINED_GLASS_PANE, DyeColor.GRAY.getDyeData(), slot);
    }

    public static SCComponent getPreviousPageComponent(int slot, @Nullable Runnable listener, @NotNull Paginator paginator) {
	    if (!paginator.hasPreviousPage()) {
	        return getPanelComponent(slot);
        }
        SCComponent c = new SCComponentImpl(lang("gui.previous.page.title"), null,
                Material.STONE_BUTTON, slot);
        c.setListener(ClickType.LEFT, listener);
        return c;
    }

    public static SCComponent getNextPageComponent(int slot, @Nullable Runnable listener, @NotNull Paginator paginator) {
	    if (!paginator.hasNextPage()) {
	        return getPanelComponent(slot);
        }
        SCComponent c = new SCComponentImpl(lang("gui.next.page.title"), null,
                Material.STONE_BUTTON, slot);
        c.setListener(ClickType.LEFT, listener);
        return c;
    }
}
