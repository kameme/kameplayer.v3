package kame.kameplayer.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;

import kame.kameplayer.baseutils.Utils;
import kame.kameplayer.baseutils.Utils.Fail;

public class CommandTrig implements CommandExecutor {


	public boolean onCommand(CommandSender sender, Command label, String cmd, String[] args) {
		//trig @p amount
		if(!Utils.hasPermCommand(sender, label.getName()))return Utils.sendFailParm(sender);
		if(args.length == 0)return Utils.sendFailCommand(sender, Fail.LowParam, "/" +cmd + " <player> amount");
		Player player = Bukkit.getPlayer(args[0]);
		if(player == null)return Utils.sendFailCommand(sender, Fail.NoEntity, args[0]);
		Objective obj = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("trig");
		if(obj == null)obj = Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective("trig", "");
		int amount = 0;
		if(args.length > 1)amount = Utils.parse(args[1], 0);
		obj.getScore(player.getName()).setScore(amount);
		return true;
	}

}
