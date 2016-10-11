package kame.kameplayer.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kame.kameplayer.baseutils.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

public class CommandCommandBlock implements CommandExecutor, TabCompleter {

	String perm = "kameplayer.command.commandblock.";
	@Override
	public boolean onCommand(CommandSender sender, Command label, String cmd, String[] args) {
		if(!(sender instanceof Player))return false;
		Player player = ((Player)sender);
		if(!Utils.hasPermCommand(sender, "comandblock.entry"))return Utils.sendFailParm(sender);
		Location loc = player.getEyeLocation();
		if(args.length == 0) {
			loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.COMMAND, 1)).setPickupDelay(0);
			player.sendMessage("[cm] " + cmd + " の後ろに続けて[add,set,remove,clear] + 入力したいコマンドを入力して実行してください");
			return true;
		}
		if(args.length > 0) {
			Block block = getTargetBlock(player);
			if(block == null) {
				return result(sender, 2);
			}
			CommandBlock cb = (CommandBlock) block.getState();

			if(args[0].equals("set")) {
				if(args.length > 1) {
					if(player.hasPermission(perm + args[1]) || sender.isOp()) {
						String command = splitCommand(args).replaceFirst(" set ", "");
						return result(player, cb, command);
					}
					return result(sender, 0);
				}
				return result(sender, 1);
			}
			if(args[0].equals("add")) {
				if(args.length > 1) {
					String comm = cb.getCommand();
					if(comm.replaceAll(" ", "").length() == 0) {
						player.sendMessage("[cm] コマンドブロックの内容が空です");
						return false;
					}
					if(player.hasPermission(perm + comm.split(" ")[0])|| sender.isOp()) {
						String command = splitCommand(args).replaceFirst(" add", "");
						return result(player, cb, comm + command);
					}
					return result(sender, 0);
				}
				return result(sender, 1);
			}
			if(args[0].equals("clear")) {
				String comm = cb.getCommand();
				if(comm.replaceAll(" ", "").length() == 0) {
					player.sendMessage("[cm] コマンドブロックの内容が空です");
					return false;
				}
				if(player.hasPermission(perm + comm.split(" ")[0])|| sender.isOp()) {
					cb.setCommand("");
					cb.update();
					player.sendMessage("[cm] コマンドを削除しました");
					return true;
				}
				return result(sender, 0);
			}
			player.sendMessage("[cm] " + cmd + " の後ろに続けて[add,set,remove,clear] + 入力したいコマンドを入力して実行してください");
		}
		return false;
	}
	private String splitCommand(String[] args) {
		StringBuilder builder = new StringBuilder();
		for(String com : args)builder.append(" ").append(com);
		return builder.toString();
	}

	private boolean result(Player player, CommandBlock block, String cmd) {
		cmd = cmd.replaceAll("&", "§");
		block.setCommand(cmd);
		block.update();
		player.sendMessage("[cm] コマンドを書き込みました");
		player.sendMessage(cmd);
		return true;
	}

	private boolean result(CommandSender sender, int i) {
		switch (i){
		case 0:
			sender.sendMessage(ChatColor.RED + "[cm] このコマンドを実行する権限がないので書き込めませんでした");
			break;
		case 1:
			sender.sendMessage(ChatColor.RED + "[cm] コマンドを指定してください");
			break;
		case 2:
			sender.sendMessage(ChatColor.RED + "[cm] 書き込むコマンドブロックにカーソルを合わせてください");
			break;

		}
		return false;
	}

	private Block getTargetBlock(Player player) {
		BlockIterator it = new BlockIterator(player, 5);
		Set<Material> set = new HashSet<Material>();
		set.add(Material.AIR);
		set.add(Material.WATER);
		set.add(Material.LAVA);
		while (true) {
			if(!it.hasNext())return null;
			Block block = it.next();
			Material material = block.getType();
			if(!set.contains(material)) {
				if(material.equals(Material.COMMAND))return block;
				else return null;
			}

		}
	}
	@Override
	public List<String> onTabComplete(CommandSender sender, Command label, String cmd, String[] args) {

		if(args.length == 1) {
			List<String> list = new ArrayList<String>();
			for(String name : new String[]{"add", "set", "clear"}) {
				if(name.startsWith(args[0]))list.add(name);
			}
			return list;
		}
		return null;
	}


}
