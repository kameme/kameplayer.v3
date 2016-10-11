package kame.kameplayer.commands;

import kame.kameplayer.baseutils.Utils;
import kame.kameplayer.baseutils.Utils.Fail;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandExplode implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {
		if(!Utils.hasPermCommand(sender, cmd.getName()))return Utils.sendFailParm(sender);
		if(args.length > 2 && args[0].equals("player"))
		{
			Player player = Bukkit.getPlayer(args[2]);
			if(player == null)return Utils.sendFailCommand(sender, Fail.NoPlayer, args[0]);
			explode(player.getEyeLocation(),(int)parse(args[1], 0f));
			return true;
		}
		if(args.length > 0 && args[0].equals("loc"))
		{
			if(args.length > 4)
			{
				if(sender instanceof Player)
				{
					Player player = (Player)sender;
					Location loc = new Location(player.getWorld(),
							parse(args[2],player.getLocation().getX()),
							parse(args[3],player.getLocation().getY()),
							parse(args[4],player.getLocation().getZ()));
					explode(loc,(int)parse(args[1], 0f));
					return true;
				}
				if(sender instanceof BlockCommandSender)
				{
					Block block = ((BlockCommandSender) sender).getBlock();
					Location loc = new Location(block.getWorld(),
							parse(args[2],block.getX()+0.5),
							parse(args[3],block.getY()+0.5),
							parse(args[4],block.getZ()+0.5));
					explode(loc,(int)parse(args[1], 0f));
					return true;
				}
				else
				{
					sender.sendMessage(ChatColor.RED + "コンソールからは送信できません");
					return false;
				}
			}
		}
		return Utils.sendFailCommand(sender, Fail.LowParam, "/explode player (amount) <player>", "/explode loc    (amount)  x y z ");
	}
	private void explode(Location loc, int i) {
		loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), i, false, false);
	}
	private float parse(String str, float def)
	{
		str = str.replace("~","");
		return Utils.parse(str, def);
	}

	private double parse(String str, double vec)
	{
		return Utils.parse(str, vec);
	}

}
