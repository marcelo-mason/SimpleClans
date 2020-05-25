package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.PermissionLevel;
import net.sacredlabyrinth.phaed.simpleclans.RankPermission;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCComponent;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCComponentImpl;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCFrame;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class WarningFrame extends SCFrame {
	private final SimpleClans plugin = SimpleClans.getInstance();
	private final Object permission;

	public WarningFrame(SCFrame parent, Player viewer, Object permission) {
		super(parent, viewer);
		this.permission = permission;
	}

	@Override
	public void createComponents() {
		for (int slot = 0; slot < 9; slot++) {
			if (slot == 4)
				continue;
			add(Components.getPanelComponent(slot));
		}
		add(Components.getBackComponent(getParent(), 4));

		int slot = 22;
		if (permission != null) {
			addNoPermissionComponent(permission, slot);
		} else {
			addNotVerifiedComponent(slot);
		}
	}

	private void addNotVerifiedComponent(int slot) {
		SCComponent verified = new SCComponentImpl(lang("gui.warning.not.verified.title"),
				Collections.singletonList(lang("gui.warning.not.verified.lore")), Material.LEVER, slot);
		add(verified);
	}

	private void addNoPermissionComponent(Object permission, int slot) {
		List<String> lore;
		if (permission instanceof String) {
			lore = Collections.singletonList(lang("gui.warning.no.permission.plugin.lore"));
			ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(getViewer().getUniqueId());
			if (((String) permission).contains("simpleclans.leader") && !cp.isLeader()) {
				lore = Collections.singletonList(lang("gui.warning.no.permission.leader.lore"));
			}
		} else {
			RankPermission p = (RankPermission) permission;
			String level = p.getPermissionLevel() == PermissionLevel.LEADER ? lang("leader")
					: lang("trusted");
			lore = Collections.singletonList(lang("gui.warning.no.permission.rank.lore", level, p.toString()));
		}
		SCComponent perm = new SCComponentImpl(lang("gui.warning.no.permission.title"), lore,
				Material.BARRIER, slot);
		add(perm);
	}

	@Override
	public @NotNull String getTitle() {
		return lang("gui.warning.title");
	}

	@Override
	public int getSize() {
		return 4 * 9;
	}

}
