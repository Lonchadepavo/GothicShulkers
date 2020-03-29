package com.loncha.gothicshulkers;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	HashMap<Player, Block> shulkerClicked = new HashMap<Player,Block>();
	
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = (Player) e.getPlayer();
		Block b = e.getClickedBlock();
	
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			String tipoBloque = b.getType().toString();
			if (tipoBloque.contains("SHULKER")) {
				if (!b.hasMetadata("usando")) {
					b.setMetadata("usando", new FixedMetadataValue(this, "usando"));
					shulkerClicked.put(p, b);
					
					e.setCancelled(true);
					
					ShulkerBox shulker = (ShulkerBox) b.getState();
					
					ItemStack[] items = shulker.getInventory().getContents();
					
					Inventory invShulker = Bukkit.createInventory(p, 27, "Caja");
					invShulker.setContents(items);
					
					p.openInventory(invShulker);
					
					for (Player players : Bukkit.getOnlinePlayers()) {
						if (p.getLocation().distanceSquared(players.getLocation()) <= 10) {
							players.getWorld().playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0F, 0.01F);
						}
					}
				} else {
					e.setCancelled(true);
					p.sendTitle(ChatColor.DARK_RED+"La caja está siendo usada por otra persona", "");
				}
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		Inventory inv = e.getInventory();
		String inventoryName = e.getInventory().getTitle();
		
		if (inventoryName.equals("Caja")) {
			Block b = shulkerClicked.get(p);
			
			ShulkerBox shulker = (ShulkerBox) b.getState();
			shulker.getInventory().clear();
			
			Inventory invShulker = shulker.getInventory();
			
			ItemStack[] items = inv.getContents();
			invShulker.setContents(items);
			
			b.removeMetadata("usando", this);
			shulkerClicked.remove(p);
			for (Player players : Bukkit.getOnlinePlayers()) {
				if (p.getLocation().distanceSquared(players.getLocation()) <= 10) {
					players.getWorld().playSound(p.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1.0F, 0.01F);
				}
			}
		}
		
	}


}
