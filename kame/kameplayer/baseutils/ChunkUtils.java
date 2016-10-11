package kame.kameplayer.baseutils;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import kame.kameplayer.Main;

public class ChunkUtils implements Listener {

	public static ItemStack kames;
	private HashSet<Chunk> chunks = new HashSet<Chunk>();

	public ChunkUtils() {
		kames = new ItemStack(Material.DIAMOND_BLOCK);
		ItemMeta im = kames.getItemMeta();
		im.setDisplayName("§6Chunkloader");
		im.setLore(Arrays.asList(new String[]{"§b@chunkloader","§b読み込ませたいチャンクに設置"}));
		kames.setItemMeta(im);
		Main.materials.add(0);
		
		YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(Main.getPlugin().getDataFolder(), "chunks.save"));
		if(config.contains("chunks")){
			for(String list : config.getStringList("chunks")) {
				String args[] = config.getString("chunks."+ list).split(" ");
				World world = Bukkit.getWorld(args[0]);
				int x = Integer.parseInt(args[1]);
				int z = Integer.parseInt(args[1]);
				chunks.add(world.getChunkAt(x, z));
			}
			for(Chunk chunk : chunks)if(!chunk.isLoaded())chunk.load();
		}
		try{
			config.save(new File(Main.getPlugin().getDataFolder(), "chunks.save"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void saveConfig(){
		YamlConfiguration respawn = new YamlConfiguration();
		int i = 0;
		for(Chunk chunk : chunks) {
			respawn.set("chunks." + i++, 
					new StringBuilder(chunk.getWorld().toString()).append(" ")
					.append(chunk.getX()).append(" ").append(chunk.getZ()).toString());
		}
		try{
			respawn.save(new File(Main.file, "chunks.save"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
	private void onChunkUnload(ChunkUnloadEvent event) {
		if(chunks.contains(event.getChunk())) {
			event.setCancelled(true);
		}
	}
	

	private boolean isChunkloader(Entity stand) {
		return stand.getType() == EntityType.ARMOR_STAND && stand.getCustomName() != null && stand.getCustomName().equals("kameplayer.chunkloader");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onBlockBreak(BlockBreakEvent event) {
		if(event.isCancelled())return;
		for(Entity e : event.getBlock().getWorld().getNearbyEntities(event.getBlock().getLocation().add(0.5, 0, 0.5), 0.01, 0.01, 0.01)) {
			if(isChunkloader(e)) {
				e.remove();
				event.getBlock().setType(Material.AIR);
				event.getPlayer().sendMessage("§a[kames.] チャンクローダーを破壊しました");
				chunks.remove(event.getBlock().getChunk());
				saveConfig();
			}
		}
	}

	private ArmorStand getBlockStand(Block block) {
		for(Entity e : block.getWorld().getNearbyEntities(block.getLocation().add(0.5, 0, 0.5), 0.01, 0.01, 0.01)) {
			if(isChunkloader(e)) {
				return (ArmorStand) e;
			}
		}
		return null;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onBlockPlace(BlockPlaceEvent event) {
		if(event.isCancelled())return;
		ItemStack item = event.getItemInHand().clone();
		item.setAmount(1);
		if(!item.equals(kames))return;
		for(Entity e : event.getBlock().getChunk().getEntities()) {
			if(isChunkloader(e)) {
				event.getPlayer().sendMessage("§c[kames.] 既にこのチャンクはロードされています");
				event.setCancelled(true);
				return;
			}
		}
		Location loc = event.getBlock().getLocation().add(0.5, 0, 0.5);
		ArmorStand entity = (ArmorStand) loc.getWorld().spawn(loc,ArmorStand.class);
		entity.setCustomName("kameplayer.chunkloader");
		entity.setGravity(false);
		entity.setSmall(true);
		entity.setMarker(true);
		entity.setVisible(false);
		entity.setHelmet(kames);
		event.getPlayer().sendMessage("§a[kames.] チャンクローダーを設置しました");
		chunks.add(event.getBlock().getChunk());
		saveConfig();
		return;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onBlockChange(EntityChangeBlockEvent event){
		if(getBlockStand(event.getBlock()) != null) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onBlockPiston(BlockPistonExtendEvent event) {
		if (event.isCancelled())return;
		for (Block block : event.getBlocks()) {
			if(getBlockStand(block) != null) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onBlockPiston(BlockPistonRetractEvent event) {
		if (event.isCancelled())return;
		for (Block block : event.getBlocks()) {
			if(getBlockStand(block) != null) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onBlockBreak(EntityExplodeEvent event) {
		if (event.isCancelled())return;
		for (Block block : event.blockList()) {
			if(getBlockStand(block) != null) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	private void onChunkCreate(final ChunkPopulateEvent event) {
		if(Main.clearOres && Main.worlds.contains(event.getWorld().getName()))
			new BukkitRunnable() {
			@Override
			public void run() {
				Chunk chunk = event.getChunk();
				for(int x=0;x<16;x++)
					for(int z=0;z<16;z++) {
						int y = 63;
						Block block;
						while(--y > 0 && ((block = chunk.getBlock(x, y, z)).getType() != Material.STONE))
							if(block.getLightFromSky() == 0 && Main.materials.contains(block.getType().ordinal()))block.setType(Material.STONE);
						while(--y > 0)if((block = chunk.getBlock(x, y, z)).getType() != Material.STONE)block.setType(Material.STONE);
					}
			}
		}.runTaskLater(Main.getPlugin(),10);

	}

}
