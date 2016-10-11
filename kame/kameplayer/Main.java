package kame.kameplayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerChannelEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import kame.kameplayer.baseutils.AxeUtils;
import kame.kameplayer.baseutils.ChunkUtils;
import kame.kameplayer.baseutils.DragonPhase;
import kame.kameplayer.baseutils.Respawn;
import kame.kameplayer.baseutils.Timer;
import kame.kameplayer.baseutils.Utils;
import kame.kameplayer.baseutils.mnsver.Vx_x_Rx;
import kame.kameplayer.commands.CommandGuideLines;
import kame.kameplayer.commands.CommandMods;
import kame.kameplayer.commands.CommandRespawn;
import kame.kameplayer.commands.Command_Listener;

public class Main
extends JavaPlugin
implements Listener
{
	public static final String ver = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].substring(1);
	private static boolean bool = false;
	public static boolean clearOres = false;
	private static Plugin plugin = null;
	public static List<Integer> materials = new ArrayList<>();
	public static Logger log;
	public static Vx_x_Rx obj;
	public static File file;
	public static List<String> worlds;
	public void onEnable()
	{
		try {
			obj = (Vx_x_Rx) Class.forName("kame.kameplayer.baseutils.mnsver.V" + Main.ver).newInstance();
		} catch (Exception e) {
			System.err.println("<kamePlayer> このBukkitのバージョンには対応していません " + Main.ver);
			System.err.println("<kamePlayer> 一部コマンドが正常に処理されない場合があります");
			System.err.println("<kamePlayer> Sorry... this version is not supported " + Main.ver);
		}
		file = getDataFolder();
		try{
			getServer().getPluginManager().registerEvents(new DragonPhase(), this);
		}catch(Exception e) {
			
		}
		if(plugin == null)plugin = this;
		loadConfig(Bukkit.getConsoleSender());
		loadSpawn();
		log = getLogger();
		new Timer().runTaskTimer(this,1,10);

		log.info("有効になりました ver" + ver + "です");
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(new AxeUtils(), this);
		getServer().getPluginManager().registerEvents(new ChunkUtils(), this);
		getServer().getPluginManager().registerEvents(new CommandRespawn(), this);
		new Command_Listener(this);
	}
	
	public void onDisable() {
		log.info("無効になりました");
	}

	public static Plugin getPlugin() {
		return plugin;
	}

	public static boolean isDebug() {
		return bool;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(CommandGuideLines.contains(event.getPlayer().getUniqueId())) {
			CommandGuideLines.a(event);
			return;
		}
	}

	@EventHandler
	public void changeWorld(PlayerChangedWorldEvent event) {
		if(CommandGuideLines.contains(event.getPlayer().getUniqueId())) {
			CommandGuideLines.a(event);
			return;
		}
	}

	@EventHandler
	public void playerQuit(PlayerQuitEvent event) {
		CommandMods.mod.remove(event.getPlayer().getName());
		if(CommandGuideLines.contains(event.getPlayer().getUniqueId())) {
			CommandGuideLines.a(event);
			return;
		}
		Objective obj = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("trig");
		if(obj != null) {
			Score score = obj.getScore(event.getPlayer().getName());
			if(score.getScore() == 10)score.setScore(0);
		}
	}
	@EventHandler
	public void playermod(PlayerChannelEvent event) {
		Player player = event.getPlayer();
		List<String> list = CommandMods.mod.get(player.getName());
		if(list == null)list = new ArrayList<String>();
		list.add(event.getChannel());
		CommandMods.mod.put(event.getPlayer().getName(), list);
		
	}


	@EventHandler
	private void onDeath(VehicleDestroyEvent event) {
		Utils.killed(event.getVehicle());
	}
	@EventHandler
	private void onDeath(EntityDeathEvent event) {
		Utils.killed(event.getEntity());
	}
	

	@EventHandler
	private void onCommand(PlayerJoinEvent event) {
		obj.sendTexture(event.getPlayer(), "https://comeapps.f5.si/kamemef/texture.zip", "2D53BC61E77DF0C6B1CEF2219826F403F63310BD");
	}
	public static void loadConfig(CommandSender sender) {
		FileConfiguration config;
		plugin.saveDefaultConfig();
		config = YamlConfiguration.loadConfiguration(new File(file, "config.yml"));
		bool = config.getBoolean("debug");
		if(config.getKeys(false).contains("clearBlock")) {
			clearOres = config.getBoolean("clearBlock.enable");
			worlds = config.getStringList("clearBlock.worlds");
			materials = config.getIntegerList("clearBlock.material");
		}

		sender.sendMessage(ChatColor.AQUA + "[kameplayer] Reload config.");
	}

	public static void loadSpawn() {
		FileConfiguration respawn = YamlConfiguration.loadConfiguration(new File(file, "respawn.save"));
		if(respawn.contains("spawn")){
			for(String uuid : respawn.getConfigurationSection("spawn").getKeys(false)) {
				UUID uid = UUID.fromString(uuid);
				String args[] = respawn.getString("spawn."+ uuid).split(" ");
				World world = Bukkit.getWorld(args[0]);
				double x = Double.parseDouble(args[1]);
				double y = Double.parseDouble(args[2]);
				double z = Double.parseDouble(args[3]);
				float yaw = Float.parseFloat(args[4]);
				float pitch=Float.parseFloat(args[5]);
				boolean keep = args[6].equals("true");
				Location loc=new Location(world, x, y, z, yaw, pitch);
				CommandRespawn.map.put(uid, new Respawn(loc, keep));
			}
		}
		try{
			respawn.save(new File(file, "respawn.save"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setDebug() {
		bool = !bool;
	}
}