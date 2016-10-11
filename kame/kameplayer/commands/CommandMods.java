package kame.kameplayer.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import kame.kameplayer.baseutils.Utils;
import kame.kameplayer.baseutils.Utils.Fail;

public class CommandMods implements CommandExecutor {
	public static Map<String, List<String>> mod = new HashMap<>();


	@Override
	public boolean onCommand(CommandSender sender, Command label, String cmd, String[] args) {
		if(args.length > 0) {
			Player player = Bukkit.getPlayer(args[0]);
			if(player == null)return Utils.sendFailCommand(sender, Fail.NoPlayer, args[0]);
			sender.sendMessage("§b" + player.getDisplayName() + "§fさんのMOD[§6" + mod.get(player.getName()) + "§f]");
		}
		return true;
	}

}
