package kame.kameplayer.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kame.kameplayer.baseutils.Utils;
import kame.kameplayer.baseutils.Utils.Fail;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class CommandEntity implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if(!Utils.hasPermCommand(sender, cmd.getName()))return Utils.sendFailParm(sender);
		if(args.length >= 3)
		{
			EntityType type = null;
			float r =  parse(args[2], 0f);
			try {
				if(args.length > 3)type = EntityType.valueOf(args[3].toUpperCase());
			}catch(Exception e) {
				return Utils.sendFailCommand(sender, Fail.NoEntity, args[3]);
			}
			Location loc;
			if(sender instanceof Player) {
				loc =((Player) sender).getLocation();
			}else
			if(sender instanceof BlockCommandSender) {
				loc = ((BlockCommandSender) sender).getBlock().getLocation();
			}else {
				sender.sendMessage("ワールド内から実行してください");
				return false;
			}
			if(args[1].startsWith("@b") && sender instanceof BlockCommandSender) {
				loc = ((BlockCommandSender)sender).getBlock().getLocation();
				Matcher m;
				m = Pattern.compile("x=(-?[0-9]+)").matcher(args[0]);
				if(m.find())loc.setX(Integer.parseInt(m.group(1)));
				m = Pattern.compile("y=(-?[0-9]+)").matcher(args[0]);
				if(m.find())loc.setY(Integer.parseInt(m.group(1)));
				m = Pattern.compile("z=(-?[0-9]+)").matcher(args[0]);
				if(m.find())loc.setZ(Integer.parseInt(m.group(1)));
				m = Pattern.compile("r=(-?[0-9]+)").matcher(args[0]);
				if(m.find())r = Integer.parseInt(m.group(1));
				loc.add(0.5, 0.5, 0.5);
			}else {
				Player player = Bukkit.getPlayer(args[1]);
				if(player == null)return Utils.sendFailCommand(sender, Fail.NoPlayer, args[0]);
				loc = player.getLocation();
			}
			if(args[0].equals("remove")) {
				//entity remove player distance type
				for(Entity entity : loc.getWorld().getNearbyEntities(loc, r, r, r))if(!(entity instanceof Player)){
					if(type == null || entity.getType().equals(type))entity.remove();
				}
				return true;
			}
			if(args[0].equals("damage")) {
				//entity damage player distance type amount toplayer
				Player p = null;
				for(Entity entity : loc.getWorld().getNearbyEntities(loc, r, r, r))if(entity instanceof Player) {
					p = (Player) entity;
					break;
				}
				double damage = 0;
				if(args.length > 4)damage = Utils.parse(args[4], 0);
				if(args.length > 5) {
					Player pl = Bukkit.getPlayer(args[5]);
					if(pl != null)p = pl;
				}
				for(Entity entity : loc.getWorld().getNearbyEntities(loc, r, r, r))if(!(entity instanceof Player) && (entity instanceof LivingEntity)){
					if(type == null || entity.getType().equals(type))((LivingEntity) entity).damage(damage, p);
				}
				return true;
			}
		}
		return Utils.sendFailCommand(sender, Fail.LowParam, "/entity remove <player> <radius> <type>" + "/entity damage <player> <radius> <type> amount <toplayer>");
	}
	public float parse(String str, float def)
	{
		return Utils.parseRaw(str, def);
	}
	public double parse(String str, double vec)
	{
		return Utils.parse(str, vec);
	}
}
