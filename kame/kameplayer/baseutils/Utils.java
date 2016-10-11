package kame.kameplayer.baseutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Joiner;

import kame.kameplayer.Main;
import kame.kameplayer.baseutils.mnsver.Vx_x_Rx;

public class Utils {

	/**
	 * エンティティの処理
	 * @param e
	 */
	public static void killed(Entity e) {
		for(Entities entity : Timer.entities)if(entity.getEntity().equals(e)) {
			CommandSender sender = entity.getSender();
			String cmd = entity.getCommand().replaceFirst("/", "");
			Bukkit.dispatchCommand(sender, cmd);
			Timer.entities.remove(entity);
			return;
		}
	}

	/**
	 * パーミッション、コマンド権限
	 * @param sender
	 * @param perm
	 * @return
	 */
	public static boolean hasPermCommand(CommandSender sender, String perm) {
		return sender.isOp() || sender.hasPermission("kameplayer.commands." + perm);
	}

	/**
	 * パーミッション、ユーザー権限
	 * @param sender
	 * @param perm
	 * @return
	 */
	public static boolean hasPermUse(CommandSender sender, String perm) {
		return sender.isOp() || sender.hasPermission("kameplayer.use." + perm);
	}

	/**
	 * アイテムパース
	 * 対応:ID,Name,InternalName
	 * @param name
	 * @return
	 */
	public static Material parseMaterial(String name) {
		if(name.equals("-") || name.equalsIgnoreCase("minecraft:air"))return Material.AIR;
		@SuppressWarnings("deprecation")
		Material material = Bukkit.getUnsafe().getMaterialFromInternalName(name);
		if(material.equals(Material.AIR))material = Material.matchMaterial(name);
		return material;
	}

	public static short getDurability(String str) {
		return str.matches("[0-9]+") ? Short.parseShort(str) : -1;
	}

