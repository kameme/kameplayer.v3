package kame.kameplayer.commands;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import kame.kameplayer.Main;
import kame.kameplayer.baseutils.Respawn;
import kame.kameplayer.baseutils.Utils;
import kame.kameplayer.baseutils.Utils.Fail;

public class CommandRespawn implements CommandExecutor, Listener {

	public static Map<UUID, Respawn> map = new HashMap<>();
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {
		//respawn @p (world) x y z pitch yaw
		if(!Utils.hasPermCommand(sender, cmd.getName()))return Utils.sendFailParm(sender);
		if(args.length > 0)
		{
			Player player = Bukkit.getPlayer(args[0]);
			if(player != null)
			{
				if(args.length > 1)
				{
					if(args[1].equals("set"))
					{
						boolean value = false;
						if(args[args.length-1].equals("keep"))value = true;
						Location location = null;
						if(player.getBedSpawnLocation() != null)location = player.getBedSpawnLocation().add(0.5, 0, 0.5);
						if(args.length > 5)
						{
							World world = Bukkit.getWorld(args[2]);
							Double x = parse(args[3]), y = parse(args[4]), z = parse(args[5]);
							Float pitch =0f, yaw = 0f;
							if(args.length > 6) yaw = parse2(args[6]);
							if(args.length > 7) pitch = parse2(args[7]);
							if(world != null && x != null && y != null && z != null)
							{
								location = new Location(world, x, y, z, yaw, pitch);
							}
						}
						if(location != null)map.put(player.getUniqueId(), new Respawn(location, value));
						else sender.sendMessage(ChatColor.RED + "座標が無効でした");
						saveConfig();
						return true;
					}
					if(args[1].equals("remove"))
					{
						map.remove(player.getUniqueId());
						saveConfig();
						return true;
					}
				}
				else if(player.isDead())
				{
					Utils.sendRespawn(player);
					if(args.length >= 5)
					{
						World world = Bukkit.getWorld(args[1]);
						Double x = parse(args[2]), y = parse(args[3]), z = parse(args[4]);
						Float pitch =0f, yaw = 0f;
						if(args.length > 5) pitch = parse2(args[5]);
						if(args.length > 6) yaw = parse2(args[6]);
						if(world == null || x == null || y == null || z == null)return true;
						player.teleport(new Location(world, x, y, z, pitch, yaw));
					}
					return true;
				}
			}
			return false;
		}
		return Utils.sendFailCommand(sender, Fail.LowParam, "/respawn <player> (world) x y z yaw pitch");
	}

	private void saveConfig(){
		YamlConfiguration respawn = new YamlConfiguration();
		for(Map.Entry<UUID, Respawn> mp : map.entrySet())
		{
			Respawn res = mp.getValue();
			Location loc = res.getLocation();

			respawn.set("spawn." + mp.getKey().toString(),
			new StringBuilder()
			.append(loc.getWorld().getName()).append(" ")
			.append(loc.getX()).append(" ")
			.append(loc.getY()).append(" ")
			.append(loc.getZ()).append(" ")
			.append(loc.getYaw()).append(" ")
			.append(loc.getPitch()).append(" ")
			.append(res.getKeep()).toString());
		}
		try{
			respawn.save(new File(Main.file, "respawn.save"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	private void onPlayerDeath(final PlayerDeathEvent event) {
		if(map.containsKey(event.getEntity().getUniqueId()))
			new BukkitRunnable(){
			@Override
			public void run(){
				Utils.sendRespawn(event.getEntity());
				return;
			}
		}.runTask(Main.getPlugin());
	}

	@EventHandler
	private void onPlayerRespawn(PlayerRespawnEvent event) {
		UUID uid = event.getPlayer().getUniqueId();
		Respawn res = map.get(uid);
		if(res != null) {
			event.setRespawnLocation(res.getLocation());
			if(!res.getKeep())map.remove(uid);
			saveConfig();
		}
	}
	
	private Double parse(String str) {
		try {if(str.contains("."))return Double.valueOf(str); return Integer.valueOf(str) + 0.5d;}
		catch (Exception e){return null;}
	}
	
	private float parse2(String str) {
		try {return Float.valueOf(str);}
		catch (Exception e){return 0;}
	}
}
