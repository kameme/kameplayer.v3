package kame.kameplayer.commands;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import kame.kameplayer.Main;
import kame.kameplayer.baseutils.Utils;
import kame.kameplayer.baseutils.Utils.Fail;


public class CommandGuideLines implements CommandExecutor{

	private static Map<UUID, LinkedList<Location>> pos = new HashMap<UUID, LinkedList<Location>>();
	private static Map<UUID, String> put = new HashMap<UUID, String>();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {
		//guidelines @a (x y z)
		if(!Utils.hasPermCommand(sender, cmd.getName()))return Utils.sendFailParm(sender);
		if(sender instanceof Player) {
			Player player = (Player) sender;
			UUID uid = player.getUniqueId();
			if(args.length == 0 || (args.length == 1 && args[0].equals("create"))) {
				if(pos.containsKey(player.getUniqueId())) {
					player.sendMessage("§b[kame.] §a素手でクリックをすると制御点を追加します。");
					player.sendMessage("§b[kame.] §aシフト右クリックすると制御点を削除します。");
					return false;
				}
				player.sendMessage("§b[kame.] §aガイドエディタを起動しました。");
				player.sendMessage("§b[kame.] §a素手でクリックをすると制御点を追加します。");
				player.sendMessage("§b[kame.] §aシフト右クリックすると制御点を削除します。");
				pos.put(player.getUniqueId(), new LinkedList<Location>());
				timer(player.getUniqueId());
				return false;
			}
			if(args.length == 1 || args.length == 2) {
				if(args[0].equals("put")) {
					if(!pos.containsKey(uid)) {
						player.sendMessage("§b[kame.] §c最初にガイドを登録してください");
						player.sendMessage("§b[kame.] §cCommand: /guidelines");
						return false;
					}
					StringBuilder builder = new StringBuilder().append("/guidelines");
					if(args.length == 2)builder.append(" ").append(args[1]);
					else builder.append(" @a");
					builder.append(" ").append(player.getWorld().getName());
					for(Location loc : pos.get(player.getUniqueId())) {
						builder.append(" ").append(loc.getX()).append(",").append(loc.getY()).append(",").append(loc.getZ());
					}
					put.put(player.getUniqueId(), builder.toString());
					pos.remove(player.getUniqueId());
					player.sendMessage("§b[kame.] §aコマンドを書き込みたいコマンドブロックに左クリックしてください");
					player.sendMessage("§b[kame.] §aガイドエディタを終了しました。");
				}
				if(args[0].equals("cancel")) {
					if(!contains(uid)) {
						player.sendMessage("§b[kame.] §cコマンドが起動されていないので終了します。");
						player.sendMessage("§b[kame.] §cCommand: /guidelines");
						return false;
					}
					a(player.getUniqueId());
					player.sendMessage("§b[kame.] §aキャンセルしました。");
					player.sendMessage("§b[kame.] §aガイドエディタを終了しました。");
				}
				return false;
			}
			player.sendMessage("§b[kame.] §cパラメーターが違います");
			return false;
		}
		if(args.length > 2) {
			Player player = Bukkit.getPlayer(args[0]);
			World world = Bukkit.getWorld(args[1]);
			if(player == null)return Utils.sendFailCommand(sender, Fail.NoPlayer, args[0]);
			if(world == null){sender.sendMessage("そのワールドは存在しません!");return false;}
			Pattern p = Pattern.compile("~?-?[0-9]+(.[0-9]+)?,~?-?[0-9]+(.[0-9]+)?,~?-?[0-9]+(.[0-9]+)?");
			Location loc = new Location(world, 0, 0, 0);
			Location old = null;
			for(String str : args) if(p.matcher(str).find()){
				String[] l = str.split(",");
				loc.setX(Utils.parse(l[0], player.getLocation().getX()));
				loc.setY(Utils.parse(l[1], player.getLocation().getY()));
				loc.setZ(Utils.parse(l[2], player.getLocation().getZ()));
				if(old != null)line(loc, old, player);
				old = loc.clone();
			}
			sender.sendMessage("[kame.] " + player.getName() + "にガイドラインを表示しました。");
			return false;
		}
		sender.sendMessage("§b[kame.] §aプレイヤーより実行してください。");
		return false;
	}

	public static void a(PlayerChangedWorldEvent event) {
		UUID id = event.getPlayer().getUniqueId();
		if(!contains(id))return;
		event.getPlayer().sendMessage("§b[kame.] §aワールドが変更されたのでガイドエディタを終了しました。" );
		a(id);
	}

	public static void a(PlayerQuitEvent event) {
		UUID id = event.getPlayer().getUniqueId();
		if(!contains(id))return;
		a(id);
	}

	private static void a(UUID id) {
		pos.remove(id);
		put.remove(id);
	}

