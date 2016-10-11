package kame.kameplayer.commands;

import kame.kameplayer.baseutils.Utils;
import kame.kameplayer.baseutils.Utils.Fail;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class CommandArmor implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command label, String cmd, String[] args)
	{
		if(!Utils.hasPermCommand(sender, "armor"))return Utils.sendFailParm(sender);
		if(args.length == 1 || args.length == 2)
		{
			Player player = Bukkit.getPlayer(args[0]);
			if(player == null)return Utils.sendFailCommand(sender, Fail.NoPlayer, args[0]);
			boolean hold = false;
			PlayerInventory inv = player.getInventory();
			if(args.length == 2)
			{
				hold = true;
				if(args[1].equals("repair"))
				{
					if(inv.getHelmet() != null)inv.getHelmet().setDurability((short) 0);
					if(inv.getChestplate() != null)inv.getChestplate().setDurability((short) 0);
					if(inv.getLeggings() != null)inv.getLeggings().setDurability((short) 0);
					if(inv.getBoots() != null)inv.getBoots().setDurability((short) 0);
					sender.sendMessage("[kame.] 装備を修復しました");
					player.updateInventory();
					return true;
				}
			}
			ItemStack item;
			for(int i=0;i<36;i++) {
				String name = "";
				item = inv.getItem(i);
				if(item != null && (name =item.getType().toString()).contains("_")) {
					name = name.split("_")[1];
					if(setEquipment(inv, name, item) && !hold)inv.setItem(i, new ItemStack(Material.AIR));
				}
			}
			sender.sendMessage("[kame.] 装備を装着しました");
			player.updateInventory();
			return true;

		}
		return Utils.sendFailCommand(sender, Fail.LowParam, "/armor <player> (repair)");
	}


	private boolean setEquipment(PlayerInventory inv, String str, ItemStack item) {
		switch(str) {
		case "HELMET":
			if(inv.getHelmet() == null) {
				inv.setHelmet(item);return true;
			}return false;
		case "CHESTPLATE":
			if(inv.getChestplate() == null) {
				inv.setChestplate(item);return true;
			}return false;
		case "LEGGINGS":
			if(inv.getLeggings() == null) {
				inv.setLeggings(item);return true;
			}return false;
		case "BOOTS":
			if(inv.getBoots() == null) {
				inv.setBoots(item);return true;
			}return false;
		default:return false;
		}
	}

}
