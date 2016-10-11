package kame.kameplayer.baseutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class Timer extends BukkitRunnable{

	private static Map<String, Long> time = new HashMap<String, Long>();
	public static List<Entities> entities = new ArrayList<Entities>();
	@Override
	public void run() {
		for(int i = 0;entities.size() > i;i++){
			Entities entity = entities.get(i);
			try{
				if(!entity.isActive()){
					CommandSender sender = entity.getSender();
					String cmd = entity.getCommand();
					if(entity.getEntity().getLastDamageCause() != null)Bukkit.dispatchCommand(sender, cmd);
					entities.remove(entity);
				}
			}catch(Exception e){
				entities.remove(entity);
				e.printStackTrace();
			}

		}
	}

	public static void timeset(String name) {
		time.put(name, System.currentTimeMillis());
	}

	public static long gettime(String name) {
		return time.containsKey(name) ? System.currentTimeMillis() - time.get(name) : 0;
	}
}

