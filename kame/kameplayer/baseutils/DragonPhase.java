package kame.kameplayer.baseutils;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragon.Phase;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EnderDragonChangePhaseEvent;
import org.bukkit.event.entity.EntityInteractEvent;

public class DragonPhase implements Listener {

	@EventHandler
	private void onDragonTarget(EnderDragonChangePhaseEvent event) {
		if(event.getNewPhase() == Phase.LAND_ON_PORTAL) {
			for(EnderDragon dragon : event.getEntity().getWorld().getEntitiesByClass(EnderDragon.class)) if(!event.getEntity().equals(dragon)){
				
				switch(dragon.getPhase()) {
				case FLY_TO_PORTAL:
				case HOVER:
				case LAND_ON_PORTAL:
				case LEAVE_PORTAL:
				case ROAR_BEFORE_ATTACK:
				case SEARCH_FOR_BREATH_ATTACK_TARGET:
					event.setCancelled(true);
					break;
				default:
					break;

				}
			}
		}
	}
	
	@EventHandler
	private void onDragonCollision(EntityInteractEvent event) {
		if(event.getEntityType() == EntityType.ENDER_DRAGON)event.setCancelled(true);
	}
}
