package kame.kameplayer.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import kame.kameplayer.baseutils.ItemVacuum;
import kame.kameplayer.baseutils.Utils;
import kame.kameplayer.baseutils.Utils.Fail;

public class CommandVacume implements CommandExecutor {

	private Map<UUID, ItemVacuum> map = new HashMap<UUID, ItemVacuum>();
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {
		//vacuum [on|off|add|remove] [range|item]
		if(!Utils.hasPermCommand(sender, cmd.getName()))return Utils.sendFailParm(sender);
		if(!(sender instanceof Player))return false;
		if(args.length > 0 && args[0].equals("off"))
		{
			Player player = (Player) sender;
			if(map.containsKey(player.getUniqueId()))map.get(player.getUniqueId()).stop();
			sender.sendMessage(ChatColor.AQUA + "[kameplayer] 吸い寄せを無効にします");
		}
		if(args.length > 1)
		{
			Player player = (Player) sender;
			if(args[0].equals("on"))
			{
				if(map.containsKey(player.getUniqueId()))map.get(player.getUniqueId()).stop();
				map.put(player.getUniqueId(), new ItemVacuum(player, Utils.parse(args[1], 5)));
				sender.sendMessage(ChatColor.AQUA + "[kameplayer] 吸い寄せを有効にします");
			}
			if(args[0].equals("add"))
			{
				if(!map.containsKey(player.getUniqueId())){
					sender.sendMessage(ChatColor.RED + "[kameplayer] 吸い寄せが有効になっていません");
					return false;
				}
				Material item = Utils.parseMaterial(args[1]);
				if(item != null)map.get(player.getUniqueId()).addMaterial(item);
				sender.sendMessage(ChatColor.AQUA + "[kameplayer] " + ChatColor.GREEN + item.name() + ChatColor.AQUA + " をリストに登録しました");
			}
			if(args[0].equals("remove"))
			{
				if(!map.containsKey(player.getUniqueId())){
					sender.sendMessage(ChatColor.RED + "[kameplayer] 吸い寄せが有効になっていません");
					return false;
				}
				Material item = Utils.parseMaterial(args[1]);
				if(item != null)map.get(player.getUniqueId()).removeMaterial(item);
				sender.sendMessage(ChatColor.AQUA + "[kameplayer] " + ChatColor.GREEN + item.name() + ChatColor.AQUA + " をリストから削除しました");
			}
			return true;
		}

		return Utils.sendFailCommand(sender, Fail.LowParam, "/vacuum [on|off|add|remove] [range|item]");
	}
}
