package io.github.pseudoresonance.pseudoapi.bukkit.listeners;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;
import io.github.pseudoresonance.pseudoapi.bukkit.utils.HeadUtils;

public class BlockL implements Listener {
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		ItemStack is = e.getItemInHand();
		Block b = e.getBlock();
		if (is.getType() == Material.PLAYER_HEAD) {
			ItemMeta im = is.getItemMeta();
			String name = im.getPersistentDataContainer().get(new NamespacedKey(PseudoAPI.plugin, "HeadName"), PersistentDataType.STRING);
			if (name != null) {
				String uuid = im.getPersistentDataContainer().get(new NamespacedKey(PseudoAPI.plugin, "Uuid"), PersistentDataType.STRING);
				if (uuid == null) {
					String base64 = im.getPersistentDataContainer().get(new NamespacedKey(PseudoAPI.plugin, "Base64"), PersistentDataType.STRING);
					if (base64 != null) {
						Skull skull = (Skull) b.getState();
						skull.getPersistentDataContainer().set(new NamespacedKey(PseudoAPI.plugin, "HeadName"), PersistentDataType.STRING, name);
						skull.getPersistentDataContainer().set(new NamespacedKey(PseudoAPI.plugin, "Base64"), PersistentDataType.STRING, base64);
						skull.update();
					}
				} else {
					Skull skull = (Skull) b.getState();
					skull.getPersistentDataContainer().set(new NamespacedKey(PseudoAPI.plugin, "HeadName"), PersistentDataType.STRING, name);
					skull.getPersistentDataContainer().set(new NamespacedKey(PseudoAPI.plugin, "Uuid"), PersistentDataType.STRING, uuid);
					skull.update();
				}
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Block b = e.getBlock();
		Player p = e.getPlayer();
		if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE) {
			if (b.getType() == Material.PLAYER_HEAD || b.getType() == Material.PLAYER_WALL_HEAD) {
				Skull skull = (Skull) b.getState();
				String name = skull.getPersistentDataContainer().get(new NamespacedKey(PseudoAPI.plugin, "HeadName"), PersistentDataType.STRING);
				if (name != null) {
					ItemStack drop = null;
					String uuid = skull.getPersistentDataContainer().get(new NamespacedKey(PseudoAPI.plugin, "Uuid"), PersistentDataType.STRING);
					if (uuid == null) {
						String base64 = skull.getPersistentDataContainer().get(new NamespacedKey(PseudoAPI.plugin, "Base64"), PersistentDataType.STRING);
						if (base64 != null) {
							drop = HeadUtils.getHeadWithBase64(base64, name);
						}
					} else {
						drop = HeadUtils.getHeadWithUUID(UUID.fromString(uuid), name);
					}
					if (drop != null) {
						b.getWorld().dropItemNaturally(b.getLocation(), drop);
						e.setDropItems(false);
					}
				}
			}
		}
	}

}
