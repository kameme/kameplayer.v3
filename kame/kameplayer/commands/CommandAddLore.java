package kame.kameplayer.commands;

import java.util.ArrayList;
import java.util.List;

import kame.kameplayer.Main;
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
import org.bukkit.inventory.meta.ItemMeta;

public class CommandAddLore implements CommandExecutor {

	@Deprecated
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		sender.sendMessage("[kameplayer] 非推奨コマンド");
		if(!Utils.hasPermCommand(sender, "addlore"))return Utils.sendFailParm(sender);
		{
			if(args.length == 4)
			{
				Player player = Bukkit.getPlayer(args[0]);
				Material material = Bukkit.getUnsafe().getMaterialFromInternalName(args[1]);
				if(player == null)return Utils.sendFailCommand(sender, Fail.NoPlayer, args[0]);

				if(material == null)return Utils.sendFailCommand(sender, Fail.NoItem, args[1]);
				PlayerInventory inv = player.getInventory();
				ItemStack item;
				int j = parse(args[2], -1), items = 0;
				String str = args[3].replaceAll("<player>", player.getName());
				for(int i=0;i<inv.getSize();i++){
					item = inv.getItem(i);
					if(item != null && item.getType().equals(material) && (j == -1 || item.getDurability() == j))
					{
						ItemMeta im = item.getItemMeta();
						List<String> lore = new ArrayList<String>();
						if(im.hasLore())lore = im.getLore();
						lore.add(str);
						im.setLore(lore);
						item.setItemMeta(im);
						inv.setItem(i, item);
						items++;
					}
				}
				if(items > 0)
				{
					if(Main.isDebug())sender.sendMessage(new StringBuilder(player.getName()).append("のインベントリに").append(items).append("スタック見つかりました").toString());
					return true;
				}
				if(Main.isDebug())sender.sendMessage(new StringBuilder(player.getName()).append("の持ち物にアイテムが見つかりませんでした").toString());
				return false;
			}
			return Utils.sendFailCommand(sender, Fail.LowParam, "/addlore <player> material (data) lore");
		}
	}
	private int parse(String str, int i)
	{
		try{
			int j = Integer.valueOf(str);
			if(j<i)return i;
			return j;
		}
		catch(Exception e){return i;}
	}

}
