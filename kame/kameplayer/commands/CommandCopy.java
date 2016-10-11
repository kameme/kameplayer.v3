package kame.kameplayer.commands;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kame.kameplayer.baseutils.Timer;
import kame.kameplayer.baseutils.Utils;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandCopy implements CommandExecutor {

	private int blocks;
	private boolean inv;
	private List<Material> materials = new ArrayList<Material>();
	ArrayList<Integer> id = new ArrayList<Integer>();
	@Override
	public boolean onCommand(CommandSender sender, Command label, String cmd, String[] args)
	{
		if(!Utils.hasPermCommand(sender, "copy"))return Utils.sendFailParm(sender);
		if(cmd.equals("pos") && sender instanceof Player) {
			Player player = ((Player)sender);
			player.getWorld().dropItemNaturally(player.getEyeLocation(), new ItemStack(Material.WOOD_AXE, 1, (short) 60)).setPickupDelay(0);
			player.sendMessage(ChatColor.AQUA + "[kame.] 左クリックで基準点、右クリックで対角点を選択してください。");
			return true;
		}
		if(!cmd.equals("pos") && sender instanceof Player) {
			Player player = ((Player)sender);
			player.sendMessage(ChatColor.AQUA + "[kame.] /posで斧を取得し左クリックで基準点、右クリックで対角点を選択してください。");
			player.sendMessage(ChatColor.AQUA + "[kame.] /" + cmd + "x y z x1 y1 z1 x2 y2 z2 block...");
			return true;
		}
		if(args.length > 8)
		{
			Location loc;
			if(sender instanceof BlockCommandSender)
			{
				loc = ((BlockCommandSender) sender).getBlock().getLocation();
			}else if(sender instanceof Player){
				loc = ((Player) sender).getLocation();
			}else return result(sender, "ゲーム内よりコマンドを実行してください");
			if(tryParse(Arrays.copyOf(args, 9)))return result(sender, ChatColor.RED + "[kameplayer] 正しい座標を入力してください");
			Location For = getLocation(loc.clone(), Arrays.copyOfRange(args, 0, 3));//コピー後
			Location To   = getLocation(loc.clone(), Arrays.copyOfRange(args, 3, 6));//コピー対角
			Location From  = getLocation(loc.clone(), Arrays.copyOfRange(args, 6, 9));//原点
			listReset();
			if(args.length > 9){
				inv = false;
				for(String name : Arrays.copyOfRange(args, 9, args.length)) {
					if(name.startsWith("!")) {
						inv = true;
						if(getMaterial(name.substring(1)) == null)return result(sender, ChatColor.RED + "[kameplayer] ブロックが不正です");
					}else {
						if(getMaterial(name) == null)return result(sender, ChatColor.RED + "[kameplayer] ブロックが不正です");
					}
				}
				if(inv && id.size() > 1) {
					sender.sendMessage("§b[kame.] §a複数選択されているので反転すべてに適用されます size= " + id.size());
				}
			}
			String from = positionBuilder(For.getBlockX(), For.getBlockY(), For.getBlockZ());
			String to = positionBuilder(To.getBlockX() - For.getBlockX(), To.getBlockY() - For.getBlockY(), To.getBlockZ() - For.getBlockZ());
			String pos = positionBuilder(From.getBlockX(), From.getBlockY(), From.getBlockZ());
			sender.sendMessage("§b[kame.] " + from + " §aから§b " + to + " §aブロックを§b " + pos + " §aより複製します");
			for(Material material : materials) {
				if(!inv && material != null)sender.sendMessage("§b[kame.] option §a" + material.name());
				if( inv && material != null)sender.sendMessage("§b[kame.] option §cinverse §a" + material.name());
			}
			blocks = 0;
			Timer.timeset("Copy");
			blockset(loc, For, To, From);
			sender.sendMessage(ChatColor.AQUA + "[kame.] §aブロックを複製しました " + (Timer.gettime("Copy")/1000f) + "[s] §e" + blocks + ".blocks");
		}
		return false;
	}

	private void blockset(Location loc, Location l1, Location l2, Location l3) {
		World world = loc.getWorld();
		int dx = updown(l1.getBlockX(), l2.getBlockX());
		int dy = updown(l1.getBlockY(), l2.getBlockY());
		int dz = updown(l1.getBlockZ(), l2.getBlockZ());
		int xx = l2.getBlockX() - l1.getBlockX();
		int yy = l2.getBlockY() - l1.getBlockY();
		int zz = l2.getBlockZ() - l1.getBlockZ();
			for(int y = 0;; y +=dy) {
				if((y + l3.getBlockY()) / 255 == 0)
				for(int x = 0;; x +=dx) {
					for(int z = 0;; z +=dz) {
						if(id.size() == 0 ||
						( inv && !id.contains(world.getBlockAt(x, y, z).getType().ordinal())) ||
						(!inv &&  id.contains(world.getBlockAt(x, y, z).getType().ordinal()))){
							Utils.copyBlockEntity(world, x + l1.getBlockX(), y + l1.getBlockY(), z + l1.getBlockZ(),
														x + l3.getBlockX(), y + l3.getBlockY(), z + l3.getBlockZ());
							blocks++;
						}
					if(z == zz)break;
				}
				if(x == xx)break;
			}
			if(y == yy)break;
		}
	}

	private int updown(int i, int j) {
		if(i > j)return -1;
		if(i < j)return 1;
		else return 0;
	}
	private void listReset() {
		materials = new ArrayList<>();
		id = new ArrayList<>();
	}

	private Material getMaterial(String str) {

		Material m = Utils.parseMaterial(str);
		if(m == null)return null;
		id.add(m.ordinal());
		materials.add(m);
		return m;
	}

	private String positionBuilder(int... i) {
		return new StringBuilder().append(i[0]).append(", ").append(i[1]).append(", ").append(i[2]).toString();
	}
	private Location getLocation(Location loc, String... args) {
		if(!args[0].matches("^~?-?[0-9]+") || !args[1].matches("^~?-?[0-9]+") || !args[2].matches("^~?-?[0-9]+"))return null;
		loc.setX(parse(args[0], loc.getBlockX()));
		loc.setY(parse(args[1], loc.getBlockY()));
		loc.setZ(parse(args[2], loc.getBlockZ()));
		return loc;
	}

	private boolean tryParse(String... str){
		for(String i : str){
			if(!i.matches("^-?+[0-9]+"))return true;
		}
		return false;
	}

	private Integer parse(String str, int vec) {
		try{
			return str.contains("~") ? vec + Integer.parseInt(str.replaceAll("~", "")) : Integer.parseInt(str);
		}catch(Exception e){return null;}
	}
	private boolean result(CommandSender sender, String str) {
		sender.sendMessage(str);
		return false;
	}


}
