package kame.kameplayer.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.base.Joiner;

import kame.kameplayer.baseutils.Utils;
import kame.kameplayer.baseutils.Utils.Fail;

public class CommandItemFix implements CommandExecutor {

	private Player player;
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {
		// itemfix player addlore|setlore(-line=fix)|removelore|addname|setname|removename| item data ~~~~
		//																						-で1無効
		if(!Utils.hasPermCommand(sender, cmd.getName()))return Utils.sendFailParm(sender);
		if(args.length > 4) {
			player = Bukkit.getPlayer(args[0]);
			if(player == null)return Utils.sendFailCommand(sender, Fail.NoPlayer, args[0]);
			String arg = replacement(Joiner.on(' ').join(Arrays.copyOfRange(args, 4, args.length)));
			int j = parse(args[1].replaceFirst(".*-line=", ""), 0);
			if(args[2].equals("inhand")) {
				PlayerInventory inv = player.getInventory();
				inv.setItem(inv.getHeldItemSlot(), setItems(inv.getItem(inv.getHeldItemSlot()), args[1], j, arg));
				player.updateInventory();
				return true;
			}
			Material material = Utils.parseMaterial(args[2]);
			short data = Utils.getDurability(args[3]);
			if(material == null)return Utils.sendFailCommand(sender, Fail.NoItem, args[2]);
			PlayerInventory inv = player.getInventory();
			ItemStack item;
			for(int i=0;i<inv.getSize();i++){
				item = inv.getItem(i);
				if(item != null && (item.getType().equals(material) || material.equals(Material.AIR)) && (data == -1 || item.getDurability() == data)) {
					inv.setItem(i, setItems(item, args[1], j, arg));
				}
			}
			player.updateInventory();
			return false;

		}
		return Utils.sendFailCommand(sender, Fail.LowParam, "itemfix player option item data ~~~~", "addlore|setlore(-line=fix)|removelore|addname|setname|removename|");
	}

	private ItemStack setItems(ItemStack item, String name, int line, String arg) {
		ItemMeta im = item.getItemMeta();

		switch(name.replaceFirst("-line=[0-9]+", "")) {
		case "addlore":
			item.setItemMeta(addLore(im, arg, line));
			break;
		case "setlore":
			item.setItemMeta(setLore(im, arg, line));
			break;
		case "removelore":
			item.setItemMeta(removeLore(im, arg, line));
			break;
		case "addname":
			item.setItemMeta(addName(im, arg));
			break;
		case "setname":
			item.setItemMeta(setName(im, arg));
			break;
		case "removename":
			item.setItemMeta(removeName(im, arg));
			break;
		}
		return item;
	}

	private ItemMeta setName(ItemMeta im, String name) {
		im.setDisplayName(name);
		return im;
	}

	private ItemMeta addName(ItemMeta im, String name) {
		String line = "";
		if(im.hasDisplayName())line = im.getDisplayName();
		im.setDisplayName(line.concat(name));
		return im;
	}

	private ItemMeta removeName(ItemMeta im, String name) {
		String line = "";
		if(im.hasDisplayName())line = im.getDisplayName();
		im.setDisplayName(line.replaceAll(name, ""));
		return im;
	}

	private ItemMeta setLore(ItemMeta im, String name, int i) {
		List<String> lore = new ArrayList<String>();
		if(im.hasLore())lore = im.getLore();
		if(lore.size() > i)lore.set(i, name);
		else lore.add(name);
		im.setLore(lore);
		return im;
	}

	private ItemMeta addLore(ItemMeta im, String name, int i) {
		List<String> lore = new ArrayList<String>();
		if(im.hasLore())lore = im.getLore();
		lore.add(name);
		im.setLore(lore);
		return im;
	}

	private ItemMeta removeLore(ItemMeta im, String name, int i) {
		List<String> lore = new ArrayList<String>();
		if(im.hasLore())lore = im.getLore();
		lore.remove(i);
		im.setLore(lore);
		return im;
	}
	private String replacement(String str) {
		return str.replaceAll("&", ChatColor.COLOR_CHAR+"").replaceAll("<player>", player.getName());
	}
	private int parse(String str, int i) {
		try{
			int j = Integer.valueOf(str);
			if(j<i)return i;
			return j;
		}
		catch(Exception e){return i;}
	}
}
