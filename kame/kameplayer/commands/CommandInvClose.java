package kame.kameplayer.commands;

import kame.kameplayer.baseutils.Utils;
import kame.kameplayer.baseutils.Utils.Fail;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandInvClose implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {
		//invclose @p
		if(!Utils.hasPermCommand(sender, cmd.getName()))return Utils.sendFailParm(sender);
		if(args.length == 0)return Utils.sendFailCommand(sender, Fail.LowParam, "/" +command + " <player>");
		Player player = Bukkit.getPlayer(args[0]);
		if(player == null)return Utils.sendFailCommand(sender, Fail.NoEntity, args[0]);
		player.closeInventory();
		return false;
	}

}
