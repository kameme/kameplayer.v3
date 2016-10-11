package kame.kameplayer.commands;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Joiner;

import kame.kameplayer.Main;
import kame.kameplayer.baseutils.Utils;
import kame.kameplayer.baseutils.Utils.Fail;


public class CommandItemRun implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command label, String cmd, String[] args) {


		//             0     1    2    3            4           5
		//itemrun <player> item data amount (consume|keep)-p? {NBT} "/command" : "/command"
		//                     メタ値  数  アイテム保持か消費か
		if(!Utils.hasPermCommand(sender, label.getName()))return Utils.sendFailParm(sender);
		if(args.length < 5) return Utils.sendFailCommand(sender, Fail.LowParam, "/itemrun <player> item data amount (consume|keep)-p? {NBT} \"/command\" \"/failcommand\"");
		Player player = Bukkit.getPlayer(args[0]);
		if(player == null)return Utils.sendFailCommand(sender, Fail.NoPlayer, args[0]);
		Material material = Utils.parseMaterial(args[1]);
		int deldata = -1, delamount = 0;
		if(args.length > 2)deldata = parse(args[2], -1);
		if(args.length > 3)delamount = parse(args[3], 0);
		if(deldata < -1)return Utils.sendFailCommand(sender, Fail.NoItem, args[2]);
		if(delamount < -1)return Utils.sendFailCommand(sender, Fail.NoItem, args[3]);

		ItemStack bufferitem;
		if(material != null)bufferitem = new ItemStack(material);
		else bufferitem = new ItemStack(Material.STONE);
		String tag = "", tagraw = "";
		int type = 0;
		if(args.length > 4) {
			if(args[4].startsWith("keep"))type++;
			if(args[4].endsWith("-p"))type+=2;
		}
		if(args.length > 5 && args[5].startsWith("{")) {
			bufferitem = Utils.setNBTTag(player, bufferitem, args, 5);
			tag = Utils.getNBTTag(bufferitem);
			tagraw = Utils.getNBTTag(args, 5);
			if(tag.length() < 1) return Utils.sendFailCommand(sender, Fail.Other, "データタグが正しくありません");

		}
		PlayerInventory inv = player.getInventory();
		int itemamount = 0;
		for(ItemStack item : inv)if(item != null && item.getType() == material &&
				(deldata == -1 || item.getDurability() == deldata) && (tag.length() == 0 || tagEquals(tag.replaceAll("<player>", player.getName()), Utils.getNBTTag(item)))){
			itemamount+=item.getAmount();
		}
		String command = Joiner.on(" ").join(Arrays.copyOfRange(args, 5, args.length))
				.substring(tagraw.length()).replaceFirst(" */", "")
					.replaceAll("<player>", player.getName())
					.replaceAll("<count>",  Integer.toString(itemamount))
					.replaceAll("<level>",  Integer.toString(player.getLevel())
					.replaceAll("<playerX>",Integer.toString(player.getLocation().getBlockX()))
					.replaceAll("<playerY>",Integer.toString(player.getLocation().getBlockY()))
					.replaceAll("<playerZ>",Integer.toString(player.getLocation().getBlockZ())));
		String[] cmds = new String[]{command};
		Pattern p = Pattern.compile(" (:|;) /");
		Matcher m = p.matcher(command);
		if(m.find()) {
			cmds = p.split(command);
			if(m.group(1).equals(";")) {
				String buf = cmds[0];
				cmds[0] = cmds[1];
				cmds[1] = buf;
			}
		}
		if(Main.isDebug()) {
			sender.sendMessage("pathcmd:[" + cmds[0] + "]");
			if(cmds.length > 1)sender.sendMessage("pathcmd:[" + cmds[1] + "]");
		}
		if(delamount <= itemamount) {
			sender.sendMessage(ChatColor.AQUA + player.getName() + "の手持ちに" + itemamount + "個見つかりました");
			if(type%2 == 0) {
				int i = 0;
				for(ItemStack item : inv){
					if(item != null && item.getType() == material && (deldata == -1 || item.getDurability() == deldata) && (tag.length() == 0 || tagEquals(tag.replaceAll("<player>", player.getName()), Utils.getNBTTag(item)))) {
						int amount = item.getAmount();
						if(amount <= delamount) {
							inv.setItem(i, new ItemStack(Material.AIR));
						}
						else {
							item.setAmount(amount - delamount);
						}
						delamount-=amount;
						if(delamount <= 0)break;
					}
					i++;
				}


			}
			if(cmds[0].matches(" *"))return true;
			String[] run = cmds[0].split(" \\| /");
			for(String line : run) {
				runCommand(sender, player, line.replaceAll("\\|", "|"), type < 2);
			}
			return true;
		}else {
			sender.sendMessage(ChatColor.RED + player.getName() + "は指定した数値以上のアイテムを持っていませんでした:"+itemamount);
			if(cmds.length == 1 || cmds[1].matches(" *"))return false;
			String[] run = cmds[1].split(" \\| /");
			for(String line : run) {
				runCommand(sender, player, line.replaceAll("\\|", "|"), type < 2);
			}
			return false;
		}

	}

	private boolean tagEquals(String base, String tags) {
		char[] c1 = base.toCharArray();
		char[] c2 = tags.toCharArray();
		int i = c1.length, j = c2.length;
		while(i != 0 && j != 0) {
			if(c1[--i] != c2[--j]) {
				if(c1[i] == '*') {
					while(j != 0 && c1[i-1] != c2[--j]);
					j++;
				}else {
					return false;
				}
			}
		}
        return i == j;
	}
	private void runCommand(final CommandSender sender, final Player player, final String line, final boolean isConsole) {
		Matcher m = Pattern.compile("delay:([0-9]+)/.*").matcher(line);
		if(m.find()) {
			new BukkitRunnable() {
				public void run() {
					runs(sender, player, line.replaceFirst("delay:[0-9]+/", ""), isConsole);
				}
			}.runTaskLater(Main.getPlugin(), Integer.parseInt(m.group(1)));
		}else {
			runs(sender, player, line, isConsole);
		}
	}


	private void runs(CommandSender sender, Player player, String line, boolean isConsole) {
		Matcher m = Pattern.compile("<score_([^>]+)>").matcher(line);
		while(m.find()) {
			try {
				line = m.replaceFirst(Integer.toString(player.getScoreboard().getObjective(m.group(1)).getScore(player.getName()).getScore()));
			}catch(NullPointerException e) {
				line = m.replaceFirst("0");
			}
		}
		m = Pattern.compile("==[0-9\\(\\)\\+\\*/-]+").matcher(line);
		while(m.find()) {
			line = line.replaceFirst("==[0-9\\(\\)\\+\\*/-]+", test(sender, m.group().replaceFirst("==", "")));
		}
		if(Main.isDebug())sender.sendMessage(line);
		if(isConsole) {
			Bukkit.dispatchCommand(sender, line);
		}else if(player.isOp()) {
			player.performCommand(line);
		}else {
			player.setOp(true);
			player.performCommand(line);
			player.setOp(false);
		}
	}
	private int parse(String str, int def) {
		str = str.replace("~","");
		return Utils.parse(str, def);
	}
	private String test(CommandSender sender, String str) {
		try {
			ScriptEngineManager factory = new ScriptEngineManager();
			ScriptEngine engine = factory.getEngineByName("JavaScript");
			str = ((Integer)engine.eval(str)).toString();
			} catch (ScriptException e) {
				sender.sendMessage("§c[kames.] 演算に失敗ました " + str);
			}
		return str;
	}

}
