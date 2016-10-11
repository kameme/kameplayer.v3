package kame.kameplayer.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import kame.kameplayer.baseutils.Utils;
import kame.kameplayer.baseutils.Utils.Fail;

public class CommandVec implements CommandExecutor {
	private static final String entities;
	static {
		StringBuilder b = new StringBuilder();
		for(EntityType e : EntityType.values()) {
			if(b.length() != 0)b.append("|");
			b.append(e.name().toLowerCase());
		}
		entities = "type=!?(" + b.append(")").toString();
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {
		if(!Utils.hasPermCommand(sender, cmd.getName()))return Utils.sendFailParm(sender);
		if(args.length > 2) {
			if(args.length == 3 && sender instanceof Player) {
				Player player = (Player) sender;
				player.setVelocity(new Vector(
						parse(args[0], (float)player.getVelocity().getX()),
						parse(args[1], (float)player.getVelocity().getY()),
						parse(args[2], (float)player.getVelocity().getZ())));
			}
			Entity player = Bukkit.getPlayer(args[0]);
			if(player == null && args[0].startsWith("@e")) {
				Location loc;
				int r = 100;
				if(sender instanceof BlockCommandSender) {
					loc = ((BlockCommandSender) sender).getBlock().getLocation();
				}else if(sender instanceof Player) {
					loc = ((Player)sender).getLocation();
				}else {
					sender.sendMessage("ワールド内から送信してください");
					return false;
				}
				
				
				Matcher m1 = Pattern.compile("x=(-?[0-9]+)").matcher(args[0]);
				Matcher m2 = Pattern.compile("y=(-?[0-9]+)").matcher(args[0]);
				Matcher m3 = Pattern.compile("z=(-?[0-9]+)").matcher(args[0]);
				Matcher m4 = Pattern.compile("r=(-?[0-9]+)").matcher(args[0]);
				Matcher m5 = Pattern.compile(entities).matcher(args[0].toLowerCase());
				String type = null;
				if(m1.find())loc.setX(Integer.parseInt(m1.group(1)));
				if(m2.find())loc.setY(Integer.parseInt(m2.group(1)));
				if(m3.find())loc.setZ(Integer.parseInt(m3.group(1)));
				if(m4.find())r = Integer.parseInt(m4.group(1));
				if(m5.find())type = m5.group(1);
				for(Entity e : loc.getWorld().getNearbyEntities(loc, r, r, r)){
					if(type == null || ((args[0].contains("type=!") && !type.equalsIgnoreCase(e.getType().toString()) || (!args[0].contains("type=!") && type.equalsIgnoreCase(e.getType().toString()))))) {
						if(args.length > 5 && args[1].equals("to")) {
						double length = parse(args[5], 1);
							Vector vec = new Vector(
									parse(args[2], (float)loc.getX()),
									parse(args[3], (float)loc.getY()),
									parse(args[4], (float)loc.getZ()));
							loc.setDirection(e.getLocation().toVector().subtract(vec));
							e.setVelocity(loc.getDirection().multiply(-length));
							return true;
						}
						e.setVelocity(new Vector(
							parse(args[1], (float)e.getVelocity().getX()),
							parse(args[2], (float)e.getVelocity().getY()),
							parse(args[3], (float)e.getVelocity().getZ())));
					}
				}
				return true;
			}
			if(player == null)return Utils.sendFailCommand(sender, Fail.NoPlayer, args[0]);
			if(args.length == 4) {
				player.setVelocity(new Vector(
					parse(args[1], (float)player.getVelocity().getX()),
					parse(args[2], (float)player.getVelocity().getY()),
					parse(args[3], (float)player.getVelocity().getZ())));
				return true;
			}
			if(args.length > 5 && args[1].equals("to")) {
				Location loc;
				if(sender instanceof BlockCommandSender) {
					loc = ((BlockCommandSender) sender).getBlock().getLocation();
				}else if(sender instanceof Player) {
					loc = ((Player)sender).getLocation();
				}else {
					sender.sendMessage("ワールド内から送信してください");
					return false;
				}
				double length = parse(args[5], 1);
				Vector vec = new Vector(
						parse(args[2], (float)loc.getX()),
						parse(args[3], (float)loc.getY()),
						parse(args[4], (float)loc.getZ()));
				loc.setDirection(player.getLocation().toVector().subtract(vec));
				player.setVelocity(loc.getDirection().multiply(-length));
				return true;
			}
		}

		return Utils.sendFailCommand(sender, Fail.LowParam, "/vec <player> x y z");
	}
	private float parse(String str, float vec) {
		return Utils.parseRaw(str, vec);
	}

}