	public static void a(PlayerInteractEvent event) {
		UUID uid = event.getPlayer().getUniqueId();
		if(!contains(uid))return;
		switch(event.getAction()) {
		case LEFT_CLICK_BLOCK:
			if(put.containsKey(uid)) commandWrite(event);
			else
			if(pos.containsKey(uid)) addList(event);
			break;

		case RIGHT_CLICK_AIR:
		case RIGHT_CLICK_BLOCK:
			if(pos.containsKey(uid)) subList(event);
			break;

		default:
			break;
		}
	}

	private static void subList(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if(event.getItem() == null || !player.isSneaking())return;
		LinkedList<Location> locs = pos.get(player.getUniqueId());
		if(locs.size() == 0)return;
		Location loc;
		event.setCancelled(true);
		loc = locs.removeLast();
		player.sendMessage("§b[kame.] §aRemove pos" + locs.size() + " Loc §e[" + loc.getBlockX() + ", " + (loc.getBlockY()-1) + ", " + loc.getBlockZ() + "]" );

	}

	private static void addList(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if(event.getItem() == null)return;
		event.setCancelled(true);
		LinkedList<Location> locs = pos.get(player.getUniqueId());
		Location loc = event.getClickedBlock().getLocation();
		if(player.isSneaking() && locs.size() > 0) {
			Location oldloc = locs.getLast();
			if(oldloc.distance(loc.clone().add(0.5, 1.1, 0.5)) < 0.8) {
				selecter(loc, player.getUniqueId());
				player.sendMessage("§b[kame.] §aFix pos" + locs.size() + " Loc §e[" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + "]" );
				locs.removeLast();
				locs.addLast(loc.add(0, 1.1, 0));
				Utils.sendPacket(player, "flame", (float)loc.getX(), (float)loc.getY(), (float)loc.getZ(), 0, 0, 0, 1, 0);
				return;
			}
		}
		select.put(player.getUniqueId(), Select.C);
		player.sendMessage("§b[kame.] §aAdd pos" + locs.size() + " Loc §e[" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + "]" );
		locs.addLast(loc.add(0.5, 1.1, 0.5));
		Utils.sendPacket(player, "flame", (float)loc.getX(), (float)loc.getY(), (float)loc.getZ(), 0, 0, 0, 1, 0);
	}

	private enum Select {
		C,N,E,S,W,NE,SE,SW,NW;
	}

	private static Map<UUID, Select> select = new HashMap<UUID, Select>();
	private static void selecter(Location loc, UUID uid) {
		Select s = select.get(uid);
		switch(s) {
		case C:
			loc.add(0.5, 0, 0);
			select.put(uid, Select.N);
			break;
		case N:
			loc.add(1, 0, 0.5);
			select.put(uid, Select.E);
			break;
		case E:
			loc.add(0.5, 0, 1);
			select.put(uid, Select.S);
			break;
		case S:
			loc.add(0, 0, 0.5);
			select.put(uid, Select.W);
			break;
		case W:
			loc.add(0, 0, 0);
			select.put(uid, Select.NE);
			break;
		case NE:
			loc.add(1, 0, 0);
			select.put(uid, Select.SE);
			break;
		case SE:
			loc.add(1, 0, 1);
			select.put(uid, Select.SW);
			break;
		case SW:
			loc.add(0, 0, 1);
			select.put(uid, Select.NW);
			break;
		case NW:
			loc.add(0.5, 0, 0.5);
			select.put(uid, Select.C);
			break;
		default:
			break;
		}
	}



	private static void commandWrite(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if(!block.getType().equals(Material.COMMAND))return;
		event.setCancelled(true);
		Player player = event.getPlayer();
		String com = put.remove(player.getUniqueId());
		CommandBlock cb = (CommandBlock) block.getState();
		cb.setCommand(com);
		cb.update();
		player.sendMessage("§b[kame.] §aコマンドを書き込みました[" + com + "]" );
	}

	private void timer(final UUID uid) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!pos.containsKey(uid)) {
					this.cancel();
					return;
				}
				Player player = Bukkit.getPlayer(uid);
				Location old = null;
				for(Location loc : pos.get(uid)) {
					if(old != null)line(loc, old, player);
					old = loc.clone();
				}
			}
    	}.runTaskTimer(Main.getPlugin(), 10, 10);
	}

	private void line(Location loc, Location old, Player player) {
		int i = (int) (loc.distance(old)*2);
		double dx = (float) (loc.getX() - old.getX())/i;
		double dy = (float) (loc.getY() - old.getY())/i;
		double dz = (float) (loc.getZ() - old.getZ())/i;
		for(int k = 1; k <= i; k++) {
			Utils.sendPacket(player, "footstep", (float)(old.getX()+dx*k), (float)(old.getY()+dy*k), (float)(old.getZ()+dz*k), 0, 0, 0, 1, 1);
		}
	}

	public static boolean contains(UUID uid) {
		return pos.containsKey(uid) || put.containsKey(uid);
	}
}
