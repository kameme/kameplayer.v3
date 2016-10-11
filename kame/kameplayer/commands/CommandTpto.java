package kame.kameplayer.commands;

import kame.kameplayer.Main;
import kame.kameplayer.baseutils.Utils;
import kame.kameplayer.baseutils.Utils.Fail;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CommandTpto implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {
		//tpto <player> x y z (pitch) (yaw) (world)
		if(!Utils.hasPermCommand(sender, cmd.getName()))return Utils.sendFailParm(sender);
		if(args.length > 3) {
			Player player = Bukkit.getPlayerExact(args[0]);
			if(player == null)return Utils.sendFailCommand(sender, Fail.NoPlayer, args[0]);
			Location loc = player.getLocation();
			Double x = parse(args[1], loc.getX()), y = parse(args[2], loc.getY()), z = parse(args[3], loc.getZ());
			World world = player.getWorld();
			float pitch = loc.getPitch(), yaw = loc.getYaw();
			if(args.length > 4)yaw = parse(args[4], yaw);
			if(args.length > 5)pitch = parse(args[5], pitch);
			if(args.length > 6)world = Bukkit.getWorld(args[6]);
			if(!(world == null || x == null || y == null || z == null)) {
				loc = new Location(world, x, y, z, yaw, pitch);
				Vector vec = player.getVelocity();
				player.teleport(loc);
				player.setVelocity(vec);
				if(Main.isDebug())sender.sendMessage(player.getDisplayName() + "を" + world.getName() + ", " + x + ", " + y + ", " + z  + " へテレポートしました");
				return true;
			}
			sender.sendMessage(ChatColor.RED + "数値が不正な座標です");
			return false;
		}
		return Utils.sendFailCommand(sender, Fail.LowParam, "/tpto <player> x y z yaw pitch world");
	}
	private Double parse(String str, double loc) {
		return Utils.parse(str, loc);
	}
	private float parse(String str, float loc) {
		return Utils.parseRaw(str, loc);
	}
}
