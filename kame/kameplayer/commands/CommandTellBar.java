package kame.kameplayer.commands;

import java.util.Arrays;

import kame.kameplayer.baseutils.Utils;
import kame.kameplayer.baseutils.Utils.Fail;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

public class CommandTellBar implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {
		if(!Utils.hasPermCommand(sender, cmd.getName()))return Utils.sendFailParm(sender);
		if(args.length > 1) {
			Player player = Bukkit.getPlayer(args[0]);
			if(player == null)return Utils.sendFailCommand(sender, Fail.NoPlayer, args[0]);
			StringBuilder builder = new StringBuilder().append(getOption(args[1], player));
			if(args.length > 2)for(String message : Arrays.copyOfRange(args, 2, args.length))builder.append(" ").append(getOption(message, player));
			Utils.sendBarMessage(player, "{\"text\": \"" + JSONObject.escape(builder.toString()) + "\"}");
			return true;
		}
		return Utils.sendFailCommand(sender, Fail.LowParam, "/tellbar <player> text");
	}

	private String getOption(String arg, Player player) {
		switch(arg) {
		case "<player>":
			return player.getName();
		case "<playername>":
			return player.getDisplayName();
		}
		return arg;
	}

}
