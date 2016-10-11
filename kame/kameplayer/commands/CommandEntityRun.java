package kame.kameplayer.commands;

import kame.kameplayer.baseutils.Entities;
import kame.kameplayer.baseutils.Timer;
import kame.kameplayer.baseutils.Utils;
import kame.kameplayer.baseutils.Utils.Fail;

import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;


public class CommandEntityRun implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//cmdmonster entity x y z command
		//cm entity x y z
		if(!Utils.hasPermCommand(sender, cmd.getName()))return Utils.sendFailParm(sender);
		if(args.length > 0)
		{
			Location loc = null;
			if(sender instanceof BlockCommandSender)
			{
				loc = ((BlockCommandSender)sender).getBlock().getLocation();
				loc.add(0.5,0,0.5);
			}
			else if(sender instanceof Player)
			{
				loc = ((Player) sender).getLocation();
			}else {
				sender.sendMessage("ワールド内から送信してください");
				return false;
			}
			if(args.length > 3) {
				loc.setX(parse(args[1], loc.getX()));
				loc.setY(parse(args[2], loc.getY()));
				loc.setZ(parse(args[3], loc.getZ()));
			}
			Entity e = Utils.getNBTEntity(sender, loc, args);
			if(e == null)return Utils.sendFailCommand(sender, Fail.NoEntity, args[0]);
			StringBuilder str = new StringBuilder();
			int i = a(args);
			if(i > 0)for(int j = i; args.length > j; j++)str.append(args[j]).append(" ");
			try{
				String command =  str.toString();
				if(command.length() > 0) Timer.entities.add(new Entities(e, sender, command.replaceFirst("/", "")));
			}catch(Exception ex) {
				return Utils.sendFailCommand(sender, Fail.Other, "コマンドを確認してください");
			}
			return true;
		}
		return Utils.sendFailCommand(sender, Fail.LowParam, "/" + label + " <entity> x y z /[cmd]");
	}

	private int a(String[] args) {
		int i = 0;
		for(String str: args)
		{
			if(str.startsWith("/"))return i;
			i++;
		}
		return i;
	}

	public double parse(String str, double vec)
	{
		return Utils.parse(str, vec);
	}


}