	private static Vx_x_Rx convert = Main.obj;
	@Deprecated
	public static ItemStack setNBTTag(CommandSender sender, ItemStack bufferitem, String[] args) {
		try {
			int i;
			for(i = 4; args.length > i && !args[i].endsWith("}"); i++);
			bufferitem = Bukkit.getUnsafe().modifyItemStack(
					bufferitem,Joiner.on(' ').join(Arrays.asList(args).subList(4, i+1)));
		} catch (Exception e) {
			return null;
		}
		return bufferitem;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack setNBTTag(CommandSender sender, ItemStack bufferitem, String[] args, int i) {
		int j = i;
		try {
			while(args.length > j && !args[j++].endsWith("}"));
			bufferitem = Bukkit.getUnsafe().modifyItemStack(
					bufferitem,Joiner.on(' ').join(Arrays.asList(args).subList(i, j)));
		} catch (Exception e) {
			return null;
		}
		return bufferitem;
	}
	public static void copyBlockEntity(World world, int x, int y, int z, int i, int j, int k) {
		convert.copyBlockEntity(world, x, y, z, i, j, k);
	}
	public static String getNBTTag(String[] args, int i) {
		int j = i;
		while(args.length > j && !args[j++].endsWith("}"));
		return Joiner.on(' ').join(Arrays.asList(args).subList(i, j));
	}
	public static String getNBTTag(ItemStack bufferitem) {
		return bufferitem == null || bufferitem.getType().equals(Material.AIR) ? "" : convert.getNBTTag(bufferitem);
	}

	public static void sendPacket(Player player, String packet, float x, float y, float z, float xt, float yt, float zt, float speed, int amount) {
		convert.sendPacket(player, packet, x, y, z, xt, yt, zt, speed, amount);
	}

	public static void sendRespawn(Player player) {
		convert.sendRespawn(player);
	}

	public static void sendBarMessage(Player player, String msg) {
		convert.sendBarMessage(player, msg);
	}
	public static Entity getNBTEntity(CommandSender sender, Location loc, String[] args) {
		args[0] = StringUtils.capitalize(args[0]);
		return convert.getNBTEntity(sender, loc, args);
	}
	public static List<String> getParticles() {
		return convert.getParticles();
	}

	public static double parseRaw(String str, double vec) {
		try{
			return str.charAt(0) == '~' ? vec + Double.parseDouble(str.substring(1)) : Double.parseDouble(str);
		}catch(Exception e){
			return vec;
		}

	}

	public static float parseRaw(String str, float vec) {
		try{
			return str.charAt(0) == '~' ? vec + Float.parseFloat(str.substring(1)) : Float.parseFloat(str);
		}catch(Exception e){
			return vec;
		}
	}

	public static int parse(String str, int vec) {
		try{
			return str.charAt(0) == '~' ? vec + Integer.parseInt(str.substring(1)) : Integer.parseInt(str);
		}catch(Exception e){
			return vec;
		}
	}

	public static double parse(String str, double vec) {
		try{
			return str.charAt(0) == '~' ? vec + Double.parseDouble(str.substring(1)) : 
				str.contains(".") ? Double.parseDouble(str) : Integer.parseInt(str) + 0.5f;
		}catch(Exception e){
			return vec;
		}
	}

	public static float parse(String str, float vec) {
		try{
			return str.charAt(0) == '~' ? vec + Float.parseFloat(str.substring(1)) : 
				str.contains(".") ? Float.parseFloat(str) : Integer.parseInt(str) + 0.5f;
		}catch(Exception e){
			return vec;
		}
	}
	public enum Fail {
		/**
		 * パラメータの値が少ないときに使用する 後ろの値はそのまま改行されて出力される
		 * @display ChatColor.RED [kame.] パラメーターが足りません！
		 */
		LowParam, 
		/**
		 * パラメータの値が多いときに使用する 後ろの値はそのまま改行されて出力される
		 * @display ChatColor.RED [kame.] パラメーターが多すぎます！
		 */
		UpParam,
		/**
		 * 指定したプレイヤーが存在しない時に使用する 後ろの値は指定名として使用される
		 * @display ChatColor.RED [kame.] そのエンティティは存在しません！
		 * ChatColor.RED [kame] [args] is offline!
		 */
		NoPlayer,
		/**
		 * 指定したエンティティが存在しない時に使用する 後ろの値は指定名として使用される
		 * @display ChatColor.RED [kame.] パラメーターが足りません！
		 */
		NoEntity,
		/**
		 * 指定したアイテムが存在しない時に使用する 後ろの値は指定名として使用される
		 * @display ChatColor.RED [kame.] そのエンティティは存在しません！！
		 * ChatColor.RED [kame] [args] is unknown!
		 */
		NoItem,
		/**
		 * パラメータの値が数字でない時に使用する 後ろの値はそのまま改行されて出力される
		 * @display ChatColor.RED [kame.] そのアイテムは存在しません！！
		 * ChatColor.RED [kame] [args] is unknown!
		 */
		NoNumber,
		/**
		 * その他の不明なエラー時に使用する 後ろの値はそのまま改行されて出力される
		 * @display ChatColor.RED [kame.] コマンドを実行できませんでした
		 */
		Other
	}

	public static boolean sendFailCommand(CommandSender sender, Fail type, String... args) {
		switch(type) {
		case LowParam:
			sender.sendMessage(ChatColor.RED + "[kame.] パラメーターが足りません！");
			break;
		case NoEntity:
			sender.sendMessage(ChatColor.RED + "[kame.] そのエンティティは存在しません！");
			if(args.length > 0)sender.sendMessage(ChatColor.RED + "[kame] " + String.join(" ", args) + " is unknown!");
			return false;
		case NoNumber:
			sender.sendMessage(ChatColor.RED + "[kame.] 指定された数値が正しくありません！");
			break;
		case NoItem:
			sender.sendMessage(ChatColor.RED + "[kame.] そのアイテムは存在しません！");
			if(args.length > 0)sender.sendMessage(ChatColor.RED + "[kame] " + String.join(" ", args) + " is unknown!");
			return false;
		case NoPlayer:
			sender.sendMessage(ChatColor.RED + "[kame.] そのプレイヤーは存在しないかオフラインです！");
			if(args.length > 0)sender.sendMessage(ChatColor.RED + "[kame] " + String.join(" ", args) + " is now offline!");
			return false;
		case Other:
			sender.sendMessage(ChatColor.RED + "[kame.] コマンドを実行できませんでした");
			break;
		case UpParam:
			sender.sendMessage(ChatColor.RED + "[kame.] パラメーターが多すぎます！");
			break;
		default:
			break;
		}
		for(String line : args)sender.sendMessage(ChatColor.RED + line);
		return false;
	}

	public static Collection<? extends String> completer(String cmd, Collection<? extends Player> args) {
		List<String> completion = new ArrayList<String>();
		for(Player text : args)if(text.getName().startsWith(cmd))completion.add(text.getName());
		return completion;
	}
	public static Collection<? extends String> completer(String cmd, String... args)
	{
		List<String> completion = new ArrayList<String>();
		for(String text : args)if(text.startsWith(cmd))completion.add(text);
		return completion;
	}
	public static boolean sendFailParm(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "[kame.] 権限がないので実行できません");
		return false;
	}




}
