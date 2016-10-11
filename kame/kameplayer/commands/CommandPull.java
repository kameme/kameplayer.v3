package kame.kameplayer.commands;

import kame.kameplayer.baseutils.Utils;
import kame.kameplayer.baseutils.Utils.Fail;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import com.google.common.base.Joiner;

public class CommandPull implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {
		//pull <player> item amount data {tag}
		if(!Utils.hasPermCommand(sender, cmd.getName()))return Utils.sendFailParm(sender);
		if(args.length < 2)return Utils.sendFailCommand(sender, Fail.LowParam, "/pull <player> item amount data {tag} (-stack)");
		Player player = Bukkit.getPlayer(args[0]);
		if(player == null)return Utils.sendFailCommand(sender, Fail.NoPlayer, args[0]);
		Material material = Utils.parseMaterial(args[1]);
		if(material == null)return Utils.sendFailCommand(sender, Fail.NoItem, args[1]);
		int amount = 1, data = 0;
		if(args.length > 2 && args[2].matches("[0-9]+"))amount = Integer.parseInt(args[2]);
		if(args.length > 3 && args[3].matches("[0-9]+"))data = Integer.parseInt(args[3]);
		ItemStack item = new ItemStack(material, amount);
		item.setDurability((short) data);
		if(args.length > 4 && args[4].startsWith("{")) {
			item = Utils.setNBTTag(sender, item, Joiner.on(" ").join(args).replaceAll("<player>", player.getName()).split(" "), 4);
			if(item == null)return Utils.sendFailCommand(sender, Fail.Other, "データタグが正しくありません");
		}
		PlayerInventory inv = player.getInventory();
		ItemStack invitem;
		int max = 64;
		if(args[args.length-1].equals("-stack"))max = item.getMaxStackSize();
		ItemStack base = item.clone();
		base.setAmount(1);
		for(int i=0;i<36 && amount > 0;i++) {
			invitem = inv.getItem(i);
			if(invitem == null) {
				if(amount > max)item.setAmount(max);
				else item.setAmount(amount);
				amount -= max;
				inv.setItem(i, item);
			}else {
				ItemStack buf = invitem.clone();
				buf.setAmount(1);
				if(base.equals(buf)) {
					int ammo = invitem.getAmount();
					if(max <= ammo)continue;
					if(amount > max || amount + ammo > max)item.setAmount(max);
					else item.setAmount(amount + ammo);
					amount -= max - ammo;
					inv.setItem(i, item);
				}
			}
		}
		for(;amount > 0;amount--) {
			item.setAmount(1);
			Item drop = player.getWorld().dropItem(player.getEyeLocation(), item);
			drop.setVelocity(new Vector(0, 0, 0));
			drop.setPickupDelay(0);
		}
		return true;
	}

}
