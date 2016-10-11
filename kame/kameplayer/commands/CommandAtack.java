package kame.kameplayer.commands;

import kame.kameplayer.baseutils.Utils;
import kame.kameplayer.baseutils.Utils.Fail;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class CommandAtack implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command label, String cmd, String[] args)
	{
		//attack @r x y z damage distance type
		if(!Utils.hasPermCommand(sender, "attack"))return Utils.sendFailParm(sender);
		if(args.length > 4)
		{
			
			Player player = Bukkit.getPlayer(args[0]);
			if(player == null)return Utils.sendFailCommand(sender, Fail.NoPlayer, args[0]);
			Location loc = new Location(player.getWorld(), parse(args[1]), parse(args[2]), parse(args[3]));
			double damage = parse(args[4]);
			double distance = 10;
			EntityType type = null ;
			if(args.length > 5)distance = parse(args[5]);
			if(args.length > 6)try{type = EntityType.valueOf(args[6].toUpperCase());
			}catch(Exception e){sender.sendMessage("[kame.] そのエンティティは存在しません! " + args[6]);}
			for(Entity entity :loc.getWorld().getEntities())if(entity.getLocation().distance(loc) < distance && entity instanceof LivingEntity && (type == null || entity.getType().equals(type)))
			{
				((LivingEntity) entity).damage(damage, player);
				return true;
			}
			return false;
		}
		return Utils.sendFailCommand(sender, Fail.LowParam, "/armor <player> loc damage distance type");
	}
	private float parse(String str)
	{
		str = str.replace("~","");
		return Utils.parse(str, 0);

	}
}
