package kame.kameplayer.baseutils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class AxeUtils implements Listener {

	private static Map<UUID, Locs> pos = new HashMap<UUID, Locs>();

	public class Locs {
		private Location L;
		private Location R;

		private Locs(Location loc) {
			L = loc;
			R = loc;
		}

		public Location getL() {
			return L;
		}
		public Location getR() {
			return R;
		}
		private boolean setL(Location loc) {
			if(L.equals(loc))return false;
			L = loc;
			if(!L.getWorld().equals(R.getWorld())) R = loc;
			return true;
		}
		private boolean setR(Location loc) {
			if(R.equals(loc))return false;
			R = loc;
			if(!L.getWorld().equals(R.getWorld())) L = loc;
			return true;
		}
	}

	public static Locs getAxePos(Player player) {
		Locs l = pos.get(player.getUniqueId());
		return l;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		if(!action.name().matches("(LEFT|RIGHT)_CLICK_BLOCK"))return;
		Player player = event.getPlayer();
		if(!Utils.hasPermCommand(player, "copy"))return;
		ItemStack item = player.getItemInHand();

		if(!item.getType().equals(Material.WOOD_AXE) || item.getDurability() != 60)return;
		event.setCancelled(true);
		Location loc = event.getClickedBlock().getLocation();
		UUID uid = player.getUniqueId();
		Locs l = pos.get(uid);
		if(l == null) {
			l = new Locs(loc);
			message(player, l);
		}else {
			if(action.equals(Action.LEFT_CLICK_BLOCK) && l.setL(loc))message(player, l);
			if(action.equals(Action.RIGHT_CLICK_BLOCK) && l.setR(loc))message(player, l);
		}
		pos.put(uid, l);
	}
	private void message(Player player, Locs loc) {
		Location L = loc.getL();
		Location R = loc.getR();
		String message ="§b[kame.] setLocation(§a" + positionBuilder(L.getBlockX(), L.getBlockY(), L.getBlockZ())
											+"§b to §a" + positionBuilder(R.getBlockX(), R.getBlockY(), R.getBlockZ())+ "§b )";
		player.sendMessage(message);
	}
	
	private String positionBuilder(int... i) {
		return new StringBuilder().append(i[0]).append(", ").append(i[1]).append(", ").append(i[2]).toString();
	}
}
