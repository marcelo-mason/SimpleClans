package net.sacredlabyrinth.phaed.simpleclans.ui;

import net.sacredlabyrinth.phaed.simpleclans.RankPermission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class InventoryDrawer {

    private InventoryDrawer() {
    }

    public static void open(@Nullable SCFrame frame) {
        if (frame == null) return;

        new BukkitRunnable() {

            @Override
            public void run() {
                Inventory inventory = Bukkit.createInventory(frame.getViewer(), frame.getSize(), frame.getTitle());

                setComponents(inventory, frame);

                frame.getViewer().openInventory(inventory);
                InventoryController.register(frame);
            }
        }.runTask(SimpleClans.getInstance());
    }

    public static void update(@NotNull SCFrame frame) {

        new BukkitRunnable() {

            @Override
            public void run() {
                InventoryView view = frame.getViewer().getOpenInventory();
                Inventory inventory = view.getTopInventory();
                if (inventory.getType() == InventoryType.CRAFTING) {
                    return;
                }
                //if the title or size changed, the inventory needs to be recreated
                if (!view.getTitle().equals(frame.getTitle()) || inventory.getSize() != frame.getSize()) {
                    open(frame);
                    return;
                }
                inventory.clear();

                setComponents(inventory, frame);

            }
        }.runTask(SimpleClans.getInstance());
    }

    private static void setComponents(@NotNull Inventory inventory, @NotNull SCFrame frame) {
        frame.clear();
        frame.createComponents();

        SimpleClans plugin = SimpleClans.getInstance();
        if (frame.getComponents().isEmpty()) {
            plugin.getLogger().warning(String.format("Frame %s has no components", frame.getTitle()));
            return;
        }
        for (SCComponent c : frame.getComponents()) {
            if (c.getSlot() >= frame.getSize()) {
                continue;
            }
            ItemMeta itemMeta = c.getItemMeta();
            if (itemMeta != null) {
                List<String> lore = itemMeta.getLore();
                if (lore != null) {
                    Object permission = c.getLorePermission();
                    if (permission != null) {
                        if (!hasPermission(frame.getViewer(), permission)) {
                            lore.clear();
                            lore.add(lang("gui.lore.no.permission"));
                            itemMeta.setLore(lore);
                            c.setItemMeta(itemMeta);
                        }
                    }
                }
            }
            inventory.setItem(c.getSlot(), c.getItem());
        }
    }

	private static boolean hasPermission(@NotNull Player viewer, @NotNull Object permission) {
    	SimpleClans plugin = SimpleClans.getInstance();
    	if (permission instanceof String) {
    		return plugin.getPermissionsManager().has(viewer, (String) permission);
		}
    	return plugin.getPermissionsManager().has(viewer, (RankPermission) permission, false);
	}

}
