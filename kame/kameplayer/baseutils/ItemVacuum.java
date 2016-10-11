package kame.kameplayer.baseutils;

import java.util.ArrayList;
import java.util.List;

import kame.kameplayer.Main;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ItemVacuum extends BukkitRunnable{
	private Player player;
	private int range;
	private List<Material> material = new ArrayList<Material>();
	public ItemVacuum(Player player, int range)
	{
		this.player = player;
		this.range = range;
		this.runTaskTimer(Main.getPlugin(), 1l, 1l);
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return this.player;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public void stop() {
		this.cancel();
	}
	public int getRange() {
		return this.range;
	}

	public void addMaterial(Material item) {
		material.add(item);
	}

	public void removeMaterial(Material item) {
		material.remove(item);
	}

	public List<Material> getMaterials() {
		return material;
	}

	public boolean isInList(Material item) {
		return material.contains(item);
	}

	@Override
	public void run() {
		if(!player.isOnline())this.cancel();
		for(Item e : player.getWorld().getEntitiesByClass(Item.class))
			if(material.size() == 0 || material.contains(e.getItemStack().getType())){
			Location iloc = e.getLocation();
			Location ploc = player.getEyeLocation();
			double d = iloc.distance(ploc);
			if(d < range)
			{
				d*=10;
				Location loc = ploc.subtract(iloc);
				Vector vec = e.getVelocity();
				vec.setX(vec.getX() + loc.getX()/d);
				vec.setY(vec.getY() + loc.getY()/d);
				vec.setZ(vec.getZ() + loc.getZ()/d);
				e.setVelocity(vec);
			}
		}
	}
}
